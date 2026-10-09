package com.yarik.watcher.feature.masterpro.impl.clientcard

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import com.yarik.watcher.core.database.entity.JobWithClient
import com.yarik.watcher.core.navigation.router.JoyRouter
import com.yarik.watcher.core.ui.ViewModelFactory
import com.yarik.watcher.core.ui.WatcherToolbar
import com.yarik.watcher.feature.masterpro.api.ClientCardJoyScreen
import com.yarik.watcher.feature.masterpro.api.JobCardJoyScreen
import com.yarik.watcher.feature.masterpro.impl.R
import com.yarik.watcher.feature.masterpro.impl.ui.ClientFormDialog
import com.yarik.watcher.feature.masterpro.impl.ui.formatKopecks

@Composable
fun ClientCardScreen(
    viewModelFactory: ViewModelFactory,
    router: JoyRouter,
) {
    val viewModel: ClientCardViewModel = viewModel(factory = viewModelFactory)
    val owner = LocalViewModelStoreOwner.current as? NavBackStackEntry
    val clientId = owner?.arguments?.getLong(ClientCardJoyScreen.ARG_CLIENT_ID)

    LaunchedEffect(clientId) {
        if (clientId != null && clientId > 0) viewModel.init(clientId)
    }

    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.errors.collect { messageRes ->
            snackbarHostState.showSnackbar(context.getString(messageRes))
        }
    }

    // Клиент удалён — уходим с экрана
    LaunchedEffect(Unit) {
        viewModel.deleted.collect { router.exit() }
    }

    Scaffold(
        topBar = {
            WatcherToolbar(
                title = state.client?.name ?: stringResource(R.string.clientcard_title),
                onBackClick = { router.exit() },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            when {
                clientId == null || clientId <= 0 || state.client == null && !state.loading -> {
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = stringResource(R.string.clientcard_not_found),
                    )
                }

                state.loading -> {
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = stringResource(R.string.clientcard_loading),
                    )
                }

                else -> ClientCardContent(
                    viewModel = viewModel,
                    state = state,
                    router = router,
                )
            }
        }
    }
}

@Composable
private fun ClientCardContent(
    viewModel: ClientCardViewModel,
    state: ClientCardUiState,
    router: JoyRouter,
) {
    val client = state.client ?: return
    val context = LocalContext.current

    var editDialogOpen by rememberSaveable { mutableStateOf(false) }
    var deleteConfirmOpen by rememberSaveable { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = client.name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )

        // Телефон: tap → звонок
        if (client.phone.isNotBlank()) {
            Text(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clickable {
                        context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${client.phone}")))
                    },
                text = stringResource(R.string.clientcard_phone_label, client.phone),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        } else {
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = stringResource(R.string.clientcard_no_phone),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (client.address.isNotBlank()) {
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = stringResource(R.string.clientcard_address_label, client.address),
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        if (state.debt > 0) {
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = stringResource(R.string.clientcard_debt, state.debt.formatKopecks()),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold,
            )
        } else {
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = stringResource(R.string.clientcard_debt_free),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        // Действия: звонок + WhatsApp
        Row(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val digitsOnly = client.phone.filter { it.isDigit() }
            OutlinedButton(
                enabled = digitsOnly.isNotEmpty(),
                onClick = {
                    context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${client.phone}")))
                },
            ) {
                Text(text = stringResource(R.string.clientcard_call))
            }
            Button(
                enabled = digitsOnly.length >= PHONE_MIN_DIGITS,
                onClick = {
                    val text = Uri.encode(buildWhatsAppText(state, client.name, context))
                    val uri = Uri.parse("https://wa.me/$digitsOnly?text=$text")
                    try {
                        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                    } catch (e: ActivityNotFoundException) {
                        // WhatsApp не установлен — игнорируем, кнопка останется на экране
                    }
                },
            ) {
                Text(text = stringResource(R.string.clientcard_whatsapp))
            }
        }

        if (client.notes.isNotBlank()) {
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = client.notes,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Row(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TextButton(onClick = { editDialogOpen = true }) {
                Text(text = stringResource(R.string.clientcard_edit))
            }
            TextButton(onClick = { deleteConfirmOpen = true }) {
                Text(
                    text = stringResource(R.string.clientcard_delete),
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        Text(
            text = stringResource(R.string.clientcard_section_history),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        if (state.jobs.isEmpty()) {
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = stringResource(R.string.clientcard_history_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            state.jobs.forEach { jobWithClient ->
                JobHistoryRow(
                    jobWithClient = jobWithClient,
                    onClick = { router.navigateTo(JobCardJoyScreen.create(jobWithClient.job.id)) },
                )
            }
        }
    }

    if (editDialogOpen) {
        ClientFormDialog(
            editClient = client,
            saving = false,
            onSave = { name, phone, address, notes ->
                viewModel.updateClient(name, phone, address, notes)
                editDialogOpen = false
            },
            onDismiss = { editDialogOpen = false },
        )
    }

    if (deleteConfirmOpen) {
        AlertDialog(
            onDismissRequest = { deleteConfirmOpen = false },
            text = { Text(text = stringResource(R.string.clientcard_delete_confirm)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        deleteConfirmOpen = false
                        viewModel.deleteClient()
                    },
                ) {
                    Text(
                        text = stringResource(R.string.clientcard_delete_yes),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteConfirmOpen = false }) {
                    Text(text = stringResource(R.string.jobcard_cancel))
                }
            },
        )
    }
}

@Composable
private fun JobHistoryRow(jobWithClient: JobWithClient, onClick: () -> Unit) {
    val job = jobWithClient.job
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .clickable { onClick() },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = job.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(statusLabelRes(job.status)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = job.totalAmount.formatKopecks(),
                    style = MaterialTheme.typography.bodyLarge,
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

private fun statusLabelRes(status: com.yarik.watcher.core.database.entity.JobStatus): Int =
    when (status) {
        com.yarik.watcher.core.database.entity.JobStatus.NEW -> R.string.jobs_tab_new
        com.yarik.watcher.core.database.entity.JobStatus.IN_PROGRESS -> R.string.jobs_tab_in_progress
        com.yarik.watcher.core.database.entity.JobStatus.AWAIT_PAYMENT -> R.string.jobs_tab_await_payment
        com.yarik.watcher.core.database.entity.JobStatus.DONE -> R.string.jobs_tab_done
    }

private fun buildWhatsAppText(
    state: ClientCardUiState,
    clientName: String,
    context: android.content.Context,
): String {
    // Битый шаблон (остался % без аргумента и т.п.) не должен ронять экран — откатываемся на дефолт
    return runCatching {
        String.format(state.messageTemplate, clientName, state.masterName)
    }.getOrElse {
        context.getString(R.string.clientcard_whatsapp_text, clientName)
    }
}

private const val PHONE_MIN_DIGITS = 10
