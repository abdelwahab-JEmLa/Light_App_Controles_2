package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.M10.Actions

import A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog.C.Components.AvertissementDialog
import EntreApps.Shared.Models.Relative_Vents.Models.M10OperationVentCouleur
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Details
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocalFireDepartment
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
import androidx.compose.ui.unit.dp
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Modules.splitCsvLine
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.a.Main.ViewModel.FeatureID1_ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun M10_FragMap_DropdownMenu(
    modifier: Modifier = Modifier,
    vm: FeatureID1_ViewModel,
    expanded: Boolean,
    onDismiss: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    var showExportRoomToCsvConfirm by remember { mutableStateOf(false) }
    var showExportCsvToFirebaseConfirm by remember { mutableStateOf(false) }
    var showImportCsvToRoomConfirm by remember { mutableStateOf(false) }
    var showImportFirebaseToCsvConfirm by remember { mutableStateOf(false) }
    var showDeleteAllRoomConfirm by remember { mutableStateOf(false) }
    var showImportFirebaseToRoomConfirm by remember { mutableStateOf(false) }

    var csvRowCount by remember { mutableStateOf<Int?>(null) }
    var csvNewCount by remember { mutableStateOf<Int?>(null) }
    var csvUpdateCount by remember { mutableStateOf<Int?>(null) }
    var csvRefreshTrigger by remember { mutableStateOf(0) }

    var firebaseRowCount by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        runCatching {
            val counts = vm.setter_LongDatas.get_Firebase_M10_Counts(
                M10OperationVentCouleur.ref_Test
            )
            firebaseRowCount = counts.first
        }.onFailure {
            firebaseRowCount = -1
        }
    }

    LaunchedEffect(vm.active_Datas.list_M10Operation, csvRefreshTrigger) {
        withContext(Dispatchers.IO) {
            val csv = M10OperationVentCouleur.csv_test
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
                    val roomKeys = vm.active_Datas.list_M10Operation
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

    if (showExportRoomToCsvConfirm) {
        AvertissementDialog(
            title        = "Export Room to CSV",
            message      = "سيتم تصدير عمليات Room إلى ملف CSV.\nهل تريد المتابعة؟",
            confirmLabel = "تصدير",
            onConfirm    = {
                showExportRoomToCsvConfirm = false
                coroutineScope.launch(Dispatchers.IO) {
                    vm.setter_LongDatas.export_M10_Room_To_Csv(
                        csv = M10OperationVentCouleur.csv_test,
                    )
                    withContext(Dispatchers.Main) {
                        csvRefreshTrigger++
                        onDismiss()
                    }
                }
            },
            onDismiss    = { showExportRoomToCsvConfirm = false },
        )
    }

    if (showExportCsvToFirebaseConfirm) {
        AvertissementDialog(
            title = "Export CSV to Firebase",
            message = "سيتم رفع بيانات العمليات من ملف CSV إلى Firebase.\nهل تريد المتابعة؟",
            confirmLabel = "رفع",
            onConfirm = {
                showExportCsvToFirebaseConfirm = false
                coroutineScope.launch(Dispatchers.IO) {
                    runCatching {
                        vm.setter_LongDatas.set_scv_M10_au_fireBase(
                            csvFile = M10OperationVentCouleur.csv_test,
                            refDataBase = M10OperationVentCouleur.ref_Test,
                        )
                    }
                    withContext(Dispatchers.Main) { onDismiss() }
                }
            },
            onDismiss = { showExportCsvToFirebaseConfirm = false },
        )
    }

    if (showImportCsvToRoomConfirm) {
        AvertissementDialog(
            title        = "Import CSV to Room",
            message      = "سيتم استيراد العمليات من ملف CSV وتحديث قاعدة البيانات المحلية.\nهل تريد المتابعة؟",
            confirmLabel = "استيراد",
            onConfirm    = {
                showImportCsvToRoomConfirm = false
                coroutineScope.launch(Dispatchers.IO) {
                    vm.setter_LongDatas.import_M10Csv_To_Room(M10OperationVentCouleur.csv_test)
                    vm.reload()
                    withContext(Dispatchers.Main) { onDismiss() }
                }
            },
            onDismiss    = { showImportCsvToRoomConfirm = false },
        )
    }

    if (showImportFirebaseToCsvConfirm) {
        AvertissementDialog(
            title        = "Import Firebase to CSV",
            message      = "سيتم تحميل العمليات من Firebase وحفظها في ملف CSV.\nهل تريد المتابعة؟",
            confirmLabel = "استيراد",
            onConfirm    = {
                showImportFirebaseToCsvConfirm = false
                coroutineScope.launch(Dispatchers.IO) {
                    runCatching {
                        vm.setter_LongDatas.import_M10_FireBase_To_Csv(
                            refDataBase = M10OperationVentCouleur.ref_Test,
                            csvFile     = M10OperationVentCouleur.csv_test,
                        )
                    }.onSuccess {
                        withContext(Dispatchers.Main) { csvRefreshTrigger++ }
                    }
                    withContext(Dispatchers.Main) { onDismiss() }
                }
            },
            onDismiss    = { showImportFirebaseToCsvConfirm = false },
        )
    }

    if (showDeleteAllRoomConfirm) {
        AvertissementDialog(
            title        = "Delete All Room",
            message      = "سيتم حذف جميع العمليات من قاعدة البيانات المحلية بشكل نهائي.\nهل تريد المتابعة؟",
            confirmLabel = "حذف",
            onConfirm    = {
                showDeleteAllRoomConfirm = false
                coroutineScope.launch(Dispatchers.IO) {
                    vm.setter_LongDatas.delete_All_M10()
                    vm.reload()
                    withContext(Dispatchers.Main) { onDismiss() }
                }
            },
            onDismiss    = { showDeleteAllRoomConfirm = false },
        )
    }

    if (showImportFirebaseToRoomConfirm) {
        AvertissementDialog(
            title        = "Import Firebase to Room",
            message      = "سيتم تحميل العمليات مباشرة من Firebase وحفظها في قاعدة البيانات المحلية.\nهل تريد المتابعة؟",
            confirmLabel = "استيراد",
            onConfirm    = {
                showImportFirebaseToRoomConfirm = false
                coroutineScope.launch(Dispatchers.IO) {
                    runCatching {
                        vm.setter_LongDatas.import_M10_FireBase_To_Room(
                            refDataBase = M10OperationVentCouleur.ref_Test,
                        )
                        vm.reload()
                    }
                    withContext(Dispatchers.Main) { onDismiss() }
                }
            },
            onDismiss    = { showImportFirebaseToRoomConfirm = false },
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = modifier.background(Color.White, RoundedCornerShape(8.dp))
    ) {
        HorizontalDivider(thickness = 3.dp, color = Color(0xFFE91E63))
        HorizontalDivider()
        Text("FireBase")
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = Color(0xFFE91E63)
                )
            },
            text = {
                Text(
                    text = "Import Firebase to Room",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            onClick = { showImportFirebaseToRoomConfirm = true }
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
                    text = "Export CSV to Firebase",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            onClick = { showExportCsvToFirebaseConfirm = true }
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
                        text = "Import Firebase to CSV",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = fbStatsLine,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            },
            onClick = { showImportFirebaseToCsvConfirm = true }
        )
        HorizontalDivider(thickness = 3.dp, color = Color(0xFFE91E63))
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
                    text = "Export Room to CSV",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            onClick = { showExportRoomToCsvConfirm = true }
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
                        text = "Import CSV to Room",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = statsLine,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            },
            onClick = { showImportCsvToRoomConfirm = true }
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
                    text = "Delete All Room",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            onClick = { showDeleteAllRoomConfirm = true }
        )
        HorizontalDivider()
    }
}
