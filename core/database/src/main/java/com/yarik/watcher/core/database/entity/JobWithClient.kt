package com.yarik.watcher.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded

/** Проекция JOIN jobs × clients: заявка + имя клиента для списков. */
data class JobWithClient(
    @Embedded val job: JobEntity,
    @ColumnInfo(name = "clientName") val clientName: String,
) {

    /** Остаток долга по заявке в копейках, не меньше нуля */
    val debt: Long
        get() = (job.totalAmount - job.paidAmount).coerceAtLeast(0)
}
