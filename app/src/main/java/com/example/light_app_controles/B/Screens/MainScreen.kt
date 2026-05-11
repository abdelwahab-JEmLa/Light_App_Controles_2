package com.example.light_app_controles.B.Screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.a.Screens.a.BonVents.Screen.Main_Preview_BonVentEtateScreen
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.a.Screens.b.M3Couleur.Screen.M3CouleurList_Screen
import com.example.light_app_controles.Modules.Base.SQL.Daos.AppDatabase

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    appDatabase: AppDatabase,
) {
    val its_dev_bigDatas = true
    if(its_dev_bigDatas) {
        M3CouleurList_Screen()
    }  else
    Main_Preview_BonVentEtateScreen(
        appDatabase = appDatabase,
    )
}
