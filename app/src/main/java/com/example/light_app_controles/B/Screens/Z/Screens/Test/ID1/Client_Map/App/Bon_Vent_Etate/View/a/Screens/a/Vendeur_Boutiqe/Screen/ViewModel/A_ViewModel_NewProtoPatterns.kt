package Application4.App.Fragment.ID1.Fragment.ViewModel

import Application4.App.Fragment.ID1.Fragment.ViewModel.y.Components.Setter_ViewModel
import Application4.App.Fragment.ID1.Fragment.ViewModel.y.Components.UiState_NewProtoPatterns
import EntreApps.Shared.Models.M09AppCompt
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Produits.Models.M16CategorieProduit
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import EntreApps.Shared.Modules.Base.SQL.Dao13TarificationInfos
import EntreApps.Shared.Modules.Base.SQL.Dao14VentPeriode
import EntreApps.Shared.Modules.Base.SQL.Dao_M03CouleurProduitInfos
import EntreApps.Shared.Modules.Base.SQL.Dao_M10OperationVentCouleur
import EntreApps.Shared.Modules.Base.SQL.Dao_M16CategorieProduit
import EntreApps.Shared.Modules.Base.SQL.Dao_M1Produit
import EntreApps.Shared.Modules.Base.SQL.Dao_M2Client
import EntreApps.Shared.Modules.Base.SQL.Dao_M8BonVent
import EntreApps.Shared.Modules.Base.SQL.Dao_M9AppCompt
import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Modules.Setter_LongDatas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@SuppressLint("StaticFieldLeak")
class A_ViewModel_NewProtoPatterns(
    private val context: Context,
    val setter_LongDatas: Setter_LongDatas,
    val dao_M8BonVent: Dao_M8BonVent,
    val dao_M9AppCompt: Dao_M9AppCompt,
    val dao_M03CouleurProduitInfos: Dao_M03CouleurProduitInfos,
    val dao_M1Produit: Dao_M1Produit,
    val dao_M2Client: Dao_M2Client,
    val dao_16CategorieProduit: Dao_M16CategorieProduit,
    val dao_M14VentPeriode: Dao14VentPeriode,
    val dao_M13TarificationInfos: Dao13TarificationInfos,
    val dao_M10OperationVentCouleur: Dao_M10OperationVentCouleur,
) : ViewModel() {
    val active_Datas = ActiveDatasFragNewProto()

     val setter_Vm = Setter_ViewModel(
        this,
        context,
        setter_LongDatas
    )
     val setter_LongDatasRef = setter_Vm.setter_LongDatas

    val _uiStateNewProtoPatterns = MutableStateFlow(UiState_NewProtoPatterns())
    val uiState = _uiStateNewProtoPatterns.asStateFlow()

    /** Setter direct de l'expansion (produit + couleur) — met à jour le state local ET notifie le client. */
    fun updateExpandedProduitEtCouleur(
        produit: M01Produit?,
        couleur: M3CouleurProduitInfos?,
        sendToClient: Boolean = true,
    ) {
        active_Datas.expanded_M1Produit = produit
        active_Datas.expanded_M3CouleurProduitInfos = couleur
    }

    init {
        Initializer_ViewModel(this@A_ViewModel_NewProtoPatterns).run()
    }

    fun retryLoadingData() {
        Initializer_ViewModel(this@A_ViewModel_NewProtoPatterns).reload()
    }

    fun maybeCreateEditedPourClientTariff(
        produit: M01Produit,
        synthetic: M13TarificationInfos?,
        datasValue_distinct_type: List<M13TarificationInfos>,
    ): M13TarificationInfos? {
        val currentBonVent = active_Datas.activeOnVent_M8BonVent
        val isGrossist = active_Datas.currentApp_ItsWorkChezGrossisst

        if (isGrossist || currentBonVent == null || synthetic == null) return null
        if (datasValue_distinct_type.any {
                it.typeChoisi == M13TarificationInfos.TypeChoisi.Edited_Pour_Client &&
                        it.parent_M8BonVent_KeyId == currentBonVent.keyID &&
                        it.parent_M1Produit_KeyId == produit.keyID
            }) return null

        val currentClient = active_Datas.activeOnVent_M2Client
        val clientBonVents = active_Datas.filteredList_M8BonVent_Par_CurrentActive_M14VentPeriod
            .filter { it.parent_M2Client_KeyID == currentClient?.keyID }
            .sortedByDescending { it.creationTimestamps }

        if (clientBonVents.firstOrNull()?.keyID != currentBonVent.keyID) return null
        if (System.currentTimeMillis() - currentBonVent.creationTimestamps >= 5 * 60 * 1000) return null

        val now = System.currentTimeMillis()
        val newTariff = synthetic.copy(
            typeChoisi = M13TarificationInfos.TypeChoisi.Edited_Pour_Client,
            parent_M8BonVent_KeyId = currentBonVent.keyID,
            parent_M8BonVent_DebugInfos = currentBonVent.get_DebugInfos(),
            parent_M2Client_KeyId = currentClient?.keyID ?: "null",
            parent_M2Client_DebugInfos = currentClient?.nom ?: "null",
            creationTimestamps = now,
            dernierTimeTampsSynchronisationAvecFireBase = now
        )
        update_M13TarificationInfos(newTariff)
        return newTariff
    }

    //────────────Setter_ViewModel────────────────────────────────────────────────
    fun update_m1Produit(new: M01Produit) = setter_Vm.update_m1Produit(new)
    fun delete_m1Produit(produit: M01Produit) = setter_Vm.delete_m1Produit(produit)

    fun update_m2(new: M2Client) = setter_Vm.update_m2(new)

    fun deleteInsertFireBase_listKeys_M3CouleurProduitInfos(
        keys: Map<String, Boolean>,
        onSuccess: () -> Unit = {}
    ) =
        setter_Vm.deleteInsertFireBase_listKeys_M3CouleurProduitInfos(keys, onSuccess)

    fun updateTariffForProductOperations(produitKeyID: String, newTariff: M13TarificationInfos) =
        setter_Vm.setter_LongDatas.updateTariffForProductOperations(
            produitKeyID,
            newTariff
        )

    fun setActiveFocuceTariffPrixDifineur(produit: M01Produit, appCompt: M09AppCompt) =
        setter_Vm.setter_LongDatas.setIN_CurrentApp_activeFocuce_TariffPrixDifineur_M1ProduitKeyID(
            produit,
            appCompt
        )

    fun update_active_Compt(compt: M09AppCompt) = setter_Vm.update_active_Compt(compt)

    fun update_listM10OperationVentCouleur(updatedList: List<M10OperationVentCouleur>?) =
        setter_Vm.update_listM10OperationVentCouleur(updatedList)

    fun addNew_listM10OperationVentCouleur(datas: List<M10OperationVentCouleur>?) =
        setter_Vm.addNew_listM10OperationVentCouleur(datas)

    fun update_m3couleur(couleur: M3CouleurProduitInfos) = setter_Vm.update_m3couleur(couleur)
    fun delete_m3couleur(couleur: M3CouleurProduitInfos) = setter_Vm.delete_m3couleur(couleur)
    fun delete_M10OperationVentCouleur(op: M10OperationVentCouleur) =
        setter_Vm.delete_M10OperationVentCouleur(op)
    fun update_depot_count(
        couleur: M3CouleurProduitInfos,
        newDepotCount: Int,
        onSuccess: () -> Unit = {}
    ) =
        setter_Vm.update_depot_count(couleur, newDepotCount, onSuccess)

    fun update_M13TarificationInfos(tariff: M13TarificationInfos) =
        setter_Vm.update_M13TarificationInfos(tariff)

    fun insert_M16CategorieProduit(new: M16CategorieProduit) =
        setter_Vm.insert_M16CategorieProduit(new)

    fun update_m16CategorieProduit(new: M16CategorieProduit) =
        setter_Vm.update_m16CategorieProduit(new)

    override fun onCleared() {
        super.onCleared()
    }


    fun update_m8(bonVent: M8BonVent) = setter_Vm.update_m8(bonVent)
}
