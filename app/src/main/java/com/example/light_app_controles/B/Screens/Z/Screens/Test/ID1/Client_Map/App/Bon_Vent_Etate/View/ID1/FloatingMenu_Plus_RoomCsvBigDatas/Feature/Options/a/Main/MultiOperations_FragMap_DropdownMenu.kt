package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.a.Main

import A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog.C.Components.AvertissementDialog
import EntreApps.Shared.Models.M09AppCompt
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M14VentPeriode
import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.a.Main.ViewModel.FeatureID1_ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun MultiOperations_FragMap_DropdownMenu(
    modifier: Modifier = Modifier,
    vm: FeatureID1_ViewModel,
    expanded: Boolean,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var showMultiImportFirebaseToCsvConfirm by remember { mutableStateOf(false) }
    var showMultiImportCsvToRoomConfirm by remember { mutableStateOf(false) }

    if (showMultiImportFirebaseToCsvConfirm) {
        AvertissementDialog(
            title        = "Multi Firebase to CSV",
            message      = "سيتم تحميل البيانات لجميع الجداول (M1, M2, M3, M8, M9, M10, M13, M14) من Firebase وحفظها في ملفات CSV.\nهل تريد المتابعة؟",
            confirmLabel = "تحميل الكل",
            onConfirm    = {
                showMultiImportFirebaseToCsvConfirm = false
                coroutineScope.launch(Dispatchers.IO) {
                    runCatching {
                        // 1. M1 Produit
                        vm.setter_LongDatas.import_M01Produit_FireBase_To_Csv(
                            refDataBase = M01Produit.ref_Test,
                            csvFile     = M01Produit.csv_test
                        )
                        // 2. M2 Client
                        vm.setter_LongDatas.import_M2Client_FireBase_To_Csv(
                            refDataBase = M2Client.ref_Test,
                            csvFile     = M2Client.csv_test
                        )
                        // 3. M3 Couleur
                        vm.setter_LongDatas.import_M03_FireBase_To_Csv(
                            refDataBase = M3CouleurProduitInfos.ref_Test,
                            csvFile     = M3CouleurProduitInfos.csv_test
                        )
                        // 4. M8 BonVent
                        vm.setter_LongDatas.import_M8_FireBase_To_Csv(
                            refDataBase = M8BonVent.ref_Test,
                            csvFile     = M8BonVent.csv_test
                        )
                        // 5. M10 Operation
                        vm.setter_LongDatas.import_M10_FireBase_To_Csv(
                            refDataBase = M10OperationVentCouleur.ref_Test,
                            csvFile     = M10OperationVentCouleur.csv_test
                        )
                        // 6. M13 Tarification
                        vm.setter_LongDatas.import_M13_FireBase_To_Csv(
                            refDataBase = M13TarificationInfos.ref_Test,
                            csvFile     = M13TarificationInfos.csv_test
                        )
                        // 7. M14 VentPeriode
                        vm.setter_LongDatas.import_M14_FireBase_To_Csv(
                            refDataBase = M14VentPeriode.ref_Test,
                            csvFile     = M14VentPeriode.csv_test
                        )
                        // 8. M9 AppCompt
                        vm.setter_LongDatas.import_M09AppCompt_FireBase_To_Csv(
                            refDataBase = M09AppCompt.ref_Test,
                            csvFile     = M09AppCompt.csv_test
                        )
                    }.onSuccess {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "تم تحميل جميع الجداول إلى CSV بنجاح ✓", Toast.LENGTH_SHORT).show()
                        }
                    }.onFailure { e ->
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "خطأ في تحميل البيانات: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                    withContext(Dispatchers.Main) { onDismiss() }
                }
            },
            onDismiss    = { showMultiImportFirebaseToCsvConfirm = false },
        )
    }

    if (showMultiImportCsvToRoomConfirm) {
        AvertissementDialog(
            title        = "Multi CSV to Room",
            message      = "سيتم استيراد البيانات لجميع الجداول (M1, M2, M3, M8, M9, M10, M13, M14) من ملفات CSV وتحديث قاعدة البيانات المحلية.\nهل تريد المتابعة؟",
            confirmLabel = "استيراد الكل",
            onConfirm    = {
                showMultiImportCsvToRoomConfirm = false
                coroutineScope.launch(Dispatchers.IO) {
                    runCatching {
                        // 1. M1 Produit
                        vm.setter_LongDatas.import_M01ProduitCsv_To_Room(M01Produit.csv_test)
                        // 2. M2 Client
                        vm.setter_LongDatas.import_M2ClientCsv_To_Room(M2Client.csv_test)
                        // 3. M3 Couleur
                        vm.setter_LongDatas.import_M03Csv_To_Room(M3CouleurProduitInfos.csv_test)
                        // 4. M8 BonVent
                        vm.setter_LongDatas.import_M8Csv_To_Room(M8BonVent.csv_test)
                        // 5. M10 Operation
                        vm.setter_LongDatas.import_M10Csv_To_Room(M10OperationVentCouleur.csv_test)
                        // 6. M13 Tarification
                        vm.setter_LongDatas.import_M13Csv_To_Room(M13TarificationInfos.csv_test)
                        // 7. M14 VentPeriode
                        vm.setter_LongDatas.import_M14Csv_To_Room(M14VentPeriode.csv_test)
                        // 8. M9 AppCompt
                        vm.setter_LongDatas.import_M09AppComptCsv_To_Room(M09AppCompt.csv_test)
                    }.onSuccess {
                        vm.reload()
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "تم استيراد جميع ملفات CSV إلى Room بنجاح ✓", Toast.LENGTH_SHORT).show()
                        }
                    }.onFailure { e ->
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "خطأ في استيراد البيانات: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                    withContext(Dispatchers.Main) { onDismiss() }
                }
            },
            onDismiss    = { showMultiImportCsvToRoomConfirm = false },
        )
    }

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = modifier.background(Color.White, RoundedCornerShape(8.dp))
    ) {
        HorizontalDivider(thickness = 3.dp, color = Color(0xFF37474F))
        HorizontalDivider()
        Text("Batch Operations")
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.CloudDownload,
                    contentDescription = null,
                    tint = Color(0xFFE65100)
                )
            },
            text = {
                Text(
                    text = "Multi Firebase to CSV",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            onClick = { showMultiImportFirebaseToCsvConfirm = true }
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
                Text(
                    text = "Multi CSV to Room",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            onClick = { showMultiImportCsvToRoomConfirm = true }
        )
        HorizontalDivider()
    }
}
