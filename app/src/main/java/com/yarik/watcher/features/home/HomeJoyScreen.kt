package com.yarik.watcher.features.home

import com.yarik.watcher.navigation.JoyScreen
import com.yarik.watcher.navigation.args.NoArgs

object HomeJoyScreen : JoyScreen<NoArgs> {
    override val args: NoArgs
        get() = NoArgs

    override val route: String
        get() = "home"
}
