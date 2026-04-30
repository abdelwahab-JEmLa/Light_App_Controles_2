package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View

import A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog.C.Components.AvertissementDialog
import A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog.C.Components.Local_Organizer
import A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog.Z_Content_Buttons.View.ButID_4_upload_datas_fireBase_au_csv
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Produits.Models.M16CategorieProduit
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
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
import androidx.compose.material.icons.filled.CameraAlt
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
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.light_app_controles.Floating_DropDownMenuS.Dialoge.Dialog.Z_Content_Buttons.View.A.Main.Z.Buttons.View.ButID2_ImportFromCSV_DropDownItemWBaseDonne
import com.example.light_app_controles.Floating_DropDownMenuS.Dialoge.Dialog.Z_Content_Buttons.View.A.Main.Z.Buttons.View.ButID_1_ExportToCSV_DropDownItemWBaseDonne
import com.example.light_app_controles.Floating_DropDownMenuS.Dialoge.Dialog.Z_Content_Buttons.View.A.Main.Z.Buttons.View.ButID_3_ImportFromCSV
import com.example.light_app_controles.Modules.Base.SQL.Daos.AppDatabase
import kotlinx.coroutines.launch
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
fun Floating_Separated_Button(
    appDatabase: AppDatabase,
    list_m16: List<M16CategorieProduit>? = emptyList(),
    list_m1: List<M01Produit>? = emptyList(),
    list_m3: List<M3CouleurProduitInfos>? = emptyList(),
    on_vent_key: String = "",
    buttonState: Button_State = Button_State.get_Default().copy(
        text_Label = "",
        icons = Pair(Icons.Default.FilterList, Icons.Default.AllInbox),
        colors = Pair(Color.Red, Color.Blue)
    ),
    onClick_Lence_Capture: (() -> Unit)? = null,
    vm: A_ViewModel,
) {
    val updatedButtonState = buttonState.copy(its_Active = true)

    val haptic = LocalHapticFeedback.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp

    var offsetX by remember { mutableFloatStateOf(screenWidth.value - 200f) }
    var offsetY by remember { mutableFloatStateOf(screenHeightDp.value - 300f) }
    var showDropdown by remember { mutableStateOf(false) }

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
                    modifier = Modifier.size(48.dp),
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

                B_FragMap_DropdownMenu(
                    appDatabase = appDatabase,
                    expanded = showDropdown,
                    onDismiss = { showDropdown = false },
                    list_m16 = list_m16,
                    list_m1 = list_m1,
                    list_m3 = list_m3,
                    on_vent_key = on_vent_key,
                    onClick_Lence_Capture = onClick_Lence_Capture,
                    vm = vm,
                )
            }
        }
    }
}

private enum class PendingAction {
    UpdateLocalTimestamps,
}

