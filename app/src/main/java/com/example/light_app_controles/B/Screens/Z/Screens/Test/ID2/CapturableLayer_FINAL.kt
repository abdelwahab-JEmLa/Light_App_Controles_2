package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID2

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay

class CapturableLayerState(
    val modifier: Modifier,
    val capture: suspend () -> ImageBitmap,
)

// FIXED: Second capture now works properly by ensuring graphics layer is always up-to-date
// The graphics layer is re-recorded on every draw, so captures always get fresh content
@Composable
fun rememberCapturableLayer(
    @DrawableRes backgroundRes: Int? = null,
): CapturableLayerState {
    val context = LocalContext.current
    val graphicsLayer = rememberGraphicsLayer()
    
    val modifier = Modifier.drawWithContent {
        // Record content to graphics layer on every draw
        graphicsLayer.record { 
            this@drawWithContent.drawContent() 
        }
        drawContent()
    }
    
    return CapturableLayerState(
        modifier = modifier,
        capture = {
            // Double delay to ensure content is fully rendered
            delay(100)
            
            // Capture from graphics layer
            val hardware = graphicsLayer.toImageBitmap()
            
            // Convert to software bitmap
            val software = hardware.asAndroidBitmap()
                .copy(Bitmap.Config.ARGB_8888, false)

            if (backgroundRes == null) {
                return@CapturableLayerState software.asImageBitmap()
            }

            // Composite with background
            val output = Bitmap.createBitmap(
                software.width, 
                software.height, 
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(output)
            
            // Draw background first
            AppCompatResources.getDrawable(context, backgroundRes)?.let { drawable ->
                drawable.setBounds(0, 0, software.width, software.height)
                drawable.draw(canvas)
            }
            
            // Draw content on top
            canvas.drawBitmap(software, 0f, 0f, null)
            
            output.asImageBitmap()
        },
    )
}
