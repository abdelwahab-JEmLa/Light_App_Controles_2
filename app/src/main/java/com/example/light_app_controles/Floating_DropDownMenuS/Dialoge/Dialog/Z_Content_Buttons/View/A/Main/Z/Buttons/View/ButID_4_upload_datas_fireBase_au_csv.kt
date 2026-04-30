package A_Main.Shared.Views.Dialogs.Floating_DropDownMenu.Dialog.Z_Content_Buttons.View

import EntreApps.Shared.Models.M00CentralParametresOfAllApps.Companion.central_Local_storageLink
import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.light_app_controles.Modules.Uis.Ui.SyncProgressIndicator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Composable
fun ButID_4_upload_datas_fireBase_au_csv(
    enabled: Boolean,
) {
    val iconTint = Color(0xFF1565C0) // Firebase blue
    val scope = rememberCoroutineScope()

    var progress by remember { mutableStateOf<Float?>(null) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var isDone by remember { mutableStateOf(false) }

    var firebase_m2_datas by remember { mutableStateOf<List<M2Client>?>(null) }
    var old_csv_m2_datas_size by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        // Firebase count
        launch {
            runCatching { fetchM2ClientsFromFirebase() }
                .onSuccess { firebase_m2_datas = it }
        }
        // Existing CSV row count (header line excluded)
        launch {
            withContext(Dispatchers.IO) {
                val csvFile = File(central_Local_storageLink, "CSV_Export/${M2Client.pathString}.csv")
                old_csv_m2_datas_size = if (csvFile.exists()) {
                    (csvFile.readLines().size - 1).coerceAtLeast(0)
                } else {
                    0
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = null,
                    tint = if (enabled) iconTint else iconTint.copy(alpha = 0.4f)
                )
            },
            text = {
                val fbSize = firebase_m2_datas?.size?.toString() ?: "…"
                val csvSize = old_csv_m2_datas_size?.toString() ?: "…"
                Text(
                    modifier = Modifier.semantics(mergeDescendants = true) {
                        set(value = "firebase:$fbSize", key = SemanticsPropertyKey("firebase_size"))
                        set(value = "csv:$csvSize", key = SemanticsPropertyKey("csv_size"))
                    },
                    text = when {
                        errorMsg != null -> "Erreur sync Firebase→CSV ✗"
                        isDone -> "Sync terminé ✓ (${firebase_m2_datas?.size ?: 0} lignes)"
                        progress != null && progress!! < 1f -> {
                            val pct = (progress!! * 100).toInt()
                            "Sync Firebase→CSV… $pct %"
                        }
                        else -> "Import Firebase($fbSize) To CSV($csvSize) — écraser CSV"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = when {
                        errorMsg != null -> MaterialTheme.colorScheme.error
                        enabled -> MaterialTheme.colorScheme.onSurface
                        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            enabled = enabled && (progress == null || progress == 1f),
            onClick = {
                errorMsg = null
                isDone = false
                scope.launch {
                    progress = 0f
                    runCatching {
                        // Step 1 — fetch fresh data from Firebase
                        val clients = fetchM2ClientsFromFirebase()
                        firebase_m2_datas = clients
                        progress = 0.4f

                        // Step 2 — overwrite local CSV
                        withContext(Dispatchers.IO) {
                            val outputDir = File(central_Local_storageLink, "CSV_Export")
                            outputDir.mkdirs()
                            val csvFile = File(outputDir, "${M2Client.pathString}.csv")
                            writeM2ClientsToCsv(csvFile, clients)
                            old_csv_m2_datas_size = clients.size
                        }

                        progress = 1f
                        isDone = true
                    }.onFailure { e ->
                        errorMsg = e.message ?: "Erreur inconnue"
                        progress = null
                    }
                }
            }
        )

        if (progress != null) {
            SyncProgressIndicator(
                progress = progress!!,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
            )
        }

        if (errorMsg != null) {
            Text(
                text = errorMsg!!,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
            )
        }
    }
}

// ── Private helpers ───────────────────────────────────────────────────────────

/**
 * Suspends until Firebase returns all [M2Client] entries under [M2Client.ref].
 */
private suspend fun fetchM2ClientsFromFirebase(): List<M2Client> =
    suspendCancellableCoroutine { cont ->
        M2Client.ref.get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.children.mapNotNull { it.getValue(M2Client::class.java) }
                cont.resume(list)
            }
            .addOnFailureListener { e -> cont.resumeWithException(e) }
    }

/**
 * Writes [clients] to [file] as a CSV whose column order matches
 * the keys produced by [M2Client.toFirebaseMap].
 *
 * Existing file content is replaced entirely (append = false).
 */
private fun writeM2ClientsToCsv(file: File, clients: List<M2Client>) {
    // Derive headers from the first client (or a default instance when list is empty)
    val sampleMap = (clients.firstOrNull() ?: M2Client.get_default()).toFirebaseMap()
    val headers = sampleMap.keys.toList()

    FileWriter(file, /* append = */ false).use { writer ->
        // Header row
        writer.write(headers.joinToString(",") { it.escapeCsvCell() })
        writer.write("\n")
        // Data rows
        clients.forEach { client ->
            val map = client.toFirebaseMap()
            val row = headers.map { key -> map[key]?.toString() ?: "" }
            writer.write(row.joinToString(",") { it.escapeCsvCell() })
            writer.write("\n")
        }
    }
}

private fun String.escapeCsvCell(): String =
    if (contains(',') || contains('"') || contains('\n') || contains('\r'))
        "\"${replace("\"", "\"\"")}\""
    else this
