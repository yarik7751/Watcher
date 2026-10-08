package com.yarik.watcher.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "jobs",
    foreignKeys = [
        ForeignKey(
            entity = ClientEntity::class,
            parentColumns = ["id"],
            childColumns = ["clientId"],
            // Клиента с историей заявок не удаляем молча — удаление в репозитории запрещено при наличии заявок
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [Index("clientId"), Index("status"), Index("scheduledAt")],
)
data class JobEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clientId: Long,
    val title: String,
    val address: String = "",
    val status: JobStatus = JobStatus.NEW,
    /** Epoch millis, плановая дата визита */
    val scheduledAt: Long? = null,
    /** Epoch millis, проставляется кнопкой «Завершить» */
    val finishedAt: Long? = null,
    /** Сумма сметы в копейках, пересчитывается репозиторием по позициям */
    val totalAmount: Long = 0,
    /** Сумма оплат в копейках, пересчитывается репозиторием по платежам */
    val paidAmount: Long = 0,
    val notes: String = "",
)
