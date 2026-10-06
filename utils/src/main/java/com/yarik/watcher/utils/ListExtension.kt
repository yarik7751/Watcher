package com.yarik.watcher.utils

fun <T> List<T>.minusOneElement(): List<T> {
    if (this.isEmpty()) return this

    val last = this.last()
    return this.minus(last)
}