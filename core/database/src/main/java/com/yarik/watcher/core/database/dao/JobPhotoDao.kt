package com.yarik.watcher.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.yarik.watcher.core.database.entity.JobPhotoEntity
import com.yarik.watcher.core.database.entity.PhotoKind
import kotlinx.coroutines.flow.Flow

@Dao
interface JobPhotoDao {

    @Query("SELECT * FROM job_photos WHERE jobId = :jobId AND kind = :kind ORDER BY id")
    fun observeByJobAndKind(jobId: Long, kind: PhotoKind): Flow<List<JobPhotoEntity>>

    @Query("SELECT * FROM job_photos WHERE jobId = :jobId ORDER BY id")
    fun observeByJob(jobId: Long): Flow<List<JobPhotoEntity>>

    @Insert
    suspend fun insert(photo: JobPhotoEntity): Long

    @Delete
    suspend fun delete(photo: JobPhotoEntity)
}
