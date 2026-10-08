package com.yarik.watcher.feature.masterpro.impl.jobcard

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import coil3.compose.AsyncImage
import com.yarik.watcher.core.database.entity.JobItemEntity
import com.yarik.watcher.core.database.entity.JobPhotoEntity
import com.yarik.watcher.core.database.entity.JobStatus
import com.yarik.watcher.core.database.entity.PaymentEntity
import com.yarik.watcher.core.database.entity.PaymentMethod
import com.yarik.watcher.core.database.entity.PhotoKind
import com.yarik.watcher.core.database.entity.PriceItemEntity
import com.yarik.watcher.core.navigation.router.JoyRouter
import com.yarik.watcher.core.ui.ViewModelFactory
import com.yarik.watcher.feature.masterpro.api.ClientCardJoyScreen
import com.yarik.watcher.feature.masterpro.api.JobCardJoyScreen
import com.yarik.watcher.feature.masterpro.impl.R
import com.yarik.watcher.feature.masterpro.impl.data.model.JobDetails
import com.yarik.watcher.feature.masterpro.impl.ui.formatKopecks
import com.yarik.watcher.feature.masterpro.impl.ui.parseMoneyToKopecks
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val DATE_FORMAT = SimpleDateFormat("dd.MM.yyyy", Locale("ru", "RU"))

private fun statusLabelRes(status: JobStatus): Int = when (status) {
    JobStatus.NEW -> R.string.jobs_tab_new
    JobStatus.IN_PROGRESS -> R.string.jobs_tab_in_progress
    JobStatus.AWAIT_PAYMENT -> R.string.jobs_tab_await_payment
    JobStatus.DONE -> R.string.jobs_tab_done
}

private fun paymentMethodLabelRes(method: PaymentMethod): Int = when (method) {
    PaymentMethod.CASH -> R.string.jobcard_payment_cash
    PaymentMethod.CARD -> R.string.jobcard_payment_card
    PaymentMethod.TRANSFER -> R.string.jobcard_payment_transfer
}

/** Плановая дата хранится с временем по умолчанию 09:00 (время визита уточняется с клиентом) */
private fun atDefaultTime(dateMillis: Long): Long = Calendar.getInstance().apply {
    timeInMillis = dateMillis
    set(Calendar.HOUR_OF_DAY, 9)
    set(Calendar.MINUTE, 0)
    set(Calendar.SECOND, 0)
    set(Calendar.MILLISECOND, 0)
}.timeInMillis

@Composable
fun JobCardScreen(
    viewModelFactory: ViewModelFactory,
    router: JoyRouter,
) {
    val viewModel: JobCardViewModel = viewModel(factory = viewModelFactory)
    val owner = LocalViewModelStoreOwner.current as? NavBackStackEntry
    val jobId = owner?.arguments?.getLong(JobCardJoyScreen.ARG_JOB_ID)

    LaunchedEffect(jobId) {
        if (jobId != null && jobId > 0) viewModel.init(jobId)
    }

    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.errors.collect { messageRes ->
            snackbarHostState.showSnackbar(context.getString(messageRes))
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            when {
                jobId == null || jobId <= 0 -> {
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = stringResource(R.string.jobcard_not_found),
                    )
                }

                state.loading -> {
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = stringResource(R.string.jobcard_loading),
                    )
                }

                state.details == null -> {
                    Text(
                        modifier = Modifier.padding(16.dp),
                        text = stringResource(R.string.jobcard_not_found),
                    )
                    TextButton(onClick = { router.exit() }) {
                        Text(text = stringResource(R.string.jobcard_back))
                    }
                }

                else -> JobCardContent(
                    viewModel = viewModel,
                    details = state.details!!,
                    clientName = state.client?.name ?: "",
                    router = router,
                )
            }
        }
    }
}

