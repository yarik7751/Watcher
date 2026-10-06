package com.yarik.watcher.features.minesweeper.gamefield.game

import com.yarik.watcher.features.minesweeper.gamefield.game.model.cell.HexCell
import com.yarik.watcher.features.minesweeper.gamefield.game.model.cell.HexCellType
import com.yarik.watcher.features.minesweeper.gamefield.game.model.cell.HexColumnsRows
import com.yarik.watcher.features.minesweeper.gamefield.game.model.cell.HexCoordinate
import com.yarik.watcher.features.minesweeper.gamefield.game.model.cell.HexGrid
import com.yarik.watcher.features.minesweeper.gamefield.game.model.cell.HexVisibility
import com.yarik.watcher.features.minesweeper.gamefield.game.model.cell.forEachCell
import com.yarik.watcher.features.minesweeper.gamefield.game.model.field.GameStatus
import com.yarik.watcher.features.minesweeper.gamefield.game.model.field.MinesweeperField
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

private const val DEFAULT_ROWS_AMOUNT = 30
private const val DEFAULT_COLUMNS_AMOUNT = 30
private const val DEFAULT_MINES_AMOUNT = 5

class GameEngine @Inject constructor() {

    private val fieldFlow = MutableStateFlow<MinesweeperField>(MinesweeperField.NotGenerated)

    /**
     * Число мин текущей игры. Запоминается в [generate],
     * потому что [MinesweeperField.Generated.Unmined] его не хранит,
     * а [putMines] получает только безопасную клетку.
     */
    private var minesTotal: Int = 0

    fun observe(): Flow<MinesweeperField> {
        return fieldFlow.asStateFlow()
    }

    /**
     * Создает пустую сетку [MinesweeperField.Generated.Unmined].
     * Параметры поля передаются явно, чтобы движок можно было
     * переиспользовать для новой игры без пересоздания.
     */
    fun generate(rows: Int, columns: Int, minesTotal: Int) {
        require(rows > 0) { "rows must be positive, got $rows" }
        require(columns > 0) { "columns must be positive, got $columns" }

        val cellsAmount = rows * columns
        require(minesTotal in 1 until cellsAmount) {
            "minesTotal must be in 1..${cellsAmount - 1}, got $minesTotal"
        }

        this.minesTotal = minesTotal

        val cells = buildList(capacity = cellsAmount) {
            HexColumnsRows(columns, rows).forEachCell { col, row ->
                add(
                    HexCell(
                        type = HexCellType.ClearField(bombAmount = 0),
                        visibility = HexVisibility.Hidden,
                        coordinates = HexCoordinate(col = col, row = row),
                    )
                )
            }
        }

        fieldFlow.value = MinesweeperField.Generated.Unmined(
            rows = rows,
            columns = columns,
            cells = cells,
        )
    }

    fun putMines(safeCell: HexCoordinate) {
        require(fieldFlow.value is MinesweeperField.Generated.Unmined) {
            "putMines can only be called when field is Generated.Unmined, " +
                    "but was ${fieldFlow.value::class.simpleName}"
        }

        val unminedField = fieldFlow.value as MinesweeperField.Generated.Unmined
        val newCells = unminedField.cells.toMutableList()

        // Безопасная зона: клетка первого клика и все ее валидные соседи.
        // Соседи за краем поля отбрасываются — у края зона меньше.
        val excludedIndexes = buildSet {
            add(HexGrid.indexOf(safeCell, unminedField))
            safeCell.neighbors()
                .filter { neighbor -> HexGrid.isValid(neighbor, unminedField) }
                .forEach { neighbor -> add(HexGrid.indexOf(neighbor, unminedField)) }
        }

        val mineCandidates = unminedField.cells.indices
            .filter { it !in excludedIndexes }

        require(mineCandidates.size >= this.minesTotal) {
            "minesTotal = ${this.minesTotal} does not fit: only " +
                    "${mineCandidates.size} cells outside the safe zone " +
                    "(field ${unminedField.columns}x${unminedField.rows}, safe zone ${excludedIndexes.size})"
        }

        mineCandidates
            .shuffled()
            .take(this.minesTotal)
            .forEach {
                newCells[it] = newCells[it].copy(type = HexCellType.Bomb)
            }

        newCells.replaceAll {
            when (it.type) {
                HexCellType.Bomb -> it
                is HexCellType.ClearField -> {
                    val bombAmount = it.coordinates.neighbors()
                        .filter { neighbor -> HexGrid.isValid(neighbor, unminedField) }
                        .count { neighbor ->
                            newCells[HexGrid.indexOf(neighbor, unminedField)]
                                .type == HexCellType.Bomb
                        }
                    it.copy(type = HexCellType.ClearField(bombAmount = bombAmount))
                }
            }
        }

        fieldFlow.value = MinesweeperField.Generated.Mined(
            rows = unminedField.rows,
            columns = unminedField.columns,
            cells = newCells,
            minesTotal = this.minesTotal,
            gameStatus = GameStatus.InProgress,
        )
    }

