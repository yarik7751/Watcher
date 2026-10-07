package com.yarik.watcher.feature.calendar.impl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.Typeface
import androidx.annotation.ColorInt
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import kotlin.math.min

/**
 * Рендерит виджет «Календарь» целиком в [Bitmap] через Canvas API.
 *
 * Градиент (gradientStart -> gradientEnd, вертикальный) применяется только к:
 * названию дня недели, заголовку «МЕСЯЦ, ГОД» и кругу выделенной даты.
 * Всё остальное рисуется плоскими цветами [Plain].
 *
 * Вся геометрия задана в юнитах макета Figma (545 x 390):
 * 1u = widthPx / 545 — пропорции текста и отступов совпадают с дизайном.
 * heightPx должен сохранять пропорцию 545:390.
 */
object CalendarDraw {

    /** Плоские цвета, не входящие в параметры API. */
    private object Plain {
        const val TEXT_DARK = 0xFF2E3A48.toInt()
        const val TEXT_GRAY = 0xFF8A93A3.toInt()
        const val WEEKEND_RED = 0xFFE05B4B.toInt()
        const val DIAL = Color.WHITE
        const val TICK = 0xFFB9BFC9.toInt()
        const val HAND = 0xFF3A4552.toInt()
    }

    private const val DESIGN_WIDTH = 545f
    private const val DESIGN_HEIGHT = 390f

    // отступы и размеры в юнитах макета
    private const val U_CARD_PAD_H = 20f       // горизонтальный отступ контента
    private const val U_CARD_PAD_TOP = 16f
    private const val U_CARD_PAD_BOTTOM = 12f
    private const val U_RADIUS = 28f           // скругление карточки
    private const val U_COL_GAP = 8f           // между колонками
    private const val U_LEFT_SPLIT = 2.15f     // деление ширины контента
    private const val U_TITLE_SIZE = 19f
    private const val U_TITLE_TO_HEADER = 20f
    private const val U_LETTERS_SIZE = 13f
    private const val U_NUMBER_SIZE = 14f
    private const val U_GRID_GAP = 8f
    private const val U_CIRCLE_R = 14f         // круг выбранного дня
    private const val U_CLOCK_R = 75f          // радиус циферблата
    private const val U_CLOCK_TOP_GAP = 14f
    private const val U_WEEKDAY_SIZE = 16f
    private const val U_WEEKDAY_TO_DIGIT = 6f
    private const val U_DIGIT_SIZE = 48f
    private const val U_SHADOW_PAD = 4f

    fun draw(
        context: Context,
        widthPx: Int,
        heightPx: Int,
        @ColorInt backgroundColor: Int,
        @ColorInt gradientStart: Int,
        @ColorInt gradientEnd: Int,
        date: LocalDate,
        time: LocalTime,
        weekStart: DayOfWeek,
    ): Bitmap {
        // юнит макета в px; контекст оставлен в сигнатуре для будущих ресурсов/шрифтов
        @Suppress("unused")
        val density = context.resources.displayMetrics.density
        val u = widthPx / DESIGN_WIDTH

        val bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val cardLeft = U_SHADOW_PAD * u
        val cardTop = U_SHADOW_PAD * u
        val cardRight = widthPx - U_SHADOW_PAD * u
        val cardBottom = heightPx - U_SHADOW_PAD * u - 2f * u
        val cardRadius = U_RADIUS * u

        drawShadow(canvas, cardLeft, cardTop, cardRight, cardBottom, cardRadius, u)

        // карточка
        val cardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = backgroundColor }
        canvas.drawRoundRect(cardLeft, cardTop, cardRight, cardBottom, cardRadius, cardRadius, cardPaint)

