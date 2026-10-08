package com.yarik.watcher.feature.masterpro.impl.notifications

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Планирование напоминаний о визитах через WorkManager (unique work на заявку —
 * повторное сохранение заявки просто перезаписывает напоминание).
 */
class JobReminderScheduler @Inject constructor(
    private val context: Context,
) {

    /** [scheduledAt] == null или в прошлом — существующее напоминание отменяется */
    fun schedule(jobId: Long, scheduledAt: Long?) {
        val workManager = WorkManager.getInstance(context)
        val name = workName(jobId)
        val delayMs = scheduledAt?.minus(System.currentTimeMillis()) ?: -1
        if (delayMs <= 0) {
            workManager.cancelUniqueWork(name)
            return
        }
        val request = OneTimeWorkRequestBuilder<JobReminderWorker>()
            .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
            .setInputData(workDataOf(JobReminderWorker.KEY_JOB_ID to jobId))
            .build()
        workManager.enqueueUniqueWork(name, ExistingWorkPolicy.REPLACE, request)
    }

    fun cancel(jobId: Long) {
        WorkManager.getInstance(context).cancelUniqueWork(workName(jobId))
    }

    private fun workName(jobId: Long) = "job_reminder_$jobId"
}
