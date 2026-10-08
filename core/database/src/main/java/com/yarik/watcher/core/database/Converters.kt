package com.yarik.watcher.core.database

import androidx.room.TypeConverter
import com.yarik.watcher.core.database.entity.JobStatus
import com.yarik.watcher.core.database.entity.PaymentMethod
import com.yarik.watcher.core.database.entity.PhotoKind

/** Enum'ы храним в БД строкой по имени — читаемо в экспортированном CSV и без хрупких ordinal-значений. */
class Converters {

    @TypeConverter
    fun jobStatusToString(value: JobStatus): String = value.name

    @TypeConverter
    fun stringToJobStatus(value: String): JobStatus = JobStatus.valueOf(value)

    @TypeConverter
    fun paymentMethodToString(value: PaymentMethod): String = value.name

    @TypeConverter
    fun stringToPaymentMethod(value: String): PaymentMethod = PaymentMethod.valueOf(value)

    @TypeConverter
    fun photoKindToString(value: PhotoKind): String = value.name

    @TypeConverter
    fun stringToPhotoKind(value: String): PhotoKind = PhotoKind.valueOf(value)
}
