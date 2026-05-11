package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.a.Screens.b.M3Couleur.Screen.Working_IN.Feature

import EntreApps.Shared.Models.M09AppCompt
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
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
    var active_M9Compt: M09AppCompt? by mutableStateOf(null)
    var list_M8bon: List<M8BonVent>? by mutableStateOf(null)
    var list_M03: List<M3CouleurProduitInfos>? by mutableStateOf(null)
    var list_M14: List<M14VentPeriode>? by mutableStateOf(null)
}

@SuppressLint("StaticFieldLeak")
class M3Features_ViewModel(
    private val appDatabase: AppDatabase,
) : ViewModel() {
    val active_Datas = ActiveDatas()
    val setter_LongOperations = Setter_LongOperations(
        appDatabase,
    )

    var captureRequested by mutableStateOf(false)

    init {
        viewModelScope.launch {
            reload()
        }
    }

    override fun onCleared() {
        super.onCleared()
    }

    fun reload() {
        viewModelScope.launch {
            active_Datas.list_M8bon = appDatabase.dao_M8BonVent().getAll()

            val now = System.currentTimeMillis()
            val all = appDatabase.dao_M03CouleurProduitInfos().getAll()
            active_Datas.list_M03 =
                all + listOf(
                            M3CouleurProduitInfos(
                                keyID = "fake_m3_within_1",
                                debugInfos = "within_limit_1",
                                nomCouleurStrSiSonImageDispo="within_limit_1",
                                dernier_achant_timeTamp = now - 1 * 24 * 60 * 60 * 1_000,   // 1 jour  ✓
                            ),
                            M3CouleurProduitInfos(
                                keyID = "fake_m3_within_2",
                                debugInfos = "within_limit_2",
                                nomCouleurStrSiSonImageDispo="within_limit_2",
                                dernier_achant_timeTamp = now - 7 * 24 * 60 * 60 * 1_000,   // 7 jours ✓
                            ),
                            M3CouleurProduitInfos(
                                keyID = "fake_m3_within_3",
                                debugInfos = "within_limit_3",
                                nomCouleurStrSiSonImageDispo="within_limit_3",
                                dernier_achant_timeTamp = now - 20 * 24 * 60 * 60 * 1_000,  // 20 jours ✓
                            ),
                            M3CouleurProduitInfos(
                                keyID = "fake_m3_hors_limite",
                                debugInfos = "outside_limit",
                                nomCouleurStrSiSonImageDispo="outside_limit",
                                dernier_achant_timeTamp = now - 45 * 24 * 60 * 60 * 1_000,  // 45 jours ✗
                            ),
                        )

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
