package Working_IN.Feature.a.Main

import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos

/**
 * Marks the first 4 items of the list as echantillants ([its_in_echantiallants] = true).
 * Items beyond index 3 are returned unchanged.
 */
fun fake_update_couleurs_echants(
    list: List<M3CouleurProduitInfos>,
): List<M3CouleurProduitInfos> =
    list.mapIndexed { index, item ->
        if (index < 4) item.copy(its_in_echantiallants = true)
        else item
    }
