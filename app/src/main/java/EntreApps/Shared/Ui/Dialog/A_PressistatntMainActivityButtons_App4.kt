package EntreApps.Shared.Ui.Dialog

import EntreApps.Shared.Ui.Dialog.ButtonID7.Action.But7_Cree_Images_Bons
import EntreApps.Shared.Ui.Dialog.ButtonID7.Action.Datas
import EntreApps.Shared.Ui.Dialog.ButtonID8.Action.Button_8_Imgs_Send_whatsappBuisness_Stored_Bon_App4
import Application4.App.Fragment.ID1.Fragment.ViewModel.A_ViewModel_NewProtoPatterns
import Application4.App.Fragment.ID1.Fragment.ViewModel.Filter_Affichage_Mode_Proto
import EntreApps.Shared.Models.Relative_Vents.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.Relative_Vents.Models.M14VentPeriode.Companion.sum_vent_et_benifice
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent.Companion.benifice
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent.Companion.sum_totale_vents
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun PressistatntMainActivityButtons_App4(
    viewModelNewProtoPatterns: A_ViewModel_NewProtoPatterns
) {
    val activeDatas = viewModelNewProtoPatterns.active_Datas
    var sharedPdfPath by remember { mutableStateOf("") }
    var sharedPdfCount by remember { mutableStateOf(0) }
    val currentMode by remember {
        derivedStateOf { activeDatas.filterAffichageMode_Proto }
    }

    fun setMode(mode: Filter_Affichage_Mode_Proto, resetSearch: Boolean = true) {
        if (resetSearch) activeDatas.filter_echatilaten = ""
        activeDatas.filterAffichageMode_Proto = mode
        activeDatas.filter_relode_tiger += 1
        viewModelNewProtoPatterns.retryLoadingData()
    }

    var showDropdown by remember { mutableStateOf(false) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    val fabColor = when (currentMode) {
        Filter_Affichage_Mode_Proto.Echants_Seulement -> MaterialTheme.colorScheme.primary
        Filter_Affichage_Mode_Proto.Panie,
        Filter_Affichage_Mode_Proto.Panie_Si_Couleur_Ac_Vent_Affiche_Tout_Ces_Freres -> MaterialTheme.colorScheme.tertiary

        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val fabIcon: ImageVector = when (currentMode) {
        Filter_Affichage_Mode_Proto.Echants_Seulement -> Icons.Default.Check
        Filter_Affichage_Mode_Proto.Panie,
        Filter_Affichage_Mode_Proto.Panie_Si_Couleur_Ac_Vent_Affiche_Tout_Ces_Freres -> Icons.Default.ShoppingCart

        else -> Icons.Default.FilterList
    }

    val fabTint = when (currentMode) {
        Filter_Affichage_Mode_Proto.Tablette_Produits_Seulement,
        Filter_Affichage_Mode_Proto.Tablette_Et_Echants -> MaterialTheme.colorScheme.onSurfaceVariant

        else -> Color.White
    }

    val labelText = when (currentMode) {
        Filter_Affichage_Mode_Proto.Tablette_Produits_Seulement -> "Tablette seulement"
        Filter_Affichage_Mode_Proto.Echants_Seulement -> "Échantillons"
        Filter_Affichage_Mode_Proto.Tablette_Et_Echants -> "Tous les produits"
        Filter_Affichage_Mode_Proto.Panie -> "Panier"
        Filter_Affichage_Mode_Proto.Panie_Si_Couleur_Ac_Vent_Affiche_Tout_Ces_Freres -> "Panier + frères"
    }

    val uiState by viewModelNewProtoPatterns.uiState.collectAsState()
    val listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state =
        activeDatas.listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state
    val tariffs = uiState.list_M13TarificationInfos

    val current_OnVent_M14VentPeriode_KeyID =
        activeDatas.active_M9Compt?.current_OnVent_M14VentPeriode_KeyID

    val activeOnVent_M8BonVent_benefice by remember(
        current_OnVent_M14VentPeriode_KeyID,
        activeDatas.list_M8BonVent,
        activeDatas.list_M10OperationVentCouleur,
        uiState.list_Datas?.m13TarificationInfos,
    ) {
        derivedStateOf {
            listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state?.let { ops ->
                activeDatas.activeOnVent_M8BonVent?.benifice(ops, tariffs)
            }
        }
    }

    val activeOnVent_M8BonVent_sum_totale_vents by remember {
        derivedStateOf {
            listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state?.let { ops ->
                activeDatas.activeOnVent_M8BonVent?.sum_totale_vents(ops, tariffs)
            }
        }
    }

    val sumRasseMaleProduitsAuDepot by remember(
        activeDatas.list_M03CouleurProduitInfos,
        uiState.list_M13TarificationInfos
    ) {
        derivedStateOf {
            val colors = activeDatas.list_M03CouleurProduitInfos ?: emptyList()
            val tariffsList = uiState.list_M13TarificationInfos ?: emptyList()
            colors.filter { it.count_Don_Depot > 0 }.sumOf { color ->
                val productKey = color.parentBProduitInfosKeyID
                val productTariffs = tariffsList.filter { it.parent_M1Produit_KeyId == productKey }
                val superGrosTariff = productTariffs.find { it.typeChoisi == M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_SuperGros }
                val groTariff = productTariffs.find { it.typeChoisi == M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_Gro }

                val price = if (superGrosTariff != null && superGrosTariff.prixCurrency > 0.0) {
                    superGrosTariff.prixCurrency
                } else {
                    groTariff?.prixCurrency ?: 0.0
                }
                color.count_Don_Depot * price
            }
        }
    }

    val m14VentPeriode_sums by remember(
        current_OnVent_M14VentPeriode_KeyID,
        activeDatas.list_M8BonVent,
        activeDatas.list_M10OperationVentCouleur,
    ) {
        derivedStateOf {
            val periodKey = current_OnVent_M14VentPeriode_KeyID
                ?.takeIf { it.isNotBlank() && it != "null" } ?: return@derivedStateOf null
            val period = uiState.list_Datas?.m14VentPeriode
                ?.find { it.keyID == periodKey } ?: return@derivedStateOf null
            val allBons = activeDatas.list_M8BonVent ?: emptyList()
            val allOperations = activeDatas.list_M10OperationVentCouleur ?: emptyList()
            val allTariffs = uiState.list_Datas?.m13TarificationInfos ?: emptyList()

            period.sum_vent_et_benifice(
                bonsList = allBons,
                ventsList = allOperations,
                tariffsList = allTariffs,
            )
        }
    }

    Box(
        modifier = Modifier
            .semantics(mergeDescendants = true) {
                set(
                    value = current_OnVent_M14VentPeriode_KeyID,
                    key = SemanticsPropertyKey("current_OnVent_M14VentPeriode_KeyID")
                )
            }
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    offsetX += dragAmount.x
                    offsetY += dragAmount.y
                }
            },
    ) {

           val listM13tarificationinfos = uiState.list_M13TarificationInfos

           val on_vent_couleurs =
               activeDatas.listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state
           val on_vent_bon = activeDatas.activeOnVent_M8BonVent
        val datas = Datas(
            activeDatas.list_M1Produit,
            activeDatas.list_M03CouleurProduitInfos,
            listM13tarificationinfos,
            on_vent_couleurs,
            on_vent_bon,
            activeDatas.activeOnVent_M2Client
        )
        Column {
            But7_Cree_Images_Bons(
                onPdfSaved = { path, count ->
                    sharedPdfPath = path
                    sharedPdfCount = count
                },
                relative_list_tariff = listM13tarificationinfos,
                on_vent_bon = on_vent_bon,
                on_vent_couleurs = on_vent_couleurs,
                on_update_m8_bon = {
                    viewModelNewProtoPatterns.update_m8(it)
                },
                datas = datas
            )

            Button_8_Imgs_Send_whatsappBuisness_Stored_Bon_App4(
                list_M13TarificationInfos = listM13tarificationinfos,
                on_upsert_M2Client ={
                    viewModelNewProtoPatterns.update_m2(
                        it
                    )
                },
                client = datas.on_vent_m2client,
                bon = datas.on_vent_bon,
                vents = datas.on_vent_couleurs,
                produits = datas.relative_produits
            )
            if (sumRasseMaleProduitsAuDepot > 0.0) {
                var showConfirmDialog by remember { mutableStateOf(false) }

                Box(
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .background(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "dépôt: %.0f DA".format(sumRasseMaleProduitsAuDepot),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        val client = activeDatas.activeOnVent_M2Client
                        val clientBons = activeDatas.list_M8BonVent?.filter { it.parent_M2Client_KeyID == client?.keyID } ?: emptyList()
                        val remainingCredit = client?.getLastSituationCredit(clientBons)?.new_situation ?: 0.0
                        Text(
                            text = " | crédit: %.0f DA".format(remainingCredit),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        IconButton(
                            onClick = { showConfirmDialog = true },
                            modifier = Modifier.size(18.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add depot sales",
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                if (showConfirmDialog) {
                    AlertDialog(
                        onDismissRequest = { showConfirmDialog = false },
                        title = { Text("Confirmer la vente", style = MaterialTheme.typography.titleMedium) },
                        text = { Text("Voulez-vous ajouter tous les produits au dépôt dans le panier de vente actuel ?", style = MaterialTheme.typography.bodyMedium) },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    val colors = activeDatas.list_M03CouleurProduitInfos ?: emptyList()
                                    val products = activeDatas.list_M1Produit ?: emptyList()
                                    val tariffsList = uiState.list_M13TarificationInfos ?: emptyList()
                                    val activeOnVent_M8BonVent = activeDatas.activeOnVent_M8BonVent
                                    val isGrossist = activeDatas.currentApp_ItsWorkChezGrossisst
                                    val currentList = activeDatas.listM10OperationVentCouleur_FilteredBy_activeM8BonVent_state ?: emptyList()

                                    if (activeOnVent_M8BonVent != null) {
                                        val newOps = mutableListOf<M10OperationVentCouleur>()

                                        colors.filter { it.count_Don_Depot > 0 }.forEach { color ->
                                            val product = products.find { it.keyID == color.parentBProduitInfosKeyID } ?: return@forEach
                                            val productTariffs = tariffsList.filter { it.parent_M1Produit_KeyId == product.keyID }
                                            val superGrosTariff = productTariffs.find { it.typeChoisi == M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_SuperGros }
                                            val groTariff = productTariffs.find { it.typeChoisi == M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_Gro }

                                            val selectedTariff = if (superGrosTariff != null && superGrosTariff.prixCurrency > 0.0) {
                                                superGrosTariff
                                            } else {
                                                groTariff
                                            } ?: return@forEach

                                            val parentM13TarificationKeyID =
                                                if (selectedTariff.typeChoisi == M13TarificationInfos.TypeChoisi.Edited_Pour_Client) "Prix_Progressive_Editable Non Saved"
                                                else selectedTariff.keyID

                                            val newOperation = M10OperationVentCouleur.get_Default().copy(
                                                creationTimestamps = System.currentTimeMillis(),
                                                setIN_Vent_Its_Quantity_Represent = product.setIN_Vent_Its_Quantity_Represent,
                                                quantite_Boit_Par_Carton = product.quantite_Boit_Par_Carton,
                                                quantity = color.count_Don_Depot,
                                                prix_de_Vent_entre_directement_NewProto = selectedTariff.prixCurrency,
                                                parentM13TarificationKeyID = parentM13TarificationKeyID,
                                                parentM13TarificationDebugInfos = selectedTariff.getDebugInfos(),
                                                parent_M1Produit_KeyId = product.keyID,
                                                parent_M1Produit_DebugInfos = "par.produit ${product.nom}",
                                                parent_M3CouleurProduit_KeyID = color.keyID,
                                                parent_M3CouleurProduit_DebugInfos = color.get_DebugsInfos(),
                                                parent_M8BonVent_KeyId = activeOnVent_M8BonVent.keyID,
                                                parent_M8BonVent_DebugInfos = activeOnVent_M8BonVent.get_DebugInfos(),
                                                parent_M2Client_KeyID = activeOnVent_M8BonVent.parent_M2Client_KeyID,
                                                typeTarificationEnumT2 = selectedTariff.typeChoisi,
                                                its_created_in_working_for_wholesaler = isGrossist
                                            )
                                            newOps.add(newOperation)
                                        }

                                        if (newOps.isNotEmpty()) {
                                            val newList = currentList + newOps
                                            viewModelNewProtoPatterns.addNew_listM10OperationVentCouleur(newList)
                                        }
                                    }
                                    showConfirmDialog = false
                                }
                            ) {
                                Text("Confirmer")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showConfirmDialog = false }) {
                                Text("Annuler")
                            }
                        }
                    )
                }
            }
            Text("Period")
            Row {
                m14VentPeriode_sums?.let { periodSums ->
                    if (periodSums.totale_vents > 0.0) {
                        FloatingActionButton(
                            onClick = { },
                            modifier = Modifier
                                .widthIn(min = 56.dp)
                                .height(40.dp),
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Text(
                                text = "pér: %.0f DA  (${periodSums.on_command_bons} bons)".format(
                                    periodSums.totale_vents
                                ),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(horizontal = 10.dp)
                            )
                        }
                    }
                    if (periodSums.totale_benifices > 0.0) {
                        FloatingActionButton(
                            onClick = { },
                            modifier = Modifier
                                .widthIn(min = 56.dp)
                                .height(40.dp),
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Text(
                                text = "bén. pér: %.0f DA".format(periodSums.totale_benifices),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(horizontal = 10.dp)
                            )
                        }
                    }
                    if (periodSums.totale_cash > 0.0) {
                        FloatingActionButton(
                            onClick = { },

                            modifier = Modifier
                                .widthIn(min = 56.dp)
                                .height(40.dp),
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Text(
                                text = "cash: %.0f DA".format(periodSums.totale_cash),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(horizontal = 10.dp)
                            )
                        }
                    }
                }
            }

            HorizontalDivider()
            Text("Bon Vent")
            Row {
                activeOnVent_M8BonVent_benefice?.let { benef ->
                    if (benef > 0.0) {
                        FloatingActionButton(
                            onClick = { },
                            modifier = Modifier
                                .semantics(mergeDescendants = true) {

                                }
                                .widthIn(min = 56.dp)
                                .height(40.dp),
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = Color.White,
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Text(
                                text = "bén: %.0f DA".format(benef),
                                color = Color.White,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(horizontal = 10.dp)
                            )
                        }
                    }
                }

                activeOnVent_M8BonVent_sum_totale_vents?.let { it ->
                    if (it > 0.0) {
                        FloatingActionButton(
                            onClick = { },
                            modifier = Modifier
                                .widthIn(min = 56.dp)
                                .height(40.dp),
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Text(
                                text = "tot: %.0f DA".format(it),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(horizontal = 10.dp)
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box {
                        FloatingActionButton(
                            modifier = Modifier
                                .padding(16.dp)
                                .size(56.dp),
                            onClick = { showDropdown = true },
                            containerColor = fabColor,
                        ) {
                            Icon(imageVector = fabIcon, contentDescription = null, tint = fabTint)
                        }
                        DropdownMenu(
                            expanded = showDropdown,
                            onDismissRequest = { showDropdown = false }
                        ) {
                            ModeMenuItem(
                                label = "Tous les produits",
                                icon = Icons.Default.FilterList,
                                isSelected = currentMode == Filter_Affichage_Mode_Proto.Tablette_Et_Echants,
                                onClick = {
                                    setMode(Filter_Affichage_Mode_Proto.Tablette_Et_Echants)
                                    showDropdown = false
                                }
                            )

                            ModeMenuItem(
                                label = "Panier",
                                icon = Icons.Default.ShoppingCart,
                                isSelected = currentMode == Filter_Affichage_Mode_Proto.Panie,
                                onClick = {
                                    setMode(Filter_Affichage_Mode_Proto.Panie)
                                    showDropdown = false
                                }
                            )
                            ModeMenuItem(
                                label = "Panier + frères",
                                icon = Icons.Default.ShoppingCart,
                                isSelected = currentMode == Filter_Affichage_Mode_Proto.Panie_Si_Couleur_Ac_Vent_Affiche_Tout_Ces_Freres,
                                onClick = {
                                    setMode(Filter_Affichage_Mode_Proto.Panie_Si_Couleur_Ac_Vent_Affiche_Tout_Ces_Freres)
                                    showDropdown = false
                                }
                            )
                        }
                    }
                }

                But_4_FloatingSearchFAB(
                    searchText = viewModelNewProtoPatterns.active_Datas.filter_echatilaten,
                    onSearchTextChange = {
                        viewModelNewProtoPatterns.active_Datas.filter_echatilaten = it
                    },
                    currentMode = currentMode,
                )

                Text(
                    text = labelText,
                    modifier = Modifier
                        .background(color = fabColor, shape = RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    color = fabTint,
                    style = MaterialTheme.typography.bodySmall
                )
            }


        }
    }
}

@Composable
private fun ModeMenuItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    DropdownMenuItem(
        text = {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface
            )
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = {
            if (isSelected) Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        onClick = onClick
    )
}
