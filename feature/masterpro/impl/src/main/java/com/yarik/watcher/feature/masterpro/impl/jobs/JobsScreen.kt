package com.yarik.watcher.feature.masterpro.impl.jobs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yarik.watcher.core.database.entity.JobStatus
import com.yarik.watcher.core.database.entity.JobWithClient
import com.yarik.watcher.core.navigation.router.JoyRouter
import com.yarik.watcher.core.ui.ViewModelFactory
import com.yarik.watcher.feature.masterpro.api.JobCardJoyScreen
import com.yarik.watcher.feature.masterpro.impl.R
import com.yarik.watcher.feature.masterpro.impl.ui.formatKopecks
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val TABS = listOf(
    JobStatus.NEW to R.string.jobs_tab_new,
    JobStatus.IN_PROGRESS to R.string.jobs_tab_in_progress,
    JobStatus.AWAIT_PAYMENT to R.string.jobs_tab_await_payment,
    JobStatus.DONE to R.string.jobs_tab_done,
)

private val DATE_FORMAT = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("ru", "RU"))

/** Маркер «новый клиент» в выпадающем списке диалога создания */
private const val NEW_CLIENT_ID = -1L

@Composable
fun JobsScreen(
    viewModelFactory: ViewModelFactory,
    router: JoyRouter,
) {
    val viewModel: JobsViewModel = viewModel(factory = viewModelFactory)
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.errors.collect { messageRes ->
            snackbarHostState.showSnackbar(context.getString(messageRes))
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.openCreateDialog() },
            ) {
                Text(text = stringResource(R.string.jobs_fab_new_job))
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = stringResource(R.string.jobs_title),
                style = MaterialTheme.typography.headlineSmall,
            )

            TabRow(selectedTabIndex = TABS.indexOfFirst { it.first == state.status }) {
                TABS.forEach { (status, labelRes) ->
                    Tab(
                        selected = state.status == status,
                        onClick = { viewModel.selectStatus(status) },
                        text = { Text(text = stringResource(labelRes)) },
                    )
                }
            }

            if (state.jobs.isEmpty()) {
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(32.dp),
                    text = stringResource(R.string.jobs_empty),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(state.jobs, key = { it.job.id }) { jobWithClient ->
                        JobRow(
                            jobWithClient = jobWithClient,
                            onClick = { router.navigateTo(JobCardJoyScreen.create(jobWithClient.job.id)) },
                        )
                    }
                }
            }
        }
    }

    if (state.createDialogOpen) {
        CreateJobDialog(
            clients = state.clients,
            creating = state.creating,
            onCreate = { clientId, newClientName, title, address ->
                viewModel.createJob(clientId, newClientName, title, address)
            },
            onDismiss = { viewModel.closeCreateDialog() },
        )
    }
}

@Composable
private fun JobRow(jobWithClient: JobWithClient, onClick: () -> Unit) {
    val job = jobWithClient.job
    Card(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = job.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = jobWithClient.clientName,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                job.scheduledAt?.let { scheduledAt ->
                    Text(
                        text = DATE_FORMAT.format(Date(scheduledAt)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = stringResource(R.string.jobs_money_format, job.totalAmount.formatKopecks()),
                    style = MaterialTheme.typography.titleMedium,
                )
                if (jobWithClient.debt > 0) {
                    Text(
                        text = stringResource(R.string.jobs_debt_label, jobWithClient.debt.formatKopecks()),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateJobDialog(
    clients: List<com.yarik.watcher.core.database.entity.ClientEntity>,
    creating: Boolean,
    onCreate: (clientId: Long?, newClientName: String?, title: String, address: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var selectedClientId by rememberSaveable { mutableStateOf<Long?>(null) }
    var dropdownExpanded by remember { mutableStateOf(false) }
    var newClientName by rememberSaveable { mutableStateOf("") }
    var title by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { mutableStateOf("") }

    val selectedClient = clients.firstOrNull { it.id == selectedClientId }
    val isNewClient = selectedClientId == NEW_CLIENT_ID
    val clientLabel = when {
        selectedClient != null -> selectedClient.name
        isNewClient -> stringResource(R.string.jobs_create_client_new)
        else -> ""
    }

    AlertDialog(
        onDismissRequest = { if (!creating) onDismiss() },
        title = { Text(text = stringResource(R.string.jobs_create_dialog_title)) },
        text = {
            Column {
                ExposedDropdownMenuBox(
                    expanded = dropdownExpanded,
                    onExpandedChange = { dropdownExpanded = it },
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        value = clientLabel,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(text = stringResource(R.string.jobs_create_client_label)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                    )
                    ExposedDropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false },
                    ) {
                        clients.forEach { client ->
                            androidx.compose.material3.DropdownMenuItem(
                                text = { Text(client.name) },
                                onClick = {
                                    selectedClientId = client.id
                                    dropdownExpanded = false
                                },
                            )
                        }
                        androidx.compose.material3.DropdownMenuItem(
                            text = { Text(text = stringResource(R.string.jobs_create_client_new)) },
                            onClick = {
                                selectedClientId = NEW_CLIENT_ID
                                dropdownExpanded = false
                            },
                        )
                    }
                }

                if (isNewClient) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        value = newClientName,
                        onValueChange = { newClientName = it },
                        label = { Text(text = stringResource(R.string.jobs_create_client_name_hint)) },
                        singleLine = true,
                    )
                }

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(text = stringResource(R.string.jobs_create_title_hint)) },
                    singleLine = true,
                )

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    value = address,
                    onValueChange = { address = it },
                    label = { Text(text = stringResource(R.string.jobs_create_address_hint)) },
                    singleLine = true,
                )
            }
        },
        confirmButton = {
            Button(
                enabled = !creating,
                onClick = {
                    onCreate(
                        if (isNewClient) null else selectedClientId,
                        if (isNewClient) newClientName else null,
                        title,
                        address,
                    )
                },
            ) {
                Text(text = stringResource(R.string.jobs_create_confirm))
            }
        },
        dismissButton = {
            TextButton(
                enabled = !creating,
                onClick = onDismiss,
            ) {
                Text(text = stringResource(R.string.jobs_create_cancel))
            }
        },
    )
}
