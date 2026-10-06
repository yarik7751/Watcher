package com.yarik.watcher.feature.subcomposelayoutsandbox.impl

import com.yarik.watcher.core.ui.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class SubcomposeLayoutSandboxViewModel @Inject constructor() : BaseViewModel() {

    val state = MutableStateFlow(State())

    data class State(
        val isLoading: Boolean = false,
    )
}
