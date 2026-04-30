package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.Z.Modules.Capture.Afficheur_locale_Image_Captured
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.Z.Modules.Capture.rememberCapturableLayer
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.Z.Modules.Capture.rememberMultiCaptureController
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.Z.Modules.Capture.saveAllToMediaStore
import com.example.light_app_controles.Modules.Base.SQL.Daos.AppDatabase
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val CREDIT_VERSEMENT_STATES = setOf(
    M8BonVent.EtateActuellementEst.Versemment,
    M8BonVent.EtateActuellementEst.Credit,
    M8BonVent.EtateActuellementEst.Cette_Transaction_Type_Est_Credit,
    M8BonVent.EtateActuellementEst.Demande_Versemet,
)

@Composable
fun Main_Preview_BonVentEtateScreen(
    context: Context = LocalContext.current,
    appDatabase: AppDatabase = AppDatabase.DatabaseModule.getDatabase(context),
    fake_allBonVentList: List<M8BonVent> = FAKE_ALL_BONS,
    parentClientKeyID: String = FAKE_CLIENT_KEY,
    parentPeriodKeyID: String = FAKE_PERIOD_KEY,
    modifier: Modifier = Modifier,
    onClick_Lence_Capture: () -> Unit = {},
    lenceTestActive: Boolean = false,
) {
    val scope = rememberCoroutineScope()
    val ctrl = rememberMultiCaptureController()

    var captured by remember { mutableStateOf<List<Pair<ImageBitmap, String>>>(emptyList()) }
    var showDlg by remember { mutableStateOf(false) }

    val sameClientPeriod: (M8BonVent) -> Boolean = { b ->
        b.parent_M2Client_KeyID == parentClientKeyID &&
                b.parent_M14VentPeriod_KeyId == parentPeriodKeyID
    }

    val sitBons = fake_allBonVentList
        .filter { sameClientPeriod(it) && it.etateActuellementEst == M8BonVent.EtateActuellementEst.New_Situation_Credit }
        .sortedByDescending { it.creationTimestamps }

    val cvBons = fake_allBonVentList
        .filter { sameClientPeriod(it) && it.etateActuellementEst in CREDIT_VERSEMENT_STATES }
        .sortedByDescending { it.creationTimestamps }

    val allBons: List<M8BonVent> = sitBons + cvBons
    val latestSit = sitBons.maxByOrNull { it.creationTimestamps }

    LaunchedEffect(lenceTestActive) {
        if (!lenceTestActive) return@LaunchedEffect

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

    Column(modifier = modifier.fillMaxSize()) {
        if (latestSit == null) {
            Text(
                "لا توجد حالة دين جديدة",
                color = Color.Gray,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(16.dp),
            )
            return
        }

        LazyColumn(
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
                    onDispose {
                        ctrl.unregister(capKey)
                    }
                }

                Box(modifier = cap.modifier) {
                    if (b.etateActuellementEst == M8BonVent.EtateActuellementEst.New_Situation_Credit) {
                        Situation_Card_ItemView(
                            allBonVentList = fake_allBonVentList,
                            relative_M8BonVent = b,
                            onUpdate = { scope.launch { appDatabase.dao_M8BonVent().upsert(it) } },
                            onDelete = { scope.launch { appDatabase.dao_M8BonVent().deleteByKeyId(it.keyID) } },
                        )
                    } else {
                        Y_Credit_And_Versement_ItemView(
                            allBonVentList = fake_allBonVentList,
                            relative_M8BonVent = b,
                            onUpdate = { scope.launch { appDatabase.dao_M8BonVent().upsert(it) } },
                            onDelete = { scope.launch { appDatabase.dao_M8BonVent().deleteByKeyId(it.keyID) } },
                        )
                    }
                }
            }
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
                saveAllToMediaStore(
                    bitmaps = bmpList,
                    context = context,
                    clientKeyID = parentClientKeyID,
                )
                showDlg = false
                captured = emptyList()
                onClick_Lence_Capture()
            },
        )
    }
}
