package com.yarik.watcher.feature.masterpro.impl.data.model

import com.yarik.watcher.core.database.entity.JobEntity
import com.yarik.watcher.core.database.entity.JobItemEntity
import com.yarik.watcher.core.database.entity.JobPhotoEntity
import com.yarik.watcher.core.database.entity.PaymentEntity

/** Полный состав карточки заявки: заявка + смета + оплаты + фото. */
data class JobDetails(
    val job: JobEntity,
    val items: List<JobItemEntity>,
    val payments: List<PaymentEntity>,
    val photosBefore: List<JobPhotoEntity>,
    val photosAfter: List<JobPhotoEntity>,
) {

    /** Остаток долга в копейках */
    val debt: Long
        get() = (job.totalAmount - job.paidAmount).coerceAtLeast(0)

    val isPaid: Boolean
        get() = debt == 0L && job.totalAmount > 0
}
