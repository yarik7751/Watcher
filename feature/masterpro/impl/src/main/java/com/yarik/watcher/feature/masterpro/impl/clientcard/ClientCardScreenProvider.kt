package com.yarik.watcher.feature.masterpro.impl.clientcard

import androidx.compose.runtime.Composable
import com.yarik.watcher.core.navigation.JoyScreen
import com.yarik.watcher.core.navigation.ScreenContentProvider
import com.yarik.watcher.core.navigation.router.JoyRouter
import com.yarik.watcher.core.ui.ViewModelFactory
import com.yarik.watcher.feature.masterpro.api.ClientCardJoyScreen

class ClientCardScreenProvider : ScreenContentProvider {

    override val screen: JoyScreen<*> = ClientCardJoyScreen.create(0)

    @Composable
    override fun Content(viewModelFactory: ViewModelFactory, router: JoyRouter) {
        ClientCardScreen(viewModelFactory = viewModelFactory, router = router)
    }
}
