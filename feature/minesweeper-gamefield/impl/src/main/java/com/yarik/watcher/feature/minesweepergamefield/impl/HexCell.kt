package com.yarik.watcher.feature.minesweepergamefield.impl.game.model.cell

data class HexCell(
    val type: HexCellType,
    val visibility: HexVisibility,
    val coordinates: HexCoordinate,
)
