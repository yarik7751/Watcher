package com.yarik.watcher.core.navigation.router

import com.yarik.watcher.core.navigation.JoyScreen
import kotlinx.coroutines.flow.Flow

interface ScreensFlow {

    val screensFlow: Flow<List<JoyScreen<*>>>
}