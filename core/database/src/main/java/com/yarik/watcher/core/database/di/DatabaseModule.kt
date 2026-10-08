package com.yarik.watcher.core.database.di

import android.content.Context
import androidx.room.Room
import com.yarik.watcher.core.database.MasterProDatabase
import com.yarik.watcher.core.database.dao.ClientDao
import com.yarik.watcher.core.database.dao.JobDao
import com.yarik.watcher.core.database.dao.JobItemDao
import com.yarik.watcher.core.database.dao.JobPhotoDao
import com.yarik.watcher.core.database.dao.PaymentDao
import com.yarik.watcher.core.database.dao.PriceItemDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/** Паттерн как в :app (DataModule/PreferencesModule): обычный @Module с @Provides. */
@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(context: Context): MasterProDatabase =
        Room.databaseBuilder(context, MasterProDatabase::class.java, MasterProDatabase.NAME)
            // fallbackToDestructiveMigration намеренно НЕ используем: это учёт денег, молчаливо
            // терять данные недопустимо. При bump версии напишем явную миграцию.
            .build()

    @Provides
    fun provideClientDao(db: MasterProDatabase): ClientDao = db.clientDao()

    @Provides
    fun provideJobDao(db: MasterProDatabase): JobDao = db.jobDao()

    @Provides
    fun providePriceItemDao(db: MasterProDatabase): PriceItemDao = db.priceItemDao()

    @Provides
    fun provideJobItemDao(db: MasterProDatabase): JobItemDao = db.jobItemDao()

    @Provides
    fun providePaymentDao(db: MasterProDatabase): PaymentDao = db.paymentDao()

    @Provides
    fun provideJobPhotoDao(db: MasterProDatabase): JobPhotoDao = db.jobPhotoDao()
}
