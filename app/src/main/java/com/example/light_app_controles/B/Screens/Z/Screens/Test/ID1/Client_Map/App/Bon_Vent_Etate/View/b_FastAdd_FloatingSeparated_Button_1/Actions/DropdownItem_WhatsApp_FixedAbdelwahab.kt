package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.b_FastAdd_FloatingSeparated_Button_1.Actions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Dropdown item — regular or Business WhatsApp using FIXED Abdelwahab Osstad number.
 *
 * FIX 1 — phone format: the constant [ABDELWAHAB_WA_ME_NUMBER] is digits-only
 *   (no "+"), so it is always valid inside "https://wa.me/<number>".
 *   The old "+213…" string was passed straight into the URL, which caused
 *   WhatsApp/WhatsApp Business to fail resolving the conversation.
 *
 * FIX 2 — flicker: the caller now dismisses the dropdown *before* invoking
 *   [onSend], preventing the state mutation that triggered [onSendWhatsApp]
 *   from causing a recomposition while the dropdown was still visible.
 *
 * @param isWhatsAppBusiness  true → open com.whatsapp.w4b; false → com.whatsapp
 * @param iconTint            WhatsApp green (0xFF25D366) or Business teal (0xFF00897B)
 * @param onSend              Trigger the capture-and-share flow with Abdelwahab's number.
 */
@Composable
 fun DropdownItem_WhatsApp_FixedAbdelwahab(
    isWhatsAppBusiness: Boolean,
    iconTint: Color,
    labelPrefix: String,
    onSend: (phoneNumber: String, isWhatsAppBusiness: Boolean) -> Unit,
) {
    DropdownMenuItem(
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.Companion.size(22.dp),
            )
        },
        text = {
            Column {
                Text(
                    text = "$labelPrefix — عبدالوهاب حمنيش",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Companion.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                // Show the human-readable form with "+" for display only;
                // the value passed to onSend has no "+" (wa.me format).
                Text(
                    text = "+$ABDELWAHAB_WA_ME_NUMBER",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        onClick = {
            // ABDELWAHAB_WA_ME_NUMBER is already normalised — no "+" → no URL error
            onSend(ABDELWAHAB_WA_ME_NUMBER, isWhatsAppBusiness)
        },
    )
}
