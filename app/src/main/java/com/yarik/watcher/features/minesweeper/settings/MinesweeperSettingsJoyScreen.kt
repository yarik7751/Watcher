package com.yarik.watcher.features.minesweeper.settings

import com.yarik.watcher.navigation.JoyScreen
import com.yarik.watcher.navigation.args.NoArgs

object MinesweeperSettingsJoyScreen : JoyScreen<NoArgs> {
    override val args: NoArgs
        get() = NoArgs

    override val route: String
        get() = "minesweeper/settings"
}
