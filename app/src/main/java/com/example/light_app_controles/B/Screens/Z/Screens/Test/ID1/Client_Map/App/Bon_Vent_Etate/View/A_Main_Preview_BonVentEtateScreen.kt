package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View

import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.light_app_controles.Modules.Base.SQL.Daos.AppDatabase
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val FAKE_CLIENT_KEY = "-OWI8JQlhGjA_HzMCGFD"
const val Targted_Bon = "-OrVHbH6u_C6TT153tUR"

val fake_new_sit = M8BonVent(         //<--
//TODO(1): pk ca ne s affiche pas 
    parent_M2Client_KeyID = FAKE_CLIENT_KEY,
    montant_principale_du_type = 10890.00,
    creationTimestamps = System.currentTimeMillis() + 1_000L,
    etateActuellementEst = M8BonVent.EtateActuellementEst.New_Situation_Credit
)

private val CREDIT_VERSEMENT_STATES = setOf(
    M8BonVent.EtateActuellementEst.COMMANDE_LIVRAI,

    M8BonVent.EtateActuellementEst.Versemment,
    M8BonVent.EtateActuellementEst.Credit,
    M8BonVent.EtateActuellementEst.Cette_Transaction_Type_Est_Credit,
    M8BonVent.EtateActuellementEst.Demande_Versemet,
    M8BonVent.EtateActuellementEst.New_Situation_Credit,
)

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
            initializer { A_ViewModel(context = context, appDatabase = appDatabase) }
        }
    )

    val active_Datas = vm.active_Datas

    val allBonVentList: List<M8BonVent> =
        active_Datas.list_M8bon ?: emptyList()

    var lenceCaptureActive by remember { mutableStateOf(false) }
    val onLenceCapture: () -> Unit = { lenceCaptureActive = !lenceCaptureActive }

    val scope = rememberCoroutineScope()
    val ctrl = rememberMultiCaptureController()

    var captured by remember { mutableStateOf<List<Pair<ImageBitmap, String>>>(emptyList()) }
    var showDlg by remember { mutableStateOf(false) }

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

    val sameClientPeriod: (M8BonVent) -> Boolean = { b ->
        b.parent_M2Client_KeyID == relative_M2Client?.keyID
    }
    val credit_affichage = allBonVentList
        .filter { sameClientPeriod(it) && it.etateActuellementEst in CREDIT_VERSEMENT_STATES }
        .sortedByDescending { it.creationTimestamps }

    val allBons: List<M8BonVent> = (credit_affichage)
        .sortedByDescending { it.creationTimestamps }

    Box() {
        Column(
            modifier = modifier
                .semantics(mergeDescendants = true) {
                    set(value = allBons, key = SemanticsPropertyKey("allBons"))
                    set(value = allBonVentList.filter {
                        it.keyID == Targted_Bon
                    }, key = SemanticsPropertyKey("Targted_Bon"))
                    set(value = allBonVentList.filter {
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(allBons, key = { it.keyID }) { b ->
                    val cap = rememberCapturableLayer()
                    val capKey = "${b.creationTimestamps}|${b.keyID}|${b.etateActuellementEst.name}"

                    DisposableEffect(capKey) {
                        ctrl.register(capKey) { cap.capture() }
                        onDispose { ctrl.unregister(capKey) }
                    }

                    Box(modifier = cap.modifier) {
                        when {
                            b.etateActuellementEst == M8BonVent.EtateActuellementEst.New_Situation_Credit -> {
                                Situation_Card_ItemView(
                                    allBonVentList = allBonVentList,
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
                                    allBonVentList = allBonVentList,
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
                                    allBonVentList = allBonVentList,
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
            vm = vm,
        )
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
