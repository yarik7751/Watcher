package com.yarik.watcher.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.yarik.watcher.core.database.entity.PriceItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PriceItemDao {

    @Query("SELECT * FROM price_items ORDER BY title COLLATE NOCASE")
    fun observeAll(): Flow<List<PriceItemEntity>>

    @Insert
    suspend fun insert(item: PriceItemEntity): Long

    @Update
    suspend fun update(item: PriceItemEntity)

    @Delete
    suspend fun delete(item: PriceItemEntity)
}
