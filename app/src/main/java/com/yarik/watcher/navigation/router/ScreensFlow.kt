package com.yarik.watcher.navigation.router

import com.yarik.watcher.navigation.JoyScreen
import kotlinx.coroutines.flow.Flow

interface ScreensFlow {

    val screensFlow: Flow<List<JoyScreen<*>>>
}