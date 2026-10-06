package com.yarik.watcher.features.minesweeper.gamefield

/**
 * UI-модель поля «Сапер»: только то, что нужно экрану для отрисовки.
 */
data class MinesweeperFieldUiModel(
    val rows: Int,
    val columns: Int,
)
