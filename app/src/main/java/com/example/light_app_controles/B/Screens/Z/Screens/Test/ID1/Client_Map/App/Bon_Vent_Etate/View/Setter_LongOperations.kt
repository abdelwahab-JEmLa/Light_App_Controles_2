package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View

import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import com.example.light_app_controles.Modules.Base.SQL.Daos.AppDatabase
import com.google.firebase.database.DatabaseReference
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

    suspend fun update_M8(bon: M8BonVent) {
        withContext(Dispatchers.IO) {
            appDatabase.dao_M8BonVent().upsert(bon)
        }
    }

    suspend fun insertAll(bons: List<M8BonVent>) {
        withContext(Dispatchers.IO) {
            appDatabase.dao_M8BonVent().insertAll(bons)
        }
    }

    suspend fun exportToCsv(
        datas: List<M8BonVent>,
        fileName: String,
    ) = withContext(Dispatchers.IO) {
        if (datas.isEmpty()) return@withContext

        val csvFile = File(M00CentralParametresOfAllApps.central_Local_Csv, fileName)
        csvFile.parentFile?.mkdirs()

        val headers: List<String> = datas.first().to_Map().keys.toList()

        // Build a mutable map of existing rows keyed by keyID
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

        // Upsert: replace existing row or append new one
        datas.forEach { bon ->
            val row = bon.to_Map().values.map { v -> (v?.toString() ?: "").escapeCsv() }
            existingRows[bon.keyID] = row
        }

        // Write header + all rows
        FileWriter(csvFile, false).use { w ->
            w.write(headers.joinToString(",") + "\n")
            existingRows.values.forEach { row ->
                w.write(row.joinToString(",") + "\n")
            }
        }
    }


    suspend fun set_scv_m8_au_fireBase(
        csvFile: File,
        refDataBase: DatabaseReference,
    ) = withContext(Dispatchers.IO) {
        if (!csvFile.exists() || csvFile.length() == 0L) return@withContext

        val lines = csvFile.readLines().filter { it.isNotBlank() }
        if (lines.size < 2) return@withContext          // header-only or empty

        val headers = lines[0].split(",")
        val keyIdx = headers.indexOf("keyID")
        if (keyIdx == -1) return@withContext            // malformed CSV

        val batch: Map<String, Any> = lines.drop(1)
            .mapNotNull { line ->
                val cells = line.split(",")
                val keyID = cells.getOrNull(keyIdx)?.trim()?.removeSurrounding("\"")
                if (keyID.isNullOrBlank()) return@mapNotNull null
                val fieldMap: Map<String, Any> = headers
                    .zip(cells)
                    .associate { (header, value) ->
                        header to value.trim().removeSurrounding("\"")
                    }
                keyID to fieldMap
            }
            .toMap()

        if (batch.isEmpty()) return@withContext

        suspendCancellableCoroutine { cont ->
            refDataBase.updateChildren(batch)
                .addOnSuccessListener { cont.resume(Unit) }
                .addOnFailureListener { e -> cont.resumeWithException(e) }
        }
    }

}

// Private helpers
private fun String.escapeCsv(): String =
    if (contains(',') || contains('"') || contains('\n'))
        "\"${replace("\"", "\"\"")}\""
    else this
