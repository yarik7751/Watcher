package com.yarik.watcher.features.minesweeper.gamefield.game.model.cell

data class HexCell(
    val type: HexCellType,
    val visibility: HexVisibility,
    val coordinates: HexCoordinate,
)
