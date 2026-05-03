package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
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

private class DrawnHolder {
    var value: Boolean = false
}
class CapturableLayerState(
    val modifier: Modifier,
    val capture: suspend () -> ImageBitmap,
    val hasBeenDrawn: () -> Boolean,
)
private const val TAG = "CapturableLayer"

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
            Log.d(TAG, "Captured bitmap size: ${sw.width}x${sw.height} px")

            if (backgroundRes == null) return@CapturableLayerState sw.asImageBitmap()

            val out = Bitmap.createBitmap(sw.width, sw.height, Bitmap.Config.ARGB_8888)
            val cvs = Canvas(out)
            AppCompatResources.getDrawable(ctx, backgroundRes)?.let { drw ->
                drw.setBounds(0, 0, sw.width, sw.height)
                drw.draw(cvs)
            }
            cvs.drawBitmap(sw, 0f, 0f, null)
            Log.d(TAG, "Captured bitmap size (with bg): ${out.width}x${out.height} px")
            out.asImageBitmap()
        },
    )
}

// ---------------------------------------------------------------------------
// MultiCaptureController
// ---------------------------------------------------------------------------

private data class CaptureEntry(
    /**
     * Position of the item inside the LazyColumn at the time of registration.
     * ⚠️  This value goes STALE after FastAdd: new items prepend to allBons and
     * shift every existing item's index by +N — but DisposableEffect re-registers
     * AFTER LaunchedEffect fires the capture.  Do NOT rely on this field for final
     * sorting; pass [orderedKeys] to [captureAllWithScroll] instead.
     */
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
        entries.entries.toList().map { (k, e) -> k to e.capture() }.also { results ->
            Log.d(TAG, "captureAll: ${results.size} items captured → " +
                results.joinToString { (k, bmp) -> "$k(${bmp.width}x${bmp.height})" })
        }

    /**
     * Only capture items whose composable has actually been drawn on screen at least once.
     * Items pre-composed by LazyColumn's look-ahead buffer but never scrolled into view
     * are silently skipped, preventing blank bitmaps from polluting the result.
     */
    suspend fun captureAllVisible(): List<Pair<String, ImageBitmap>> =
        entries.entries.toList()
            .filter { (_, e) -> e.hasBeenDrawn() }
            .map { (k, e) -> k to e.capture() }.also { results ->
                Log.d(TAG, "captureAllVisible: ${results.size}/${entries.size} items captured → " +
                    results.joinToString { (k, bmp) -> "$k(${bmp.width}x${bmp.height})" })
            }

    /**
     * **Full capture of every item, including those off-screen.**
     *
     * Iterates indices 0..[totalItemCount-1], scrolling to each position and
     * harvesting any newly-drawn items that appear.  This is the correct approach
     * when items unregister themselves as they scroll off-screen (LazyColumn
     * disposal).
     *
     * ### Ordering — why [orderedKeys] is required after FastAdd
     *
     * When FastAdd fires:
     * 1. `allBons` recomposes → new items land at index 0, 1 (sortedByDescending).
     * 2. `LaunchedEffect(fastAddCaptureVersion)` starts the coroutine **immediately**.
     * 3. `DisposableEffect` re-registers existing items with their new indices **later**,
     *    during the next composition frame.
     * 4. The stale `e.index` values in [entries] (e.g. 0,1,2 instead of 2,3,4) make
     *    `sortedBy { it.listIndex }` produce the wrong order → `Credit(Apr30)` at `[0]`.
     *
     * Fix: pass [orderedKeys] = the canonical key list derived from `allBons` **at the
     * moment the coroutine starts** (post-recomposition).  We sort by position in that
     * list — never by the potentially-stale stored index.
     *
     * @param state           The [LazyListState] of the target LazyColumn.
     * @param totalItemCount  Total number of items in the LazyColumn for this list.
     *                        Pass the filtered count for this client/period, NOT
     *                        the global list size.
     * @param scrollSettleMs  Milliseconds to wait after each scroll for Compose to lay
     *                        out and draw newly visible items. Increase to 300+ on slow
     *                        devices.
     * @param restoreIndex    List index to scroll back to after capture is done.
     * @param orderedKeys     **Pass this always.**  The authoritative list of keys in
     *                        the exact order they should appear in the output — built
     *                        from `allBons.map { capKey(it) }` right before calling
     *                        this function.  When non-null this replaces the fallback
     *                        sort-by-[CaptureEntry.index] which is unreliable after
     *                        FastAdd shifts all indices before DisposableEffect runs.
     */
    suspend fun captureAllWithScroll(
        state: LazyListState,
        totalItemCount: Int,
        scrollSettleMs: Long = 300,
        restoreIndex: Int = 0,
        // ── FIX: authoritative order from allBons, immune to stale e.index ──
        orderedKeys: List<String>? = null,
    ): List<Pair<String, ImageBitmap>> {
        data class R(val key: String, val listIndex: Int, val bmp: ImageBitmap)
        val results = mutableListOf<R>()
        val capturedKeys = mutableSetOf<String>()

        for (index in 0 until totalItemCount) {
            state.scrollToItem(index)
            delay(scrollSettleMs)
            // Harvest any newly drawn entries not yet captured at this scroll position.
            for ((k, e) in entries.entries.toList()) {
                if (k !in capturedKeys && e.hasBeenDrawn()) {
                    results.add(R(k, e.index, e.capture()))
                    capturedKeys.add(k)
                }
            }
        }

        // ── Sort ───────────────────────────────────────────────────────────────
        // Prefer orderedKeys (authoritative allBons snapshot passed by the caller).
        // Fall back to e.index only when orderedKeys is not provided — e.g. for the
        // simple runCapture() path where FastAdd is not involved.
        val sorted = if (orderedKeys != null) {
            results.sortedBy { r ->
                val pos = orderedKeys.indexOf(r.key)
                if (pos >= 0) pos else Int.MAX_VALUE   // unknown keys go to the end
            }
        } else {
            results.sortedBy { it.listIndex }
        }.map { it.key to it.bmp }

        state.scrollToItem(restoreIndex)
        Log.d(TAG, "captureAllWithScroll: ${sorted.size}/$totalItemCount items captured → " +
            sorted.mapIndexed { i, (k, bmp) -> "[$i]$k(${bmp.width}x${bmp.height})" }
                .joinToString())
        return sorted
    }

    /** Capture only the [n] most-recently-registered entries (last N items in the list). */
    suspend fun captureLastN(n: Int): List<Pair<String, ImageBitmap>> =
        entries.entries.toList().takeLast(n).map { (k, e) -> k to e.capture() }.also { results ->
            Log.d(TAG, "captureLastN($n): ${results.size} items captured → " +
                results.joinToString { (k, bmp) -> "$k(${bmp.width}x${bmp.height})" })
        }
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
