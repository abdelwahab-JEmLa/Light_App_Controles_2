package com.example.light_app_controles.Floating_DropDownMenuS.Dialoge.Dialog.Z_Content_Buttons.View.A.Main.Z.Buttons.View

import EntreApps.Shared.Modules.Base.SQL.ImportCSV_Result
import EntreApps.Shared.Modules.Base.SQL.importAllTablesFromCSV
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.light_app_controles.Modules.Base.SQL.AppDatabase
import com.example.light_app_controles.Modules.Uis.Ui.SyncProgressIndicator
import kotlinx.coroutines.launch

@Composable
fun ButID2_ImportFromCSV_DropDownItemWBaseDonne(
    appDatabase: AppDatabase,
    enabled: Boolean,
) {
    val iconTint = Color(0xFF43A047)
    val scope = rememberCoroutineScope()

    var progress by remember { mutableStateOf<Float?>(null) }
    var currentTable by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var importResult by remember { mutableStateOf<ImportCSV_Result?>(null) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    // ── Confirmation dialog ────────────────────────────────────────────────
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = {
                Text(
                    text = "Importer depuis CSV",
                    style = MaterialTheme.typography.titleMedium,
                )
            },
            text = {
                Text(
                    text = "Cette action va effacer toutes les données locales et les " +
                            "remplacer par le contenu des fichiers CSV. " +
                            "Cette opération est irréversible. Continuer ?",
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        errorMsg = null
                        importResult = null
                        scope.launch {
                            progress = 0f
                            appDatabase.importAllTablesFromCSV(
                                onProgress = { p -> progress = p },
                                onCurrentTable = { t -> currentTable = t },
                            ).onSuccess { result ->
                                importResult = result
                            }.onFailure { e ->
                                errorMsg = e.message ?: "Erreur inconnue"
                                progress = null
                            }
                        }
                    }
                ) {
                    Text(
                        text = "Importer",
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text(text = "Annuler")
                }
            }
        )
    }

    // ── Menu item ──────────────────────────────────────────────────────────
    Column(modifier = Modifier.fillMaxWidth()) {
        DropdownMenuItem(
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.CloudDownload,
                    contentDescription = null,
                    tint = if (enabled) iconTint
                    else iconTint.copy(alpha = 0.4f)
                )
            },
            text = {
                Text(
                    modifier = Modifier.semantics(mergeDescendants = true) {
                        set(value = "", key = SemanticsPropertyKey(""))
                    },
                    text = when {
                        errorMsg != null -> "Erreur import ✗"
                        progress == null -> "Importer BD ← CSV (avec confirmation)"
                        progress!! < 1f -> {
                            val pct = (progress!! * 100).toInt()
                            if (currentTable.isNotBlank()) "Import… $pct % — $currentTable"
                            else "Import… $pct %"
                        }
                        else -> importResult?.let {
                            "Import terminé ✓ (${it.totalRows} lignes)"
                        } ?: "Import terminé ✓"
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
            onClick = { showConfirmDialog = true }
        )

        if (progress != null) {
            if (currentTable.isNotBlank() && progress!! < 1f) {
                Text(
                    text = currentTable,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                )
            }
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
