package com.yarik.watcher.feature.masterpro.impl.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yarik.watcher.core.database.entity.ClientEntity
import com.yarik.watcher.feature.masterpro.impl.R

/** Форма создания/редактирования клиента: имя, телефон, адрес, заметки. */
@Composable
internal fun ClientFormDialog(
    editClient: ClientEntity?,
    saving: Boolean,
    onSave: (name: String, phone: String, address: String, notes: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by rememberSaveable { mutableStateOf(editClient?.name ?: "") }
    var phone by rememberSaveable { mutableStateOf(editClient?.phone ?: "") }
    var address by rememberSaveable { mutableStateOf(editClient?.address ?: "") }
    var notes by rememberSaveable { mutableStateOf(editClient?.notes ?: "") }

    AlertDialog(
        onDismissRequest = { if (!saving) onDismiss() },
        title = {
            Text(
                text = stringResource(
                    if (editClient == null) R.string.clients_dialog_new_title
                    else R.string.clients_dialog_edit_title,
                ),
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(text = stringResource(R.string.clients_field_name_hint)) },
                    singleLine = true,
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(text = stringResource(R.string.clients_field_phone_hint)) },
                    singleLine = true,
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    value = address,
                    onValueChange = { address = it },
                    label = { Text(text = stringResource(R.string.clients_field_address_hint)) },
                    singleLine = true,
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(text = stringResource(R.string.clients_field_notes_hint)) },
                )
            }
        },
        confirmButton = {
            Button(
                enabled = !saving,
                onClick = { onSave(name, phone, address, notes) },
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
