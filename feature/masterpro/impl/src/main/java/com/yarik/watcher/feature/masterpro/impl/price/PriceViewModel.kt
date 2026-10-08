package com.yarik.watcher.feature.masterpro.impl.price

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yarik.watcher.core.database.entity.PriceItemEntity
import com.yarik.watcher.feature.masterpro.impl.R
import com.yarik.watcher.feature.masterpro.impl.data.MasterProRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PriceUiState(
    val items: List<PriceItemEntity> = emptyList(),
    val dialogOpen: Boolean = false,
    val dialogSaving: Boolean = false,
    val editItem: PriceItemEntity? = null,
)

class PriceViewModel @Inject constructor(
    private val repository: MasterProRepository,
) : ViewModel() {

    private data class DialogState(
        val open: Boolean = false,
        val saving: Boolean = false,
        val editItem: PriceItemEntity? = null,
    )

    private val dialogFlow = MutableStateFlow(DialogState())

    private val _errors = MutableSharedFlow<Int>(extraBufferCapacity = 1)
    val errors: SharedFlow<Int> = _errors

    val uiState: StateFlow<PriceUiState> = combine(
        repository.observePriceItems(),
        dialogFlow,
    ) { items, dialog ->
        PriceUiState(
            items = items,
            dialogOpen = dialog.open,
            dialogSaving = dialog.saving,
            editItem = dialog.editItem,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PriceUiState())

    fun openCreateDialog() {
        dialogFlow.value = DialogState(open = true)
    }

    fun openEditDialog(item: PriceItemEntity) {
        dialogFlow.value = DialogState(open = true, editItem = item)
    }

    fun closeDialog() {
        dialogFlow.value = DialogState()
    }

    fun saveItem(itemId: Long?, title: String, priceKopecks: Long) {
        viewModelScope.launch {
            dialogFlow.value = dialogFlow.value.copy(saving = true)
            repository.savePriceItem(
                PriceItemEntity(
                    id = itemId ?: 0,
                    title = title,
                    price = priceKopecks,
                ),
            )
                .onSuccess { dialogFlow.value = DialogState() }
                .onFailure {
                    dialogFlow.value = dialogFlow.value.copy(saving = false)
                    _errors.emit(mapError(it))
                }
        }
    }

    fun deleteItem(item: PriceItemEntity) {
        viewModelScope.launch {
            repository.deletePriceItem(item).onFailure { _errors.emit(mapError(it)) }
        }
    }

    private fun mapError(error: Throwable): Int =
        when ((error as? IllegalArgumentException)?.message) {
            "empty item title" -> R.string.price_error_empty_title
            "negative price" -> R.string.price_error_price
            else -> R.string.price_error_unknown
        }
}
