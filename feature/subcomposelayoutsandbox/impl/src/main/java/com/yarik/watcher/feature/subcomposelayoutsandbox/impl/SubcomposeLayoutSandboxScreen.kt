package com.yarik.watcher.feature.subcomposelayoutsandbox.impl

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yarik.watcher.core.ui.ViewModelFactory
import com.yarik.watcher.utils.textorresource.TextOrResource
import com.yarik.watcher.utils.textorresource.getString

/**
 * Песочница для изучения SubcomposeLayout.
 */
@Composable
fun SubcomposeLayoutSandboxScreen(viewModelFactory: ViewModelFactory) {
    val viewModel: SubcomposeLayoutSandboxViewModel = viewModel(factory = viewModelFactory)
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LoadingIndicator(
                modifier = Modifier
                    .size(200.dp),
                text = TextOrResource.Resource(R.string.subcompose_loading),
            )

            LoadingIndicator(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .size(48.dp),
                text = TextOrResource.Resource(R.string.subcompose_loading),
            )
        }
    }
}

@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    text: TextOrResource,
) {
    val density = LocalDensity.current

    SubcomposeLayout(
        modifier = modifier,
    ) { constraints ->
        val maxHeight = constraints.maxHeight

        val requiredHeightPx = with(density) { 100.dp.toPx() }

        val isTextVisible = constraints.hasBoundedWidth && maxHeight >= requiredHeightPx

        val measurables = subcompose(slotId = "loading_content") {
            if (isTextVisible) {
                // Если места много: показываем вертикальный стек (лоадер + текст)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                    Text(text = text.getString(), fontSize = 16.sp)
                }
            } else {
                // Если места мало: создаем ТОЛЬКО прогресс-бар
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
            }
        }

        val placeables = measurables.map { it.measure(constraints) }

        val layoutWidth = placeables.maxOfOrNull { it.width } ?: constraints.minWidth
        val layoutHeight = placeables.maxOfOrNull { it.height } ?: constraints.minHeight

        layout(layoutWidth, layoutHeight) {
            placeables.forEach { placeable ->
                val x = (constraints.maxWidth - placeable.width) / 2
                val y = (constraints.maxHeight - placeable.height) / 2
                placeable.placeRelative(x, y)
            }
        }
    }
}
