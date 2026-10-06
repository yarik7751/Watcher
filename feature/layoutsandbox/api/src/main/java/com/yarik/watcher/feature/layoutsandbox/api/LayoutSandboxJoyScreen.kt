package com.yarik.watcher.feature.layoutsandbox.api

import com.yarik.watcher.core.navigation.JoyScreen
import com.yarik.watcher.core.navigation.NoArgs

object LayoutSandboxJoyScreen : JoyScreen<NoArgs> {

    const val ROUTE = "layoutsandbox"
    override val args: NoArgs
        get() = NoArgs

    override val route: String
        get() = ROUTE
}
