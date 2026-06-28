@file:Suppress("COMPOSE_APPLIER_CALL_MISMATCH")

package com.example.light_app_controles.B.Screens.Z.Screens.Apps.App

import Application5.App.A_ViewModel_SeparatedAppsCodingPattern
import Application5.App.MonthSelectionDialog_SeparatedAppsCodingPattern
import Application5.App.Options.FabButton_When_Its_EducationFragment
import Application5.App.Options.FabDropdownMenu_WhenIts_FragmentEducation
import Application5.App.Repository.M19Etudiant
import Application5.App.View.DropDownItems.View.ButID8.SessionsEducationDialog.Dialog.SessionsEducationDialog
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Close
import androidx.compose.foundation.border
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.light_app_controles.Modules.Base.SQL.Daos.AppDatabase
import com.example.light_app_controles.R
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun A_EducationFragment_SeparatedAppsCodingPattern(
    modifier: Modifier = Modifier,
    appDatabase: AppDatabase,
) {
    val context = LocalContext.current

    val viewModel: A_ViewModel_SeparatedAppsCodingPattern = viewModel(
        factory = viewModelFactory {
            initializer {
                A_ViewModel_SeparatedAppsCodingPattern(
                    context = context,
                    appDatabase = appDatabase,
                )
            }
        }
    )

    val repo19Etudiant = viewModel.repo19Etudiant
    val repo20Obsarvation = viewModel.repo20ObsarvationEtudion
    val activeDatas = viewModel.activeCentralValues
    val searchQuery = activeDatas.outlined_filter_searcher_floating_abouve_all

    var isSearchActive by remember { mutableStateOf(searchQuery.isNotEmpty()) }
    var selectedEtudiantForSessions by remember { mutableStateOf<M19Etudiant?>(null) }

    val activeOusstad = activeDatas.active_Ousstad_Tahfid

    if (activeDatas.displaye_dialog_mois_moinAcPlus_6_du_current) {
        MonthSelectionDialog_SeparatedAppsCodingPattern(
            onDismiss = {
                viewModel.update_activeDatas(displaye_dialog_mois_moinAcPlus_6_du_current = false)
                selectedEtudiantForSessions = null
            },
            onMonthSelected = { selectedMonth ->
                viewModel.update_activeDatas(
                    displaye_dialog_mois_moinAcPlus_6_du_current = false,
                    displaye_sections_education_du_mois = selectedMonth
                )
            }
        )
    }

    val selectedMonth = activeDatas.displaye_sections_education_du_mois
    if (selectedMonth != null) {
       SessionsEducationDialog(
            viewModel = viewModel,
            selectedMonth = selectedMonth,
            repo20Observation = repo20Obsarvation,
            onDismiss = {
                viewModel.update_activeDatas(displaye_sections_education_du_mois = null)
                selectedEtudiantForSessions = null
            }
        )
    }

    LaunchedEffect(activeOusstad) {
        repo19Etudiant.setFilter(activeOusstad)
    }

    val baseEtudiants = repo19Etudiant.datasValue.filter { it.parent_ousstad_key == activeOusstad?.key }

    val etudiants = if (searchQuery.isNotBlank()) {
        baseEtudiants.filter { etudiant ->
            etudiant.nom.contains(searchQuery, ignoreCase = true) ||
                    etudiant.prenom.contains(searchQuery, ignoreCase = true)
        }
    } else {
        baseEtudiants
    }.sortedWith(
        compareByDescending<M19Etudiant> { it.dernierTimeTampsSynchronisationAvecFireBase }
            .thenBy { it.positon_don_classe }
    )

    val hasUpdateToday = remember(etudiants) {
        etudiants.any { etudiant ->
            isSameDay(etudiant.dernierTimeTampsSynchronisationAvecFireBase, System.currentTimeMillis())
        }
    }

    val totalStudents = etudiants.size
    val updatedToday = etudiants.count { etudiant ->
        val updateTimestamp = etudiant.dernierTimeTampsSynchronisationAvecFireBase ?: etudiant.creationTimestamps
        isSameDay(updateTimestamp, System.currentTimeMillis())
    }

    // ── Screen dimensions for drag bounds ──────────────────────────────────────
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
    val buttonBoundXPx = with(density) { 80.dp.toPx() }
    val buttonBoundYPx = with(density) { 80.dp.toPx() }

    // ── FAB state: collapsed by default, opens on tap ─────────────────────────
    var showFabMenu by remember { mutableStateOf(false) }
    var fabOffsetX by remember { mutableFloatStateOf(screenWidthPx - buttonBoundXPx) }
    var fabOffsetY by remember { mutableFloatStateOf(screenHeightPx - with(density) { 200.dp.toPx() }) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .semantics(mergeDescendants = true) {
                set(
                    value = repo19Etudiant.datasValue.filter { it.parent_ousstad_key == "Kissm_Intikali" },
                    key = SemanticsPropertyKey("etudiants_kissm_intikali")
                )
                set(value = repo19Etudiant.datasValue, key = SemanticsPropertyKey("etudiants_all"))
            }
    ) {
        ScrollableInformationBanner(
            ousstadName = activeOusstad?.nom_arab ?: "",
            modifier = Modifier.fillMaxWidth()
        )

        if (etudiants.isEmpty()) {
            EmptyState(
                modifier = Modifier.fillMaxSize(),
                isFiltered = isSearchActive && searchQuery.isNotBlank()
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items = etudiants) { etudiant ->
                    B_EtudiantCard_SeparatedAppsCodingPattern(
                        viewModel = viewModel,
                        etudiant = etudiant,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        var showVideoMenu by remember { mutableStateOf(false) }
        var showFloatingVideo by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .offset { IntOffset(fabOffsetX.roundToInt(), fabOffsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        fabOffsetX = (fabOffsetX + dragAmount.x).coerceIn(0f, screenWidthPx - buttonBoundXPx)
                        fabOffsetY = (fabOffsetY + dragAmount.y).coerceIn(0f, screenHeightPx - buttonBoundYPx)
                    }
                }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                androidx.compose.foundation.layout.Box {
                    androidx.compose.material3.FloatingActionButton(
                        onClick = { showVideoMenu = true },
                        shape = androidx.compose.foundation.shape.CircleShape,
                        containerColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(          
                            imageVector = androidx.compose.material.icons.Icons.Default.PlayArrow,
                            contentDescription = "تعريف اولي بالتطبيق",
                            tint = Color.White
                        )
                    }
                    
                    androidx.compose.material3.DropdownMenu(
                        expanded = showVideoMenu,
                        onDismissRequest = { showVideoMenu = false }
                    ) {
                        androidx.compose.material3.DropdownMenuItem(
                            text = { androidx.compose.material3.Text("1- فديو التعريفي للواجهة الرئيسة") },
                            onClick = {
                                showFloatingVideo = true
                                showVideoMenu = false
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.PlayArrow,
                                    contentDescription = null
                                )
                            }
                        )
                    }
                }

                FabButton_When_Its_EducationFragment(
                    showWarningState = etudiants.isNotEmpty() && !hasUpdateToday,
                    isFabVisible = showFabMenu,
                    its_Targeted_Frag = true,
                    onToggleFabVisibility = { showFabMenu = !showFabMenu },
                    onShowDropdown = { showFabMenu = true }
                )
            }

            // Rendered only when the FAB was tapped — starts hidden.
            if (showFabMenu) {
                FabDropdownMenu_WhenIts_FragmentEducation(
                    aCentralFacade = viewModel,
                    onDismissDropdown = { showFabMenu = false }
                )
            }
        }
        
        if (showFloatingVideo) {
            FloatingDraggableVideoPlayer(
                onDismiss = { showFloatingVideo = false }
            )
        }
    }
}

