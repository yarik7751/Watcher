package com.yarik.watcher.features.start

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yarik.watcher.features.base.BaseComposeFragment
import com.yarik.watcher.features.base.viewModels
import com.yarik.watcher.features.start.StartViewModel.Commands
import com.yarik.watcher.features.home.HomeJoyScreen
import com.yarik.watcher.navigation.router.JoyRouter
import com.yarik.watcher.utils.observe

class StartFragment : BaseComposeFragment() {

    private val viewModel: StartViewModel by viewModels()

    private fun getPermissions(): Array<String> {
        val permissions = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(android.Manifest.permission.POST_NOTIFICATIONS)
        }
        permissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION)
        permissions.add(android.Manifest.permission.ACCESS_COARSE_LOCATION)
        permissions.add(android.Manifest.permission.CAMERA)
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions.add(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }*/

        return permissions.toTypedArray()
    }

    @Composable
    override fun ScreenContent() {
        val state by viewModel.stateFlow.collectAsStateWithLifecycle()

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) {
            viewModel.onPermissionsRequested()
        }

        LaunchedEffect(Unit) {
            launcher.launch(getPermissions())
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {

            Text(
                modifier = Modifier
                    .align(Alignment.Center),
                text = "Start screen",
                fontSize = 28.sp
            )
        }

        HandleCommands(
            viewModel = viewModel,
            router = router,
        )
    }
}

@Composable
private fun HandleCommands(
    viewModel: StartViewModel,
    router: JoyRouter,
) {
    val context = LocalContext.current
    viewModel.commandsFlow.observe { command ->
        when (command) {
            Commands.OpenWidgets -> {
                router.replaceScreen(HomeJoyScreen)
            }
        }
    }
}