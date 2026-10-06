package com.yarik.watcher.snackbar.flow

import com.yarik.watcher.snackbar.model.SnackBarData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

interface SnackBarManager {
    fun showMessage(data: SnackBarData)
}

interface SnackBarManagerFlow {

    fun observe(): Flow<SnackBarData>
}

class SnackBarManagerImpl @Inject constructor() : SnackBarManager, SnackBarManagerFlow {

    private val snackBarFlow = MutableSharedFlow<SnackBarData>(
        replay = 1,
        extraBufferCapacity = 2,
    )

    override fun showMessage(data: SnackBarData) {
        snackBarFlow.tryEmit(data)
    }

    override fun observe(): Flow<SnackBarData> {
        return snackBarFlow
    }
}