package com.yarik.watcher.feature.layoutsandbox.impl

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val SelectedColor = Color(0xFF4CAF50)
private val UnselectedColor = Color(0xFF2196F3)

@Composable
fun ActionButton(
    model: ActionUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (model.isSelected) SelectedColor else UnselectedColor,
        ),
        onClick = onClick,
    ) {
        Text(
            text = model.title,
            fontSize = 16.sp,
        )
    }
}
