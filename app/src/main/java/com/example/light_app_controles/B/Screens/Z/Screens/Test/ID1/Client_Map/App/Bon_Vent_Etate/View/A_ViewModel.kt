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
        val currentList = activeCentralValues.list_M8bon?.toMutableList() ?: mutableListOf()

        // 1. Create and add the new Versement bon
        val versementBon = M8BonVent(
            keyID = "fake_key_versement_${System.currentTimeMillis()}",
            parent_M2Client_KeyID = clientKey,
            parent_M14VentPeriod_KeyId = periodKey,
            etateActuellementEst = M8BonVent.EtateActuellementEst.Versemment,
            creationTimestamps = System.currentTimeMillis(),
            versement_fait = montant,
        )
        currentList.add(versementBon)

        // 2. Find the latest New_Situation_Credit for this client+period and reduce its principal
        val latestSitIdx = currentList
            .indexOfLast {
                it.parent_M2Client_KeyID == clientKey &&
                        it.parent_M14VentPeriod_KeyId == periodKey &&
                        it.etateActuellementEst == M8BonVent.EtateActuellementEst.New_Situation_Credit
            }
        if (latestSitIdx != -1) {
            val sit = currentList[latestSitIdx]
            currentList[latestSitIdx] = sit.copy(
                montant_principale_du_type = sit.montant_principale_du_type - montant
            )
        }

        activeCentralValues.list_M8bon = currentList
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
        "commande_old",
        M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT,
        creationOffset = 60_000,
    ),
    fakeBon(
        "commande_new",
        M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT,
        creationOffset = 50_000,
    ),
    fakeBon(
        "versement",
        M8BonVent.EtateActuellementEst.Versemment,
        creationOffset = 40_000,
        versementFait = 1500.0,
    ),
    fakeBon(
        "credit",
        M8BonVent.EtateActuellementEst.Credit,
        creationOffset = 30_000,
        creditFait = 3000.0,
    ),
    fakeBon(
        "demande",
        M8BonVent.EtateActuellementEst.Demande_Versemet,
        creationOffset = 20_000,
        demandeVersement = 500.0,
    ),
    // new_credit is the most recent entry (creationOffset = 0) with montant_principale = 1500
    fakeBon(
        "new_credit",
        M8BonVent.EtateActuellementEst.New_Situation_Credit,
        creationOffset = 0,
        montantPrincipale = 1500.0,
    ),
)
