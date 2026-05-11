package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View

import EntreApps.Shared.Models.M09AppCompt
import android.annotation.SuppressLint
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FloatingMenu_Plus_RoomCsvBigDatas.Feature.Modules.Setter_LongOperations
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


