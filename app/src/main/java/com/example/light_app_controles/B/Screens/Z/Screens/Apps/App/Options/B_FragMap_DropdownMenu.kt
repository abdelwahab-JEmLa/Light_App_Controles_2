package Application5.App.Options

import A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog.C.Components.AvertissementDialog
import A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog.C.Components.SyncReport
import Application5.App.A_ViewModel_SeparatedAppsCodingPattern
import Application5.App.View.DropDownItems.View.But2.DropDownItem_Imprime_pdf_communication_ac_parent
import Application5.App.View.DropDownItems.View.But4.DropDownItem_Imprime_pdf_List_Talaba
import Application5.App.View.DropDownItems.View.But5.DropDownItem_Imprime_pdf_Case_A_Cochet
import Application5.App.View.DropDownItems.View.But9.DropDownItem_Send_Cards_WhatsApp_Parent
import com.example.light_app_controles.Modules.Base.SQL.Daos.AppDatabase
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
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

private enum class PendingAction {
    ImprimePdfCommunicationParent
}

@Composable
fun B_FragMap_DropdownMenu_App5(
    vm : A_ViewModel_SeparatedAppsCodingPattern,
    appDatabase: AppDatabase,
    expanded: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var organizeDropBoxProgress by remember { mutableStateOf<Float?>(null) }
    var organizeLocalProgress by remember { mutableStateOf<Float?>(null) }
    var syncImages2Progress by remember { mutableStateOf<Float?>(null) }
    var syncImages2Label by remember { mutableStateOf("") }
    var updateTimestampsProgress by remember { mutableStateOf<Float?>(null) }
    var pendingAction by remember { mutableStateOf<PendingAction?>(null) }
    var syncReport by remember { mutableStateOf<SyncReport?>(null) }


    pendingAction?.let { action ->
        when (action) {

            // FIX (2): Replaced dangling `PendingAction.` with the correct entry and
            // updated dialog title / message / confirmLabel for button 2.
            PendingAction.ImprimePdfCommunicationParent -> AvertissementDialog(
                title = "طباعة بطاقات التواصل مع الولي",
                message = "سيتم إنشاء بطاقات PDF لطلاب اليوم الحاضرين وحفظها في التنزيلات. " +
                        "كما سيتم تحويلها إلى صور JPG جاهزة للإرسال عبر واتساب. هل تريد المتابعة؟",
                confirmLabel = "طباعة",
                onConfirm = {
                    pendingAction = null
                    // Actual PDF generation is self-managed inside
                    // DropDownItem_Imprime_pdf_communication_ac_parent below.
                    // Dismissing this dialog is enough to let the user tap the button.
                },
                onDismiss = { pendingAction = null }
            )

            // Note: Fab_CleanupM8AndM10 is NOT routed through pendingAction because it
            // renders its own sub-DropdownMenu with a built-in 2-tap confirmation guard
            // (confirmM8M10 state). It lives directly inside the DropdownMenu below.
        }
    }

    val anyRunning = organizeDropBoxProgress != null
            || organizeLocalProgress != null
            || syncImages2Progress != null
            || updateTimestampsProgress != null

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = modifier.background(Color.White, RoundedCornerShape(8.dp))
    ) {
        // But 2 — بطاقة التواصل مع الولي (PDF individual per student)
        DropDownItem_Imprime_pdf_communication_ac_parent(viewModel = vm)
        HorizontalDivider()

        // But 4 — قائمة الطلبة (PDF table of all students)
        DropDownItem_Imprime_pdf_List_Talaba(aCentralFacade = vm)
        HorizontalDivider()

        // But 5 — شبكة الحضور (checkbox grid PDF, 20 pages)
        DropDownItem_Imprime_pdf_Case_A_Cochet(aCentralFacade = vm)
        HorizontalDivider()

        // But 9 — إرسال البطاقات عبر واتساب (send today's JPG cards via WhatsApp)
        DropDownItem_Send_Cards_WhatsApp_Parent(aCentralFacade = vm)

    }
}
