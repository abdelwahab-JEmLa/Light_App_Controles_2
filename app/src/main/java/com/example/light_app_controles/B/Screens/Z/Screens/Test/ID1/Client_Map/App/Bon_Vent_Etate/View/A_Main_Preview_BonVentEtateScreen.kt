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
import androidx.compose.foundation.lazy.items
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
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.Z.Modules.Capture.rememberCapturableLayer
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.Z.Modules.Capture.rememberMultiCaptureController
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.Z.Modules.Capture.saveAllToMediaStore
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
    // Incremented each time the fast-add button commits, triggering a last-8 capture.
    var fastAddCaptureVersion by remember { mutableStateOf(0) }
    // Controls the LazyColumn scroll position — used to scroll to top before capture.
    val listState = rememberLazyListState()

    // ── Shared capture logic ──────────────────────────────────────────────────
    suspend fun runCapture() {
        kotlinx.coroutines.delay(200)
        val raw: List<Pair<String, ImageBitmap>> = ctrl.captureAll()
        val sdf = SimpleDateFormat("MMdd_HHmmss_SSS", Locale.getDefault())
        captured = raw.map { (k, bmp) ->
            val pts = k.split("|")
            val ts = pts.getOrNull(0)?.toLongOrNull()
            val st = pts.getOrNull(2) ?: k
            val ds = sdf.format(Date(ts ?: System.currentTimeMillis()))
            bmp to "${ds}_${st}"
        }
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

    LaunchedEffect(fastAddCaptureVersion) {
        if (fastAddCaptureVersion == 0) return@LaunchedEffect

        Log.d(CAPTURE_TAG, "=== FastAdd capture triggered (version=$fastAddCaptureVersion) ===")
        Log.d(CAPTURE_TAG, "allBons.size (expected) = ${allBons.size}")
        Log.d(CAPTURE_TAG, "ctrl registered BEFORE scroll = ${ctrl.registeredKeys().size}")

        // Scroll to index 0 — the 2 new bons are at the top (newest-first sort).
        listState.scrollToItem(0)
        Log.d(CAPTURE_TAG, "scrollToItem(0) done — waiting for composition…")

        // Wait for LazyColumn to compose the newly visible items and run their DisposableEffect.
        kotlinx.coroutines.delay(300)

        val registeredAfter = ctrl.registeredKeys()
        Log.d(CAPTURE_TAG, "ctrl registered AFTER scroll+delay = ${registeredAfter.size}")
        registeredAfter.forEachIndexed { i, k -> Log.d(CAPTURE_TAG, "  [after][$i] key=$k") }

        // Confirm the 2 new bons are now present.
        val registeredKeyIds = registeredAfter.map { it.split("|").getOrNull(1) ?: "" }.toSet()
        val newBonIds = listOf(allBons.getOrNull(0)?.keyID, allBons.getOrNull(1)?.keyID)
        newBonIds.forEach { id ->
            if (id != null && id in registeredKeyIds)
                Log.d(CAPTURE_TAG, "✅ new bon $id IS registered — will be captured")
            else
                Log.w(CAPTURE_TAG, "⚠️ new bon $id NOT registered even after scroll")
        }

        val sdf = SimpleDateFormat("MMdd_HHmmss_SSS", Locale.getDefault())
        val raw: List<Pair<String, ImageBitmap>> = ctrl.captureAll()

        Log.d(CAPTURE_TAG, "captureAll() returned ${raw.size} image(s) (registered ${registeredAfter.size})")

        captured = raw.map { (k, bmp) ->
            val pts = k.split("|")
            val ts  = pts.getOrNull(0)?.toLongOrNull()
            val st  = pts.getOrNull(2) ?: k
            val ds  = sdf.format(Date(ts ?: System.currentTimeMillis()))
            bmp to "${ds}_${st}"
        }
        if (captured.isNotEmpty()) showDlg = true
    }




    Box() {
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
                .fillMaxSize()) {
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
                items(allBons, key = { it.keyID }) { b ->
                    val cap = rememberCapturableLayer()
                    val capKey = "${b.creationTimestamps}|${b.keyID}|${b.etateActuellementEst.name}"

                    DisposableEffect(capKey) {
                        Log.d(CAPTURE_TAG, "REGISTER   key=$capKey  (ctrl.size=${ctrl.registeredKeys().size + 1})")
                        ctrl.register(capKey) { cap.capture() }
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
                                    onUpdate = {
                                        scope.launch {
                                            vm.update_M8(it)
                                        }
                                    },
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
                                    onUpdate = {
                                        scope.launch {
                                            vm.update_M8(it)
                                        }
                                    },
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
                                    onUpdate = {
                                        scope.launch {
                                            vm.update_M8(it)
                                        }
                                    },
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
                it.add(bon1)
                it.add(bon2)
            }
            vm.active_Datas.list_M8bon = updated
            Log.d(CAPTURE_TAG, "allBonVentList.size after add = ${updated.size}  — triggering fastAddCaptureVersion++")
            // Persist both bons to Room
            vm.add_New_M8BonVent(bon1)
            vm.add_New_M8BonVent(bon2)
            // Trigger a capture of the last 8 items once Compose re-renders
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
                        bitmaps = bmpList,
                        context = context,
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
