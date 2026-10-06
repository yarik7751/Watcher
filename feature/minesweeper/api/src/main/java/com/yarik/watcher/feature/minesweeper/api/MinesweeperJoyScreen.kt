package com.yarik.watcher.feature.minesweeper.api

import com.yarik.watcher.core.navigation.JoyScreen
import com.yarik.watcher.core.navigation.NoArgs

object MinesweeperJoyScreen : JoyScreen<NoArgs> {

    const val ROUTE = "minesweeper"
    override val args: NoArgs
        get() = NoArgs

    override val route: String
        get() = ROUTE
}
