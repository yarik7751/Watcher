package com.yarik.watcher.feature.masterpro.impl.stats

import androidx.compose.runtime.Composable
import com.yarik.watcher.core.navigation.JoyScreen
import com.yarik.watcher.core.navigation.ScreenContentProvider
import com.yarik.watcher.core.navigation.router.JoyRouter
import com.yarik.watcher.core.ui.ViewModelFactory
import com.yarik.watcher.feature.masterpro.api.StatsJoyScreen

class StatsScreenProvider : ScreenContentProvider {

    override val screen: JoyScreen<*> = StatsJoyScreen

    @Composable
    override fun Content(viewModelFactory: ViewModelFactory, router: JoyRouter) {
        StatsScreen(viewModelFactory = viewModelFactory, router = router)
    }
}
