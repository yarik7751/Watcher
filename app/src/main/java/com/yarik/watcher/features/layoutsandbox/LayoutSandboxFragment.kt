package com.yarik.watcher.features.layoutsandbox

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yarik.watcher.features.base.BaseComposeFragment
import com.yarik.watcher.features.base.viewModels

/**
 * Песочница для изучения Compose Layout.
 */
class LayoutSandboxFragment : BaseComposeFragment() {

    private val viewModel: LayoutSandboxViewModel by viewModels()

    @Composable
    override fun ScreenContent() {
        val state by viewModel.state.collectAsStateWithLifecycle()

        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            ActionButtonsList(
                actions = state.actions,
                onActionClick = viewModel::onActionClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(24.dp),
            )
        }
    }
}

@Composable
private fun ActionButtonsList(
    actions: List<ActionUiModel>,
    onActionClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var offset by remember { mutableFloatStateOf(0f) }
    var maxScrollExtent by remember { mutableFloatStateOf(0f) }

    val scrollableState = rememberScrollableState { delta ->
        val targetOffset = offset + delta

        val coercedOffset = targetOffset.coerceIn(maxScrollExtent, 0f)

        val consumed = coercedOffset - offset
        offset = coercedOffset
        consumed
    }
    Layout(
        modifier = modifier
            .background(Color.Gray)
            .scrollable(
                state = scrollableState,
                orientation = Orientation.Horizontal,
            ),
        content = {
            actions.forEachIndexed { index, model ->
                ActionButton(
                    model = model,
                    onClick = {
                        onActionClick(index)
                    },
                )
            }
        },
    ) { measurables, constraints ->
        val targetWidth = constraints.maxWidth
        val maxHeight = if (constraints.hasBoundedHeight) constraints.maxHeight else 0
        val placeables = measurables.map { measurable ->
            val customConstraints = constraints.copy(
                minWidth = (targetWidth * 0.5).toInt(),
                maxWidth = (targetWidth * 0.5).toInt(),
                minHeight = (maxHeight * 0.1).toInt(),
                maxHeight = (maxHeight * 0.1).toInt(),
            )
            measurable.measure(customConstraints)
        }

        val totalChildrenWidth = placeables.sumOf { it.width }
        maxScrollExtent = (targetWidth - totalChildrenWidth).coerceAtMost(0).toFloat()

        layout(targetWidth, maxHeight) {
            var x = 0
            var y = 0
            placeables.forEach { placeable ->
                val finalX = x + offset.toInt()
                placeable.place(finalX, y)
                x += placeable.width
                if (x < 0) {
                    x = 0
                }
                y += placeable.height
            }
        }
    }
}
