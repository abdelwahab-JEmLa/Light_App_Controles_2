package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.Action

import A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog.C.Components.AvertissementDialog
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Produits.Models.M16CategorieProduit
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Details
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Save
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
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.A_ViewModel
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.FAKE_CLIENT_KEY
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.M8BonVent
import com.example.light_app_controles.Modules.Base.SQL.Daos.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
                    expanded = showDropdown,
                    onDismiss = { showDropdown = false },
                    on_vent_key = on_vent_key,
                    onClick_Lence_Capture = onClick_Lence_Capture,
                    vm = vm,
                )
            }
        }
    }
}

enum class PendingAction() {
    But1_Export_M8_Room_To_Csv,
    But2_Export_M8_Csv_To_FireBase,
    But3_Import_M8Csv_To_Room,
    But5_Import_M8_Ui_To_Room,
    But6_Import_M8_FireBase_To_Csv,
    But7_DeleteImport_M8Csv_To_Room,
    But8_DeleteAll_M8_Room, ;

}

@Composable
fun B_FragMap_DropdownMenu(
    modifier: Modifier = Modifier,
    vm: A_ViewModel,
    expanded: Boolean,
    onDismiss: () -> Unit,
    on_vent_key: String = "",
    onClick_Lence_Capture: (() -> Unit)? = null,
) {
    val coroutineScope = rememberCoroutineScope()

    val latestSituationMontant: Int? = remember(vm.active_Datas.list_M8bon) {
        vm.active_Datas.list_M8bon
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

    // CSV stats for But7 label: total rows, new (not in Room), updates (already in Room)
    var csvRowCount by remember { mutableStateOf<Int?>(null) }
    var csvNewCount by remember { mutableStateOf<Int?>(null) }
    var csvUpdateCount by remember { mutableStateOf<Int?>(null) }

    var firebaseRowCount by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        runCatching {
            firebaseRowCount = vm.setter_LongOperations.get_Firebase_M8_Count(M8BonVent.ref_Test)
        }.onFailure {
            firebaseRowCount = -1
        }
    }

    LaunchedEffect(vm.active_Datas.list_M8bon) {
        withContext(Dispatchers.IO) {
            val csv = M8BonVent.csv_test
            if (csv.exists() && csv.length() > 0L) {
                val lines = csv.readLines().filter { it.isNotBlank() }
                if (lines.size >= 2) {
                    val headers = lines[0].split(",")
                    val keyIdx = headers.indexOf("keyID")
                    val csvKeys = lines.drop(1).mapNotNull { line ->
                        line.split(",").getOrNull(keyIdx)
                            ?.trim()?.removeSurrounding("\"")
                            ?.takeIf { it.isNotBlank() }
                    }.toSet()
                    val roomKeys = vm.active_Datas.list_M8bon
                        ?.map { it.keyID }?.toSet() ?: emptySet()
                    csvRowCount = csvKeys.size
                    csvNewCount = (csvKeys - roomKeys).size
                    csvUpdateCount = (csvKeys intersect roomKeys).size
                }
            } else {
                csvRowCount = 0
                csvNewCount = 0
                csvUpdateCount = 0
            }
        }
    }

    var isEditingCredits by remember { mutableStateOf(false) }
    var out_val by remember { mutableStateOf("") }
    val creditsFocusRequester = remember { FocusRequester() }

    LaunchedEffect(isEditingCredits) {
        if (isEditingCredits) creditsFocusRequester.requestFocus()
    }

    pendingAction?.let { action ->
        when (action) {
            PendingAction.But8_DeleteAll_M8_Room -> AvertissementDialog(
                title = PendingAction.But8_DeleteAll_M8_Room.name,
                message =
                        "هل تريد المتابعة؟",
                confirmLabel = "yes",
                onConfirm = {
                    pendingAction = null
                    coroutineScope.launch(Dispatchers.IO) {
                        vm.setter_LongOperations.delete_All_M8()
                        vm.reload()
                        onDismiss()
                    }
                },
                onDismiss = { pendingAction = null },
            )

            PendingAction.But3_Import_M8Csv_To_Room -> AvertissementDialog(
                title = PendingAction.But3_Import_M8Csv_To_Room.name,
                message = "سيتم استيراد بيانات M8BonVent.csv إلى قاعدة البيانات المحلية.\n" +
                        "الصفوف الموجودة ستُحدَّث والجديدة ستُضاف.\n" +
                        "هل تريد المتابعة؟",
                confirmLabel = "استيراد",
                onConfirm = {
                    pendingAction = null
                    coroutineScope.launch(Dispatchers.IO) {
                        vm.setter_LongOperations.import_M8Csv_To_Room(
                            M8BonVent.csv_test
                        )
                        vm.reload()

                        onDismiss()
                    }
                },
                onDismiss = { pendingAction = null },
            )

            PendingAction.But2_Export_M8_Csv_To_FireBase -> But2_Export_M8_Csv_To_FireBase(
                vm = vm,
                coroutineScope = coroutineScope,
                onDismiss = onDismiss,
                onPendingClear = { pendingAction = null },
                action_definition = PendingAction.But2_Export_M8_Csv_To_FireBase,
            )

            PendingAction.But1_Export_M8_Room_To_Csv -> But1_Export_M8_Room_To_Csv(
                vm = vm,
                coroutineScope = coroutineScope,
                onDismiss = onDismiss,
                onPendingClear = { pendingAction = null },
                action_definition = PendingAction.But1_Export_M8_Room_To_Csv,
            )

            PendingAction.But6_Import_M8_FireBase_To_Csv -> But6_Import_M8_FireBase_To_Csv(
                vm = vm,
                coroutineScope = coroutineScope,
                onDismiss = onDismiss,
                onPendingClear = { pendingAction = null },
                action_definition = PendingAction.But6_Import_M8_FireBase_To_Csv,
            )

            PendingAction.But5_Import_M8_Ui_To_Room -> {
                AvertissementDialog(
                    title = action.name,
                    message = "سيتم حفظ بيانات M8BonVent من الواجهة إلى قاعدة البيانات المحلية.\n" +
                            "الصفوف الموجودة ستُحدَّث والجديدة ستُضاف.\n" +
                            "هل تريد المتابعة؟",
                    onConfirm = {
                        pendingAction = null
                        coroutineScope.launch {
                            vm.active_Datas.list_M8bon?.let { bons ->
                                vm.setter_LongOperations.insertAll(bons)
                            }
                            onDismiss()
                        }
                    },
                    onDismiss = { pendingAction = null },
                )
            }

            PendingAction.But7_DeleteImport_M8Csv_To_Room -> {
                AvertissementDialog(
                    title = action.name,
                    message =
                        "هل تريد المتابعة؟",
                    onConfirm = {
                        pendingAction = null
                        coroutineScope.launch {
                            vm.active_Datas.list_M8bon?.let { bons ->
                                vm.setter_LongOperations.delete_All_M8()
                                vm.setter_LongOperations.insertAll(bons)
                            }
                            vm.reload()
                            onDismiss()
                        }
                    },
                    onDismiss = { pendingAction = null },
                )
            }

            else -> {}
        }
    }



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

        val relative_M2Client = FAKE_CLIENT_KEY
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
                            val accepted = input.all { it.isDigit() }
                            if (accepted) out_val = input
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

                                if (parsed != null) fake_init_val_du_ancien_credits_situation =
                                    parsed
                                out_val =
                                    fake_init_val_du_ancien_credits_situation?.toString() ?: ""
                                isEditingCredits = false
                                vm.ajoute_credit_et_affiche_compos_image(
                                    clientKey = relative_M2Client,
                                    montant = montant,
                                )
                            }
                        ),
                        label = {
                            val diff = (fake_init_val_du_ancien_credits_situation
                                ?: 0) - (out_val.toIntOrNull() ?: 0)
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
        HorizontalDivider(thickness = 3.dp, color = Color.Red)
        HorizontalDivider()
        Text("FireBase")
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.CloudUpload,
                    contentDescription = null,
                    tint = Color(0xFFE65100)
                )
            },
            text = {
                Text(
                    text = PendingAction.But2_Export_M8_Csv_To_FireBase.name,
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            onClick = {
                pendingAction = PendingAction.But2_Export_M8_Csv_To_FireBase
            }
        )
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.CloudDownload,
                    contentDescription = null,
                    tint = Color(0xFFFF6F00)
                )
            },
            text = {
                val fbStatsLine = when {
                    firebaseRowCount == null -> "..."
                    firebaseRowCount == -1   -> "Firebase: خطأ في الاتصال"
                    firebaseRowCount == 0    -> "Firebase: فارغ"
                    else -> "Firebase: $firebaseRowCount | CSV: ${csvRowCount ?: "..."}"
                }
                Column {
                    Text(
                        text = PendingAction.But6_Import_M8_FireBase_To_Csv.name,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = fbStatsLine,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            },
            onClick = {
                pendingAction = PendingAction.But6_Import_M8_FireBase_To_Csv
            }
        )
        HorizontalDivider(thickness = 3.dp, color = Color.Red)
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = null,
                    tint = Color(0xFF1565C0)
                )
            },
            text = {
                Text(
                    text = "But1_Export_M8_Room_To_Csv",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            onClick = {
                pendingAction = PendingAction.But1_Export_M8_Room_To_Csv
            }
        )

        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    tint = Color(0xFF6A1B9A)
                )
            },
            text = {
                val statsLine = when {
                    csvRowCount == null -> "..."
                    csvRowCount == 0    -> "CSV فارغ"
                    else -> "CSV: $csvRowCount | +${csvNewCount} جديد | ↺${csvUpdateCount} تحديث"
                }
                Column {
                    Text(
                        text = PendingAction.But7_DeleteImport_M8Csv_To_Room.name,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = statsLine,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            },
            onClick = {
                pendingAction = PendingAction.But7_DeleteImport_M8Csv_To_Room
            }
        )
        HorizontalDivider()
        DropdownMenuItem(      //<--
            //TODO(1): extract don un separated fichie
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Details,
                    contentDescription = null,
                    tint = Color(0xFF6A1B9A)
                )
            },
            text = {
                Text(
                    text = PendingAction.But8_DeleteAll_M8_Room.name,
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            onClick = {
                pendingAction = PendingAction.But8_DeleteAll_M8_Room
            }
        )
        DropdownMenuItem(       //<--
        //TODO(1): extract don un separated fichie
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Download,
                    contentDescription = null,
                    tint = Color(0xFF6A1B9A)
                )
            },
            text = {
                Text(
                    text = PendingAction.But3_Import_M8Csv_To_Room.name,
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            onClick = {
                pendingAction = PendingAction.But3_Import_M8Csv_To_Room
            }
        )
        HorizontalDivider()

        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    tint = Color(0xFF6A1B9A)
                )
            },
            text = {
                Text(
                    text = PendingAction.But5_Import_M8_Ui_To_Room.name,
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            onClick = {
                pendingAction = PendingAction.But5_Import_M8_Ui_To_Room
            }
        )

        HorizontalDivider()
    }
}

