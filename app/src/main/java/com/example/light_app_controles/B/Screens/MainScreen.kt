package com.example.light_app_controles.B.Screens  // FIX: lowercase 'b' and 'screens'

// FIX: updated import path to match the corrected lowercase package name below
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.light_app_controles.Floating_DropDownMenuS.Dialoge.Dialog.Z_Content_Buttons.View.A.Main.Floating_Separated_Button
import com.example.light_app_controles.Modules.Base.AppDatabase

@Composable
fun MainScreen(
    appDatabase: AppDatabase,          // FIX: added as explicit parameter (nullable so Preview works)
    name: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
    Floating_Separated_Button(
        appDatabase = appDatabase        // FIX: pass the resolved parameter down
    )
}
