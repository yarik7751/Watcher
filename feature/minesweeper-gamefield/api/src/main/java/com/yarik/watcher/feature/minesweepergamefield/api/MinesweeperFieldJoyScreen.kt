package com.yarik.watcher.feature.minesweepergamefield.api

import com.yarik.watcher.core.navigation.JoyScreen
import com.yarik.watcher.core.navigation.NoArgs

object MinesweeperFieldJoyScreen : JoyScreen<NoArgs> {

    const val ROUTE = "minesweeper/game"
    override val args: NoArgs
        get() = NoArgs

    override val route: String
        get() = ROUTE
}
