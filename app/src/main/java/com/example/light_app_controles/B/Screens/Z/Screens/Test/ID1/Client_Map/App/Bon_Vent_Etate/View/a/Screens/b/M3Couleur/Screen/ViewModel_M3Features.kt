package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.a.Screens.b.M3Couleur.Screen

import EntreApps.Shared.Models.M09AppCompt
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos.Companion.filter_passive_datas
import EntreApps.Shared.Models.Relative_Vents.Models.M10OperationVentCouleur
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.a.Screens.a.BonVents.Screen.Filter_Affichage_Mode_Proto
import com.example.light_app_controles.Models.Relative_Vents.Models.M14VentPeriode
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.a.Screens.b.M3Couleur.Screen.ViewModel.preview.FAKE_M9Compt
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.a.Screens.b.M3Couleur.Screen.ViewModel.preview.FAKE_ON_VENT
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.a.Screens.b.M3Couleur.Screen.ViewModel.preview.fake_extra
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.a.Screens.b.M3Couleur.Screen.ViewModel.preview.fake_update_couleurs_count_depo
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.a.Screens.b.M3Couleur.Screen.ViewModel.preview.fake_update_couleurs_echants
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
    var list_M03: List<M3CouleurProduitInfos>? by mutableStateOf(null)
    var list_M10: List<M10OperationVentCouleur>? by mutableStateOf(null)
    var list_M14: List<M14VentPeriode>? by mutableStateOf(null)
    var tiger_filterID2_Filter_Affichage_Mode_Proto: Filter_Affichage_Mode_Proto by
    mutableStateOf(Filter_Affichage_Mode_Proto.Panie)
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
                val dao_list_m3 = appDatabase.dao_M03CouleurProduitInfos().getAll()

                active_Datas.list_M03 = fake_update_couleurs_echants(
                    fake_update_couleurs_count_depo(fake_extra(dao_list_m3))
                ).filter_passive_datas(
                    active_Datas.active_M9Compt!!.limite_couleurs_ou_leur_last_achate_est_moin_que_jour
                )

                val processedList = active_Datas.list_M03!!
                active_Datas.list_M10 = FAKE_ON_VENT(processedList)
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
