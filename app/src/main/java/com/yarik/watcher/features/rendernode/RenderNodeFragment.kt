package com.yarik.watcher.features.rendernode

import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RenderNode
import android.graphics.Shader
import android.os.Build
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yarik.watcher.features.base.BaseComposeFragment
import kotlin.math.min
import androidx.core.graphics.withTranslation

/**
 * Экран для изучения RenderNode.
 */
class RenderNodeFragment : BaseComposeFragment() {

    @Composable
    override fun ScreenContent() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "RenderNode",
                    fontSize = 28.sp,
                )

                Counter()

                Spacer(modifier = Modifier.height(32.dp))

                RenderNodeDemo()
            }
        }
    }
}

@Composable
fun Counter() {
    var count by remember { mutableStateOf(0) }      // ← значение, за которым следят

    Column {
        Text("Нажато: $count")                       // ← ЗДЕСЬ значение читают
        Button(onClick = { count++ }) {              // ← ЗДЕСЬ значение меняют
            Text("Нажать")
        }
    }
}

/**
 * Демо RenderNode (API 29+):
 * контент записывается в RenderNode ОДИН раз, дальше каждый кадр
 * на экран выводится только поворот узла — без повторной записи
 * контента и без рекомпозиции.
 */
@Composable
private fun RenderNodeDemo() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        Text("RenderNode доступен только с Android 10 (API 29)")
        return
    }

    val density = LocalDensity.current
    var nodeSize by remember { mutableStateOf(IntSize.Zero) }
    val renderNode = remember { RenderNode("demo") }
    var angle by remember { mutableFloatStateOf(0f) }

    // 1) записываем контент в RenderNode один раз при смене размера
    LaunchedEffect(nodeSize) {
        if (nodeSize == IntSize.Zero) return@LaunchedEffect
        renderNode.setPosition(0, 0, nodeSize.width, nodeSize.height)
        val canvas = renderNode.beginRecording()
        drawNodeContent(
            canvas = canvas,
            width = nodeSize.width.toFloat(),
            height = nodeSize.height.toFloat(),
            titlePx = with(density) { 18.sp.toPx() },
            subtitlePx = with(density) { 10.sp.toPx() },
        )
        renderNode.endRecording()
    }

    // 2) бесконечная анимация угла — перезаписи контента не происходит
    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos { nanos ->
                angle = (nanos / 20_000_000f) % 360f
            }
        }
    }

    Text(
        text = "контент записан один раз — крутим узел каждый кадр",
        fontSize = 12.sp,
        modifier = Modifier.padding(bottom = 8.dp),
    )

    Canvas(
        modifier = Modifier
            .size(width = 280.dp, height = 200.dp)
            .onSizeChanged { nodeSize = it },
    ) {
        if (nodeSize == IntSize.Zero) return@Canvas

        drawIntoCanvas { composeCanvas ->
            val native = composeCanvas.nativeCanvas
            native.withTranslation(size.width / 2f, size.height / 2f) {
                // поворачиваем вокруг центра канваса
                rotate(angle)
                translate(-nodeSize.width / 2f, -nodeSize.height / 2f)
                drawRenderNode(renderNode)
            }
        }
    }
}

/** Содержимое узла: карточка с градиентом, круг и подписи. Рисуется один раз. */
private fun drawNodeContent(
    canvas: android.graphics.Canvas,
    width: Float,
    height: Float,
    titlePx: Float,
    subtitlePx: Float,
) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // карточка с градиентом
    paint.shader = LinearGradient(
        0f, 0f, 0f, height,
        0xFF5AA0F2.toInt(), 0xFF3B78D8.toInt(),
        Shader.TileMode.CLAMP,
    )
    canvas.drawRoundRect(0f, 0f, width, height, 28f, 28f, paint)
    paint.shader = null

    // белый круг
    paint.color = android.graphics.Color.WHITE
    canvas.drawCircle(width * 0.5f, height * 0.35f, min(width, height) * 0.17f, paint)

    // подписи
    paint.color = android.graphics.Color.WHITE
    paint.textAlign = Paint.Align.CENTER
    paint.typeface = android.graphics.Typeface.DEFAULT_BOLD
    paint.textSize = titlePx
    canvas.drawText("RenderNode", width / 2f, height * 0.72f, paint)
    paint.typeface = android.graphics.Typeface.DEFAULT
    paint.textSize = subtitlePx
    canvas.drawText("записано 1 раз — только поворот", width / 2f, height * 0.85f, paint)
}
