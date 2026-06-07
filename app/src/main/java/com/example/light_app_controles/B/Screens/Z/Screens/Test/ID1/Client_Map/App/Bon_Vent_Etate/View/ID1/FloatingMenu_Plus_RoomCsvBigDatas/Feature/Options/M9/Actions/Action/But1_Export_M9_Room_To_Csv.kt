package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.M9.Actions.Action

import A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog.C.Components.AvertissementDialog
import androidx.compose.runtime.Composable
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.M9.Actions.PendingAction_M09
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.a.Main.ViewModel.FeatureID1_ViewModel
import EntreApps.Shared.Models.M09AppCompt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun But1_Export_M9_Room_To_Csv(
    vm: FeatureID1_ViewModel,
    coroutineScope: CoroutineScope,
    onDismiss: () -> Unit,
    onPendingClear: () -> Unit,
    onCsvWritten: () -> Unit,
    action_definition: PendingAction_M09,
) {
    AvertissementDialog(
        title        = action_definition.name,
        message      = "سيتم تصدير حسابات التطبيق Room إلى ملف CSV.\nهل تريد المتابعة؟",
        confirmLabel = "تصدير",
        onConfirm    = {
            onPendingClear()
            coroutineScope.launch(Dispatchers.IO) {
                vm.setter_LongDatas.export_M09AppCompt_Room_To_Csv(
                    csv = M09AppCompt.csv_test,
                )
                withContext(Dispatchers.Main) {
                    onCsvWritten()
                    onDismiss()
                }
            }
        },
        onDismiss    = onPendingClear,
    )
}
