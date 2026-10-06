package com.yarik.watcher.feature.start.impl

import androidx.compose.runtime.Composable
import com.yarik.watcher.core.navigation.JoyScreen
import com.yarik.watcher.core.navigation.ScreenContentProvider
import com.yarik.watcher.core.navigation.router.JoyRouter
import com.yarik.watcher.core.ui.ViewModelFactory
import com.yarik.watcher.feature.start.api.StartJoyScreen

class StartScreenProvider : ScreenContentProvider {

    override val screen: JoyScreen<*> = StartJoyScreen

    @Composable
    override fun Content(viewModelFactory: ViewModelFactory, router: JoyRouter) {
        StartScreen(viewModelFactory = viewModelFactory, router = router)
    }
}
