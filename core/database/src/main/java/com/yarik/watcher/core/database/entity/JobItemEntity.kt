package com.yarik.watcher.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "job_items",
    foreignKeys = [
        ForeignKey(
            entity = JobEntity::class,
            parentColumns = ["id"],
            childColumns = ["jobId"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = PriceItemEntity::class,
            parentColumns = ["id"],
            childColumns = ["priceItemId"],
            // Позиция из прайса копируется в title/price; удаление прайс-элемента строку сметы не трогает
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [Index("jobId"), Index("priceItemId")],
)
data class JobItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val jobId: Long,
    /** Ссылка на прайс; null — позиция введена вручную */
    val priceItemId: Long? = null,
    val title: String,
    val qty: Int = 1,
    /** Цена за единицу в копейках */
    val price: Long,
)
