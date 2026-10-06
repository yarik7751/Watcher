package com.yarik.watcher.feature.minesweepergamefield.impl.game.model.cell

data class HexCoordinate(
    val col: Int,
    val row: Int,
) {

    fun neighbors(): List<HexCoordinate> {
        val odd = col % 2 != 0
        return listOf(
            HexCoordinate(col - 1, if (odd) row else row - 1),
            HexCoordinate(col - 1, if (odd) row + 1 else row),
            HexCoordinate(col, row - 1),
            HexCoordinate(col, row + 1),
            HexCoordinate(col + 1, if (odd) row else row - 1),
            HexCoordinate(col + 1, if (odd) row + 1 else row),
        )
    }
}
