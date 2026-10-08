package com.yarik.watcher.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.yarik.watcher.core.database.entity.JobEntity
import com.yarik.watcher.core.database.entity.JobStatus
import com.yarik.watcher.core.database.entity.JobWithClient
import kotlinx.coroutines.flow.Flow

@Dao
interface JobDao {

    /** Список заявок вкладки; сначала запланированные с датой, по убыванию даты */
    @Query(
        "SELECT * FROM jobs WHERE status = :status " +
            "ORDER BY scheduledAt IS NULL, scheduledAt DESC, id DESC",
    )
    fun observeByStatus(status: JobStatus): Flow<List<JobEntity>>

    /** То же, с именем клиента для отображения в списке (один JOIN вместо N+1) */
    @Query(
        "SELECT jobs.*, clients.name AS clientName FROM jobs " +
            "INNER JOIN clients ON clients.id = jobs.clientId " +
            "WHERE jobs.status = :status " +
            "ORDER BY jobs.scheduledAt IS NULL, jobs.scheduledAt DESC, jobs.id DESC",
    )
    fun observeByStatusWithClient(status: JobStatus): Flow<List<JobWithClient>>

    /** История заявок клиента, новые сверху */
    @Query("SELECT * FROM jobs WHERE clientId = :clientId ORDER BY id DESC")
    fun observeByClient(clientId: Long): Flow<List<JobEntity>>

    /** История заявок клиента с именем клиента (для карточки клиента) */
    @Query(
        "SELECT jobs.*, clients.name AS clientName FROM jobs " +
            "INNER JOIN clients ON clients.id = jobs.clientId " +
            "WHERE jobs.clientId = :clientId ORDER BY jobs.id DESC",
    )
    fun observeByClientWithClient(clientId: Long): Flow<List<JobWithClient>>

    @Query("SELECT COUNT(*) FROM jobs WHERE clientId = :clientId")
    suspend fun countByClient(clientId: Long): Int

    @Query("SELECT * FROM jobs WHERE id = :id")
    fun observeById(id: Long): Flow<JobEntity?>

    @Query("SELECT * FROM jobs WHERE id = :id")
    suspend fun getById(id: Long): JobEntity?

    /** Полный снимок таблицы для экспорта CSV */
    @Query("SELECT * FROM jobs ORDER BY id")
    suspend fun getAll(): List<JobEntity>

    @Insert
    suspend fun insert(job: JobEntity): Long

    @Update
    suspend fun update(job: JobEntity)

    @Delete
    suspend fun delete(job: JobEntity)

    /** «Завершить»: DONE + finishedAt одним апдейтом */
    @Query("UPDATE jobs SET status = 'DONE', finishedAt = :finishedAt WHERE id = :jobId")
    suspend fun markFinished(jobId: Long, finishedAt: Long)

    /** Кол-во завершённых заявок в [from, to) */
    @Query("SELECT COUNT(*) FROM jobs WHERE status = 'DONE' AND finishedAt >= :from AND finishedAt < :to")
    fun observeFinishedCountInPeriod(from: Long, to: Long): Flow<Int>

    /** Сумма смет завершённых заявок в [from, to) — для среднего чека */
    @Query(
        "SELECT COALESCE(SUM(totalAmount), 0) FROM jobs " +
            "WHERE status = 'DONE' AND finishedAt >= :from AND finishedAt < :to",
    )
    fun observeFinishedTotalInPeriod(from: Long, to: Long): Flow<Long>
}
