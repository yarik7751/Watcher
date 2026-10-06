package com.yarik.watcher.feature.minesweepergamefield.impl.hexagon

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.sp
import com.yarik.watcher.feature.minesweepergamefield.impl.HexCellUiModel
import com.yarik.watcher.feature.minesweepergamefield.impl.game.model.cell.HexCoordinate
import com.yarik.watcher.feature.minesweepergamefield.impl.game.model.cell.HexGrid
import kotlin.math.sqrt

@Composable
fun HexagonGridMap(
    rows: Int,
    columns: Int,
    hexRadiusPx: Float,
    cells: List<HexCellUiModel>,
    modifier: Modifier = Modifier,
    onCellClick: (HexCoordinate) -> Unit = {},
    onCellLongClick: (HexCoordinate) -> Unit = {},
) {
    var offset by remember { mutableStateOf(Offset.Zero) }
    var scale by remember { mutableFloatStateOf(1f) }

    val widthSpacing = hexRadiusPx * 1.5f
    val heightSpacing = sqrt(3f) * hexRadiusPx

    // Обратное преобразование: экранная точка -> точка в системе сетки
    // (graphicsLayer применяет translation, затем scale).
    fun hitCell(tap: Offset): HexCoordinate? {
        if (cells.isEmpty()) return null
        val layoutX = (tap.x - offset.x) / scale
        val layoutY = (tap.y - offset.y) / scale
        return HexGrid.coordinateAt(
            x = layoutX,
            y = layoutY,
            radiusPx = hexRadiusPx,
            rows = rows,
            columns = columns,
        )
    }

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { tap -> hitCell(tap)?.let(onCellClick) },
                    onLongPress = { tap -> hitCell(tap)?.let(onCellLongClick) },
                )
            }
            .pointerInput(Unit) {
                detectTransformGestures { centroid, pan, zoom, rotation ->
                    scale = (scale * zoom).coerceIn(0.3f, 2.5f)
                    offset += pan
                }
            },

    ) {
        Layout(
            content = {
                cells.forEach { cell ->
                    Box(
                        modifier = Modifier
                            .clip(HexagonShape())
                            .background(cell.backgroundColor),
                        contentAlignment = Alignment.Center,
                    ) {
                        cell.text?.let {
                            Text(text = it, color = cell.textColor, fontSize = 12.sp)
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offset.x,
                    translationY = offset.y,
                ),
        ) { measurables, constraints ->
            val childWidth = (hexRadiusPx * 2).toInt()
            val childHeight = (heightSpacing).toInt()
            val childConstraints = Constraints.fixed(childWidth, childHeight)

            val placeables = measurables.map { it.measure(childConstraints) }

            layout(constraints.maxWidth, constraints.maxHeight) {
                var index = 0

                cells.forEach { cell ->
                    val placeable = placeables[index++]

                    // Базовые координаты центра/начала гекса без учета общего zoom и offset
                    // Смещаем по X на 1.5 * R за каждый столбец
                    val x = (cell.col * widthSpacing).toInt()
                    // Каждый нечетный столбец сдвигаем по вертикали на половину высоты гекса
                    val y = (cell.row * heightSpacing + if (cell.col % 2 != 0) heightSpacing / 2f else 0f).toInt()

                    // Центрируем элемент относительно рассчитанной точки
                    val posX = x - childWidth / 2
                    val posY = y - childHeight / 2

                    // Оптимизация (Culling): определяем, попадает ли элемент в видимое окно с учетом текущего скролла и зума
                    val transformedX = posX * scale + offset.x
                    val transformedY = posY * scale + offset.y
                    val transformedWidth = childWidth * scale
                    val transformedHeight = childHeight * scale

                    val isVisible = transformedX + transformedWidth >= 0 &&
                            transformedX <= constraints.maxWidth &&
                            transformedY + transformedHeight >= 0 &&
                            transformedY <= constraints.maxHeight

                    // Размещаем элемент только если он виден на экране
                    if (isVisible) {
                        placeable.placeRelative(posX, posY)
                    }
                }
            }
        }
    }
}
