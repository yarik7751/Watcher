package com.yarik.watcher.feature.masterpro.impl.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.yarik.watcher.core.database.MasterProDatabase
import com.yarik.watcher.core.database.entity.JobEntity
import com.yarik.watcher.core.database.entity.JobStatus
import com.yarik.watcher.feature.masterpro.impl.R

/**
 * Показывает локальное напоминание о запланированном визите.
 *
 * Worker-инфраструктура проекта пока пустой каркас (WorkerModule без фабрики),
 * поэтому БД поднимается здесь напрямую — короткоживущий инстанс Room к тому же
 * файлу; инстанс закрывается в finally. Когда появится Dagger-фабрика воркеров,
 * этот код сводится к инъекции DAO.
 */
class JobReminderWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val jobId = inputData.getLong(KEY_JOB_ID, 0L)
        if (jobId <= 0) return Result.failure()

        // Короткоживущий Room-инстанс к тому же файлу БД; закрывается в finally
        val db = Room.databaseBuilder(
            applicationContext,
            MasterProDatabase::class.java,
            MasterProDatabase.NAME,
        ).build()

        return try {
            val job = db.jobDao().getById(jobId)
            // Заявку удалили, завершили или сняли дату — напоминать не о чем
            if (job == null || job.status == JobStatus.DONE || job.scheduledAt == null) {
                Result.success()
            } else {
                val clientName = db.clientDao().getById(job.clientId)?.name.orEmpty()
                showNotification(job, clientName)
                Result.success()
            }
        } finally {
            db.close()
        }
    }

    private fun showNotification(job: JobEntity, clientName: String) {
        val context = applicationContext
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.notif_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = context.getString(R.string.notif_channel_desc)
        }
        nm.createNotificationChannel(channel)

        val launchIntent = context.packageManager
            .getLaunchIntentForPackage(context.packageName)
            ?.apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK) }
        val pendingIntent = PendingIntent.getActivity(
            context,
            job.id.toInt(),
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val text = if (clientName.isNotBlank()) "$clientName — ${job.title}" else job.title
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            // Отдельной иконки у фичи нет; общесистемная — консервативный вариант
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(context.getString(R.string.notif_reminder_title))
            .setContentText(text)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        nm.notify(job.id.toInt(), notification)
    }

    companion object {
        const val KEY_JOB_ID = "job_id"
        private const val CHANNEL_ID = "job_reminders"
    }
}
