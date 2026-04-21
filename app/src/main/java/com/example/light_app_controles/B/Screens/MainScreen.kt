package com.example.light_app_controles.B.Screens  // FIX: lowercase 'b' and 'screens'

// FIX: updated import path to match the corrected lowercase package name below
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.Preview.BonVentEtateScreen
import com.example.light_app_controles.Floating_DropDownMenuS.Dialoge.Dialog.Z_Content_Buttons.View.A.Main.Floating_Separated_Button
import com.example.light_app_controles.Modules.Base.SQL.AppDatabase

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    appDatabase: AppDatabase
) {
    BonVentEtateScreen()
    Floating_Separated_Button(
        appDatabase = appDatabase
    )
}
