package com.yarik.watcher.feature.home.impl

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yarik.watcher.core.ui.ViewModelFactory
import com.yarik.watcher.feature.calendar.api.CalendarJoyScreen
import com.yarik.watcher.feature.layoutsandbox.api.LayoutSandboxJoyScreen
import com.yarik.watcher.feature.masterpro.api.ClientsJoyScreen
import com.yarik.watcher.feature.masterpro.api.JobsJoyScreen
import com.yarik.watcher.feature.masterpro.api.PriceJoyScreen
import com.yarik.watcher.feature.masterpro.api.SettingsJoyScreen
import com.yarik.watcher.feature.masterpro.api.StatsJoyScreen
import com.yarik.watcher.feature.minesweeper.api.MinesweeperJoyScreen
import com.yarik.watcher.feature.rendernode.api.RenderNodeJoyScreen
import com.yarik.watcher.feature.subcomposelayoutsandbox.api.SubcomposeLayoutSandboxJoyScreen
import com.yarik.watcher.feature.testgetuserdata.TestGetUserActivity
import com.yarik.watcher.core.navigation.router.JoyRouter

@Composable
fun HomeScreen(
    viewModelFactory: ViewModelFactory,
    router: JoyRouter,
) {
    val viewModel: HomeViewModel = viewModel(factory = viewModelFactory)

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
                text = stringResource(R.string.home_title),
                fontSize = 28.sp
            )

            Button(
                modifier = Modifier
                    .padding(top = 8.dp),
                onClick = {
                    router.navigateTo(JobsJoyScreen)
                }
            ) {
                Text(text = stringResource(R.string.home_button_masterpro))
            }

            Button(
                modifier = Modifier
                    .padding(top = 8.dp),
                onClick = {
                    router.navigateTo(ClientsJoyScreen)
                }
            ) {
                Text(text = stringResource(R.string.home_button_masterpro_clients))
            }

            Button(
                modifier = Modifier
                    .padding(top = 8.dp),
                onClick = {
                    router.navigateTo(StatsJoyScreen)
                }
            ) {
                Text(text = stringResource(R.string.home_button_masterpro_stats))
            }

            Button(
                modifier = Modifier
                    .padding(top = 8.dp),
                onClick = {
                    router.navigateTo(PriceJoyScreen)
                }
            ) {
                Text(text = stringResource(R.string.home_button_masterpro_price))
            }

            Button(
                modifier = Modifier
                    .padding(top = 8.dp),
                onClick = {
                    router.navigateTo(SettingsJoyScreen)
                }
            ) {
                Text(text = stringResource(R.string.home_button_masterpro_settings))
            }

            Button(
                modifier = Modifier
                    .padding(top = 8.dp),
                onClick = {
                    viewModel.onSimpleCounterClick()
                }
            ) {
                Text(text = stringResource(R.string.home_button_simple_counter))
            }

            Button(
                modifier = Modifier
                    .padding(top = 8.dp),
                onClick = {
                    viewModel.onSimpleAtomicCounterClick()
                }
            ) {
                Text(text = stringResource(R.string.home_button_simple_atomic_counter))
            }

            Button(
                modifier = Modifier
                    .padding(top = 8.dp),
                onClick = {
                    viewModel.onReorderingClick()
                }
            ) {
                Text(text = stringResource(R.string.home_button_reordering))
            }

            Button(
                modifier = Modifier
                    .padding(top = 8.dp),
                onClick = {
                    viewModel.onBufferExampleClick()
                }
            ) {
                Text(text = stringResource(R.string.home_button_buffer))
            }

            Button(
                modifier = Modifier
                    .padding(top = 8.dp),
                onClick = {
                    viewModel.onSemaphoreClick()
                }
            ) {
                Text(text = stringResource(R.string.home_button_semaphore))
            }

            Button(
                modifier = Modifier.padding(top = 8.dp),
                onClick = {
                    router.navigateTo(CalendarJoyScreen)
                }
            ) {
                Text(text = stringResource(R.string.home_button_calendar))
            }

            Button(
                modifier = Modifier.padding(top = 8.dp),
                onClick = {
                    router.navigateTo(RenderNodeJoyScreen)
                }
            ) {
                Text(text = stringResource(R.string.home_button_rendernode))
            }

            Button(
                modifier = Modifier.padding(top = 8.dp),
                onClick = {
                    router.navigateTo(LayoutSandboxJoyScreen)
                }
            ) {
                Text(text = stringResource(R.string.home_button_layout_sandbox))
            }

            Button(
                modifier = Modifier.padding(top = 8.dp),
                onClick = {
                    router.navigateTo(SubcomposeLayoutSandboxJoyScreen)
                }
            ) {
                Text(text = stringResource(R.string.home_button_subcompose_sandbox))
            }

            Button(
                modifier = Modifier.padding(top = 8.dp),
                onClick = {
                    context.startActivity(
                        Intent(context, TestGetUserActivity::class.java)
                    )
                }
            ) {
                Text(text = stringResource(R.string.home_button_test_user_data))
            }

            Button(
                modifier = Modifier.padding(top = 8.dp),
                onClick = {
                    router.navigateTo(MinesweeperJoyScreen)
                }
            ) {
                Text(text = stringResource(R.string.home_button_minesweeper))
            }
        }
    }
}
