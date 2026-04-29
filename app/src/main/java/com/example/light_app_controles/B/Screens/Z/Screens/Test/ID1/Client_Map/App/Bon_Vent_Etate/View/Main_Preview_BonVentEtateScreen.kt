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
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.rememberGraphicsLayer
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID2.Afficheur_locale_Image_Captured
import com.example.light_app_controles.Modules.Base.SQL.Daos.AppDatabase
import com.google.protobuf.LazyStringArrayList.emptyList
import kotlinx.coroutines.launch

@Composable
fun Main_Preview_BonVentEtateScreen(
    context: Context = LocalContext.current,
    appDatabase: AppDatabase = AppDatabase.DatabaseModule.getDatabase(context),
    fake_allBonVentList: List<M8BonVent> = FAKE_ALL_BONS,
    parentClientKeyID: String = FAKE_CLIENT_KEY,
    parentPeriodKeyID: String = FAKE_PERIOD_KEY,
    modifier: Modifier = Modifier.Companion,
    onClick_Lence_Test: () -> Unit = {},
    lenceTestActive: Boolean = false,
) {
    val graphicsLayer = rememberGraphicsLayer()
    val scope = rememberCoroutineScope()
    val collectAsState = appDatabase.dao_M8BonVent().getAllFlow().collectAsState(initial = emptyList())

    var capturedBitmap: ImageBitmap? by remember { mutableStateOf(null) }
    var showCapturedDialog by remember { mutableStateOf(false) }

    // TODO(1) FIXED: capture the composable with rememberGraphicsLayer, hold it in state,
    // then show the dialog. On Save the dialog calls saveToMediaStore which writes the
    // bitmap to Downloads/Image_Compose_Screen/{clientKey}.webp via MediaStore.
    LaunchedEffect(lenceTestActive) {
        if (lenceTestActive) {
            val bitmap = graphicsLayer.toImageBitmap()
            capturedBitmap = bitmap
            showCapturedDialog = true
        }
    }

    val relative: M8BonVent? = fake_allBonVentList
        .filter { bon ->
            bon.parent_M2Client_KeyID == parentClientKeyID &&
                    bon.parent_M14VentPeriod_KeyId == parentPeriodKeyID &&
                    bon.etateActuellementEst == M8BonVent.EtateActuellementEst.New_Situation_Credit
        }
        .maxByOrNull { it.creationTimestamps }

    Column(
        modifier = modifier
            .fillMaxSize()
            .drawWithContent {
                graphicsLayer.record { this@drawWithContent.drawContent() }
                drawLayer(graphicsLayer)
            }
    ) {
        if (relative == null) {
            Text(
                text = "لا توجد حالة دين جديدة",
                color = Color.Companion.Gray,
                fontWeight = FontWeight.Companion.Medium,
                modifier = Modifier.Companion.padding(16.dp)
            )
            return
        }

        LazyColumn(
            modifier = Modifier.Companion
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = fake_allBonVentList.filter { bon ->
                    bon.parent_M2Client_KeyID == parentClientKeyID &&
                            bon.parent_M14VentPeriod_KeyId == parentPeriodKeyID &&
                            bon.etateActuellementEst == M8BonVent.EtateActuellementEst.New_Situation_Credit
                },
                key = { it.keyID }
            ) { bon ->
                Situation_Card_ItemView(
                    allBonVentList = fake_allBonVentList,
                    relative_M8BonVent = bon,
                    onUpdate = { updatedBon ->
                        scope.launch { appDatabase.dao_M8BonVent().upsert(updatedBon) }
                    },
                    onDelete = { bonToDelete ->
                        scope.launch { appDatabase.dao_M8BonVent().deleteByKeyId(bonToDelete.keyID) }
                    },
                )
            }

            items(
                items = fake_allBonVentList.filter { bon ->
                    bon.parent_M2Client_KeyID == parentClientKeyID &&
                            bon.parent_M14VentPeriod_KeyId == parentPeriodKeyID &&
                            bon.etateActuellementEst in listOf(
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
                    onUpdate = { updatedBon ->
                        scope.launch { appDatabase.dao_M8BonVent().upsert(updatedBon) }
                    },
                    onDelete = { bonToDelete ->
                        scope.launch { appDatabase.dao_M8BonVent().deleteByKeyId(bonToDelete.keyID) }
                    },
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
                onClick_Lence_Test()
            },
            onSave = { androidBitmap ->
                // TODO(1) FIXED: save via MediaStore to Downloads/Image_Compose_Screen/{clientKey}.webp
                saveToMediaStore(
                    bitmap = androidBitmap,
                    context = context,
                    clientKeyID = parentClientKeyID,
                )
                showCapturedDialog = false
                capturedBitmap = null
                onClick_Lence_Test()
            }
        )
    }
}

/**
 * Saves [bitmap] as a lossless WebP file to the public Downloads folder under
 * Downloads/Image_Compose_Screen/{clientKeyID}.webp using MediaStore.
 *
 * Requires READ/WRITE_EXTERNAL_STORAGE on API < 29, or just
 * READ_EXTERNAL_STORAGE on API 29+ (scoped storage handles writes automatically).
 */
fun saveToMediaStore(
    bitmap: Bitmap,
    context: Context,
    clientKeyID: String,
) {
    val fileName = "$clientKeyID.webp"
    val mimeType = "image/webp"
    val relativePath = "Download/Image_Compose_Screen"

    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        put(MediaStore.Images.Media.MIME_TYPE, mimeType)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Images.Media.RELATIVE_PATH, relativePath)
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
    }

    val resolver = context.contentResolver
    val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    } else {
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    }

    val uri = resolver.insert(collection, contentValues) ?: return

    resolver.openOutputStream(uri)?.use { outputStream ->
        val format = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Bitmap.CompressFormat.WEBP_LOSSLESS
        } else {
            @Suppress("DEPRECATION")
            Bitmap.CompressFormat.WEBP
        }
        bitmap.compress(format, 100, outputStream)
    }

    // Mark the file as no longer pending so it becomes visible in the gallery
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        contentValues.clear()
        contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
        resolver.update(uri, contentValues, null, null)
    }
}
