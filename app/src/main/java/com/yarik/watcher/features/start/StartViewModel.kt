package com.yarik.watcher.features.start

import androidx.lifecycle.viewModelScope
import com.yarik.watcher.features.base.BaseViewModel
import com.yarik.watcher.flow.CommandFlow
import com.yarik.watcher.flow.emit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class StartViewModel @Inject constructor() : BaseViewModel() {

    val stateFlow = MutableStateFlow(State())
    val commandsFlow = CommandFlow<Commands>(viewModelScope)
    private val isPermissionsRequestedFlow = MutableStateFlow(false)

    init {
        isPermissionsRequestedFlow.onEach { isRequested ->
            if (isRequested) {
                openApp()
            }
        }.launchIn(viewModelScope)
    }

    private fun openApp() {
        commandsFlow emit Commands.OpenWidgets
    }

    fun onPermissionsRequested() {
        isPermissionsRequestedFlow.value = true
    }

    data class State(
        val isLoading: Boolean = false,
    )

    sealed interface Commands {
        data object OpenWidgets : Commands
    }
}