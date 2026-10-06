package com.yarik.watcher.feature.minesweepergamefield.impl.game.model.field

import com.yarik.watcher.feature.minesweepergamefield.impl.game.model.cell.HexCell

sealed interface MinesweeperField {

    data object NotGenerated : MinesweeperField

    sealed interface Generated : MinesweeperField {

        val rows: Int
        val columns: Int
        val cells: List<HexCell>

        /**
         * Сетка построена (клетки и размеры есть), но мины ещё не расставлены.
         * Стадия до первого клика.
         */
        data class Unmined(
            override val rows: Int,
            override val columns: Int,
            override val cells: List<HexCell>,
        ) : Generated

        /**
         * Мины расставлены, игра идёт.
         */
        data class Mined(
            override val rows: Int,
            override val columns: Int,
            val minesTotal: Int,
            override val cells: List<HexCell>,
            val gameStatus: GameStatus,
        ) : Generated
    }
}
