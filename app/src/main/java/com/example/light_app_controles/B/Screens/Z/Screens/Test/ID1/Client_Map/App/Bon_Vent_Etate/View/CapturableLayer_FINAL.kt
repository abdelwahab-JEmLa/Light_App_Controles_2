package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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

@Composable
fun rememberCapturableLayer(
    @DrawableRes backgroundRes: Int? = null,
): CapturableLayerState {
    val context = LocalContext.current
    val graphicsLayer = rememberGraphicsLayer()

    val modifier = Modifier.drawWithContent {
        graphicsLayer.record { this@drawWithContent.drawContent() }
        drawContent()
    }

    return CapturableLayerState(
        modifier = modifier,
        capture = {
            delay(100)

            val hardware = graphicsLayer.toImageBitmap()
            val software = hardware.asAndroidBitmap().copy(Bitmap.Config.ARGB_8888, false)

            if (backgroundRes == null) return@CapturableLayerState software.asImageBitmap()

            val output = Bitmap.createBitmap(software.width, software.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(output)
            AppCompatResources.getDrawable(context, backgroundRes)?.let { drawable ->
                drawable.setBounds(0, 0, software.width, software.height)
                drawable.draw(canvas)
            }
            canvas.drawBitmap(software, 0f, 0f, null)
            output.asImageBitmap()
        },
    )
}

class MultiCaptureController {
    private val entries = linkedMapOf<String, suspend () -> ImageBitmap>()

    fun register(key: String, capture: suspend () -> ImageBitmap) {
        entries[key] = capture
    }

    fun unregister(key: String) {
        entries.remove(key)
    }

    suspend fun captureAll(): List<ImageBitmap> = entries.values.toList().map { it() }
}

@Composable
fun rememberMultiCaptureController() = androidx.compose.runtime.remember { MultiCaptureController() }

@Composable
fun CapturableItem(
    itemKey: String,
    controller: MultiCaptureController,
    content: @Composable (captureModifier: Modifier) -> Unit,
) {
    val graphicsLayer = rememberGraphicsLayer()

    val captureModifier = Modifier.drawWithContent {
        graphicsLayer.record { this@drawWithContent.drawContent() }
        drawContent()
    }

    DisposableEffect(itemKey) {
        controller.register(itemKey) {
            delay(50)
            val hw = graphicsLayer.toImageBitmap()
            hw.asAndroidBitmap().copy(Bitmap.Config.ARGB_8888, false).asImageBitmap()
        }
        onDispose { controller.unregister(itemKey) }
    }

    content(captureModifier)
}
