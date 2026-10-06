package com.yarik.watcher.features.minesweeper.gamefield.game.model.cell

sealed interface HexCellType {
    data object Bomb : HexCellType
    data class ClearField(
        val bombAmount: Int,
    ) : HexCellType
}