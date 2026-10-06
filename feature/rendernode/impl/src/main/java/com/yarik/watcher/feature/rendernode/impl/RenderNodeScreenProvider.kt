package com.yarik.watcher.feature.rendernode.impl

import androidx.compose.runtime.Composable
import com.yarik.watcher.core.navigation.JoyScreen
import com.yarik.watcher.core.navigation.ScreenContentProvider
import com.yarik.watcher.core.navigation.router.JoyRouter
import com.yarik.watcher.core.ui.ViewModelFactory
import com.yarik.watcher.feature.rendernode.api.RenderNodeJoyScreen

class RenderNodeScreenProvider : ScreenContentProvider {

    override val screen: JoyScreen<*> = RenderNodeJoyScreen

    @Composable
    override fun Content(viewModelFactory: ViewModelFactory, router: JoyRouter) {
        RenderNodeScreen()
    }
}
