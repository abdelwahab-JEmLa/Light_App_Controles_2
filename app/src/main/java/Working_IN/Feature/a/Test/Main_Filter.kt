package Working_IN.Feature.a.Test

import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M10OperationVentCouleur
import Working_IN.Feature.Models.Filter_Affichage_Mode_Proto
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

@Composable
fun Main_Filter(
    relative_listM03: List<M3CouleurProduitInfos>,
    query_outline_searcher: String,
    relative_m10_vents: List<M10OperationVentCouleur>?,
    mode: Filter_Affichage_Mode_Proto
) {
    fun filterByQuery(q: String, list: List<M3CouleurProduitInfos>): List<M3CouleurProduitInfos> {
        val lq = q.trim().lowercase()
        return if (lq.isEmpty()) list
        else list.filter {
            it.nomCouleurStrSiSonImageDispo.lowercase().contains(lq) || it.keyID.lowercase()
                .contains(lq) || it.parentBProduitInfosKeyID.lowercase()
                .contains(lq) || it.parentId1ProduitInfosDebugName.lowercase().contains(lq)
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
                val keys = (relative_m10_vents ?: emptyList()).filter { it.quantity > 0 }
                    .map { it.parent_M3CouleurProduit_KeyID }.toSet()
                list.filter { it.keyID in keys }
            }
        }

    val finale_filtred_list by remember {
        derivedStateOf {
            filterByMode(
                mode,
                filterByDepo(
                    filterByQuery(
                        query_outline_searcher, relative_listM03
                    )
                ),
            )
        }
    }

    // ── List ─────────────────────────────────────────────────────────
    Main_List(finale_filtred_list)
}

