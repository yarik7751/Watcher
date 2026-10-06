package com.yarik.watcher.feature.home.impl

import androidx.lifecycle.viewModelScope
import com.yarik.watcher.core.ui.BaseViewModel

import com.yarik.watcher.feature.home.impl.buffer.BufferExample
import com.yarik.watcher.feature.home.impl.simplecounter.SimpleAtomicCounter
import com.yarik.watcher.feature.home.impl.simplecounter.SimpleCounter
import com.yarik.watcher.feature.home.impl.reordering.Reordering
import com.yarik.watcher.feature.home.impl.semaphore.SemaphoreExample
import com.yarik.watcher.core.ui.CommandFlow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

class HomeViewModel @Inject constructor() : BaseViewModel() {

    val state = MutableStateFlow<State>(State())

    val commandsFlow = CommandFlow<Commands>(viewModelScope)

    data class State(
        val isLoading: Boolean = false,
    )

    sealed interface Commands

    fun onSimpleCounterClick() {
        SimpleCounter()
    }

    fun onSimpleAtomicCounterClick() {
        SimpleAtomicCounter()
    }

    fun onReorderingClick() {
        Reordering()
    }

    fun onBufferExampleClick() {
        BufferExample()
    }

    fun onSemaphoreClick() {
        SemaphoreExample()
    }


}
