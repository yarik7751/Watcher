package com.yarik.watcher.feature.subcomposelayoutsandbox.impl

import androidx.compose.runtime.Composable
import com.yarik.watcher.core.navigation.JoyScreen
import com.yarik.watcher.core.navigation.ScreenContentProvider
import com.yarik.watcher.core.navigation.router.JoyRouter
import com.yarik.watcher.core.ui.ViewModelFactory
import com.yarik.watcher.feature.subcomposelayoutsandbox.api.SubcomposeLayoutSandboxJoyScreen

class SubcomposeLayoutSandboxScreenProvider : ScreenContentProvider {

    override val screen: JoyScreen<*> = SubcomposeLayoutSandboxJoyScreen

    @Composable
    override fun Content(viewModelFactory: ViewModelFactory, router: JoyRouter) {
        SubcomposeLayoutSandboxScreen(viewModelFactory = viewModelFactory, router = router)
    }
}
