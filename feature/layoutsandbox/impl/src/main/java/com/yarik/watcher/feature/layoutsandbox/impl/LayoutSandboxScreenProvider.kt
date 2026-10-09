package com.yarik.watcher.feature.layoutsandbox.impl

import androidx.compose.runtime.Composable
import com.yarik.watcher.core.navigation.JoyScreen
import com.yarik.watcher.core.navigation.ScreenContentProvider
import com.yarik.watcher.core.navigation.router.JoyRouter
import com.yarik.watcher.core.ui.ViewModelFactory
import com.yarik.watcher.feature.layoutsandbox.api.LayoutSandboxJoyScreen

class LayoutSandboxScreenProvider : ScreenContentProvider {

    override val screen: JoyScreen<*> = LayoutSandboxJoyScreen

    @Composable
    override fun Content(viewModelFactory: ViewModelFactory, router: JoyRouter) {
        LayoutSandboxScreen(viewModelFactory = viewModelFactory, router = router)
    }
}
