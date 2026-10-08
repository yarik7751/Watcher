package com.yarik.watcher.feature.masterpro.impl.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yarik.watcher.feature.masterpro.impl.R
import com.yarik.watcher.feature.masterpro.impl.data.export.CsvExport
import com.yarik.watcher.feature.masterpro.impl.data.settings.MasterProSettings
import com.yarik.watcher.feature.masterpro.impl.data.settings.MasterProSettingsData
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SettingsUiState(
    val masterName: String = "",
    val messageTemplate: String = "",
    val exporting: Boolean = false,
)

class SettingsViewModel @Inject constructor(
    private val settings: MasterProSettings,
    private val csvExport: CsvExport,
) : ViewModel() {

    private val exportingFlow = MutableStateFlow(false)

    /** События для snackbar: готовые строковые ресурсы (export_done передаёт число файлов отдельно) */
    private val _events = MutableSharedFlow<Int>(extraBufferCapacity = 1)
    val events: SharedFlow<Int> = _events

    private val _exportDone = MutableSharedFlow<Int>(extraBufferCapacity = 1)
    val exportDone: SharedFlow<Int> = _exportDone

    val uiState: StateFlow<SettingsUiState> = combine(
        settings.data,
        exportingFlow,
    ) { data, exporting ->
        SettingsUiState(
            masterName = data.masterName,
            messageTemplate = data.messageTemplate,
            exporting = exporting,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState())

    fun save(masterName: String, messageTemplate: String) {
        settings.save(MasterProSettingsData(masterName = masterName, messageTemplate = messageTemplate))
        viewModelScope.launch { _events.emit(R.string.settings_saved) }
    }

    fun export() {
        viewModelScope.launch {
            exportingFlow.value = true
            csvExport.exportAll()
                .onSuccess { count -> _exportDone.emit(count) }
                .onFailure { _events.emit(R.string.settings_export_failed) }
            exportingFlow.value = false
        }
    }
}
