package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.a.Screens.b.M3Couleur.Screen.Working_IN.Feature

import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos

/** 30-day sliding window used to decide whether a couleur is still "active". */
private const val LIMITE_PERIOD_MS = 30L * 24 * 60 * 60 * 1_000

/**
 * Keeps only [M3CouleurProduitInfos] whose [M3CouleurProduitInfos.dernier_achant_timeTamp]
 * falls inside the last [LIMITE_PERIOD_MS] milliseconds.
 * Items with a zero timestamp (never purchased) are excluded.
 */
fun List<M3CouleurProduitInfos>.get_filtred_m3_by_limite_period(): List<M3CouleurProduitInfos> {
    val now = System.currentTimeMillis()
    return filter { m3 ->
        m3.dernier_achant_timeTamp > 0 &&
                (now - m3.dernier_achant_timeTamp) <= LIMITE_PERIOD_MS
    }
}
