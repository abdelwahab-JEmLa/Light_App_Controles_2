package Working_IN.Feature.a.Main

import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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

// ─────────────────────────────────────────────────────────────────────────────
// Carte item
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun M3CouleurItem(
    item: M3CouleurProduitInfos,
    highlight: String = "",
) {
    val dotColor = remember(item.nomCouleurStrSiSonImageDispo) {
        runCatching {
            val raw = item.nomCouleurStrSiSonImageDispo
            if (raw.startsWith("#") && raw.length in listOf(7, 9))
                Color(raw.toColorInt())
            else Color(0xFF6A1B9A)
        }.getOrDefault(Color(0xFF6A1B9A))
    }

    // Card légèrement surlignée si elle correspond à la recherche
    val cardBg = if (highlight.isNotEmpty() &&
        (item.nomCouleurStrSiSonImageDispo.lowercase().contains(highlight.lowercase()) ||
                item.keyID.lowercase().contains(highlight.lowercase()) ||
                item.parentBProduitInfosKeyID.lowercase().contains(highlight.lowercase()))
    ) Color(0xFFEDE7F6) else Color(0xFFF3E5F5)

    Card(
        modifier = Modifier.Companion.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier.Companion
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.Companion.CenterVertically,
        ) {

            // Pastille colorée
            Box(
                modifier = Modifier.Companion
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(dotColor),
                contentAlignment = Alignment.Companion.Center,
            ) {
                Text(
                    text = item.keyID.takeLast(3).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Companion.ExtraBold,
                    color = Color.Companion.White,
                )
            }

            Spacer(Modifier.Companion.width(10.dp))

            Column(modifier = Modifier.Companion.weight(1f)) {

                // Nom couleur
                Text(
                    text = item.nomCouleurStrSiSonImageDispo.ifBlank { "— nom non défini —" },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Companion.Bold,
                    color = Color(0xFF4A148C),
                    maxLines = 1,
                    overflow = TextOverflow.Companion.Ellipsis,
                )

                Spacer(Modifier.Companion.height(2.dp))

                // Nom debug parent produit
                Text(
                    text = "📦 ${item.parentId1ProduitInfosDebugName.ifBlank { "—" }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF6A1B9A),
                    maxLines = 1,
                    overflow = TextOverflow.Companion.Ellipsis,
                )

                Spacer(Modifier.Companion.height(6.dp))

                // Days since last purchase
                val daysSinceAchat = remember(item.dernier_achant_timeTamp) {
                    if (item.dernier_achant_timeTamp <= 0L) null
                    else ((System.currentTimeMillis() - item.dernier_achant_timeTamp) /
                            (24L * 60L * 60L * 1_000L)).toInt()
                }
                Text(
                    text = when (daysSinceAchat) {
                        null -> "🛒 jamais acheté"
                        0 -> "🛒 acheté aujourd'hui"
                        1 -> "🛒 acheté il y a 1 jour"
                        else -> "🛒 acheté il y a $daysSinceAchat jours"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = when {
                        daysSinceAchat == null -> Color(0xFF9E9E9E)
                        daysSinceAchat <= 7 -> Color(0xFF2E7D32)  // vert  — récent
                        daysSinceAchat <= 30 -> Color(0xFFE65100)  // orange — limite proche
                        else -> Color(0xFFC62828)  // rouge — dépassé
                    },
                    fontWeight = FontWeight.Companion.Medium,
                )

                Spacer(Modifier.Companion.height(4.dp))
                // Chips IDs
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.Companion.CenterVertically,
                ) {
                    IdChip(
                        label = "key",
                        value = item.keyID.takeLast(3).uppercase(),
                        bg = Color(0xFF7B1FA2),
                        highlighted = highlight.isNotEmpty() &&
                                item.keyID.lowercase().contains(highlight.lowercase()),
                    )
                    IdChip(
                        label = "pKey",
                        value = item.parentBProduitInfosKeyID
                            .takeLast(3).uppercase().ifBlank { "—" },
                        bg = Color(0xFF512DA8),
                        highlighted = highlight.isNotEmpty() &&
                                item.parentBProduitInfosKeyID.lowercase()
                                    .contains(highlight.lowercase()),
                    )
                    IdChip(
                        label = "oldID",
                        value = item.parentBProduitOldID.toString(),
                        bg = Color(0xFF303F9F),
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Chip avec option de surlignage
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun IdChip(
    label: String,
    value: String,
    bg: Color,
    highlighted: Boolean = false,
) {
    val chipBg = if (highlighted) Color(0xFFFFD600) else bg
    val textColor = if (highlighted) Color.Black else Color.White
    val labelColor = if (highlighted) Color(0xFF555555) else Color.White.copy(alpha = 0.75f)

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(chipBg)
            .padding(horizontal = 7.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.labelSmall,
            color = labelColor,
        )
        Spacer(Modifier.width(3.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = textColor,
        )
    }
}
