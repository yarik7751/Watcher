package com.yarik.watcher.feature.masterpro.impl.data.model

/** Метрики за период [from, to). Деньги — в копейках. */
data class PeriodStats(
    /** Выручка: фактически полученные платежи за период */
    val revenue: Long,
    /** Кол-во завершённых заявок в периоде */
    val finishedJobs: Int,
    /** Сумма смет завершённых заявок в периоде */
    val finishedTotal: Long,
) {

    /** Средний чек завершённых заявок, копейки; 0, если заявок не было */
    val averageCheck: Long
        get() = if (finishedJobs == 0) 0 else finishedTotal / finishedJobs
}
