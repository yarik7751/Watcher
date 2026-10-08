package com.yarik.watcher.feature.masterpro.impl.data

import androidx.room.withTransaction
import com.yarik.watcher.core.database.MasterProDatabase
import com.yarik.watcher.core.database.dao.ClientDao
import com.yarik.watcher.core.database.dao.JobDao
import com.yarik.watcher.core.database.dao.JobItemDao
import com.yarik.watcher.core.database.dao.JobPhotoDao
import com.yarik.watcher.core.database.dao.PaymentDao
import com.yarik.watcher.core.database.dao.PriceItemDao
import com.yarik.watcher.core.database.entity.ClientEntity
import com.yarik.watcher.core.database.entity.ClientWithDebt
import com.yarik.watcher.core.database.entity.JobEntity
import com.yarik.watcher.core.database.entity.JobItemEntity
import com.yarik.watcher.core.database.entity.JobPhotoEntity
import com.yarik.watcher.core.database.entity.JobStatus
import com.yarik.watcher.core.database.entity.JobWithClient
import com.yarik.watcher.core.database.entity.PaymentEntity
import com.yarik.watcher.core.database.entity.PaymentMethod
import com.yarik.watcher.core.database.entity.PhotoKind
import com.yarik.watcher.core.database.entity.PriceItemEntity
import com.yarik.watcher.feature.masterpro.impl.data.model.JobDetails
import com.yarik.watcher.feature.masterpro.impl.data.model.PeriodStats
import com.yarik.watcher.feature.masterpro.impl.notifications.JobReminderScheduler
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/**
 * Единая точка доступа к данным «МастерPRO».
 *
 * Правила валидации (согласно ТЗ):
 * - клиент без имени не сохраняется;
 * - оплата не больше остатка долга;
 * - «Завершить» ставит finishedAt и обновляет lastVisitAt клиента — атомарно.
 *
 * Ошибки валидации — Result.failure с IllegalArgumentException, у которого message —
 * машиночитаемый код ("empty name", "amount exceeds debt" и т.п.). Пользовательский текст
 * формирует UI-слой (strings.xml), репозиторий тексты не хранит.
 */
