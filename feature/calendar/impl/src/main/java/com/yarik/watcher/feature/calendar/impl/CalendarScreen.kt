package com.yarik.watcher.feature.calendar.impl

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yarik.watcher.core.navigation.router.JoyRouter
import com.yarik.watcher.core.ui.WatcherToolbar
import kotlinx.coroutines.delay
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

private val PromoBackground = Color(0xFF4C7DF0)
private val CardBackground = 0xFFF4F5F9.toInt()
private val GradientStart = 0xFF5AA0F2.toInt()
private val GradientEnd = 0xFF3B78D8.toInt()

/**
 * Экран-превью виджета «Календарь»: рендерит [CalendarDraw] в Bitmap
 * и показывает его на синем фоне, как в макете Figma.
 */
@Composable
fun CalendarScreen(router: JoyRouter) {
    val context = LocalContext.current
    val density = LocalDensity.current

    val bitmap by produceState<Bitmap?>(null) {
        while (true) {
            val now = LocalTime.now()
            val today = LocalDate.now()
            val w = with(density) { 312.dp.roundToPx() }
            val h = with(density) { (312.dp * 390f / 545f).roundToPx() }
            value = CalendarDraw.draw(
                context = context,
                widthPx = w,
                heightPx = h,
                backgroundColor = CardBackground,
                gradientStart = GradientStart,
                gradientEnd = GradientEnd,
                date = today,
                time = now,
                weekStart = DayOfWeek.MONDAY,
            )
            delay(1_000)
        }
    }

    Scaffold(
        topBar = {
            WatcherToolbar(
                title = stringResource(R.string.calendar_title),
                onBackClick = { router.exit() },
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(PromoBackground),
            contentAlignment = Alignment.Center,
        ) {
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentScale = ContentScale.FillWidth,
                )
            }
        }
    }
}
