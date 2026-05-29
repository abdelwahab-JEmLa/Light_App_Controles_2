package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.a.Screens.b.M3Couleur.Screen.ViewModel.preview

import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos

/**
 * Stamps the first 8 items of the list with a non-zero [count_Don_Depot] value.
 * Items beyond index 7 are returned unchanged.
 */
fun fake_update_couleurs_count_depo(
    list: List<M3CouleurProduitInfos>,
): List<M3CouleurProduitInfos> =
    list.mapIndexed { index, item ->
        if (index < 8) item.copy(count_Don_Depot = 2) else item
    }
