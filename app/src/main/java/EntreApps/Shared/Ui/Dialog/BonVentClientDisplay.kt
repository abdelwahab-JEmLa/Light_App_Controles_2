package EntreApps.Shared.Ui.Dialog

import EntreApps.Shared.Models.Relative_Vents.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.util.concurrent.TimeUnit

@Composable
fun BonVentClientDisplay(
    activeClient: M2Client?,
    on_vent_bon: M8BonVent?,
    on_vent_couleurs: List<M10OperationVentCouleur>?,
    listM13tarificationinfos: List<M13TarificationInfos>?,
) {
    var isTextCollapsed by remember { mutableStateOf(true) }
    val nomClient = activeClient?.nom ?: ""
    val phoneDisplay = formatPhoneDisplay(
        numTelephone = activeClient?.numTelephone ?: "",
        nomClient = nomClient
    )

    val clientDisplayText = if (activeClient == null) {
        "Aucun client"
    } else if (isTextCollapsed) {
        "$nomClient$phoneDisplay"
    } else {
        on_vent_bon?.let { bon ->
            val timeElapsed = getTimeElapsedString(bon.creationTimestamps)
            val ventsTrouve = on_vent_couleurs?.filter {
                it.etateDelivery == M10OperationVentCouleur.EtateDelivery.Trouve
            } ?: emptyList()
            val totalProducts = ventsTrouve.groupBy { it.parent_M1Produit_KeyId }.size
            val totalValue = ventsTrouve.sumOf { vent ->
                val tariff = listM13tarificationinfos?.find { it.keyID == vent.parentM13TarificationKeyID }
                val prix = tariff?.prixCurrency ?: 0.0
                vent.quantity * prix
            }
            if (bon.parent_M2Client_DebugInfos.isNotEmpty() &&
                bon.parent_M2Client_DebugInfos != "Non Defini"
            ) {
                "$nomClient$phoneDisplay - $timeElapsed - $totalProducts P - ${String.format("%.2f", totalValue)} DA"
            } else "Rechercher Client"
        } ?: "Aucun bon ouvert"
    }

    Text(
        text = clientDisplayText,
        modifier = Modifier
            .background(Color(0xFF4CAF50), shape = RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
            .clickable { isTextCollapsed = !isTextCollapsed },
        color = Color.White,
        style = MaterialTheme.typography.bodySmall
    )
}

internal fun getTimeElapsedString(creationTimestamp: Long): String {
    val elapsed = System.currentTimeMillis() - creationTimestamp
    val days = TimeUnit.MILLISECONDS.toDays(elapsed)
    val hours = TimeUnit.MILLISECONDS.toHours(elapsed) % 24
    val minutes = TimeUnit.MILLISECONDS.toMinutes(elapsed) % 60

    return when {
        days > 0 -> "${days}j ${hours}h"
        hours > 0 -> "${hours}h ${minutes}m"
        minutes > 0 -> "${minutes}m"
        else -> "< 1m"
    }
}

internal fun dzSignificantDigits(phone: String): String {
    val d = phone.filter { it.isDigit() }
    return when {
        d.startsWith("213") -> d.removePrefix("213")
        d.startsWith("0")   -> d.removePrefix("0")
        else                -> d
    }
}

internal fun formatPhoneDisplay(numTelephone: String, nomClient: String = ""): String {
    if (numTelephone.isBlank()) return ""
    val sig = dzSignificantDigits(numTelephone)
    if (sig == "553885037") return ""
    if (sig.takeLast(4) == "5037") return ""

    val normalized = ("0$sig").filter { it.isDigit() }
    if (!normalized.startsWith("0") || normalized.length < 9) return ""

    val operatorLabel = when (normalized.getOrNull(1)) {
        '5' -> "Ned"
        '7' -> "Dj"
        '6' -> "Mo"
        else -> ""
    }
    val lastTwo = normalized.takeLast(2)
    return " \uD83D\uDCDE${operatorLabel}:$lastTwo"
}
