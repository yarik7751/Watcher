package com.yarik.watcher.snackbar.model

import com.yarik.watcher.utils.textorresource.TextOrResource

data class SnackBarData(
    val type: SnackBarType,
    val duration: SnackBarDuration = SnackBarDuration.Short,
    val message: TextOrResource,
)