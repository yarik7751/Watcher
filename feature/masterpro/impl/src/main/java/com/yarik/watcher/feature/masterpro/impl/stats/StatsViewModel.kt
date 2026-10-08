package com.yarik.watcher.feature.masterpro.impl.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yarik.watcher.core.database.entity.ClientWithDebt
import com.yarik.watcher.feature.masterpro.impl.data.MasterProRepository
import com.yarik.watcher.feature.masterpro.impl.data.model.PeriodStats
import java.util.Calendar
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

enum class StatsPeriod { MONTH, YEAR }

data class StatsUiState(
    val period: StatsPeriod = StatsPeriod.MONTH,
    val stats: PeriodStats = PeriodStats(0, 0, 0),
    val debtors: List<ClientWithDebt> = emptyList(),
)

class StatsViewModel @Inject constructor(
    private val repository: MasterProRepository,
) : ViewModel() {

    private val periodFlow = MutableStateFlow(StatsPeriod.MONTH)

    /** Границы периода [from, to) по текущей дате */
    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<StatsUiState> = combine(
        periodFlow.flatMapLatest { period ->
            val (from, to) = periodRange(period)
            repository.observePeriodStats(from, to)
        },
        repository.observeDebtors(),
        periodFlow,
    ) { stats, debtors, period ->
        StatsUiState(period = period, stats = stats, debtors = debtors)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StatsUiState())

    fun selectPeriod(period: StatsPeriod) {
        periodFlow.value = period
    }

    private fun periodRange(period: StatsPeriod): Pair<Long, Long> {
        val from = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (period == StatsPeriod.MONTH) {
                set(Calendar.DAY_OF_MONTH, 1)
            } else {
                set(Calendar.DAY_OF_YEAR, 1)
            }
        }
        val to = from.clone() as Calendar
        if (period == StatsPeriod.MONTH) {
            to.add(Calendar.MONTH, 1)
        } else {
            to.add(Calendar.YEAR, 1)
        }
        return from.timeInMillis to to.timeInMillis
    }
}
