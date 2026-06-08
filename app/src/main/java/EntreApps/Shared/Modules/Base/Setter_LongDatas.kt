package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Modules

import EntreApps.Shared.Models.M09AppCompt
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Produits.Models.M16CategorieProduit
import android.util.Log
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.Relative_Produits.Models.Ref_list_Filtred_Keys_M3Couleur_Main_Values
import EntreApps.Shared.Models.Relative_Vents.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import EntreApps.Shared.Models.Relative_Vents.Models.M14VentPeriode
import EntreApps.Shared.Modules.Base.AppDatabase
import android.content.Context
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import kotlin.Any
import kotlin.String
import kotlin.collections.forEach
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import Application4.App.Fragment.ID1.Fragment.ViewModel.Prioriter
import EntreApps.Shared.Models.Components.DisponibilityEtates

@Suppress("unused")
class Setter_LongDatas(
    val appDatabase: AppDatabase,
    val  context: Context,
) {

    suspend fun delete_All_M01Produit() {
        appDatabase.dao_M1Produit().deleteAll()
    }

    suspend fun insertAll_M01Produit(items: List<M01Produit>) = withContext(Dispatchers.IO) {
        items.forEach { appDatabase.dao_M1Produit().upsertData(it) }
    }

    suspend fun get_Firebase_M01Produit_Counts(refDataBase: DatabaseReference): Pair<Int, Int> =
        withContext(Dispatchers.IO) {
            val snapshot = suspendFirebaseSnapshot(refDataBase)
            val total = snapshot.childrenCount.toInt()
            Pair(total, 0)
        }

    suspend fun export_M01Produit_Room_To_Csv(csv: File) = withContext(Dispatchers.IO) {
        val datas = appDatabase.dao_M1Produit().getAll()
        if (datas.isEmpty()) return@withContext

        csv.parentFile?.mkdirs()

        val headers = datas.first().to_Map().keys.toList()
        val existingRows: LinkedHashMap<String, List<String>> = linkedMapOf()

        if (csv.exists()) {
            val lines = csv.readLines()
            if (lines.size > 1) {
                val fileHeaders = lines[0].splitCsvLine()
                val keyIdx = fileHeaders.indexOf("keyID")
                lines.drop(1).forEach { line ->
                    val cells = line.splitCsvLine()
                    val id = cells.getOrNull(keyIdx) ?: ""
                    if (id.isNotEmpty()) existingRows[id] = cells
                }
            }
        }

        datas.forEach { item ->
            existingRows[item.keyID] = item.to_Map().values.map { (it?.toString() ?: "").escapeCsv() }
        }

        FileWriter(csv, false).use { w ->
            w.write(headers.joinToString(",") + "\n")
            existingRows.values.forEach { cells ->
                w.write(cells.joinToString(",") { it.escapeCsv() } + "\n")
            }
        }
    }

    suspend fun set_scv_M01Produit_au_fireBase(
        csvFile: File,
        refDataBase: DatabaseReference,
    ) = withContext(Dispatchers.IO) {
        if (!csvFile.exists() || csvFile.length() == 0L) return@withContext
        val lines = csvFile.readLines().filter { it.isNotBlank() }
        if (lines.size < 2) return@withContext

        val headers = lines[0].splitCsvLine()
        val keyIdx = headers.indexOf("keyID")
        if (keyIdx == -1) return@withContext

        val items = lines.drop(1).mapNotNull { line ->
            val cells = line.splitCsvLine()
            val keyID = cells.getOrNull(keyIdx)?.trim()?.removeSurrounding("\"")
            if (keyID.isNullOrBlank()) return@mapNotNull null
            val map = headers.zip(cells).associate { (h, v) ->
                h to v.trim().removeSurrounding("\"").ifEmpty { null }
            }
            runCatching { m01_from_Map(map) }.getOrNull()
        }

        if (items.isEmpty()) return@withContext
        val updates: Map<String, Any> = items.associate { it.keyID to it.to_Map() }
        refDataBase.updateChildren(updates).await()
    }

    suspend fun import_M01Produit_FireBase_To_Csv(
        refDataBase: DatabaseReference,
        csvFile: File,
    ) = withContext(Dispatchers.IO) {
        val snapshot = suspendFirebaseSnapshot(refDataBase)

        val items = snapshot.children.mapNotNull { child ->
            val raw = child.value
            if (raw !is Map<*, *>) return@mapNotNull null
            @Suppress("UNCHECKED_CAST")
            val map = (raw as Map<String, Any?>).mapValues { it.value?.toString() }
            val item = runCatching { m01_from_Map(map) }.getOrNull()
            item
        }

        if (items.isEmpty()) return@withContext

        csvFile.parentFile?.mkdirs()

        val headers = items.first().to_Map().keys.toList()
        val existingRows: LinkedHashMap<String, List<String>> = linkedMapOf()

        if (csvFile.exists()) {
            val lines = csvFile.readLines()
            if (lines.size > 1) {
                val fileHeaders = lines[0].splitCsvLine()
                val keyIdx = fileHeaders.indexOf("keyID")
                lines.drop(1).forEach { line ->
                    val cells = line.splitCsvLine()
                    val id = cells.getOrNull(keyIdx) ?: ""
                    if (id.isNotEmpty()) existingRows[id] = cells
                }
            }
        }

        items.forEach { item ->
            existingRows[item.keyID] = item.to_Map().values.map { (it?.toString() ?: "").escapeCsv() }
        }

        FileWriter(csvFile, false).use { w ->
            w.write(headers.joinToString(",") + "\n")
            existingRows.values.forEach { cells ->
                w.write(cells.joinToString(",") { it.escapeCsv() } + "\n")
            }
        }
    }

    suspend fun import_M01ProduitCsv_To_Room(csvFile: File) = withContext(Dispatchers.IO) {
        if (!csvFile.exists() || csvFile.length() == 0L) return@withContext
        val lines = csvFile.readLines().filter { it.isNotBlank() }
        if (lines.size < 2) return@withContext

        val headers = lines[0].splitCsvLine()
        val keyIdx = headers.indexOf("keyID")
        if (keyIdx == -1) return@withContext

        val items = lines.drop(1).mapNotNull { line ->
            val cells = line.splitCsvLine()
            val keyID = cells.getOrNull(keyIdx)?.trim()?.removeSurrounding("\"")
            if (keyID.isNullOrBlank()) return@mapNotNull null
            val map = headers.zip(cells).associate { (h, v) ->
                h to v.trim().removeSurrounding("\"").ifEmpty { null }
            }
            runCatching { m01_from_Map(map) }.getOrNull()
        }

        if (items.isNotEmpty()) {
            items.forEach { appDatabase.dao_M1Produit().upsertData(it) }
        }
    }

    suspend fun import_M01Produit_FireBase_To_Room(refDataBase: DatabaseReference) =
        withContext(Dispatchers.IO) {
            val snapshot = suspendFirebaseSnapshot(refDataBase)
            val items = snapshot.children.mapNotNull { child ->
                val raw = child.value
                if (raw !is Map<*, *>) return@mapNotNull null
                @Suppress("UNCHECKED_CAST")
                val map = (raw as Map<String, Any?>).mapValues { it.value?.toString() }
                val item = runCatching { m01_from_Map(map) }.getOrNull()
                item
            }
            items.forEach { appDatabase.dao_M1Produit().upsertData(it) }
        }

    private fun m01_from_Map(map: Map<String, String?>): M01Produit {
        return M01Produit(
            id = map["id"]?.toLongOrNull() ?: 0L,
            keyID = map["keyID"] ?: M01Produit.generePushKey(),
            creationTimestamp = map["creationTimestamp"]?.toLongOrNull() ?: System.currentTimeMillis(),
            dernierTimeTampsSynchronisationAvecFireBase = map["dernierTimeTampsSynchronisationAvecFireBase"]?.toLongOrNull() ?: System.currentTimeMillis(),
            bsonObjectId = map["bsonObjectId"] ?: "",
            dernierFireBaseUpdateTimestamps = map["dernierFireBaseUpdateTimestamps"]?.toLongOrNull() ?: 0L,
            count_Don_Depot = map["count_Don_Depot"]?.toIntOrNull() ?: 0,
            idParentCategorie = map["idParentCategorie"]?.toLongOrNull() ?: 0L,
            positionDonSonCesFrereCategorieProduits = map["positionDonSonCesFrereCategorieProduits"]?.toIntOrNull() ?: 0,
            nom = map["nom"] ?: "",
            nomMutable = map["nomMutable"] ?: "",
            processPositioningInFactory = map["processPositioningInFactory"]?.let {
                runCatching { M01Produit.ProcessPositioningInFactoryID1.valueOf(it) }.getOrNull()
            } ?: M01Produit.ProcessPositioningInFactoryID1.CreeAuGeneralHandler,
            etateActuelleOnFusionAvecBaseDonne = map["etateActuelleOnFusionAvecBaseDonne"]?.let {
                runCatching { M01Produit.EtateActuelleOnFusionAvecBaseDonne.valueOf(it) }.getOrNull()
            } ?: M01Produit.EtateActuelleOnFusionAvecBaseDonne.CategorieOriginaleDefinie,
            tag_prioriter_str = map["tag_prioriter_str"] ?: "",
            nombreUniteInt = map["nombreUniteInt"]?.toIntOrNull() ?: 1,
            nombreProduitDonSonCarton = map["nombreProduitDonSonCarton"]?.toIntOrNull() ?: 1,
            its_Carton = map["its_Carton"]?.toBoolean() ?: false,
            cartonState = map["cartonState"] ?: "",
            heldPrioriteDemandAuGrossist = map["heldPrioriteDemandAuGrossist"]?.toBoolean() ?: false,
            position_store_3jamale = map["position_store_3jamale"]?.toIntOrNull() ?: 0,
            dernier_timeTamps_position_store_3jamale = map["dernier_timeTamps_position_store_3jamale"]?.toLongOrNull() ?: 0L,
            prixDefiniParGerant = map["prixDefiniParGerant"]?.toDoubleOrNull() ?: 0.0,
            prixVent = map["prixVent"]?.toDoubleOrNull() ?: 0.0,
            cachePrixVent = map["cachePrixVent"]?.toBoolean() ?: false,
            pourcentage_Prix_Progressive = map["pourcentage_Prix_Progressive"]?.toIntOrNull() ?: 60,
            prixAchat = map["prixAchat"]?.toDoubleOrNull() ?: 0.0,
            prixAchatDernierTimeTempUpdate = map["prixAchatDernierTimeTempUpdate"]?.toLongOrNull() ?: 0L,
            clientPrixVentUnite = map["clientPrixVentUnite"]?.toDoubleOrNull() ?: 0.0,
            afficheUniteAuPrint = map["afficheUniteAuPrint"]?.toBoolean() ?: false,
            monPrixVentUniter = map["monPrixVentUniter"]?.toDoubleOrNull() ?: 0.0,
            actualiseSonImage = map["actualiseSonImage"]?.toIntOrNull() ?: 0,
            actualiseSonImageTest2 = map["actualiseSonImageTest2"]?.toIntOrNull() ?: 0,
            afficheCesDetailPourComptBsonId = map["afficheCesDetailPourComptBsonId"] ?: "",
            disponibilityEtates = map["disponibilityEtates"]?.let {
                runCatching { DisponibilityEtates.valueOf(it) }.getOrNull()
            } ?: DisponibilityEtates.NON_DISPO,
            disponibilityEtates_Pour_presentaion_par_Camion = map["disponibilityEtates_Pour_presentaion_par_Camion"]?.let {
                runCatching { DisponibilityEtates.valueOf(it) }.getOrNull()
            } ?: DisponibilityEtates.NON_DISPO,
            keyFireBase = map["keyFireBase"] ?: "",
            nomArab = map["nomArab"] ?: "",
            autreNomDarticle = map["autreNomDarticle"],
            couleur1 = map["couleur1"] ?: "couleur1",
            couleur2 = map["couleur2"],
            couleur3 = map["couleur3"],
            couleur4 = map["couleur4"],
            couleur5 = map["couleur5"],
            couleur6 = map["couleur6"],
            couleur7 = map["couleur7"],
            couleur8 = map["couleur8"],
            couleur9 = map["couleur9"],
            idcolor1 = map["idcolor1"]?.toLongOrNull() ?: 1L,
            idcolor2 = map["idcolor2"]?.toLongOrNull() ?: 0L,
            idcolor3 = map["idcolor3"]?.toLongOrNull() ?: 0L,
            idcolor4 = map["idcolor4"]?.toLongOrNull() ?: 0L,
            idcolor5 = map["idcolor5"]?.toLongOrNull() ?: 0L,
            idcolor6 = map["idcolor6"]?.toLongOrNull() ?: 0L,
            idcolor7 = map["idcolor7"]?.toLongOrNull() ?: 0L,
            idcolor8 = map["idcolor8"]?.toLongOrNull() ?: 0L,
            idcolor9 = map["idcolor9"]?.toLongOrNull() ?: 0L,
            nomCategorie2 = map["nomCategorie2"],
            affichageUniteState = map["affichageUniteState"]?.toBoolean() ?: false,
            commmentSeVent = map["commmentSeVent"],
            afficheBoitSiUniter = map["afficheBoitSiUniter"],
            minQuan = map["minQuan"]?.toIntOrNull() ?: 0,
            monBenfice = map["monBenfice"]?.toDoubleOrNull() ?: 0.0,
            neaon2 = map["neaon2"] ?: "",
            funChangeImagsDimention = map["funChangeImagsDimention"]?.toBoolean() ?: false,
            nomCategorie = map["nomCategorie"] ?: "",
            neaon1 = map["neaon1"]?.toDoubleOrNull() ?: 0.0,
            lastUpdateState = map["lastUpdateState"] ?: "",
            dateCreationCategorie = map["dateCreationCategorie"] ?: "",
            prixDeVentTotaleChezClient = map["prixDeVentTotaleChezClient"]?.toDoubleOrNull() ?: 0.0,
            benficeTotaleEntreMoiEtClien = map["benficeTotaleEntreMoiEtClien"]?.toDoubleOrNull() ?: 0.0,
            benificeTotaleEn2 = map["benificeTotaleEn2"]?.toDoubleOrNull() ?: 0.0,
            monPrixAchatUniter = map["monPrixAchatUniter"]?.toDoubleOrNull() ?: 0.0,
            articleHaveUniteImages = map["articleHaveUniteImages"]?.toBoolean() ?: false,
            itsNewArrivale = map["itsNewArrivale"]?.toBoolean() ?: false,
            imageDimention = map["imageDimention"] ?: "",
            idForSearchArticles = map["idForSearchArticles"]?.toLongOrNull() ?: 0L,
            quantite_Boit_Par_Carton = map["quantite_Boit_Par_Carton"]?.toIntOrNull() ?: 1,
            prioriter = map["prioriter"]?.let {
                runCatching { Prioriter.valueOf(it) }.getOrNull()
            },
            setIN_Vent_Its_Quantity_Represent = map["setIN_Vent_Its_Quantity_Represent"]?.let {
                runCatching { M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.valueOf(it) }.getOrNull()
            } ?: M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.quantity_Par_Boit
        )
    }

    suspend fun delete_All_M09AppCompt() {
        appDatabase.dao_M9AppCompt().deleteAll()
    }

    suspend fun insertAll_M09AppCompt(items: List<M09AppCompt>) = withContext(Dispatchers.IO) {
        items.forEach { appDatabase.dao_M9AppCompt().upsert(it) }
    }

    suspend fun get_Firebase_M09AppCompt_Counts(refDataBase: DatabaseReference): Pair<Int, Int> =
        withContext(Dispatchers.IO) {
            val snapshot = suspendFirebaseSnapshot(refDataBase)
            val total = snapshot.childrenCount.toInt()
            Pair(total, 0)
        }

    suspend fun export_M09AppCompt_Room_To_Csv(csv: File) = withContext(Dispatchers.IO) {
        val datas = appDatabase.dao_M9AppCompt().getAll()
        if (datas.isEmpty()) return@withContext

        csv.parentFile?.mkdirs()

        val headers = datas.first().to_Map().keys.toList()
        val existingRows: LinkedHashMap<String, List<String>> = linkedMapOf()

        if (csv.exists()) {
            val lines = csv.readLines()
            if (lines.size > 1) {
                val fileHeaders = lines[0].splitCsvLine()
                val keyIdx = fileHeaders.indexOf("keyID")
                lines.drop(1).forEach { line ->
                    val cells = line.splitCsvLine()
                    val id = cells.getOrNull(keyIdx) ?: ""
                    if (id.isNotEmpty()) existingRows[id] = cells
                }
            }
        }

        datas.forEach { item ->
            existingRows[item.keyID] = item.to_Map().values.map { (it?.toString() ?: "").escapeCsv() }
        }

        FileWriter(csv, false).use { w ->
            w.write(headers.joinToString(",") + "\n")
            existingRows.values.forEach { cells ->
                w.write(cells.joinToString(",") { it.escapeCsv() } + "\n")
            }
        }
    }

    suspend fun set_scv_M09AppCompt_au_fireBase(
        csvFile: File,
        refDataBase: DatabaseReference,
    ) = withContext(Dispatchers.IO) {
        if (!csvFile.exists() || csvFile.length() == 0L) return@withContext
        val lines = csvFile.readLines().filter { it.isNotBlank() }
        if (lines.size < 2) return@withContext

        val headers = lines[0].splitCsvLine()
        val keyIdx = headers.indexOf("keyID")
        if (keyIdx == -1) return@withContext

        val items = lines.drop(1).mapNotNull { line ->
            val cells = line.splitCsvLine()
            val keyID = cells.getOrNull(keyIdx)?.trim()?.removeSurrounding("\"")
            if (keyID.isNullOrBlank()) return@mapNotNull null
            val map = headers.zip(cells).associate { (h, v) ->
                h to v.trim().removeSurrounding("\"").ifEmpty { null }
            }
            runCatching { m09_from_Map(map) }.getOrNull()
        }

        if (items.isEmpty()) return@withContext
        val updates: Map<String, Any> = items.associate { it.keyID to it.to_Map() }
        refDataBase.updateChildren(updates).await()
    }

    suspend fun import_M09AppCompt_FireBase_To_Csv(
        refDataBase: DatabaseReference,
        csvFile: File,
    ) = withContext(Dispatchers.IO) {
        val snapshot = suspendFirebaseSnapshot(refDataBase)

        val items = snapshot.children.mapNotNull { child ->
            val raw = child.value
            if (raw !is Map<*, *>) return@mapNotNull null
            @Suppress("UNCHECKED_CAST")
            val map = (raw as Map<String, Any?>).mapValues { it.value?.toString() }
            val item = runCatching { m09_from_Map(map) }.getOrNull()
            item
        }

        if (items.isEmpty()) return@withContext

        csvFile.parentFile?.mkdirs()

        val headers = items.first().to_Map().keys.toList()
        val existingRows: LinkedHashMap<String, List<String>> = linkedMapOf()

        if (csvFile.exists()) {
            val lines = csvFile.readLines()
            if (lines.size > 1) {
                val fileHeaders = lines[0].splitCsvLine()
                val keyIdx = fileHeaders.indexOf("keyID")
                lines.drop(1).forEach { line ->
                    val cells = line.splitCsvLine()
                    val id = cells.getOrNull(keyIdx) ?: ""
                    if (id.isNotEmpty()) existingRows[id] = cells
                }
            }
        }

        items.forEach { item ->
            existingRows[item.keyID] = item.to_Map().values.map { (it?.toString() ?: "").escapeCsv() }
        }

        FileWriter(csvFile, false).use { w ->
            w.write(headers.joinToString(",") + "\n")
            existingRows.values.forEach { cells ->
                w.write(cells.joinToString(",") { it.escapeCsv() } + "\n")
            }
        }
    }

    suspend fun import_M09AppComptCsv_To_Room(csvFile: File) = withContext(Dispatchers.IO) {
        if (!csvFile.exists() || csvFile.length() == 0L) return@withContext
        val lines = csvFile.readLines().filter { it.isNotBlank() }
        if (lines.size < 2) return@withContext

        val headers = lines[0].splitCsvLine()
        val keyIdx = headers.indexOf("keyID")
        if (keyIdx == -1) return@withContext

        val items = lines.drop(1).mapNotNull { line ->
            val cells = line.splitCsvLine()
            val keyID = cells.getOrNull(keyIdx)?.trim()?.removeSurrounding("\"")
            if (keyID.isNullOrBlank()) return@mapNotNull null
            val map = headers.zip(cells).associate { (h, v) ->
                h to v.trim().removeSurrounding("\"").ifEmpty { null }
            }
            runCatching { m09_from_Map(map) }.getOrNull()
        }

        if (items.isNotEmpty()) {
            items.forEach { appDatabase.dao_M9AppCompt().upsert(it) }
        }
    }

    suspend fun import_M09AppCompt_FireBase_To_Room(refDataBase: DatabaseReference) =
        withContext(Dispatchers.IO) {
            val snapshot = suspendFirebaseSnapshot(refDataBase)
            val items = snapshot.children.mapNotNull { child ->
                val raw = child.value
                if (raw !is Map<*, *>) return@mapNotNull null
                @Suppress("UNCHECKED_CAST")
                val map = (raw as Map<String, Any?>).mapValues { it.value?.toString() }
                val item = runCatching { m09_from_Map(map) }.getOrNull()
                item
            }
            items.forEach { appDatabase.dao_M9AppCompt().upsert(it) }
        }

    private fun m09_from_Map(map: Map<String, String?>): M09AppCompt {
        return M09AppCompt(
            keyID = map["keyID"] ?: M09AppCompt.generePushKey(),
            creationTimestamp = map["creationTimestamp"]?.toLongOrNull() ?: System.currentTimeMillis(),
            dernierTimeTampsSynchronisationAvecFireBase = map["dernierTimeTampsSynchronisationAvecFireBase"]?.toLongOrNull() ?: System.currentTimeMillis(),
            appDesignedPourWorkingGrossisst3Ali = map["appDesignedPourWorkingGrossisst3Ali"]?.toBoolean() ?: true,
            its_mode_affiche_que_produits_au_depot = map["its_mode_affiche_que_produits_au_depot"]?.toBoolean() ?: true,
            mode_edite_dispo = map["mode_edite_dispo"]?.toBoolean() ?: false,
            credit_fait = map["credit_fait"]?.toDoubleOrNull() ?: 0.0,
            nom = map["nom"] ?: "",
            autres_Noms_SepareParComma = map["autres_Noms_SepareParComma"] ?: "",
            deviceModelNom = map["deviceModelNom"] ?: android.os.Build.MODEL,
            deviceModelId = map["deviceModelId"] ?: android.os.Build.ID,
            period_Qui_Doit_Etre_Au_Entre = map["period_Qui_Doit_Etre_Au_Entre"] ?: "",
            separeted_by_commas_keys_clients_a_cible_groupe_n1 = map["separeted_by_commas_keys_clients_a_cible_groupe_n1"] ?: ",",
            keys_clients_a_cible_groupe_n2 = map["keys_clients_a_cible_groupe_n2"] ?: ",",
            keys_clients_a_cible_groupe_n3 = map["keys_clients_a_cible_groupe_n3"] ?: ",",
            image_detail_produit_s_affiche = map["image_detail_produit_s_affiche"]?.toBoolean() ?: true,
            presentoireEBoutiqueFilterProduitDuCatalogueAvecBsonObjectId = map["presentoireEBoutiqueFilterProduitDuCatalogueAvecBsonObjectId"] ?: "",
            hideAppScreen = map["hideAppScreen"]?.toBoolean() ?: false,
            travailleChezGrossisst3Ali = map["travailleChezGrossisst3Ali"]?.toBoolean() ?: false,
            affiche_toujoure_tariffs_tournet = map["affiche_toujoure_tariffs_tournet"]?.toBoolean() ?: false,
            its_Admin = map["its_Admin"]?.toBoolean() ?: false,
            c_Ouvert_Pour_Au_Command_Add_Period = map["c_Ouvert_Pour_Au_Command_Add_Period"]?.toBoolean() ?: true,
            text_Message_Warning = map["text_Message_Warning"] ?: "",
            ne_affiche_que_fragment = map["ne_affiche_que_fragment"] ?: "",
            itsProductionModePourCeCompt = map["itsProductionModePourCeCompt"]?.toBoolean() ?: false,
            ceComptVendeurInsertBonsAchatAuPeriodID = map["ceComptVendeurInsertBonsAchatAuPeriodID"]?.toLongOrNull() ?: 0L,
            ceComptVendeurStartAffichePeriod = map["ceComptVendeurStartAffichePeriod"]?.toLongOrNull() ?: 0L,
            migreSonDataBaseAuStart = map["migreSonDataBaseAuStart"]?.toBoolean() ?: false,
            cConnectAuDevelopingDataBaseAuRelodApp = map["cConnectAuDevelopingDataBaseAuRelodApp"]?.toBoolean() ?: false,
            mainInitDataBaseProgressEtate = map["mainInitDataBaseProgressEtate"]?.toFloatOrNull() ?: 0f,
            its_Panie_Mode_Au_Lence_Boutique = map["its_Panie_Mode_Au_Lence_Boutique"]?.toBoolean() ?: false,
            filter_marqueClient_Name = map["filter_marqueClient_Name"] ?: "no Filter",
            next_start = map["next_start"]?.let {
                runCatching { EntreApps.Shared.Models.Do.valueOf(it) }.getOrNull()
            } ?: EntreApps.Shared.Models.Do.StandartInit_Sans_RienFair,
            force_next_start_ = map["force_next_start_"]?.let {
                runCatching { EntreApps.Shared.Models.Do.valueOf(it) }.getOrNull()
            } ?: EntreApps.Shared.Models.Do.DeleteInsertAll_Active_Key,
            activeDialogSearchM1Produit = map["activeDialogSearchM1Produit"]?.toBoolean() ?: false,
            active_ProduitKeyID_Au_DroopDown_PresenterEcran = map["active_ProduitKeyID_Au_DroopDown_PresenterEcran"] ?: "",
            active_CouleurKeyID_Extended_Image = map["active_CouleurKeyID_Extended_Image"] ?: "",
            affiche_Dialog_Fast_Affiche_Panie_App4 = map["affiche_Dialog_Fast_Affiche_Panie_App4"]?.toBoolean() ?: false,
            affiche_ProduitDataBaseEdites_ComposableViews = map["affiche_ProduitDataBaseEdites_ComposableViews"]?.toBoolean() ?: true,
            couleurAchateOperationIdOuvertPourCeCompt = map["couleurAchateOperationIdOuvertPourCeCompt"] ?: "",
            couleurAchateOperationKeyOuvertPourCeCompt = map["couleurAchateOperationKeyOuvertPourCeCompt"] ?: "",
            ouvertProduitOnVentNom = map["ouvertProduitOnVentNom"] ?: "",
            current_OnVent_M14VentPeriode_KeyID = map["current_OnVent_M14VentPeriode_KeyID"] ?: "",
            current_OnVent_M14VentPeriode_DebugInfos = map["current_OnVent_M14VentPeriode_DebugInfos"] ?: "",
            onVentM8BonVentKey = map["onVentM8BonVentKey"] ?: "",
            onVentM8BonVentDebugInfos = map["onVentM8BonVentDebugInfos"] ?: "",
            onVentM1ProduitInfosKeyID = map["onVentM1ProduitInfosKeyID"] ?: "",
            onVentM1ProduitInfosDebugName = map["onVentM1ProduitInfosDebugName"] ?: "",
            onVentM3CouleurProduitInfosKeyID = map["onVentM3CouleurProduitInfosKeyID"] ?: "null",
            onVentM3CouleurProduitDebugInfos = map["onVentM3CouleurProduitDebugInfos"] ?: "null",
            dialogAboveAll_OutlinedSearchListProduits = map["dialogAboveAll_OutlinedSearchListProduits"]?.toBoolean() ?: false,
            dialogChoisireQuantityM1ProduitInfosKeyID = map["dialogChoisireQuantityM1ProduitInfosKeyID"] ?: "null",
            dialogChoisireQuantityM1ProduitInfosDebugName = map["dialogChoisireQuantityM1ProduitInfosDebugName"] ?: "null",
            activeFocuce_TariffPrixDifineur_M1ProduitKeyID = map["activeFocuce_TariffPrixDifineur_M1ProduitKeyID"] ?: "null",
            activeFocuceTariffPrixDifineurM1ProduitDebugInfos = map["activeFocuceTariffPrixDifineurM1ProduitDebugInfos"] ?: "null",
            startTextSearchM1Produit = map["startTextSearchM1Produit"] ?: "",
            click_On_Marque = map["click_On_Marque"]?.let {
                runCatching { EntreApps.Shared.Models.Home.ActiveCentralValues.Click_On_Marque.valueOf(it) }.getOrNull()
            } ?: EntreApps.Shared.Models.Home.ActiveCentralValues.Click_On_Marque.Standart,
            KeyByParent = map["KeyByParent"] ?: "",
            vid = map["vid"]?.toLongOrNull() ?: 1L,
            limite_couleurs_ou_leur_last_achate_est_moin_que_jour = map["limite_couleurs_ou_leur_last_achate_est_moin_que_jour"]?.toIntOrNull() ?: 30
        )
    }

    private val composScope = CoroutineScope(Dispatchers.IO)

    suspend fun add_New_M2Client(client: M2Client) {
        appDatabase.dao_M2Client().insert(client)
    }

    suspend fun update_M2Client(client: M2Client) = withContext(Dispatchers.IO) {
        appDatabase.dao_M2Client().upsert(client)
    }

    suspend fun insertAll_M2Client(clients: List<M2Client>) = withContext(Dispatchers.IO) {
        clients.forEach { appDatabase.dao_M2Client().upsert(it) }
    }

    suspend fun bach_update_FireBase_M2Client(
        clients: List<M2Client>,
        refDataBase: DatabaseReference,
    ) = withContext(Dispatchers.IO) {
        clients.forEachIndexed { _, client ->
            runCatching {
                suspendCancellableCoroutine { cont ->
                    refDataBase.child(client.keyID).setValue(client.toFirebaseMap())
                        .addOnSuccessListener { cont.resume(Unit) }
                        .addOnFailureListener { cont.resumeWithException(it) }
                }
            }.onFailure { return@withContext }
        }
    }

    suspend fun get_Firebase_M2Client_Counts(
        refDataBase: DatabaseReference
    ): Pair<Int, Int> =
        withContext(Dispatchers.IO) {
            val snapshot = suspendFirebaseSnapshot(refDataBase)
            val total = snapshot.childrenCount.toInt()
            Pair(total, 0)
        }

    suspend fun get_Firebase_M2Client_Count(refDataBase: DatabaseReference): Int =
        withContext(Dispatchers.IO) {
            suspendFirebaseSnapshot(refDataBase).childrenCount.toInt()
        }

    suspend fun export_M2Client_Room_To_Csv(csv: File) = withContext(Dispatchers.IO) {
        val datas = appDatabase.dao_M2Client().getAll()
        if (datas.isEmpty()) return@withContext

        csv.parentFile?.mkdirs()

        val headers = datas.first().toFirebaseMap().keys.toList()
        val existingRows: LinkedHashMap<String, List<String>> = linkedMapOf()

        if (csv.exists()) {
            val lines = csv.readLines()
            if (lines.size > 1) {
                val fileHeaders = lines[0].splitCsvLine()
                val keyIdx = fileHeaders.indexOf("keyID")
                lines.drop(1).forEach { line ->
                    val cells = line.splitCsvLine()
                    val id = cells.getOrNull(keyIdx) ?: ""
                    if (id.isNotEmpty()) existingRows[id] = cells
                }
            }
        }

        datas.forEach { client ->
            existingRows[client.keyID] = client.toFirebaseMap().values.map { (it?.toString() ?: "").escapeCsv() }
        }

        FileWriter(csv, false).use { w ->
            w.write(headers.joinToString(",") + "\n")
            existingRows.values.forEach { cells ->
                w.write(cells.joinToString(",") { it.escapeCsv() } + "\n")
            }
        }
    }

    suspend fun set_scv_m2client_au_fireBase(
        csvFile: File,
        refDataBase: DatabaseReference,
    ) = withContext(Dispatchers.IO) {
        if (!csvFile.exists() || csvFile.length() == 0L) return@withContext
        val lines = csvFile.readLines().filter { it.isNotBlank() }
        if (lines.size < 2) return@withContext

        val headers = lines[0].splitCsvLine()
        val keyIdx = headers.indexOf("keyID")
        if (keyIdx == -1) return@withContext

        val clients = lines.drop(1).mapNotNull { line ->
            val cells = line.splitCsvLine()
            val keyID = cells.getOrNull(keyIdx)?.trim()?.removeSurrounding("\"")
            if (keyID.isNullOrBlank()) return@mapNotNull null
            val map = headers.zip(cells).associate { (h, v) ->
                h to v.trim().removeSurrounding("\"").ifEmpty { null }
            }
            runCatching { m2client_from_Map(map) }.getOrNull()
        }

        if (clients.isEmpty()) return@withContext
        bach_update_FireBase_M2Client(clients, refDataBase)
    }

    suspend fun import_M2Client_FireBase_To_Csv(
        refDataBase: DatabaseReference,
        csvFile: File,
        importOnlyCredits: Boolean = false,
    ) = withContext(Dispatchers.IO) {
        val snapshot = suspendFirebaseSnapshot(refDataBase)

        val clients = snapshot.children.mapNotNull { child ->
            val raw = child.value
            if (raw !is Map<*, *>) return@mapNotNull null
            @Suppress("UNCHECKED_CAST")
            val map = (raw as Map<String, Any?>).mapValues { it.value?.toString() }
            val client = runCatching { m2client_from_Map(map) }.getOrNull()
            client
        }

        if (clients.isEmpty()) return@withContext

        csvFile.parentFile?.mkdirs()

        val headers = clients.first().toFirebaseMap().keys.toList()
        val existingRows: LinkedHashMap<String, List<String>> = linkedMapOf()

        if (csvFile.exists()) {
            val lines = csvFile.readLines()
            if (lines.size > 1) {
                val fileHeaders = lines[0].splitCsvLine()
                val keyIdx = fileHeaders.indexOf("keyID")
                lines.drop(1).forEach { line ->
                    val cells = line.splitCsvLine()
                    val id = cells.getOrNull(keyIdx) ?: ""
                    if (id.isNotEmpty()) existingRows[id] = cells
                }
            }
        }

        clients.forEach { client ->
            existingRows[client.keyID] = client.toFirebaseMap().values.map { (it?.toString() ?: "").escapeCsv() }
        }

        FileWriter(csvFile, false).use { w ->
            w.write(headers.joinToString(",") + "\n")
            existingRows.values.forEach { cells ->
                w.write(cells.joinToString(",") { it.escapeCsv() } + "\n")
            }
        }
    }

    suspend fun import_M2ClientCsv_To_Room(
        csvFile: File,
        importOnlyCredits: Boolean = false,
    ) = withContext(Dispatchers.IO) {
        if (!csvFile.exists() || csvFile.length() == 0L) return@withContext
        val lines = csvFile.readLines().filter { it.isNotBlank() }
        if (lines.size < 2) return@withContext

        val headers = lines[0].splitCsvLine()
        var clients = lines.drop(1).mapNotNull { line ->
            val cells = line.splitCsvLine()
            val map = headers.zip(cells).associate { (h, v) ->
                h to v.trim().removeSurrounding("\"").ifEmpty { null }
            }
            runCatching { m2client_from_Map(map) }.getOrNull()
        }

        if (clients.isNotEmpty()) clients.forEach { appDatabase.dao_M2Client().upsert(it) }
    }

    suspend fun import_M2Client_FireBase_To_Room(
        refDataBase: DatabaseReference,
        importOnlyCredits: Boolean = false,
    ) = withContext(Dispatchers.IO) {
        val snapshot = suspendFirebaseSnapshot(refDataBase)

        var clients = snapshot.children.mapNotNull { child ->
            val raw = child.value
            if (raw !is Map<*, *>) return@mapNotNull null
            @Suppress("UNCHECKED_CAST")
            val map = (raw as Map<String, Any?>).mapValues { it.value?.toString() }
            runCatching { m2client_from_Map(map) }.getOrNull()
        }

        if (clients.isEmpty()) return@withContext
        clients.forEach { appDatabase.dao_M2Client().upsert(it) }
    }

    suspend fun delete_All_M2Client() {
        appDatabase.dao_M2Client().deleteAll()
    }

    private fun m2client_from_Map(map: Map<String, String?>): M2Client {
        return M2Client(
            keyID = map["keyID"] ?: M2Client.generePushKey(),
            c_un_admin_client = map["c_un_admin_client"]?.toBoolean() ?: false,
            nom_worker = map["nom_worker"] ?: "",
            num_worker = map["num_worker"] ?: "",
            dernierTimeTampsSynchronisationAvecFireBase = map["dernierTimeTampsSynchronisationAvecFireBase"]?.toLongOrNull() ?: 0L,
            creationTimestamps = map["creationTimestamps"]?.toLongOrNull() ?: System.currentTimeMillis(),
            nom = map["nom"] ?: "Non Defini",
            cretionTimestamps = map["cretionTimestamps"]?.toLongOrNull() ?: System.currentTimeMillis(),
            its_Fournisseur = map["its_Fournisseur"]?.toBoolean() ?: false,
            parentComptCreateurKEyID = map["parentComptCreateurKEyID"] ?: "",
            numTelephone = map["numTelephone"] ?: "",
            couleur = map["couleur"] ?: "#FFFFFF",
            bonDuClientsSu = map["bonDuClientsSu"] ?: "",
            currentCreditBalance = map["currentCreditBalance"]?.toDoubleOrNull() ?: 0.0,
            positionDonClientsList = map["positionDonClientsList"]?.toIntOrNull() ?: 0,
            cUnClientTemporaire = map["cUnClientTemporaire"]?.toBoolean() ?: true,
            auFilterFAB = map["auFilterFAB"]?.toBoolean() ?: false,
            typeDeSonMagasine = map["typeDeSonMagasine"]?.let { runCatching { M2Client.TypeDeSonMagasine.valueOf(it) }.getOrNull() } ?: M2Client.TypeDeSonMagasine.ATAYAT_MOUKASSARAT,
            clientTypeMode = map["clientTypeMode"]?.let { runCatching { M2Client.ClientTypeMode.valueOf(it) }.getOrNull() } ?: M2Client.ClientTypeMode.NEVEAU,
            caMarqueGpsEstOuvert = map["caMarqueGpsEstOuvert"]?.toBoolean() ?: false,
            latitude = map["latitude"]?.toDoubleOrNull() ?: 0.0,
            longitude = map["longitude"]?.toDoubleOrNull() ?: 0.0,
            title = map["title"] ?: "",
            snippet = map["snippet"] ?: "",
            actuelleEtat = map["actuelleEtat"]?.let { runCatching { M2Client.DernierEtatAAffiche.valueOf(it) }.getOrNull() } ?: M2Client.DernierEtatAAffiche.NON_DEFINI,
            edite_Exact_Gps_est_fait = map["edite_Exact_Gps_est_fait"]?.toBoolean() ?: false,
            tagCeBonEstOuvertPourComptsIds = map["tagCeBonEstOuvertPourComptsIds"] ?: "",
            id = map["id"]?.toLongOrNull() ?: 0L,
            keyByParent = map["keyByParent"] ?: "",
            bsonObjectId = map["bsonObjectId"] ?: "",
            nomPrenomArabe = map["nomPrenomArabe"] ?: "حمنيش عبد الوهاب",
            register_Commerce_Nm = map["register_Commerce_Nm"] ?: "16/00 – 5138424 D20",
            nif_Num = map["nif_Num"] ?: "16291403036"
        )
    }
    suspend fun delete_All_M03() {
        appDatabase.dao_M03CouleurProduitInfos().deleteAll()
    }

    suspend fun insertAll_M03(items: List<M3CouleurProduitInfos>) = withContext(Dispatchers.IO) {
        items.forEach { appDatabase.dao_M03CouleurProduitInfos().upsert(it) }
    }

    suspend fun get_Firebase_M03_Counts(refDataBase: DatabaseReference): Pair<Int, Int> =
        withContext(Dispatchers.IO) {
            val snapshot = suspendFirebaseSnapshot(refDataBase)
            val total = snapshot.childrenCount.toInt()
            Pair(total, 0)
        }

    suspend fun export_M03_Room_To_Csv(csv: File) = withContext(Dispatchers.IO) {
        val datas = appDatabase.dao_M03CouleurProduitInfos().getAll()
        if (datas.isEmpty()) return@withContext

        csv.parentFile?.mkdirs()

        val headers = datas.first().to_Map().keys.toList()
        val existingRows: LinkedHashMap<String, List<String>> = linkedMapOf()

        if (csv.exists()) {
            val lines = csv.readLines()
            if (lines.size > 1) {
                val fileHeaders = lines[0].splitCsvLine()
                val keyIdx = fileHeaders.indexOf("keyID")
                lines.drop(1).forEach { line ->
                    val cells = line.splitCsvLine()
                    val id = cells.getOrNull(keyIdx) ?: ""
                    if (id.isNotEmpty()) existingRows[id] = cells
                }
            }
        }

        datas.forEach { item ->
            existingRows[item.keyID] =
                item.to_Map().values.map { (it?.toString() ?: "").escapeCsv() }
        }

        FileWriter(csv, false).use { w ->
            w.write(headers.joinToString(",") + "\n")
            existingRows.values.forEach { cells ->
                w.write(cells.joinToString(",") { it.escapeCsv() } + "\n")
            }
        }
    }

    suspend fun set_scv_M03_au_fireBase(
        csvFile: File,
        refDataBase: DatabaseReference,
    ) = withContext(Dispatchers.IO) {
        if (!csvFile.exists() || csvFile.length() == 0L) return@withContext
        val lines = csvFile.readLines().filter { it.isNotBlank() }
        if (lines.size < 2) return@withContext

        val headers = lines[0].splitCsvLine()
        val keyIdx = headers.indexOf("keyID")
        if (keyIdx == -1) return@withContext

        val items = lines.drop(1).mapNotNull { line ->
            val cells = line.splitCsvLine()
            val keyID = cells.getOrNull(keyIdx)?.trim()?.removeSurrounding("\"")
            if (keyID.isNullOrBlank()) return@mapNotNull null
            val map = headers.zip(cells).associate { (h, v) ->
                h to v.trim().removeSurrounding("\"").ifEmpty { null }
            }
            runCatching { m03_from_Map(map) }.getOrNull()
        }

        if (items.isEmpty()) return@withContext

        items.forEach { item ->
            runCatching {
                suspendCancellableCoroutine { cont ->
                    refDataBase.child(item.keyID).setValue(item.to_Map())
                        .addOnSuccessListener { cont.resume(Unit) }
                        .addOnFailureListener { cont.resumeWithException(it) }
                }
            }.onFailure { return@withContext }
        }
    }

    suspend fun import_M03_FireBase_To_Csv(
        refDataBase: DatabaseReference,
        csvFile: File,
    ) = withContext(Dispatchers.IO) {
        val snapshot = suspendFirebaseSnapshot(refDataBase)

        val items = snapshot.children.mapNotNull { child ->
            val raw = child.value
            if (raw !is Map<*, *>) return@mapNotNull null
            @Suppress("UNCHECKED_CAST")
            val map = (raw as Map<String, Any?>).mapValues { it.value?.toString() }
            runCatching { m03_from_Map(map) }.getOrNull()
        }

        if (items.isEmpty()) return@withContext

        csvFile.parentFile?.mkdirs()

        val headers = items.first().to_Map().keys.toList()
        val existingRows: LinkedHashMap<String, List<String>> = linkedMapOf()

        if (csvFile.exists()) {
            val lines = csvFile.readLines()
            if (lines.size > 1) {
                val fileHeaders = lines[0].splitCsvLine()
                val keyIdx = fileHeaders.indexOf("keyID")
                lines.drop(1).forEach { line ->
                    val cells = line.splitCsvLine()
                    val id = cells.getOrNull(keyIdx) ?: ""
                    if (id.isNotEmpty()) existingRows[id] = cells
                }
            }
        }

        items.forEach { item ->
            existingRows[item.keyID] =
                item.to_Map().values.map { (it?.toString() ?: "").escapeCsv() }
        }

        FileWriter(csvFile, false).use { w ->
            w.write(headers.joinToString(",") + "\n")
            existingRows.values.forEach { cells ->
                w.write(cells.joinToString(",") { it.escapeCsv() } + "\n")
            }
        }
    }

    suspend fun import_M03Csv_To_Room(csvFile: File) = withContext(Dispatchers.IO) {
        if (!csvFile.exists() || csvFile.length() == 0L) return@withContext
        val lines = csvFile.readLines().filter { it.isNotBlank() }
        if (lines.size < 2) return@withContext

        val headers = lines[0].splitCsvLine()
        val items = lines.drop(1).mapNotNull { line ->
            val cells = line.splitCsvLine()
            val map = headers.zip(cells).associate { (h, v) ->
                h to v.trim().removeSurrounding("\"").ifEmpty { null }
            }
            runCatching { m03_from_Map(map) }.getOrNull()
        }

        if (items.isNotEmpty()) items.forEach { appDatabase.dao_M03CouleurProduitInfos().upsert(it) }
    }

    suspend fun import_M03_FireBase_To_Room(
        refDataBase: DatabaseReference,
    ) = withContext(Dispatchers.IO) {
        val snapshot = suspendFirebaseSnapshot(refDataBase)

        val items = snapshot.children.mapNotNull { child ->
            val raw = child.value
            if (raw !is Map<*, *>) return@mapNotNull null
            @Suppress("UNCHECKED_CAST")
            val map = (raw as Map<String, Any?>).mapValues { it.value?.toString() }
            runCatching { m03_from_Map(map) }.getOrNull()
        }

        if (items.isEmpty()) return@withContext
        items.forEach { appDatabase.dao_M03CouleurProduitInfos().upsert(it) }
    }

    
    private fun m03_from_Map(map: Map<String, String?>): M3CouleurProduitInfos =
        M3CouleurProduitInfos(
            keyID = map["keyID"] ?: M3CouleurProduitInfos.generePushKey(),
            debugInfos = map["debugInfos"] ?: "",
            creationTimestamp = map["creationTimestamp"]?.toLongOrNull()
                ?: System.currentTimeMillis(),
            dernierTimeTampsSynchronisationAvecFireBase = map["dernierTimeTampsSynchronisationAvecFireBase"]?.toLongOrNull()
                ?: System.currentTimeMillis(),
            its_in_echantiallants = map["its_in_echantiallants"]?.equals(
                "true", ignoreCase = true
            ) ?: false,
            its_pour_affiche_au_presenter = map["its_pour_affiche_au_presenter"]?.equals(
                "true", ignoreCase = true
            ) ?: false,
            parentProduit_Classement = map["parentProduit_Classement"]?.toIntOrNull(),
            processPositioningInFactory = map["processPositioningInFactory"]?.let {
                runCatching {
                    M3CouleurProduitInfos.ProcessPositioningInFactory.valueOf(it)
                }.getOrDefault(M3CouleurProduitInfos.ProcessPositioningInFactory.CreeAuGeneralHandler)
            } ?: M3CouleurProduitInfos.ProcessPositioningInFactory.CreeAuGeneralHandler,
            aAffiche = map["aAffiche"]?.let {
                runCatching { M3CouleurProduitInfos.Type.valueOf(it) }
                    .getOrDefault(M3CouleurProduitInfos.Type.Image)
            } ?: M3CouleurProduitInfos.Type.Image,
            dropBox_key = map["dropBox_key"] ?: "Non Dispo",
            nomImageFichieSansEtansion = map["nomImageFichieSansEtansion"] ?: "Non Dispo",
            telephone_Prise_depuit = map["telephone_Prise_depuit"] ?: "",
            count_Don_Depot = map["count_Don_Depot"]?.toIntOrNull() ?: 0,
            a_cammende_depuit_grossist = map["a_cammende_depuit_grossist"]?.toIntOrNull() ?: 0,
            nomCouleurStrSiSonImageDispo = map["nomCouleurStrSiSonImageDispo"] ?: "",
            parentBProduitInfosKeyID = map["parentBProduitInfosKeyID"] ?: "",
            parentBProduitOldID = map["parentBProduitOldID"]?.toLongOrNull() ?: 0L,
            parentId1ProduitInfosDebugName = map["parentId1ProduitInfosDebugName"] ?: "",
            indexCouleurDansAncienProto = map["indexCouleurDansAncienProto"]?.toIntOrNull() ?: 0,
            extensionDisponible = map["extensionDisponible"] ?: "webp",
        )

    suspend fun add_New_M8BonVent(bon: M8BonVent) {
        appDatabase.dao_M8BonVent().insert(bon)
    }

    suspend fun update_M8(bon: M8BonVent) = withContext(Dispatchers.IO) {
        appDatabase.dao_M8BonVent().upsert(bon)
    }

    suspend fun insertAll(bons: List<M8BonVent>) = withContext(Dispatchers.IO) {
        bons.forEach { appDatabase.dao_M8BonVent().upsert(it) }
    }

    suspend fun bach_update_FireBase_M8(
        bons: List<M8BonVent>,
        refDataBase: DatabaseReference,
    ) = withContext(Dispatchers.IO) {
        bons.forEachIndexed { _, bon ->
            runCatching {
                suspendCancellableCoroutine { cont ->
                    refDataBase.child(bon.keyID).setValue(bon.to_Map())
                        .addOnSuccessListener { cont.resume(Unit) }
                        .addOnFailureListener { cont.resumeWithException(it) }
                }
            }.onFailure { return@withContext }
        }
    }

    suspend fun get_Firebase_M8_Counts(
        refDataBase: DatabaseReference
    ): Pair<Int, Int> =
        withContext(Dispatchers.IO) {
            val creditNames = M8BonVent.EtateActuellementEst.entries
                .filter { it.credit_type }.map { it.name }.toSet()

            val snapshot = suspendFirebaseSnapshot(refDataBase)
            val total = snapshot.childrenCount.toInt()
            val creditCount = snapshot.children.count { child ->
                val raw = child.value
                if (raw !is Map<*, *>) return@count false
                @Suppress("UNCHECKED_CAST")
                val etat = (raw as Map<String, Any?>)["etateActuellementEst"]?.toString()
                etat != null && etat in creditNames
            }
            Pair(total, creditCount)
        }

    suspend fun get_Firebase_M8_Count(refDataBase: DatabaseReference): Int =
        withContext(Dispatchers.IO) {
            suspendFirebaseSnapshot(refDataBase).childrenCount.toInt()
        }

    suspend fun export_M8_Room_To_Csv(csv: File) = withContext(Dispatchers.IO) {
        val datas = appDatabase.dao_M8BonVent().getAll()
        if (datas.isEmpty()) return@withContext

        csv.parentFile?.mkdirs()

        val headers = datas.first().to_Map().keys.toList()
        val existingRows: LinkedHashMap<String, List<String>> = linkedMapOf()

        if (csv.exists()) {
            val lines = csv.readLines()
            if (lines.size > 1) {
                val fileHeaders = lines[0].splitCsvLine()
                val keyIdx = fileHeaders.indexOf("keyID")
                lines.drop(1).forEach { line ->
                    val cells = line.splitCsvLine()
                    val id = cells.getOrNull(keyIdx) ?: ""
                    if (id.isNotEmpty()) existingRows[id] = cells
                }
            }
        }

        datas.forEach { bon ->
            existingRows[bon.keyID] = bon.to_Map().values.map { (it?.toString() ?: "").escapeCsv() }
        }

        FileWriter(csv, false).use { w ->
            w.write(headers.joinToString(",") + "\n")
            existingRows.values.forEach { cells ->
                w.write(cells.joinToString(",") { it.escapeCsv() } + "\n")
            }
        }
    }

    suspend fun set_scv_m8_au_fireBase(
        csvFile: File,
        refDataBase: DatabaseReference,
    ) = withContext(Dispatchers.IO) {
        if (!csvFile.exists() || csvFile.length() == 0L) return@withContext
        val lines = csvFile.readLines().filter { it.isNotBlank() }
        if (lines.size < 2) return@withContext

        val headers = lines[0].splitCsvLine()
        val keyIdx = headers.indexOf("keyID")
        if (keyIdx == -1) return@withContext

        val bons = lines.drop(1).mapNotNull { line ->
            val cells = line.splitCsvLine()
            val keyID = cells.getOrNull(keyIdx)?.trim()?.removeSurrounding("\"")
            if (keyID.isNullOrBlank()) return@mapNotNull null
            val map = headers.zip(cells).associate { (h, v) ->
                h to v.trim().removeSurrounding("\"").ifEmpty { null }
            }
            runCatching { M8BonVent.Companion.to_Map(map) }.getOrNull()
        }

        if (bons.isEmpty()) return@withContext
        bach_update_FireBase_M8(bons, refDataBase)
    }

    suspend fun import_M8_FireBase_To_Csv(
        refDataBase: DatabaseReference,
        csvFile: File,
        importOnlyCredits: Boolean = false,
    ) = withContext(Dispatchers.IO) {
        val snapshot = suspendFirebaseSnapshot(refDataBase)

        var bons = snapshot.children.mapNotNull { child ->
            val raw = child.value
            if (raw !is Map<*, *>) return@mapNotNull null
            @Suppress("UNCHECKED_CAST")
            val map = (raw as Map<String, Any?>).mapValues { it.value?.toString() }
            runCatching { M8BonVent.Companion.to_Map(map) }.getOrNull()
        }


        if (importOnlyCredits) {
            bons = bons.filter { it.etateActuellementEst.credit_type }
        }

        if (bons.isEmpty()) return@withContext

        csvFile.parentFile?.mkdirs()

        val headers = bons.first().to_Map().keys.toList()
        val existingRows: LinkedHashMap<String, List<String>> = linkedMapOf()

        if (csvFile.exists()) {
            val lines = csvFile.readLines()
            if (lines.size > 1) {
                val fileHeaders = lines[0].splitCsvLine()
                val keyIdx = fileHeaders.indexOf("keyID")
                lines.drop(1).forEach { line ->
                    val cells = line.splitCsvLine()
                    val id = cells.getOrNull(keyIdx) ?: ""
                    if (id.isNotEmpty()) existingRows[id] = cells
                }
            }
        }

        bons.forEach { bon ->
            existingRows[bon.keyID] = bon.to_Map().values.map { (it?.toString() ?: "").escapeCsv() }
        }

        FileWriter(csvFile, false).use { w ->
            w.write(headers.joinToString(",") + "\n")
            existingRows.values.forEach { cells ->
                w.write(cells.joinToString(",") { it.escapeCsv() } + "\n")
            }
        }
    }

    suspend fun import_M8Csv_To_Room(
        csvFile: File,
        importOnlyCredits: Boolean = false,
    ) = withContext(Dispatchers.IO) {
        if (!csvFile.exists() || csvFile.length() == 0L) return@withContext
        val lines = csvFile.readLines().filter { it.isNotBlank() }
        if (lines.size < 2) return@withContext

        val headers = lines[0].splitCsvLine()
        var bons = lines.drop(1).mapNotNull { line ->
            val cells = line.splitCsvLine()
            val map = headers.zip(cells).associate { (h, v) ->
                h to v.trim().removeSurrounding("\"").ifEmpty { null }
            }
            runCatching { M8BonVent.Companion.to_Map(map) }.getOrNull()
        }

        if (importOnlyCredits) {
            bons = bons.filter { it.etateActuellementEst.credit_type }
        }

        if (bons.isNotEmpty()) {
            bons.forEach { bon ->
                appDatabase.dao_M8BonVent().upsert(bon)
            }
        }
    }

    suspend fun import_M8_FireBase_To_Room(
        refDataBase: DatabaseReference,
        importOnlyCredits: Boolean = false,
    ) = withContext(Dispatchers.IO) {
        val snapshot = suspendFirebaseSnapshot(refDataBase)

        var bons = snapshot.children.mapNotNull { child ->
            val raw = child.value
            if (raw !is Map<*, *>) return@mapNotNull null
            @Suppress("UNCHECKED_CAST")
            val map = (raw as Map<String, Any?>).mapValues { it.value?.toString() }
            runCatching { M8BonVent.Companion.to_Map(map) }.getOrNull()
        }

        if (importOnlyCredits) {
            bons = bons.filter { it.etateActuellementEst.credit_type }
        }

        if (bons.isEmpty()) return@withContext
        bons.forEach { appDatabase.dao_M8BonVent().upsert(it) }
    }

    suspend fun delete_All_M8() {
        appDatabase.dao_M8BonVent().deleteAll()
    }

    suspend fun suspendFirebaseSnapshot(ref: DatabaseReference): DataSnapshot =
        suspendCancellableCoroutine { cont ->
            val listener = object : ValueEventListener {
                override fun onDataChange(snap: DataSnapshot) {
                    if (cont.isActive) cont.resume(snap)
                }
                override fun onCancelled(error: DatabaseError) {
                    if (cont.isActive) cont.resumeWithException(error.toException())
                }
            }
            ref.addListenerForSingleValueEvent(listener)
            cont.invokeOnCancellation { ref.removeEventListener(listener) }
        }

    fun update_List_M1Produit_BathFireBase(
        datas: List<M01Produit>,
        onSuccess: () -> Unit = {}
    ) {
        composScope.launch {
            if (datas.isNotEmpty()) {
                datas.forEach { appDatabase.dao_M1Produit().update(it) }
                val updates: Map<String, Any> = datas.associate { it.keyID to it.to_Map() }
                M01Produit.Companion.ref.updateChildren(updates).await()
            }
            withContext(Dispatchers.Main) { onSuccess() }
        }
    }

    fun update_List_M3CouleurProduitInfos_BathFireBase(
        datas: List<M3CouleurProduitInfos>,
        onSuccess: () -> Unit = {}
    ) {
        composScope.launch {
            if (datas.isNotEmpty()) {
                datas.forEach { appDatabase.dao_M03CouleurProduitInfos().update(it) }
                val updates: Map<String, Any> = datas.associate { it.keyID to it.to_Map() }
                M3CouleurProduitInfos.Companion.ref.updateChildren(updates).await()
            }
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Firebase : ${datas.size} couleurs synchronisées ✓",
                    Toast.LENGTH_SHORT
                ).show()
                onSuccess()
            }
        }
    }

    fun update_M1Produit(data: M01Produit) {
        composScope.launch {
            appDatabase.dao_M1Produit().update(data)
            val updates = mutableMapOf<String, Any>(data.keyID to data.to_Map())
            M01Produit.Companion.ref.updateChildren(updates).await()
        }
    }

    fun delete_M1Produit(data: M01Produit) {
        composScope.launch {
            appDatabase.dao_M1Produit().delete(data)
            M01Produit.Companion.ref.child(data.keyID).removeValue().await()
        }
    }

    fun delete_M3CouleurProduitInfos(data: M3CouleurProduitInfos) {
        composScope.launch {
            appDatabase.dao_M03CouleurProduitInfos().delete(data)
            M3CouleurProduitInfos.Companion.ref.child(data.keyID).removeValue().await()
        }
    }

    fun insertFireBase_list_Main_Values_M3CouleurProduitInfos(
        keys: Map<String, Ref_list_Filtred_Keys_M3Couleur_Main_Values>,
        onSuccess: () -> Unit = {}
    ) {
        val ref_listKeys = M3CouleurProduitInfos.Companion.ref_listKeys_M3CouleurProduitInfos
        composScope.launch {
            if (keys.isNotEmpty()) {
                val updates: Map<String, Any> = keys.mapValues { (_, v) ->
                    mapOf(
                        "nom"                    to v.nom,
                        "classment"              to v.classment,
                        "activated"              to v.activated,
                        "parentProduitKeyID"     to v.parentProduitKeyID,
                        "parentProduitDebugName" to v.parentProduitDebugName,
                        "parentProduitClassement" to v.parentProduitClassement
                    )
                }
                ref_listKeys.removeValue().await()
                ref_listKeys.updateChildren(updates).await()
            }
            withContext(Dispatchers.Main) { onSuccess() }
        }
    }

    fun deleteFireBase_listKeys_M3CouleurProduitInfos(
        onSuccess: () -> Unit = {}
    ) {
        val ref_listKeys = M3CouleurProduitInfos.Companion.ref_listKeys_M3CouleurProduitInfos
        composScope.launch {
            ref_listKeys.removeValue().await()
            withContext(Dispatchers.Main) { onSuccess() }
        }
    }

    fun deleteInsertFireBase_listKeys_M3CouleurProduitInfos(
        keys: Map<String, Boolean>,
        onSuccess: () -> Unit = {}
    ) {
        val ref_listKeys = M3CouleurProduitInfos.Companion.ref_listKeys_M3CouleurProduitInfos
        composScope.launch {
            ref_listKeys.removeValue().await()
            if (keys.isNotEmpty()) {
                val updates: Map<String, Any> = keys.mapValues { it.value }
                ref_listKeys.updateChildren(updates).await()
            }
            withContext(Dispatchers.Main) { onSuccess() }
        }
    }

    fun update_M3CouleurProduitInfos(
        data: M3CouleurProduitInfos,
        onSuccess: () -> Unit = {}
    ) {
        if (data.keyID.isBlank()) {
            composScope.launch {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Erreur : données non disponibles, mise à jour annulée",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            return
        }
        composScope.launch {
            appDatabase.dao_M03CouleurProduitInfos().update(data)
            val updates = mutableMapOf<String, Any>(data.keyID to data.to_Map())
            M3CouleurProduitInfos.Companion.ref.updateChildren(updates).await()
            withContext(Dispatchers.Main) { onSuccess() }
        }
    }

    // -------------------------------------------------------------------------
    // M8BonVent
    // -------------------------------------------------------------------------

    fun update_M8BonVent(
        data: M8BonVent,
        onSuccess: () -> Unit = {}
    ) {
        if (data.keyID.isBlank()) {
            composScope.launch {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Erreur : données non disponibles, mise à jour annulée",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            return
        }
        composScope.launch {
            try {
                appDatabase.dao_M8BonVent().update(data)
                val updates = mutableMapOf<String, Any>(data.keyID to data.to_Map())
                M8BonVent.Companion.ref.updateChildren(updates).await()
                withContext(Dispatchers.Main) { onSuccess() }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Erreur lors de la mise à jour : ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // M16CategorieProduit
    // -------------------------------------------------------------------------

    fun insert_M16CategorieProduit(data: M16CategorieProduit) {
        composScope.launch {
            appDatabase.dao_16CategorieProduit().insert(data)
            val updates = mutableMapOf<String, Any>(data.keyID to data.toFirebaseMap())
            M16CategorieProduit.Companion.ref.updateChildren(updates).await()
        }
    }

    fun update_M16CategorieProduit(data: M16CategorieProduit) {
        if (data.keyID.isBlank()) {
            composScope.launch {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Erreur : données non disponibles, mise à jour annulée",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            return
        }
        composScope.launch {
            appDatabase.dao_16CategorieProduit().update(data)
            val updates = mutableMapOf<String, Any>(data.keyID to data.toFirebaseMap())
            M16CategorieProduit.Companion.ref.updateChildren(updates).await()
        }
    }

    // -------------------------------------------------------------------------
    // M13TarificationInfos
    // -------------------------------------------------------------------------

    fun update_M13TarificationInfos(data: M13TarificationInfos) {
        if (data.keyID.isBlank()) {
            composScope.launch {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Erreur : données non disponibles, mise à jour annulée",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            return
        }
        composScope.launch {
            appDatabase.dao_M13TarificationInfos().update(data)
            val updates = mutableMapOf<String, Any>(data.keyID to data.toFirebaseMap())
            M13TarificationInfos.Companion.ref.updateChildren(updates).await()
        }
    }

    // -------------------------------------------------------------------------
    // M10OperationVentCouleur
    // -------------------------------------------------------------------------

    /** Deletes a single operation from Room and Firebase. */
    fun delete_M10OperationVentCouleur(
        data: M10OperationVentCouleur,
        onSuccess: () -> Unit = {}
    ) = deleteList_M10OperationVentCouleur(listOf(data), onSuccess)

    fun deleteList_M10OperationVentCouleur(
        datas: List<M10OperationVentCouleur>,
        onSuccess: () -> Unit = {}
    ) {
        if (datas.isEmpty()) {
            onSuccess()
            return
        }
        composScope.launch {
            try {
                datas.forEach { appDatabase.dao_M10OperationVentCouleur().delete(it) }
                val updates: Map<String, Any?> = datas.associate { it.keyID to null }
                M10OperationVentCouleur.Companion.ref.updateChildren(updates).await()
                withContext(Dispatchers.Main) { onSuccess() }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Erreur lors de la suppression : ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun upsert_M10OperationVentCouleur(
        operation: M10OperationVentCouleur,
        selectedTariff: M13TarificationInfos
    ): kotlinx.coroutines.Job {
        return composScope.launch {
            appDatabase.dao_M10OperationVentCouleur().upsert(operation)
            val tariffWithDefaults = selectedTariff.copy(
                defaultNonSaved_Entre = false,
                dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
            )
            appDatabase.dao_M13TarificationInfos().update(tariffWithDefaults)

            // Firebase updates in background
            composScope.launch {
                try {
                    val opUpdates = mutableMapOf<String, Any>(operation.keyID to operation)
                    M10OperationVentCouleur.Companion.ref.updateChildren(opUpdates).await()

                    val tariffUpdates = mutableMapOf<String, Any>(
                        tariffWithDefaults.keyID to tariffWithDefaults.toFirebaseMap()
                    )
                    M13TarificationInfos.Companion.ref.updateChildren(tariffUpdates).await()
                } catch (e: Exception) {
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // M9AppCompt
    // -------------------------------------------------------------------------

    fun insert_M9AppCompt(
        data: M09AppCompt,
        onSuccess: () -> Unit = {}
    ) {
        if (data.keyID.isBlank()) {
            composScope.launch {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Erreur : données du compte invalides", Toast.LENGTH_SHORT).show()
                }
            }
            return
        }
        composScope.launch {
            try {
                appDatabase.dao_M9AppCompt().insert(data)
                val updates = mutableMapOf<String, Any>(data.keyID to data.to_Map())
                M09AppCompt.Companion.ref.updateChildren(updates).await()
                withContext(Dispatchers.Main) { onSuccess() }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Erreur lors de l'insertion : ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun update_M9AppCompt(
        data: M09AppCompt,
        onSuccess: () -> Unit = {}
    ) {
        if (data.keyID.isBlank()) {
            composScope.launch {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Erreur : données non disponibles, mise à jour annulée", Toast.LENGTH_SHORT).show()
                }
            }
            return
        }
        composScope.launch {
            try {
                appDatabase.dao_M9AppCompt().update(data)
                val updates = mutableMapOf<String, Any>(data.keyID to data.to_Map())
                M09AppCompt.Companion.ref.updateChildren(updates).await()
                withContext(Dispatchers.Main) { onSuccess() }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Erreur lors de la mise à jour : ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun setIN_CurrentApp_activeFocuce_TariffPrixDifineur_M1ProduitKeyID(
        produit: M01Produit,
        currentAppCompt: M09AppCompt
    ) {
        val updatedAppCompt = currentAppCompt.copy(
            activeFocuce_TariffPrixDifineur_M1ProduitKeyID = produit.keyID,
            activeFocuceTariffPrixDifineurM1ProduitDebugInfos = produit.getDebugInfos(),
        )
        composScope.launch {
            appDatabase.dao_M9AppCompt().upsert(updatedAppCompt)
            val updates = mutableMapOf<String, Any>(updatedAppCompt.keyID to updatedAppCompt)
            M09AppCompt.Companion.ref.updateChildren(updates).await()
        }
    }
    // -------------------------------------------------------------------------
    // Bulk tariff update for all operations of a product
    // -------------------------------------------------------------------------

    fun updateTariffForProductOperations(
        produitKeyID: String,
        newTariff: M13TarificationInfos,
    ) {
        composScope.launch {
            val operations = appDatabase.dao_M10OperationVentCouleur()
                .getAll()
                .filter { it.parent_M1Produit_KeyId == produitKeyID }

            val updated = operations.map { op ->
                op.copy(
                    parentM13TarificationKeyID = newTariff.keyID,
                    parentM13TarificationDebugInfos = newTariff.getDebugInfos(),
                )
            }

            updated.forEach { op ->
                appDatabase.dao_M10OperationVentCouleur().update(op)
                val updates = mutableMapOf<String, Any>(op.keyID to op)
                M10OperationVentCouleur.Companion.ref.updateChildren(updates).await()
            }
        }
    }

    // -------------------------------------------------------------------------
    // M2Client
    // -------------------------------------------------------------------------

    fun update_M2(new: M2Client) {
        if (new.keyID.isBlank()) return
        composScope.launch {
            appDatabase.dao_M2Client().upsert(new)
            val updates = mutableMapOf<String, Any>(new.keyID to new.toFirebaseMap())
            M2Client.Companion.ref.updateChildren(updates).await()
        }
    }

    // =========================================================================
    // M10OperationVentCouleur Sync Operations
    // =========================================================================

    suspend fun delete_All_M10() {
        appDatabase.dao_M10OperationVentCouleur().deleteAll()
    }

    suspend fun insertAll_M10(items: List<M10OperationVentCouleur>) = withContext(Dispatchers.IO) {
        items.forEach { appDatabase.dao_M10OperationVentCouleur().upsert(it) }
    }

    suspend fun get_Firebase_M10_Counts(refDataBase: DatabaseReference): Pair<Int, Int> =
        withContext(Dispatchers.IO) {
            val snapshot = suspendFirebaseSnapshot(refDataBase)
            val total = snapshot.childrenCount.toInt()
            Pair(total, 0)
        }

    suspend fun export_M10_Room_To_Csv(csv: File) = withContext(Dispatchers.IO) {
        val datas = appDatabase.dao_M10OperationVentCouleur().getAll()
        if (datas.isEmpty()) return@withContext

        csv.parentFile?.mkdirs()

        val headers = datas.first().to_Map().keys.toList()
        val existingRows: LinkedHashMap<String, List<String>> = linkedMapOf()

        if (csv.exists()) {
            val lines = csv.readLines()
            if (lines.size > 1) {
                val fileHeaders = lines[0].splitCsvLine()
                val keyIdx = fileHeaders.indexOf("keyID")
                lines.drop(1).forEach { line ->
                    val cells = line.splitCsvLine()
                    val id = cells.getOrNull(keyIdx) ?: ""
                    if (id.isNotEmpty()) existingRows[id] = cells
                }
            }
        }

        datas.forEach { item ->
            existingRows[item.keyID] = item.to_Map().values.map { (it?.toString() ?: "").escapeCsv() }
        }

        FileWriter(csv, false).use { w ->
            w.write(headers.joinToString(",") + "\n")
            existingRows.values.forEach { cells ->
                w.write(cells.joinToString(",") { it.escapeCsv() } + "\n")
            }
        }
    }

    suspend fun set_scv_M10_au_fireBase(
        csvFile: File,
        refDataBase: DatabaseReference,
    ) = withContext(Dispatchers.IO) {
        if (!csvFile.exists() || csvFile.length() == 0L) return@withContext
        val lines = csvFile.readLines().filter { it.isNotBlank() }
        if (lines.size < 2) return@withContext

        val headers = lines[0].splitCsvLine()
        val keyIdx = headers.indexOf("keyID")
        if (keyIdx == -1) return@withContext

        val items = lines.drop(1).mapNotNull { line ->
            val cells = line.splitCsvLine()
            val keyID = cells.getOrNull(keyIdx)?.trim()?.removeSurrounding("\"")
            if (keyID.isNullOrBlank()) return@mapNotNull null
            val map = headers.zip(cells).associate { (h, v) ->
                h to v.trim().removeSurrounding("\"").ifEmpty { null }
            }
            runCatching { m10_from_Map(map) }.getOrNull()
        }

        if (items.isEmpty()) return@withContext
        val updates: Map<String, Any> = items.associate { it.keyID to it.to_Map() }
        refDataBase.updateChildren(updates).await()
    }

    suspend fun import_M10_FireBase_To_Csv(
        refDataBase: DatabaseReference,
        csvFile: File,
    ) = withContext(Dispatchers.IO) {
        val snapshot = suspendFirebaseSnapshot(refDataBase)

        val items = snapshot.children.mapNotNull { child ->
            val raw = child.value
            if (raw !is Map<*, *>) return@mapNotNull null
            @Suppress("UNCHECKED_CAST")
            val map = (raw as Map<String, Any?>).mapValues { it.value?.toString() }
            val item = runCatching { m10_from_Map(map) }.getOrNull()
            item
        }

        if (items.isEmpty()) return@withContext

        csvFile.parentFile?.mkdirs()

        val headers = items.first().to_Map().keys.toList()
        val existingRows: LinkedHashMap<String, List<String>> = linkedMapOf()

        if (csvFile.exists()) {
            val lines = csvFile.readLines()
            if (lines.size > 1) {
                val fileHeaders = lines[0].splitCsvLine()
                val keyIdx = fileHeaders.indexOf("keyID")
                lines.drop(1).forEach { line ->
                    val cells = line.splitCsvLine()
                    val id = cells.getOrNull(keyIdx) ?: ""
                    if (id.isNotEmpty()) existingRows[id] = cells
                }
            }
        }

        items.forEach { item ->
            existingRows[item.keyID] = item.to_Map().values.map { (it?.toString() ?: "").escapeCsv() }
        }

        FileWriter(csvFile, false).use { w ->
            w.write(headers.joinToString(",") + "\n")
            existingRows.values.forEach { cells ->
                w.write(cells.joinToString(",") { it.escapeCsv() } + "\n")
            }
        }
    }

    suspend fun import_M10Csv_To_Room(csvFile: File) = withContext(Dispatchers.IO) {
        if (!csvFile.exists() || csvFile.length() == 0L) return@withContext
        val lines = csvFile.readLines().filter { it.isNotBlank() }
        if (lines.size < 2) return@withContext

        val headers = lines[0].splitCsvLine()
        val keyIdx = headers.indexOf("keyID")
        if (keyIdx == -1) return@withContext

        val items = lines.drop(1).mapNotNull { line ->
            val cells = line.splitCsvLine()
            val keyID = cells.getOrNull(keyIdx)?.trim()?.removeSurrounding("\"")
            if (keyID.isNullOrBlank()) return@mapNotNull null
            val map = headers.zip(cells).associate { (h, v) ->
                h to v.trim().removeSurrounding("\"").ifEmpty { null }
            }
            runCatching { m10_from_Map(map) }.getOrNull()
        }

        if (items.isNotEmpty()) {
            items.forEach { appDatabase.dao_M10OperationVentCouleur().upsert(it) }
        }
    }

    suspend fun import_M10_FireBase_To_Room(refDataBase: DatabaseReference) =
        withContext(Dispatchers.IO) {
            val snapshot = suspendFirebaseSnapshot(refDataBase)
            val items = snapshot.children.mapNotNull { child ->
                val raw = child.value
                if (raw !is Map<*, *>) return@mapNotNull null
                @Suppress("UNCHECKED_CAST")
                val map = (raw as Map<String, Any?>).mapValues { it.value?.toString() }
                val item = runCatching { m10_from_Map(map) }.getOrNull()
                item
            }
            items.forEach { appDatabase.dao_M10OperationVentCouleur().upsert(it) }
        }

    private fun M10OperationVentCouleur.to_Map(): Map<String, Any?> {
        return mapOf(
            "keyID" to keyID,
            "creationTimestamps" to creationTimestamps,
            "dernierTimeTampsSynchronisationAvecFireBase" to dernierTimeTampsSynchronisationAvecFireBase,
            "its_created_in_working_for_wholesaler" to its_created_in_working_for_wholesaler,
            "commetaire" to commetaire,
            "prix_de_Vent_entre_directement_NewProto" to prix_de_Vent_entre_directement_NewProto,
            "its_Linked_To_Autre_Vent_Si_NonDispo" to its_Linked_To_Autre_Vent_Si_NonDispo,
            "linked_To_M10OperationVent_KeyID" to linked_To_M10OperationVent_KeyID,
            "linked_To_M10OperationVent_DebugInfos" to linked_To_M10OperationVent_DebugInfos,
            "siNonDispoParentM10Vent_it_parent_M3CouleurInfos_KeyId" to siNonDispoParentM10Vent_it_parent_M3CouleurInfos_KeyId,
            "siNonDispoParentM10Vent_it_parent_M1Produit_Nom" to siNonDispoParentM10Vent_it_parent_M1Produit_Nom,
            "parent_M9AppCompt_KeyID" to parent_M9AppCompt_KeyID,
            "parent_M9AppCompt_DebugInfos" to parent_M9AppCompt_DebugInfos,
            "parent_M14VentPeriod_KeyId" to parent_M14VentPeriod_KeyId,
            "parent_M14VentPeriod_DebugInfos" to parent_M14VentPeriod_DebugInfos,
            "parentEPeriodVentStartDate" to parentEPeriodVentStartDate,
            "parent_M8BonVent_KeyId" to parent_M8BonVent_KeyId,
            "parent_M8BonVent_DebugInfos" to parent_M8BonVent_DebugInfos,
            "parent_M1Produit_KeyId" to parent_M1Produit_KeyId,
            "parent_M1Produit_DebugInfos" to parent_M1Produit_DebugInfos,
            "parent_M1Produit_Nom" to parent_M1Produit_Nom,
            "parentProduitInfosOldId" to parentProduitInfosOldId,
            "parent_M3CouleurProduit_KeyID" to parent_M3CouleurProduit_KeyID,
            "parent_M3CouleurProduit_DebugInfos" to parent_M3CouleurProduit_DebugInfos,
            "parentM13TarificationKeyID" to parentM13TarificationKeyID,
            "parentM13TarificationDebugInfos" to parentM13TarificationDebugInfos,
            "etateActuellementEst" to etateActuellementEst.name,
            "provisoireMonPrix" to provisoireMonPrix,
            "etateDelivery" to etateDelivery.name,
            "lence_pour_check" to lence_pour_check,
            "premier_Check_Donne" to premier_Check_Donne,
            "last_update_premier_Check_Donne_TimeTamps" to last_update_premier_Check_Donne_TimeTamps,
            "non_places_au_depot" to non_places_au_depot,
            "pas_Dispo_Pour_Aujourduit" to pas_Dispo_Pour_Aujourduit,
            "typeTarificationEnumT2" to typeTarificationEnumT2.name,
            "parentClientInfosKeyID" to parentClientInfosKeyID,
            "parentClientName" to parentClientName,
            "type" to type.name,
            "achatParentBsonIDOld" to achatParentBsonIDOld,
            "quantite_Boit_Par_Carton" to quantite_Boit_Par_Carton,
            "quantity" to quantity,
            "setIN_Vent_Its_Quantity_Represent" to setIN_Vent_Its_Quantity_Represent.name,
            "affiche_Unite_Au_Printing" to affiche_Unite_Au_Printing,
            "parent_M2Client_KeyID" to parent_M2Client_KeyID
        )
    }

    private fun m10_from_Map(map: Map<String, String?>): M10OperationVentCouleur {
        return M10OperationVentCouleur(
            keyID = map["keyID"] ?: "",
            creationTimestamps = map["creationTimestamps"]?.toLongOrNull() ?: System.currentTimeMillis(),
            dernierTimeTampsSynchronisationAvecFireBase = map["dernierTimeTampsSynchronisationAvecFireBase"]?.toLongOrNull() ?: System.currentTimeMillis(),
            its_created_in_working_for_wholesaler = map["its_created_in_working_for_wholesaler"]?.toBoolean() ?: false,
            commetaire = map["commetaire"] ?: "",
            prix_de_Vent_entre_directement_NewProto = map["prix_de_Vent_entre_directement_NewProto"]?.toDoubleOrNull() ?: 0.0,
            its_Linked_To_Autre_Vent_Si_NonDispo = map["its_Linked_To_Autre_Vent_Si_NonDispo"]?.toBoolean() ?: false,
            linked_To_M10OperationVent_KeyID = map["linked_To_M10OperationVent_KeyID"] ?: "",
            linked_To_M10OperationVent_DebugInfos = map["linked_To_M10OperationVent_DebugInfos"] ?: "",
            siNonDispoParentM10Vent_it_parent_M3CouleurInfos_KeyId = map["siNonDispoParentM10Vent_it_parent_M3CouleurInfos_KeyId"] ?: "",
            siNonDispoParentM10Vent_it_parent_M1Produit_Nom = map["siNonDispoParentM10Vent_it_parent_M1Produit_Nom"] ?: "",
            parent_M9AppCompt_KeyID = map["parent_M9AppCompt_KeyID"] ?: "null",
            parent_M9AppCompt_DebugInfos = map["parent_M9AppCompt_DebugInfos"] ?: "null",
            parent_M14VentPeriod_KeyId = map["parent_M14VentPeriod_KeyId"] ?: "null",
            parent_M14VentPeriod_DebugInfos = map["parent_M14VentPeriod_DebugInfos"] ?: "null",
            parentEPeriodVentStartDate = map["parentEPeriodVentStartDate"]?.toLongOrNull() ?: 0L,
            parent_M8BonVent_KeyId = map["parent_M8BonVent_KeyId"] ?: "null",
            parent_M8BonVent_DebugInfos = map["parent_M8BonVent_DebugInfos"] ?: "null",
            parent_M1Produit_KeyId = map["parent_M1Produit_KeyId"] ?: "null",
            parent_M1Produit_DebugInfos = map["parent_M1Produit_DebugInfos"] ?: "null",
            parent_M1Produit_Nom = map["parent_M1Produit_Nom"] ?: "",
            parentProduitInfosOldId = map["parentProduitInfosOldId"]?.toLongOrNull() ?: 0L,
            parent_M3CouleurProduit_KeyID = map["parent_M3CouleurProduit_KeyID"] ?: "null",
            parent_M3CouleurProduit_DebugInfos = map["parent_M3CouleurProduit_DebugInfos"] ?: "null",
            parentM13TarificationKeyID = map["parentM13TarificationKeyID"] ?: "null",
            parentM13TarificationDebugInfos = map["parentM13TarificationDebugInfos"] ?: "null",
            etateActuellementEst = map["etateActuellementEst"]?.let { runCatching { M10OperationVentCouleur.EtateActuellementEst.valueOf(it) }.getOrNull() } ?: M10OperationVentCouleur.EtateActuellementEst.CreeSlote,
            provisoireMonPrix = map["provisoireMonPrix"]?.toDoubleOrNull() ?: 0.0,
            etateDelivery = map["etateDelivery"]?.let { runCatching { M10OperationVentCouleur.EtateDelivery.valueOf(it) }.getOrNull() } ?: M10OperationVentCouleur.EtateDelivery.Trouve,
            lence_pour_check = map["lence_pour_check"]?.toBoolean() ?: false,
            premier_Check_Donne = map["premier_Check_Donne"]?.toBoolean() ?: false,
            last_update_premier_Check_Donne_TimeTamps = map["last_update_premier_Check_Donne_TimeTamps"]?.toLongOrNull() ?: 0L,
            non_places_au_depot = map["non_places_au_depot"]?.toBoolean() ?: false,
            pas_Dispo_Pour_Aujourduit = map["pas_Dispo_Pour_Aujourduit"]?.toBoolean() ?: false,
            typeTarificationEnumT2 = map["typeTarificationEnumT2"]?.let { runCatching { M13TarificationInfos.TypeChoisi.valueOf(it) }.getOrNull() } ?: M13TarificationInfos.TypeChoisi.Prix_Detaille,
            parentClientInfosKeyID = map["parentClientInfosKeyID"] ?: "",
            parentClientName = map["parentClientName"] ?: "",
            type = map["type"]?.let { runCatching { M10OperationVentCouleur.Type.valueOf(it) }.getOrNull() } ?: M10OperationVentCouleur.Type.CommandeDeLui,
            achatParentBsonIDOld = map["achatParentBsonIDOld"] ?: "",
            quantite_Boit_Par_Carton = map["quantite_Boit_Par_Carton"]?.toIntOrNull() ?: 10,
            quantity = map["quantity"]?.toIntOrNull() ?: 0,
            setIN_Vent_Its_Quantity_Represent = map["setIN_Vent_Its_Quantity_Represent"]?.let { runCatching { M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.valueOf(it) }.getOrNull() } ?: M10OperationVentCouleur.SetIN_Vent_Its_Quantity_Represent.quantity_Par_Boit,
            affiche_Unite_Au_Printing = map["affiche_Unite_Au_Printing"]?.toBoolean() ?: true,
            parent_M2Client_KeyID = map["parent_M2Client_KeyID"] ?: "null"
        )
    }

    // =========================================================================
    // M13TarificationInfos Sync Operations
    // =========================================================================

    suspend fun delete_All_M13() {
        appDatabase.dao_M13TarificationInfos().deleteAll()
    }

    suspend fun insertAll_M13(items: List<M13TarificationInfos>) = withContext(Dispatchers.IO) {
        items.forEach { appDatabase.dao_M13TarificationInfos().upsert(it) }
    }

    suspend fun get_Firebase_M13_Counts(refDataBase: DatabaseReference): Pair<Int, Int> =
        withContext(Dispatchers.IO) {
            val snapshot = suspendFirebaseSnapshot(refDataBase)
            val total = snapshot.childrenCount.toInt()
            Pair(total, 0)
        }

    suspend fun export_M13_Room_To_Csv(csv: File) = withContext(Dispatchers.IO) {
        val datas = appDatabase.dao_M13TarificationInfos().getAll()
        if (datas.isEmpty()) return@withContext

        csv.parentFile?.mkdirs()

        val headers = datas.first().toFirebaseMap().keys.toList()
        val existingRows: LinkedHashMap<String, List<String>> = linkedMapOf()

        if (csv.exists()) {
            val lines = csv.readLines()
            if (lines.size > 1) {
                val fileHeaders = lines[0].splitCsvLine()
                val keyIdx = fileHeaders.indexOf("keyID")
                lines.drop(1).forEach { line ->
                    val cells = line.splitCsvLine()
                    val id = cells.getOrNull(keyIdx) ?: ""
                    if (id.isNotEmpty()) existingRows[id] = cells
                }
            }
        }

        datas.forEach { item ->
            existingRows[item.keyID] = item.toFirebaseMap().values.map { (it?.toString() ?: "").escapeCsv() }
        }

        FileWriter(csv, false).use { w ->
            w.write(headers.joinToString(",") + "\n")
            existingRows.values.forEach { cells ->
                w.write(cells.joinToString(",") { it.escapeCsv() } + "\n")
            }
        }
    }

    suspend fun set_scv_M13_au_fireBase(
        csvFile: File,
        refDataBase: DatabaseReference,
    ) = withContext(Dispatchers.IO) {
        if (!csvFile.exists() || csvFile.length() == 0L) return@withContext
        val lines = csvFile.readLines().filter { it.isNotBlank() }
        if (lines.size < 2) return@withContext

        val headers = lines[0].splitCsvLine()
        val keyIdx = headers.indexOf("keyID")
        if (keyIdx == -1) return@withContext

        val items = lines.drop(1).mapNotNull { line ->
            val cells = line.splitCsvLine()
            val keyID = cells.getOrNull(keyIdx)?.trim()?.removeSurrounding("\"")
            if (keyID.isNullOrBlank()) return@mapNotNull null
            val map = headers.zip(cells).associate { (h, v) ->
                h to v.trim().removeSurrounding("\"").ifEmpty { null }
            }
            runCatching { m13_from_Map(map) }.getOrNull()
        }

        if (items.isEmpty()) return@withContext
        val updates: Map<String, Any> = items.associate { it.keyID to it.toFirebaseMap() }
        refDataBase.updateChildren(updates).await()
    }

    suspend fun import_M13_FireBase_To_Csv(
        refDataBase: DatabaseReference,
        csvFile: File,
    ) = withContext(Dispatchers.IO) {
        val snapshot = suspendFirebaseSnapshot(refDataBase)

        val items = snapshot.children.mapNotNull { child ->
            val raw = child.value
            if (raw !is Map<*, *>) return@mapNotNull null
            @Suppress("UNCHECKED_CAST")
            val map = (raw as Map<String, Any?>).mapValues { it.value?.toString() }
            val item = runCatching { m13_from_Map(map) }.getOrNull()
            item
        }

        if (items.isEmpty()) return@withContext

        csvFile.parentFile?.mkdirs()

        val headers = items.first().toFirebaseMap().keys.toList()
        val existingRows: LinkedHashMap<String, List<String>> = linkedMapOf()

        if (csvFile.exists()) {
            val lines = csvFile.readLines()
            if (lines.size > 1) {
                val fileHeaders = lines[0].splitCsvLine()
                val keyIdx = fileHeaders.indexOf("keyID")
                lines.drop(1).forEach { line ->
                    val cells = line.splitCsvLine()
                    val id = cells.getOrNull(keyIdx) ?: ""
                    if (id.isNotEmpty()) existingRows[id] = cells
                }
            }
        }

        items.forEach { item ->
            existingRows[item.keyID] = item.toFirebaseMap().values.map { (it?.toString() ?: "").escapeCsv() }
        }

        FileWriter(csvFile, false).use { w ->
            w.write(headers.joinToString(",") + "\n")
            existingRows.values.forEach { cells ->
                w.write(cells.joinToString(",") { it.escapeCsv() } + "\n")
            }
        }
    }

    suspend fun import_M13Csv_To_Room(csvFile: File) = withContext(Dispatchers.IO) {
        if (!csvFile.exists() || csvFile.length() == 0L) return@withContext
        val lines = csvFile.readLines().filter { it.isNotBlank() }
        if (lines.size < 2) return@withContext

        val headers = lines[0].splitCsvLine()
        val keyIdx = headers.indexOf("keyID")
        if (keyIdx == -1) return@withContext

        val items = lines.drop(1).mapNotNull { line ->
            val cells = line.splitCsvLine()
            val keyID = cells.getOrNull(keyIdx)?.trim()?.removeSurrounding("\"")
            if (keyID.isNullOrBlank()) return@mapNotNull null
            val map = headers.zip(cells).associate { (h, v) ->
                h to v.trim().removeSurrounding("\"").ifEmpty { null }
            }
            runCatching { m13_from_Map(map) }.getOrNull()
        }

        if (items.isNotEmpty()) {
            items.forEach { appDatabase.dao_M13TarificationInfos().upsert(it) }
        }
    }

    suspend fun import_M13_FireBase_To_Room(refDataBase: DatabaseReference) =
        withContext(Dispatchers.IO) {
            val snapshot = suspendFirebaseSnapshot(refDataBase)
            val items = snapshot.children.mapNotNull { child ->
                val raw = child.value
                if (raw !is Map<*, *>) return@mapNotNull null
                @Suppress("UNCHECKED_CAST")
                val map = (raw as Map<String, Any?>).mapValues { it.value?.toString() }
                val item = runCatching { m13_from_Map(map) }.getOrNull()
                item
            }
            items.forEach { appDatabase.dao_M13TarificationInfos().upsert(it) }
        }

    private fun m13_from_Map(map: Map<String, String?>): M13TarificationInfos {
        return M13TarificationInfos(
            keyID = map["keyID"] ?: "",
            id = map["id"]?.toLongOrNull() ?: 0L,
            creationTimestamps = map["creationTimestamps"]?.toLongOrNull() ?: System.currentTimeMillis(),
            dernierTimeTampsSynchronisationAvecFireBase = map["dernierTimeTampsSynchronisationAvecFireBase"]?.toLongOrNull() ?: System.currentTimeMillis(),
            defaultNonSaved_Entre = map["defaultNonSaved_Entre"]?.toBoolean() ?: true,
            its_From_CalculeParNewBenifice = map["its_From_CalculeParNewBenifice"]?.toBoolean() ?: true,
            laisse_Au_Gerant = map["laisse_Au_Gerant"]?.toBoolean() ?: false,
            typeChoisi = map["typeChoisi"]?.let { runCatching { M13TarificationInfos.TypeChoisi.valueOf(it) }.getOrNull() } ?: M13TarificationInfos.TypeChoisi.Prix_SupperGro_Et_PresentationService,
            prixCurrency = map["prixCurrency"]?.toDoubleOrNull() ?: 0.0,
            profitMargin = map["profitMargin"]?.toDoubleOrNull() ?: 0.0,
            suggestedUpgrade = map["suggestedUpgrade"]?.let { runCatching { M13TarificationInfos.TypeChoisi.valueOf(it) }.getOrNull() },
            parent_M14VentPeriod_KeyId = map["parent_M14VentPeriod_KeyId"] ?: "",
            parent_M14VentPeriod_DebugInfos = map["parent_M14VentPeriod_DebugInfos"] ?: "",
            parent_M1Produit_KeyId = map["parent_M1Produit_KeyId"] ?: "null",
            parent_M1Produit_DebugInfos = map["parent_M1Produit_DebugInfos"] ?: "null",
            parent_M8BonVent_KeyId = map["parent_M8BonVent_KeyId"] ?: "null",
            parent_M8BonVent_DebugInfos = map["parent_M8BonVent_DebugInfos"] ?: "null",
            parent_M2Client_KeyId = map["parent_M2Client_KeyId"] ?: "null",
            parent_M2Client_DebugInfos = map["parent_M2Client_DebugInfos"] ?: "null"
        )
    }

    // =========================================================================
    // M14VentPeriode Sync Operations
    // =========================================================================

    suspend fun delete_All_M14() {
        appDatabase.dao_M14VentPeriode().deleteAll()
    }

    suspend fun insertAll_M14(items: List<M14VentPeriode>) = withContext(Dispatchers.IO) {
        items.forEach { appDatabase.dao_M14VentPeriode().upsert(it) }
    }

    suspend fun get_Firebase_M14_Counts(refDataBase: DatabaseReference): Pair<Int, Int> =
        withContext(Dispatchers.IO) {
            val snapshot = suspendFirebaseSnapshot(refDataBase)
            val total = snapshot.childrenCount.toInt()
            Pair(total, 0)
        }

    suspend fun export_M14_Room_To_Csv(csv: File) = withContext(Dispatchers.IO) {
        val datas = appDatabase.dao_M14VentPeriode().getAll()
        if (datas.isEmpty()) return@withContext

        csv.parentFile?.mkdirs()

        val headers = datas.first().toFirebaseMap().keys.toList()
        val existingRows: LinkedHashMap<String, List<String>> = linkedMapOf()

        if (csv.exists()) {
            val lines = csv.readLines()
            if (lines.size > 1) {
                val fileHeaders = lines[0].splitCsvLine()
                val keyIdx = fileHeaders.indexOf("keyID")
                lines.drop(1).forEach { line ->
                    val cells = line.splitCsvLine()
                    val id = cells.getOrNull(keyIdx) ?: ""
                    if (id.isNotEmpty()) existingRows[id] = cells
                }
            }
        }

        datas.forEach { item ->
            existingRows[item.keyID] = item.toFirebaseMap().values.map { (it?.toString() ?: "").escapeCsv() }
        }

        FileWriter(csv, false).use { w ->
            w.write(headers.joinToString(",") + "\n")
            existingRows.values.forEach { cells ->
                w.write(cells.joinToString(",") { it.escapeCsv() } + "\n")
            }
        }
    }

    suspend fun set_scv_M14_au_fireBase(
        csvFile: File,
        refDataBase: DatabaseReference,
    ) = withContext(Dispatchers.IO) {
        if (!csvFile.exists() || csvFile.length() == 0L) return@withContext
        val lines = csvFile.readLines().filter { it.isNotBlank() }
        if (lines.size < 2) return@withContext

        val headers = lines[0].splitCsvLine()
        val keyIdx = headers.indexOf("keyID")
        if (keyIdx == -1) return@withContext

        val items = lines.drop(1).mapNotNull { line ->
            val cells = line.splitCsvLine()
            val keyID = cells.getOrNull(keyIdx)?.trim()?.removeSurrounding("\"")
            if (keyID.isNullOrBlank()) return@mapNotNull null
            val map = headers.zip(cells).associate { (h, v) ->
                h to v.trim().removeSurrounding("\"").ifEmpty { null }
            }
            runCatching { m14_from_Map(map) }.getOrNull()
        }

        if (items.isEmpty()) return@withContext
        val updates: Map<String, Any> = items.associate { it.keyID to it.toFirebaseMap() }
        refDataBase.updateChildren(updates).await()
    }

    suspend fun import_M14_FireBase_To_Csv(
        refDataBase: DatabaseReference,
        csvFile: File,
    ) = withContext(Dispatchers.IO) {
        val snapshot = suspendFirebaseSnapshot(refDataBase)

        val items = snapshot.children.mapNotNull { child ->
            val raw = child.value
            if (raw !is Map<*, *>) return@mapNotNull null
            @Suppress("UNCHECKED_CAST")
            val map = (raw as Map<String, Any?>).mapValues { it.value?.toString() }
            val item = runCatching { m14_from_Map(map) }.getOrNull()
            item
        }

        if (items.isEmpty()) return@withContext

        csvFile.parentFile?.mkdirs()

        val headers = items.first().toFirebaseMap().keys.toList()
        val existingRows: LinkedHashMap<String, List<String>> = linkedMapOf()

        if (csvFile.exists()) {
            val lines = csvFile.readLines()
            if (lines.size > 1) {
                val fileHeaders = lines[0].splitCsvLine()
                val keyIdx = fileHeaders.indexOf("keyID")
                lines.drop(1).forEach { line ->
                    val cells = line.splitCsvLine()
                    val id = cells.getOrNull(keyIdx) ?: ""
                    if (id.isNotEmpty()) existingRows[id] = cells
                }
            }
        }

        items.forEach { item ->
            existingRows[item.keyID] = item.toFirebaseMap().values.map { (it?.toString() ?: "").escapeCsv() }
        }

        FileWriter(csvFile, false).use { w ->
            w.write(headers.joinToString(",") + "\n")
            existingRows.values.forEach { cells ->
                w.write(cells.joinToString(",") { it.escapeCsv() } + "\n")
            }
        }
    }

    suspend fun import_M14Csv_To_Room(csvFile: File) = withContext(Dispatchers.IO) {
        if (!csvFile.exists() || csvFile.length() == 0L) return@withContext
        val lines = csvFile.readLines().filter { it.isNotBlank() }
        if (lines.size < 2) return@withContext

        val headers = lines[0].splitCsvLine()
        val keyIdx = headers.indexOf("keyID")
        if (keyIdx == -1) return@withContext

        val items = lines.drop(1).mapNotNull { line ->
            val cells = line.splitCsvLine()
            val keyID = cells.getOrNull(keyIdx)?.trim()?.removeSurrounding("\"")
            if (keyID.isNullOrBlank()) return@mapNotNull null
            val map = headers.zip(cells).associate { (h, v) ->
                h to v.trim().removeSurrounding("\"").ifEmpty { null }
            }
            runCatching { m14_from_Map(map) }.getOrNull()
        }

        if (items.isNotEmpty()) {
            items.forEach { appDatabase.dao_M14VentPeriode().upsert(it) }
        }
    }

    suspend fun import_M14_FireBase_To_Room(refDataBase: DatabaseReference) =
        withContext(Dispatchers.IO) {
            val snapshot = suspendFirebaseSnapshot(refDataBase)
            val items = snapshot.children.mapNotNull { child ->
                val raw = child.value
                if (raw !is Map<*, *>) return@mapNotNull null
                @Suppress("UNCHECKED_CAST")
                val map = (raw as Map<String, Any?>).mapValues { it.value?.toString() }
                val item = runCatching { m14_from_Map(map) }.getOrNull()
                item
            }
            items.forEach { appDatabase.dao_M14VentPeriode().upsert(it) }
        }

    private fun m14_from_Map(map: Map<String, String?>): M14VentPeriode {
        return M14VentPeriode(
            keyID = map["keyID"] ?: "",
            creationTimestamp = map["creationTimestamp"]?.toLongOrNull() ?: System.currentTimeMillis(),
            dernierTimeTampsSynchronisationAvecFireBase = map["dernierTimeTampsSynchronisationAvecFireBase"]?.toLongOrNull() ?: System.currentTimeMillis(),
            abdelmounen_Doit_Etre_Ici = map["abdelmounen_Doit_Etre_Ici"]?.toBoolean() ?: false,
            parent_M9AppCompt_KeyID = map["parent_M9AppCompt_KeyID"] ?: "",
            parent_M9AppCompt_DebugInfos = map["parent_M9AppCompt_DebugInfos"] ?: "",
            son_verification_entre_vent_et_achat_est_fait = map["son_verification_entre_vent_et_achat_est_fait"]?.toBoolean() ?: true,
            credit_Vents_Totale = map["credit_Vents_Totale"]?.toDoubleOrNull() ?: 0.0,
            cash_Vents_Totale = map["cash_Vents_Totale"]?.toDoubleOrNull() ?: 0.0,
            credit_achats_Totale = map["credit_achats_Totale"]?.toDoubleOrNull() ?: 0.0,
            cash_achats_Totale = map["cash_achats_Totale"]?.toDoubleOrNull() ?: 0.0,
            credit_produitsAuDepot = map["credit_produitsAuDepot"]?.toDoubleOrNull() ?: 0.0,
            valeur_Produits_depuit_Ancien_Vent_Period = map["valeur_Produits_depuit_Ancien_Vent_Period"]?.toDoubleOrNull() ?: 0.0,
            acheter_produitsAuDepot = map["acheter_produitsAuDepot"]?.toDoubleOrNull() ?: 0.0,
            pre_fraits_voiture_essance_marche_et_paprasse = map["pre_fraits_voiture_essance_marche_et_paprasse"]?.toDoubleOrNull() ?: 0.0,
            saved_balance = map["saved_balance"]?.toDoubleOrNull() ?: 0.0,
            etateActuellementEst = map["etateActuellementEst"]?.let { runCatching { M14VentPeriode.EtateActuellementEst.valueOf(it) }.getOrNull() } ?: M14VentPeriode.EtateActuellementEst.SoquetteNonDefinie
        )
    }
}

fun String.escapeCsv(): String {
    val sanitized = replace("\r\n", " ").replace("\n", " ").replace("\r", " ")
    return if (sanitized.contains(',') || sanitized.contains('"')) {
        "\"${sanitized.replace("\"", "\"\"")}\""
    } else sanitized
}

fun String.splitCsvLine(): List<String> {
    val result = mutableListOf<String>()
    val current = StringBuilder()
    var inQuotes = false
    var i = 0
    while (i < length) {
        val c = this[i]
        when {
            c == '"' && inQuotes && i + 1 < length && this[i + 1] == '"' -> {
                current.append('"'); i += 2; continue
            }
            c == '"' -> inQuotes = !inQuotes
            c == ',' && !inQuotes -> { result.add(current.toString()); current.clear() }
            else -> current.append(c)
        }
        i++
    }
    result.add(current.toString())
    return result
}
