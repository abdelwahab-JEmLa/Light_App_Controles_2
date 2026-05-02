package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.Options

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

// ── Which dropdown item is currently in edit mode ────────────────────────────
private enum class ActiveDropdownItem { None, Credit, Versement }

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

@Composable
fun A_FastAdd_FloatingSeparated_Button_1(
    buttonState: Button_State = Button_State.get_Default().copy(
        text_Label = "",
        icons = Pair(Icons.Default.FilterList, Icons.Default.AllInbox),
        colors = Pair(Color.Red, Color.Blue)
    ),
    vm: A_ViewModel,
    bons: List<M8BonVent>? = vm.active_Datas.list_M8bon,
    relative_M2Client: M2Client?,
) {
    val updatedButtonState = buttonState.copy(its_Active = true)

    val haptic = LocalHapticFeedback.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp

    var offsetX by remember { mutableFloatStateOf(screenWidth.value - 200f) }
    var offsetY by remember { mutableFloatStateOf(screenHeightDp.value - 300f) }
    var showDropdown by remember { mutableStateOf(false) }

    val clientKey = relative_M2Client?.keyID ?: ""

    // ── Resolve latest situation montant ─────────────────────────────────────
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

    // ── Which item is editing right now ──────────────────────────────────────
    var activeItem by remember { mutableStateOf(ActiveDropdownItem.None) }

    // ── Commit helper ────────────────────────────────────────────────────────
    fun commitBons(bon1: M8BonVent, bon2: M8BonVent) {
        val updated = (bons?.toMutableList() ?: mutableListOf()).also {
            it.add(bon1)
            it.add(bon2)
        }
        vm.active_Datas.list_M8bon = updated
        activeItem = ActiveDropdownItem.None
        /* vm.add_New_M8BonVent(bon1); vm.add_New_M8BonVent(bon2) */
    }

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
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                    DropdownItem_Credit(
                        clientKey = clientKey,
                        latestSituationMontant = latestSituationMontant,
                        isActive = activeItem == ActiveDropdownItem.Credit,
                        onActivate = { activeItem = ActiveDropdownItem.Credit },
                        onCommit = { credit, newSit -> commitBons(credit, newSit) },
                    )

                    DropdownItem_Versement(
                        clientKey = clientKey,
                        latestSituationMontant = latestSituationMontant,
                        isActive = activeItem == ActiveDropdownItem.Versement,
                        onActivate = { activeItem = ActiveDropdownItem.Versement },
                        onCommit = { versement, newSit -> commitBons(versement, newSit) },
                    )

                    HorizontalDivider(thickness = 3.dp, color = Color.Red)
                }
            }
        }
    }
}
