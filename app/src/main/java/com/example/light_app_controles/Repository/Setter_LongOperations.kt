package com.example.light_app_controles.Repository

import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.M8BonVent
import com.example.light_app_controles.Modules.Base.SQL.Daos.AppDatabase
import kotlinx.coroutines.CoroutineScope


class Setter_LongOperations(
    val appDatabase: AppDatabase,
) {
    suspend fun update_M8(vent: M8BonVent) {
        appDatabase.dao_M8BonVent().upsert(vent)
    }
    suspend fun insertAll(bons: List<M8BonVent>) {
         appDatabase.dao_M8BonVent().insertAll(bons)
    }
}
