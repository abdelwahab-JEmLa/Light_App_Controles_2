package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.c.Screens.b.M2Client.Screen

import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt

@Composable
fun M2ClientItem(
    item: M2Client,
    highlight: String = "",
) {
    val avatarBg = remember(item.couleur) {
        runCatching {
            val raw = item.couleur
            if (raw.startsWith("#") && raw.length in listOf(7, 9)) {
                val resolved = Color(raw.toColorInt())
                if (resolved == Color.White) Color(0xFF6A1B9A) else resolved
            } else Color(0xFF6A1B9A)
        }.getOrDefault(Color(0xFF6A1B9A))
    }

    val cardBg = if (highlight.isNotEmpty() &&
        (item.nom.lowercase().contains(highlight.lowercase()) ||
                item.nomPrenomArabe.lowercase().contains(highlight.lowercase()) ||
                item.keyID.lowercase().contains(highlight.lowercase()))
    ) Color(0xFFEDE7F6) else Color(0xFFF3E5F5)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(avatarBg),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = item.keyID.takeLast(3).uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Nom (Français)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = item.nom.ifBlank { "— nom non défini —" },
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4A148C),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Text(
                        text = "(${item.keyID})",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Spacer(Modifier.height(2.dp))

                // Nom Arabe
                if (item.nomPrenomArabe.isNotBlank()) {
                    Text(
                        text = item.nomPrenomArabe,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF6A1B9A),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(2.dp))
                }

                // Numéro de téléphone
                if (item.numTelephone.isNotBlank()) {
                    Text(
                        text = "📞 ${item.numTelephone}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray,
                    )
                    Spacer(Modifier.height(4.dp))
                }

                // Row de Badges/Chips
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ClientBadge(
                        label = item.clientTypeMode.name,
                        bg = Color(0xFF7B1FA2)
                    )
                    ClientBadge(
                        label = item.typeDeSonMagasine.name.replace("_", " "),
                        bg = Color(0xFF512DA8)
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            // Solde du Crédit
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Crédit",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                )
                Text(
                    text = String.format("%.2f DA", item.currentCreditBalance),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (item.currentCreditBalance > 0.0) Color(0xFFD32F2F) else Color(0xFF388E3C),
                )
            }
        }
    }
}

@Composable
fun ClientBadge(
    label: String,
    bg: Color,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg.copy(alpha = 0.15f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = label.lowercase().capitalize(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = bg,
        )
    }
}
