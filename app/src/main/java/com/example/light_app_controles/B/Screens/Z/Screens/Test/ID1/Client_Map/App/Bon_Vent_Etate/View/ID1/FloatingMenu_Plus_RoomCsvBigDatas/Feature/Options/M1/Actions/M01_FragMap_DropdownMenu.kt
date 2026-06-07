package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.M1.Actions

import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Details
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Modules.splitCsvLine
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.M1.Actions.Action.But1_Export_M1_Room_To_Csv
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.M1.Actions.Action.But2_Export_M1_Csv_To_FireBase
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.M1.Actions.Action.But3_Import_M1Csv_To_Room
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.M1.Actions.Action.But6_Import_M1_FireBase_To_Csv
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.M1.Actions.Action.But8_DeleteAll_M1_Room
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.M1.Actions.Action.But9_Import_M1_FireBase_To_Room
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.a.Main.ViewModel.FeatureID1_ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

enum class PendingAction_M01(
    val color: Color = Color(0xFFF8F8F8),
    val imageVector: ImageVector = Icons.Default.Numbers
) {
    But1_Export_M01_Room_To_Csv_Entries,
    But2_Export_M01_Csv_To_FireBase_Entries,
    But3_Import_M01Csv_To_Room_Entries,
    But6_Import_M01_FireBase_To_Csv_Entries,
    But_9_Import_M01_FireBase_To_Room_Entries(Color(0xFFE91E63), Icons.Default.LocalFireDepartment),
    But8_DeleteAll_M01_Room_Entries;
}

