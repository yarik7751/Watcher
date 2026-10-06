package com.yarik.watcher.feature.calendar.api

import com.yarik.watcher.core.navigation.JoyScreen
import com.yarik.watcher.core.navigation.NoArgs

object CalendarJoyScreen : JoyScreen<NoArgs> {

    const val ROUTE = "calendar"
    override val args: NoArgs
        get() = NoArgs

    override val route: String
        get() = ROUTE
}
