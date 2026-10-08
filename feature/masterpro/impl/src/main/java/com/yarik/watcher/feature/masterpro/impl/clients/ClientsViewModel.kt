package com.yarik.watcher.feature.masterpro.impl.clients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yarik.watcher.core.database.entity.ClientEntity
import com.yarik.watcher.core.database.entity.ClientWithDebt
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
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ClientsUiState(
    val query: String = "",
    val clients: List<ClientWithDebt> = emptyList(),
    val dialogOpen: Boolean = false,
    val dialogSaving: Boolean = false,
    val editClient: ClientEntity? = null,
)

class ClientsViewModel @Inject constructor(
    private val repository: MasterProRepository,
) : ViewModel() {

    private data class DialogState(
        val open: Boolean = false,
        val saving: Boolean = false,
        val editClient: ClientEntity? = null,
    )

    private val queryFlow = MutableStateFlow("")
    private val dialogFlow = MutableStateFlow(DialogState())

    private val _errors = MutableSharedFlow<Int>(extraBufferCapacity = 1)
    val errors: SharedFlow<Int> = _errors

    /** Пустой запрос → полный список; непустой — поиск с лёгким debounce */
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<ClientsUiState> = combine(
        queryFlow.debounce(SEARCH_DEBOUNCE_MS).distinctUntilChanged().flatMapLatest { query ->
            if (query.isBlank()) repository.observeClientsWithDebt()
            else repository.searchClientsWithDebt(query)
        },
        queryFlow,
        dialogFlow,
    ) { clients, query, dialog ->
        ClientsUiState(
            query = query,
            clients = clients,
            dialogOpen = dialog.open,
            dialogSaving = dialog.saving,
            editClient = dialog.editClient,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ClientsUiState())

    fun onQueryChange(query: String) {
        queryFlow.value = query
    }

    fun openCreateDialog() {
        dialogFlow.value = DialogState(open = true)
    }

    fun openEditDialog(client: ClientEntity) {
        dialogFlow.value = DialogState(open = true, editClient = client)
    }

    fun closeDialog() {
        dialogFlow.value = DialogState()
    }

    fun saveClient(clientId: Long?, name: String, phone: String, address: String, notes: String) {
        viewModelScope.launch {
            dialogFlow.value = dialogFlow.value.copy(saving = true)
            val result = if (clientId == null) {
                repository.createClient(name, phone, address, notes)
            } else {
                repository.updateClient(clientId, name, phone, address, notes)
            }
            result
                .onSuccess { dialogFlow.value = DialogState() }
                .onFailure {
                    dialogFlow.value = dialogFlow.value.copy(saving = false)
                    _errors.emit(mapError(it))
                }
        }
    }

    private fun mapError(error: Throwable): Int =
        when ((error as? IllegalArgumentException)?.message) {
            "empty name" -> R.string.clients_error_empty_name
            else -> R.string.clients_error_unknown
        }

    private companion object {
        const val SEARCH_DEBOUNCE_MS = 250L
    }
}
