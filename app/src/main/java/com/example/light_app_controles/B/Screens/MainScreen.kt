package com.example.light_app_controles.B.Screens

import Application4.App.Fragment.ID1.Fragment.A_Compact_Presentoire_App_Produits_App4
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import android.annotation.SuppressLint
import EntreApps.Shared.Modules.Base.AppDatabase

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.semantics.semantics
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.a.Main.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button

enum class Feature {
    CleanupScreen,
    M3CouleurList_Screen,
    Vendeur_App4,

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
        val activeFeature = remember { Feature.Vendeur_App4 }

        when (activeFeature) {  //<--
            Feature.Vendeur_App4 -> A_Compact_Presentoire_App_Produits_App4(
                appDatabase =appDatabase,
            )
            else -> {
                Text("Feature ${activeFeature.name} not implemented yet")
            }
        }
        FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button(appDatabase = appDatabase)
    }
}
