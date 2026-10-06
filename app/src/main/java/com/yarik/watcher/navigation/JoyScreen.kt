package com.yarik.watcher.navigation

import android.os.Parcelable
import com.github.terrakok.cicerone.Screen

interface JoyScreen<A: Parcelable> {

    val args: A

    val screen: Screen
}