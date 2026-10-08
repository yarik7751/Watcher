package com.yarik.watcher.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.yarik.watcher.core.database.entity.PaymentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PaymentDao {

    @Query("SELECT * FROM payments WHERE jobId = :jobId ORDER BY paidAt DESC, id DESC")
    fun observeByJob(jobId: Long): Flow<List<PaymentEntity>>

    /** Сумма оплат по заявке в копейках */
    @Query("SELECT COALESCE(SUM(amount), 0) FROM payments WHERE jobId = :jobId")
    fun observeSumByJob(jobId: Long): Flow<Long>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM payments WHERE jobId = :jobId")
    suspend fun sumByJob(jobId: Long): Long

    @Insert
    suspend fun insert(payment: PaymentEntity): Long

    @Delete
    suspend fun delete(payment: PaymentEntity)
}
