package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
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
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID2.Afficheur_locale_Image_Captured
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID2.rememberCapturableLayer
import com.example.light_app_controles.Modules.Base.SQL.Daos.AppDatabase
import com.example.light_app_controles.R
// FIX: removed wrong protobuf import `com.google.protobuf.LazyStringArrayList.emptyList`.
//      Kotlin stdlib's emptyList() needs no import.
import kotlinx.coroutines.launch

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
) {                 //<--
//TODO(1): fait passe chanque item au capture layer pour cree son image 
    val captureState = rememberCapturableLayer(backgroundRes = R.drawable.logo)
    val scope = rememberCoroutineScope()

    var capturedBitmap: ImageBitmap? by remember { mutableStateOf(null) }
    var showCapturedDialog by remember { mutableStateOf(false) }

    LaunchedEffect(lenceTestActive) {
        if (lenceTestActive) {
            // Small delay to ensure UI is fully rendered before capture
            kotlinx.coroutines.delay(100)
            capturedBitmap = captureState.capture()
            showCapturedDialog = true
        }
    }

    val relative = fake_allBonVentList
        .filter { it.parent_M2Client_KeyID == parentClientKeyID && it.parent_M14VentPeriod_KeyId == parentPeriodKeyID && it.etateActuellementEst == M8BonVent.EtateActuellementEst.New_Situation_Credit }
        .maxByOrNull { it.creationTimestamps }

    Column(modifier = modifier.fillMaxSize().then(captureState.modifier)) {
        if (relative == null) {
            Text("لا توجد حالة دين جديدة", color = Color.Gray, fontWeight = FontWeight.Medium, modifier = Modifier.padding(16.dp))
            return
        }

        LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = fake_allBonVentList.filter {
                    it.parent_M2Client_KeyID == parentClientKeyID &&
                    it.parent_M14VentPeriod_KeyId == parentPeriodKeyID &&
                    it.etateActuellementEst == M8BonVent.EtateActuellementEst.New_Situation_Credit
                },
                key = { it.keyID }
            ) { bon ->
                Situation_Card_ItemView(
                    allBonVentList = fake_allBonVentList,
                    relative_M8BonVent = bon,
                    onUpdate = { scope.launch { appDatabase.dao_M8BonVent().upsert(it) } },
                    onDelete = { scope.launch { appDatabase.dao_M8BonVent().deleteByKeyId(it.keyID) } },
                )
            }

            items(
                items = fake_allBonVentList.filter {
                    it.parent_M2Client_KeyID == parentClientKeyID &&
                    it.parent_M14VentPeriod_KeyId == parentPeriodKeyID &&
                    it.etateActuellementEst in listOf(
                        M8BonVent.EtateActuellementEst.Versemment,
                        M8BonVent.EtateActuellementEst.Credit,
                        M8BonVent.EtateActuellementEst.Cette_Transaction_Type_Est_Credit,
                        M8BonVent.EtateActuellementEst.Demande_Versemet,
                    )
                },
                key = { it.keyID }
            ) { bon ->
                Y_Credit_And_Versement_ItemView(
                    allBonVentList = fake_allBonVentList,
                    relative_M8BonVent = bon,
                    onUpdate = { scope.launch { appDatabase.dao_M8BonVent().upsert(it) } },
                    onDelete = { scope.launch { appDatabase.dao_M8BonVent().deleteByKeyId(it.keyID) } },
                )
            }
        }
    }

    if (showCapturedDialog && capturedBitmap != null) {
        Afficheur_locale_Image_Captured(
            capturedBitmap = capturedBitmap!!,
            onDismiss = {
                showCapturedDialog = false
                capturedBitmap = null
                onClick_Lence_Capture()
            },
            onSave = { androidBitmap ->
                saveToMediaStore(androidBitmap, context, parentClientKeyID)
                showCapturedDialog = false
                capturedBitmap = null
                onClick_Lence_Capture()
            }
        )
    }
}

fun saveToMediaStore(bitmap: Bitmap, context: Context, clientKeyID: String) {
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "$clientKeyID.webp")
        put(MediaStore.Images.Media.MIME_TYPE, "image/webp")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "Download/Image_Compose_Screen")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
    }
    val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    else MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    val resolver = context.contentResolver
    val uri = resolver.insert(collection, contentValues) ?: return
    resolver.openOutputStream(uri)?.use { out ->
        val format = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) Bitmap.CompressFormat.WEBP_LOSSLESS
                     else @Suppress("DEPRECATION") Bitmap.CompressFormat.WEBP
        bitmap.compress(format, 100, out)
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        contentValues.clear()
        contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
        resolver.update(uri, contentValues, null, null)
    }
}