@Composable
fun B_FragMap_DropdownMenu(
    vm: A_ViewModel,
    appDatabase: AppDatabase,
    expanded: Boolean,
    onDismiss: () -> Unit,
    list_m16: List<M16CategorieProduit>?,
    list_m1: List<M01Produit>?,
    list_m3: List<M3CouleurProduitInfos>?,
    on_vent_key: String = "",
    modifier: Modifier = Modifier,
    onClick_Lence_Capture: (() -> Unit)? = null,
) {
    val coroutineScope = rememberCoroutineScope()

    val latestSituationMontant: Int? = remember(vm.activeCentralValues.list_M8bon) {
        vm.activeCentralValues.list_M8bon
            ?.filter {
                it.etateActuellementEst == M8BonVent.EtateActuellementEst.New_Situation_Credit &&
                        (on_vent_key.isEmpty() || it.parent_M2Client_KeyID == on_vent_key)
            }
            ?.maxByOrNull { it.creationTimestamps }
            ?.montant_principale_du_type
            ?.toInt()
    }
    var fake_init_val_du_ancien_credits_situation by remember(latestSituationMontant) {
        mutableStateOf<Int?>(latestSituationMontant)
    }

    var organizeDropBoxProgress by remember { mutableStateOf<Float?>(null) }
    var organizeLocalProgress by remember { mutableStateOf<Float?>(null) }
    var syncImages2Progress by remember { mutableStateOf<Float?>(null) }
    var updateTimestampsProgress by remember { mutableStateOf<Float?>(null) }
    var pendingAction by remember { mutableStateOf<PendingAction?>(null) }

    var isEditingCredits by remember { mutableStateOf(false) }
    var out_val by remember { mutableStateOf("") }
    val creditsFocusRequester = remember { FocusRequester() }

    LaunchedEffect(isEditingCredits) {
        if (isEditingCredits) creditsFocusRequester.requestFocus()
    }

    pendingAction?.let { action ->
        when (action) {
            PendingAction.UpdateLocalTimestamps -> AvertissementDialog(
                title = "Mettre à jour dates locales",
                message = "La date de modification de chaque fichier image local sera " +
                        "remplacée par l'heure actuelle. Cela forcera un re-téléchargement " +
                        "lors de la prochaine synchronisation. Continuer ?",
                confirmLabel = "Mettre à jour",
                onConfirm = {
                    pendingAction = null
                    coroutineScope.launch {
                        updateTimestampsProgress = 0f
                        Local_Organizer.updateLocalTimestampsToNow(
                            list_m3 = list_m3,
                            onProgress = { p -> updateTimestampsProgress = p }
                        )
                        updateTimestampsProgress = null
                        onDismiss()
                    }
                },
                onDismiss = { pendingAction = null }
            )
        }
    }

    val anyRunning = organizeDropBoxProgress != null
            || organizeLocalProgress != null
            || syncImages2Progress != null
            || updateTimestampsProgress != null

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = modifier.background(Color.White, RoundedCornerShape(8.dp))
    ) {
        if (onClick_Lence_Capture != null) {
            DropdownMenuItem(
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = Color(0xFF1E88E5)
                    )
                },
                text = {
                    Text(
                        text = "التقاط صورة الشاشة",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                onClick = {
                    onDismiss()
                    onClick_Lence_Capture.invoke()
                }
            )
            HorizontalDivider()
        }

        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    tint = Color(0xFF43A047)
                )
            },
            text = {
                if (isEditingCredits) {
                    OutlinedTextField(
                        value = out_val,
                        onValueChange = { input ->
                            if (input.all { it.isDigit() }) out_val = input
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done,
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                val parsed = out_val.toIntOrNull()
                                val montant = parsed?.toDouble() ?: 0.0

                                if (parsed != null) fake_init_val_du_ancien_credits_situation = parsed
                                out_val = fake_init_val_du_ancien_credits_situation?.toString() ?: ""
                                isEditingCredits = false

                                // Launch credit + New_Situation_Credit update, then trigger capture
                                vm.ajoute_credit_et_affiche_compos_image(
                                    montant = montant,
                                    clientKey = on_vent_key.ifEmpty { FAKE_CLIENT_KEY },
                                )
                            }
                        ),
                        label = {
                            val diff = (fake_init_val_du_ancien_credits_situation ?: 0) - (out_val.toIntOrNull() ?: 0)
                            Text(
                                text = "الرصيد السابق — $diff",
                                style = MaterialTheme.typography.labelSmall,
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(creditsFocusRequester),
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
                if (!isEditingCredits) {
                    out_val = ""
                    isEditingCredits = true
                }
            }
        )

        HorizontalDivider()

        ButID_1_ExportToCSV_DropDownItemWBaseDonne(appDatabase = appDatabase, enabled = true)
        ButID2_ImportFromCSV_DropDownItemWBaseDonne(appDatabase = appDatabase, enabled = true)
        HorizontalDivider()
        ButID_3_ImportFromCSV(appDatabase = appDatabase, enabled = true)
        ButID_4_upload_datas_fireBase_au_csv(enabled = true)
    }
}
