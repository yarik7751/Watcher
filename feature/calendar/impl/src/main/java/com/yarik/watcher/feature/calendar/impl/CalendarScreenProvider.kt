package com.yarik.watcher.feature.calendar.impl

import androidx.compose.runtime.Composable
import com.yarik.watcher.core.navigation.JoyScreen
import com.yarik.watcher.core.navigation.ScreenContentProvider
import com.yarik.watcher.core.navigation.router.JoyRouter
import com.yarik.watcher.core.ui.ViewModelFactory
import com.yarik.watcher.feature.calendar.api.CalendarJoyScreen

class CalendarScreenProvider : ScreenContentProvider {

    override val screen: JoyScreen<*> = CalendarJoyScreen

    @Composable
    override fun Content(viewModelFactory: ViewModelFactory, router: JoyRouter) {
        CalendarScreen(router = router)
    }
}
