package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.Action

import A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog.C.Components.AvertissementDialog
import androidx.compose.runtime.Composable
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.A_ViewModel
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.M8BonVent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun But1_Export_M8_Room_To_Csv(
    vm: A_ViewModel,
    coroutineScope: CoroutineScope,
    onDismiss: () -> Unit,
    onPendingClear: () -> Unit,
    action_definition: PendingAction,
) {
    AvertissementDialog(
        title        = action_definition.name,
        message      = "سيتم تصدير جميع بيانات M8BonVent إلى\nM8BonVent.csv\n" +
                "إذا كان الملف موجوداً سيتم تحديث الصفوف الموجودة وإضافة الجديدة.\n" +
                "هل تريد المتابعة؟",
        confirmLabel = "تصدير",
        onConfirm    = {
            onPendingClear()
            coroutineScope.launch(Dispatchers.IO) {
                val bons = vm.active_Datas.list_M8bon ?: emptyList()

                if (bons.isNotEmpty()) {
                    vm.setter_LongOperations.export_M8_Room_To_Csv(
                        csv = M8BonVent.csv_test,
                    )
                }

                withContext(Dispatchers.Main) { onDismiss() }
            }
        },
        onDismiss    = onPendingClear,
    )
}
