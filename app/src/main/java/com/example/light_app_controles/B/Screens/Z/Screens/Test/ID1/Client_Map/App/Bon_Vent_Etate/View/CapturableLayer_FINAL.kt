package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.Z.Modules.Capture

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.lazy.LazyListState
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

// ---------------------------------------------------------------------------
// Plain (non-State) holder — safe to write inside drawWithContent's draw phase.
// ---------------------------------------------------------------------------
private class DrawnHolder {
    var value: Boolean = false
}

// ---------------------------------------------------------------------------
// CapturableLayerState
// ---------------------------------------------------------------------------

/**
 * @param modifier     Apply to the target composable so its content is recorded.
 * @param capture      Suspends briefly then returns the recorded [ImageBitmap].
 * @param hasBeenDrawn Returns true once [modifier] has gone through at least one draw pass.
 *                     Used by [MultiCaptureController] to skip LazyColumn items that are
 *                     pre-composed in the composition buffer but have never been painted
 *                     on screen (fixes TODO(1)).
 */
class CapturableLayerState(
    val modifier: Modifier,
    val capture: suspend () -> ImageBitmap,
    val hasBeenDrawn: () -> Boolean,
)

@Composable
fun rememberCapturableLayer(
    @DrawableRes backgroundRes: Int? = null,
): CapturableLayerState {
    val ctx = LocalContext.current
    val gLayer = rememberGraphicsLayer()

    // Plain holder — written from the draw phase, read from a coroutine later.
    val drawnHolder = remember { DrawnHolder() }

    val mod = Modifier.drawWithContent {
        gLayer.record { this@drawWithContent.drawContent() }
        drawnHolder.value = true   // mark that at least one real draw has happened
        drawContent()
    }

    return CapturableLayerState(
        modifier = mod,
        hasBeenDrawn = { drawnHolder.value },
        capture = {
            delay(100)
            val hw = gLayer.toImageBitmap()
            val sw = hw.asAndroidBitmap().copy(Bitmap.Config.ARGB_8888, false)

            if (backgroundRes == null) return@CapturableLayerState sw.asImageBitmap()

            val out = Bitmap.createBitmap(sw.width, sw.height, Bitmap.Config.ARGB_8888)
            val cvs = Canvas(out)
            AppCompatResources.getDrawable(ctx, backgroundRes)?.let { drw ->
                drw.setBounds(0, 0, sw.width, sw.height)
                drw.draw(cvs)
            }
            cvs.drawBitmap(sw, 0f, 0f, null)
            out.asImageBitmap()
        },
    )
}

// ---------------------------------------------------------------------------
// MultiCaptureController
// ---------------------------------------------------------------------------

private data class CaptureEntry(
    /** Position of the item inside the LazyColumn — used for scroll-based capture. */
    val index: Int,
    /** True once the composable has gone through at least one draw pass. */
    val hasBeenDrawn: () -> Boolean,
    val capture: suspend () -> ImageBitmap,
)

class MultiCaptureController {
    private val entries = linkedMapOf<String, CaptureEntry>()

    /**
     * @param key          Unique key (e.g. `"$ts|$keyID|$etat"`)
     * @param index        The item's position in the LazyColumn list (for scroll-based capture).
     * @param hasBeenDrawn Lambda that returns true once the item has been drawn at least once.
     * @param capture      Suspending capture lambda from [CapturableLayerState.capture].
     */
    fun register(
        key: String,
        index: Int,
        hasBeenDrawn: () -> Boolean,
        capture: suspend () -> ImageBitmap,
    ) {
        entries[key] = CaptureEntry(index, hasBeenDrawn, capture)
    }

    fun unregister(key: String) {
        entries.remove(key)
    }

    /** Returns a snapshot of all currently-registered keys (for diagnostic logging). */
    fun registeredKeys(): List<String> = entries.keys.toList()

    // ── Capture variants ────────────────────────────────────────────────────

    /**
     * Capture ALL registered items regardless of visibility.
     * Items that have never been drawn will produce a blank bitmap.
     * Prefer [captureAllVisible] or [captureAllWithScroll] instead.
     */
    suspend fun captureAll(): List<Pair<String, ImageBitmap>> =
        entries.entries.toList().map { (k, e) -> k to e.capture() }

    /**
     * **FIX for TODO(1)** — Only capture items whose composable has actually been
     * drawn on screen at least once.  Items pre-composed by LazyColumn's look-ahead
     * buffer but never scrolled into view are silently skipped, preventing blank
     * bitmaps from polluting the result.
     */
    suspend fun captureAllVisible(): List<Pair<String, ImageBitmap>> =
        entries.entries.toList()
            .filter { (_, e) -> e.hasBeenDrawn() }
            .map { (k, e) -> k to e.capture() }

    /**
     * **Full capture of every item, including those off-screen.**
     * Iterates entries in list order, scrolls [state] to each item's index,
     * waits [scrollSettleMs] for the composition to re-render, then captures.
     * After all captures the list is scrolled back to [restoreIndex] (default 0).
     *
     * Use this when you need a complete record of all items, not just the
     * currently visible portion of the LazyColumn.
     *
     * @param state           The [LazyListState] of the target LazyColumn.
     * @param scrollSettleMs  Milliseconds to wait after each scroll for Compose
     *                        to lay out and draw the newly visible items.
     * @param restoreIndex    List index to scroll back to after capture is done.
     */
    suspend fun captureAllWithScroll(
        state: LazyListState,
        scrollSettleMs: Long = 150,
        restoreIndex: Int = 0,
    ): List<Pair<String, ImageBitmap>> {
        val sorted = entries.entries.toList().sortedBy { (_, e) -> e.index }
        val results = sorted.map { (k, e) ->
            state.scrollToItem(e.index)
            delay(scrollSettleMs)           // wait for draw pass
            k to e.capture()
        }
        // Restore scroll position so the UI feels natural after capture.
        if (sorted.isNotEmpty()) {
            state.scrollToItem(restoreIndex)
        }
        return results
    }

    /** Capture only the [n] most-recently-registered entries (last N items in the list). */
    suspend fun captureLastN(n: Int): List<Pair<String, ImageBitmap>> =
        entries.entries.toList().takeLast(n).map { (k, e) -> k to e.capture() }
}

@Composable
fun rememberMultiCaptureController() = remember { MultiCaptureController() }

// ---------------------------------------------------------------------------
// MediaStore helper
// ---------------------------------------------------------------------------

fun saveAllToMediaStore(
    bitmaps: List<Pair<Bitmap, String>>,
    context: Context,
    clientKeyID: String,
) {
    if (bitmaps.isEmpty()) return

    val safeKey = clientKeyID.replace(Regex("[^a-zA-Z0-9_\\-]"), "_")
    val folderPath = "Download/Image_Compose_Screen/$safeKey"

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

    val fmt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        Bitmap.CompressFormat.WEBP_LOSSLESS
    else
        @Suppress("DEPRECATION") Bitmap.CompressFormat.WEBP

    bitmaps.forEach { (bmp, lbl) ->
        val fname = "image_${lbl}.webp"
        val cv = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fname)
            put(MediaStore.Images.Media.MIME_TYPE, "image/webp")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "$folderPath/")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val uri = resolver.insert(collection, cv) ?: return@forEach
        resolver.openOutputStream(uri)?.use { out -> bmp.compress(fmt, 100, out) }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            resolver.update(uri, ContentValues().apply {
                put(MediaStore.Images.Media.IS_PENDING, 0)
            }, null, null)
        }
    }
}
