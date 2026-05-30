package Application5.App.Dialog.Dialog.Sub

import Application5.App.Dialog.Dialog.Sub.A_Takiyim.Utils.ClickableFieldWithIcon
import Application5.App.Repository.M19Etudiant
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun IstedrakSection_SeparatedAppsCodingPattern(
    etudiant: M19Etudiant,
    onShowIstedrakSouraDialog: () -> Unit,
    onShowIstedrakMokarrareDialog: () -> Unit,
    onShowIstedrakTakiyimDialog: () -> Unit,
    onExportIstedrak: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Section header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "استدراك قديم (Previous Records)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            IconButton(onClick = onExportIstedrak) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Export Observations via Gemini",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Istedrak Kadim - Akher Soura
        ClickableFieldWithIcon(
            label = "آخر سورة (قديم):",
            value = etudiant.istedrak_kadim_Akher_Soura_Wassale_Laha.arabicName,
            onClick = onShowIstedrakSouraDialog,
            color = MaterialTheme.colorScheme.primary
        )

        // Istedrak Kadim - Moukarare
        ClickableFieldWithIcon(
            label = "مكررة (قديم):",
            value = etudiant.istedrak_kadim_Moukarare.arabicName,
            onClick = onShowIstedrakMokarrareDialog,
            color = MaterialTheme.colorScheme.secondary
        )

        // Istedrak Kadim - Takyim
        ClickableFieldWithIcon(
            label = "تقييم (قديم):",
            value = etudiant.istedrak_kadim_Takyim_hali.arabicName,
            onClick = onShowIstedrakTakiyimDialog,
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}
