package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.Preview

import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.Situation_Card_ItemView

@Preview(showBackground = true, name = "BonVentEtateScreen – fake data")
@Composable
fun Preview_BonVentEtateScreen() {
    val fakeNewSituationBons = FAKE_ALL_BONS.filter { bon ->
        bon.parent_M2Client_KeyID == FAKE_CLIENT_KEY &&
                bon.parent_M14VentPeriod_KeyId == FAKE_PERIOD_KEY &&
                bon.etateActuellementEst == M8BonVent.EtateActuellementEst.New_Situation_Credit
    }

    LazyColumn(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items = fakeNewSituationBons, key = { it.keyID }) { bon ->
            Situation_Card_ItemView(
                allBonVentList = FAKE_ALL_BONS,
                relative_M8BonVent = bon,
                onUpdate = {},
                onDelete = {},
            )
        }
    }
}
