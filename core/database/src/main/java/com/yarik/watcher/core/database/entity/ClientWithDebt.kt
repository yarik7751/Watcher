package com.yarik.watcher.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded

/** Клиент + суммарный долг по активным (не DONE) заявкам, одним запросом. */
data class ClientWithDebt(
    @Embedded val client: ClientEntity,
    @ColumnInfo(name = "debt") val debt: Long,
)
