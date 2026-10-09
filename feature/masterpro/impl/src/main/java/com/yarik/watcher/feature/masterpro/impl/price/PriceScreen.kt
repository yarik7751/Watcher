package com.yarik.watcher.feature.masterpro.impl.price

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yarik.watcher.core.database.entity.PriceItemEntity
import com.yarik.watcher.core.navigation.router.JoyRouter
import com.yarik.watcher.core.ui.ViewModelFactory
import com.yarik.watcher.core.ui.WatcherToolbar
import com.yarik.watcher.feature.masterpro.impl.R
import com.yarik.watcher.feature.masterpro.impl.ui.formatKopecks
import com.yarik.watcher.feature.masterpro.impl.ui.parseMoneyToKopecks

@Composable
fun PriceScreen(
    viewModelFactory: ViewModelFactory,
    router: JoyRouter,
) {
    val viewModel: PriceViewModel = viewModel(factory = viewModelFactory)
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
                title = stringResource(R.string.price_title),
                onBackClick = { router.exit() },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.openCreateDialog() },
            ) {
                Text(text = stringResource(R.string.price_fab_new_item))
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            if (state.items.isEmpty()) {
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(32.dp),
                    text = stringResource(R.string.price_empty),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(state.items, key = { it.id }) { item ->
                        PriceRow(
                            item = item,
                            onClick = { viewModel.openEditDialog(item) },
                            onDelete = { viewModel.deleteItem(item) },
                        )
                    }
                }
            }
        }
    }

    if (state.dialogOpen) {
        PriceItemDialog(
            editItem = state.editItem,
            saving = state.dialogSaving,
            onSave = { title, priceKopecks ->
                viewModel.saveItem(state.editItem?.id, title, priceKopecks)
            },
            onDismiss = { viewModel.closeDialog() },
        )
    }
}

@Composable
private fun PriceRow(
    item: PriceItemEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = item.price.formatKopecks(),
                style = MaterialTheme.typography.bodyLarge,
            )
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = null)
            }
        }
    }
}

@Composable
private fun PriceItemDialog(
    editItem: PriceItemEntity?,
    saving: Boolean,
    onSave: (title: String, priceKopecks: Long) -> Unit,
    onDismiss: () -> Unit,
) {
    var title by rememberSaveable { mutableStateOf(editItem?.title ?: "") }
    var priceText by rememberSaveable {
        mutableStateOf(editItem?.let { (it.price / 100.0).toString().removeSuffix(".0") } ?: "")
    }
    val priceKopecks = parseMoneyToKopecks(priceText)

    AlertDialog(
        onDismissRequest = { if (!saving) onDismiss() },
        title = {
            Text(
                text = stringResource(
                    if (editItem == null) R.string.price_dialog_new_title
                    else R.string.price_dialog_edit_title,
                ),
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(text = stringResource(R.string.price_field_title_hint)) },
                    singleLine = true,
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text(text = stringResource(R.string.price_field_price_hint)) },
                    singleLine = true,
                )
                if (priceText.isNotBlank() && priceKopecks == null) {
                    Text(
                        modifier = Modifier.padding(top = 4.dp),
                        text = stringResource(R.string.price_error_invalid_price),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        },
        confirmButton = {
            Button(
                enabled = !saving && priceKopecks != null,
                onClick = { priceKopecks?.let { onSave(title, it) } },
            ) {
                Text(text = stringResource(R.string.jobcard_save))
            }
        },
        dismissButton = {
            TextButton(
                enabled = !saving,
                onClick = onDismiss,
            ) {
                Text(text = stringResource(R.string.jobcard_cancel))
            }
        },
    )
}
