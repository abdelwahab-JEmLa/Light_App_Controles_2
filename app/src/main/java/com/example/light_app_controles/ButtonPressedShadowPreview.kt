package com.example.light_app_controles

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
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

@OptIn(ExperimentalFoundationStyleApi::class)
val ScaleSpringButtonStyle = Style {
    contentPadding(horizontal = 24.dp, vertical = 12.dp)
    shape(RoundedCornerShape(12.dp))
    background(Color(0xFF6200EE))
    contentColor(Color.White)
    
    pressed {
        animate(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) {
            scaleX(1.15f)
            scaleY(1.15f)
        }
    }
}

@OptIn(ExperimentalFoundationStyleApi::class)
val ColorShiftButtonStyle = Style {
    contentPadding(horizontal = 24.dp, vertical = 12.dp)
    shape(RoundedCornerShape(12.dp))
    background(Color(0xFF3F51B5))
    contentColor(Color.White)
    
    pressed {
        animate {
            background(Color(0xFFFF5722))
        }
    }
}

@OptIn(ExperimentalFoundationStyleApi::class)
val BorderMorphButtonStyle = Style {
    contentPadding(horizontal = 24.dp, vertical = 12.dp)
    shape(RoundedCornerShape(12.dp))
    border(2.dp, Color(0xFF9C27B0))
    background(Color.Transparent)
    contentColor(Color(0xFF9C27B0))
    
    pressed {
        animate {
            border(4.dp, Color(0xFFE91E63))
            contentColor(Color(0xFFE91E63))
            background(Color(0xFFE91E63).copy(alpha = 0.1f))
        }
    }
}

@OptIn(ExperimentalFoundationStyleApi::class)
@Composable
fun CustomEffectButton(
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
                indication = null
            )
            .styleable(styleState, style),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

@Preview(showBackground = true)
@Composable
fun ButtonPressedShadowPreview() {
    Column(
        modifier = Modifier
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Effets d'Ombres",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray
        )

        // 1. Bouton Primaire (Style Violet par défaut)
        StyledShadowButton(onClick = {}) {
            Text(
                text = "Bouton Primaire",
                fontSize = 16.sp,
                color = Color.White
            )
        }

        // 2. Bouton Secondaire (Style Vert Teal combiné)
        StyledShadowButton(
            onClick = {},
            style = Style {
                background(Color(0xFF03DAC5))
                contentColor(Color.Black)
            }
        ) {
            Text(
                text = "Bouton Secondaire",
                fontSize = 16.sp,
                color = Color.Black
            )
        }

        // 3. Bouton Alerte/Avertissement (Style Rouge combiné)
        StyledShadowButton(
            onClick = {},
            style = Style {
                background(Color(0xFFCF6679))
                contentColor(Color.White)
            }
        ) {
            Text(
                text = "Bouton Alerte",
                fontSize = 16.sp,
                color = Color.White
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        Text(
            text = "Autres Effets (Sans Ombre)",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray
        )

        // 4. Bouton Zoom Élastique (Scale Spring)
        CustomEffectButton(
            onClick = {},
            style = ScaleSpringButtonStyle
        ) {
            Text(
                text = "Bouton Zoom Élastique",
                fontSize = 16.sp,
                color = Color.White
            )
        }

        // 5. Bouton Changement de Couleur (Color Shift)
        CustomEffectButton(
            onClick = {},
            style = ColorShiftButtonStyle
        ) {
            Text(
                text = "Bouton Transition Couleur",
                fontSize = 16.sp,
                color = Color.White
            )
        }

        // 6. Bouton Bordure Interactive (Border Morph)
        CustomEffectButton(
            onClick = {},
            style = BorderMorphButtonStyle
        ) {
            Text(
                text = "Bouton Bordure Morph",
                fontSize = 16.sp,
                color = Color(0xFF9C27B0)
            )
        }
    }
}
