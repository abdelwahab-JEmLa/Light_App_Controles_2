package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FloatingMenu_Plus_RoomCsvBigDatas.Feature.Options.M3.Actions.Action

import A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog.C.Components.AvertissementDialog
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import androidx.compose.runtime.Composable
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FloatingMenu_Plus_RoomCsvBigDatas.Feature.Options.M03Bon_Operations_FragMap_DropdownMenu.Actions.PendingAction_M03
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FloatingMenu_Plus_RoomCsvBigDatas.Feature.Options.M3.Actions.PendingAction_M03
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.a.Screens.a.BonVents.Screen.ViewModel.A_ViewModel
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.b.Models.M3CouleurProduitInfos
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun But1_Export_M03_Room_To_Csv(
    vm: A_ViewModel,
    coroutineScope: CoroutineScope,
    onDismiss: () -> Unit,
    onPendingClear: () -> Unit,
    onCsvWritten: () -> Unit,
    action_definition: PendingAction_M03,
) {
    AvertissementDialog(
        title        = action_definition.name,
        message      = "سيتم تصدير جميع بيانات M3CouleurProduitInfos إلى\nM3CouleurProduitInfos.csv\n" +
                "إذا كان الملف موجوداً سيتم تحديث الصفوف الموجودة وإضافة الجديدة.\n" +
                "هل تريد المتابعة؟",
        confirmLabel = "تصدير",
        onConfirm    = {
            onPendingClear()
            coroutineScope.launch(Dispatchers.IO) {
                vm.setter_LongOperations.export_M03_Room_To_Csv(
                    csv = M3CouleurProduitInfos.csv_test,
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
