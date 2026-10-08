package com.yarik.watcher.feature.masterpro.api

import com.yarik.watcher.core.navigation.JoyScreen
import com.yarik.watcher.core.navigation.NoArgs

/** Статистика: выручка, заявки, средний чек, должники. */
object StatsJoyScreen : JoyScreen<NoArgs> {

    const val ROUTE = "masterpro_stats"

    override val args: NoArgs
        get() = NoArgs

    override val route: String
        get() = ROUTE
}
