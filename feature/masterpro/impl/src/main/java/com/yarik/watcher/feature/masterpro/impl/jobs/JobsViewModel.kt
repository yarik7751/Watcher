package com.yarik.watcher.feature.masterpro.impl.jobs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yarik.watcher.core.database.entity.ClientEntity
import com.yarik.watcher.core.database.entity.JobStatus
import com.yarik.watcher.core.database.entity.JobWithClient
import com.yarik.watcher.feature.masterpro.impl.R
import com.yarik.watcher.feature.masterpro.impl.data.MasterProRepository
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class JobsUiState(
    val status: JobStatus = JobStatus.NEW,
    val jobs: List<JobWithClient> = emptyList(),
    val clients: List<ClientEntity> = emptyList(),
    val createDialogOpen: Boolean = false,
    val creating: Boolean = false,
)

class JobsViewModel @Inject constructor(
    private val repository: MasterProRepository,
) : ViewModel() {

    private data class DialogState(val open: Boolean = false, val creating: Boolean = false)

    private val statusFlow = MutableStateFlow(JobStatus.NEW)
    private val dialogFlow = MutableStateFlow(DialogState())

    /** Одноразовые ошибки: значения — stringRes из strings.xml модуля */
    private val _errors = MutableSharedFlow<Int>(extraBufferCapacity = 1)
    val errors: SharedFlow<Int> = _errors

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<JobsUiState> = combine(
        statusFlow,
        dialogFlow,
        statusFlow.flatMapLatest { repository.observeJobsByStatus(it) },
        repository.observeClients(),
    ) { status, dialog, jobs, clients ->
        JobsUiState(
            status = status,
            jobs = jobs,
            clients = clients,
            createDialogOpen = dialog.open,
            creating = dialog.creating,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), JobsUiState())

    fun selectStatus(status: JobStatus) {
        statusFlow.value = status
    }

    fun openCreateDialog() {
        dialogFlow.value = dialogFlow.value.copy(open = true)
    }

    fun closeCreateDialog() {
        dialogFlow.value = DialogState()
    }

    /**
     * Создание заявки из диалога. Если [clientId] == null — создаём клиента из [newClientName].
     */
    fun createJob(clientId: Long?, newClientName: String?, title: String, address: String) {
        viewModelScope.launch {
            dialogFlow.value = dialogFlow.value.copy(creating = true)
            val targetClientId = when {
                clientId != null -> clientId
                !newClientName.isNullOrBlank() ->
                    repository.createClient(newClientName, "", "", "")
                        .getOrElse {
                            dialogFlow.value = dialogFlow.value.copy(creating = false)
                            _errors.emit(mapError(it))
                            return@launch
                        }
                else -> {
                    dialogFlow.value = dialogFlow.value.copy(creating = false)
                    _errors.emit(R.string.jobs_error_no_client)
                    return@launch
                }
            }
            repository.createJob(targetClientId, title, address, scheduledAt = null, notes = "")
                .onSuccess { dialogFlow.value = DialogState() }
                .onFailure {
                    dialogFlow.value = dialogFlow.value.copy(creating = false)
                    _errors.emit(mapError(it))
                }
        }
    }

    private fun mapError(error: Throwable): Int =
        when ((error as? IllegalArgumentException)?.message) {
            "empty title" -> R.string.jobs_error_empty_title
            "empty name" -> R.string.jobs_error_empty_name
            else -> R.string.jobs_error_unknown
        }
}
