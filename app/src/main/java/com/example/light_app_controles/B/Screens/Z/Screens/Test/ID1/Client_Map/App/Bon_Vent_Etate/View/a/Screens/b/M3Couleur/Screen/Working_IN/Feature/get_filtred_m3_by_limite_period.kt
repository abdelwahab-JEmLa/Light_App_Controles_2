package EntreApps.Shared.Models.Relative_Produits.Models.Functions

import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M14VentPeriode

/**
 * Garde uniquement les [M3CouleurProduitInfos] dont le [M3CouleurProduitInfos.dernier_achant_timeTamp]
 * est postérieur au début de la période la plus récente ayant
 * [M14VentPeriode.its_limite_active_couleurs] == true ET [M14VentPeriode.EtateActuellementEst.CONFIRME].
 *
 * Si aucune telle période n'existe, la liste est retournée intacte.
 */
fun List<M3CouleurProduitInfos>.get_filtred_m3_by_limite_period(
    periods: List<M14VentPeriode>,
): List<M3CouleurProduitInfos> {
    val limitePeriod = periods
        .filter {
            it.its_limite_active_couleurs
        }
        .maxByOrNull { it.creationTimestamp }
        ?: return this

    return filter { m3 ->
        m3.dernier_achant_timeTamp >= limitePeriod.creationTimestamp
    }
}
