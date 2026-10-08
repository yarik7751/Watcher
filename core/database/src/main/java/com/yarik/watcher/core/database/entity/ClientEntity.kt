package com.yarik.watcher.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "clients",
    indices = [Index("name")],
)
data class ClientEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String = "",
    val address: String = "",
    val notes: String = "",
    /** Epoch millis */
    val createdAt: Long,
    /** Epoch millis, обновляется при завершении заявки; null — заявок не было */
    val lastVisitAt: Long? = null,
)
