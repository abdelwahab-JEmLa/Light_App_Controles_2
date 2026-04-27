package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.light_app_controles.Modules.Base.SQL.AppDatabase
import com.google.protobuf.LazyStringArrayList.emptyList
import kotlinx.coroutines.launch

@Composable
fun Main_Preview_BonVentEtateScreen(
    context: Context = LocalContext.current,
    appDatabase: AppDatabase = AppDatabase.DatabaseModule.getDatabase(context),
    fake_allBonVentList: List<M8BonVent> = FAKE_ALL_BONS,
    parentClientKeyID: String = FAKE_CLIENT_KEY,
    parentPeriodKeyID: String = FAKE_PERIOD_KEY,
    modifier: Modifier = Modifier.Companion,
) {
    val scope = rememberCoroutineScope()
    val collectAsState = appDatabase
        .dao_M8BonVent()
        .getAllFlow()
        .collectAsState(initial = emptyList())

    val relative: M8BonVent? = fake_allBonVentList
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
            // Situation card (New_Situation_Credit) — shows Σ credits − Σ versements
            items(
                items = fake_allBonVentList.filter { bon ->
                    bon.parent_M2Client_KeyID == parentClientKeyID &&
                            bon.parent_M14VentPeriod_KeyId == parentPeriodKeyID &&
                            bon.etateActuellementEst == M8BonVent.EtateActuellementEst.New_Situation_Credit
                },
                key = { it.keyID }
            ) { bon ->
                Situation_Card_ItemView(
                    allBonVentList = fake_allBonVentList,
                    relative_M8BonVent = bon,
                    onUpdate = { updatedBon ->
                        scope.launch { appDatabase.dao_M8BonVent().upsert(updatedBon) }
                    },
                    onDelete = { bonToDelete ->
                        scope.launch { appDatabase.dao_M8BonVent().deleteByKeyId(bonToDelete.keyID) }
                    },
                )
            }

            // Versement / Credit / Demande bons — shown below the situation card
            items(
                items = fake_allBonVentList.filter { bon ->
                    bon.parent_M2Client_KeyID == parentClientKeyID &&
                            bon.parent_M14VentPeriod_KeyId == parentPeriodKeyID &&
                            bon.etateActuellementEst in listOf(
                                M8BonVent.EtateActuellementEst.Versemment,
                                M8BonVent.EtateActuellementEst.Credit,
                                M8BonVent.EtateActuellementEst.Cette_Transaction_Type_Est_Credit,
                                M8BonVent.EtateActuellementEst.Demande_Versemet,
                            )
                },
                key = { it.keyID }
            ) { bon ->
                Y_Credit_And_Versement_ItemView(
                    allBonVentList = fake_allBonVentList,
                    relative_M8BonVent = bon,
                    onUpdate = { updatedBon ->
                        scope.launch { appDatabase.dao_M8BonVent().upsert(updatedBon) }
                    },
                    onDelete = { bonToDelete ->
                        scope.launch { appDatabase.dao_M8BonVent().deleteByKeyId(bonToDelete.keyID) }
                    },
                )
            }
        }
    }
}
