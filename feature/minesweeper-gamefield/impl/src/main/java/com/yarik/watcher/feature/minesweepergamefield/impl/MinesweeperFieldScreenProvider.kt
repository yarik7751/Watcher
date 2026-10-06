package com.yarik.watcher.feature.minesweepergamefield.impl

import androidx.compose.runtime.Composable
import com.yarik.watcher.core.navigation.JoyScreen
import com.yarik.watcher.core.navigation.ScreenContentProvider
import com.yarik.watcher.core.navigation.router.JoyRouter
import com.yarik.watcher.core.ui.ViewModelFactory
import com.yarik.watcher.feature.minesweepergamefield.api.MinesweeperFieldJoyScreen

class MinesweeperFieldScreenProvider : ScreenContentProvider {

    override val screen: JoyScreen<*> = MinesweeperFieldJoyScreen

    @Composable
    override fun Content(viewModelFactory: ViewModelFactory, router: JoyRouter) {
        MinesweeperFieldScreen(viewModelFactory = viewModelFactory)
    }
}
