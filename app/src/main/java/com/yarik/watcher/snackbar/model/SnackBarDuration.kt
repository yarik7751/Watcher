package com.yarik.watcher.snackbar.model

sealed class SnackBarDuration(val durationMs: kotlin.Long) {

    data object Short : SnackBarDuration(durationMs = 4000)
    data object Long : SnackBarDuration(durationMs = 8000)
}