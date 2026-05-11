package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.a.Screens.a.BonVents.Screen

import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import android.content.ClipData
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FloatingMenu_Plus_RoomCsvBigDatas.Feature.Options.Floating_Separated_Button
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID3.WhatsappSendFolder.Feature.b_FastAdd_FloatingSeparated_Button_1.Actions.A_FastAdd_FloatingSeparated_Button_1
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.Views.Affiche_NonCredit_Etate
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.Views.Situation_Card_ItemView
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.Views.Y_Credit_And_Versement_ItemView
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID2.CaptureListItems.Feature.Capture.Afficheur_locale_Image_Captured
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID2.CaptureListItems.Feature.Capture.rememberCapturableLayer
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID2.CaptureListItems.Feature.Capture.rememberMultiCaptureController
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID2.CaptureListItems.Feature.Capture.saveAllToMediaStore
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.b.Models.M8BonVent
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.Z.preview.FAKE_CLIENT_KEY
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.a.Screens.a.BonVents.Screen.ViewModel.A_ViewModel
import com.example.light_app_controles.Modules.Base.SQL.Daos.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

private const val WA_LOG = "WA_SHARE_FLOW"

@Composable
fun Main_Preview_BonVentEtateScreen(
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current,
    appDatabase: AppDatabase = AppDatabase.DatabaseModule.getDatabase(context),
    onClick_Lence_Capture: () -> Unit = {},
    lenceTestActive: Boolean = false,
    relative_M2Client: M2Client? = M2Client.get_default().copy(
        keyID = FAKE_CLIENT_KEY,
        nom = "Youcef Zohire",
        numTelephone = "+213 542 70 05 75"
    ),
) {
    val vm: A_ViewModel = viewModel(
        factory = viewModelFactory { initializer { A_ViewModel(appDatabase = appDatabase) } }
    )

    val active_Datas = vm.active_Datas
    val allBons: List<M8BonVent> = active_Datas.list_M8bon
        ?.filter { it.parent_M2Client_KeyID == relative_M2Client?.keyID && it.etateActuellementEst in CREDIT_VERSEMENT_STATES }
        ?.sortedByDescending { it.creationTimestamps } ?: emptyList()

    var lenceCaptureActive by remember { mutableStateOf(false) }
    val onLenceCapture: () -> Unit = { lenceCaptureActive = !lenceCaptureActive }

    val scope = rememberCoroutineScope()
    val ctrl = rememberMultiCaptureController()

    var captured by remember { mutableStateOf<List<Pair<ImageBitmap, String>>>(emptyList()) }
    var showDlg by remember { mutableStateOf(false) }
    var fastAddCaptureVersion by remember { mutableStateOf(0) }
    var whatsappSendRequest by remember { mutableStateOf<Pair<String, Boolean>?>(null) }
    val listState = rememberLazyListState()

    val sdfFull  = SimpleDateFormat("MMdd_HHmmss", Locale.getDefault())
    val sdfShort = SimpleDateFormat("mm-ss-SSS",   Locale.getDefault())

    fun buildImageName(idx: Int, key: String): String {
        val pts  = key.split("|")
        val ts   = pts.getOrNull(0)?.toLongOrNull() ?: System.currentTimeMillis()
        val etat = pts.getOrNull(2) ?: key
        val date = Date(ts)
        return "${idx}_${sdfShort.format(date)}_${etat}_${sdfFull.format(date)}"
    }

    fun mapRawToNamed(raw: List<Pair<String, ImageBitmap>>): List<Pair<ImageBitmap, String>> =
        raw.mapIndexed { idx, (k, bmp) -> bmp to buildImageName(idx, k) }

    fun buildOrderedKeys(): List<String> =
        allBons.map { b -> "${b.creationTimestamps}|${b.keyID}|${b.etateActuellementEst.name}" }

    suspend fun runCapture() {
        delay(200)
        val raw = ctrl.captureAllWithScroll(
            state          = listState,
            totalItemCount = allBons.size,
            scrollSettleMs = 150,
            restoreIndex   = 0,
            orderedKeys    = buildOrderedKeys(),
        )
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

    LaunchedEffect(fastAddCaptureVersion) {
        if (fastAddCaptureVersion == 0) return@LaunchedEffect
        val raw = ctrl.captureAllWithScroll(
            state          = listState,
            totalItemCount = allBons.size,
            scrollSettleMs = 300,
            restoreIndex   = 0,
            orderedKeys    = buildOrderedKeys(),
        )
        captured = mapRawToNamed(raw)
        if (captured.isNotEmpty()) showDlg = true
    }

    LaunchedEffect(whatsappSendRequest) {
        val request = whatsappSendRequest ?: run {
            Log.d(WA_LOG, "[0] whatsappSendRequest est null → skip")
            return@LaunchedEffect
        }
        val (phoneNumber, isWhatsAppBusiness) = request
        Log.i(WA_LOG, "[0] ▶ flow démarré | phone=$phoneNumber | business=$isWhatsAppBusiness")

        Log.d(WA_LOG, "[1] capture → allBons.size=${allBons.size} | orderedKeys=${buildOrderedKeys()}")
        val raw = ctrl.captureAllWithScroll(
            state          = listState,
            totalItemCount = allBons.size,
            scrollSettleMs = 300,
            restoreIndex   = 0,
            orderedKeys    = buildOrderedKeys(),
        )
        Log.d(WA_LOG, "[1] captureAllWithScroll terminé → ${raw.size} bitmaps bruts")

        val namedImages = mapRawToNamed(raw)
        Log.d(WA_LOG, "[1] namedImages → ${namedImages.map { it.second }}")

        if (namedImages.isEmpty()) {
            Log.w(WA_LOG, "[1] ✗ aucune image capturée → abandon")
            whatsappSendRequest = null
            return@LaunchedEffect
        }

        // ─────────────────────────────────────────────────────────
        // STEP 2 — sauvegarde MediaStore
        // ─────────────────────────────────────────────────────────
        Log.d(WA_LOG, "[2] saveAllToMediaStore | clientKeyID=${relative_M2Client?.keyID}")
        val savedUris: List<Uri> = withContext(Dispatchers.IO) {
            relative_M2Client?.let {
                saveAllToMediaStore(
                    bitmaps     = namedImages.map { (img, lbl) -> img.asAndroidBitmap() to lbl },
                    context     = context,
                    clientKeyID = it.keyID,
                )
            } ?: emptyList<Uri>().also {
                Log.w(WA_LOG, "[2] ✗ relative_M2Client est null → pas de sauvegarde")
            }
        }
        Log.d(WA_LOG, "[2] savedUris (${savedUris.size}) → ${savedUris.joinToString()}")

        if (savedUris.isEmpty()) {
            Log.w(WA_LOG, "[2] ✗ aucun URI sauvegardé → abandon")
            whatsappSendRequest = null
            return@LaunchedEffect
        }

        // ─────────────────────────────────────────────────────────
        // STEP 3 — résolution du composant WhatsApp
        // ─────────────────────────────────────────────────────────
        // setPackage() échoue pour ACTION_SEND_MULTIPLE car WhatsApp n'expose pas ce
        // handler comme activité résolvable via package filter. On résout donc le
        // ComponentName exact en interrogeant queryIntentActivities avec la bonne action.
        delay(250)
        val packageName = if (isWhatsAppBusiness) "com.whatsapp.w4b" else "com.whatsapp"
        val intentAction = if (savedUris.size == 1) Intent.ACTION_SEND else Intent.ACTION_SEND_MULTIPLE
        Log.d(WA_LOG, "[3] résolution | package=$packageName | action=${intentAction.substringAfterLast('.')} | uris=${savedUris.size}")

        val pm = context.packageManager

        // Android 11+ (API 30) Package Visibility : queryIntentActivities retourne vide
        // pour les packages non déclarés dans <queries> du manifest, même avec flag 0.
        // Fix manifest requis (voir commentaire en bas de ce bloc).
        // On tente avec flag 0 (plus large que MATCH_DEFAULT_ONLY qui exige CATEGORY_DEFAULT
        // dans le filtre — WhatsApp ne le déclare pas pour SEND_MULTIPLE).
        @Suppress("DEPRECATION")
        val allCandidates = pm.queryIntentActivities(
            Intent(intentAction).apply { type = "image/*" },
            0,
        )
        Log.d(WA_LOG, "[3] queryIntentActivities → ${allCandidates.size} candidats" +
                " | packages=${allCandidates.map { it.activityInfo.packageName }}")

        // Cherche d'abord le package demandé, puis le variant alternatif si absent.
        // com.whatsapp ne déclare pas toujours ACTION_SEND_MULTIPLE selon la version
        // installée ; com.whatsapp.w4b peut le faire à sa place et vice-versa.
        val alternativePackage = if (packageName == "com.whatsapp") "com.whatsapp.w4b" else "com.whatsapp"

        val resolvedInfo = allCandidates.firstOrNull { it.activityInfo.packageName == packageName }
            ?: allCandidates.firstOrNull { it.activityInfo.packageName == alternativePackage }
                ?.also { Log.w(WA_LOG, "[3] $packageName absent des candidats → fallback vers $alternativePackage") }

        val resolvedComponent: ComponentName? = resolvedInfo
            ?.activityInfo
            ?.let { ComponentName(it.packageName, it.name) }

        if (resolvedComponent == null) {
            Log.w(WA_LOG, "[3] resolvedComponent=null — ni $packageName ni $alternativePackage" +
                    " ne déclarent ACTION_SEND_MULTIPLE sur ce device." +
                    " Candidats présents : ${allCandidates.map { it.activityInfo.packageName }.distinct()}")
        } else {
            Log.d(WA_LOG, "[3] resolvedComponent=$resolvedComponent")
        }

        // Intent de base sans binding package/composant — on applique l'un ou l'autre ensuite.
        fun buildBaseIntent(): Intent =
            if (savedUris.size == 1) {
                Intent(Intent.ACTION_SEND).apply {
                    type = "image/*"
                    putExtra(Intent.EXTRA_STREAM, savedUris.first())
                    clipData = ClipData.newRawUri("", savedUris.first())
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            } else {
                Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                    type = "image/*"
                    putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(savedUris))
                    val clip = ClipData.newRawUri("", savedUris.first())
                    savedUris.drop(1).forEach { clip.addItem(ClipData.Item(it)) }
                    clipData = clip
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }

        // ─────────────────────────────────────────────────────────
        // STEP 4 — grant URI permissions
        // ─────────────────────────────────────────────────────────
        savedUris.forEachIndexed { i, uri ->
            try {
                context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                Log.d(WA_LOG, "[4] grantUriPermission ✓ [$i] $uri")
            } catch (e: Exception) {
                Log.w(WA_LOG, "[4] grantUriPermission ✗ [$i] $uri | ${e.message}")
            }
        }

        // ─────────────────────────────────────────────────────────
        // STEP 5 — lancement de l'intent
        // ─────────────────────────────────────────────────────────
        val directIntent = buildBaseIntent().apply {
            if (resolvedComponent != null) component = resolvedComponent
            else setPackage(packageName) // setPackage seul — marche pour ACTION_SEND
        }
        Log.d(WA_LOG, "[5] lancement | component=$resolvedComponent | hasPackage=${directIntent.`package`}")

        try {
            context.startActivity(directIntent)
            Log.i(WA_LOG, "[5] ✓ startActivity direct réussi")
        } catch (e: Exception) {
            Log.e(WA_LOG, "[5] ✗ startActivity direct échoué | ${e.message}")

            // Fallback B — chooser système (images restent attachées, contrairement à wa.me)
            try {
                Log.d(WA_LOG, "[5] fallback chooser (images conservées)")
                context.startActivity(
                    Intent.createChooser(buildBaseIntent(), null)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
                Log.i(WA_LOG, "[5] ✓ fallback chooser lancé")
            } catch (e2: Exception) {
                Log.e(WA_LOG, "[5] ✗ fallback chooser échoué | ${e2.message}")

                // Fallback C — wa.me (perd les images, dernier recours)
                try {
                    val waUrl = "https://wa.me/$phoneNumber"
                    Log.d(WA_LOG, "[5] fallback wa.me → $waUrl")
                    context.startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse(waUrl))
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                    Log.i(WA_LOG, "[5] ✓ fallback wa.me lancé")
                } catch (e3: Exception) {
                    Log.e(WA_LOG, "[5] ✗ tous les fallbacks échoués | ${e3.message}")
                    Toast.makeText(context, "WhatsApp non installé", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // ─────────────────────────────────────────────────────────
        // STEP 6 — nettoyage
        // ─────────────────────────────────────────────────────────
        Log.d(WA_LOG, "[6] reset whatsappSendRequest + callback onClick_Lence_Capture")
        whatsappSendRequest = null
        onClick_Lence_Capture()
        Log.i(WA_LOG, "[6] ■ flow terminé")
    }

    Box {
        Column(modifier = modifier.fillMaxSize()) {
            if (allBons.isEmpty()) {
                Text(
                    "لا توجد حالة دين جديدة",
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(16.dp),
                )
            } else LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                itemsIndexed(allBons, key = { _, b -> b.keyID }) { index, b ->
                    val cap = rememberCapturableLayer()
                    val capKey = "${b.creationTimestamps}|${b.keyID}|${b.etateActuellementEst.name}"

                    DisposableEffect(capKey) {
                        ctrl.register(capKey, index, cap.hasBeenDrawn) { cap.capture() }
                        onDispose { ctrl.unregister(capKey) }
                    }

                    Box(modifier = cap.modifier) {
                        when {
                            b.etateActuellementEst == M8BonVent.EtateActuellementEst.New_Situation_Credit ->
                                Situation_Card_ItemView(
                                    allBonVentList = allBons,
                                    relative_M8BonVent = b,
                                    onUpdate = { scope.launch { vm.update_M8(it) } },
                                    onDelete = { scope.launch { appDatabase.dao_M8BonVent().deleteByKeyId(it.keyID) } },
                                )
                            b.etateActuellementEst.credit_type ->
                                Y_Credit_And_Versement_ItemView(
                                    allBonVentList = allBons,
                                    relative_M8BonVent = b,
                                    onUpdate = { scope.launch { vm.update_M8(it) } },
                                    onDelete = { scope.launch { appDatabase.dao_M8BonVent().deleteByKeyId(it.keyID) } },
                                )
                            else ->
                                Affiche_NonCredit_Etate(
                                    allBonVentList = allBons,
                                    relative_M8BonVent = b,
                                    onUpdate = { scope.launch { vm.update_M8(it) } },
                                    onDelete = { scope.launch { appDatabase.dao_M8BonVent().deleteByKeyId(it.keyID) } },
                                )
                        }
                    }
                }
            }
        }

        Floating_Separated_Button(onClick_Lence_Capture = onLenceCapture, vm = vm)

        A_FastAdd_FloatingSeparated_Button_1(
            relative_M2Client = relative_M2Client,
            bons = allBons,
            onSendWhatsApp = { phoneNumber, isWhatsAppBusiness ->
                whatsappSendRequest = Pair(phoneNumber, isWhatsAppBusiness)
            },
            onUpdateClient = { updatedClient ->
                scope.launch { appDatabase.dao_M2Client().upsert(updatedClient) }
            }
        ) { bon1, bon2 ->
            val updated = allBons.toMutableList().also {
                it.add(0, bon2)
                it.add(0, bon1)
            }
            vm.active_Datas.list_M8bon = updated
            fastAddCaptureVersion++
        }
    }

    if (showDlg && captured.isNotEmpty()) {
        Afficheur_locale_Image_Captured(
            capturedBitmaps = captured,
            onDismiss = { showDlg = false; captured = emptyList(); onClick_Lence_Capture() },
            onSave = { bmpList ->
                relative_M2Client?.let {
                    saveAllToMediaStore(bitmaps = bmpList, context = context, clientKeyID = it.keyID)
                }
                showDlg = false; captured = emptyList(); onClick_Lence_Capture()
            },
        )
    }
}
