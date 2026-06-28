package Application5.App

import Application5.App.Repository.Data.DataBaseInitFactory_M20ObsarvationEtudion
import Application5.App.Repository.Data.Repo19Etudiant
import Application5.App.Repository.Data.Repo20ObsarvationEtudion
import Application5.App.Repository.DataBaseInitFactory_SeparatedAppsCodingPattern_19Etudiant
import Application5.App.Repository.DataBaseInit_SeparatedDataBasesCodingPattern_M9AppCompt
import Application5.App.Repository.DataBaseInit_SeparatedDataBasesCodingPattern_Z_AppCompt
import Application5.App.Repository.M19Etudiant
import Application5.App.Repository.M20ObsarvationEtudion
import Application5.App.Repository.Repo9AppCompt_SeparatedAppsCodingPattern
import Application5.App.Repository.W_DatabaseInitializationManager_SeparatedDataBasesCodingPattern
import EntreApps.Shared.Models.Components.Ousstad_Tahfid
import EntreApps.Shared.Models.Compts
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.M09AppCompt
import EntreApps.Shared.Models.Utilisateur
import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.light_app_controles.Modules.Base.SQL.Daos.AppDatabase
import kotlinx.coroutines.launch
import java.util.Calendar

@Stable
class ActiveDatas_SeparatedAppsCodingPattern {
    var active_M9Compt: M09AppCompt? by mutableStateOf(null)
    var outlined_filter_searcher_floating_abouve_all by mutableStateOf("")
    var displaye_dialog_mois_moinAcPlus_6_du_current by mutableStateOf(false)
    var filter_les_absents by mutableStateOf(false)
    var affiche_last_histoque_seulement by mutableStateOf(false)
    var displaye_sections_education_du_mois: Calendar? by mutableStateOf(null)
    var active_Ousstad_Tahfid: Ousstad_Tahfid? by mutableStateOf(
        run {
            val params = M00CentralParametresOfAllApps()
            val utilisateur = when (params.au_Lence_Set_Compt_Ac_KeyId) {
                params.abdelmomen_Compt_KeyId -> Utilisateur.Abdelmoumen
                params.walid_Compt_KeyId -> Utilisateur.Walid
                Compts.AbdelwahabTravailleChezGros_KeyId.keyId -> Utilisateur.Abdelwahab_Osstad
                params.amine_madrasa_Compt_KeyId -> Utilisateur.Amine_Madrassa
                params.kissme_talaba_li_dirassatihim_mena_idata_Compt_KeyId -> Utilisateur.kissme_talaba_li_dirassatihim_mena_idata
                else -> Utilisateur.Admin
            }
            when (utilisateur) {
                Utilisateur.Abdelwahab_Osstad -> Ousstad_Tahfid.Abdelwahab_Osstad
                Utilisateur.Amine_Madrassa -> Ousstad_Tahfid.Amine_Madrassa
                Utilisateur.kissme_talaba_li_dirassatihim_mena_idata -> Ousstad_Tahfid.kissme_talaba_li_dirassatihim_mena_idata
                else -> null
            }
        }
    )
}

@SuppressLint("StaticFieldLeak")
class A_ViewModel_SeparatedAppsCodingPattern(
    private val context: Context,
    val appDatabase: AppDatabase,
) : ViewModel() {
    val dataBaseInitFactory_19Etudiant =
        DataBaseInitFactory_SeparatedAppsCodingPattern_19Etudiant(appDatabase)

    val repo19Etudiant = Repo19Etudiant(
        context = context,
        dataBaseCreationFactory = dataBaseInitFactory_19Etudiant,
    )

    val dataBaseInitFactory_M20ObsarvationEtudion =
        DataBaseInitFactory_M20ObsarvationEtudion(appDatabase)

    val repo20ObsarvationEtudion = Repo20ObsarvationEtudion(
        context = context,
        dataBaseCreationFactory = dataBaseInitFactory_M20ObsarvationEtudion,
    )

    val dataBaseInit_SeparatedDataBasesCodingPattern_M9AppCompt =
        DataBaseInit_SeparatedDataBasesCodingPattern_M9AppCompt(appDatabase.dao_M9AppCompt())

    val repo9AppCompt = Repo9AppCompt_SeparatedAppsCodingPattern(
        context = context,
        dataBaseInit_SeparatedDataBasesCodingPattern_M9AppCompt
    )

    val databaseInitializationManager =
        W_DatabaseInitializationManager_SeparatedDataBasesCodingPattern(
            appComptComposeRepositoryPJ17 = repo9AppCompt,
            dataBaseInitZ_AppCompt = DataBaseInit_SeparatedDataBasesCodingPattern_Z_AppCompt(appDatabase),
            dataBaseInitFactory_19Etudiant = dataBaseInitFactory_19Etudiant,
            dataBaseInitFactory_M20ObsarvationEtudion=dataBaseInitFactory_M20ObsarvationEtudion
        )

    val activeCentralValues = ActiveDatas_SeparatedAppsCodingPattern()

    init {
        viewModelScope.launch { databaseInitializationManager.initializeAllRepositories(context) }
    }

    override fun onCleared() {
        super.onCleared()
        databaseInitializationManager.cancel()
    }

    fun add_M19Etudiant(newStudent: M19Etudiant) {
        viewModelScope.launch { repo19Etudiant.upsert(newStudent) }
    }

    fun upsert_M20ObsarvationEtudion(absenceObservation: M20ObsarvationEtudion) {
        viewModelScope.launch { repo20ObsarvationEtudion.upsert(absenceObservation) }
    }

    fun update_activeCentralValues(updated: ActiveDatas_SeparatedAppsCodingPattern) {
        activeCentralValues.active_M9Compt = updated.active_M9Compt
        activeCentralValues.outlined_filter_searcher_floating_abouve_all = updated.outlined_filter_searcher_floating_abouve_all
        activeCentralValues.displaye_dialog_mois_moinAcPlus_6_du_current = updated.displaye_dialog_mois_moinAcPlus_6_du_current
        activeCentralValues.filter_les_absents = updated.filter_les_absents
        activeCentralValues.displaye_sections_education_du_mois = updated.displaye_sections_education_du_mois
        activeCentralValues.active_Ousstad_Tahfid = updated.active_Ousstad_Tahfid
    }

    fun update_activeDatas(
        active_M9Compt: M09AppCompt? = activeCentralValues.active_M9Compt,
        outlined_filter_searcher_floating_abouve_all: String = activeCentralValues.outlined_filter_searcher_floating_abouve_all,
        displaye_dialog_mois_moinAcPlus_6_du_current: Boolean = activeCentralValues.displaye_dialog_mois_moinAcPlus_6_du_current,
        filter_les_absents: Boolean = activeCentralValues.filter_les_absents,
        displaye_sections_education_du_mois: Calendar? = activeCentralValues.displaye_sections_education_du_mois,
        active_Ousstad_Tahfid: Ousstad_Tahfid? = activeCentralValues.active_Ousstad_Tahfid,
    ) {
        activeCentralValues.active_M9Compt = active_M9Compt
        activeCentralValues.outlined_filter_searcher_floating_abouve_all = outlined_filter_searcher_floating_abouve_all
        activeCentralValues.displaye_dialog_mois_moinAcPlus_6_du_current = displaye_dialog_mois_moinAcPlus_6_du_current
        activeCentralValues.filter_les_absents = filter_les_absents
        activeCentralValues.displaye_sections_education_du_mois = displaye_sections_education_du_mois
        activeCentralValues.active_Ousstad_Tahfid = active_Ousstad_Tahfid
    }
}
