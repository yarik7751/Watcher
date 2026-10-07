package com.yarik.watcher.feature.start.impl

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yarik.watcher.core.ui.ViewModelFactory
import com.yarik.watcher.feature.home.api.HomeJoyScreen
import com.yarik.watcher.feature.start.impl.StartViewModel.Commands
import com.yarik.watcher.core.navigation.router.JoyRouter
import com.yarik.watcher.utils.observe

@Composable
fun StartScreen(
    viewModelFactory: ViewModelFactory,
    router: JoyRouter,
) {
    val viewModel: StartViewModel = viewModel(factory = viewModelFactory)
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
            text = stringResource(R.string.start_screen_title),
            fontSize = 28.sp
        )
    }

    HandleCommands(
        viewModel = viewModel,
        router = router,
    )
}

private fun getPermissions(): Array<String> {
    val permissions = mutableListOf<String>()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissions.add(android.Manifest.permission.POST_NOTIFICATIONS)
    }
    permissions.add(android.Manifest.permission.ACCESS_FINE_LOCATION)
    permissions.add(android.Manifest.permission.ACCESS_COARSE_LOCATION)
    permissions.add(android.Manifest.permission.CAMERA)

    return permissions.toTypedArray()
}

@Composable
private fun HandleCommands(
    viewModel: StartViewModel,
    router: JoyRouter,
) {
    viewModel.commandsFlow.observe { command ->
        when (command) {
            Commands.OpenWidgets -> {
                router.replaceScreen(HomeJoyScreen)
            }
        }
    }
}
