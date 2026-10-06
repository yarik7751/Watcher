package com.yarik.watcher.feature.home.api

import com.yarik.watcher.core.navigation.JoyScreen
import com.yarik.watcher.core.navigation.NoArgs

object HomeJoyScreen : JoyScreen<NoArgs> {

    const val ROUTE = "home"
    override val args: NoArgs
        get() = NoArgs

    override val route: String
        get() = ROUTE
}
