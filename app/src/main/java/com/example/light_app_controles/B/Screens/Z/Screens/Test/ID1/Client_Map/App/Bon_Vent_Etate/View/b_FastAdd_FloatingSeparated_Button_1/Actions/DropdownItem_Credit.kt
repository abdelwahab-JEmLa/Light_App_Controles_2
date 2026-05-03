package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.b_FastAdd_FloatingSeparated_Button_1.Actions

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
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.M8BonVent

/**
 * Dropdown item — "دين جديد" (new credit).
 *
 * @param isActive       true when this item is in editing mode (controlled by parent)
 * @param onActivate     called when the user taps the item; parent should set isActive = true
 *                       and close any other active item
 * @param onCommit       called with the two new bons once the user presses Done
 */
@Composable
fun DropdownItem_Credit(
    clientKey: String,
    latestSituationMontant: Int?,
    isActive: Boolean,
    onActivate: () -> Unit,
    onCommit: (credit: M8BonVent, newSituation: M8BonVent) -> Unit,
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
        val creditBon = M8BonVent(
            parent_M2Client_KeyID = clientKey,
            etateActuellementEst = M8BonVent.EtateActuellementEst.Credit,
            creationTimestamps = baseTs,
            credit_fait = montant,
        )
        val newSituation = M8BonVent(
            parent_M2Client_KeyID = clientKey,
            etateActuellementEst = M8BonVent.EtateActuellementEst.New_Situation_Credit,
            creationTimestamps = baseTs + 1_000L,
            montant_principale_du_type = (latestSituationMontant?.toDouble() ?: 0.0) + montant,
        )
        return Pair(creditBon, newSituation)
    }

    DropdownMenuItem(
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.TextIncrease,
                contentDescription = null,
                tint = Color(0xFFE53935),
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
                            val (creditBon, newSituation) = buildBons()
                            onCommit(creditBon, newSituation)
                        }
                    ),
                    label = {
                        val diff = (out_val.toIntOrNull() ?: 0) + (displayedMontant ?: 0)
                        Text(
                            text = "الرصيد الجديد — $diff",
                            style = MaterialTheme.typography.labelSmall,
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                )
            } else {
                Text(
                    text = "New Credit: ${displayedMontant ?: "-"} دج",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
        onClick = { if (!isActive) onActivate() },
    )
}
