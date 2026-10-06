package com.yarik.watcher.features.subcomposelayoutsandbox

import com.yarik.watcher.navigation.JoyScreen
import com.yarik.watcher.navigation.args.NoArgs

object SubcomposeLayoutSandboxJoyScreen : JoyScreen<NoArgs> {
    override val args: NoArgs
        get() = NoArgs

    override val route: String
        get() = "subcomposelayoutsandbox"
}
