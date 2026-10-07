package com.yarik.watcher.feature.minesweeper.impl

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yarik.watcher.core.ui.ViewModelFactory
import com.yarik.watcher.feature.minesweepergamefield.api.MinesweeperFieldJoyScreen
import com.yarik.watcher.core.navigation.router.JoyRouter

/**
 * Главный экран игры «Сапер».
 */
@Composable
fun MinesweeperScreen(
    viewModelFactory: ViewModelFactory,
    router: JoyRouter,
) {
    val viewModel: MinesweeperViewModel = viewModel(factory = viewModelFactory)

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Button(
            modifier = Modifier
                .align(Alignment.Center),
            onClick = {
                router.navigateTo(MinesweeperFieldJoyScreen)
            }
        ) {
            Text(text = stringResource(R.string.minesweeper_start_game))
        }
    }
}
