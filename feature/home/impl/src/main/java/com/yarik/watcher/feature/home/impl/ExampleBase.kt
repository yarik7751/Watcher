package com.yarik.watcher.feature.home.impl.base

import android.util.Log

abstract class ExampleBase {

    open val logTag: String = "TestLogTag"

    fun log(msg: String) {
        Log.d(logTag, msg)
    }

    abstract operator fun invoke()

    protected fun withTime(action: () -> Unit) {
        val initTime = System.nanoTime()
        action()
        val lastTime = System.nanoTime() - initTime
        log("EXECUTING TIME --- $lastTime")
    }
}