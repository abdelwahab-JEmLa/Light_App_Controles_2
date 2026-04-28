package com.example.light_app_controles.B.Screens  // FIX: lowercase 'b' and 'screens'

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.light_app_controles.B.Screens.Z.Screens.Apps.App.A_EducationFragment_SeparatedAppsCodingPattern
import com.example.light_app_controles.Floating_DropDownMenuS.Dialoge.Dialog.Z_Content_Buttons.View.A.Main.Floating_Separated_Button
import com.example.light_app_controles.Modules.Base.SQL.Daos.AppDatabase

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    appDatabase: AppDatabase
) {
    /*Main_Preview_BonVentEtateScreen()*/

    A_EducationFragment_SeparatedAppsCodingPattern(appDatabase=appDatabase)

    Floating_Separated_Button(
        appDatabase = appDatabase
    )
}
