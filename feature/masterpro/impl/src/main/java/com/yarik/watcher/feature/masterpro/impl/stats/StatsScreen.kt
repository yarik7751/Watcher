package com.yarik.watcher.feature.masterpro.impl.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yarik.watcher.core.database.entity.ClientWithDebt
import com.yarik.watcher.core.navigation.router.JoyRouter
import com.yarik.watcher.core.ui.ViewModelFactory
import com.yarik.watcher.core.ui.WatcherToolbar
import com.yarik.watcher.feature.masterpro.api.ClientCardJoyScreen
import com.yarik.watcher.feature.masterpro.impl.R
import com.yarik.watcher.feature.masterpro.impl.data.model.PeriodStats
import com.yarik.watcher.feature.masterpro.impl.ui.formatKopecks

private val PERIODS = listOf(StatsPeriod.MONTH, StatsPeriod.YEAR)

@Composable
fun StatsScreen(
    viewModelFactory: ViewModelFactory,
    router: JoyRouter,
) {
    val viewModel: StatsViewModel = viewModel(factory = viewModelFactory)
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            WatcherToolbar(
                title = stringResource(R.string.stats_title),
                onBackClick = { router.exit() },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
        ) {
            TabRow(selectedTabIndex = PERIODS.indexOf(state.period)) {
                PERIODS.forEach { period ->
                    Tab(
                        selected = state.period == period,
                        onClick = { viewModel.selectPeriod(period) },
                        text = {
                            Text(
                                text = stringResource(
                                    if (period == StatsPeriod.MONTH) R.string.stats_period_month
                                    else R.string.stats_period_year,
                                ),
                            )
                        },
                    )
                }
            }

            StatsCards(stats = state.stats)

            HorizontalDivider(modifier = Modifier.padding(16.dp))

            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = stringResource(R.string.stats_section_debtors),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            if (state.debtors.isEmpty()) {
                Text(
                    modifier = Modifier.padding(16.dp),
                    text = stringResource(R.string.stats_debtors_empty),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                state.debtors.forEach { debtor ->
                    DebtorRow(
                        debtor = debtor,
                        onClick = { router.navigateTo(ClientCardJoyScreen.create(debtor.client.id)) },
                    )
                }
            }
        }
    }
}

@Composable
private fun StatsCards(stats: PeriodStats) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        MetricCard(
            label = stringResource(R.string.stats_revenue),
            value = stats.revenue.formatKopecks(),
        )
        MetricCard(
            label = stringResource(R.string.stats_finished_jobs),
            value = stats.finishedJobs.toString(),
        )
        MetricCard(
            label = stringResource(R.string.stats_average_check),
            value = stats.averageCheck.formatKopecks(),
        )
        if (stats.finishedJobs == 0) {
            Text(
                text = stringResource(R.string.stats_no_data),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun MetricCard(label: String, value: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = label,
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun DebtorRow(debtor: ClientWithDebt, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = debtor.client.name,
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = stringResource(R.string.clients_debt_badge, debtor.debt.formatKopecks()),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