@Singleton
class MasterProRepository @Inject constructor(
    private val db: MasterProDatabase,
    private val clientDao: ClientDao,
    private val jobDao: JobDao,
    private val priceItemDao: PriceItemDao,
    private val jobItemDao: JobItemDao,
    private val paymentDao: PaymentDao,
    private val jobPhotoDao: JobPhotoDao,
    private val reminderScheduler: JobReminderScheduler,
) {

    // region Клиенты

    fun observeClients(): Flow<List<ClientEntity>> = clientDao.observeAll()

    /** Список клиентов с бейджем долга для экрана C */
    fun observeClientsWithDebt(): Flow<List<ClientWithDebt>> = clientDao.observeAllWithDebt()

    fun searchClientsWithDebt(query: String): Flow<List<ClientWithDebt>> =
        clientDao.searchWithDebt("%${query.trim()}%")

    fun searchClients(query: String): Flow<List<ClientEntity>> =
        clientDao.search("%${query.trim()}%")

    fun observeClient(clientId: Long): Flow<ClientEntity?> = clientDao.observeById(clientId)

    /** Общий долг клиента в копейках: Σ незакрытого по активным (не DONE) заявкам */
    fun observeClientDebt(clientId: Long): Flow<Long> =
        jobDao.observeByClient(clientId).map { jobs ->
            jobs.filter { it.status != JobStatus.DONE }
                .sumOf { (it.totalAmount - it.paidAmount).coerceAtLeast(0) }
        }

    suspend fun createClient(
        name: String,
        phone: String,
        address: String,
        notes: String,
    ): Result<Long> {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) {
            return Result.failure(IllegalArgumentException("empty name"))
        }
        return runCatching {
            clientDao.insert(
                ClientEntity(
                    name = trimmedName,
                    phone = phone.trim(),
                    address = address.trim(),
                    notes = notes.trim(),
                    createdAt = System.currentTimeMillis(),
                ),
            )
        }
    }

    suspend fun updateClient(client: ClientEntity): Result<Unit> {
        if (client.name.isBlank()) {
            return Result.failure(IllegalArgumentException("empty name"))
        }
        return runCatching { clientDao.update(client.copy(name = client.name.trim())) }
    }

    /** Обновление полей карточки: существующая запись подгружается, чтобы не терять createdAt/lastVisitAt */
    suspend fun updateClient(
        clientId: Long,
        name: String,
        phone: String,
        address: String,
        notes: String,
    ): Result<Unit> {
        if (name.isBlank()) {
            return Result.failure(IllegalArgumentException("empty name"))
        }
        val existing = clientDao.getById(clientId)
            ?: return Result.failure(IllegalArgumentException("client not found"))
        return runCatching {
            clientDao.update(
                existing.copy(
                    name = name.trim(),
                    phone = phone.trim(),
                    address = address.trim(),
                    notes = notes.trim(),
                ),
            )
        }
    }

    /** Клиента с заявками не удаляем — защита над FK RESTRICT */
    suspend fun deleteClient(clientId: Long): Result<Unit> {
        if (jobDao.countByClient(clientId) > 0) {
            return Result.failure(IllegalArgumentException("client has jobs"))
        }
        val client = clientDao.getById(clientId)
            ?: return Result.failure(IllegalArgumentException("client not found"))
        return runCatching { clientDao.delete(client) }
    }

    // endregion

    // region Заявки

    fun observeJobsByStatus(status: JobStatus): Flow<List<JobWithClient>> =
        jobDao.observeByStatusWithClient(status)

    fun observeJobsByClient(clientId: Long): Flow<List<JobWithClient>> =
        jobDao.observeByClientWithClient(clientId)

    /** Карточка заявки: заявка + позиции + оплаты + фото. JobEntity? == null → null целиком */
    @OptIn(ExperimentalCoroutinesApi::class)
    fun observeJobDetails(jobId: Long): Flow<JobDetails?> =
        jobDao.observeById(jobId).flatMapLatest { job ->
            if (job == null) {
                flowOf(null)
            } else {
                combine(
                    jobItemDao.observeByJob(jobId),
                    paymentDao.observeByJob(jobId),
                    jobPhotoDao.observeByJob(jobId),
                ) { items, payments, photos ->
                    JobDetails(
                        job = job,
                        items = items,
                        payments = payments,
                        photosBefore = photos.filter { it.kind == PhotoKind.BEFORE },
                        photosAfter = photos.filter { it.kind == PhotoKind.AFTER },
                    )
                }
            }
        }

    suspend fun createJob(
        clientId: Long,
        title: String,
        address: String,
        scheduledAt: Long?,
        notes: String,
    ): Result<Long> {
        val trimmedTitle = title.trim()
        if (trimmedTitle.isEmpty()) {
            return Result.failure(IllegalArgumentException("empty title"))
        }
        return runCatching {
            val jobId = jobDao.insert(
                JobEntity(
                    clientId = clientId,
                    title = trimmedTitle,
                    address = address.trim(),
                    scheduledAt = scheduledAt,
                    notes = notes.trim(),
                ),
            )
            reminderScheduler.schedule(jobId, scheduledAt)
            jobId
        }
    }

    suspend fun updateJob(job: JobEntity): Result<Unit> {
        if (job.title.isBlank()) {
            return Result.failure(IllegalArgumentException("empty title"))
        }
        return runCatching {
            val normalized = job.copy(title = job.title.trim())
            jobDao.update(normalized)
            // Дата сдвинулась/снялась — REPLACE; завершённая заявка напоминать не должна
            if (normalized.status == JobStatus.DONE) {
                reminderScheduler.cancel(normalized.id)
            } else {
                reminderScheduler.schedule(normalized.id, normalized.scheduledAt)
            }
        }
    }

    /** «Завершить»: DONE + finishedAt у заявки, lastVisitAt у клиента — одна транзакция */
    suspend fun finishJob(jobId: Long): Result<Unit> = runCatching {
        val finishedAt = System.currentTimeMillis()
        db.withTransaction {
            jobDao.markFinished(jobId, finishedAt)
            val job = jobDao.getById(jobId)
                ?: throw IllegalArgumentException("job not found")
            val client = clientDao.getById(job.clientId) ?: return@withTransaction
            clientDao.update(client.copy(lastVisitAt = finishedAt))
        }
        reminderScheduler.cancel(jobId)
    }

    // endregion

    // region Смета (позиции заявки)

    suspend fun addJobItem(item: JobItemEntity): Result<Unit> {
        validateItem(item)?.let { return it }
        return saveItem(item, isNew = true)
    }

    suspend fun updateJobItem(item: JobItemEntity): Result<Unit> {
        validateItem(item)?.let { return it }
        return saveItem(item, isNew = false)
    }

    suspend fun deleteJobItem(item: JobItemEntity): Result<Unit> = runCatching {
        db.withTransaction {
            jobItemDao.delete(item)
            recalcJobTotal(item.jobId)
        }
    }

    private fun validateItem(item: JobItemEntity): Result<Unit>? {
        if (item.title.isBlank()) return Result.failure(IllegalArgumentException("empty item title"))
        if (item.qty <= 0) return Result.failure(IllegalArgumentException("non-positive qty"))
        if (item.price < 0) return Result.failure(IllegalArgumentException("negative price"))
        return null
    }

    private suspend fun saveItem(item: JobItemEntity, isNew: Boolean): Result<Unit> = runCatching {
        db.withTransaction {
            val normalized = item.copy(title = item.title.trim())
            if (isNew) jobItemDao.insert(normalized) else jobItemDao.update(normalized)
            recalcJobTotal(item.jobId)
        }
    }

    /** totalAmount заявки = Σ qty × price по позициям */
    private suspend fun recalcJobTotal(jobId: Long) {
        val job = jobDao.getById(jobId) ?: return
        jobDao.update(job.copy(totalAmount = jobItemDao.sumByJob(jobId)))
    }

    // endregion

    // region Оплаты

    /** Частичная оплата: amount > 0 и не больше остатка долга */
    suspend fun addPayment(jobId: Long, amount: Long, method: PaymentMethod): Result<Unit> {
        if (amount <= 0) {
            return Result.failure(IllegalArgumentException("non-positive amount"))
        }
        val job = jobDao.getById(jobId)
            ?: return Result.failure(IllegalArgumentException("job not found"))
        val debt = job.totalAmount - job.paidAmount
        if (amount > debt) {
            return Result.failure(IllegalArgumentException("amount exceeds debt"))
        }
        return runCatching {
            db.withTransaction {
                paymentDao.insert(
                    PaymentEntity(
                        jobId = jobId,
                        amount = amount,
                        method = method,
                        paidAt = System.currentTimeMillis(),
                    ),
                )
                recalcJobPaid(jobId)
            }
        }
    }

    suspend fun deletePayment(payment: PaymentEntity): Result<Unit> = runCatching {
        db.withTransaction {
            paymentDao.delete(payment)
            recalcJobPaid(payment.jobId)
        }
    }

    /** paidAmount заявки = Σ платежей */
    private suspend fun recalcJobPaid(jobId: Long) {
        val job = jobDao.getById(jobId) ?: return
        jobDao.update(job.copy(paidAmount = paymentDao.sumByJob(jobId)))
    }

    // endregion

    // region Фото

    /**
     * Регистрация уже сохранённого на диск файла. Само сжатие/запись файла — ответственность
     * UI-слоя (PhotoStorage, итерация с карточкой заявки); репозиторий хранит только путь.
     */
    suspend fun addJobPhoto(jobId: Long, filePath: String, kind: PhotoKind): Result<Long> =
        runCatching {
            jobPhotoDao.insert(JobPhotoEntity(jobId = jobId, filePath = filePath, kind = kind))
        }

    /** Файл с диска здесь не удаляем — это делает UI-слой, которому известен storage */
    suspend fun deleteJobPhoto(photo: JobPhotoEntity): Result<Unit> =
        runCatching { jobPhotoDao.delete(photo) }

    // endregion

    // region Прайс

    fun observePriceItems(): Flow<List<PriceItemEntity>> = priceItemDao.observeAll()

    /** id == 0 — вставка, иначе обновление */
    suspend fun savePriceItem(item: PriceItemEntity): Result<Unit> {
        if (item.title.isBlank()) {
            return Result.failure(IllegalArgumentException("empty item title"))
        }
        if (item.price < 0) {
            return Result.failure(IllegalArgumentException("negative price"))
        }
        return runCatching {
            val normalized = item.copy(title = item.title.trim())
            if (item.id == 0L) priceItemDao.insert(normalized) else priceItemDao.update(normalized)
            Unit
        }
    }

    suspend fun deletePriceItem(item: PriceItemEntity): Result<Unit> =
        runCatching { priceItemDao.delete(item) }

    // endregion

    // region Статистика

    /** Метрики периода [from, to): выручка по платежам, завершённые заявки, средний чек */
    fun observePeriodStats(from: Long, to: Long): Flow<PeriodStats> = combine(
        paymentDao.observeSumInPeriod(from, to),
        jobDao.observeFinishedCountInPeriod(from, to),
        jobDao.observeFinishedTotalInPeriod(from, to),
    ) { revenue, finishedCount, finishedTotal ->
        PeriodStats(
            revenue = revenue,
            finishedJobs = finishedCount,
            finishedTotal = finishedTotal,
        )
    }

    /** Должники: клиенты с незакрытым долгом по активным заявкам, по убыванию долга */
    fun observeDebtors(): Flow<List<ClientWithDebt>> =
        clientDao.observeAllWithDebt().map { clients ->
            clients.filter { it.debt > 0 }.sortedByDescending { it.debt }
        }

    // endregion
}
