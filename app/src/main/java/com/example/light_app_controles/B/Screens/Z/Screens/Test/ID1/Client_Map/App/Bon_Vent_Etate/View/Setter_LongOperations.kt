package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View

import com.example.light_app_controles.Modules.Base.SQL.Daos.AppDatabase
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

sealed class FirebaseUploadState {
    object Idle : FirebaseUploadState()
    data class InProgress(val done: Int, val total: Int) : FirebaseUploadState()
    object Success : FirebaseUploadState()
    data class Error(val message: String) : FirebaseUploadState()
}

class Setter_LongOperations(
    private val appDatabase: AppDatabase,
) {
    private val _uploadState = MutableStateFlow<FirebaseUploadState>(FirebaseUploadState.Idle)
    val uploadState: StateFlow<FirebaseUploadState> = _uploadState.asStateFlow()

    suspend fun update_M8(bon: M8BonVent) = withContext(Dispatchers.IO) {
        appDatabase.dao_M8BonVent().upsert(bon)
    }

    suspend fun insertAll(bons: List<M8BonVent>) = withContext(Dispatchers.IO) {
        appDatabase.dao_M8BonVent().insertAll(bons)
    }

    suspend fun bach_update_FireBase_M8(
        bons: List<M8BonVent>,
        refDataBase: DatabaseReference,
    ) = withContext(Dispatchers.IO) {
        val total = bons.size
        _uploadState.value = FirebaseUploadState.InProgress(0, total)
        bons.forEachIndexed { index, bon ->
            runCatching {
                suspendCancellableCoroutine { cont ->
                    refDataBase.child(bon.keyID).setValue(bon.to_Map())
                        .addOnSuccessListener { cont.resume(Unit) }
                        .addOnFailureListener { cont.resumeWithException(it) }
                }
            }.onFailure {
                _uploadState.value = FirebaseUploadState.Error(it.message ?: "Unknown error")
                return@withContext
            }
            _uploadState.value = FirebaseUploadState.InProgress(index + 1, total)
        }
        _uploadState.value = FirebaseUploadState.Success
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
        if (!csvFile.exists() || csvFile.length() == 0L) return@withContext
        val lines = csvFile.readLines().filter { it.isNotBlank() }
        if (lines.size < 2) return@withContext

        val headers = lines[0].split(",")
        val keyIdx = headers.indexOf("keyID")
        if (keyIdx == -1) return@withContext

        val bons = lines.drop(1).mapNotNull { line ->
            val cells = line.split(",")
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

        if (bons.isNotEmpty()) appDatabase.dao_M8BonVent().insertAll(bons)
    }
}

private fun String.escapeCsv() =
    if (contains(',') || contains('"') || contains('\n')) "\"${replace("\"", "\"\"")}\"" else this
