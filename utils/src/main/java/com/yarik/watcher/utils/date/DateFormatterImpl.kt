package com.yarik.watcher.utils.date

import android.content.Context
import com.yarik.watcher.utils.R
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

class DateFormatterImpl @Inject constructor(
    private val context: Context,
    private val locale: Locale,
) : DateFormatter {

    private val formatMonthYear = SimpleDateFormat("MMMM yyyy")
    private val formatTime = SimpleDateFormat("HH:mm", locale)
    private val formatTimeOffset = DateTimeFormatter.ofPattern("HH:mm", locale)
    private val formatTime12HourFormatOffset = DateTimeFormatter.ofPattern("hh:mm a", locale)
    private val formatTimeDateOffset = DateTimeFormatter.ofPattern("HH:mm d MMMM", locale)
    private val formatMonth = SimpleDateFormat("d MMMM", locale)
    private val formatMonthOffset = DateTimeFormatter.ofPattern("dd MMMM", locale)
    private val formatShortMonth = SimpleDateFormat("LLL", locale)
    private val formatFullMonth = DateTimeFormatter.ofPattern("MMMM", locale)
    private val formatDayMonth = DateTimeFormatter.ofPattern("d MMMM", locale)
    private val formatDayMonthShort = DateTimeFormatter.ofPattern("d MMM", locale)
    private val formatDayMonthHour = DateTimeFormatter.ofPattern("d MMMM, HH:mm", locale)
    private val formatDayMonthDayOfWeek = DateTimeFormatter.ofPattern("d MMMM, EEEE", locale)
    private val formatDayShortMonthShortDayOfWeek =
        DateTimeFormatter.ofPattern("d MMM, EEE", locale)
    private val formatDayMonthYearDayOfWeek =
        DateTimeFormatter.ofPattern("d MMMM yyyy, EEEE", locale)
    private val formatDayShortMonthYear = DateTimeFormatter.ofPattern("d MMM yyyy", locale)
    private val formatDayFullMonthYear = DateTimeFormatter.ofPattern("d MMMM yyyy", locale)
    private val formatDayMonthYearDigits = DateTimeFormatter.ofPattern("dd MM yyyy", locale)
    private val formatDayMonthDigitsShort = DateTimeFormatter.ofPattern("dd.MM", locale)
    private val formatDayMonthYear = DateTimeFormatter.ofPattern("dd.MM.yy", locale)
    private val formatDayMonthFullYear = DateTimeFormatter.ofPattern("dd.MM.yyyy", locale)
    private val formatTimeDayMonthYear = DateTimeFormatter.ofPattern("HH:mm dd.MM.yy", locale)
    private val formatDayMonthYearHourMinutes =
        DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm", locale)
    private val formatDayMonthYear12HourFormatMinutes =
        DateTimeFormatter.ofPattern("d MMMM yyyy, hh:mm a", locale)
    private val formatDayMonthShortDayOfWeek = DateTimeFormatter.ofPattern("d MMMM, EEE", locale)
    private val formatDayOfWeek = DateTimeFormatter.ofPattern("EEE", locale)
    private val formatDayOfMonth = DateTimeFormatter.ofPattern("dd", locale)
    private val formatRequestDayMonthYearTime = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", locale)
    private val formatBalanceOperationDate =
        DateTimeFormatter.ofPattern("d MMMM, EEEE, HH:mm", locale)
    private val formatYearMonthDay = DateTimeFormatter.ofPattern("yyyy-MM-dd", locale)
    private val formatQueryId = DateTimeFormatter.ofPattern("yyyyMMddHHmmss", locale)
    private val formatDayMonthYearTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", locale)

    private val formatYearOnly = SimpleDateFormat("yyyy", locale)

    /**
     * Сегодня/Вчера/12.04.2024
     */
    override fun formatDayMonthYear(dateTime: OffsetDateTime): String {
        return when {
            isSameDay(
                date1 = OffsetDateTime.now(),
                date2 = dateTime
            ) -> context.getString(R.string.date_today)

            isYesterdayDay(
                date = dateTime,
            ) -> context.getString(R.string.date_yesterday)

            else -> {
                val localDateTime = dateTime
                    .atZoneSameInstant(ZoneId.systemDefault())
                    .toLocalDateTime()

                formatDayMonthFullYear.format(localDateTime)
            }
        }
    }

    override fun formatDayMonthOrTime(dateTime: OffsetDateTime): String {
        val localDateTime = dateTime
            .atZoneSameInstant(ZoneId.systemDefault())
            .toLocalDateTime()

        return when {
            isSameDay(
                date1 = OffsetDateTime.now(),
                date2 = dateTime
            ) -> formatTimeOffset.format(localDateTime)

            else -> {
                formatDayMonthDigitsShort.format(localDateTime)
            }
        }
    }

    override fun isSameDay(date1: OffsetDateTime, date2: OffsetDateTime): Boolean {
        return date1.year == date2.year && date1.dayOfYear == date2.dayOfYear
    }

    override fun isYesterdayDay(
        date: OffsetDateTime,
        today: OffsetDateTime,
    ): Boolean {
        val dayDiff = today.dayOfYear - date.dayOfYear
        return dayDiff == 1
    }

    override fun formatHoursMinutes(date: OffsetDateTime): String {
        val localDateTime = date
            .atZoneSameInstant(ZoneId.systemDefault())
            .toLocalDateTime()

        return formatTimeOffset.format(localDateTime)
    }

    override fun formatDayMonthYearDots(date: OffsetDateTime): String {
        val localDateTime = date
            .atZoneSameInstant(ZoneId.systemDefault())
            .toLocalDateTime()

        return formatDayMonthFullYear.format(localDateTime)
    }

    override fun formatDayOfWeek(date: OffsetDateTime): String {
        val localDateTime = date
            .atZoneSameInstant(ZoneId.systemDefault())
            .toLocalDateTime()

        return formatDayOfWeek.format(localDateTime)
    }

    override fun formatDayOfWeek(date: LocalDateTime): String {
        return formatDayOfWeek.format(date)
    }

    override fun formatDayOfMonth(date: OffsetDateTime): String {
        val localDateTime = date
            .atZoneSameInstant(ZoneId.systemDefault())
            .toLocalDateTime()

        return formatDayOfMonth.format(localDateTime)
    }

    override fun formatDayOfMonth(date: LocalDateTime): String {
        return formatDayOfMonth.format(date)
    }

    override fun formatFullDateAndTime(date: LocalDateTime): String {
        return formatTimeDayMonthYear.format(date)
    }
}