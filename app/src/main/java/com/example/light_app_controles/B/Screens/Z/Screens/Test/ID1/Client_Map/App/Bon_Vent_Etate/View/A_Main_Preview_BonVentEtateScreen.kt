package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View

import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.Action.Floating_Separated_Button
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.Options.A_FastAdd_FloatingSeparated_Button_1
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.Z.Modules.Capture.Afficheur_locale_Image_Captured
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.Z.preview.FAKE_CLIENT_KEY
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.Z.preview.Targted_Bon
import com.example.light_app_controles.Modules.Base.SQL.Daos.AppDatabase
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val CREDIT_VERSEMENT_STATES = setOf(
    M8BonVent.EtateActuellementEst.COMMANDE_LIVRAI,
    M8BonVent.EtateActuellementEst.Versemment,
    M8BonVent.EtateActuellementEst.Credit,
    M8BonVent.EtateActuellementEst.Cette_Transaction_Type_Est_Credit,
    M8BonVent.EtateActuellementEst.Demande_Versemet,
    M8BonVent.EtateActuellementEst.New_Situation_Credit,
)

private const val CAPTURE_TAG = "CaptureDebug"

@Composable
fun Main_Preview_BonVentEtateScreen(
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current,
    appDatabase: AppDatabase = AppDatabase.DatabaseModule.getDatabase(context),
    onClick_Lence_Capture: () -> Unit = {},
    lenceTestActive: Boolean = false,
    relative_M2Client: M2Client? = M2Client.get_default().copy(
        keyID = FAKE_CLIENT_KEY
    )
) {
    val vm: A_ViewModel = viewModel(
        factory = viewModelFactory {
            initializer { A_ViewModel(appDatabase = appDatabase) }
        }
    )

    val active_Datas = vm.active_Datas

    val sameClientPeriod: (M8BonVent) -> Boolean = { b ->
        b.parent_M2Client_KeyID == relative_M2Client?.keyID
    }
    val allBons: List<M8BonVent> = active_Datas.list_M8bon
        ?.filter { sameClientPeriod(it) && it.etateActuellementEst in CREDIT_VERSEMENT_STATES }
        ?.sortedByDescending { it.creationTimestamps } ?: emptyList()

    var lenceCaptureActive by remember { mutableStateOf(false) }
    val onLenceCapture: () -> Unit = { lenceCaptureActive = !lenceCaptureActive }

    val scope = rememberCoroutineScope()
    val ctrl = rememberMultiCaptureController()

    var captured by remember { mutableStateOf<List<Pair<ImageBitmap, String>>>(emptyList()) }
    var showDlg by remember { mutableStateOf(false) }
    var fastAddCaptureVersion by remember { mutableStateOf(0) }
    val listState = rememberLazyListState()

    // ── Image-name helpers ────────────────────────────────────────────────────
    //
    // Key format : "${creationTimestamps}|${keyID}|${etatName}"
    // Image name : "{idx}_{MM-ss-SSS}_{etatName}_{MMdd_HHmmss}"
    //   idx          = position in allBons (0 = newest, sortedByDescending)
    //   MM-ss-SSS    = creation-time minutes-seconds-millis (readable, no colons)
    //   etatName     = bon state label
    //   MMdd_HHmmss  = full date stamp for unique file names
    //
    // The "MM:SS:SSS" the user sees in logs is emitted by buildImageName below so
    // creation timestamps appear directly in image file names for easy correlation.

    val sdfFull  = SimpleDateFormat("MMdd_HHmmss", Locale.getDefault())
    val sdfShort = SimpleDateFormat("mm-ss-SSS",   Locale.getDefault())   // MM:SS:SSS → safe chars

    fun buildImageName(idx: Int, key: String): String {
        val pts  = key.split("|")
        val ts   = pts.getOrNull(0)?.toLongOrNull() ?: System.currentTimeMillis()
        val etat = pts.getOrNull(2) ?: key
        val date = Date(ts)
        val name = "${idx}_${sdfShort.format(date)}_${etat}_${sdfFull.format(date)}"
        Log.d(CAPTURE_TAG, "  image[$idx] name=$name  creationTs=$ts")
        return name
    }

    fun mapRawToNamed(raw: List<Pair<String, ImageBitmap>>): List<Pair<ImageBitmap, String>> =
        raw.mapIndexed { idx, (k, bmp) -> bmp to buildImageName(idx, k) }

    // ── Canonical key builder ─────────────────────────────────────────────────
    //
    // Returns the ordered key list derived from `allBons` **at the instant it is
    // called** — i.e. after Compose has already applied FastAdd's state update.
    // Passing this snapshot to captureAllWithScroll() makes sorting immune to the
    // stale e.index problem: new items are at positions 0,1 in allBons (newest
    // first), so they will be at positions [0] and [1] in the output regardless
    // of what index values DisposableEffect happened to register.

    fun buildOrderedKeys(): List<String> =
        allBons.map { b -> "${b.creationTimestamps}|${b.keyID}|${b.etateActuellementEst.name}" }

    // ── Shared capture logic ──────────────────────────────────────────────────
    //
    // Used by lenceTestActive and vm.captureRequested paths — no FastAdd involved,
    // so orderedKeys still ensures correct ordering even if the list changed since
    // the last composition.

    suspend fun runCapture() {
        kotlinx.coroutines.delay(200)
        val orderedKeys = buildOrderedKeys()   // snapshot the authoritative order
        val raw: List<Pair<String, ImageBitmap>> = ctrl.captureAllWithScroll(
            state          = listState,
            totalItemCount = allBons.size,
            scrollSettleMs = 150,
            restoreIndex   = 0,
            orderedKeys    = orderedKeys,
        )
        Log.d(CAPTURE_TAG, "runCapture: ${raw.size} images")
        captured = mapRawToNamed(raw)
        if (captured.isNotEmpty()) showDlg = true
    }

    LaunchedEffect(lenceTestActive) {
        if (!lenceTestActive) return@LaunchedEffect
        runCapture()
    }

    LaunchedEffect(vm.captureRequested) {
        if (!vm.captureRequested) return@LaunchedEffect
        runCapture()
        vm.captureRequested = false
    }

    // ── FastAdd capture ───────────────────────────────────────────────────────
    //
    // Root cause of the ordering bug (see CapturableLayer_FINAL.kt for full
    // analysis):
    //   • allBons recomposes → new items at index 0,1 (sortedByDescending).
    //   • This LaunchedEffect fires BEFORE DisposableEffect re-registers existing
    //     items with their shifted indices (2,3,4 …).
    //   • Therefore e.index in entries is stale (still 0,1,2 for old items).
    //   • Sorting by e.index puts old items first → Credit(Apr30) lands at [0].
    //
    // Fix: snapshot orderedKeys from allBons RIGHT HERE, at the start of the
    // coroutine body.  At this point Compose has already updated allBons (new items
    // are at positions 0 and 1).  captureAllWithScroll sorts by orderedKeys position,
    // not by the stale e.index — new items are guaranteed to appear at [0] and [1].

    LaunchedEffect(fastAddCaptureVersion) {
        if (fastAddCaptureVersion == 0) return@LaunchedEffect

        // ── Snapshot the authoritative order BEFORE any scroll ─────────────────
        val orderedKeys = buildOrderedKeys()

        Log.d(CAPTURE_TAG, "=== FastAdd capture triggered (version=$fastAddCaptureVersion) ===")
        Log.d(CAPTURE_TAG, "allBons.size (expected) = ${allBons.size}")
        Log.d(CAPTURE_TAG, "ctrl registered BEFORE capture = ${ctrl.registeredKeys().size}")
        Log.d(CAPTURE_TAG, "orderedKeys = $orderedKeys")

        // captureAllWithScroll handles all scrolling internally — do NOT call
        // scrollToItem(0) here.  Doing so before the capture causes items that
        // were visible at the bottom to unregister (LazyColumn disposes off-screen
        // composables), leaving the controller with only the top-N visible items
        // and producing the 4/10 bug observed in the logs.
        val raw: List<Pair<String, ImageBitmap>> = ctrl.captureAllWithScroll(
            state          = listState,
            totalItemCount = allBons.size,
            scrollSettleMs = 300,
            restoreIndex   = 0,
            orderedKeys    = orderedKeys,   // ← THE FIX
        )

        Log.d(CAPTURE_TAG, "captureAllWithScroll() returned ${raw.size} image(s)")
        captured = mapRawToNamed(raw)
        if (captured.isNotEmpty()) showDlg = true
    }

    Box {
        Column(
            modifier = modifier
                .semantics(mergeDescendants = true) {
                    set(value = allBons, key = SemanticsPropertyKey("allBons"))
                    set(value = allBons.filter {
                        it.keyID == Targted_Bon
                    }, key = SemanticsPropertyKey("Targted_Bon"))
                    set(value = allBons.filter {
                        it.parent_M2Client_KeyID == FAKE_CLIENT_KEY
                    }, key = SemanticsPropertyKey("FAKE_CLIENT_KEY"))
                }
                .fillMaxSize()
        ) {
            if (allBons.isEmpty()) {
                Text(
                    "لا توجد حالة دين جديدة",
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(16.dp),
                )
            } else LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // itemsIndexed so each item knows its position.
                // index is registered in ctrl to enable captureAllWithScroll scrolling.
                // NOTE: after FastAdd, index shifts — do not rely on it for output
                //       ordering; orderedKeys is the source of truth.
                itemsIndexed(allBons, key = { _, b -> b.keyID }) { index, b ->
                    val cap = rememberCapturableLayer()
                    val capKey = "${b.creationTimestamps}|${b.keyID}|${b.etateActuellementEst.name}"

                    DisposableEffect(capKey) {
                        Log.d(CAPTURE_TAG, "REGISTER   key=$capKey  index=$index  (ctrl.size=${ctrl.registeredKeys().size + 1})")
                        ctrl.register(
                            key        = capKey,
                            index      = index,
                            hasBeenDrawn = cap.hasBeenDrawn,
                            capture    = { cap.capture() },
                        )
                        onDispose {
                            Log.d(CAPTURE_TAG, "UNREGISTER key=$capKey  (ctrl.size=${ctrl.registeredKeys().size - 1})")
                            ctrl.unregister(capKey)
                        }
                    }

                    Box(modifier = cap.modifier) {
                        when {
                            b.etateActuellementEst == M8BonVent.EtateActuellementEst.New_Situation_Credit -> {
                                Situation_Card_ItemView(
                                    allBonVentList = allBons,
                                    relative_M8BonVent = b,
                                    onUpdate = { scope.launch { vm.update_M8(it) } },
                                    onDelete = {
                                        scope.launch {
                                            appDatabase.dao_M8BonVent().deleteByKeyId(it.keyID)
                                        }
                                    },
                                )
                            }

                            b.etateActuellementEst.credit_type -> {
                                Y_Credit_And_Versement_ItemView(
                                    allBonVentList = allBons,
                                    relative_M8BonVent = b,
                                    onUpdate = { scope.launch { vm.update_M8(it) } },
                                    onDelete = {
                                        scope.launch {
                                            appDatabase.dao_M8BonVent().deleteByKeyId(it.keyID)
                                        }
                                    },
                                )
                            }

                            else -> {
                                Affiche_NonCredit_Etate(
                                    allBonVentList = allBons,
                                    relative_M8BonVent = b,
                                    onUpdate = { scope.launch { vm.update_M8(it) } },
                                    onDelete = {
                                        scope.launch {
                                            appDatabase.dao_M8BonVent().deleteByKeyId(it.keyID)
                                        }
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }

        Floating_Separated_Button(
            onClick_Lence_Capture = onLenceCapture,
            vm = vm,
        )

        A_FastAdd_FloatingSeparated_Button_1(
            relative_M2Client = relative_M2Client,
            bons = allBons,
        ) { bon1, bon2 ->
            Log.d(CAPTURE_TAG, "FastAdd commit: bon1.keyID=${bon1.keyID} etat=${bon1.etateActuellementEst.name}")
            Log.d(CAPTURE_TAG, "FastAdd commit: bon2.keyID=${bon2.keyID} etat=${bon2.etateActuellementEst.name}")
            Log.d(CAPTURE_TAG, "allBonVentList.size before add = ${allBons.size}")
            val updated = allBons.toMutableList().also {
                it.add(0, bon2)   // versement  → index 1 (second newest)
                it.add(0, bon1)   // new credit → index 0 (newest)
            }
            vm.active_Datas.list_M8bon = updated
            Log.d(CAPTURE_TAG, "allBonVentList.size after add = ${updated.size}  — triggering fastAddCaptureVersion++")
            /* vm.add_New_M8BonVent(bon1)
               vm.add_New_M8BonVent(bon2)      */
            fastAddCaptureVersion++
        }
    }

    if (showDlg && captured.isNotEmpty()) {
        Afficheur_locale_Image_Captured(
            capturedBitmaps = captured,
            onDismiss = {
                showDlg = false
                captured = emptyList()
                onClick_Lence_Capture()
            },
            onSave = { bmpList ->
                relative_M2Client?.let {
                    saveAllToMediaStore(
                        bitmaps   = bmpList,
                        context   = context,
                        clientKeyID = it.keyID,
                    )
                }
                showDlg = false
                captured = emptyList()
                onClick_Lence_Capture()
            },
        )
    }
}
