package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.a.Screens.b.M3Couleur.Screen.Working_IN.Feature

import EntreApps.Shared.Models.Relative_Vents.Models.M14VentPeriode

private fun fakePeriod(
    keySuffix: String,
    limitActive: Boolean,
    etat: M14VentPeriode.EtateActuellementEst,
    creationOffsetDays: Long = 0L,
) = M14VentPeriode(
    keyID = "fake_period_$keySuffix",
    creationTimestamp = System.currentTimeMillis() - creationOffsetDays * 24 * 60 * 60 * 1_000,
    its_limite_active_couleurs = limitActive,
    etateActuellementEst = etat,
)

val FAKE_PERIODS = listOf(
    fakePeriod(
        keySuffix = "old_sans_limite",
        limitActive = false,
        etat = M14VentPeriode.EtateActuellementEst.CONFIRME,
        creationOffsetDays = 60,
    ),

    // Période active avec limite = true ET réglée → c'est elle qui pilote le filtre
    fakePeriod(
        keySuffix = "avec_limite_confirme",
        limitActive = true,
        etat = M14VentPeriode.EtateActuellementEst.CONFIRME,
        creationOffsetDays = 25,   // démarre il y a 25 jours → seuil du filtre
    ),

    fakePeriod(
        keySuffix = "avec_limite_non_regle",
        limitActive = false,
        etat = M14VentPeriode.EtateActuellementEst.SoquetteNonDefinie,
        creationOffsetDays = 5,
    ),
)
