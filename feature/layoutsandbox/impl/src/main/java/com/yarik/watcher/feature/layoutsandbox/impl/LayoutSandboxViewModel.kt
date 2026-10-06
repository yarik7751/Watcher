package com.yarik.watcher.feature.layoutsandbox.impl

import com.yarik.watcher.core.ui.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class LayoutSandboxViewModel @Inject constructor() : BaseViewModel() {

    val state = MutableStateFlow(State())

    data class State(
        val actions: List<ActionUiModel> = emptyList(),
    )

    init {
        state.update {
            it.copy(
                actions = (1..10).map { index ->
                    ActionUiModel(
                        title = "Кнопка $index",
                        isSelected = index == 1,
                    )
                },
            )
        }
    }

    fun onActionClick(clickedIndex: Int) {
        state.update { current ->
            current.copy(
                actions = current.actions.mapIndexed { index, model ->
                    model.copy(isSelected = index == clickedIndex)
                }
            )
        }
    }
}
