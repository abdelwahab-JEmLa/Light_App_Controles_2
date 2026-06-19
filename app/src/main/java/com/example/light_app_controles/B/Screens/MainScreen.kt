package com.example.light_app_controles.B.Screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.a.Screens.a.BonVents.Screen.Main_Preview_BonVentEtateScreen
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.a.Screens.b.M3Couleur.Screen.M3CouleurList_Screen
import android.annotation.SuppressLint
import com.example.light_app_controles.Modules.Base.SQL.Daos.AppDatabase

import Working_IN.Feature.a.Test.CleanupScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.remember
import androidx.compose.ui.semantics.semantics
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.a.Main.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button

import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.c.Screens.b.M2Client.Screen.M2ClientList_Screen

import com.example.light_app_controles.ButtonPressedShadowPreview

enum class Feature {
    CleanupScreen,
    M3CouleurList_Screen,
    Credit_Bon_Whatsapp_Sender,
    M2Client_S,
    ButtonPressedShadowPreview,
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
        val activeFeature = remember { Feature.ButtonPressedShadowPreview }
        when (activeFeature) {
            Feature.CleanupScreen -> CleanupScreen(appDatabase = appDatabase)
            Feature.M3CouleurList_Screen -> M3CouleurList_Screen()
            Feature.Credit_Bon_Whatsapp_Sender -> Main_Preview_BonVentEtateScreen()
            Feature.M2Client_S -> M2ClientList_Screen(appDatabase = appDatabase)
            Feature.ButtonPressedShadowPreview -> ButtonPressedShadowPreview()
        }
        FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button(appDatabase = appDatabase)
    }
}
