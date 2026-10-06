package com.yarik.watcher.feature.minesweeper.impl

import androidx.compose.runtime.Composable
import com.yarik.watcher.core.navigation.JoyScreen
import com.yarik.watcher.core.navigation.ScreenContentProvider
import com.yarik.watcher.core.navigation.router.JoyRouter
import com.yarik.watcher.core.ui.ViewModelFactory
import com.yarik.watcher.feature.minesweeper.api.MinesweeperJoyScreen

class MinesweeperScreenProvider : ScreenContentProvider {

    override val screen: JoyScreen<*> = MinesweeperJoyScreen

    @Composable
    override fun Content(viewModelFactory: ViewModelFactory, router: JoyRouter) {
        MinesweeperScreen(viewModelFactory = viewModelFactory, router = router)
    }
}
