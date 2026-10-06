package com.yarik.watcher.feature.minesweepersettings.impl

import androidx.lifecycle.viewModelScope
import com.yarik.watcher.core.ui.BaseViewModel
import com.yarik.watcher.core.ui.CommandFlow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class MinesweeperSettingsViewModel @Inject constructor() : BaseViewModel() {

    val state = MutableStateFlow<State>(State())

    val commandsFlow = CommandFlow<Commands>(viewModelScope)

    data class State(
        val isLoading: Boolean = false,
    )

    sealed interface Commands
}
