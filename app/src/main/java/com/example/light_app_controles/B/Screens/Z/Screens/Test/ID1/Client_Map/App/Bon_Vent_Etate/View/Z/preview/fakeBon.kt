package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.Z.preview

import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.b.Models.M8BonVent

const val FAKE_CLIENT_KEY = "-OWI8JQlhGjA_HzMCGFD"
const val Targted_Bon = "-OrVHbH6u_C6TT153tUR"

val fake_new_sit = M8BonVent(
    parent_M2Client_KeyID = FAKE_CLIENT_KEY,
    montant_principale_du_type = 10890.00,
    creationTimestamps = System.currentTimeMillis() + 1_000L,
    etateActuellementEst = M8BonVent.EtateActuellementEst.New_Situation_Credit
)

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
