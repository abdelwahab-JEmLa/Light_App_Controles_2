package Working_IN.Feature.a.Main

import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M10OperationVentCouleur

/**
 * Builds a fake [M10OperationVentCouleur] list from a given M3 list.
 * Called by the ViewModel as `FAKE_ON_VENT(dao_list_m3)`.
 */
fun FAKE_ON_VENT(m3List: List<M3CouleurProduitInfos>): List<M10OperationVentCouleur> =
    m3List
        .take(2)
        .map { m3 ->
            M10OperationVentCouleur(
                keyID = "fake_m10_${m3.keyID}",
                parent_M3CouleurProduit_KeyID = m3.keyID,
                parent_M3CouleurProduit_DebugInfos = m3.debugInfos,
                etateActuellementEst = M10OperationVentCouleur.EtateActuellementEst.ParentBonVentConfirme,
                quantity = 3,
            )
        } + M10OperationVentCouleur(
        keyID = "fake_m11",
        parent_M3CouleurProduit_KeyID = m3List[2].keyID,
        parent_M3CouleurProduit_DebugInfos = m3List[2].debugInfos,
        etateActuellementEst = M10OperationVentCouleur.EtateActuellementEst.ParentBonVentConfirme,
        quantity = 4,
    )

