package com.yarik.watcher.utils

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.util.SizeF

fun <T> Context.pinWidget(widgetClass: Class<T>) {
    val appWidgetManager = AppWidgetManager.getInstance(this)
    val provider = ComponentName(this, widgetClass)

    if (appWidgetManager.isRequestPinAppWidgetSupported) {
        val successCallback = PendingIntent.getBroadcast(
            this, 0, Intent(this, widgetClass),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        appWidgetManager.requestPinAppWidget(provider, null, successCallback)
    } else {
        // TODO
    }
}

fun Context.getWidgetSizeInDp(
    appWidgetId: Int,
    defaultWidth: Int? = null,
    defaultHeight: Int? = null,
): Pair<Int, Int> {
    val appWidgetManager = AppWidgetManager.getInstance(this)
    val options = appWidgetManager.getAppWidgetOptions(appWidgetId)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val sizes = options.getParcelableArrayList<SizeF>(AppWidgetManager.OPTION_APPWIDGET_SIZES)
        if (!sizes.isNullOrEmpty()) {
            return Pair(sizes[0].width.toInt(), sizes[0].height.toInt())
        }
    }

    val isPortrait = this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val width = if (isPortrait) AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH else AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH
    val height = if (isPortrait) AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT else AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT

    val widthDp = options.getInt(width).let {
        if (it == 0 && defaultWidth != null) {
            defaultWidth
        } else {
            it
        }
    }
    val heightDp = options.getInt(height).let {
        if (it == 0 && defaultHeight != null) {
            defaultHeight
        } else {
            it
        }
    }
    return Pair(widthDp, heightDp)
}