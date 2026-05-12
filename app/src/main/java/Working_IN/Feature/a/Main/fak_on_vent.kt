package Working_IN.Feature.a.Main

import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M10OperationVentCouleur

private val dayMs = 24L * 60L * 60L * 1_000L
private val now   get() = System.currentTimeMillis()

// ── M3 colour entries that are currently on sale ──────────────────────────────
val FAKE_ON_VENT_M3: List<M3CouleurProduitInfos> = listOf(
    M3CouleurProduitInfos(
        keyID                        = "fake_on_vent_1",
        debugInfos                   = "on_vent_pass_1",
        nomCouleurStrSiSonImageDispo = "#E53935",
        dernier_achant_timeTamp      = now - 1 * dayMs,
    ),
    M3CouleurProduitInfos(
        keyID                        = "fake_on_vent_2",
        debugInfos                   = "on_vent_pass_2",
        nomCouleurStrSiSonImageDispo = "#1E88E5",
        dernier_achant_timeTamp      = now - 3 * dayMs,
    ),
    M3CouleurProduitInfos(
        keyID                        = "fake_on_vent_3",
        debugInfos                   = "on_vent_pass_3",
        nomCouleurStrSiSonImageDispo = "#43A047",
        dernier_achant_timeTamp      = now - 7 * dayMs,
    ),
    M3CouleurProduitInfos(
        keyID                        = "fake_on_vent_4",
        debugInfos                   = "on_vent_pass_4",
        nomCouleurStrSiSonImageDispo = "#FB8C00",
        dernier_achant_timeTamp      = now - 14 * dayMs,
    ),
)

// ── One M10 vent operation per colour, so the Panie filter can find them ──────
val FAKE_ON_VENT: List<M10OperationVentCouleur> =
    FAKE_ON_VENT_M3.map { m3 ->
        M10OperationVentCouleur(
            keyID                          = "fake_m10_${m3.keyID}",
            parent_M3CouleurProduit_KeyID  = m3.keyID,
            parent_M3CouleurProduit_DebugInfos = m3.debugInfos,
            etateActuellementEst           = M10OperationVentCouleur.EtateActuellementEst.ParentBonVentConfirme,
        )
    }
