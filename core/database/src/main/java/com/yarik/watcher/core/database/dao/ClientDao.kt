package com.yarik.watcher.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.yarik.watcher.core.database.entity.ClientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {

    @Query("SELECT * FROM clients ORDER BY name COLLATE NOCASE")
    fun observeAll(): Flow<List<ClientEntity>>

    /** Поиск по имени / телефону / адресу. Запрос должен уже содержать % с двух сторон. */
    @Query(
        "SELECT * FROM clients " +
            "WHERE name LIKE :query OR phone LIKE :query OR address LIKE :query " +
            "ORDER BY name COLLATE NOCASE",
    )
    fun search(query: String): Flow<List<ClientEntity>>

    @Query("SELECT * FROM clients WHERE id = :id")
    fun observeById(id: Long): Flow<ClientEntity?>

    @Query("SELECT * FROM clients WHERE id = :id")
    suspend fun getById(id: Long): ClientEntity?

    @Insert
    suspend fun insert(client: ClientEntity): Long

    @Update
    suspend fun update(client: ClientEntity)

    @Delete
    suspend fun delete(client: ClientEntity)
}
