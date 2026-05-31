package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.c.Screens.b.M2Client.Screen

import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Main_List(
    relative_list_m2: List<M2Client>,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
    ) {
        items(items = relative_list_m2) { item ->
            M2ClientItem(item = item)
        }
    }
}
