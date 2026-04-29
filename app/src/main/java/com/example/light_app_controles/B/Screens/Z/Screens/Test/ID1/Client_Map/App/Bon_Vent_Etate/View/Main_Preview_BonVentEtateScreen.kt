package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View

import android.content.Context
import android.graphics.Bitmap
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.light_app_controles.Modules.Base.SQL.Daos.AppDatabase
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** States that belong to the "credit / versement" section of the list. */
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
    val multiCaptureController = rememberMultiCaptureController()

    var capturedBitmaps by remember { mutableStateOf<List<Pair<ImageBitmap, String>>>(emptyList()) }
    var showCapturedDialog by remember { mutableStateOf(false) }

    val isForThisClientPeriod: (M8BonVent) -> Boolean = { bon ->
        bon.parent_M2Client_KeyID == parentClientKeyID &&
                bon.parent_M14VentPeriod_KeyId == parentPeriodKeyID
    }

    val situationBons = fake_allBonVentList
        .filter { isForThisClientPeriod(it) && it.etateActuellementEst == M8BonVent.EtateActuellementEst.New_Situation_Credit }
        .sortedByDescending { it.creationTimestamps }

    val creditVersementBons = fake_allBonVentList
        .filter { isForThisClientPeriod(it) && it.etateActuellementEst in CREDIT_VERSEMENT_STATES }
        .sortedByDescending { it.creationTimestamps }

    val allDisplayBons: List<M8BonVent> = situationBons + creditVersementBons

    val relative = situationBons.maxByOrNull { it.creationTimestamps }

    LaunchedEffect(lenceTestActive) {
        if (!lenceTestActive) return@LaunchedEffect

        kotlinx.coroutines.delay(200)

        val rawBitmaps: List<Pair<String, ImageBitmap>> = multiCaptureController.captureAll()
        val sdf = SimpleDateFormat("MMdd_HHmmss_SSS", Locale.getDefault())

        capturedBitmaps = rawBitmaps.map { (key, bmp) ->
            val parts = key.split("|")           // [ts, keyID, stateName]
            val ts = parts.getOrNull(0)?.toLongOrNull()
            val stateName = parts.getOrNull(2) ?: key
            val dateStr = sdf.format(Date(ts ?: System.currentTimeMillis()))
            bmp to "${dateStr}_${stateName}"
        }

        if (capturedBitmaps.isNotEmpty()) showCapturedDialog = true
    }

    Column(modifier = modifier.fillMaxSize()) {
        if (relative == null) {
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
            items(allDisplayBons, key = { it.keyID }) { bon ->
                val capturableLayer = rememberCapturableLayer()
                val captureKey = "${bon.creationTimestamps}|${bon.keyID}|${bon.etateActuellementEst.name}"

                DisposableEffect(captureKey) {
                    multiCaptureController.register(captureKey) { capturableLayer.capture() }
                    android.util.Log.d("CaptureLayer", "✅ Image registered  → $captureKey")
                    onDispose {
                        multiCaptureController.unregister(captureKey)
                        android.util.Log.d("CaptureLayer", "🗑 Image unregistered → $captureKey")
                    }
                }

                Box(modifier = capturableLayer.modifier) {
                    if (bon.etateActuellementEst == M8BonVent.EtateActuellementEst.New_Situation_Credit) {
                        Situation_Card_ItemView(
                            allBonVentList = fake_allBonVentList,
                            relative_M8BonVent = bon,
                            onUpdate = { scope.launch { appDatabase.dao_M8BonVent().upsert(it) } },
                            onDelete = { scope.launch { appDatabase.dao_M8BonVent().deleteByKeyId(it.keyID) } },
                        )
                    } else {
                        Y_Credit_And_Versement_ItemView(
                            allBonVentList = fake_allBonVentList,
                            relative_M8BonVent = bon,
                            onUpdate = { scope.launch { appDatabase.dao_M8BonVent().upsert(it) } },
                            onDelete = { scope.launch { appDatabase.dao_M8BonVent().deleteByKeyId(it.keyID) } },
                        )
                    }
                }
            }
        }
    }

    if (showCapturedDialog && capturedBitmaps.isNotEmpty()) {
        Afficheur_locale_Image_Captured(
            capturedBitmaps = capturedBitmaps,
            onDismiss = {
                showCapturedDialog = false
                capturedBitmaps = emptyList()
                onClick_Lence_Capture()
            },
            onSave = { bitmapList ->
                android.util.Log.d("CaptureLayer", "💾 Saving ${bitmapList.size} image(s):")
                bitmapList.forEachIndexed { i, (_, label) ->
                    android.util.Log.d("CaptureLayer", "   [${i + 1}] image_${label}.webp")
                }
                saveAllToMediaStore(
                    bitmaps = bitmapList,
                    context = context,
                    clientKeyID = parentClientKeyID,
                )
                android.util.Log.d("CaptureLayer", "✅ Save complete — folder: Download/Image_Compose_Screen/$parentClientKeyID")
                showCapturedDialog = false
                capturedBitmaps = emptyList()
                onClick_Lence_Capture()
            },
        )
    }
}
