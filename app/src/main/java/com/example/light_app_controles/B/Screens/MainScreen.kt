package com.example.light_app_controles.B.Screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.Floating_Separated_Button
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.Main_Preview_BonVentEtateScreen
import com.example.light_app_controles.Modules.Base.SQL.Daos.AppDatabase

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    appDatabase: AppDatabase,
) {

    Main_Preview_BonVentEtateScreen(
        appDatabase = appDatabase,
    )
}
