package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.Action

import A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog.C.Components.AvertissementDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.A_ViewModel
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.FirebaseUploadState
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.M8BonVent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun But2_Export_M8_Csv_To_FireBase(
    vm: A_ViewModel,
    coroutineScope: CoroutineScope,
    onDismiss: () -> Unit,
    onPendingClear: () -> Unit,
    action_definition: PendingAction,
) {
    val uploadState by vm.setter_LongOperations.uploadState.collectAsState()

    when (val state = uploadState) {
        is FirebaseUploadState.InProgress -> {
            FirebaseUploadProgressDialog(done = state.done, total = state.total)
        }

        is FirebaseUploadState.Error -> {
            AvertissementDialog(
                title = "خطأ في الرفع",
                message = state.message,
                confirmLabel = "حسناً",
                onConfirm = { onDismiss() },
                onDismiss = { onDismiss() },
            )
        }

        is FirebaseUploadState.Success -> {
            LaunchedEffect(Unit) { onDismiss() }
        }

        is FirebaseUploadState.Idle -> {
            AvertissementDialog(
                title = action_definition.name,
                message = "سيتم رفع بيانات M8BonVent.csv إلى Firebase Realtime Database.\n" +
                        "تأكد من أن الملف المحلي محدّث قبل المتابعة.\n" +
                        "هل تريد المتابعة؟",
                confirmLabel = "رفع",
                onConfirm = {
                    onPendingClear()
                    coroutineScope.launch(Dispatchers.IO) {
                        runCatching {
                            vm.setter_LongOperations.set_scv_m8_au_fireBase(
                                csvFile = M8BonVent.csv_test,
                                refDataBase = M8BonVent.ref_Test,
                            )
                        }.onFailure {
                        }
                    }
                },
                onDismiss = onPendingClear,
            )
        }
    }
}

@Composable
private fun FirebaseUploadProgressDialog(done: Int, total: Int) {
    val progress = if (total > 0) done.toFloat() / total else 0f
    AlertDialog(
        onDismissRequest = {},
        title = { Text("جارٍ الرفع إلى Firebase…") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth(),
                )
                Text("$done / $total")
            }
        },
        confirmButton = {},
    )
}
