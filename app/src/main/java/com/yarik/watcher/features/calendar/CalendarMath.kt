package com.yarik.watcher.features.calendar

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Чистые вычисления для календарного виджета.
 * Без Android-зависимостей — используются и Canvas-рендером, и тестами.
 */
object CalendarMath {

    private val RU = Locale("ru")
    private val MONTH_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("LLLL", RU)
    private val DAY_OF_WEEK_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("EEEE", RU)

    private val WEEKDAY_LETTERS_MONDAY_FIRST = listOf("ПН", "ВТ", "СР", "ЧТ", "ПТ", "СБ", "ВС")

    fun isWeekend(date: LocalDate): Boolean =
        date.dayOfWeek == DayOfWeek.SATURDAY || date.dayOfWeek == DayOfWeek.SUNDAY

    /** «СЕНТЯБРЬ, 2026» (именительный падеж). */
    fun monthTitle(date: LocalDate): String = buildString {
        append(date.format(MONTH_FORMATTER).uppercase(RU))
        append(", ")
        append(date.year)
    }

    /** «Четверг» */
    fun dayOfWeekTitle(date: LocalDate): String =
        date.format(DAY_OF_WEEK_FORMATTER).replaceFirstChar { it.uppercase(RU) }

    /** Заголовки колонок в порядке, заданном первым днём недели. */
    fun weekdayLetters(weekStart: DayOfWeek): List<String> {
        val shift = normalizedShift(weekStart)
        return (0..6).map { WEEKDAY_LETTERS_MONDAY_FIRST[(it + shift) % 7] }
    }

    /**
     * Недели месяца в порядке колонок, заданном [weekStart];
     * null = день чужого месяца (пустая ячейка).
     */
    fun buildWeeks(date: LocalDate, weekStart: DayOfWeek): List<List<LocalDate?>> {
        val firstOfMonth = date.withDayOfMonth(1)
        // сколько дней нужно откатиться от 1-го числа до первого дня недели
        val shift = ((firstOfMonth.dayOfWeek.value - weekStart.value) + 7) % 7
        var cursor = firstOfMonth.minusDays(shift.toLong())
        val weeks = mutableListOf<List<LocalDate?>>()
        while (weeks.size < 6) {
            val week = (0..6).map { cursor.plusDays(it.toLong()) }
            if (week.all { it.isBefore(firstOfMonth) } || week.all { it.month != date.month }) {
                break
            }
            weeks.add(week.map { if (it.month == date.month) it else null })
            cursor = cursor.plusDays(7)
        }
        return weeks
    }

    /** Допустим только понедельник/воскресенье, иначе — понедельник. */
    fun normalizeWeekStart(weekStart: DayOfWeek): DayOfWeek =
        when (weekStart) {
            DayOfWeek.MONDAY, DayOfWeek.SUNDAY -> weekStart
            else -> DayOfWeek.MONDAY
        }

    private fun normalizedShift(weekStart: DayOfWeek): Int =
        when (normalizeWeekStart(weekStart)) {
            DayOfWeek.MONDAY -> 0
            DayOfWeek.SUNDAY -> 6
            else -> 0
        }
}
