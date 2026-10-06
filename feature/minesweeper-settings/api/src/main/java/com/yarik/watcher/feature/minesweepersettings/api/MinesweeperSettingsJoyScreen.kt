package com.yarik.watcher.feature.minesweepersettings.api

import com.yarik.watcher.core.navigation.JoyScreen
import com.yarik.watcher.core.navigation.NoArgs

object MinesweeperSettingsJoyScreen : JoyScreen<NoArgs> {

    const val ROUTE = "minesweeper/settings"
    override val args: NoArgs
        get() = NoArgs

    override val route: String
        get() = ROUTE
}
