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
@Suppress("unused")
class Setter_LongDatas(
    val appDatabase: AppDatabase,
    val  context: Context,
) {
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

        val xp4Bons = bons.filter { it.keyID.endsWith("xp4") }
        Log.d("But6_FireBaseToCsv", "XP4 records fetched from Firebase: ${xp4Bons.map { it.keyID }}")

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
            if (bon.keyID.endsWith("xp4")) {
                Log.d("But6_FireBaseToCsv", "Writing XP4 record to CSV: ${bon.keyID}")
            }
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

        val xp4Bons = bons.filter { it.keyID.endsWith("xp4") }
        Log.d("But3_CsvToRoom", "XP4 records parsed from CSV: ${xp4Bons.map { it.keyID }}")

        if (importOnlyCredits) {
            bons = bons.filter { it.etateActuellementEst.credit_type }
        }

        if (bons.isNotEmpty()) {
            bons.forEach { bon ->
                if (bon.keyID.endsWith("xp4")) {
                    Log.d("But3_CsvToRoom", "Upserting XP4 record to Room database: ${bon.keyID}")
                }
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
                val updates: Map<String, Any> = datas.associate { it.keyID to it.toFirebaseMap() }
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
            val updates = mutableMapOf<String, Any>(data.keyID to data.toFirebaseMap())
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
    ) {
        composScope.launch {
            appDatabase.dao_M10OperationVentCouleur().upsert(operation)
            val opUpdates = mutableMapOf<String, Any>(operation.keyID to operation)
            M10OperationVentCouleur.Companion.ref.updateChildren(opUpdates).await()

            val tariffWithDefaults = selectedTariff.copy(
                defaultNonSaved_Entre = false,
                dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
            )
            appDatabase.dao_M13TarificationInfos().update(tariffWithDefaults)
            val tariffUpdates = mutableMapOf<String, Any>(
                tariffWithDefaults.keyID to tariffWithDefaults.toFirebaseMap()
            )
            M13TarificationInfos.Companion.ref.updateChildren(tariffUpdates).await()
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
