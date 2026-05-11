package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.a.Screens.b.M3Couleur.Screen.Working_IN.Feature

import EntreApps.Shared.Models.M09AppCompt
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos


fun List<M3CouleurProduitInfos>.get_filtred_m3_by_limite_active_M9Compt_limite_couleurs_ou_leur_last_achate_est_moin_que_jour(
    activeCompt: M09AppCompt?,
): List<M3CouleurProduitInfos> {
    activeCompt ?: return this   // no active compt → all colours pass

    val thresholdMs =
        activeCompt.limite_couleurs_ou_leur_last_achate_est_moin_que_jour.toLong() *
        24L * 60L * 60L * 1_000L

    val now = System.currentTimeMillis()

    return filter { m3 ->
        m3.dernier_achant_timeTamp > 0L &&
        (now - m3.dernier_achant_timeTamp) <= thresholdMs
    }
}
