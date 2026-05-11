package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.a.Screens.b.M3Couleur.Screen.Working_IN.Feature

import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.a.Main.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button
import com.example.light_app_controles.Modules.Base.SQL.Daos.AppDatabase

@Composable
fun M3CouleurList_Screen(
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current,
    appDatabase: AppDatabase = AppDatabase.DatabaseModule.getDatabase(context),
    viewModel: M3Features_ViewModel = viewModel(
        factory = viewModelFactory { initializer { M3Features_ViewModel(appDatabase = appDatabase) } }
    )
) {
    val focusManager = LocalFocusManager.current

    // ── État réactif sur list_M03 ────────────────────────────────────────────
    // 15 fake M3 items overlaid on the VM list: indices 1-8 are within the 30-day limit ✓,
    // indices 9-15 exceed it ✗ — so exactly 8 pass the filter.
    val listM03 = remember(viewModel.active_Datas.list_M03) {
        val base = viewModel.active_Datas.list_M03 ?: emptyList()
        val dayMs = 24L * 60L * 60L * 1_000L
        val now = System.currentTimeMillis()
        val fakeExtras = base
            .shuffled()
            .take(15)
            .mapIndexed { i, real ->
                real.copy(
                    dernier_achant_timeTamp = if (i < 8)
                        now - (i + 1) * 3 * dayMs       // 3, 6 … 24 days ✓
                    else
                        now - (31 + (i + 1)) * dayMs,   // 40, 41 … 46 days ✗
                )
            }
        base + fakeExtras
    }

    val list_filtred_by_limite_jours by remember { derivedStateOf {
        listM03
            ?.get_filtred_m3_by_limite_active_M9Compt_limite_couleurs_ou_leur_last_achate_est_moin_que_jour(FAKE_M9Compt)
            ?: emptyList() } }

    // ── Texte de recherche ───────────────────────────────────────────────────
    var query by remember { mutableStateOf("") }

    // ── Liste filtrée (recalculée à chaque changement de query ou fullList) ──
    val filteredList by remember {
        derivedStateOf {
            val q = query.trim().lowercase()
            if (q.isEmpty()) list_filtred_by_limite_jours
            else list_filtred_by_limite_jours.filter { item ->
                item.nomCouleurStrSiSonImageDispo.lowercase().contains(q) ||
                        item.keyID.lowercase().contains(q) ||
                        item.parentBProduitInfosKeyID.lowercase().contains(q) ||
                        item.parentId1ProduitInfosDebugName.lowercase().contains(q) // ← was missing
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {

        Column(modifier = Modifier
            .semantics(mergeDescendants = true) {
                set(value = listM03, key = SemanticsPropertyKey("listM03"))
            }
            .fillMaxSize()) {

            // ── Header violet ────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF6A1B9A))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "M3Couleur — ${filteredList.size} / ${list_filtred_by_limite_jours.size}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }

            // ── Barre de recherche ───────────────────────────────────────────
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                placeholder = {
                    Text(
                        text = "بحث بالاسم / keyID / parent M1 key",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF9E9E9E),
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color(0xFF6A1B9A),
                    )
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "مسح",
                                tint = Color(0xFF9E9E9E),
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = Color(0xFF6A1B9A),
                    unfocusedBorderColor = Color(0xFFCE93D8),
                    cursorColor          = Color(0xFF6A1B9A),
                ),
            )

            // ── Résultats ────────────────────────────────────────────────────
            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = if (query.isBlank()) "لا توجد بيانات" else "لا توجد نتائج لـ \"$query\"",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    items(
                        items = filteredList,
                        key = { it.keyID },
                    ) { item ->
                        M3CouleurItem(item = item, highlight = query.trim())
                    }
                }
            }
        }

        // ── FABs flottants ────────────────────────────────────────────────────
        FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button(appDatabase = appDatabase)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Carte item
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun M3CouleurItem(
    item: M3CouleurProduitInfos,
    highlight: String = "",
) {
    val dotColor = remember(item.nomCouleurStrSiSonImageDispo) {
        runCatching {
            val raw = item.nomCouleurStrSiSonImageDispo
            if (raw.startsWith("#") && raw.length in listOf(7, 9))
                Color(android.graphics.Color.parseColor(raw))
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
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            // Pastille colorée
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(dotColor),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = item.keyID.takeLast(3).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                )
            }

            Spacer(Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {

                // Nom couleur
                Text(
                    text = item.nomCouleurStrSiSonImageDispo.ifBlank { "— nom non défini —" },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A148C),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(Modifier.height(2.dp))

                // Nom debug parent produit
                Text(
                    text = "📦 ${item.parentId1ProduitInfosDebugName.ifBlank { "—" }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF6A1B9A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(Modifier.height(6.dp))

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
                        daysSinceAchat == null  -> Color(0xFF9E9E9E)
                        daysSinceAchat <= 7     -> Color(0xFF2E7D32)  // vert  — récent
                        daysSinceAchat <= 30    -> Color(0xFFE65100)  // orange — limite proche
                        else                    -> Color(0xFFC62828)  // rouge — dépassé
                    },
                    fontWeight = FontWeight.Medium,
                )

                Spacer(Modifier.height(4.dp))
                // Chips IDs
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
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
private fun IdChip(
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
