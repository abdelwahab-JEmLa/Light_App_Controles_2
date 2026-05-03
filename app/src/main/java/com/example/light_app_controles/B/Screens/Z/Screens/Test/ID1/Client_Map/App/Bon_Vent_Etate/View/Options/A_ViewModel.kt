package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.Options

import EntreApps.Shared.Models.M09AppCompt
import android.annotation.SuppressLint
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.FAKE_CLIENT_KEY
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.M8BonVent
import com.example.light_app_controles.Modules.Base.SQL.Daos.AppDatabase
import kotlinx.coroutines.launch

@Stable
class ActiveDatas {
    var active_M9Compt: M09AppCompt? by mutableStateOf(null)
    var list_M8bon: List<M8BonVent>? by mutableStateOf(null)
}

@SuppressLint("StaticFieldLeak")
class A_ViewModel(
    private val appDatabase: AppDatabase,
) : ViewModel() {
    val active_Datas = ActiveDatas()
    val setter_LongOperations = Setter_LongOperations(
        appDatabase,
    )

    var captureRequested by mutableStateOf(false)

    init {
        viewModelScope.launch {
            active_Datas.list_M8bon = (appDatabase.dao_M8BonVent().getAll())
        }
    }

    override fun onCleared() {
        super.onCleared()
    }

    /** Re-fetch list_M8bon from Room — call after any bulk DB mutation. */
    fun reload() {
        viewModelScope.launch {
            active_Datas.list_M8bon = appDatabase.dao_M8BonVent().getAll()
        }
    }

    fun update_M8(it: M8BonVent) {
        active_Datas.list_M8bon = active_Datas.list_M8bon
            ?.map { bon -> if (bon.keyID == it.keyID) it else bon }

        viewModelScope.launch {
            setter_LongOperations.update_M8(it)
        }
    }

    fun add_New_M8BonVent(bon: M8BonVent) {
        viewModelScope.launch {
            setter_LongOperations.add_New_M8BonVent(bon)
        }
    }
}


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
        montantPrincipale = 1500.0,
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
