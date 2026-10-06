package com.yarik.watcher.core.navigation.router

import android.os.Parcelable

interface CurrentArgs {

    fun <A: Parcelable> get(): A
}