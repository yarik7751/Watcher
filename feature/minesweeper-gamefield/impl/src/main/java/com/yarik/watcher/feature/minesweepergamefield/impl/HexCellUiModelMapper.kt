package com.yarik.watcher.feature.minesweepergamefield.impl

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.yarik.watcher.core.ui.DesignSystem
import com.yarik.watcher.feature.minesweepergamefield.impl.game.model.cell.HexCell
import com.yarik.watcher.feature.minesweepergamefield.impl.game.model.cell.HexCellType
import com.yarik.watcher.feature.minesweepergamefield.impl.game.model.cell.HexVisibility
import com.yarik.watcher.feature.minesweepergamefield.impl.game.model.field.MinesweeperField
import javax.inject.Inject

/**
 * Преобразует игровое состояние поля в список [HexCellUiModel]
 * для отрисовки. Вся логика раскраски живет здесь.
 *
 * Правила раскраски:
 * - Unmined: все ячейки синие;
 * - Mined: закрытые — голубые, открытые чистые — зеленые,
 *   флажок — желтый, открытая мина — красная.
 */
class HexCellUiModelMapper @Inject constructor(context: Context) {

    private val flagText = context.getString(R.string.minesweeper_cell_flag)
    private val mineText = context.getString(R.string.minesweeper_cell_mine)

    fun map(field: MinesweeperField): List<HexCellUiModel> {
        return when (field) {
            MinesweeperField.NotGenerated -> emptyList()

            is MinesweeperField.Generated.Unmined ->
                field.cells.map { cell ->
                    cell.toUiModel(
                        backgroundColor = DesignSystem.Colors.minesweeperCellUnmined,
                        text = null,
                    )
                }

            is MinesweeperField.Generated.Mined ->
                field.cells.map { cell -> cell.toMinedUiModel() }
        }
    }

    private fun HexCell.toMinedUiModel(): HexCellUiModel {
        return when (visibility) {
            HexVisibility.Hidden -> toUiModel(
                backgroundColor = DesignSystem.Colors.minesweeperCellHidden,
                text = null,
            )

            HexVisibility.Flagged -> toUiModel(
                backgroundColor = DesignSystem.Colors.minesweeperCellFlagged,
                text = flagText,
            )

            HexVisibility.Revealed -> when (type) {
                HexCellType.Bomb -> toUiModel(
                    backgroundColor = DesignSystem.Colors.minesweeperCellMine,
                    text = mineText,
                )

                is HexCellType.ClearField -> toUiModel(
                    backgroundColor = DesignSystem.Colors.minesweeperCellRevealed,
                    text = if (type.bombAmount > 0) type.bombAmount.toString() else null,
                )
            }
        }
    }

    private fun HexCell.toUiModel(
        backgroundColor: Color,
        text: String?,
    ): HexCellUiModel {
        return HexCellUiModel(
            col = coordinates.col,
            row = coordinates.row,
            backgroundColor = backgroundColor,
            text = text,
            textColor = DesignSystem.Colors.minesweeperCellText,
        )
    }
}
