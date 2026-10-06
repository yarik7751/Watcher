package com.yarik.watcher.features.rendernode

import com.yarik.watcher.navigation.JoyScreen
import com.yarik.watcher.navigation.args.NoArgs

object RenderNodeJoyScreen : JoyScreen<NoArgs> {
    override val args: NoArgs
        get() = NoArgs

    override val route: String
        get() = "rendernode"
}
