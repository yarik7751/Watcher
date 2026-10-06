package com.yarik.watcher.features.subcomposelayoutsandbox

import com.yarik.watcher.features.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class SubcomposeLayoutSandboxViewModel @Inject constructor() : BaseViewModel() {

    val state = MutableStateFlow(State())

    data class State(
        val isLoading: Boolean = false,
    )
}
