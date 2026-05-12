package Working_IN.Feature.a.Main

import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos

/**
 * Stamps the first 8 items of the list with a non-zero [count_Don_Depot] value.
 * Items beyond index 7 are returned unchanged.
 */
fun fake_update_couleurs_count_depo(
    list: List<M3CouleurProduitInfos>,
): List<M3CouleurProduitInfos> =
    list.mapIndexed { index, item ->
        if (index < 8) item.copy(count_Don_Depot = (index + 1) * 3)
        else item
    }
