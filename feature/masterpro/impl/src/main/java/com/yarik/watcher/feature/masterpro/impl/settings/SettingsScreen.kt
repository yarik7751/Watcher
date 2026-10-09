package com.yarik.watcher.feature.masterpro.impl.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yarik.watcher.core.navigation.router.JoyRouter
import com.yarik.watcher.core.ui.ViewModelFactory
import com.yarik.watcher.core.ui.WatcherToolbar
import com.yarik.watcher.feature.masterpro.impl.R

@Composable
fun SettingsScreen(
    viewModelFactory: ViewModelFactory,
    router: JoyRouter,
) {
    val viewModel: SettingsViewModel = viewModel(factory = viewModelFactory)
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    var masterName by rememberSaveable { mutableStateOf(state.masterName) }
    var template by rememberSaveable { mutableStateOf(state.messageTemplate) }

    LaunchedEffect(state.masterName, state.messageTemplate) {
        // Подтягиваем сохранённые значения при первой загрузке
        if (masterName.isEmpty() && state.masterName.isNotEmpty()) masterName = state.masterName
        if (template.isEmpty() && state.messageTemplate.isNotEmpty()) template = state.messageTemplate
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { messageRes ->
            snackbarHostState.showSnackbar(context.getString(messageRes))
        }
    }

    LaunchedEffect(Unit) {
        viewModel.exportDone.collect { count ->
            snackbarHostState.showSnackbar(context.getString(R.string.settings_export_done, count))
        }
    }

    Scaffold(
        topBar = {
            WatcherToolbar(
                title = stringResource(R.string.settings_title),
                onBackClick = { router.exit() },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                value = masterName,
                onValueChange = { masterName = it },
                label = { Text(text = stringResource(R.string.settings_master_name_hint)) },
                singleLine = true,
            )
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                value = template,
                onValueChange = { template = it },
                label = {
                    Text(
                        text = stringResource(
                            R.string.settings_template_hint,
                            "%1\$s",
                            "%2\$s",
                        ),
                    )
                },
            )

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                onClick = { viewModel.save(masterName, template) },
            ) {
                Text(text = stringResource(R.string.settings_save))
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))

            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.exporting,
                onClick = { viewModel.export() },
            ) {
                Text(
                    text = stringResource(
                        if (state.exporting) R.string.settings_exporting else R.string.settings_export,
                    ),
                )
            }
        }
    }
}
