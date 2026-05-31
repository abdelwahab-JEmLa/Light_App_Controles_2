package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.c.Screens.b.M2Client.Screen

import EntreApps.Shared.Models.M09AppCompt
import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.a.Screens.b.M3Couleur.Screen.ViewModel.preview.FAKE_M9Compt
import android.annotation.SuppressLint
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Modules.Setter_LongOperations
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.b.Models.M8BonVent
import com.example.light_app_controles.Modules.Base.SQL.Daos.AppDatabase
import kotlinx.coroutines.launch

@Stable
class ActiveDatas {
    var isLoading: Boolean by mutableStateOf(false)
    var active_M9Compt: M09AppCompt? by mutableStateOf(null)
    var list_M8bon: List<M8BonVent>? by mutableStateOf(null)
    var list_M02: List<M2Client>? by mutableStateOf(null)
}

@SuppressLint("StaticFieldLeak")
class ViewModel_M3Features(private val appDatabase: AppDatabase) : ViewModel() {
    val active_Datas = ActiveDatas()
    val setter_LongOperations = Setter_LongOperations(appDatabase)

    init { viewModelScope.launch { reload() } }

    fun reload() {
        viewModelScope.launch {
            active_Datas.isLoading = true
            try {
                active_Datas.active_M9Compt = FAKE_M9Compt
                active_Datas.list_M8bon = appDatabase.dao_M8BonVent().getAll()
                var list = appDatabase.dao_M2Client().getAll()
                if (list.isEmpty()) {
                    setter_LongOperations.import_M2ClientCsv_To_Room(M2Client.csv_test)
                    list = appDatabase.dao_M2Client().getAll()
                }
                active_Datas.list_M02 = list
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                active_Datas.isLoading = false
            }
        }
    }

    fun update_M8(it: M8BonVent) {
        active_Datas.list_M8bon =
            active_Datas.list_M8bon?.map { bon -> if (bon.keyID == it.keyID) it else bon }
        viewModelScope.launch { setter_LongOperations.update_M8(it) }
    }

    fun add_New_M8BonVent(bon: M8BonVent) {
        viewModelScope.launch { setter_LongOperations.add_New_M8BonVent(bon) }
    }
}
