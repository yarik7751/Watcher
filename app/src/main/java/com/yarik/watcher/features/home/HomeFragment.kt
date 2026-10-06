package com.yarik.watcher.features.home

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yarik.watcher.features.base.BaseComposeFragment
import com.yarik.watcher.features.base.viewModels
import com.yarik.watcher.features.calendar.CalendarJoyScreen
import com.yarik.watcher.features.layoutsandbox.LayoutSandboxJoyScreen
import com.yarik.watcher.features.minesweeper.MinesweeperJoyScreen
import com.yarik.watcher.features.rendernode.RenderNodeJoyScreen
import com.yarik.watcher.features.subcomposelayoutsandbox.SubcomposeLayoutSandboxJoyScreen
import com.yarik.watcher.features.testgetuserdata.TestGetUserActivity

class HomeFragment : BaseComposeFragment() {

    private val viewModel: HomeViewModel by viewModels()

    @Composable
    override fun ScreenContent() {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val context = LocalContext.current
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    modifier = Modifier,
                    text = "Home",
                    fontSize = 28.sp
                )

                Button(
                    modifier = Modifier
                        .padding(top = 8.dp),
                    onClick = {
                        viewModel.onSimpleCounterClick()
                    }
                ) {
                    Text(text = "SimpleCounter")
                }

                Button(
                    modifier = Modifier
                        .padding(top = 8.dp),
                    onClick = {
                        viewModel.onSimpleAtomicCounterClick()
                    }
                ) {
                    Text(text = "SimpleAtomicCounter")
                }

                Button(
                    modifier = Modifier
                        .padding(top = 8.dp),
                    onClick = {
                        viewModel.onReorderingClick()
                    }
                ) {
                    Text(text = "Reordering")
                }

                Button(
                    modifier = Modifier
                        .padding(top = 8.dp),
                    onClick = {
                        viewModel.onBufferExampleClick()
                    }
                ) {
                    Text(text = "Buffer(wait, notify)")
                }

                Button(
                    modifier = Modifier
                        .padding(top = 8.dp),
                    onClick = {
                        viewModel.onSemaphoreClick()
                    }
                ) {
                    Text(text = "Semaphore")
                }



                Button(
                    modifier = Modifier.padding(top = 8.dp),
                    onClick = {
                        router.navigateTo(CalendarJoyScreen)
                    }
                ) {
                    Text(text = "Calendar widget")
                }

                Button(
                    modifier = Modifier.padding(top = 8.dp),
                    onClick = {
                        router.navigateTo(RenderNodeJoyScreen)
                    }
                ) {
                    Text(text = "RenderNode")
                }

                Button(
                    modifier = Modifier.padding(top = 8.dp),
                    onClick = {
                        router.navigateTo(LayoutSandboxJoyScreen)
                    }
                ) {
                    Text(text = "Layout Sandbox")
                }

                Button(
                    modifier = Modifier.padding(top = 8.dp),
                    onClick = {
                        router.navigateTo(SubcomposeLayoutSandboxJoyScreen)
                    }
                ) {
                    Text(text = "SubcomposeLayout Sandbox")
                }

                Button(
                    modifier = Modifier.padding(top = 8.dp),
                    onClick = {
                        context.startActivity(
                            Intent(context, TestGetUserActivity::class.java)
                        )
                    }
                ) {
                    Text(text = "Test get user data")
                }

                Button(
                    modifier = Modifier.padding(top = 8.dp),
                    onClick = {
                        router.navigateTo(MinesweeperJoyScreen)
                    }
                ) {
                    Text(text = "Minesweeper")
                }
            }
        }
    }
}
