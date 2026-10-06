package com.yarik.watcher

import android.app.Activity
import android.os.Build
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.yarik.watcher.core.navigation.ScreenContentProvider
import com.yarik.watcher.core.navigation.router.JoyRouter
import com.yarik.watcher.core.ui.DesignSystem
import com.yarik.watcher.core.ui.ViewModelFactory
import com.yarik.watcher.navigation.WatcherNavHost
import com.yarik.watcher.snackbar.ShackBar
import com.yarik.watcher.snackbar.flow.SnackBarManagerFlow
import com.yarik.watcher.snackbar.model.SnackBarData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

private const val SNACK_BAR_ANIMATION_DURATION = 300

/**
 * Корневая Compose-обёртка приложения: тема, системные бары, NavHost,
 * глобальный оверлей снекбара. Живёт в MainActivity (setContent).
 */
@Composable
fun WatcherAppContent(
    router: JoyRouter,
    viewModelFactory: ViewModelFactory,
    contentProviders: Map<String, ScreenContentProvider>,
    snackBarManagerFlow: SnackBarManagerFlow,
) {
    MaterialTheme {
        Surface(
            color = DesignSystem.Colors.testBg
        ) {
            ApplySystemBars()

            Box(modifier = Modifier.fillMaxSize()) {
                WatcherNavHost(
                    router = router,
                    viewModelFactory = viewModelFactory,
                    contentProviders = contentProviders,
                )

                SnackBarOverlay(
                    snackBarManagerFlow = snackBarManagerFlow,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .align(Alignment.BottomCenter),
                )
            }
        }
    }
}

@Composable
private fun ApplySystemBars() {
    val view = LocalView.current
    if (view.isInEditMode) return

    SideEffect {
        val window = (view.context as Activity).window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) { // Android 15+
            window.decorView.setOnApplyWindowInsetsListener { v, insets ->
                val statusBarInsets =
                    insets.getInsets(android.view.WindowInsets.Type.statusBars())

                val navBarInsets =
                    insets.getInsets(android.view.WindowInsets.Type.navigationBars())
                v.setBackgroundColor(DesignSystem.Colors.testBlockBg.toArgb())

                v.setPadding(0, statusBarInsets.top, 0, navBarInsets.bottom)

                insets
            }
        } else {
            window.statusBarColor = DesignSystem.Colors.testBlockBg.toArgb()
            window.navigationBarColor = DesignSystem.Colors.testBlockBg.toArgb()
        }

        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = true
        }
    }
}

@Composable
private fun SnackBarOverlay(
    snackBarManagerFlow: SnackBarManagerFlow,
    modifier: Modifier = Modifier,
) {
    var snackBarData by remember { mutableStateOf<SnackBarData?>(null) }
    LaunchedEffect(Unit) {
        snackBarManagerFlow.observe().onEach { data ->
            snackBarData = data
            delay(data.duration.durationMs)
            snackBarData = null
        }.launchIn(this)
    }

    Box(modifier = modifier) {
        AnimatedContent(
            snackBarData != null,
            transitionSpec = {
                ContentTransform(
                    targetContentEnter = fadeIn(
                        animationSpec = tween(SNACK_BAR_ANIMATION_DURATION),
                    ),
                    initialContentExit = fadeOut(
                        animationSpec = tween(SNACK_BAR_ANIMATION_DURATION),
                    ),
                    sizeTransform = SizeTransform(
                        sizeAnimationSpec = { _, _ -> tween(SNACK_BAR_ANIMATION_DURATION) },
                    )
                )
            }

        ) { targetState ->
            if (targetState) {
                snackBarData?.let {
                    ShackBar(
                        data = it,
                    )
                }
            }
        }
    }
}
