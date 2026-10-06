package com.yarik.watcher.features.minesweeper

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.yarik.watcher.features.base.BaseComposeFragment
import com.yarik.watcher.features.base.viewModels
import com.yarik.watcher.features.minesweeper.gamefield.MinesweeperFieldJoyScreen

/**
 * Главный экран игры «Сапер».
 */
class MinesweeperFragment : BaseComposeFragment() {

    private val viewModel: MinesweeperViewModel by viewModels()

    @Composable
    override fun ScreenContent() {
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
                Text(text = "Start game")
            }
        }
    }
}
