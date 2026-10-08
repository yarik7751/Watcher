package com.yarik.watcher.feature.masterpro.impl.jobcard

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import com.yarik.watcher.core.database.entity.PhotoKind
import java.io.File
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Хранение фото заявок в app-private storage ([Context.getFilesDir]/photos).
 *
 * Съёмка через камеру идёт во временный файл в cacheDir/captures (нужен content:// —
 * FileProvider), затем [savePhoto] декодирует с inSampleSize, ужимает длинную сторону
 * до [MAX_SIDE_PX] и пишет JPEG в постоянное место; временный capture-файл удаляется.
 *
 * Одновременно может быть только один pending capture (съёмка — последовательный
 * UI-флоу), поэтому временный файл отслеживается простым полем.
 */
class PhotoStorage @Inject constructor(
    private val context: Context,
) {

    private var pendingCapture: File? = null

    /** URI временного файла для контракта TakePicture. */
    fun createCaptureUri(jobId: Long, kind: PhotoKind): Uri {
        val dir = File(context.cacheDir, "captures").apply { mkdirs() }
        val file = File(dir, "capture_${jobId}_${kind.name}_${System.currentTimeMillis()}.jpg")
        pendingCapture = file
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    /** Сжать [source] до ~1024px и сохранить; вернуть абсолютный путь постоянного файла. */
    suspend fun savePhoto(source: Uri, jobId: Long, kind: PhotoKind): String =
        withContext(Dispatchers.IO) {
            val bitmap = decodeScaled(source)
                ?: throw IOException("cannot decode $source")
            val dir = File(context.filesDir, "photos/job_$jobId").apply { mkdirs() }
            val file = File(dir, "${kind.name.lowercase()}_${System.currentTimeMillis()}.jpg")
            file.outputStream().use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)
            }
            // Одноразовый capture-файл (если source — это он) больше не нужен
            val capture = pendingCapture
            pendingCapture = null
            capture?.delete()
            file.absolutePath
        }

    fun deletePhoto(filePath: String) {
        File(filePath).delete()
    }

    private fun decodeScaled(uri: Uri): Bitmap? {
        val resolver = context.contentResolver

        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolver.openInputStream(uri)?.use { input ->
            BitmapFactory.decodeStream(input, null, bounds)
        }
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

        var sample = 1
        val maxDim = maxOf(bounds.outWidth, bounds.outHeight)
        while (maxDim / sample > MAX_SIDE_PX) sample *= 2

        val opts = BitmapFactory.Options().apply { inSampleSize = sample }
        val sampled = resolver.openInputStream(uri)?.use { input ->
            BitmapFactory.decodeStream(input, null, opts)
        } ?: return null

        // Точная доводка длинной стороны до 1024 (inSampleSize даёт лишь степени двойки)
        val scale = MAX_SIDE_PX.toFloat() / maxOf(sampled.width, sampled.height)
        if (scale >= 1f) return sampled
        val w = (sampled.width * scale).toInt().coerceAtLeast(1)
        val h = (sampled.height * scale).toInt().coerceAtLeast(1)
        return Bitmap.createScaledBitmap(sampled, w, h, true)
    }

    private companion object {
        const val MAX_SIDE_PX = 1024
        const val JPEG_QUALITY = 85
    }
}
