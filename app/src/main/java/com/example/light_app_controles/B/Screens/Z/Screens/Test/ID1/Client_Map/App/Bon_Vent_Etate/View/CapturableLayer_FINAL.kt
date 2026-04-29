package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.provider.MediaStore
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

    /**
     * Captures all registered items and returns them paired with their registration key.
     * Using key-based pairing ensures image names are correctly matched to their
     * source bon (by creationTimestamps), regardless of list order.
     */
    suspend fun captureAll(): List<Pair<String, ImageBitmap>> =
        entries.entries.toList().map { (key, capture) -> key to capture() }
}

@Composable
fun rememberMultiCaptureController() = remember { MultiCaptureController() }

fun saveAllToMediaStore(
    bitmaps: List<Pair<Bitmap, String>>,
    context: Context,
    clientKeyID: String,
) {
    if (bitmaps.isEmpty()) return

    val safeClientKey = clientKeyID.replace(Regex("[^a-zA-Z0-9_\\-]"), "_")
    val folderPath = "Download/Image_Compose_Screen/$safeClientKey"

    val resolver = context.contentResolver
    val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    else
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        resolver.delete(
            collection,
            "${MediaStore.Images.Media.RELATIVE_PATH} = ?",
            arrayOf("$folderPath/"),
        )
    }

    val format = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        Bitmap.CompressFormat.WEBP_LOSSLESS
    else
        @Suppress("DEPRECATION") Bitmap.CompressFormat.WEBP

    bitmaps.forEach { (bitmap, label) ->
        val fileName = "image_${label}.webp"

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/webp")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "$folderPath/")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val uri = resolver.insert(collection, contentValues) ?: return@forEach
        resolver.openOutputStream(uri)?.use { out -> bitmap.compress(format, 100, out) }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            resolver.update(uri, ContentValues().apply {
                put(MediaStore.Images.Media.IS_PENDING, 0)
            }, null, null)
        }
    }
}
