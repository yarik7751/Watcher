package com.yarik.watcher.feature.masterpro.api

import com.yarik.watcher.core.navigation.JoyScreen
import com.yarik.watcher.core.navigation.NoArgs

/** Список заявок — точка входа в «МастерPRO». */
object JobsJoyScreen : JoyScreen<NoArgs> {

    const val ROUTE = "masterpro_jobs"

    override val args: NoArgs
        get() = NoArgs

    override val route: String
        get() = ROUTE
}
