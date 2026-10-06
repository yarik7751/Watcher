package com.yarik.watcher.feature.minesweepersettings.impl

import androidx.compose.runtime.Composable
import com.yarik.watcher.core.navigation.JoyScreen
import com.yarik.watcher.core.navigation.ScreenContentProvider
import com.yarik.watcher.core.navigation.router.JoyRouter
import com.yarik.watcher.core.ui.ViewModelFactory
import com.yarik.watcher.feature.minesweepersettings.api.MinesweeperSettingsJoyScreen

class MinesweeperSettingsScreenProvider : ScreenContentProvider {

    override val screen: JoyScreen<*> = MinesweeperSettingsJoyScreen

    @Composable
    override fun Content(viewModelFactory: ViewModelFactory, router: JoyRouter) {
        MinesweeperSettingsScreen(viewModelFactory = viewModelFactory)
    }
}
