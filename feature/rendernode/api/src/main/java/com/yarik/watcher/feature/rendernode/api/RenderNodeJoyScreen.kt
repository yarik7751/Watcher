package com.yarik.watcher.feature.rendernode.api

import com.yarik.watcher.core.navigation.JoyScreen
import com.yarik.watcher.core.navigation.NoArgs

object RenderNodeJoyScreen : JoyScreen<NoArgs> {

    const val ROUTE = "rendernode"
    override val args: NoArgs
        get() = NoArgs

    override val route: String
        get() = ROUTE
}
