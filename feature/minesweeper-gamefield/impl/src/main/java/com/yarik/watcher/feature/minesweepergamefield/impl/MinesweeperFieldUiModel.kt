package com.yarik.watcher.feature.minesweepergamefield.impl

/**
 * UI-модель поля «Сапер»: только то, что нужно экрану для отрисовки.
 */
data class MinesweeperFieldUiModel(
    val rows: Int,
    val columns: Int,
)
