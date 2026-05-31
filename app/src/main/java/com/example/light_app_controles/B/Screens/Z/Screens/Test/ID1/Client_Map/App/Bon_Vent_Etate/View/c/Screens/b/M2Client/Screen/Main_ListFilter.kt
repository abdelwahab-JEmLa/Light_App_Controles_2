package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.c.Screens.b.M2Client.Screen

import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun Main_ListFilter(
    relative_listM02: List<M2Client>,
    query_outline_searcher: String,
) {
    fun filterByQuery(q: String, list: List<M2Client>): List<M2Client> {
        val lq = q.trim().lowercase()
        return if (lq.isEmpty()) list
        else list.filter {
            it.nom.lowercase().contains(lq) ||
            it.nomPrenomArabe.lowercase().contains(lq) ||
            it.keyID.lowercase().contains(lq) ||
            it.numTelephone.contains(lq)
        }
    }

    val finale_filtred_list = remember(relative_listM02, query_outline_searcher) {
        filterByQuery(query_outline_searcher, relative_listM02)
    }

    // ── List ─────────────────────────────────────────────────────────
    Main_List(finale_filtred_list)
}

