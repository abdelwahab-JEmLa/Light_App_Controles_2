package Working_IN.Feature

import EntreApps.Shared.Models.M09AppCompt
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos

fun List<M3CouleurProduitInfos>.filter_passive_datas(
    active_M9Compt: M09AppCompt,
): List<M3CouleurProduitInfos> {
    val limitMs =
        active_M9Compt.limite_couleurs_ou_leur_last_achate_est_moin_que_jour * 24L * 60L * 60L * 1_000L
    val now = System.currentTimeMillis()
    return filter { m3 ->
        m3.dernier_achant_timeTamp > 0L &&
                (now - m3.dernier_achant_timeTamp) < limitMs
    }
}
