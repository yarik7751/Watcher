package com.yarik.watcher.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.yarik.watcher.core.database.entity.JobItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JobItemDao {

    @Query("SELECT * FROM job_items WHERE jobId = :jobId ORDER BY id")
    fun observeByJob(jobId: Long): Flow<List<JobItemEntity>>

    /** Сумма сметы заявки в копейках: Σ qty × price */
    @Query("SELECT COALESCE(SUM(qty * price), 0) FROM job_items WHERE jobId = :jobId")
    fun observeSumByJob(jobId: Long): Flow<Long>

    @Query("SELECT COALESCE(SUM(qty * price), 0) FROM job_items WHERE jobId = :jobId")
    suspend fun sumByJob(jobId: Long): Long

    @Insert
    suspend fun insert(item: JobItemEntity): Long

    @Update
    suspend fun update(item: JobItemEntity)

    @Delete
    suspend fun delete(item: JobItemEntity)
}
