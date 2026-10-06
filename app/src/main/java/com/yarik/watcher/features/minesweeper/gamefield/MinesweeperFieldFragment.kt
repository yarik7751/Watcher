package com.yarik.watcher.features.minesweeper.gamefield

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.yarik.watcher.features.base.BaseComposeFragment
import com.yarik.watcher.features.base.viewModels
import com.yarik.watcher.features.minesweeper.gamefield.hexagon.HexagonGridMap

/**
 * Экран игрового поля «Сапер».
 */
class MinesweeperFieldFragment : BaseComposeFragment() {

    private val viewModel: MinesweeperFieldViewModel by viewModels()

    @Composable
    override fun ScreenContent() {
        val fieldUiModel by viewModel.fieldUiModelStateFlow.collectAsState()
        val cells by viewModel.cellsStateFlow.collectAsState()
        val density = LocalDensity.current

        Box(
            modifier = Modifier
                .fillMaxSize()
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

    private companion object {
        const val HEX_RADIUS_DP = 28
    }
}
