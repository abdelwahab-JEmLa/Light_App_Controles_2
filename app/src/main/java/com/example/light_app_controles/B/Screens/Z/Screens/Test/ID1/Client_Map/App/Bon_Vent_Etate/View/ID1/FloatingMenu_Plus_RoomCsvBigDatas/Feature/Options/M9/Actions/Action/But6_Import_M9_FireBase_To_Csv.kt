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
fun But6_Import_M9_FireBase_To_Csv(
    vm: FeatureID1_ViewModel,
    coroutineScope: CoroutineScope,
    onDismiss: () -> Unit,
    onPendingClear: () -> Unit,
    onCsvWritten: () -> Unit,
    action_definition: PendingAction_M09,
) {
    AvertissementDialog(
        title        = action_definition.name,
        message      = "سيتم تحميل حسابات التطبيق من Firebase وحفظها في ملف CSV.\nهل تريد المتابعة؟",
        confirmLabel = "استيراد",
        onConfirm    = {
            onPendingClear()
            coroutineScope.launch(Dispatchers.IO) {
                runCatching {
                    vm.setter_LongDatas.import_M09AppCompt_FireBase_To_Csv(
                        refDataBase = M09AppCompt.ref_Test,
                        csvFile     = M09AppCompt.csv_test,
                    )
                }.onSuccess {
                    withContext(Dispatchers.Main) { onCsvWritten() }
                }
                withContext(Dispatchers.Main) { onDismiss() }
            }
        },
        onDismiss    = onPendingClear,
    )
}