@Composable
fun M01_FragMap_DropdownMenu(
    modifier: Modifier = Modifier,
    vm: FeatureID1_ViewModel,
    expanded: Boolean,
    onDismiss: () -> Unit,
    on_vent_key: String = "",
    onClick_Lence_Capture: (() -> Unit)? = null,
) {
    val coroutineScope = rememberCoroutineScope()

    var pendingAction by remember { mutableStateOf<PendingAction_M01?>(null) }

    var csvRowCount by remember { mutableStateOf<Int?>(null) }
    var csvNewCount by remember { mutableStateOf<Int?>(null) }
    var csvUpdateCount by remember { mutableStateOf<Int?>(null) }
    var csvRefreshTrigger by remember { mutableStateOf(0) }

    var firebaseRowCount by remember { mutableStateOf<Int?>(null) }

    val fireBase_source_compt_email =
        " Firebase Realtime Database (${EntreApps.Shared.Models.M00CentralParametresOfAllApps.get_Default().fireBase_source_compt_email.gmail})\n"

    LaunchedEffect(Unit) {
        runCatching {
            val counts = vm.setter_LongDatas.get_Firebase_M01Produit_Counts(
                M01Produit.ref_Test
            )
            firebaseRowCount = counts.first
        }.onFailure {
            firebaseRowCount = -1
        }
    }

    LaunchedEffect(vm.active_Datas.list_M1Produit, csvRefreshTrigger) {
        withContext(Dispatchers.IO) {
            val csv = M01Produit.csv_test
            if (csv.exists() && csv.length() > 0L) {
                val lines = csv.readLines().filter { it.isNotBlank() }
                if (lines.size >= 2) {
                    val headers = lines[0].splitCsvLine()
                    val keyIdx = headers.indexOf("keyID")

                    val dataLines = lines.drop(1)
                    val csvKeys = dataLines.mapNotNull { line ->
                        line.splitCsvLine().getOrNull(keyIdx)
                            ?.trim()?.removeSurrounding("\"")
                            ?.takeIf { it.isNotBlank() }
                    }.toSet()
                    val roomKeys = vm.active_Datas.list_M1Produit
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

    pendingAction?.let { action ->
        when (action) {
            PendingAction_M01.But8_DeleteAll_M01_Room_Entries -> {
                But8_DeleteAll_M1_Room(
                    vm = vm,
                    coroutineScope = coroutineScope,
                    onDismiss = onDismiss,
                    onPendingClear = { pendingAction = null },
                    action_definition = action
                )
            }

            PendingAction_M01.But3_Import_M01Csv_To_Room_Entries -> {
                But3_Import_M1Csv_To_Room(
                    vm = vm,
                    coroutineScope = coroutineScope,
                    onDismiss = onDismiss,
                    onPendingClear = { pendingAction = null },
                    action_definition = action
                )
            }

            PendingAction_M01.But2_Export_M01_Csv_To_FireBase_Entries -> {
                But2_Export_M1_Csv_To_FireBase(
                    vm = vm,
                    coroutineScope = coroutineScope,
                    onDismiss = onDismiss,
                    onPendingClear = { pendingAction = null },
                    action_definition = action
                )
            }

            PendingAction_M01.But1_Export_M01_Room_To_Csv_Entries -> {
                But1_Export_M1_Room_To_Csv(
                    vm = vm,
                    coroutineScope = coroutineScope,
                    onDismiss = onDismiss,
                    onPendingClear = { pendingAction = null },
                    onCsvWritten = { csvRefreshTrigger++ },
                    action_definition = action
                )
            }

            PendingAction_M01.But6_Import_M01_FireBase_To_Csv_Entries -> {
                But6_Import_M1_FireBase_To_Csv(
                    vm = vm,
                    coroutineScope = coroutineScope,
                    onDismiss = onDismiss,
                    onPendingClear = { pendingAction = null },
                    onCsvWritten = { csvRefreshTrigger++ },
                    action_definition = action
                )
            }

            PendingAction_M01.But_9_Import_M01_FireBase_To_Room_Entries -> {
                But9_Import_M1_FireBase_To_Room(
                    vm = vm,
                    coroutineScope = coroutineScope,
                    onDismiss = onDismiss,
                    onPendingClear = { pendingAction = null },
                    action_definition = action
                )
            }
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

        HorizontalDivider(thickness = 3.dp, color = Color.Red)
        HorizontalDivider()
        Text("FireBase")
        val action = PendingAction_M01.But_9_Import_M01_FireBase_To_Room_Entries
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = action.imageVector,
                    contentDescription = null,
                    tint = action.color
                )
            },
            text = {
                Text(
                    text = action.name,
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            onClick = { pendingAction = action }
        )
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
                    text = PendingAction_M01.But2_Export_M01_Csv_To_FireBase_Entries.name,
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            onClick = { pendingAction = PendingAction_M01.But2_Export_M01_Csv_To_FireBase_Entries }
        )
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.CloudDownload,
                    contentDescription = null,
                    tint = Color(0xFFE65100)
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
                        text = PendingAction_M01.But6_Import_M01_FireBase_To_Csv_Entries.name + fireBase_source_compt_email,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = fbStatsLine,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            },
            onClick = { pendingAction = PendingAction_M01.But6_Import_M01_FireBase_To_Csv_Entries }
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
                    text = "But1_Export_M01_Room_To_Csv_Entries",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            onClick = { pendingAction = PendingAction_M01.But1_Export_M01_Room_To_Csv_Entries }
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
                    csvRowCount == 0   -> "CSV فارغ"
                    else -> "CSV: $csvRowCount | +${csvNewCount} جديد | ↺${csvUpdateCount} تحديث"
                }
                Column {
                    Text(
                        text = PendingAction_M01.But3_Import_M01Csv_To_Room_Entries.name,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = statsLine,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            },
            onClick = { pendingAction = PendingAction_M01.But3_Import_M01Csv_To_Room_Entries }
        )
        HorizontalDivider()
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Details,
                    contentDescription = null,
                    tint = Color(0xFF6A1B9A)
                )
            },
            text = {
                Text(
                    text = PendingAction_M01.But8_DeleteAll_M01_Room_Entries.name,
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            onClick = { pendingAction = PendingAction_M01.But8_DeleteAll_M01_Room_Entries }
        )
        HorizontalDivider()
    }
}
