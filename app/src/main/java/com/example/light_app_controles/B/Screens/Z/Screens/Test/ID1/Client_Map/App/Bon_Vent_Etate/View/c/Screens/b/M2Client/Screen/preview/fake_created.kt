package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.c.Screens.b.M2Client.Screen.preview

import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos

fun fake_created(): List<M3CouleurProduitInfos>
    {
        val now = System.currentTimeMillis()

        return listOf(
            M3CouleurProduitInfos(
                keyID = "fake_m3_within_1",
                debugInfos = "within_limit_1",
                nomCouleurStrSiSonImageDispo = "within_limit_1",
                dernier_achant_timeTamp = now - 1 * 24 * 60 * 60 * 1_000,   // 1 jour  ✓
            ),
            M3CouleurProduitInfos(
                keyID = "fake_m3_hors_limite",
                debugInfos = "outside_limit",
                nomCouleurStrSiSonImageDispo = "outside_limit",
                dernier_achant_timeTamp = now - 45 * 24 * 60 * 60 * 1_000,  // 45 jours ✗
            ),
        )
    }
