package com.yarik.watcher.feature.masterpro.impl.clientcard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yarik.watcher.core.database.entity.ClientEntity
import com.yarik.watcher.core.database.entity.JobWithClient
import com.yarik.watcher.feature.masterpro.impl.R
import com.yarik.watcher.feature.masterpro.impl.data.MasterProRepository
import com.yarik.watcher.feature.masterpro.impl.data.settings.MasterProSettings
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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ClientCardUiState(
    val loading: Boolean = true,
    val client: ClientEntity? = null,
    val debt: Long = 0,
    val jobs: List<JobWithClient> = emptyList(),
    /** Шаблон сообщения из настроек: %1$s — имя клиента, %2$s — имя мастера */
    val messageTemplate: String = "",
    val masterName: String = "",
)

class ClientCardViewModel @Inject constructor(
    private val repository: MasterProRepository,
    private val settings: MasterProSettings,
) : ViewModel() {

    private val clientIdFlow = MutableStateFlow<Long?>(null)

    private val _errors = MutableSharedFlow<Int>(extraBufferCapacity = 1)
    val errors: SharedFlow<Int> = _errors

    /** Сработает ровно один раз — после успешного удаления клиента */
    private val _deleted = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val deleted: SharedFlow<Unit> = _deleted

    @OptIn(ExperimentalCoroutinesApi::class)
    private val clientId: Flow<Long> = clientIdFlow.filterNotNull()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val clientFlow: Flow<ClientEntity?> = clientId.flatMapLatest(repository::observeClient)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val debtFlow: Flow<Long> = clientId.flatMapLatest(repository::observeClientDebt)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val jobsFlow: Flow<List<JobWithClient>> = clientId.flatMapLatest(repository::observeJobsByClient)

    val uiState: StateFlow<ClientCardUiState> =
        combine(
            combine(clientFlow, debtFlow, jobsFlow) { client, debt, jobs ->
                Triple(client, debt, jobs)
            },
            settings.data,
        ) { (client, debt, jobs), settingsData ->
            ClientCardUiState(
                loading = client == null,
                client = client,
                debt = debt,
                jobs = jobs,
                messageTemplate = settingsData.messageTemplate,
                masterName = settingsData.masterName,
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ClientCardUiState())

    fun init(clientId: Long) {
        if (clientIdFlow.value == null) clientIdFlow.value = clientId
    }

    fun updateClient(name: String, phone: String, address: String, notes: String) {
        val id = clientIdFlow.value ?: return
        viewModelScope.launch {
            repository.updateClient(id, name, phone, address, notes)
                .onFailure { _errors.emit(mapError(it)) }
        }
    }

    fun deleteClient() {
        val id = clientIdFlow.value ?: return
        viewModelScope.launch {
            repository.deleteClient(id)
                .onSuccess { _deleted.emit(Unit) }
                .onFailure { _errors.emit(mapError(it)) }
        }
    }

    private fun mapError(error: Throwable): Int =
        when ((error as? IllegalArgumentException)?.message) {
            "empty name" -> R.string.clients_error_empty_name
            "client has jobs" -> R.string.clientcard_error_has_jobs
            else -> R.string.clientcard_error_unknown
        }
}
