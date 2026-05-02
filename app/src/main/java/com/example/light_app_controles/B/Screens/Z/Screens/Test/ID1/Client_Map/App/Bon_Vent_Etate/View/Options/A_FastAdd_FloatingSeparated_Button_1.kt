package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.Options

import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AllInbox
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.A_ViewModel
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.M8BonVent
import kotlin.math.roundToInt

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

    val latestSituationMontant: Int? = remember(bons) {
        val targetClientKey = relative_M2Client?.keyID ?: ""
        val clientBons = bons
            ?.filter { it.parent_M2Client_KeyID == targetClientKey }

        val latestSituation = clientBons
            ?.filter { it.etateActuellementEst == M8BonVent.EtateActuellementEst.New_Situation_Credit }
            ?.maxByOrNull { it.creationTimestamps }

        latestSituation?.montant_principale_du_type?.toInt()
            ?: clientBons
                ?.filter {
                    it.etateActuellementEst == M8BonVent.EtateActuellementEst.Credit ||
                            it.etateActuellementEst == M8BonVent.EtateActuellementEst.Cette_Transaction_Type_Est_Credit
                }
                ?.maxByOrNull { it.creationTimestamps }
                ?.credit_fait
                ?.toInt()
    }

    var fake_init_val_du_ancien_credits_situation by remember(latestSituationMontant) {
        mutableStateOf<Int?>(latestSituationMontant)
    }

    // FIX TODO(1): Each dropdown item has its own editing flag, input value, and FocusRequester
    // so they never bleed into each other.

    // ── Credit item state ────────────────────────────────────────────────────
    var isEditingCredit by remember { mutableStateOf(false) }
    var out_val_credit by remember { mutableStateOf("") }
    val creditFocusRequester = remember { FocusRequester() }

    // ── Versement item state ─────────────────────────────────────────────────
    var isEditingVersement by remember { mutableStateOf(false) }
    var out_val_versement by remember { mutableStateOf("") }
    val versementFocusRequester = remember { FocusRequester() }

    LaunchedEffect(isEditingCredit) {
        if (isEditingCredit) creditFocusRequester.requestFocus()
    }
    LaunchedEffect(isEditingVersement) {
        if (isEditingVersement) versementFocusRequester.requestFocus()
    }

    val baseTs = System.currentTimeMillis()
    val currentList = bons?.toMutableList() ?: mutableListOf()

    var montant by remember { mutableStateOf(0.0) }

    // ── Versement: client pays back a portion of their debt ──────────────────
    val versementBon = M8BonVent(
        parent_M2Client_KeyID = clientKey,
        etateActuellementEst = M8BonVent.EtateActuellementEst.Versemment,
        creationTimestamps = baseTs,
        versement_fait = montant,
    )
    val newMontantAfterVersement = (latestSituationMontant?.toDouble() ?: 0.0) - montant
    val newSituationAfterVersement = M8BonVent(
        parent_M2Client_KeyID = clientKey,
        etateActuellementEst = M8BonVent.EtateActuellementEst.New_Situation_Credit,
        creationTimestamps = baseTs + 1_000L,
        montant_principale_du_type = newMontantAfterVersement,
    )

    val creditBon = M8BonVent(
        parent_M2Client_KeyID = clientKey,
        etateActuellementEst = M8BonVent.EtateActuellementEst.Credit,
        creationTimestamps = baseTs,
        credit_fait = montant,
    )
    val newMontantAfterCredit = (latestSituationMontant?.toDouble() ?: 0.0) + montant
    val newSituationAfterCredit = M8BonVent(
        parent_M2Client_KeyID = clientKey,
        etateActuellementEst = M8BonVent.EtateActuellementEst.New_Situation_Credit,
        creationTimestamps = baseTs + 1_000L,
        montant_principale_du_type = newMontantAfterCredit,
    )

    fun ajoute_versement_et_new_sit() {
        currentList.add(versementBon)
        currentList.add(newSituationAfterVersement)
        vm.active_Datas.list_M8bon = currentList
        /* vm.add_New_M8BonVent(versementBon)
           vm.add_New_M8BonVent(newSituationAfterVersement) */
    }

    fun ajoute_credit_et_new_sit() {
        currentList.add(creditBon)
        currentList.add(newSituationAfterCredit)
        vm.active_Datas.list_M8bon = currentList
        /* vm.add_New_M8BonVent(creditBon)
           vm.add_New_M8BonVent(newSituationAfterCredit) */
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
                            set(value = versementBon, key = SemanticsPropertyKey("versementBon"))
                            set(value = newSituationAfterVersement, key = SemanticsPropertyKey("newSituationBon"))

                            set(value = creditBon, key = SemanticsPropertyKey("creditBon"))
                            set(value = newSituationAfterCredit, key = SemanticsPropertyKey("newSituationAfterCredit"))
                        }
                        .size(48.dp),
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        showDropdown = true
                    },
                    containerColor = updatedButtonState.colors.second
                ) {
                    Icon(
                        imageVector = updatedButtonState.icons.second,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                DropdownMenu(
                    expanded = showDropdown,
                    onDismissRequest = { showDropdown = false },
                    modifier = Modifier.background(Color.White, RoundedCornerShape(8.dp))
                ) {

                    // ── Credit item ──────────────────────────────────────────────────────────
                    DropdownMenuItem(
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = Color(0xFFE53935)
                            )
                        },
                        text = {
                            if (isEditingCredit) {
                                OutlinedTextField(
                                    value = out_val_credit,
                                    onValueChange = { input ->
                                        if (input.all { it.isDigit() }) out_val_credit = input
                                    },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Done,
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onDone = {
                                            val parsed = out_val_credit.toIntOrNull()
                                            if (parsed != null) {
                                                montant = parsed.toDouble()
                                                fake_init_val_du_ancien_credits_situation = parsed
                                            }
                                            out_val_credit =
                                                fake_init_val_du_ancien_credits_situation?.toString()
                                                    ?: ""
                                            isEditingCredit = false
                                            ajoute_credit_et_new_sit()
                                        }
                                    ),
                                    label = {
                                        val diff = (out_val_credit.toIntOrNull() ?: 0) +
                                                (fake_init_val_du_ancien_credits_situation ?: 0)
                                        Text(
                                            text = "الرصيد الجديد — $diff",
                                            style = MaterialTheme.typography.labelSmall,
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .focusRequester(creditFocusRequester),
                                )
                            } else {
                                Text(
                                    text = "دين جديد: ${fake_init_val_du_ancien_credits_situation ?: "-"} دج",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            }
                        },
                        onClick = {
                            if (!isEditingCredit) {
                                out_val_credit = ""
                                isEditingVersement = false  // close the other field if open
                                isEditingCredit = true
                            }
                        }
                    )

                    // ── Versement item ───────────────────────────────────────────────────────
                    DropdownMenuItem(
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = Color(0xFF43A047)
                            )
                        },
                        text = {
                            if (isEditingVersement) {
                                OutlinedTextField(
                                    value = out_val_versement,
                                    onValueChange = { input ->
                                        if (input.all { it.isDigit() }) out_val_versement = input
                                    },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Number,
                                        imeAction = ImeAction.Done,
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onDone = {
                                            val parsed = out_val_versement.toIntOrNull()
                                            if (parsed != null) {
                                                montant = parsed.toDouble()
                                                fake_init_val_du_ancien_credits_situation = parsed
                                            }
                                            out_val_versement =
                                                fake_init_val_du_ancien_credits_situation?.toString()
                                                    ?: ""
                                            isEditingVersement = false
                                            ajoute_versement_et_new_sit()
                                        }
                                    ),
                                    label = {
                                        val diff = (fake_init_val_du_ancien_credits_situation
                                            ?: 0) - (out_val_versement.toIntOrNull() ?: 0)
                                        Text(
                                            text = "الرصيد السابق — $diff",
                                            style = MaterialTheme.typography.labelSmall,
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .focusRequester(versementFocusRequester),
                                )
                            } else {
                                Text(
                                    text = "الرصيد السابق: ${fake_init_val_du_ancien_credits_situation ?: "-"} دج",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                )
                            }
                        },
                        onClick = {
                            if (!isEditingVersement) {
                                out_val_versement = ""
                                isEditingCredit = false  // close the other field if open
                                isEditingVersement = true
                            }
                        }
                    )
                    HorizontalDivider(thickness = 3.dp, color = Color.Red)
                }
            }
        }
    }
}
