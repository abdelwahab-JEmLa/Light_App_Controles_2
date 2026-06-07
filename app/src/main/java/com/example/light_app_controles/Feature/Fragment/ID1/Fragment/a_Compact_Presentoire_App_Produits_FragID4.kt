package Application4.App.Fragment.ID1.Fragment

import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Produits.Models.M16CategorieProduit
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import EntreApps.Shared.Modules.Base.AppDatabase
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Modules.Setter_LongDatas
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.a.Main.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button
import kotlinx.coroutines.delay

@Composable
fun A_Compact_Presentoire_App_Produits_App4(
    modifier: Modifier = Modifier,
    appDatabase: AppDatabase,
    on_update_M13TarificationInfos_par_ecriture: (M13TarificationInfos) -> Unit ={},
) {
    val context = LocalContext.current
    val viewModelNewProtoPatterns: A_ViewModel_NewProtoPatterns =
        viewModel(
            factory = viewModelFactory {
                initializer {
                    val setterLongDatas = Setter_LongDatas(appDatabase, context)
                    A_ViewModel_NewProtoPatterns(
                        context = context,
                        setter_LongDatas = setterLongDatas,
                        dao_M8BonVent = appDatabase.dao_M8BonVent(),
                        dao_M9AppCompt = appDatabase.dao_M9AppCompt(),
                        dao_M03CouleurProduitInfos = appDatabase.dao_M03CouleurProduitInfos(),
                        dao_M1Produit = appDatabase.dao_M1Produit(),
                        dao_M2Client = appDatabase.dao_M2Client(),
                        dao_16CategorieProduit = appDatabase.dao_16CategorieProduit(),
                        dao_M14VentPeriode = appDatabase.dao_M14VentPeriode(),
                        dao_M13TarificationInfos = appDatabase.dao_M13TarificationInfos(),
                        dao_M10OperationVentCouleur = appDatabase.dao_M10OperationVentCouleur()
                    )
                }
            }
        )

    LaunchedEffect(Unit) {
        viewModelNewProtoPatterns.retryLoadingData()
    }

    var showFabDropdown_Compact_Presentoire_App_Produits_FragID4 by remember { mutableStateOf(false) }
    var affiche_pub_abdelwahab_electro_gro_store by remember { mutableStateOf(false) }

    val active_Datas = viewModelNewProtoPatterns.active_Datas

    val uiState by viewModelNewProtoPatterns.uiState.collectAsState()
    val isInitDone = uiState.initDatasProgressEtate >= 1f

    val allCategories: List<M16CategorieProduit>? by remember {
        derivedStateOf {
            active_Datas.list_M16CategorieProduit
                ?.takeIf { it.isNotEmpty() }
        }
    }

    val allProducts: List<M01Produit>? by remember {
        derivedStateOf { active_Datas.list_M1Produit }
    }

    var selectedProductForCategoryChange by remember { mutableStateOf<M01Produit?>(null) }
    var justMovedProductKeyID by remember { mutableStateOf<String?>(null) }
    var hasRetriedLoading by remember { mutableStateOf(false) }

    // Retry loading data if initialization is done but no items are displayed
    LaunchedEffect(isInitDone, allProducts) {
        if (isInitDone && !hasRetriedLoading && allProducts.isNullOrEmpty()) {
            delay(6000)
            if (allProducts.isNullOrEmpty()) {
                hasRetriedLoading = true
                viewModelNewProtoPatterns.retryLoadingData()
            }
        }
    }

    LaunchedEffect(justMovedProductKeyID) {
        justMovedProductKeyID?.let {
            delay(1500)
            justMovedProductKeyID = null
        }
    }

    if (!isInitDone) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = { uiState.initDatasProgressEtate },
                modifier = Modifier.size(48.dp),
                trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
                color = MaterialTheme.colorScheme.primary
            )
        }
    } else {
        Box(
            modifier = Modifier.semantics(mergeDescendants = true) {
                set(value = active_Datas.list_M03CouleurProduitInfos?.find {
                    it.keyID == "-OWDMGKsnReAaqOHO5iH"
                }, key = SemanticsPropertyKey(""))

                set(
                    value = active_Datas.list_M03CouleurProduitInfos?.size,
                    key = SemanticsPropertyKey("size")
                )

                set(
                    value = active_Datas.active_M9Compt?.onVentM8BonVentDebugInfos,
                    key = SemanticsPropertyKey("onVentM8BonVentDebugInfos")
                )
                set(
                    value = active_Datas.active_M9Compt?.onVentM8BonVentKey,
                    key = SemanticsPropertyKey("onVentM8BonVentKey")
                )
            }
        ) {
            if (affiche_pub_abdelwahab_electro_gro_store) {
            } else {
                Main_LazyColumnList_App4(
                    modifier = modifier,
                    uiState_NewProtoPatterns_viewModel = Pair(uiState, viewModelNewProtoPatterns),
                    onProductCategoryClick = { product ->
                        selectedProductForCategoryChange = product
                    },
                    justMovedProductKeyID = justMovedProductKeyID,
                    on_update_M13TarificationInfos_par_ecriture = on_update_M13TarificationInfos_par_ecriture,
                    ventCouleurs = active_Datas.listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state,
                )
            }

        }
    }
}
