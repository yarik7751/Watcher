package com.yarik.watcher.features.minesweeper.gamefield

import com.yarik.watcher.navigation.JoyScreen
import com.yarik.watcher.navigation.args.NoArgs

object MinesweeperFieldJoyScreen : JoyScreen<NoArgs> {
    override val args: NoArgs
        get() = NoArgs

    override val route: String
        get() = "minesweeper/game"
}
