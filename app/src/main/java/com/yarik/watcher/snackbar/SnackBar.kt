package com.yarik.watcher.snackbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yarik.watcher.designsystem.DesignSystem
import com.yarik.watcher.snackbar.model.SnackBarData
import com.yarik.watcher.snackbar.model.SnackBarType
import com.yarik.watcher.utils.textorresource.getString

@Composable
fun ShackBar(
    data: SnackBarData,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .padding(horizontal = 24.dp)
            .height(56.dp)
            .background(
                color = DesignSystem.Colors.testWidgetBg,
                shape = RoundedCornerShape(23.dp),
            ),
    ) {
        val color = when (data.type) {
            SnackBarType.Success -> DesignSystem.Colors.testSuccessText
            SnackBarType.Error -> DesignSystem.Colors.testErrorText
            SnackBarType.Info -> DesignSystem.Colors.testBlockText
        }
        Text(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .align(Alignment.Center),
            text = data.message.getString(),
            color = color,
            textAlign = TextAlign.Start,
            fontSize = 14.sp,
        )
    }
}