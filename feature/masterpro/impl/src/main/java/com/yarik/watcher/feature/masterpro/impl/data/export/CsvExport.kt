package com.yarik.watcher.feature.masterpro.impl.data.export

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.yarik.watcher.core.database.MasterProDatabase
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

/**
 * Экспорт базы в CSV → Downloads/MasterPRO.
 *
 * API 29+ — через MediaStore (scoped storage, разрешение не нужно);
 * API 26–28 — прямой записью в публичную папку (WRITE_EXTERNAL_STORAGE с maxSdk 28 в манифесте).
 *
 * Деньги экспортируются в рублях с точкой («1234.50») — удобно для Excel/Sheets.
 */
class CsvExport @Inject constructor(
    private val context: Context,
    private val db: MasterProDatabase,
) {

    /** @return число записанных файлов */
    suspend fun exportAll(): Result<Int> = withContext(Dispatchers.IO) {
        runCatching {
            val stamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val files = listOf(
                "masterpro_clients_$stamp.csv" to buildClientsCsv(),
                "masterpro_jobs_$stamp.csv" to buildJobsCsv(),
                "masterpro_payments_$stamp.csv" to buildPaymentsCsv(),
                "masterpro_price_$stamp.csv" to buildPriceCsv(),
            )
            files.forEach { (name, content) -> writeToDownloads(name, content) }
            files.size
        }
    }

    private suspend fun buildClientsCsv(): String = buildString {
        appendLine("id;name;phone;address;notes;created_at;last_visit_at")
        db.clientDao().observeAll().first().forEach { c ->
            appendLine(
                listOf(
                    c.id.toString(),
                    c.name.escapeCsv(),
                    c.phone.escapeCsv(),
                    c.address.escapeCsv(),
                    c.notes.escapeCsv(),
                    c.createdAt.toString(),
                    c.lastVisitAt?.toString() ?: "",
                ).joinToString(";"),
            )
        }
    }

    private suspend fun buildJobsCsv(): String = buildString {
        appendLine("id;client_id;title;address;status;scheduled_at;finished_at;total_rub;paid_rub;notes")
        db.jobDao().getAll().forEach { j ->
            appendLine(
                listOf(
                    j.id.toString(),
                    j.clientId.toString(),
                    j.title.escapeCsv(),
                    j.address.escapeCsv(),
                    j.status.name,
                    j.scheduledAt?.toString() ?: "",
                    j.finishedAt?.toString() ?: "",
                    j.totalAmount.toRubles(),
                    j.paidAmount.toRubles(),
                    j.notes.escapeCsv(),
                ).joinToString(";"),
            )
        }
    }

    private suspend fun buildPaymentsCsv(): String = buildString {
        appendLine("id;job_id;amount_rub;method;paid_at")
        db.paymentDao().getAll().forEach { p ->
            appendLine(
                listOf(
                    p.id.toString(),
                    p.jobId.toString(),
                    p.amount.toRubles(),
                    p.method.name,
                    p.paidAt.toString(),
                ).joinToString(";"),
            )
        }
    }

    private suspend fun buildPriceCsv(): String = buildString {
        appendLine("id;title;price_rub")
        db.priceItemDao().observeAll().first().forEach { item ->
            appendLine(
                listOf(item.id.toString(), item.title.escapeCsv(), item.price.toRubles()).joinToString(";"),
            )
        }
    }

    private fun writeToDownloads(fileName: String, content: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "text/csv")
                put(MediaStore.Downloads.RELATIVE_PATH, "${Environment.DIRECTORY_DOWNLOADS}/MasterPRO")
            }
            val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                ?: throw IOException("MediaStore insert failed for $fileName")
            context.contentResolver.openOutputStream(uri)?.use { out ->
                out.write(content.toByteArray(Charsets.UTF_8))
            } ?: throw IOException("MediaStore openOutputStream failed for $fileName")
        } else {
            @Suppress("DEPRECATION")
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            File(dir, fileName).writeText(content, Charsets.UTF_8)
        }
    }

    /** Точка с запятой как разделитель — в русском Excel открывается по колонкам сразу */
    private fun String.escapeCsv(): String =
        if (any { it == ';' || it == '"' || it == '\n' || it == '\r' }) {
            "\"${replace("\"", "\"\"")}\""
        } else {
            this
        }

    private fun Long.toRubles(): String = String.format(Locale.US, "%.2f", this / 100.0)
}
