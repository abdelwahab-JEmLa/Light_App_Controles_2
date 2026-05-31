package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.c.Screens.b.M2Client.Screen

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.a.Main.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button
import com.example.light_app_controles.Modules.Base.SQL.Daos.AppDatabase
import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import EntreApps.Shared.Models.M00CentralParametresOfAllApps

@Composable
fun M2ClientList_Screen(
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current,
    appDatabase: AppDatabase = AppDatabase.DatabaseModule.getDatabase(context),
    viewModel: ViewModel_M3Features = viewModel(
        factory = viewModelFactory { initializer { ViewModel_M3Features(appDatabase = appDatabase) } }
    )
) {
    val focusManager = LocalFocusManager.current

    val relative_listM02 by remember {
        derivedStateOf { viewModel.active_Datas.list_M02 ?: emptyList() }
    }

    var its_lanceRapide_develepment_de_searche by remember { mutableStateOf("zoh") }

    val initialQuery = if (M00CentralParametresOfAllApps.get_Default().its_lanceRapide_develepment) {
        its_lanceRapide_develepment_de_searche
    } else {
        ""
    }
    var query by remember { mutableStateOf(initialQuery) }

    Box(
        modifier = modifier
            .semantics(mergeDescendants = true) {
            }
            .fillMaxSize()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Header ──────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF6A1B9A))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "M2Client — ${relative_listM02.size} Clients",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }

            // ── Search field ─────────────────────────────────────────────────
            OutlinedTextField(                    //<--
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                placeholder = {
                    Text(
                        "بحث بالاسم أو الهاتف أو المعرف",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF9E9E9E),
                    )
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF6A1B9A))
                },
                trailingIcon = {
                    if (query.isNotEmpty()) IconButton(onClick = { query = "" }) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = null,
                            tint = Color(0xFF9E9E9E),
                        )
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6A1B9A),
                    unfocusedBorderColor = Color(0xFFCE93D8),
                    cursorColor = Color(0xFF6A1B9A),
                ),
            )

            AnimatedVisibility(visible = viewModel.active_Datas.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    color = Color(0xFF6A1B9A),
                    trackColor = Color(0xFFCE93D8),
                )
            }

            Main_ListFilter(
                relative_listM02,
                query
            )
        }
    }
}

