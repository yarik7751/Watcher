package com.yarik.watcher.feature.masterpro.api

import com.yarik.watcher.core.navigation.JoyScreen
import com.yarik.watcher.core.navigation.NoArgs

/** Прайс-лист типовых работ. */
object PriceJoyScreen : JoyScreen<NoArgs> {

    const val ROUTE = "masterpro_price"

    override val args: NoArgs
        get() = NoArgs

    override val route: String
        get() = ROUTE
}
