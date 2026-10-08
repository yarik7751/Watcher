package com.yarik.watcher.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.yarik.watcher.core.database.dao.ClientDao
import com.yarik.watcher.core.database.dao.JobDao
import com.yarik.watcher.core.database.dao.JobItemDao
import com.yarik.watcher.core.database.dao.JobPhotoDao
import com.yarik.watcher.core.database.dao.PaymentDao
import com.yarik.watcher.core.database.dao.PriceItemDao
import com.yarik.watcher.core.database.entity.ClientEntity
import com.yarik.watcher.core.database.entity.JobEntity
import com.yarik.watcher.core.database.entity.JobItemEntity
import com.yarik.watcher.core.database.entity.JobPhotoEntity
import com.yarik.watcher.core.database.entity.PaymentEntity
import com.yarik.watcher.core.database.entity.PriceItemEntity

@Database(
    entities = [
        ClientEntity::class,
        JobEntity::class,
        PriceItemEntity::class,
        JobItemEntity::class,
        PaymentEntity::class,
        JobPhotoEntity::class,
    ],
    version = 1,
    // Схему пока не экспортируем; при первой миграции включим exportSchema и заведём schemaDirectory
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class MasterProDatabase : RoomDatabase() {

    abstract fun clientDao(): ClientDao

    abstract fun jobDao(): JobDao

    abstract fun priceItemDao(): PriceItemDao

    abstract fun jobItemDao(): JobItemDao

    abstract fun paymentDao(): PaymentDao

    abstract fun jobPhotoDao(): JobPhotoDao

    companion object {
        const val NAME = "masterpro.db"
    }
}
