package Working_IN.Feature.a.Main

import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import android.content.Context
import android.util.Log.i
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.a.Main.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button
import com.example.light_app_controles.Modules.Base.SQL.Daos.AppDatabase

@Composable
fun M3CouleurList_Screen(
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current,
    appDatabase: AppDatabase = AppDatabase.DatabaseModule.getDatabase(context),
    viewModel: ViewModel_M3Features = viewModel(
        factory = viewModelFactory { initializer { ViewModel_M3Features(appDatabase = appDatabase) } }
    )
) {
    val focusManager = LocalFocusManager.current
    val relative_listM03 = remember(viewModel.active_Datas.list_M03) {
        viewModel.active_Datas.list_M03 ?: emptyList()
    }
    var query by remember { mutableStateOf("") }

    fun filterByQuery(q: String, list: List<M3CouleurProduitInfos>): List<M3CouleurProduitInfos> {
        val lq = q.trim().lowercase()
        return if (lq.isEmpty()) list
        else list.filter {
            it.nomCouleurStrSiSonImageDispo.lowercase().contains(lq) ||
                    it.keyID.lowercase().contains(lq) ||
                    it.parentBProduitInfosKeyID.lowercase().contains(lq) ||
                    it.parentId1ProduitInfosDebugName.lowercase().contains(lq)
        }
    }

    fun filterByDepo(list: List<M3CouleurProduitInfos>): List<M3CouleurProduitInfos> {
        return list.filter { it.count_Don_Depot > 0 }
    }

    fun filterByMode(mode: Filter_Affichage_Mode_Proto, list: List<M3CouleurProduitInfos>) =
        when (mode) {
            Filter_Affichage_Mode_Proto.Tablette_Produits_Seulement -> list.filter { !it.its_in_echantiallants }
            Filter_Affichage_Mode_Proto.Echants_Seulement -> list.filter { it.its_in_echantiallants }
            Filter_Affichage_Mode_Proto.Tablette_Et_Echants -> list
            Filter_Affichage_Mode_Proto.Panie -> {
                val keys = (viewModel.active_Datas.list_M10 ?: emptyList())
                    .map { it.parent_M3CouleurProduit_KeyID }.toSet()
                list.filter { it.keyID in keys }
            }
        }

    // Key on relative_listM03 so the derivedStateOf lambda is rebuilt whenever the
    // list reference changes (e.g. after the coroutine in reload() completes).
    val byQuery by remember(relative_listM03) {
        derivedStateOf {
            filterByQuery(
                query,
                relative_listM03
            )
        }
    }
    val byDepo by remember { derivedStateOf { filterByDepo(byQuery) } }
    val byMode by remember {
        derivedStateOf {
            filterByMode(
                viewModel.active_Datas.tiger_filterID2_Filter_Affichage_Mode_Proto,
                byDepo,
            )
        }
    }

    val finale_filtred_list by remember { derivedStateOf { byMode } }

    Box(
        modifier = modifier
            .semantics(mergeDescendants = true) {
                set(
                    value = byQuery
                        .filter { it.count_Don_Depot > 0 }
                        .map { it.parentId1ProduitInfosDebugName to it.count_Don_Depot },
                    key = SemanticsPropertyKey("")
                )

                set(value = relative_listM03, key = SemanticsPropertyKey("relative_listM03"))
                set(
                    value = finale_filtred_list,
                    key = SemanticsPropertyKey("finale_filtred_list")
                )
            }
            .fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF6A1B9A))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "M3Couleur — ${finale_filtred_list.size} / ${relative_listM03.size}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                placeholder = {
                    Text(
                        "بحث بالاسم / keyID / parent M1 key",
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
                            tint = Color(0xFF9E9E9E)
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

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            ) {
                items(items = finale_filtred_list) { item ->
                    M3CouleurItem(item = item, highlight = query.trim())
                }
            }
        }

        FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button(appDatabase = appDatabase)
    }
}
