package com.yarik.watcher.features.minesweeper.gamefield

import com.yarik.watcher.features.minesweeper.gamefield.game.model.field.MinesweeperField
import javax.inject.Inject

/**
 * Преобразует игровое состояние поля в [MinesweeperFieldUiModel].
 * [MinesweeperField.NotGenerated] на экране не отображается — null.
 */
class MinesweeperFieldUiModelMapper @Inject constructor() {

    fun map(field: MinesweeperField): MinesweeperFieldUiModel? {
        return when (field) {
            MinesweeperField.NotGenerated -> null

            is MinesweeperField.Generated -> MinesweeperFieldUiModel(
                rows = field.rows,
                columns = field.columns,
            )
        }
    }
}
