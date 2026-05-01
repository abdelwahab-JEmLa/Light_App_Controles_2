package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.Action

import android.util.Log
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.M8BonVent
import com.example.light_app_controles.Modules.Base.SQL.Daos.AppDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import kotlin.collections.forEachIndexed
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


private const val TAG = "Setter_LongOperations"

class Setter_LongOperations(
    private val appDatabase: AppDatabase,
) {

    suspend fun add_New_M8BonVent(bon: M8BonVent)  {
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
        Log.d(TAG, "bach_update_FireBase_M8: ref=${refDataBase} | bons=${bons.size}")
        bons.forEachIndexed { index, bon ->
            runCatching {
                suspendCancellableCoroutine { cont ->
                    refDataBase.child(bon.keyID).setValue(bon.to_Map())
                        .addOnSuccessListener { cont.resume(Unit) }
                        .addOnFailureListener { cont.resumeWithException(it) }
                }
            }.onFailure { err ->
                Log.e(
                    TAG,
                    "bach_update_FireBase_M8: échec à l'index $index | keyID=${bon.keyID} | " +
                            "raison=${err.message ?: "inconnue"}",
                    err,
                )
                return@withContext
            }
        }
    }

    // FIX: new helper — fetches only the child count from Firebase without parsing full objects.
    // Used by the dropdown menu to display a "Firebase: N | CSV: M" stats line for But6.
    suspend fun get_Firebase_M8_Count(refDataBase: DatabaseReference): Int =
        withContext(Dispatchers.IO) {
            val snapshot = suspendCancellableCoroutine<DataSnapshot> { cont ->
                val listener = object : ValueEventListener {
                    override fun onDataChange(snap: DataSnapshot) {
                        if (cont.isActive) cont.resume(snap)
                    }
                    override fun onCancelled(error: DatabaseError) {
                        if (cont.isActive) cont.resumeWithException(error.toException())
                    }
                }
                refDataBase.addListenerForSingleValueEvent(listener)
                cont.invokeOnCancellation { refDataBase.removeEventListener(listener) }
            }
            snapshot.childrenCount.toInt()
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
                val fileHeaders = lines[0].split(",")
                val keyIdx = fileHeaders.indexOf("keyID")
                lines.drop(1).forEach { line ->
                    val cells = line.split(",")
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
            existingRows.values.forEach { w.write(it.joinToString(",") + "\n") }
        }
    }

    suspend fun set_scv_m8_au_fireBase(
        csvFile: File,
        refDataBase: DatabaseReference,
    ) = withContext(Dispatchers.IO) {
        Log.d(TAG, "set_scv_m8_au_fireBase: ref=${refDataBase} | csv=${csvFile.absolutePath}")
        if (!csvFile.exists() || csvFile.length() == 0L) {
            Log.w(TAG, "set_scv_m8_au_fireBase: fichier absent ou vide → ${csvFile.absolutePath}")
            return@withContext
        }
        val lines = csvFile.readLines().filter { it.isNotBlank() }
        if (lines.size < 2) {
            Log.w(TAG, "set_scv_m8_au_fireBase: fichier sans données (lignes=${lines.size})")
            return@withContext
        }

        val headers = lines[0].split(",")
        val keyIdx = headers.indexOf("keyID")
        if (keyIdx == -1) {
            Log.e(TAG, "set_scv_m8_au_fireBase: colonne 'keyID' introuvable dans les entêtes → $headers")
            return@withContext
        }

        val bons = lines.drop(1).mapNotNull { line ->
            val cells = line.split(",")
            val keyID = cells.getOrNull(keyIdx)?.trim()?.removeSurrounding("\"")
            if (keyID.isNullOrBlank()) return@mapNotNull null
            val map = headers.zip(cells).associate { (h, v) ->
                h to v.trim().removeSurrounding("\"").ifEmpty { null }
            }
            runCatching { M8BonVent.to_Map(map) }.onFailure { err ->
                Log.e(TAG, "set_scv_m8_au_fireBase: impossible de parser la ligne keyID=$keyID | ${err.message}", err)
            }.getOrNull()
        }

        if (bons.isEmpty()) {
            Log.w(TAG, "set_scv_m8_au_fireBase: aucun bon valide extrait du CSV → abandon")
            return@withContext
        }

        Log.d(TAG, "set_scv_m8_au_fireBase: envoi de ${bons.size} bons vers Firebase…")
        bach_update_FireBase_M8(bons, refDataBase)
    }

    suspend fun import_M8_FireBase_To_Csv(
        refDataBase: DatabaseReference,
        csvFile: File,
    ) = withContext(Dispatchers.IO) {
        Log.d(TAG, "import_M8_FireBase_To_Csv: ref=$refDataBase | csv=${csvFile.absolutePath}")

        val snapshot = suspendCancellableCoroutine<DataSnapshot> { cont ->
            val listener = object : ValueEventListener {
                override fun onDataChange(snap: DataSnapshot) {
                    if (cont.isActive) cont.resume(snap)
                }
                override fun onCancelled(error: DatabaseError) {
                    if (cont.isActive) cont.resumeWithException(error.toException())
                }
            }
            refDataBase.addListenerForSingleValueEvent(listener)
            cont.invokeOnCancellation { refDataBase.removeEventListener(listener) }
        }

        val bons = snapshot.children.mapNotNull { child ->
            val raw = child.value
            if (raw !is Map<*, *>) return@mapNotNull null
            @Suppress("UNCHECKED_CAST")
            val map = (raw as Map<String, Any?>).mapValues { it.value?.toString() }
            runCatching { M8BonVent.to_Map(map) }.onFailure { err ->
                Log.e(TAG, "import_M8_FireBase_To_Csv: parse échoué | keyID=${child.key} | ${err.message}", err)
            }.getOrNull()
        }

        if (bons.isEmpty()) {
            Log.w(TAG, "import_M8_FireBase_To_Csv: aucun bon valide extrait de Firebase → abandon")
            return@withContext
        }

        Log.d(TAG, "import_M8_FireBase_To_Csv: ${bons.size} bons récupérés, écriture CSV…")

        csvFile.parentFile?.mkdirs()

        val headers = bons.first().to_Map().keys.toList()
        val existingRows: LinkedHashMap<String, List<String>> = linkedMapOf()

        if (csvFile.exists()) {
            val lines = csvFile.readLines()
            if (lines.size > 1) {
                val fileHeaders = lines[0].split(",")
                val keyIdx = fileHeaders.indexOf("keyID")
                lines.drop(1).forEach { line ->
                    val cells = line.split(",")
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
            existingRows.values.forEach { w.write(it.joinToString(",") + "\n") }
        }

        Log.d(TAG, "import_M8_FireBase_To_Csv: CSV mis à jour avec ${bons.size} bons.")
    }

    suspend fun import_M8Csv_To_Room(csvFile: File) = withContext(Dispatchers.IO) {
        if (!csvFile.exists() || csvFile.length() == 0L) return@withContext
        val lines = csvFile.readLines().filter { it.isNotBlank() }
        if (lines.size < 2) return@withContext

        val headers = lines[0].split(",")
        val bons = lines.drop(1).mapNotNull { line ->
            val cells = line.split(",")
            val map = headers.zip(cells).associate { (h, v) ->
                h to v.trim().removeSurrounding("\"").ifEmpty { null }
            }
            runCatching { M8BonVent.to_Map(map) }.getOrNull()
        }

        if (bons.isNotEmpty()) bons.forEach { appDatabase.dao_M8BonVent().upsert(it) }
    }

    suspend fun delete_All_M8() {
        appDatabase.dao_M8BonVent().deleteAll()
    }
}

private fun String.escapeCsv() =
    if (contains(',') || contains('"') || contains('\n')) "\"${replace("\"", "\"\"")}\"" else this
