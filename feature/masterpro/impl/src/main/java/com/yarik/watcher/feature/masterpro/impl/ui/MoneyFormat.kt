package com.yarik.watcher.feature.masterpro.impl.ui

import java.util.Locale

/** Форматирование денег (копейки → «1 234,50») для экранов «МастерPRO». */
internal fun Long.formatKopecks(): String =
    String.format(Locale("ru", "RU"), "%,.2f", this / 100.0)

/**
 * Парсинг ввода пользователя в копейки. Принимает «1234», «1234.5», «1234,50».
 * Возвращает null при любом неразборчивом вводе — UI обязан показать ошибку, а не угадывать.
 */
internal fun parseMoneyToKopecks(input: String): Long? {
    // NBSP (\u00A0) приходит из автозамены клавиатуры — пишем экранированный литерал
    val normalized = input.trim().replace(" ", "").replace("\u00A0", "").replace(',', '.')
    if (normalized.isEmpty()) return null
    val parts = normalized.split('.')
    if (parts.size > 2) return null
    val rubles = parts[0].toLongOrNull() ?: return null
    if (rubles < 0) return null
    var kopecks = 0L
    if (parts.size == 2) {
        val fraction = parts[1]
        if (fraction.length > 2 || fraction.any { !it.isDigit() }) return null
        kopecks = (fraction + "0".repeat(2 - fraction.length)).toLong()
    }
    return rubles * 100 + kopecks
}
