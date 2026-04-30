package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View

import A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog.C.Components.AvertissementDialog
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

private const val M8_CSV_FILE_NAME = "TestDatas/M8BonVent.csv"

/**
 * Confirmation dialog that:
 *  1. Exports all current [M8BonVent] items to a local CSV file.
 *  2. Uploads the same data to Firebase Realtime Database via
 *     [Setter_LongOperations.set_scv_m8_au_fireBase].
 *
 * Extracted from [B_FragMap_DropdownMenu] per TODO(1).
 *
 * @param vm            The screen's [A_ViewModel], used to read [A_ViewModel.active_Datas]
 *                      and [A_ViewModel.setter_LongOperations].
 * @param coroutineScope Scope used to run the IO operations (pass [rememberCoroutineScope]).
 * @param onDismiss     Called after the user cancels *or* after a successful export.
 * @param onPendingClear Called when this dialog should close itself (i.e. set pendingAction = null).
 */
@Composable
fun ExportM8BonsToLocalCsvDialog(
    vm: A_ViewModel,
    coroutineScope: CoroutineScope,
    onDismiss: () -> Unit,
    onPendingClear: () -> Unit,
) {
    AvertissementDialog(
        title = "تصدير البيانات إلى CSV",
        message = "سيتم تصدير جميع بيانات M8BonVent إلى\n$M8_CSV_FILE_NAME\n" +
                "إذا كان الملف موجوداً سيتم تحديث الصفوف الموجودة وإضافة الجديدة.\n" +
                "سيتم أيضاً رفع البيانات إلى قاعدة بيانات Firebase.\n" +
                "هل تريد المتابعة؟",
        confirmLabel = "تصدير",
        onConfirm = {
            onPendingClear()
            coroutineScope.launch(Dispatchers.IO) {
                val bons = vm.active_Datas.list_M8bon ?: emptyList()
                if (bons.isEmpty()) { onDismiss(); return@launch }

                // 1. Export to local CSV
                vm.setter_LongOperations.exportToCsv(
                    datas = bons,
                    fileName = M8_CSV_FILE_NAME,
                )

                // 2. Upload to Firebase Realtime Database
                runCatching {
                    vm.setter_LongOperations.set_scv_m8_au_fireBase(
                        csvFile = File(
                            M00CentralParametresOfAllApps.central_Local_Csv,
                            M8_CSV_FILE_NAME,
                        ),
                        refDataBase = M8BonVent.ref_Test,
                    )
                } // silently swallow; Firebase errors should not block the local export result

                onDismiss()
            }
        },
        onDismiss = onPendingClear,
    )
}
