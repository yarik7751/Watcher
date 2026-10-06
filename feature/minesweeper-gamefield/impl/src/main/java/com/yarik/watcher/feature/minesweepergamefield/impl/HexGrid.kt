package com.yarik.watcher.feature.minesweepergamefield.impl.game.model.cell

import com.yarik.watcher.feature.minesweepergamefield.impl.game.model.field.MinesweeperField
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * Единственный канонический способ работы с гекс-сеткой:
 * конвертация координат и проверка границ.
 *
 * Клетки нумеруются в column-major порядке (совпадает с порядком
 * рендера HexagonGridMap): index = col * rows + row.
 *
 * Никакой другой код приложения не должен писать свой обход
 * сетки или вычислять индексы вручную — только через этот файл.
 */
object HexGrid {

    /**
     * Координата -> индекс в списке клеток.
     */
    fun indexOf(coordinate: HexCoordinate, field: MinesweeperField.Generated): Int {
        return indexOf(col = coordinate.col, row = coordinate.row, field = field)
    }

    /**
     * Координата -> индекс в списке клеток.
     */
    fun indexOf(col: Int, row: Int, field: MinesweeperField.Generated): Int {
        return col * field.rows + row
    }

    /**
     * Индекс -> координата.
     */
    fun coordinateOf(index: Int, field: MinesweeperField.Generated): HexCoordinate {
        return HexCoordinate(
            col = index / field.rows,
            row = index % field.rows,
        )
    }

    /**
     * Попадает ли координата в границы поля.
     */
    fun isValid(coordinate: HexCoordinate, field: MinesweeperField.Generated): Boolean {
        return coordinate.col in 0 until field.columns &&
                coordinate.row in 0 until field.rows
    }

    /**
     * Hit-testing: точка в системе координат сетки -> ближайшая клетка.
     *
     * Конвенция центров совпадает с размещением в HexagonGridMap:
     * центр = (col * 1.5R, row * sqrt(3)R + половина высоты для нечетного col).
     *
     * Возвращает null, если точка дальше R от ближайшего центра.
     */
    fun coordinateAt(
        x: Float,
        y: Float,
        radiusPx: Float,
        rows: Int,
        columns: Int,
    ): HexCoordinate? {
        if (rows <= 0 || columns <= 0) return null

        val widthSpacing = radiusPx * 1.5f
        val heightSpacing = sqrt(3f) * radiusPx

        val estimatedCol = (x / widthSpacing).roundToInt()

        var nearest: HexCoordinate? = null
        var nearestDistance = Float.MAX_VALUE

        // Поиск ближайшего центра среди кандидатов вокруг оценочной позиции
        for (col in (estimatedCol - 1)..(estimatedCol + 1)) {
            if (col < 0 || col >= columns) continue

            val parityOffset = if (col % 2 != 0) heightSpacing / 2f else 0f
            val estimatedRow = ((y - parityOffset) / heightSpacing).roundToInt()

            for (row in (estimatedRow - 1)..(estimatedRow + 1)) {
                if (row < 0 || row >= rows) continue

                val centerX = col * widthSpacing
                val centerY = row * heightSpacing + parityOffset
                val distance = (x - centerX) * (x - centerX) + (y - centerY) * (y - centerY)

                if (distance < nearestDistance) {
                    nearestDistance = distance
                    nearest = HexCoordinate(col = col, row = row)
                }
            }
        }

        return if (nearestDistance <= radiusPx * radiusPx) nearest else null
    }
}

/**
 * Размеры гекс-сетки: сколько столбцов и сколько строк.
 * Именованные поля защищают от перепутанных аргументов
 * (в отличие от пары Int или Pair).
 */
data class HexColumnsRows(
    val columns: Int,
    val rows: Int,
)

/**
 * Канонический обход гекс-сетки в column-major порядке:
 * внешний проход по столбцам, внутренний по строкам.
 * Порядок совпадает с нумерацией клеток в [HexGrid.indexOf]
 * и с порядком рендера HexagonGridMap.
 *
 * Пример:
 * HexColumnsRows(columns = 3, rows = 4).forEachCell { col, row -> ... }
 *
 * Свой двойной цикл for (col) { for (row) { } } не писать — только так.
 */
inline fun HexColumnsRows.forEachCell(action: (col: Int, row: Int) -> Unit) {
    for (col in 0 until this.columns) {
        for (row in 0 until this.rows) {
            action(col, row)
        }
    }
}
