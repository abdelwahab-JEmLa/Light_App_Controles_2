package Working_IN.Feature.a.Test

import EntreApps.Shared.Models.Relative_Vents.Models.AbdelwahabJomla_Client_Speciale
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.b.Models.M8BonVent
import com.example.light_app_controles.Modules.Base.SQL.Daos.Dao_M8BonVent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun cleanupOldBonVents_Np(
    daoM8BonVent: Dao_M8BonVent,
    bonVents: List<M8BonVent>,
    on_vent_key: String
) {
    val specialClientKeyIDs = AbdelwahabJomla_Client_Speciale.entries
        .map { it.keyID }
        .filter { it.isNotEmpty() }
        .toSet()

    val lastBonVentKeyPerClient: Set<String> = bonVents
        .groupBy { it.parent_M2Client_KeyID }
        .values
        .mapNotNull { clientBons -> clientBons.maxByOrNull { it.creationTimestamps }?.keyID }
        .toSet()

    val bonVentsToRemove = bonVents.filter { bonVent ->
        if (bonVent.etateActuellementEst.nonDeletable) return@filter false
        if (bonVent.keyID == on_vent_key) return@filter false
        if (bonVent.keyID in lastBonVentKeyPerClient) return@filter false
        val isSpecialClient = bonVent.parent_M2Client_KeyID in specialClientKeyIDs ||
                bonVent.parent_M2Client_DebugInfos.contains("abdelwahab", ignoreCase = true)
        !isSpecialClient
    }

    if (bonVentsToRemove.isEmpty()) return

    withContext(Dispatchers.IO) {
        bonVentsToRemove.forEach { bonVent ->
            daoM8BonVent.delete(bonVent)
        }
    }
}
