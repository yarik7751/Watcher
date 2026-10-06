package com.yarik.watcher.feature.minesweepergamefield.impl.hexagon

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.cos
import kotlin.math.sin

class HexagonShape : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val radius = size.width / 2f
            val centerX = size.width / 2f
            val centerY = size.height / 2f

            for (i in 0 until 6) {
                val angleRad = Math.toRadians(60.0 * i).toFloat()
                val x = centerX + radius * cos(angleRad)
                val y = centerY + radius * sin(angleRad)
                if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }
        return Outline.Generic(path)
    }
}