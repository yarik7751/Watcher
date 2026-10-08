package com.yarik.watcher.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.yarik.watcher.core.database.entity.JobEntity
import com.yarik.watcher.core.database.entity.JobStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface JobDao {

    /** Список заявок вкладки; сначала запланированные с датой, по убыванию даты */
    @Query(
        "SELECT * FROM jobs WHERE status = :status " +
            "ORDER BY scheduledAt IS NULL, scheduledAt DESC, id DESC",
    )
    fun observeByStatus(status: JobStatus): Flow<List<JobEntity>>

    /** История заявок клиента, новые сверху */
    @Query("SELECT * FROM jobs WHERE clientId = :clientId ORDER BY id DESC")
    fun observeByClient(clientId: Long): Flow<List<JobEntity>>

    @Query("SELECT * FROM jobs WHERE id = :id")
    fun observeById(id: Long): Flow<JobEntity?>

    @Query("SELECT * FROM jobs WHERE id = :id")
    suspend fun getById(id: Long): JobEntity?

    @Insert
    suspend fun insert(job: JobEntity): Long

    @Update
    suspend fun update(job: JobEntity)

    @Delete
    suspend fun delete(job: JobEntity)

    /** «Завершить»: DONE + finishedAt одним апдейтом */
    @Query("UPDATE jobs SET status = 'DONE', finishedAt = :finishedAt WHERE id = :jobId")
    suspend fun markFinished(jobId: Long, finishedAt: Long)
}
