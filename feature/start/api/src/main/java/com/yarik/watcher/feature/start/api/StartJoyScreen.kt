package com.yarik.watcher.feature.start.api

import com.yarik.watcher.core.navigation.JoyScreen
import com.yarik.watcher.core.navigation.NoArgs

object StartJoyScreen : JoyScreen<NoArgs> {

    const val ROUTE = "start"
    override val args: NoArgs
        get() = NoArgs

    override val route: String
        get() = ROUTE
}
