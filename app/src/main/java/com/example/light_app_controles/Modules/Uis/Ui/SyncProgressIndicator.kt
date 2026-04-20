package com.example.light_app_controles.Modules.Uis.Ui

import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SyncProgressIndicator(progress: Float, modifier: Modifier = Modifier) {
    LinearProgressIndicator(
        progress = { progress },
        modifier = modifier.height(6.dp),
        color = Color(0xFF4CAF50),
        trackColor = Color.Gray.copy(alpha = 0.25f),
    )
}
