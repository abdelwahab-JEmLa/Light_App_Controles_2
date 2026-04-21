package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View


const val FAKE_CLIENT_KEY = "fake_client_key_001"
const val FAKE_PERIOD_KEY = "fake_period_key_001"

private fun fakeBon(
    keySuffix: String,
    etat: M8BonVent.EtateActuellementEst,
    creationOffset: Long = 0L,
    versementFait: Double = 0.0,
    creditFait: Double = 0.0,
    demandeVersement: Double = 0.0,
    demandeVersementRegle: Boolean = false,
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
)

val FAKE_ALL_BONS = listOf(
    fakeBon(
        "commande_old",
        M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT,
        creationOffset = 60_000
    ),
    fakeBon(
        "commande_new",
        M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT,
        creationOffset = 10_000
    ),
    fakeBon("versement", M8BonVent.EtateActuellementEst.Versemment, versementFait = 1500.0),
    fakeBon("credit", M8BonVent.EtateActuellementEst.Credit, creditFait = 3000.0),
    fakeBon("demande", M8BonVent.EtateActuellementEst.Demande_Versemet, demandeVersement = 500.0),
    fakeBon("new_credit", M8BonVent.EtateActuellementEst.New_Situation_Credit),
)

