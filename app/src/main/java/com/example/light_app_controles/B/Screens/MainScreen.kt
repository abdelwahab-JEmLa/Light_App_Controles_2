package com.example.light_app_controles.B.Screens

import EntreApps.Shared.Models.Components.Ousstad_Tahfid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.light_app_controles.B.Screens.Z.Screens.Apps.App.A_EducationFragment_SeparatedAppsCodingPattern
import com.example.light_app_controles.Floating_DropDownMenuS.Dialoge.Dialog.Z_Content_Buttons.View.A.Main.Floating_Separated_Button
import com.example.light_app_controles.Modules.Base.SQL.Daos.AppDatabase

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    appDatabase: AppDatabase
) {
    var loggedInOusstad by remember { mutableStateOf<Ousstad_Tahfid?>(null) }

    if (loggedInOusstad == null) {
        LoginScreen(onLoginSuccess = { ousstad -> loggedInOusstad = ousstad })
    } else {
        A_EducationFragment_SeparatedAppsCodingPattern(
            appDatabase = appDatabase,
            initialOusstad = loggedInOusstad
        )
        Floating_Separated_Button(
            appDatabase = appDatabase,
            activeOusstad = loggedInOusstad
        )
    }
}

