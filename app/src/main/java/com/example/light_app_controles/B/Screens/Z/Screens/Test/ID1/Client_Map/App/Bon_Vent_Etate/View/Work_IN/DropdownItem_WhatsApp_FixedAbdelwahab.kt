package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.Work_IN

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
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.Work_IN.ABDELWAHAB_WA_ME_NUMBER

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
                modifier = Modifier.size(22.dp),
            )
        },
        text = {
            Column {
                Text(
                    text = "$labelPrefix — عبدالوهاب حمنيش",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "+${ABDELWAHAB_WA_ME_NUMBER}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        onClick = {
            onSend(ABDELWAHAB_WA_ME_NUMBER, isWhatsAppBusiness)
        },
    )
}
