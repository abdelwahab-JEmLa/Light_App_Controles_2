package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.a.Screens.b.M3Couleur.Screen.Working_IN.Feature


private fun fakePeriod(
    keySuffix: String,
    etat: M14VentPeriode.EtateActuellementEst,
    creationOffsetDays: Long = 0L,
) = M14VentPeriode(
    keyID = "fake_period_$keySuffix",
    creationTimestamp = System.currentTimeMillis() - creationOffsetDays * 24 * 60 * 60 * 1_000,
    etateActuellementEst = etat,
)

val FAKE_PERIODS = listOf(
    fakePeriod(
        keySuffix = "old_sans_limite",
        etat = M14VentPeriode.EtateActuellementEst.CONFIRME,
        creationOffsetDays = 60,
    ),
    fakePeriod(
        keySuffix = "avec_limite_confirme",
        etat = M14VentPeriode.EtateActuellementEst.CONFIRME,
        creationOffsetDays = 25,
    ),
    fakePeriod(
        keySuffix = "avec_limite_non_regle",
        etat = M14VentPeriode.EtateActuellementEst.SoquetteNonDefinie,
        creationOffsetDays = 5,
    ),
)
