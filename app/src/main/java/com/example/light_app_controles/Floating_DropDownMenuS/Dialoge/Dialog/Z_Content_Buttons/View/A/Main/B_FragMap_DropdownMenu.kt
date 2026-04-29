package A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog

import A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog.C.Components.AvertissementDialog
import A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog.C.Components.Local_Organizer
import A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog.Z_Content_Buttons.View.ButID_4_upload_datas_fireBase_au_csv
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Produits.Models.M16CategorieProduit
import EntreApps.Shared.Models.Relative_Produits.Models.M21CataloguesCategorie
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.Relative_Produits.Models.get_ListM21CataloguesCategorie
import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.light_app_controles.Floating_DropDownMenuS.Dialoge.Dialog.Z_Content_Buttons.View.A.Main.Z.Buttons.View.ButID_3_ImportFromCSV
import com.example.light_app_controles.Floating_DropDownMenuS.Dialoge.Dialog.Z_Content_Buttons.View.A.Main.Z.Buttons.View.ButID2_ImportFromCSV_DropDownItemWBaseDonne
import com.example.light_app_controles.Floating_DropDownMenuS.Dialoge.Dialog.Z_Content_Buttons.View.A.Main.Z.Buttons.View.ButID_1_ExportToCSV_DropDownItemWBaseDonne
import com.example.light_app_controles.Modules.Base.SQL.Daos.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private enum class PendingAction {
    Local,
    UpdateLocalTimestamps,
}

@Composable
fun B_FragMap_DropdownMenu(
    appDatabase: AppDatabase,
    expanded: Boolean,
    onDismiss: () -> Unit,
    list_m16: List<M16CategorieProduit>?,
    list_m1: List<M01Produit>?,
    list_m3: List<M3CouleurProduitInfos>?,
    on_vent_key: String = "",
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var organizeDropBoxProgress by remember { mutableStateOf<Float?>(null) }
    var organizeLocalProgress by remember { mutableStateOf<Float?>(null) }
    var syncImages2Progress by remember { mutableStateOf<Float?>(null) }
    var syncImages2Label by remember { mutableStateOf("") }
    var updateTimestampsProgress by remember { mutableStateOf<Float?>(null) }
    var pendingAction by remember { mutableStateOf<PendingAction?>(null) }


    pendingAction?.let { action ->
        when (action) {


            PendingAction.Local -> AvertissementDialog(
                title = "Organiser en local",
                message = "Cette action va déplacer toutes les images depuis le dossier " +
                        "central local vers leurs dossiers catalogues dans le dossier " +
                        "de sauvegarde. Les fichiers sources seront supprimés. Continuer ?",
                confirmLabel = "Déplacer",
                onConfirm = {
                    pendingAction = null
                    coroutineScope.launch {
                        organizeLocalProgress = 0f
                        val groups = buildCatalogueGroups(list_m16, list_m1, list_m3)
                        Local_Organizer.organizeByCategories(
                            catalogueGroups = groups,
                            onProgress = { p -> organizeLocalProgress = p }
                        )
                        organizeLocalProgress = null
                        onDismiss()
                    }
                },
                onDismiss = { pendingAction = null }
            )


            PendingAction.UpdateLocalTimestamps -> AvertissementDialog(
                title = "Mettre à jour dates locales",
                message = "La date de modification de chaque fichier image local sera " +
                        "remplacée par l'heure actuelle. Cela forcera un re-téléchargement " +
                        "lors de la prochaine synchronisation. Continuer ?",
                confirmLabel = "Mettre à jour",
                onConfirm = {
                    pendingAction = null
                    coroutineScope.launch {
                        updateTimestampsProgress = 0f
                        Local_Organizer.updateLocalTimestampsToNow(
                            list_m3 = list_m3,
                            onProgress = { p -> updateTimestampsProgress = p }
                        )
                        updateTimestampsProgress = null
                        onDismiss()
                    }
                },
                onDismiss = { pendingAction = null }
            )
        }
    }

    val anyRunning = organizeDropBoxProgress != null
            || organizeLocalProgress != null
            || syncImages2Progress != null
            || updateTimestampsProgress != null

    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = modifier.background(Color.White, RoundedCornerShape(8.dp))
    ) {


        ButID_1_ExportToCSV_DropDownItemWBaseDonne(
            appDatabase = appDatabase,
            enabled = true,
        )
        ButID2_ImportFromCSV_DropDownItemWBaseDonne(
            appDatabase = appDatabase,
            enabled = true,
        )
        HorizontalDivider()
        ButID_3_ImportFromCSV(
            appDatabase = appDatabase,
            enabled = true,
        )

        ButID_4_upload_datas_fireBase_au_csv(
            enabled = true,
        )
    }
}

private suspend fun buildCatalogueGroups(
    list_m16: List<M16CategorieProduit>?,
    list_m1: List<M01Produit>?,
    list_m3: List<M3CouleurProduitInfos>?,
): Map<M21CataloguesCategorie, List<M3CouleurProduitInfos>>? =
    withContext(Dispatchers.Default) {
        val catalogues = get_ListM21CataloguesCategorie()
        val sansCatalogue = catalogues.find { it.nom == "Sans Catalogue" }
            ?: M21CataloguesCategorie(keyID = "t4", id = 4, nom = "Sans Catalogue")

        val catalogueById = catalogues.associateBy { it.id }
        val catalogueByCategorieId = list_m16?.associate { cat ->
            cat.id to (catalogueById[cat.catalogueParentId] ?: sansCatalogue)
        }
        val catalogueByProduitKey = list_m1?.associate { p ->
            p.keyID to (catalogueByCategorieId?.get(p.idParentCategorie) ?: sansCatalogue)
        }

        list_m3
            ?.filter { it.nomImageFichieSansEtansion.isNotBlank() && it.nomImageFichieSansEtansion != "Non Dispo" }
            ?.groupBy { catalogueByProduitKey?.get(it.parentBProduitInfosKeyID) ?: sansCatalogue }
    }
