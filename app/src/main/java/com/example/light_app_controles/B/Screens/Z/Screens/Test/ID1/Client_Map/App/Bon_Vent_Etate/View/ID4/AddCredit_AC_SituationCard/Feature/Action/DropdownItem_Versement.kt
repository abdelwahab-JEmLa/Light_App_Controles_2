package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID4.AddCredit_AC_SituationCard.Feature.Action

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TextIncrease
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.b.Models.M8BonVent

@Composable
fun DropdownItem_Versement(
    clientKey: String,
    latestSituationMontant: Int?,
    isActive: Boolean,
    onActivate: () -> Unit,
    onCommit: (versement: M8BonVent, newSituation: M8BonVent) -> Unit,
) {
    // ── Own state ────────────────────────────────────────────────────────────
    var out_val by remember { mutableStateOf("") }
    var montant by remember { mutableStateOf(0.0) }
    var displayedMontant by remember(latestSituationMontant) {
        mutableStateOf<Int?>(latestSituationMontant)
    }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(isActive) {
        if (isActive) focusRequester.requestFocus()
        else out_val = "" // reset field when closed from outside
    }

    // ── Bons built from own montant ──────────────────────────────────────────
    fun buildBons(): Pair<M8BonVent, M8BonVent> {
        val baseTs = System.currentTimeMillis()
        val versementBon = M8BonVent(
            parent_M2Client_KeyID = clientKey,
            etateActuellementEst = M8BonVent.EtateActuellementEst.Versemment,
            creationTimestamps = baseTs,
            versement_fait = montant,
        )
        val newSituation = M8BonVent(
            parent_M2Client_KeyID = clientKey,
            etateActuellementEst = M8BonVent.EtateActuellementEst.New_Situation_Credit,
            creationTimestamps = baseTs + 1_000L,
            montant_principale_du_type = (latestSituationMontant?.toDouble() ?: 0.0) - montant,
        )
        return Pair(versementBon, newSituation)
    }

    DropdownMenuItem(
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.TextIncrease,
                contentDescription = null,
                tint = Color(0xFF43A047),
            )
        },
        text = {
            if (isActive) {
                OutlinedTextField(
                    value = out_val,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() }) out_val = input
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            val parsed = out_val.toIntOrNull()
                            if (parsed != null) {
                                montant = parsed.toDouble()
                                displayedMontant = parsed
                            }
                            out_val = displayedMontant?.toString() ?: ""
                            val (versementBon, newSituation) = buildBons()
                            onCommit(versementBon, newSituation)
                        }
                    ),
                    label = {
                        val diff = (displayedMontant ?: 0) - (out_val.toIntOrNull() ?: 0)
                        Text(
                            text = "الرصيد السابق — $diff",
                            style = MaterialTheme.typography.labelSmall,
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                )
            } else {
                Text(
                    text = "New Versement: ${displayedMontant ?: "-"} دج",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
        onClick = { if (!isActive) onActivate() },
    )
}
