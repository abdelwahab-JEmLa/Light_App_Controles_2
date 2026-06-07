package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.M3.Actions.Action

import A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog.C.Components.AvertissementDialog
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import androidx.compose.runtime.Composable
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.M3.Actions.PendingAction_M03
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.a.Main.ViewModel.FeatureID1_ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "But2_CsvToFireBase"

@Composable
fun But2_Export_M03_Csv_To_FireBase(
    vm: FeatureID1_ViewModel,
    coroutineScope: CoroutineScope,
    onDismiss: () -> Unit,
    onPendingClear: () -> Unit,
    action_definition: PendingAction_M03,
) {
    AvertissementDialog(
        title = action_definition.name,
        message = "سيتم رفع بيانات M3CouleurProduitInfos.csv إلى Firebase Realtime Database.\n" +
                "تأكد من أن الملف المحلي محدّث قبل المتابعة.\n" +
                "هل تريد المتابعة؟",
        confirmLabel = "رفع",
        onConfirm = {
            onPendingClear() // FIX: clear pending before launching (was missing)
            coroutineScope.launch(Dispatchers.IO) {
                runCatching {
                    vm.setter_LongDatas.set_scv_M03_au_fireBase(
                        csvFile = M3CouleurProduitInfos.csv_test,
                        refDataBase = M3CouleurProduitInfos.ref_Test,
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
