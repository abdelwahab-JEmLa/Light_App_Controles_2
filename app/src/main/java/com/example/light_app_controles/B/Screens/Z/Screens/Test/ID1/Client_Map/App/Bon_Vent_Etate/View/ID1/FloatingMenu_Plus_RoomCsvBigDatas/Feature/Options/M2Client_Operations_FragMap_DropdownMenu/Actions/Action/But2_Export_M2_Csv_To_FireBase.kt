package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.M2Client_Operations_FragMap_DropdownMenu.Actions.Action

import A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog.C.Components.AvertissementDialog
import androidx.compose.runtime.Composable
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.M2Client_Operations_FragMap_DropdownMenu.Actions.PendingAction
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.a.Main.ViewModel.FeatureID1_ViewModel
import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "But2_CsvToFireBase"

@Composable
fun But2_Export_M2_Csv_To_FireBase(
    vm: FeatureID1_ViewModel,
    coroutineScope: CoroutineScope,
    onDismiss: () -> Unit,
    onPendingClear: () -> Unit,
    action_definition: PendingAction,
) {
    AvertissementDialog(
        title = action_definition.name,
        message = "سيتم رفع بيانات M2Client.csv إلى Firebase Realtime Database.\n" +
                "تأكد من أن الملف المحلي محدّث قبل المتابعة.\n" +
                "هل تريد المتابعة؟",
        confirmLabel = "رفع",
        onConfirm = {
            onPendingClear() // FIX: clear pending before launching (was missing)
            coroutineScope.launch(Dispatchers.IO) {
                runCatching {
                    vm.setter_LongDatas.set_scv_m2client_au_fireBase(
                        csvFile = M2Client.csv_test,
                        refDataBase = M2Client.ref_Test,
                    )
                }.onFailure { err ->
                }.onSuccess {
                }
                withContext(Dispatchers.Main) { onDismiss() } // FIX: dismiss after operation (was missing)
            }
        },
        onDismiss = onPendingClear,
    )
}
