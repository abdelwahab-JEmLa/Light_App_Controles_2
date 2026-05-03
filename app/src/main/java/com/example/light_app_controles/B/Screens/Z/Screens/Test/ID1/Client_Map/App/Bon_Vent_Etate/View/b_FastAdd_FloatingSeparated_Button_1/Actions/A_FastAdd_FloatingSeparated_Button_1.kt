package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.b_FastAdd_FloatingSeparated_Button_1.Actions

import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AllInbox
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.M8BonVent
import kotlin.math.roundToInt

// ── Active item tracker ───────────────────────────────────────────────────────
 enum class ActiveDropdownItem {
    None, Credit, Versement,
    WorkerPhone,   // editing worker num_worker / nom_worker
}

// ── Button appearance model ───────────────────────────────────────────────────
data class Button_State(
    val showLabels: Boolean = true,
    val its_Active: Boolean = false,
    val text_Label: String = "",
    val colors: Pair<Color, Color> = Pair(Color.White, Color.White),
    val icons: Pair<ImageVector, ImageVector> = Pair(Icons.Default.Remove, Icons.Default.Add),
    val description_Functionement: String = "",
) {
    companion object {
        fun get_Default() = Button_State()
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Private helpers
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Normalises any Algerian phone number to the digits-only international format
 * required by wa.me URLs (no leading "+", country code 213).
 *
 * Examples:
 *   "+213553885037" → "213553885037"
 *   "0553885037"    → "213553885037"
 *   "553885037"     → "213553885037"
 *   "213553885037"  → "213553885037"  (already correct)
 */
 fun normaliseToWaMeNumber(raw: String): String {
    val digits = raw.filter { it.isDigit() }
    return when {
        digits.startsWith("213") -> digits
        digits.startsWith("0")   -> "213${digits.drop(1)}"
        else                     -> "213$digits"
    }
}

/**
 * Fixed Abdelwahab Osstad number in wa.me format (digits only, no "+").
 * Pre-normalised so it is safe to embed directly in "https://wa.me/<number>".
 */
 const val ABDELWAHAB_WA_ME_NUMBER = "213553885037"

// ─────────────────────────────────────────────────────────────────────────────
// Private composable helpers
// ─────────────────────────────────────────────────────────────────────────────
       //<--
       //TODO(1): enleve les commantaire et logs et les sementics pour but de consise le max possible  tallie du code sans change le foctionemen

// ─────────────────────────────────────────────────────────────────────────────
// Main composable
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Floating, draggable action button that opens a dropdown with:
 *
 * - **Credit** / **Versement** quick-add items (persist new [M8BonVent] records via [onCommit]).
 * - **Abdelwahab WhatsApp** — sends captured images to Abdelwahab's fixed number (not editable).
 * - **Abdelwahab WhatsApp Business** — same number, but targets com.whatsapp.w4b.
 * - **Worker WhatsApp** — sends to [M2Client.num_worker]; nom/num are editable inline.
 *
 * @param onCommit         Called with the two new bons (operation + new_situation).
 * @param onSendWhatsApp   Called when the user taps a WhatsApp item.
 *                         Receives (phoneNumber, isWhatsAppBusiness).
 * @param onUpdateClient   Called whenever the user saves edited worker info.
 */
@Composable
fun A_FastAdd_FloatingSeparated_Button_1(
    buttonState: Button_State = Button_State.get_Default().copy(
        text_Label = "",
        icons  = Pair(Icons.Default.FilterList, Icons.Default.AllInbox),
        colors = Pair(Color.Red, Color.Blue),
    ),
    bons: List<M8BonVent>? = emptyList(),
    relative_M2Client: M2Client?,
    onSendWhatsApp: (phoneNumber: String, isWhatsAppBusiness: Boolean) -> Unit = { _, _ -> },
    onUpdateClient: (M2Client) -> Unit = {},
    onCommit: (bon: M8BonVent, newSituation: M8BonVent) -> Unit,
) {
    val updatedButtonState = buttonState.copy(its_Active = true)

    val haptic         = LocalHapticFeedback.current
    val configuration  = LocalConfiguration.current
    val screenWidth    = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp

    var offsetX by remember { mutableFloatStateOf(screenWidth.value - 200f) }
    var offsetY by remember { mutableFloatStateOf(screenHeightDp.value - 300f) }
    var showDropdown by remember { mutableStateOf(false) }

    val clientKey = relative_M2Client?.keyID ?: ""

    // ── Resolve latest situation montant ──────────────────────────────────────
    val latestSituationMontant: Int? = remember(bons) {
        val clientBons = bons?.filter { it.parent_M2Client_KeyID == clientKey }
        clientBons
            ?.filter { it.etateActuellementEst == M8BonVent.EtateActuellementEst.New_Situation_Credit }
            ?.maxByOrNull { it.creationTimestamps }
            ?.montant_principale_du_type?.toInt()
            ?: clientBons
                ?.filter {
                    it.etateActuellementEst == M8BonVent.EtateActuellementEst.Credit ||
                            it.etateActuellementEst == M8BonVent.EtateActuellementEst.Cette_Transaction_Type_Est_Credit
                }
                ?.maxByOrNull { it.creationTimestamps }
                ?.credit_fait?.toInt()
    }

    // ── Which item is currently in edit mode ──────────────────────────────────
    var activeItem by remember { mutableStateOf(ActiveDropdownItem.None) }

    val showWhatsAppItems = bons?.isNotEmpty() == true

    // ─────────────────────────────────────────────────────────────────────────
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX = (offsetX + dragAmount.x).coerceIn(0f, screenWidth.value - 100f)
                        offsetY = (offsetY + dragAmount.y).coerceIn(0f, screenHeightDp.value - 100f)
                    }
                }
                .padding(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FloatingActionButton(
                    modifier = Modifier
                        .semantics(mergeDescendants = true) {
                            set(SemanticsPropertyKey<String>("clientKey"), clientKey)
                            set(SemanticsPropertyKey<Int?>("latestSituationMontant"), latestSituationMontant)
                        }
                        .size(48.dp),
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        showDropdown = true
                    },
                    containerColor = updatedButtonState.colors.second,
                ) {
                    Icon(
                        imageVector = updatedButtonState.icons.second,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp),
                    )
                }

                DropdownMenu(
                    expanded = showDropdown,
                    onDismissRequest = {
                        showDropdown = false
                        activeItem = ActiveDropdownItem.None
                    },
                    modifier = Modifier.background(Color.White, RoundedCornerShape(8.dp)),
                ) {
                    // ── Credit / Versement ────────────────────────────────────
                    HorizontalDivider(thickness = 3.dp, color = Color.Red)

                    DropdownItem_Credit(
                        clientKey = clientKey,
                        latestSituationMontant = latestSituationMontant,
                        isActive = activeItem == ActiveDropdownItem.Credit,
                        onActivate = { activeItem = ActiveDropdownItem.Credit },
                        onCommit = { bon, newSit ->
                            onCommit(bon, newSit)
                            activeItem = ActiveDropdownItem.None
                        },
                    )

                    DropdownItem_Versement(
                        clientKey = clientKey,
                        latestSituationMontant = latestSituationMontant,
                        isActive = activeItem == ActiveDropdownItem.Versement,
                        onActivate = { activeItem = ActiveDropdownItem.Versement },
                        onCommit = { bon, newSit ->
                            onCommit(bon, newSit)
                            activeItem = ActiveDropdownItem.None
                        },
                    )

                    HorizontalDivider(thickness = 3.dp, color = Color.Red)

                    // ── WhatsApp items ────────────────────────────────────────
                    if (showWhatsAppItems) {

                        // 1 — Abdelwahab regular WhatsApp
                        DropdownItem_WhatsApp_FixedAbdelwahab(
                            isWhatsAppBusiness = false,
                            iconTint = Color(0xFF25D366),
                            labelPrefix = "WhatsApp",
                            onSend = { phone, isBusiness ->
                                // FIX: dismiss *before* mutating state so there is no
                                // recomposition frame where the dropdown is still visible
                                showDropdown = false
                                activeItem = ActiveDropdownItem.None
                                onSendWhatsApp(phone, isBusiness)
                            },
                        )

                        // 2 — Abdelwahab WhatsApp Business
                        DropdownItem_WhatsApp_FixedAbdelwahab(
                            isWhatsAppBusiness = true,
                            iconTint = Color(0xFF00897B),
                            labelPrefix = "WhatsApp Business",
                            onSend = { phone, isBusiness ->
                                // FIX: dismiss *before* mutating state (same reason)
                                showDropdown = false
                                activeItem = ActiveDropdownItem.None
                                onSendWhatsApp(phone, isBusiness)
                            },
                        )

                        HorizontalDivider(thickness = 1.dp, color = Color.LightGray)

                        // 3 — Worker WhatsApp (editable)
                        if (relative_M2Client != null) {
                            DropdownItem_WhatsApp_Worker(
                                currentNomWorker = relative_M2Client.nom_worker,
                                currentNumWorker = relative_M2Client.num_worker,
                                isActive = activeItem == ActiveDropdownItem.WorkerPhone,
                                onActivate = { activeItem = ActiveDropdownItem.WorkerPhone },
                                onWorkerSaved = { nom, num ->
                                    onUpdateClient(
                                        relative_M2Client.copy(nom_worker = nom, num_worker = num)
                                    )
                                    activeItem = ActiveDropdownItem.None
                                },
                                onSend = { phone ->
                                    // FIX: dismiss *before* mutating state (same reason)
                                    showDropdown = false
                                    activeItem = ActiveDropdownItem.None
                                    onSendWhatsApp(phone, false)
                                },
                            )
                        }

                        HorizontalDivider(thickness = 3.dp, color = Color.Red)
                    }
                }
            }
        }
    }
}