    /**
     * Открывает клетку.
     *
     * - [MinesweeperField.NotGenerated] — невалидный вызов, [require].
     * - [MinesweeperField.Generated.Unmined] — первый клик: сначала
     *   заминирование через [putMines], затем открытие этой же клетки.
     * - [MinesweeperField.Generated.Mined] — открытие; пустая клетка
     *   раскрывает соседей каскадно (BFS), флажки при каскаде не трогаем.
     *
     * Бомба -> [GameStatus.Defeat] и раскрытие всех мин.
     * Все чистые клетки открыты -> [GameStatus.Victory] и автоматические
     * флажки на оставшихся минах.
     *
     * No-op, если игра окончена (Victory/Defeat), клетка уже открыта
     * или помечена флажком.
     */
    fun openCell(coordinates: HexCoordinate) {
        when (val field = fieldFlow.value) {
            MinesweeperField.NotGenerated ->
                error("openCell called before generate")

            is MinesweeperField.Generated.Unmined -> {
                putMines(safeCell = coordinates)
                openCell(coordinates)
            }

            is MinesweeperField.Generated.Mined ->
                openCellInMinedField(field, coordinates)
        }
    }

    private fun openCellInMinedField(
        field: MinesweeperField.Generated.Mined,
        coordinates: HexCoordinate,
    ) {
        if (field.gameStatus != GameStatus.InProgress) return
        if (!HexGrid.isValid(coordinates, field)) return

        val target = field.cells[HexGrid.indexOf(coordinates, field)]
        if (target.visibility != HexVisibility.Hidden) return

        val newCells = field.cells.toMutableList()

        if (target.type == HexCellType.Bomb) {
            // Поражение: раскрываем все мины на поле.
            field.cells.forEachIndexed { index, cell ->
                if (cell.type == HexCellType.Bomb) {
                    newCells[index] = cell.copy(visibility = HexVisibility.Revealed)
                }
            }
            fieldFlow.value = field.copy(
                cells = newCells,
                gameStatus = GameStatus.Defeat,
            )
            return
        }

        // Каскадное раскрытие (BFS): пустая клетка тянет за собой соседей,
        // клетка с числом открывается сама, но соседей не раскрывает.
        // Повторно в очередь попасть нельзя: открытая клетка проверяется
        // по visibility — гексы не зацикливаются.
        val queue = ArrayDeque<HexCoordinate>()
        queue.add(coordinates)

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            if (!HexGrid.isValid(current, field)) continue

            val currentIndex = HexGrid.indexOf(current, field)
            val cell = newCells[currentIndex]
            if (cell.visibility != HexVisibility.Hidden) continue

            newCells[currentIndex] = cell.copy(visibility = HexVisibility.Revealed)

            val clearType = cell.type as? HexCellType.ClearField ?: continue
            if (clearType.bombAmount == 0) {
                current.neighbors()
                    .filter { neighbor -> HexGrid.isValid(neighbor, field) }
                    .forEach { neighbor ->
                        val neighborCell = newCells[HexGrid.indexOf(neighbor, field)]
                        if (neighborCell.visibility == HexVisibility.Hidden) {
                            queue.add(neighbor)
                        }
                    }
            }
        }

        // Победа: не осталось ни одной закрытой чистой клетки.
        val isVictory = newCells.none { cell ->
            cell.type is HexCellType.ClearField && cell.visibility != HexVisibility.Revealed
        }

        val finalCells = if (isVictory) {
            newCells.map { cell ->
                if (cell.type == HexCellType.Bomb && cell.visibility == HexVisibility.Hidden) {
                    cell.copy(visibility = HexVisibility.Flagged)
                } else {
                    cell
                }
            }
        } else {
            newCells
        }

        fieldFlow.value = field.copy(
            cells = finalCells,
            gameStatus = if (isVictory) GameStatus.Victory else GameStatus.InProgress,
        )
    }

    /**
     * Ставит или снимает флажок на клетке (переключение).
     *
     * Работает только с [MinesweeperField.Generated.Mined]
     * в статусе [GameStatus.InProgress].
     *
     * No-op, если игра окончена, координата вне поля
     * или клетка уже открыта (флажок на открытую не ставится).
     */
    fun putFlag(coordinates: HexCoordinate) {
        val field = fieldFlow.value as? MinesweeperField.Generated.Mined ?: return
        if (field.gameStatus != GameStatus.InProgress) return
        if (!HexGrid.isValid(coordinates, field)) return

        val index = HexGrid.indexOf(coordinates, field)
        val cell = field.cells[index]
        val newVisibility = when (cell.visibility) {
            HexVisibility.Hidden -> HexVisibility.Flagged
            HexVisibility.Flagged -> HexVisibility.Hidden
            HexVisibility.Revealed -> return
        }

        val newCells = field.cells.toMutableList()
        newCells[index] = cell.copy(visibility = newVisibility)

        fieldFlow.value = field.copy(cells = newCells)
    }
}
