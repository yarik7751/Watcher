package com.yarik.watcher.feature.masterpro.impl.jobcard

import androidx.compose.runtime.Composable
import com.yarik.watcher.core.navigation.JoyScreen
import com.yarik.watcher.core.navigation.ScreenContentProvider
import com.yarik.watcher.core.navigation.router.JoyRouter
import com.yarik.watcher.core.ui.ViewModelFactory
import com.yarik.watcher.feature.masterpro.api.JobCardJoyScreen

class JobCardScreenProvider : ScreenContentProvider {

    // Репрезентативный экземпляр для мапы route → screen; сам destination
    // регистрируется по ROUTE_PATTERN (ключ Dagger-мапы в MasterProModule)
    override val screen: JoyScreen<*> = JobCardJoyScreen.create(0)

    @Composable
    override fun Content(viewModelFactory: ViewModelFactory, router: JoyRouter) {
        JobCardScreen(viewModelFactory = viewModelFactory, router = router)
    }
}
