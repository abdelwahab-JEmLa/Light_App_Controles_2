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
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.Options.Floating_Separated_Button
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.b_FastAdd_FloatingSeparated_Button_1.Actions.A_FastAdd_FloatingSeparated_Button_1
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.Views.Affiche_NonCredit_Etate
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.Views.Situation_Card_ItemView
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.Views.Y_Credit_And_Versement_ItemView
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.Z.Modules.Capture.Afficheur_locale_Image_Captured
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.Z.Modules.Capture.rememberCapturableLayer
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.Z.Modules.Capture.rememberMultiCaptureController
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.Z.Modules.Capture.saveAllToMediaStore
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.Z.preview.FAKE_CLIENT_KEY
import com.example.light_app_controles.Modules.Base.SQL.Daos.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TAG = "MainPreviewScreen"

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
    relative_M2Client: M2Client? = M2Client.get_default().copy(keyID = FAKE_CLIENT_KEY),
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
        kotlinx.coroutines.delay(200)
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

    // ── FIX TODO(1) ──────────────────────────────────────────────────────────
    // BUG: saveAllToMediaStore faisait du IO lourd (Bitmap.compress WEBP_LOSSLESS)
    // sur Dispatchers.Main. startActivity() était appelé immédiatement après, avant
    // que le IS_PENDING=0 ait eu le temps de se propager via le ContentProvider de
    // MediaStore aux autres processus → WhatsApp recevait un URI valide mais dont
    // le fichier était encore invisible (pending) ou non-flushé.
    //
    // FIX 1: withContext(Dispatchers.IO) pour le save → IO thread dédié, écriture réelle.
    // FIX 2: delay(250) après le save → laisse MediaStore propager IS_PENDING=0
    //         avant que WhatsApp ouvre le file descriptor.
    // FIX 3: guard savedUris.isEmpty() → évite le crash silencieux sur savedUris.first()
    //         qui avalait l'exception dans le scope de la coroutine et n'envoyait rien.
    // ─────────────────────────────────────────────────────────────────────────
    LaunchedEffect(whatsappSendRequest) {
        val request = whatsappSendRequest ?: return@LaunchedEffect
        val (phoneNumber, isWhatsAppBusiness) = request

        Log.d(TAG, "WhatsApp flow start — phone=$phoneNumber business=$isWhatsAppBusiness")

        val raw = ctrl.captureAllWithScroll(
            state          = listState,
            totalItemCount = allBons.size,
            scrollSettleMs = 300,
            restoreIndex   = 0,
            orderedKeys    = buildOrderedKeys(),
        )
        val namedImages = mapRawToNamed(raw)

        if (namedImages.isEmpty()) {
            Log.w(TAG, "WhatsApp flow: no images captured — aborting")
            whatsappSendRequest = null
            return@LaunchedEffect
        }

        // FIX 1: withContext(IO) — le compress WEBP_LOSSLESS ne bloque plus le Main thread,
        // et le flush disque + IS_PENDING=0 sont garantis terminés avant de continuer.
        val savedUris: List<android.net.Uri> = withContext(Dispatchers.IO) {
            relative_M2Client?.let {
                saveAllToMediaStore(
                    bitmaps     = namedImages.map { (img, lbl) -> img.asAndroidBitmap() to lbl },
                    context     = context,
                    clientKeyID = it.keyID,
                )
            } ?: emptyList()
        }
        Log.d(TAG, "WhatsApp flow: saved ${savedUris.size} URIs via MediaStore")

        // FIX 3: guard — si aucun URI (client null ou insert échoué), ne pas construire l'intent.
        if (savedUris.isEmpty()) {
            Log.e(TAG, "WhatsApp flow: savedUris empty — intent annulé")
            whatsappSendRequest = null
            return@LaunchedEffect
        }

        // FIX 2: délai de propagation — MediaStore notifie les observers via binder asynchrone.
        // Sans ce délai, WhatsApp peut ouvrir le URI pendant la fenêtre où le fichier
        // est encore marqué IS_PENDING ou son file descriptor pas encore visible cross-process.
        kotlinx.coroutines.delay(250)
        Log.d(TAG, "WhatsApp flow: propagation delay done — building intent")

        val packageName = if (isWhatsAppBusiness) "com.whatsapp.w4b" else "com.whatsapp"
        val pm = context.packageManager

        // FIX ActivityNotFoundException:
        // setPackage() + ACTION_SEND_MULTIPLE échoue car WhatsApp n'expose pas cette
        // combinaison dans son manifest. Il faut résoudre le composant exact via
        // queryIntentActivities() SANS setPackage, puis cibler avec setComponent().
        // Ainsi Android route directement vers la bonne Activity de WhatsApp sans
        // passer par le resolver — et le FLAG_GRANT_READ_URI_PERMISSION est honoré.
        fun buildBaseIntent(): android.content.Intent =
            if (savedUris.size == 1) {
                android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                    type = "image/webp"
                    putExtra(android.content.Intent.EXTRA_STREAM, savedUris.first())
                    clipData = android.content.ClipData.newRawUri("", savedUris.first())
                    addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            } else {
                android.content.Intent(android.content.Intent.ACTION_SEND_MULTIPLE).apply {
                    type = "image/*"
                    putParcelableArrayListExtra(android.content.Intent.EXTRA_STREAM, ArrayList(savedUris))
                    val clip = android.content.ClipData.newRawUri("", savedUris.first())
                    savedUris.drop(1).forEach { clip.addItem(android.content.ClipData.Item(it)) }
                    clipData = clip
                    addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            }

        // Résolution du composant WhatsApp exact pour cet intent.
        @Suppress("DEPRECATION")
        val resolvedComponent = pm
            .queryIntentActivities(buildBaseIntent(), android.content.pm.PackageManager.MATCH_DEFAULT_ONLY)
            .firstOrNull { it.activityInfo.packageName == packageName }
            ?.activityInfo
            ?.let { android.content.ComponentName(it.packageName, it.name) }

        Log.d(TAG, "WhatsApp flow: resolved component = $resolvedComponent")

        val shareIntent = buildBaseIntent().apply {
            if (resolvedComponent != null) {
                // Ciblage exact → pas d'ActivityNotFoundException, flag URI propagé.
                setComponent(resolvedComponent)
            } else {
                // WhatsApp pas installé ou intent non supporté → on laisse le chooser décider.
                Log.w(TAG, "WhatsApp flow: composant introuvable pour $packageName — chooser fallback")
            }
        }

        savedUris.forEach { uri ->
            try { context.grantUriPermission(packageName, uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION) }
            catch (_: Exception) { }
        }

        Log.i(TAG, "WhatsApp flow: startActivity — pkg=$packageName uris=${savedUris.size} component=$resolvedComponent")
        try {
            if (resolvedComponent != null) {
                context.startActivity(shareIntent)
            } else {
                // WhatsApp introuvable → fallback wa.me
                throw android.content.ActivityNotFoundException("$packageName non résolu")
            }
        } catch (e: Exception) {
            Log.e(TAG, "WhatsApp non résolu — fallback wa.me", e)
            try {
                context.startActivity(
                    android.content.Intent(
                        android.content.Intent.ACTION_VIEW,
                        android.net.Uri.parse("https://wa.me/$phoneNumber"),
                    )
                )
            } catch (e2: Exception) {
                Log.e(TAG, "Fallback aussi échoué", e2)
                android.widget.Toast.makeText(context, "WhatsApp non installé", android.widget.Toast.LENGTH_SHORT).show()
            }
        }

        whatsappSendRequest = null
        onClick_Lence_Capture()
        Log.i(TAG, "TODO(1) FIX COMPLETE — WhatsApp flow terminé proprement")
    }
    // ─────────────────────────────────────────────────────────────────────────

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
