package com.yarik.watcher.feature.masterpro.api

import com.yarik.watcher.core.navigation.JoyScreen
import com.yarik.watcher.core.navigation.NoArgs

/**
 * Карточка клиента. Аргумент — clientId — query-параметром,
 * см. [JobCardJoyScreen] для описания схемы навигации.
 */
class ClientCardJoyScreen private constructor(val clientId: Long) : JoyScreen<NoArgs> {

    override val args: NoArgs
        get() = NoArgs

    override val route: String
        get() = "$BASE?$ARG_CLIENT_ID=$clientId"

    companion object {
        const val BASE = "masterpro_client_card"
        const val ROUTE_PATTERN = "masterpro_client_card?clientId={clientId}"
        const val ARG_CLIENT_ID = "clientId"

        fun create(clientId: Long) = ClientCardJoyScreen(clientId)
    }
}