        val contentLeft = cardLeft + U_CARD_PAD_H * u
        val contentRight = cardRight - U_CARD_PAD_H * u

        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)

        // --- левая колонка: часы + день (строится снизу вверх) ---
        val leftColWidth = (contentRight - contentLeft) / U_LEFT_SPLIT
        val leftCenterX = contentLeft + leftColWidth / 2f

        // большая цифра дня — размер подгоняется под ширину колонки
        textPaint.typeface = Typeface.DEFAULT_BOLD
        var digitSize = U_DIGIT_SIZE * u
        textPaint.textSize = digitSize
        while (digitSize > 20f * u && textPaint.measureText("88") > leftColWidth - 8f * u) {
            digitSize -= 1f * u
            textPaint.textSize = digitSize
        }
        val weekdaySize = U_WEEKDAY_SIZE * u

        // часы — сверху колонки, не больше размера из макета и не залезая на блок дня
        val clockTopGap = U_CLOCK_TOP_GAP * u
        val spaceBelowClock = 10f * u
        val dayBlockHeight = weekdaySize + U_WEEKDAY_TO_DIGIT * u + digitSize
        val maxRadiusBySpace =
            (cardBottom - U_CARD_PAD_BOTTOM * u - dayBlockHeight - spaceBelowClock - cardTop - clockTopGap) / 2f
        val clockRadius = min(U_CLOCK_R * u, maxRadiusBySpace)
        val clockCenterY = cardTop + clockTopGap + clockRadius
        drawClock(canvas, leftCenterX, clockCenterY, clockRadius, time, gradientEnd)

        // блок «день недели + цифра» — по центру между часами и нижним краем карточки
        val blockTop = clockCenterY + clockRadius + spaceBelowClock +
            (cardBottom - U_CARD_PAD_BOTTOM * u - clockCenterY - clockRadius - spaceBelowClock - dayBlockHeight) / 2f
        val weekdayBaseline = blockTop + weekdaySize
        textPaint.textSize = weekdaySize
        val weekday = CalendarMath.dayOfWeekTitle(date)
        textPaint.shader = verticalGradient(gradientStart, gradientEnd, weekdayBaseline - weekdaySize, weekdayBaseline)
        canvas.drawText(
            weekday,
            leftCenterX - textPaint.measureText(weekday) / 2f,
            weekdayBaseline,
            textPaint,
        )
        textPaint.shader = null

        // большая цифра дня
        val digitBaseline = weekdayBaseline + U_WEEKDAY_TO_DIGIT * u + digitSize
        textPaint.textSize = digitSize
        canvas.drawText(
            date.dayOfMonth.toString(),
            leftCenterX - textPaint.measureText(date.dayOfMonth.toString()) / 2f,
            digitBaseline,
            textPaint,
        )

        // --- правая колонка: календарь ---
        val colLeft = contentLeft + leftColWidth + U_COL_GAP * u
        val colWidth = contentRight - colLeft

        // заголовок «СЕНТЯБРЬ, 2026» (градиент)
        val titleSize = U_TITLE_SIZE * u
        textPaint.textSize = titleSize
        val title = CalendarMath.monthTitle(date)
        val titleTop = cardTop + U_CARD_PAD_TOP * u
        textPaint.shader = verticalGradient(gradientStart, gradientEnd, titleTop, titleTop + titleSize)
        canvas.drawText(title, colLeft, titleTop + titleSize, textPaint)
        textPaint.shader = null

        // шапка дней недели
        val lettersSize = U_LETTERS_SIZE * u
        textPaint.textSize = lettersSize
        textPaint.typeface = Typeface.DEFAULT_BOLD
        textPaint.letterSpacing = 0.08f
        val headerTop = titleTop + titleSize + U_TITLE_TO_HEADER * u
        val lettersMondayFirst = context.resources
            .getStringArray(R.array.calendar_weekday_letters_monday_first)
            .toList()
        val letters = CalendarMath.weekdayLetters(weekStart, lettersMondayFirst)
        val cellWidth = colWidth / 7f
        letters.forEachIndexed { i, letter ->
            // последние две колонки — всегда выходные, независимо от первого дня недели
            val weekend = i >= 5
            textPaint.color = if (weekend) Plain.WEEKEND_RED else Plain.TEXT_GRAY
            canvas.drawText(
                letter,
                colLeft + cellWidth * i + (cellWidth - textPaint.measureText(letter)) / 2f,
                headerTop + lettersSize,
                textPaint,
            )
        }

        // сетка чисел
        val numberSize = U_NUMBER_SIZE * u
        textPaint.textSize = numberSize
        textPaint.typeface = Typeface.DEFAULT
        textPaint.letterSpacing = 0f
        val gridTop = headerTop + lettersSize + U_GRID_GAP * u
        val gridBottom = cardBottom - U_CARD_PAD_BOTTOM * u
        val rowHeight = (gridBottom - gridTop) / 6f

        val fm = textPaint.fontMetrics
        val numberCenterOffset = -(fm.ascent + fm.descent) / 2f

        val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        val weeks = CalendarMath.buildWeeks(date, weekStart)
        weeks.forEachIndexed { row, week ->
            val rowCenterY = gridTop + rowHeight * row + rowHeight / 2f
            week.forEachIndexed { col, day ->
                if (day == null) return@forEachIndexed
                val cellCenterX = colLeft + cellWidth * col + cellWidth / 2f
                val isSelected = day == date
                if (isSelected) {
                    circlePaint.shader = LinearGradient(
                        cellCenterX, rowCenterY - U_CIRCLE_R * u,
                        cellCenterX, rowCenterY + U_CIRCLE_R * u,
                        gradientStart, gradientEnd,
                        Shader.TileMode.CLAMP,
                    )
                    canvas.drawCircle(cellCenterX, rowCenterY, U_CIRCLE_R * u, circlePaint)
                    circlePaint.shader = null
                    textPaint.color = Color.WHITE
                } else {
                    textPaint.color = if (CalendarMath.isWeekend(day)) Plain.WEEKEND_RED else Plain.TEXT_DARK
                }
                val label = day.dayOfMonth.toString()
                canvas.drawText(
                    label,
                    cellCenterX - textPaint.measureText(label) / 2f,
                    rowCenterY + numberCenterOffset,
                    textPaint,
                )
            }
        }

        return bitmap
    }

    /** Имитация мягкой тени: 3 слоя полупрозрачного roundRect с нарастанием радиуса. */
    private fun drawShadow(
        canvas: Canvas,
        left: Float, top: Float, right: Float, bottom: Float,
        radius: Float,
        u: Float,
    ) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        listOf(0f to 20, 3f to 12, 7f to 6).forEach { (expand, alpha) ->
            paint.color = Color.argb(alpha, 0, 0, 0)
            canvas.drawRoundRect(
                left + expand * 0.5f * u,
                top + (expand + 2f) * u,
                right - expand * 0.5f * u,
                bottom + (expand + 2f) * u,
                radius + expand * u,
                radius + expand * u,
                paint,
            )
        }
    }

    private fun drawClock(
        canvas: Canvas,
        centerX: Float, centerY: Float, radius: Float,
        time: LocalTime,
        secondHandColor: Int,
    ) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // циферблат
        paint.color = Plain.DIAL
        canvas.drawCircle(centerX, centerY, radius, paint)

        // засечки
        paint.color = Plain.TICK
        for (i in 0 until 60) {
            val major = i % 5 == 0
            val tickLength = radius * if (major) 0.10f else 0.05f
            paint.strokeWidth = radius * if (major) 0.028f else 0.014f
            canvas.save()
            canvas.rotate(i * 6f, centerX, centerY)
            canvas.drawLine(
                centerX, centerY - radius * 0.90f,
                centerX, centerY - radius * 0.90f + tickLength,
                paint,
            )
            canvas.restore()
        }

        // стрелки
        val hours = time.hour % 12
        val handPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        fun hand(angle: Float, length: Float, width: Float, color: Int) {
            handPaint.color = color
            handPaint.strokeWidth = width
            canvas.save()
            canvas.rotate(angle, centerX, centerY)
            canvas.drawLine(centerX, centerY, centerX, centerY - length, handPaint)
            canvas.restore()
        }

        hand(hours * 30f + time.minute * 0.5f, radius * 0.45f, radius * 0.055f, Plain.HAND)
        hand(time.minute * 6f + time.second * 0.1f, radius * 0.68f, radius * 0.040f, Plain.HAND)
        hand(time.second * 6f, radius * 0.78f, radius * 0.018f, secondHandColor)

        // ось
        paint.color = secondHandColor
        canvas.drawCircle(centerX, centerY, radius * 0.07f, paint)
        paint.color = Plain.DIAL
        canvas.drawCircle(centerX, centerY, radius * 0.03f, paint)
    }

    private fun verticalGradient(
        @ColorInt start: Int,
        @ColorInt end: Int,
        top: Float,
        bottom: Float,
    ): Shader = LinearGradient(
        0f, top, 0f, bottom,
        start, end,
        Shader.TileMode.CLAMP,
    )
}
