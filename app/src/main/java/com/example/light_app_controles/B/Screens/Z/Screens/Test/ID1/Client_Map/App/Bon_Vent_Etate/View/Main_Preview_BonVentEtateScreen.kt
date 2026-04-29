package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.rememberGraphicsLayer
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
import java.io.File

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
            Text(text = "لا توجد حالة دين جديدة", color = Color.Companion.Gray, fontWeight = FontWeight.Companion.Medium, modifier = Modifier.Companion.padding(16.dp))
            return
        }

        LazyColumn(
            modifier = Modifier.Companion.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
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
                onClick_Lence_Test()
            },
            onSave = { androidBitmap ->
                saveComposableAsWebP(androidBitmap, context)
            }
        )
    }
}

fun saveComposableAsWebP(bitmap: Bitmap, context: Context, fileName: String = "snapshot.webp") {
    val file = File(context.filesDir, fileName)
    file.outputStream().use { out ->
        bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSLESS, 100, out)
    }
}
