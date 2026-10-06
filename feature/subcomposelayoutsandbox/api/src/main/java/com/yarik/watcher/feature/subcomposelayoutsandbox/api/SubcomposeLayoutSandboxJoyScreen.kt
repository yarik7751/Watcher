package com.yarik.watcher.feature.subcomposelayoutsandbox.api

import com.yarik.watcher.core.navigation.JoyScreen
import com.yarik.watcher.core.navigation.NoArgs

object SubcomposeLayoutSandboxJoyScreen : JoyScreen<NoArgs> {

    const val ROUTE = "subcomposelayoutsandbox"
    override val args: NoArgs
        get() = NoArgs

    override val route: String
        get() = ROUTE
}
