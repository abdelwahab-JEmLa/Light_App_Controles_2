package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext

class CapturableLayerState(
    val modifier: Modifier,
    val capture: suspend () -> ImageBitmap,
)

@Composable
fun rememberCapturableLayer(
    @DrawableRes backgroundRes: Int? = null,
): CapturableLayerState {
    val context = LocalContext.current
    val graphicsLayer = rememberGraphicsLayer()
    return remember(graphicsLayer, backgroundRes) {
        val mod = Modifier.drawWithContent {
            graphicsLayer.record { this@drawWithContent.drawContent() }
            drawContent()
        }
        CapturableLayerState(
            modifier = mod,
            capture = {
                // Hardware bitmap → software copy (hardware bitmaps can't be drawn onto a software Canvas)
                val software = graphicsLayer.toImageBitmap()
                    .asAndroidBitmap()
                    .copy(Bitmap.Config.ARGB_8888, false)

                if (backgroundRes == null) return@CapturableLayerState software.asImageBitmap()

                // Composite: background drawable first, content on top — native Canvas, no hw restriction
                val output = Bitmap.createBitmap(software.width, software.height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(output)
                AppCompatResources.getDrawable(context, backgroundRes)?.let { d ->
                    d.setBounds(0, 0, software.width, software.height)
                    d.draw(canvas)
                }
                canvas.drawBitmap(software, 0f, 0f, null)
                output.asImageBitmap()
            },
        )
    }
}
