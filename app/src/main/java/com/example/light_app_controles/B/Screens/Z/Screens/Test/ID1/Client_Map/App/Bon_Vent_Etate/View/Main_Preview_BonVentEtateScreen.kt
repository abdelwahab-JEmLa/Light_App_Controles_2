package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
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

    val situationBons = fake_allBonVentList
        .filter {
            it.parent_M2Client_KeyID == parentClientKeyID &&
                    it.parent_M14VentPeriod_KeyId == parentPeriodKeyID &&
                    it.etateActuellementEst == M8BonVent.EtateActuellementEst.New_Situation_Credit
        }
        .sortedByDescending { it.creationTimestamps }

    val creditVersementBons = fake_allBonVentList
        .filter {
            it.parent_M2Client_KeyID == parentClientKeyID &&
                    it.parent_M14VentPeriod_KeyId == parentPeriodKeyID &&
                    it.etateActuellementEst in listOf(
                M8BonVent.EtateActuellementEst.Versemment,
                M8BonVent.EtateActuellementEst.Credit,
                M8BonVent.EtateActuellementEst.Cette_Transaction_Type_Est_Credit,
                M8BonVent.EtateActuellementEst.Demande_Versemet,
            )
        }
        .sortedByDescending { it.creationTimestamps }

    val relative = situationBons.maxByOrNull { it.creationTimestamps }

    LaunchedEffect(lenceTestActive) {
        if (!lenceTestActive) return@LaunchedEffect

        kotlinx.coroutines.delay(200)

        val allBons = situationBons + creditVersementBons
        val rawBitmaps: List<ImageBitmap> = multiCaptureController.captureAll()

        val sdf = SimpleDateFormat("MMdd_HHmmss", Locale.getDefault())
        capturedBitmaps = rawBitmaps.mapIndexed { i, bmp ->
            val bon = allBons.getOrNull(i)
            val dateStr = sdf.format(Date(bon?.creationTimestamps ?: System.currentTimeMillis()))
            val typeName = bon?.etateActuellementEst?.name ?: "item_$i"
            bmp to "${dateStr}_${typeName}"
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
            items(situationBons, key = { it.keyID }) { bon ->
                CapturableItem(
                    itemKey = bon.keyID,
                    controller = multiCaptureController,
                ) { captureMod ->
                    Box(modifier = captureMod) {
                        Situation_Card_ItemView(
                            allBonVentList = fake_allBonVentList,
                            relative_M8BonVent = bon,
                            onUpdate = { scope.launch { appDatabase.dao_M8BonVent().upsert(it) } },
                            onDelete = { scope.launch { appDatabase.dao_M8BonVent().deleteByKeyId(it.keyID) } },
                        )
                    }
                }
            }

            items(creditVersementBons, key = { it.keyID }) { bon ->
                CapturableItem(
                    itemKey = bon.keyID,
                    controller = multiCaptureController,
                ) { captureMod ->
                    Box(modifier = captureMod) {
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
                saveAllToMediaStore(
                    bitmaps = bitmapList,
                    context = context,
                    clientKeyID = parentClientKeyID,
                )
                showCapturedDialog = false
                capturedBitmaps = emptyList()
                onClick_Lence_Capture()
            },
        )
    }
}

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

fun saveToMediaStore(bitmap: Bitmap, context: Context, clientKeyID: String) {
    saveAllToMediaStore(listOf(bitmap to "single"), context, clientKeyID)
}
