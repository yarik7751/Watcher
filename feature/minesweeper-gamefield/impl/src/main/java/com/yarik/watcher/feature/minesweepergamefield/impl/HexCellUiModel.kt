package com.yarik.watcher.feature.minesweepergamefield.impl

import androidx.compose.ui.graphics.Color

/**
 * UI-модель одной ячейки поля «Сапер».
 * Готова к отрисовке: маппер уже решил цвет и текст,
 * composable не знает про игровые состояния.
 */
data class HexCellUiModel(
    val col: Int,
    val row: Int,
    val backgroundColor: Color,
    val text: String?,
    val textColor: Color,
)
