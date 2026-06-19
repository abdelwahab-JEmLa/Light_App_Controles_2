package com.example.light_app_controles

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.*
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalFoundationStyleApi::class)
val PressedShadowButtonStyle = Style {
    // Basic layouts & paddings
    contentPadding(horizontal = 24.dp, vertical = 12.dp)
    shape(RoundedCornerShape(12.dp))
    background(Color(0xFF6200EE)) // Nice purple background
    contentColor(Color.White)
    
    // Normal state drop shadow (deeper shadow)
    dropShadow(
        Shadow(
            offset = DpOffset(0.dp, 6.dp),
            radius = 8.dp,
            spread = 0.dp,
            color = Color.Black.copy(alpha = 0.25f)
        )
    )
    
    // When pressed, animate properties (shadow becomes smaller and scale decreases)
    pressed {
        animate(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) {
            dropShadow(
                Shadow(
                    offset = DpOffset(0.dp, 2.dp),
                    radius = 3.dp,
                    spread = 0.dp,
                    color = Color.Black.copy(alpha = 0.15f)
                )
            )
            scaleX(0.95f)
            scaleY(0.95f)
        }
    }
}

@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun StyledShadowButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: Style = Style,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = rememberUpdatedStyleState(interactionSource) {
        it.isEnabled = enabled
    }
    
    Row(
        modifier = modifier
            .clickable(
                onClick = onClick,
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null // Let the styles API handle the visuals!
            )
            .styleable(styleState, PressedShadowButtonStyle then style),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

@Preview(showBackground = true)
@Composable
fun ButtonPressedShadowPreview() {
    Box(
        modifier = Modifier
            .padding(48.dp),
        contentAlignment = Alignment.Center
    ) {
        StyledShadowButton(onClick = {}) {
            Text(
                text = "Pressez-moi",
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}