@Composable
private fun JobCardContent(
    viewModel: JobCardViewModel,
    details: JobDetails,
    clientName: String,
    router: JoyRouter,
) {
    val job = details.job
    val isDone = job.status == JobStatus.DONE

    var editDialogOpen by rememberSaveable { mutableStateOf(false) }
    var addItemDialogOpen by rememberSaveable { mutableStateOf(false) }
    var addPaymentDialogOpen by rememberSaveable { mutableStateOf(false) }
    var viewerPhoto by remember { mutableStateOf<JobPhotoEntity?>(null) }

    // Фото: камера/галерея. captureKind — для какой секции идёт захват.
    var captureKind by remember { mutableStateOf<PhotoKind?>(null) }
    var pendingCaptureUri by remember { mutableStateOf<android.net.Uri?>(null) }
    val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        val kind = captureKind
        val uri = pendingCaptureUri
        captureKind = null
        pendingCaptureUri = null
        if (success && kind != null && uri != null) viewModel.addPhoto(uri, kind)
    }
    val pickPicture = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        val kind = captureKind
        captureKind = null
        if (uri != null && kind != null) viewModel.addPhoto(uri, kind)
    }

    fun launchCamera(kind: PhotoKind) {
        viewModel.createCaptureUri(kind)?.let { uri ->
            pendingCaptureUri = uri
            captureKind = kind
            takePicture.launch(uri)
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        TextButton(onClick = { router.exit() }) {
            Text(text = stringResource(R.string.jobcard_back))
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                modifier = Modifier.weight(1f),
                text = job.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            AssistChip(
                onClick = {},
                label = { Text(text = stringResource(statusLabelRes(job.status))) },
            )
        }

        Text(
            modifier = Modifier.clickable {
                router.navigateTo(ClientCardJoyScreen.create(job.clientId))
            },
            text = stringResource(R.string.jobcard_client_label, clientName),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        if (job.address.isNotBlank()) {
            Text(
                text = job.address,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = stringResource(R.string.jobcard_plan_date_label) + ": " +
                (job.scheduledAt?.let { DATE_FORMAT.format(Date(it)) }
                    ?: stringResource(R.string.jobcard_plan_date_empty)),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (job.notes.isNotBlank()) {
            Text(
                text = job.notes,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        if (!isDone) {
            TextButton(onClick = { editDialogOpen = true }) {
                Text(text = stringResource(R.string.jobcard_edit_data))
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // region Смета

        SectionTitle(text = stringResource(R.string.jobcard_section_estimate))
        details.items.forEach { item ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = item.title, style = MaterialTheme.typography.bodyLarge)
                    Text(
                        text = "${item.qty} × ${item.price.formatKopecks()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text = (item.qty * item.price).formatKopecks(),
                    style = MaterialTheme.typography.bodyLarge,
                )
                if (!isDone) {
                    IconButton(onClick = { viewModel.deleteItem(item) }) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                    }
                }
            }
        }
        Text(
            modifier = Modifier.padding(top = 4.dp),
            text = stringResource(R.string.jobcard_total, details.job.totalAmount.formatKopecks()),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        if (!isDone) {
            OutlinedButton(
                modifier = Modifier.padding(top = 4.dp),
                onClick = { addItemDialogOpen = true },
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Text(text = stringResource(R.string.jobcard_add_item))
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // endregion

        // region Фото

        PhotoSection(
            title = stringResource(R.string.jobcard_section_photos_before),
            photos = details.photosBefore,
            enabled = !isDone,
            onTake = { launchCamera(PhotoKind.BEFORE) },
            onPick = {
                captureKind = PhotoKind.BEFORE
                pickPicture.launch("image/*")
            },
            onClick = { viewerPhoto = it },
        )
        PhotoSection(
            title = stringResource(R.string.jobcard_section_photos_after),
            photos = details.photosAfter,
            enabled = !isDone,
            onTake = { launchCamera(PhotoKind.AFTER) },
            onPick = {
                captureKind = PhotoKind.AFTER
                pickPicture.launch("image/*")
            },
            onClick = { viewerPhoto = it },
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // endregion

        // region Оплаты

        SectionTitle(text = stringResource(R.string.jobcard_section_payments))
        details.payments.forEach { payment ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(paymentMethodLabelRes(payment.method)) + ", " +
                            DATE_FORMAT.format(Date(payment.paidAt)),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                Text(
                    text = payment.amount.formatKopecks(),
                    style = MaterialTheme.typography.bodyLarge,
                )
                if (!isDone) {
                    IconButton(onClick = { viewModel.deletePayment(payment) }) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                    }
                }
            }
        }
        Text(
            modifier = Modifier.padding(top = 4.dp),
            text = stringResource(R.string.jobcard_paid, job.paidAmount.formatKopecks()),
            style = MaterialTheme.typography.bodyLarge,
        )
        if (details.debt > 0) {
            Text(
                text = stringResource(R.string.jobcard_debt, details.debt.formatKopecks()),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold,
            )
        } else if (job.totalAmount > 0) {
            Text(
                text = stringResource(R.string.jobcard_debt_free),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        if (!isDone) {
            OutlinedButton(
                modifier = Modifier.padding(top = 4.dp),
                onClick = { addPaymentDialogOpen = true },
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Text(text = stringResource(R.string.jobcard_add_payment))
            }
        }

        // endregion

        if (isDone) {
            job.finishedAt?.let { finishedAt ->
                Text(
                    modifier = Modifier.padding(top = 12.dp),
                    text = stringResource(R.string.jobcard_finished_at, DATE_FORMAT.format(Date(finishedAt))),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                onClick = { viewModel.finishJob() },
            ) {
                Text(text = stringResource(R.string.jobcard_finish))
            }
        }
    }

    if (editDialogOpen) {
        EditJobDialog(
            job = job,
            onSave = { title, address, scheduledAt, notes, status ->
                viewModel.updateJob(title, address, scheduledAt, notes, status)
                editDialogOpen = false
            },
            onDismiss = { editDialogOpen = false },
        )
    }

    if (addItemDialogOpen) {
        val priceItems by viewModel.priceItems.collectAsState()
        AddItemDialog(
            priceItems = priceItems,
            onAddFromPrice = { priceItem, qty ->
                viewModel.addItemFromPrice(priceItem, qty)
                addItemDialogOpen = false
            },
            onAddManual = { title, qty, priceKopecks ->
                viewModel.addItemManual(title, qty, priceKopecks)
                addItemDialogOpen = false
            },
            onDismiss = { addItemDialogOpen = false },
        )
    }

    if (addPaymentDialogOpen) {
        AddPaymentDialog(
            debtKopecks = details.debt,
            onAdd = { amountKopecks, method ->
                viewModel.addPayment(amountKopecks, method)
                addPaymentDialogOpen = false
            },
            onDismiss = { addPaymentDialogOpen = false },
        )
    }

    viewerPhoto?.let { photo ->
        PhotoViewerDialog(
            photo = photo,
            onDelete = {
                viewModel.deletePhoto(JobPhotoEntityUi(photo))
                viewerPhoto = null
            },
            onDismiss = { viewerPhoto = null },
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        modifier = Modifier.padding(bottom = 4.dp),
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
private fun PhotoSection(
    title: String,
    photos: List<JobPhotoEntity>,
    enabled: Boolean,
    onTake: () -> Unit,
    onPick: () -> Unit,
    onClick: (JobPhotoEntity) -> Unit,
) {
    SectionTitle(text = title)
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        photos.forEach { photo ->
            Card(modifier = Modifier.clickable { onClick(photo) }) {
                AsyncImage(
                    modifier = Modifier.size(88.dp),
                    model = File(photo.filePath),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                )
            }
        }
        if (enabled) {
            OutlinedButton(onClick = onTake) {
                Text(text = stringResource(R.string.jobcard_take_photo))
            }
            OutlinedButton(onClick = onPick) {
                Text(text = stringResource(R.string.jobcard_pick_photo))
            }
        }
    }
}

@Composable
private fun PhotoViewerDialog(
    photo: JobPhotoEntity,
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            AsyncImage(
                modifier = Modifier.fillMaxWidth(),
                model = File(photo.filePath),
                contentDescription = null,
                contentScale = ContentScale.Fit,
            )
        },
        confirmButton = {
            TextButton(onClick = onDelete) {
                Text(
                    text = stringResource(R.string.jobcard_photo_delete),
                    color = MaterialTheme.colorScheme.error,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.jobcard_cancel))
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditJobDialog(
    job: com.yarik.watcher.core.database.entity.JobEntity,
    onSave: (title: String, address: String, scheduledAt: Long?, notes: String, status: JobStatus) -> Unit,
    onDismiss: () -> Unit,
) {
    var title by rememberSaveable { mutableStateOf(job.title) }
    var address by rememberSaveable { mutableStateOf(job.address) }
    var notes by rememberSaveable { mutableStateOf(job.notes) }
    var scheduledAt by rememberSaveable { mutableStateOf(job.scheduledAt) }
    var status by rememberSaveable { mutableStateOf(job.status) }
    var datePickerOpen by rememberSaveable { mutableStateOf(false) }
    var statusDropdownOpen by remember { mutableStateOf(false) }

    // DONE через диалог не выбирается — только кнопка «Завершить»
    val selectableStatuses = listOf(JobStatus.NEW, JobStatus.IN_PROGRESS, JobStatus.AWAIT_PAYMENT)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.jobcard_edit_dialog_title)) },
        text = {
            Column {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
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
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(text = stringResource(R.string.jobcard_field_notes_hint)) },
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = stringResource(R.string.jobcard_plan_date_label) + ": " +
                            (scheduledAt?.let { DATE_FORMAT.format(Date(it)) }
                                ?: stringResource(R.string.jobcard_plan_date_empty)),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    TextButton(onClick = { datePickerOpen = true }) {
                        Text(text = stringResource(R.string.jobcard_plan_date_pick))
                    }
                    TextButton(onClick = { scheduledAt = null }) {
                        Text(text = stringResource(R.string.jobcard_plan_date_clear))
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = statusDropdownOpen,
                    onExpandedChange = { statusDropdownOpen = it },
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        value = stringResource(statusLabelRes(status)),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(text = stringResource(R.string.jobcard_status_label)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusDropdownOpen) },
                    )
                    ExposedDropdownMenu(
                        expanded = statusDropdownOpen,
                        onDismissRequest = { statusDropdownOpen = false },
                    ) {
                        selectableStatuses.forEach { option ->
                            androidx.compose.material3.DropdownMenuItem(
                                text = { Text(text = stringResource(statusLabelRes(option))) },
                                onClick = {
                                    status = option
                                    statusDropdownOpen = false
                                },
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSave(title, address, scheduledAt, notes, status) }) {
                Text(text = stringResource(R.string.jobcard_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.jobcard_cancel))
            }
        },
    )

    if (datePickerOpen) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = scheduledAt ?: System.currentTimeMillis(),
        )
        DatePickerDialog(
            onDismissRequest = { datePickerOpen = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { scheduledAt = atDefaultTime(it) }
                        datePickerOpen = false
                    },
                ) {
                    Text(text = stringResource(R.string.jobcard_save))
                }
            },
            dismissButton = {
                TextButton(onClick = { datePickerOpen = false }) {
                    Text(text = stringResource(R.string.jobcard_cancel))
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddItemDialog(
    priceItems: List<PriceItemEntity>,
    onAddFromPrice: (PriceItemEntity, Int) -> Unit,
    onAddManual: (title: String, qty: Int, priceKopecks: Long) -> Unit,
    onDismiss: () -> Unit,
) {
    var priceDropdownOpen by remember { mutableStateOf(false) }
    var selectedPrice by remember { mutableStateOf<PriceItemEntity?>(priceItems.firstOrNull()) }
    var title by rememberSaveable { mutableStateOf("") }
    var qtyText by rememberSaveable { mutableStateOf("1") }
    var priceText by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.jobcard_item_dialog_title)) },
        text = {
            Column {
                if (priceItems.isNotEmpty()) {
                    ExposedDropdownMenuBox(
                        expanded = priceDropdownOpen,
                        onExpandedChange = { priceDropdownOpen = it },
                    ) {
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            value = selectedPrice?.title ?: stringResource(R.string.jobcard_item_manual),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(text = stringResource(R.string.jobcard_item_from_price)) },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = priceDropdownOpen)
                            },
                        )
                        ExposedDropdownMenu(
                            expanded = priceDropdownOpen,
                            onDismissRequest = { priceDropdownOpen = false },
                        ) {
                            androidx.compose.material3.DropdownMenuItem(
                                text = { Text(text = stringResource(R.string.jobcard_item_manual)) },
                                onClick = {
                                    selectedPrice = null
                                    priceDropdownOpen = false
                                },
                            )
                            priceItems.forEach { item ->
                                androidx.compose.material3.DropdownMenuItem(
                                    text = { Text("${item.title} — ${item.price.formatKopecks()}") },
                                    onClick = {
                                        selectedPrice = item
                                        priceDropdownOpen = false
                                    },
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    value = selectedPrice?.title ?: title,
                    onValueChange = { title = it },
                    readOnly = selectedPrice != null,
                    label = { Text(text = stringResource(R.string.jobs_create_title_hint)) },
                    singleLine = true,
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = qtyText,
                        onValueChange = { qtyText = it.filter(Char::isDigit).take(4) },
                        label = { Text(text = stringResource(R.string.jobcard_item_qty_hint)) },
                        singleLine = true,
                    )
                    OutlinedTextField(
                        modifier = Modifier.weight(2f),
                        value = selectedPrice?.let { (it.price / 100.0).let { rub -> if (rub % 1 == 0.0) rub.toLong().toString() else rub.toString() } }
                            ?: priceText,
                        onValueChange = { priceText = it },
                        readOnly = selectedPrice != null,
                        label = { Text(text = stringResource(R.string.jobcard_item_price_hint)) },
                        singleLine = true,
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val qty = qtyText.toIntOrNull() ?: 0
                    val selected = selectedPrice
                    when {
                        selected != null -> onAddFromPrice(selected, qty)
                        else -> {
                            val priceKopecks = parseMoneyToKopecks(priceText) ?: 0
                            onAddManual(title, qty, priceKopecks)
                        }
                    }
                },
            ) {
                Text(text = stringResource(R.string.jobcard_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.jobcard_cancel))
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddPaymentDialog(
    debtKopecks: Long,
    onAdd: (amountKopecks: Long, method: PaymentMethod) -> Unit,
    onDismiss: () -> Unit,
) {
    var amountText by rememberSaveable { mutableStateOf("") }
    var method by rememberSaveable { mutableStateOf(PaymentMethod.CASH) }
    var methodDropdownOpen by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.jobcard_payment_dialog_title)) },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.jobcard_debt, debtKopecks.formatKopecks()),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text(text = stringResource(R.string.jobcard_payment_amount_hint)) },
                    singleLine = true,
                )
                ExposedDropdownMenuBox(
                    modifier = Modifier.padding(top = 8.dp),
                    expanded = methodDropdownOpen,
                    onExpandedChange = { methodDropdownOpen = it },
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        value = stringResource(paymentMethodLabelRes(method)),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(text = stringResource(R.string.jobcard_payment_method_label)) },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = methodDropdownOpen)
                        },
                    )
                    ExposedDropdownMenu(
                        expanded = methodDropdownOpen,
                        onDismissRequest = { methodDropdownOpen = false },
                    ) {
                        PaymentMethod.entries.forEach { option ->
                            androidx.compose.material3.DropdownMenuItem(
                                text = { Text(text = stringResource(paymentMethodLabelRes(option))) },
                                onClick = {
                                    method = option
                                    methodDropdownOpen = false
                                },
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    parseMoneyToKopecks(amountText)?.let { onAdd(it, method) }
                },
                // пустой/невалидный ввод — кнопка неактивна
                enabled = parseMoneyToKopecks(amountText) != null,
            ) {
                Text(text = stringResource(R.string.jobcard_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.jobcard_cancel))
            }
        },
    )
}
