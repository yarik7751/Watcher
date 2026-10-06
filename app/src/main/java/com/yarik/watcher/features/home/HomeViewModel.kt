package com.yarik.watcher.features.home

import androidx.lifecycle.viewModelScope
import com.yarik.watcher.features.base.BaseViewModel

import com.yarik.watcher.features.home.buffer.BufferExample
import com.yarik.watcher.features.home.simplecounter.SimpleAtomicCounter
import com.yarik.watcher.features.home.simplecounter.SimpleCounter
import com.yarik.watcher.features.home.reordering.Reordering
import com.yarik.watcher.features.home.semaphore.SemaphoreExample
import com.yarik.watcher.flow.CommandFlow
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
