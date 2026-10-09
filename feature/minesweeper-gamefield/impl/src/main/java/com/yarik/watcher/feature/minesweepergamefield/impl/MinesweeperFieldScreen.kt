package com.yarik.watcher.feature.minesweepergamefield.impl

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yarik.watcher.core.navigation.router.JoyRouter
import com.yarik.watcher.core.ui.ViewModelFactory
import com.yarik.watcher.core.ui.WatcherToolbar
import com.yarik.watcher.feature.minesweepergamefield.impl.hexagon.HexagonGridMap

/**
 * Экран игрового поля «Сапер».
 */
@Composable
fun MinesweeperFieldScreen(
    viewModelFactory: ViewModelFactory,
    router: JoyRouter,
) {
    val viewModel: MinesweeperFieldViewModel = viewModel(factory = viewModelFactory)
    val fieldUiModel by viewModel.fieldUiModelStateFlow.collectAsState()
    val cells by viewModel.cellsStateFlow.collectAsState()
    val density = LocalDensity.current

    Scaffold(
        topBar = {
            WatcherToolbar(
                title = stringResource(R.string.minesweeper_field_title),
                onBackClick = { router.exit() },
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val field = fieldUiModel ?: return@Box

            HexagonGridMap(
                rows = field.rows,
                columns = field.columns,
                hexRadiusPx = with(density) { HEX_RADIUS_DP.dp.toPx() },
                cells = cells,
                modifier = Modifier.fillMaxSize(),
                onCellClick = viewModel::onCellClick,
                onCellLongClick = viewModel::onCellLongClick,
            )
        }
    }
}

private const val HEX_RADIUS_DP = 28
