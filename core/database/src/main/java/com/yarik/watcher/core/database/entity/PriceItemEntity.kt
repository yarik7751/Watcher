package com.yarik.watcher.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Типовая работа из прайс-листа мастера. */
@Entity(tableName = "price_items")
data class PriceItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    /** Цена в копейках */
    val price: Long,
)
