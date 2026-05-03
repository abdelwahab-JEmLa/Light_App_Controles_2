package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View

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
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class Setter_LongOperations(
    private val appDatabase: AppDatabase,
) {
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
        bons.forEachIndexed { index, bon ->
            runCatching {
                suspendCancellableCoroutine { cont ->
                    refDataBase.child(bon.keyID).setValue(bon.to_Map())
                        .addOnSuccessListener { cont.resume(Unit) }
                        .addOnFailureListener { cont.resumeWithException(it) }
                }
            }.onFailure { return@withContext }
        }
    }

    suspend fun get_Firebase_M8_Counts(refDataBase: DatabaseReference): Pair<Int, Int> =
        withContext(Dispatchers.IO) {
            val creditNames = M8BonVent.EtateActuellementEst.values()
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
                w.write(cells.map { it.escapeCsv() }.joinToString(",") + "\n")
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
            runCatching { M8BonVent.to_Map(map) }.getOrNull()
        }

        if (bons.isEmpty()) return@withContext
        bach_update_FireBase_M8(bons, refDataBase)
    }

    suspend fun import_M8_FireBase_To_Csv(
        refDataBase: DatabaseReference,
        csvFile: File,
    ) = withContext(Dispatchers.IO) {
        val snapshot = suspendFirebaseSnapshot(refDataBase)

        val bons = snapshot.children.mapNotNull { child ->
            val raw = child.value
            if (raw !is Map<*, *>) return@mapNotNull null
            @Suppress("UNCHECKED_CAST")
            val map = (raw as Map<String, Any?>).mapValues { it.value?.toString() }
            runCatching { M8BonVent.to_Map(map) }.getOrNull()
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

    suspend fun import_M8Csv_To_Room(csvFile: File) = withContext(Dispatchers.IO) {
        if (!csvFile.exists() || csvFile.length() == 0L) return@withContext
        val lines = csvFile.readLines().filter { it.isNotBlank() }
        if (lines.size < 2) return@withContext

        val headers = lines[0].splitCsvLine()
        val bons = lines.drop(1).mapNotNull { line ->
            val cells = line.splitCsvLine()
            val map = headers.zip(cells).associate { (h, v) ->
                h to v.trim().removeSurrounding("\"").ifEmpty { null }
            }
            runCatching { M8BonVent.to_Map(map) }.getOrNull()
        }

        if (bons.isNotEmpty()) bons.forEach { appDatabase.dao_M8BonVent().upsert(it) }
    }

    suspend fun import_M8_FireBase_To_Room(
        refDataBase: DatabaseReference,
    ) = withContext(Dispatchers.IO) {
        val snapshot = suspendFirebaseSnapshot(refDataBase)

        val bons = snapshot.children.mapNotNull { child ->
            val raw = child.value
            if (raw !is Map<*, *>) return@mapNotNull null
            @Suppress("UNCHECKED_CAST")
            val map = (raw as Map<String, Any?>).mapValues { it.value?.toString() }
            runCatching { M8BonVent.to_Map(map) }.getOrNull()
        }

        if (bons.isEmpty()) return@withContext
        bons.forEach { appDatabase.dao_M8BonVent().upsert(it) }
    }

    suspend fun delete_All_M8() {
        appDatabase.dao_M8BonVent().deleteAll()
    }

    private suspend fun suspendFirebaseSnapshot(ref: DatabaseReference): DataSnapshot =
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
}

private fun String.escapeCsv(): String {
    val sanitized = replace("\r\n", " ").replace("\n", " ").replace("\r", " ")
    return if (sanitized.contains(',') || sanitized.contains('"')) {
        "\"${sanitized.replace("\"", "\"\"")}\""
    } else sanitized
}

private fun String.splitCsvLine(): List<String> {
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
