package com.yarik.watcher.navigation.router

import android.os.Parcelable

interface CurrentArgs {

    fun <A: Parcelable> get(): A
}