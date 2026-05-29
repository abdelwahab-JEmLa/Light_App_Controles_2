package Working_IN.Feature.a.Test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.light_app_controles.Modules.Base.SQL.Daos.AppDatabase
import kotlinx.coroutines.launch

@Composable
fun CleanupScreen(
    modifier: Modifier = Modifier,
    appDatabase: AppDatabase = AppDatabase.DatabaseModule.getDatabase(LocalContext.current)
) {
    val coroutineScope = rememberCoroutineScope()
    val bonVentsList by appDatabase.dao_M8BonVent().getAllFlow().collectAsState(initial = emptyList())

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Total Bon Vents: ${bonVentsList.size}")
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                coroutineScope.launch {
                    cleanupOldBonVents_Np(
                        daoM8BonVent = appDatabase.dao_M8BonVent(),
                        bonVents = bonVentsList,
                        on_vent_key = ""
                    )
                }
            }
        ) {
            Text(text = "Cleanup Old Bon Vents")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CleanupScreenPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Total Bon Vents: 42")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {}) {
            Text(text = "Cleanup Old Bon Vents")
        }
    }
}
