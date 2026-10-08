package com.yarik.watcher.feature.masterpro.impl.price

import androidx.compose.runtime.Composable
import com.yarik.watcher.core.navigation.JoyScreen
import com.yarik.watcher.core.navigation.ScreenContentProvider
import com.yarik.watcher.core.navigation.router.JoyRouter
import com.yarik.watcher.core.ui.ViewModelFactory
import com.yarik.watcher.feature.masterpro.api.PriceJoyScreen

class PriceScreenProvider : ScreenContentProvider {

    override val screen: JoyScreen<*> = PriceJoyScreen

    @Composable
    override fun Content(viewModelFactory: ViewModelFactory, router: JoyRouter) {
        PriceScreen(viewModelFactory = viewModelFactory, router = router)
    }
}
