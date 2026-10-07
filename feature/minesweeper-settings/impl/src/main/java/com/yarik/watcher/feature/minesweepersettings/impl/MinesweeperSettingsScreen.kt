package com.yarik.watcher.feature.minesweepersettings.impl

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yarik.watcher.core.ui.ViewModelFactory

/**
 * Экран настроек игры «Сапер».
 */
@Composable
fun MinesweeperSettingsScreen(viewModelFactory: ViewModelFactory) {
    val viewModel: MinesweeperSettingsViewModel = viewModel(factory = viewModelFactory)

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.Center),
            text = stringResource(R.string.minesweeper_settings_title),
            fontSize = 28.sp
        )
    }
}
