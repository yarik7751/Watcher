package com.yarik.watcher.feature.minesweepergamefield.impl.game.model.cell

sealed interface HexCellType {
    data object Bomb : HexCellType
    data class ClearField(
        val bombAmount: Int,
    ) : HexCellType
}