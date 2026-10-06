package com.yarik.watcher.features.minesweeper.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.yarik.watcher.features.base.BaseComposeFragment
import com.yarik.watcher.features.base.viewModels

/**
 * Экран настроек игры «Сапер».
 */
class MinesweeperSettingsFragment : BaseComposeFragment() {

    private val viewModel: MinesweeperSettingsViewModel by viewModels()

    @Composable
    override fun ScreenContent() {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Text(
                modifier = Modifier
                    .align(Alignment.Center),
                text = "Minesweeper Settings",
                fontSize = 28.sp
            )
        }
    }
}
