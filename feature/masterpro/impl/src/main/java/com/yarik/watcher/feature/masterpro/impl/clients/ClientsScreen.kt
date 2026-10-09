package com.yarik.watcher.feature.masterpro.impl.clients

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yarik.watcher.core.database.entity.ClientWithDebt
import com.yarik.watcher.core.navigation.router.JoyRouter
import com.yarik.watcher.core.ui.ViewModelFactory
import com.yarik.watcher.core.ui.WatcherToolbar
import com.yarik.watcher.feature.masterpro.api.ClientCardJoyScreen
import com.yarik.watcher.feature.masterpro.impl.R
import com.yarik.watcher.feature.masterpro.impl.ui.ClientFormDialog
import com.yarik.watcher.feature.masterpro.impl.ui.formatKopecks

@Composable
fun ClientsScreen(
    viewModelFactory: ViewModelFactory,
    router: JoyRouter,
) {
    val viewModel: ClientsViewModel = viewModel(factory = viewModelFactory)
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.errors.collect { messageRes ->
            snackbarHostState.showSnackbar(context.getString(messageRes))
        }
    }

    Scaffold(
        topBar = {
            WatcherToolbar(
                title = stringResource(R.string.clients_title),
                onBackClick = { router.exit() },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.openCreateDialog() },
            ) {
                Text(text = stringResource(R.string.clients_fab_new_client))
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                value = state.query,
                onValueChange = { viewModel.onQueryChange(it) },
                label = { Text(text = stringResource(R.string.clients_search_hint)) },
                singleLine = true,
            )

            if (state.clients.isEmpty()) {
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(32.dp),
                    text = stringResource(
                        if (state.query.isBlank()) R.string.clients_empty else R.string.clients_nothing_found,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(state.clients, key = { it.client.id }) { clientWithDebt ->
                        ClientRow(
                            clientWithDebt = clientWithDebt,
                            onClick = { router.navigateTo(ClientCardJoyScreen.create(clientWithDebt.client.id)) },
                        )
                    }
                }
            }
        }
    }

    if (state.dialogOpen) {
        ClientFormDialog(
            editClient = state.editClient,
            saving = state.dialogSaving,
            onSave = { name, phone, address, notes ->
                viewModel.saveClient(state.editClient?.id, name, phone, address, notes)
            },
            onDismiss = { viewModel.closeDialog() },
        )
    }
}

@Composable
private fun ClientRow(clientWithDebt: ClientWithDebt, onClick: () -> Unit) {
    val client = clientWithDebt.client
    Card(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = client.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                if (client.phone.isNotBlank()) {
                    Text(
                        text = client.phone,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (client.address.isNotBlank()) {
                    Text(
                        text = client.address,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            if (clientWithDebt.debt > 0) {
                AssistChip(
                    onClick = onClick,
                    label = {
                        Text(text = stringResource(R.string.clients_debt_badge, clientWithDebt.debt.formatKopecks()))
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        labelColor = MaterialTheme.colorScheme.error,
                    ),
                )
            }
        }
    }
}
