package com.yarik.watcher.feature.minesweepersettings.impl

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yarik.watcher.core.navigation.router.JoyRouter
import com.yarik.watcher.core.ui.ViewModelFactory
import com.yarik.watcher.core.ui.WatcherToolbar

/**
 * Экран настроек игры «Сапер».
 */
@Composable
fun MinesweeperSettingsScreen(
    viewModelFactory: ViewModelFactory,
    router: JoyRouter,
) {
    val viewModel: MinesweeperSettingsViewModel = viewModel(factory = viewModelFactory)

    Scaffold(
        topBar = {
            WatcherToolbar(
                title = stringResource(R.string.minesweeper_settings_title),
                onBackClick = { router.exit() },
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
        }
    }
}
