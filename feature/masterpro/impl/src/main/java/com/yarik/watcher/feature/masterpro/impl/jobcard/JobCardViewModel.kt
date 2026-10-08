package com.yarik.watcher.feature.masterpro.impl.jobcard

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yarik.watcher.core.database.entity.ClientEntity
import com.yarik.watcher.core.database.entity.JobEntity
import com.yarik.watcher.core.database.entity.JobItemEntity
import com.yarik.watcher.core.database.entity.JobStatus
import com.yarik.watcher.core.database.entity.PaymentEntity
import com.yarik.watcher.core.database.entity.PaymentMethod
import com.yarik.watcher.core.database.entity.PhotoKind
import com.yarik.watcher.core.database.entity.PriceItemEntity
import com.yarik.watcher.feature.masterpro.impl.R
import com.yarik.watcher.feature.masterpro.impl.data.MasterProRepository
import com.yarik.watcher.feature.masterpro.impl.data.model.JobDetails
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class JobCardUiState(
    val loading: Boolean = true,
    val details: JobDetails? = null,
    val client: ClientEntity? = null,
)

class JobCardViewModel @Inject constructor(
    private val repository: MasterProRepository,
    private val photoStorage: PhotoStorage,
) : ViewModel() {

    private val jobIdFlow = MutableStateFlow<Long?>(null)

    /** Одноразовые ошибки: значения — stringRes из strings.xml модуля */
    private val _errors = MutableSharedFlow<Int>(extraBufferCapacity = 1)
    val errors: SharedFlow<Int> = _errors

    @OptIn(ExperimentalCoroutinesApi::class)
    private val detailsFlow: Flow<JobDetails?> =
        jobIdFlow.filterNotNull().flatMapLatest(repository::observeJobDetails)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val clientFlow: Flow<ClientEntity?> = detailsFlow.flatMapLatest { details ->
        details?.let { repository.observeClient(it.job.clientId) } ?: flowOf(null)
    }

    val uiState: StateFlow<JobCardUiState> =
        combine(detailsFlow, clientFlow) { details, client ->
            JobCardUiState(loading = details == null, details = details, client = client)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), JobCardUiState())

    /** Прайс для диалога добавления позиции */
    val priceItems: StateFlow<List<PriceItemEntity>> = repository.observePriceItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun init(jobId: Long) {
        if (jobIdFlow.value == null) jobIdFlow.value = jobId
    }

    /** URI временного файла для контракта TakePicture (камера) */
    fun createCaptureUri(kind: PhotoKind): android.net.Uri? =
        jobIdFlow.value?.let { photoStorage.createCaptureUri(it, kind) }

    private fun currentJob(): JobEntity? = uiState.value.details?.job

    fun updateJob(
        title: String,
        address: String,
        scheduledAt: Long?,
        notes: String,
        status: JobStatus,
    ) {
        val job = currentJob() ?: return
        viewModelScope.launch {
            repository.updateJob(
                job.copy(
                    title = title,
                    address = address,
                    scheduledAt = scheduledAt,
                    notes = notes,
                    status = status,
                ),
            ).onFailure { _errors.emit(mapError(it)) }
        }
    }

    // region Смета

    fun addItemFromPrice(priceItem: PriceItemEntity, qty: Int) {
        val jobId = jobIdFlow.value ?: return
        addItem(
            JobItemEntity(
                jobId = jobId,
                priceItemId = priceItem.id,
                title = priceItem.title,
                qty = qty,
                price = priceItem.price,
            ),
        )
    }

    fun addItemManual(title: String, qty: Int, priceKopecks: Long) {
        val jobId = jobIdFlow.value ?: return
        addItem(JobItemEntity(jobId = jobId, title = title, qty = qty, price = priceKopecks))
    }

    private fun addItem(item: JobItemEntity) {
        viewModelScope.launch {
            repository.addJobItem(item).onFailure { _errors.emit(mapError(it)) }
        }
    }

    fun deleteItem(item: JobItemEntity) {
        viewModelScope.launch {
            repository.deleteJobItem(item).onFailure { _errors.emit(mapError(it)) }
        }
    }

    // endregion

    // region Оплаты

    fun addPayment(amountKopecks: Long, method: PaymentMethod) {
        val jobId = jobIdFlow.value ?: return
        viewModelScope.launch {
            repository.addPayment(jobId, amountKopecks, method).onFailure { _errors.emit(mapError(it)) }
        }
    }

    fun deletePayment(payment: PaymentEntity) {
        viewModelScope.launch {
            repository.deletePayment(payment).onFailure { _errors.emit(mapError(it)) }
        }
    }

    // endregion

    // region Фото

    /** [source] — либо content:// временного capture-файла, либо uri из галереи */
    fun addPhoto(source: Uri, kind: PhotoKind) {
        val jobId = jobIdFlow.value ?: return
        viewModelScope.launch {
            runCatching { photoStorage.savePhoto(source, jobId, kind) }
                .onSuccess { path ->
                    repository.addJobPhoto(jobId, path, kind).onFailure { _errors.emit(mapError(it)) }
                }
                .onFailure { _errors.emit(R.string.jobcard_error_photo) }
        }
    }

    fun deletePhoto(photo: JobPhotoEntityUi) {
        viewModelScope.launch {
            repository.deleteJobPhoto(photo.entity).onFailure { _errors.emit(mapError(it)) }
            photoStorage.deletePhoto(photo.entity.filePath)
        }
    }

    // endregion

    /** «Завершить»: DONE + finishedAt + lastVisitAt клиента — транзакция в репозитории */
    fun finishJob() {
        val jobId = jobIdFlow.value ?: return
        viewModelScope.launch {
            repository.finishJob(jobId).onFailure { _errors.emit(mapError(it)) }
        }
    }

    private fun mapError(error: Throwable): Int =
        when ((error as? IllegalArgumentException)?.message) {
            "empty title" -> R.string.jobcard_error_empty_title
            "empty item title" -> R.string.jobcard_error_empty_item_title
            "non-positive qty" -> R.string.jobcard_error_qty
            "negative price" -> R.string.jobcard_error_price
            "non-positive amount" -> R.string.jobcard_error_amount
            "amount exceeds debt" -> R.string.jobcard_error_amount_exceeds
            else -> R.string.jobcard_error_unknown
        }
}

/** Обёртка для UI: фото + сгенерированный file-ключ для Coil */
data class JobPhotoEntityUi(val entity: com.yarik.watcher.core.database.entity.JobPhotoEntity) {
    val file: java.io.File
        get() = java.io.File(entity.filePath)
}
