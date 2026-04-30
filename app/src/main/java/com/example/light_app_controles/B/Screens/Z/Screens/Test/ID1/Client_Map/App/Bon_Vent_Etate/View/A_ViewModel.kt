package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View

import EntreApps.Shared.Models.M09AppCompt
import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.light_app_controles.Modules.Base.SQL.Daos.AppDatabase

@Stable
class ActiveDatas {
    var active_M9Compt: M09AppCompt? by mutableStateOf(null)

    var list_M8bon: List<M8BonVent>? by mutableStateOf(null)
    var focused_M2Client: M2Client? by mutableStateOf(null)
    var focused_prriod: String? by mutableStateOf(null) // holds parent_M14VentPeriod_KeyId
}

@SuppressLint("StaticFieldLeak")
class A_ViewModel(
    private val context: Context,
) : ViewModel() {
    val activeCentralValues = ActiveDatas()

    /** Set to true by [ajoute_credit_et_affiche_compos_image] to signal the screen to capture. */
    var captureRequested by mutableStateOf(false)

    init {
        activeCentralValues.list_M8bon = FAKE_ALL_BONS
        // Replace with real M2Client lookup when a real data source is wired in
        activeCentralValues.focused_M2Client = null
        activeCentralValues.focused_prriod = FAKE_PERIOD_KEY
    }

    override fun onCleared() {
        super.onCleared()
    }

    /**
     * Adds a [Versemment] bon for [montant], then subtracts [montant] from the latest
     * [M8BonVent.EtateActuellementEst.New_Situation_Credit]'s [M8BonVent.montant_principale_du_type].
     * Afterwards sets [captureRequested] = true so the screen triggers a capture + dialog.
     */
    fun ajoute_credit_et_affiche_compos_image(
        montant: Double,
        clientKey: String = FAKE_CLIENT_KEY,
        periodKey: String = FAKE_PERIOD_KEY,
    ) {
        // One stable base timestamp so every item created in this call is
        // guaranteed to differ by exactly 1 second from the next.
        val baseTs = System.currentTimeMillis()
        val currentList = activeCentralValues.list_M8bon?.toMutableList() ?: mutableListOf()

        BonVentFlowLogger.vmEntry(
            montant = montant,
            clientKey = clientKey,
            periodKey = periodKey,
            listSize = currentList.size,
        )

        // 1. Create and add the new Versement bon  (t + 0 s)
        val versementBon = M8BonVent(
            keyID = "fake_key_versement_$baseTs",
            parent_M2Client_KeyID = clientKey,
            parent_M14VentPeriod_KeyId = periodKey,
            etateActuellementEst = M8BonVent.EtateActuellementEst.Versemment,
            creationTimestamps = baseTs,
            versement_fait = montant,
        )
        currentList.add(versementBon)
        BonVentFlowLogger.versementCreated(key = versementBon.keyID, versementFait = montant)

        // 2. Read the current principal from the latest New_Situation_Credit — do NOT mutate it.
        //    The old record is kept as a historical snapshot.
        val latestSit = currentList
            .filter {
                it.parent_M2Client_KeyID == clientKey &&
                        it.parent_M14VentPeriod_KeyId == periodKey &&
                        it.etateActuellementEst == M8BonVent.EtateActuellementEst.New_Situation_Credit
            }
            .maxByOrNull { it.creationTimestamps }

        BonVentFlowLogger.latestSitFound(
            found = latestSit != null,
            key = latestSit?.keyID,
            oldMontant = latestSit?.montant_principale_du_type,
        )

        val newMontant = (latestSit?.montant_principale_du_type ?: 0.0) - montant

        // New situation bon is created 1 second after the versement bon (t + 1 s)
        val newSituationBon = M8BonVent(
            keyID = "fake_key_new_sit_${baseTs + 1_000L}",
            parent_M2Client_KeyID = clientKey,
            parent_M14VentPeriod_KeyId = periodKey,
            etateActuellementEst = M8BonVent.EtateActuellementEst.New_Situation_Credit,
            creationTimestamps = baseTs + 1_000L,
            montant_principale_du_type = newMontant,
        )

        currentList.add(newSituationBon)
        BonVentFlowLogger.newSitCreated(key = newSituationBon.keyID, newMontant = newMontant)

        activeCentralValues.list_M8bon = currentList
        captureRequested = true          // signal the screen to capture + show dialog
        BonVentFlowLogger.listUpdated(list = currentList, captureRequested = true)
    }
}

// ─── Constants ───────────────────────────────────────────────────────────────

const val FAKE_CLIENT_KEY = "fake_client_key_001"
const val FAKE_PERIOD_KEY = "fake_period_key_001"

// ─── Fake data helpers ────────────────────────────────────────────────────────

private fun fakeBon(
    keySuffix: String,
    etat: M8BonVent.EtateActuellementEst,
    creationOffset: Long = 0L,
    versementFait: Double = 0.0,
    creditFait: Double = 0.0,
    demandeVersement: Double = 0.0,
    demandeVersementRegle: Boolean = false,
    montantPrincipale: Double = 0.0,
): M8BonVent = M8BonVent(
    keyID = "fake_key_$keySuffix",
    parent_M2Client_KeyID = FAKE_CLIENT_KEY,
    parent_M14VentPeriod_KeyId = FAKE_PERIOD_KEY,
    etateActuellementEst = etat,
    creationTimestamps = System.currentTimeMillis() - creationOffset,
    versement_fait = versementFait,
    credit_fait = creditFait,
    demande_Versemet_si_Type = demandeVersement,
    demande_Versemet_si_Type_est_regle = demandeVersementRegle,
    montant_principale_du_type = montantPrincipale,
)

val FAKE_ALL_BONS = listOf(
    fakeBon(
        "new_credit_1",
        M8BonVent.EtateActuellementEst.New_Situation_Credit,
        creationOffset = 0,
        montantPrincipale = 1500.0,   //<--
        //TODO(1): pk ca comme ce change  a 1100 quand
    ),
    fakeBon(
        "versement_1",
        M8BonVent.EtateActuellementEst.Versemment,
        creationOffset = 30_000,
        versementFait = 1500.0,
    ),
    fakeBon(
        "demande_1",
        M8BonVent.EtateActuellementEst.Demande_Versemet,
        creationOffset = 40_000,
        demandeVersement = 500.0,
    ),
    fakeBon(
        "credit_1",
        M8BonVent.EtateActuellementEst.Credit,
        creationOffset = 50_000,
        creditFait = 3000.0,
    ),
    fakeBon(
        "COMMANDE_LIVRAI_1",
        M8BonVent.EtateActuellementEst.COMMANDE_LIVRAI,
        creationOffset = 60_000,
        montantPrincipale = 3000.0,
    ),
)
