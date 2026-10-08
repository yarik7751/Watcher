package com.yarik.watcher.feature.masterpro.impl.jobs

import androidx.compose.runtime.Composable
import com.yarik.watcher.core.navigation.JoyScreen
import com.yarik.watcher.core.navigation.ScreenContentProvider
import com.yarik.watcher.core.navigation.router.JoyRouter
import com.yarik.watcher.core.ui.ViewModelFactory
import com.yarik.watcher.feature.masterpro.api.JobsJoyScreen

class JobsScreenProvider : ScreenContentProvider {

    override val screen: JoyScreen<*> = JobsJoyScreen

    @Composable
    override fun Content(viewModelFactory: ViewModelFactory, router: JoyRouter) {
        JobsScreen(viewModelFactory = viewModelFactory, router = router)
    }
}
