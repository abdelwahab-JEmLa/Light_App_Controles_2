package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.a.Main.ViewModel

import EntreApps.Shared.Models.M09AppCompt
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import android.annotation.SuppressLint
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Modules.Setter_LongDatas
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import EntreApps.Shared.Modules.Base.AppDatabase
import android.content.Context
import kotlinx.coroutines.launch

@Stable
class ActiveDatas {
    var active_M9Compt: M09AppCompt? by mutableStateOf(null)
    var list_M8bon: List<M8BonVent>? by mutableStateOf(null)
    var list_M03: List<M3CouleurProduitInfos>? by mutableStateOf(null)
    var list_M2Client: List<M2Client>? by mutableStateOf(null)
}

@SuppressLint("StaticFieldLeak")
class FeatureID1_ViewModel(
    private val appDatabase: AppDatabase,
    context: Context,
) : ViewModel() {
    val active_Datas = ActiveDatas()
    val setter_LongDatas = Setter_LongDatas(
        appDatabase,
        context,
    )

    var captureRequested by mutableStateOf(false)

    init {
        viewModelScope.launch {
            active_Datas.list_M8bon = (appDatabase.dao_M8BonVent().getAll())
            active_Datas.list_M03 = (appDatabase.dao_M03CouleurProduitInfos().getAll())
            active_Datas.list_M2Client = (appDatabase.dao_M2Client().getAll())
        }
    }

    override fun onCleared() {
        super.onCleared()
    }

    fun reload() {
        viewModelScope.launch {
            active_Datas.list_M8bon = appDatabase.dao_M8BonVent().getAll()
            active_Datas.list_M03 = appDatabase.dao_M03CouleurProduitInfos().getAll()
            active_Datas.list_M2Client = appDatabase.dao_M2Client().getAll()
        }
    }

    fun update_M8(it: M8BonVent) {
        active_Datas.list_M8bon = active_Datas.list_M8bon
            ?.map { bon -> if (bon.keyID == it.keyID) it else bon }

        viewModelScope.launch {
            setter_LongDatas.update_M8(it)
        }
    }

    fun add_New_M8BonVent(bon: M8BonVent) {
        viewModelScope.launch {
            setter_LongDatas.add_New_M8BonVent(bon)
        }
    }
}

