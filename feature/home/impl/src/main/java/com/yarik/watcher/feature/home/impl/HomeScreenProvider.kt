package com.yarik.watcher.feature.home.impl

import androidx.compose.runtime.Composable
import com.yarik.watcher.core.navigation.JoyScreen
import com.yarik.watcher.core.navigation.ScreenContentProvider
import com.yarik.watcher.core.navigation.router.JoyRouter
import com.yarik.watcher.core.ui.ViewModelFactory
import com.yarik.watcher.feature.home.api.HomeJoyScreen

class HomeScreenProvider : ScreenContentProvider {

    override val screen: JoyScreen<*> = HomeJoyScreen

    @Composable
    override fun Content(viewModelFactory: ViewModelFactory, router: JoyRouter) {
        HomeScreen(viewModelFactory = viewModelFactory, router = router)
    }
}
