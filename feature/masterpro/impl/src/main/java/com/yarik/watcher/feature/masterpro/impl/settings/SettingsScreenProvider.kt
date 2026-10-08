package com.yarik.watcher.feature.masterpro.impl.settings

import androidx.compose.runtime.Composable
import com.yarik.watcher.core.navigation.JoyScreen
import com.yarik.watcher.core.navigation.ScreenContentProvider
import com.yarik.watcher.core.navigation.router.JoyRouter
import com.yarik.watcher.core.ui.ViewModelFactory
import com.yarik.watcher.feature.masterpro.api.SettingsJoyScreen

class SettingsScreenProvider : ScreenContentProvider {

    override val screen: JoyScreen<*> = SettingsJoyScreen

    @Composable
    override fun Content(viewModelFactory: ViewModelFactory, router: JoyRouter) {
        SettingsScreen(viewModelFactory = viewModelFactory, router = router)
    }
}
