package com.example.light_app_controles.B.Screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.Main_Preview_BonVentEtateScreen
import com.example.light_app_controles.Floating_DropDownMenuS.Dialoge.Dialog.Z_Content_Buttons.View.A.Main.Floating_Separated_Button
import com.example.light_app_controles.Modules.Base.SQL.Daos.AppDatabase

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    appDatabase: AppDatabase,
) {
    // FIX TODO(1): hoisted state that tracks whether the lence-test mode is active.
    // Toggling this from the FAB propagates the action into Main_Preview_BonVentEtateScreen.
    var lenceTestActive by remember { mutableStateOf(false) }

    // FIX TODO(1): shared lambda — the FAB triggers it, the screen reacts to it.
    val onLenceTest: () -> Unit = { lenceTestActive = !lenceTestActive }

    // FIX TODO(1): pass the action so the screen can react (or also trigger) it.
    Main_Preview_BonVentEtateScreen(
        onClick_Lence_Test = onLenceTest,
        lenceTestActive = lenceTestActive,
    )

    // FIX TODO(1): the FAB fires onLenceTest when clicked (click_Lence_Test guard is active
    // because onClick_Lence_Test is non-null — see Floating_Separated_Button).
    Floating_Separated_Button(
        appDatabase = appDatabase,
        onClick_Lence_Test = onLenceTest,
    )
}
