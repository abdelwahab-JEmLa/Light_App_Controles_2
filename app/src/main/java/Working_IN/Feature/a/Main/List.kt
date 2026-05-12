package Working_IN.Feature.a.Main

import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ColumnScope.List(
    viewModel: ViewModel_M3Features,
    query_outline_searcher: String,
    relative_listM03: List<M3CouleurProduitInfos>
) {
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
                    .filter { it.quantity > 0 }
                    .map { it.parent_M3CouleurProduit_KeyID }.toSet()
                list.filter { it.keyID in keys }
            }
        }

    val finale_filtred_list by remember {
        derivedStateOf {
            filterByMode(
                viewModel.active_Datas.tiger_filterID2_Filter_Affichage_Mode_Proto,
                filterByDepo(filterByQuery(query_outline_searcher, relative_listM03)),
            )
        }
    }

    // ── List ─────────────────────────────────────────────────────────
    LazyColumn(
        modifier = Modifier.Companion
            .weight(1f)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
    ) {
        items(items = finale_filtred_list) { item ->
            M3CouleurItem(item = item, highlight = query_outline_searcher.trim())
        }
    }
}
