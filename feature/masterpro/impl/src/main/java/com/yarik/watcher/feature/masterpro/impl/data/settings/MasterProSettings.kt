package com.yarik.watcher.feature.masterpro.impl.data.settings

import android.content.Context
import com.yarik.watcher.feature.masterpro.impl.R
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class MasterProSettingsData(
    val masterName: String,
    /** Шаблон WhatsApp-сообщения; %1$s — имя клиента, %2$s — имя мастера */
    val messageTemplate: String,
)

/**
 * Настройки «МастерPRO» в SharedPreferences.
 * Офлайн-приложение на одном устройстве — SharedPreferences достаточно,
 * отдельный data-store слой не нужен.
 */
class MasterProSettings @Inject constructor(
    private val context: Context,
) {

    private val _data = MutableStateFlow(read())
    val data: StateFlow<MasterProSettingsData> = _data.asStateFlow()

    fun save(data: MasterProSettingsData) {
        prefs().edit()
            .putString(KEY_MASTER_NAME, data.masterName.trim())
            .putString(KEY_TEMPLATE, data.messageTemplate)
            .apply()
        _data.value = data.copy(masterName = data.masterName.trim())
    }

    private fun read(): MasterProSettingsData = MasterProSettingsData(
        masterName = prefs().getString(KEY_MASTER_NAME, "").orEmpty(),
        messageTemplate = prefs().getString(KEY_TEMPLATE, null)
            ?: context.getString(R.string.settings_template_default),
    )

    private fun prefs() = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private companion object {
        const val PREFS_NAME = "masterpro_settings"
        const val KEY_MASTER_NAME = "master_name"
        const val KEY_TEMPLATE = "message_template"
    }
}
