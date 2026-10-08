package com.yarik.watcher.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.yarik.watcher.core.database.entity.ClientEntity
import com.yarik.watcher.core.database.entity.ClientWithDebt
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {

    @Query("SELECT * FROM clients ORDER BY name COLLATE NOCASE")
    fun observeAll(): Flow<List<ClientEntity>>

    /** Список клиентов с долгом по активным заявкам (один запрос вместо N+1) */
    @Query(
        "SELECT clients.*, " +
            "COALESCE(SUM(CASE WHEN jobs.status != 'DONE' " +
            "THEN MAX(jobs.totalAmount - jobs.paidAmount, 0) ELSE 0 END), 0) AS debt " +
            "FROM clients LEFT JOIN jobs ON jobs.clientId = clients.id " +
            "GROUP BY clients.id ORDER BY clients.name COLLATE NOCASE",
    )
    fun observeAllWithDebt(): Flow<List<ClientWithDebt>>

    /** Поиск с долгом. Запрос должен уже содержать % с двух сторон. */
    @Query(
        "SELECT clients.*, " +
            "COALESCE(SUM(CASE WHEN jobs.status != 'DONE' " +
            "THEN MAX(jobs.totalAmount - jobs.paidAmount, 0) ELSE 0 END), 0) AS debt " +
            "FROM clients LEFT JOIN jobs ON jobs.clientId = clients.id " +
            "WHERE clients.name LIKE :query OR clients.phone LIKE :query OR clients.address LIKE :query " +
            "GROUP BY clients.id ORDER BY clients.name COLLATE NOCASE",
    )
    fun searchWithDebt(query: String): Flow<List<ClientWithDebt>>

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
