package com.yarik.watcher.feature.masterpro.impl.clients

import androidx.compose.runtime.Composable
import com.yarik.watcher.core.navigation.JoyScreen
import com.yarik.watcher.core.navigation.ScreenContentProvider
import com.yarik.watcher.core.navigation.router.JoyRouter
import com.yarik.watcher.core.ui.ViewModelFactory
import com.yarik.watcher.feature.masterpro.api.ClientsJoyScreen

class ClientsScreenProvider : ScreenContentProvider {

    override val screen: JoyScreen<*> = ClientsJoyScreen

    @Composable
    override fun Content(viewModelFactory: ViewModelFactory, router: JoyRouter) {
        ClientsScreen(viewModelFactory = viewModelFactory, router = router)
    }
}
