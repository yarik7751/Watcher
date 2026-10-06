package com.yarik.watcher.utils

import kotlinx.coroutines.CancellationException
import java.lang.Exception

fun Exception.rethrowIfCancellation() {
    if (this is CancellationException) throw this
}