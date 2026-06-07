package com.example.light_app_controles.B.Screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.a.Screens.b.M3Couleur.Screen.M3CouleurList_Screen
import android.annotation.SuppressLint
import com.example.light_app_controles.Modules.Base.SQL.Daos.AppDatabase

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.remember
import androidx.compose.ui.semantics.semantics
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.a.Main.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button

enum class Feature {
    CleanupScreen,
    M3CouleurList_Screen,
    Credit_Bon_Whatsapp_Sender,
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    appDatabase: AppDatabase,
) {
    Box(
        modifier = modifier
            .semantics(mergeDescendants = true) {
            }
            .fillMaxSize()
    ) {
        val activeFeature = remember { Feature.M3CouleurList_Screen }
        when (activeFeature) {  //<--
            Feature.M3CouleurList_Screen -> M3CouleurList_Screen()
            else -> {}
        }
        FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button(appDatabase = appDatabase)
    }
}
