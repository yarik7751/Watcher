package com.yarik.watcher.feature.masterpro.api

import com.yarik.watcher.core.navigation.JoyScreen
import com.yarik.watcher.core.navigation.NoArgs

/** Список клиентов с поиском. */
object ClientsJoyScreen : JoyScreen<NoArgs> {

    const val ROUTE = "masterpro_clients"

    override val args: NoArgs
        get() = NoArgs

    override val route: String
        get() = ROUTE
}
