package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.Preview

import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.Situation_Card_ItemView
import com.example.light_app_controles.Modules.Base.SQL.AppDatabase
import kotlinx.coroutines.launch

@Composable
fun BonVentEtateScreen(
    context: Context = LocalContext.current,
    appDatabase: AppDatabase = AppDatabase.DatabaseModule.getDatabase(context),
    parentClientKeyID: String = FAKE_CLIENT_KEY,
    parentPeriodKeyID: String = FAKE_PERIOD_KEY,
    modifier: Modifier = Modifier.Companion,
) {
    val scope = rememberCoroutineScope()

    val allBonVentList: List<M8BonVent> by appDatabase
        .dao_M8BonVent()
        .getAllFlow()
        .collectAsState(initial = emptyList())

    val relative: M8BonVent? = allBonVentList
        .filter { bon ->
            bon.parent_M2Client_KeyID == parentClientKeyID &&
                    bon.parent_M14VentPeriod_KeyId == parentPeriodKeyID &&
                    bon.etateActuellementEst == M8BonVent.EtateActuellementEst.New_Situation_Credit
        }
        .maxByOrNull { it.creationTimestamps }

    Column(modifier = modifier.fillMaxSize()) {
        if (relative == null) {
            Text(
                text = "لا توجد حالة دين جديدة",
                color = Color.Companion.Gray,
                fontWeight = FontWeight.Companion.Medium,
                modifier = Modifier.Companion.padding(16.dp)
            )
            return
        }

        LazyColumn(
            modifier = Modifier.Companion
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = allBonVentList.filter { bon ->
                    bon.parent_M2Client_KeyID == parentClientKeyID &&
                            bon.parent_M14VentPeriod_KeyId == parentPeriodKeyID &&
                            bon.etateActuellementEst == M8BonVent.EtateActuellementEst.New_Situation_Credit
                },
                key = { it.keyID }
            ) { bon ->
                Situation_Card_ItemView(
                    allBonVentList = allBonVentList,
                    relative_M8BonVent = bon,
                    onUpdate = { updatedBon ->
                        // FIX: was referencing undefined `dao` — route through appDatabase
                        scope.launch { appDatabase.dao_M8BonVent().upsert(updatedBon) }
                    },
                    onDelete = { bonToDelete ->
                        scope.launch {
                            appDatabase.dao_M8BonVent().deleteByKeyId(bonToDelete.keyID)
                        }
                    },
                )
            }
        }
    }
}