@Composable
fun ScrollableInformationBanner(
    ousstadName: String,
    modifier: Modifier = Modifier
) {
    var currentBannerIndex by remember { mutableStateOf(0) }
    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    val cardWidth = with(density) { 320.dp.toPx() }
    val totalCards = 3

    LaunchedEffect(Unit) {
        while (true) {
            while (currentBannerIndex < totalCards - 1) {
                delay(1500)
                val totalSteps = 35
                val stepSize = cardWidth / totalSteps
                for (step in 0 until totalSteps) {
                    scrollState.scrollTo(((currentBannerIndex * cardWidth) + (step * stepSize)).toInt())
                    delay(10)
                }
                currentBannerIndex++
            }
            delay(3000)
            val totalSteps = 35
            val maxScroll = (totalCards - 1) * cardWidth
            val stepSize = maxScroll / totalSteps
            for (step in 0 until totalSteps) {
                scrollState.scrollTo((maxScroll - (step * stepSize)).toInt())
                delay(10)
            }
            currentBannerIndex = 0
        }
    }

    Row(
        modifier = modifier
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier
                .width(320.dp)
                .height(140.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ecole_logo1),
                    contentDescription = null,
                    modifier = Modifier
                        .size(140.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(4.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
        Card(
            modifier = Modifier
                .width(320.dp)
                .height(140.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
            )
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "الأستاذ",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        ),
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "قسم حفظة القرآن",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp
                        ),
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Card(
            modifier = Modifier
                .width(320.dp)
                .height(140.dp),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "الأهداف المبتغاة",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "إيصال الغيابات والتقارير للإدارة\nتسهيل تذكر المحفوظ للطلبة",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    ),
                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun EmptyState(
    modifier: Modifier = Modifier,
    isFiltered: Boolean = false
) {
    Column(
        modifier = modifier.padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = if (isFiltered) Icons.Default.Search else Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = if (isFiltered) "لا توجد نتائج" else "لا يوجد طلاب مسجلين",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Text(
            text = if (isFiltered) "جرب البحث بكلمات أخرى" else "سيظهر الطلاب هنا بعد إضافتهم",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp),
            textAlign = TextAlign.Center
        )
    }
}

fun formatDate(timestamp: Long): String {
    return SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(Date(timestamp))
}

fun isSameDay(timestamp1: Long, timestamp2: Long): Boolean {
    val cal1 = Calendar.getInstance().apply { timeInMillis = timestamp1 }
    val cal2 = Calendar.getInstance().apply { timeInMillis = timestamp2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

@Composable
fun FloatingDraggableVideoPlayer(onDismiss: () -> Unit) {
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val density = androidx.compose.ui.platform.LocalDensity.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
    val playerWidthPx = with(density) { 320.dp.toPx() }
    val playerHeightPx = with(density) { 240.dp.toPx() }

    var offsetX by remember { mutableStateOf(screenWidthPx / 2f - playerWidthPx / 2f) }
    var offsetY by remember { mutableStateOf(screenHeightPx / 2f - playerHeightPx / 2f) }

    val context = androidx.compose.ui.platform.LocalContext.current
    val exoPlayer = remember {
        com.google.android.exoplayer2.ExoPlayer.Builder(context).build().apply {
            val uri = android.net.Uri.parse("android.resource://${context.packageName}/raw/presentation_start")
            val mediaItem = com.google.android.exoplayer2.MediaItem.fromUri(uri)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }
    }

    androidx.compose.runtime.DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    Column(
        modifier = Modifier
            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .size(width = 320.dp, height = 270.dp)
            .background(Color.Black, shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
            .border(2.dp, MaterialTheme.colorScheme.primary, androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .background(Color.DarkGray)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.Text(
                text = "Présentation",
                color = Color.White,
                modifier = Modifier.padding(start = 12.dp),
                style = MaterialTheme.typography.labelSmall
            )
            androidx.compose.material3.IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(30.dp)
            ) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Close,
                    contentDescription = "Fermer",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        AndroidView(
            factory = { ctx ->
                com.google.android.exoplayer2.ui.PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = true
                }
            },
            update = { view ->
                view.player = exoPlayer
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
