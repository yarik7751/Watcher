package com.yarik.watcher.feature.masterpro.api

import com.yarik.watcher.core.navigation.JoyScreen
import com.yarik.watcher.core.navigation.NoArgs

/** Настройки «МастерPRO»: имя мастера, шаблон сообщения, экспорт CSV. */
object SettingsJoyScreen : JoyScreen<NoArgs> {

    const val ROUTE = "masterpro_settings"

    override val args: NoArgs
        get() = NoArgs

    override val route: String
        get() = ROUTE
}
