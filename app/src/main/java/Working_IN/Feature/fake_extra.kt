package Working_IN.Feature

import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos

fun fake_extra(
    dao_datas: MutableList<M3CouleurProduitInfos>,
): List<M3CouleurProduitInfos> {
    val dayMs = 24L * 60L * 60L * 1_000L
    val now = System.currentTimeMillis()
    val fakeExtras = dao_datas
        .shuffled()
        .take(15)
        .mapIndexed { i, real ->
            real.copy(
                dernier_achant_timeTamp = if (i < 3)
                    now - (i + 1) * 3 * dayMs
                else
                    now - (31 + (i + 1)) * dayMs,
            )
        }
    return fakeExtras
}
