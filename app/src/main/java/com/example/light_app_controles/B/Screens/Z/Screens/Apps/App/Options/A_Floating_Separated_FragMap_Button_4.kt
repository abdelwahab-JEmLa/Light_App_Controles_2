package Application5.App.Options

import Application5.App.A_ViewModel_SeparatedAppsCodingPattern
import Application5.App.Repository.M19Etudiant
import Application5.App.Repository.M20ObsarvationEtudion
import Application5.App.View.DropDownItems.View.But10.DropDownItem_Imprime_pdf_collecte_numeros_whatsapp
import Application5.App.View.DropDownItems.View.But2.DropDownItem_Imprime_pdf_communication_ac_parent
import Application5.App.View.DropDownItems.View.But4.DropDownItem_Imprime_pdf_List_Talaba
import Application5.App.View.DropDownItems.View.But5.DropDownItem_Imprime_pdf_Case_A_Cochet
import Application5.App.View.DropDownItems.View.But9.DropDownItem_Send_Cards_WhatsApp_Parent
import Application5.App.View.DropDownItems.View.ButID6.DropDownItem_ID6
import Application5.App.View.DropDownItems.View.ButID8.DropDownItem_ButID8
import EntreApps.Shared.Models.Components.Ousstad_Tahfid
import EntreApps.Shared.Models.Compts.AbdelwahabTravailleChezGros_KeyId
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Modules.Base.AppDatabase
import android.text.format.DateUtils
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AllInbox
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.PersonOff
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.light_app_controles.R
import kotlin.math.roundToInt

data class Button_State(
    val showLabels: Boolean = true,
    val its_Active: Boolean = false,
    val text_Label: String = "",
    val colors: Pair<Color, Color> = Pair(Color.White, Color.White),
    val icons: Pair<ImageVector, ImageVector> = Pair(Icons.Default.Remove, Icons.Default.Add),
    val description_Functionement: String = "",
) {
    companion object {
        fun get_Default(): Button_State = Button_State()
    }
}

@Composable
fun Floating_Separated_Button(
    appDatabase: AppDatabase,
    buttonState: Button_State = Button_State.get_Default().copy(
        text_Label = "",
        icons = Pair(Icons.Default.FilterList, Icons.Default.AllInbox),
        colors = Pair(Color.Red, Color.Blue)
    ),
    vm :    A_ViewModel_SeparatedAppsCodingPattern
) {
    val isShowingAll = true
    val updatedButtonState = buttonState.copy(its_Active = isShowingAll)

    val haptic = LocalHapticFeedback.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp

    var offsetX by remember { mutableFloatStateOf(screenWidth.value - 200f) }
    var offsetY by remember { mutableFloatStateOf(screenHeightDp.value - 300f) }
    var showDropdown by remember { mutableStateOf(false) }


    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX = (offsetX + dragAmount.x).coerceIn(0f, screenWidth.value - 100f)
                        offsetY = (offsetY + dragAmount.y).coerceIn(0f, screenHeightDp.value - 100f)
                    }
                }
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                B_FragMap_DropdownMenu_App5(
                    vm = vm,
                    expanded = showDropdown,
                    onDismiss = { showDropdown = false },
                    appDatabase = appDatabase
                )
            }
        }
    }
}

@Composable
fun FabDropdownMenu_WhenIts_FragmentEducation(
    onDismissDropdown: () -> Unit,
    modifier: Modifier = Modifier,
    aCentralFacade: A_ViewModel_SeparatedAppsCodingPattern,
) {
    val context = LocalContext.current
    var showTextField by remember { mutableStateOf(true) }
    var studentName by remember { mutableStateOf("") }
    var showOussstadSelection by remember { mutableStateOf(false) }

    val repo19 = aCentralFacade.repo19Etudiant
    val activeCentralValues = aCentralFacade.activeCentralValues
    val activeOusstad = activeCentralValues.active_Ousstad_Tahfid

    // Count students not updated today
    val studentsNotUpdatedToday by remember {
        derivedStateOf {
            repo19.datasValue.filter { etudiant ->
                !DateUtils.isToday(etudiant.dernierTimeTampsSynchronisationAvecFireBase)
            }
        }
    }

    // Get active Ousstad and determine parent key
    fun getActiveOussstadKey(): String {
        val params = M00CentralParametresOfAllApps()

        return when (activeOusstad) {
            Ousstad_Tahfid.Abdelwahab_Osstad -> AbdelwahabTravailleChezGros_KeyId.keyId
            Ousstad_Tahfid.Amine_Madrassa -> params.amine_madrasa_Compt_KeyId
            Ousstad_Tahfid.Kissm_Intikali -> "Kissm_Intikali"
            Ousstad_Tahfid.Non_Defini_Actuellemen -> "Non_Defini_Actuellemen"
            null -> AbdelwahabTravailleChezGros_KeyId.keyId // Default fallback
        }
    }

    // ActiveDatas_SeparatedAppsCodingPattern is a @Stable class (not a data class),
    // so it has no .copy(). We write the mutableStateOf field directly instead.
    fun updateActiveOusstad(ousstad: Ousstad_Tahfid) {
        activeCentralValues.active_Ousstad_Tahfid = ousstad

        Toast.makeText(
            context,
            "تم تحديد الأستاذ النشط: ${ousstad.nom_arab}",
            Toast.LENGTH_SHORT
        ).show()

        showOussstadSelection = false
    }

    fun add() {
        if (studentName.isNotBlank()) {
            // Set active Ousstad as parent
            val activeOussstadKey = getActiveOussstadKey()

            val newStudent = M19Etudiant(
                nom = studentName.trim(),
                parent_ousstad_key = activeOussstadKey
            )

            aCentralFacade.add_M19Etudiant(newStudent)

            Toast.makeText(
                context,
                "تمت إضافة الطالب بنجاح",
                Toast.LENGTH_SHORT
            ).show()

            studentName = ""
            showTextField = false
            onDismissDropdown()
        }
    }

    fun markAllAsAbsent() {
        val studentsToMark = studentsNotUpdatedToday

        if (studentsToMark.isEmpty()) {
            Toast.makeText(
                context,
                "جميع الطلاب محدثون اليوم",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        var successCount = 0
        studentsToMark.forEach { etudiant ->
            try {
                val absenceObservation = M20ObsarvationEtudion.Companion.get_default().copy(
                    type = M20ObsarvationEtudion.Type.Raeeb,
                    etudiant_keyID = etudiant.keyID,
                    min_soura = etudiant.dernier_Soura_Wassale_Laha,
                    min_aya = etudiant.dernier_Soura_sater,
                    ila_soura = etudiant.mokarrare_hifde,
                    ila_aya = if (etudiant.mokarrare_hifde_sater == 0) {
                        etudiant.mokarrare_hifde.rakme_akher_aya
                    } else {
                        etudiant.mokarrare_hifde_sater
                    },
                    takyim = M19Etudiant.Takiyim.Lam_Yahfed,
                    parent_ousstad_key = etudiant.parent_ousstad_key,
                    creationTimestamps = System.currentTimeMillis()
                )

                aCentralFacade.upsert_M20ObsarvationEtudion(absenceObservation)
                successCount++
            } catch (e: Exception) {
            }
        }

        Toast.makeText(
            context,
            "تم تسجيل الغياب لـ $successCount طالب",
            Toast.LENGTH_LONG
        ).show()

        onDismissDropdown()
    }

    Box(
        modifier = modifier
            .offset(y = (-90).dp)
    ) {
        DropdownMenu(
            expanded = true,
            onDismissRequest = onDismissDropdown,
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            Card(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "الأستاذ النشط:",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        Text(
                            text = activeOusstad?.nom_arab ?: "غير محدد",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.size(8.dp))

                    DropdownMenuItem(
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        text = {
                            Text(
                                text = "تغيير الأستاذ النشط",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        },
                        onClick = { showOussstadSelection = !showOussstadSelection }
                    )

                    if (showOussstadSelection) {
                        Divider(modifier = Modifier.padding(vertical = 4.dp))

                        Ousstad_Tahfid.values()
                            .forEach { ousstad ->
                                DropdownMenuItem(
                                    leadingIcon = {
                                        if (activeOusstad == ousstad) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        } else {
                                            Spacer(modifier = Modifier.size(20.dp))
                                        }
                                    },
                                    text = {
                                        Text(
                                            text = ousstad.nom_arab,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = if (activeOusstad == ousstad) {
                                                FontWeight.Bold
                                            } else {
                                                FontWeight.Normal
                                            },
                                            color = if (activeOusstad == ousstad) {
                                                MaterialTheme.colorScheme.primary
                                            } else {
                                                MaterialTheme.colorScheme.onSurface
                                            }
                                        )
                                    },
                                    onClick = { updateActiveOusstad(ousstad) }
                                )
                            }
                    }
                }
            }

            Divider()

            Card(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (studentsNotUpdatedToday.isNotEmpty()) {
                        MaterialTheme.colorScheme.errorContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                DropdownMenuItem(
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.PersonOff,
                            contentDescription = null,
                            tint = if (studentsNotUpdatedToday.isNotEmpty()) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    },
                    text = {
                        Text(
                            text = if (studentsNotUpdatedToday.isNotEmpty()) {
                                "تسجيل الغياب للطلاب غير المحدثين (${studentsNotUpdatedToday.size})"
                            } else {
                                "تسجيل الغياب للطلاب غير المحدثين"
                            },
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    onClick = { markAllAsAbsent() },
                    enabled = studentsNotUpdatedToday.isNotEmpty()
                )
            }

            Divider()
            DropDownItem_ButID8(aCentralFacade = aCentralFacade)
            DropDownItem_ID6(aCentralFacade = aCentralFacade)
            DropDownItem_Imprime_pdf_List_Talaba(aCentralFacade = aCentralFacade)
            DropDownItem_Imprime_pdf_communication_ac_parent(viewModel = aCentralFacade)
            DropDownItem_Imprime_pdf_collecte_numeros_whatsapp(aCentralFacade = aCentralFacade)
            DropDownItem_Send_Cards_WhatsApp_Parent(aCentralFacade = aCentralFacade)
            DropDownItem_Imprime_pdf_Case_A_Cochet(aCentralFacade = aCentralFacade)

            Divider()

            if (showTextField) {
                Divider()

                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .width(250.dp)
                ) {
                    OutlinedTextField(
                        value = studentName,
                        onValueChange = { studentName = it },
                        label = { Text("Nom de l'étudiant") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(
                                onClick = { add() },
                                enabled = studentName.isNotBlank()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Confirmer",
                                    tint = if (studentName.isNotBlank())
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FabButton_When_Its_EducationFragment(
    showWarningState: Boolean,
    isFabVisible: Boolean,
    its_Targeted_Frag: Boolean,
    onToggleFabVisibility: () -> Unit,
    onShowDropdown: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .offset(y = (-28).dp)
            .size(56.dp),
        shape = CircleShape,
    ) {
        Box {
            if (showWarningState) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFF9800), // Orange
                                    Color(0xFF4CAF50)  // Green
                                )
                            ),
                            shape = CircleShape
                        )
                        .clickable {
                            when (its_Targeted_Frag) {
                                false -> onToggleFabVisibility()
                                true -> onShowDropdown()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            } else {
                Image(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable {
                            when (its_Targeted_Frag) {
                                false -> onToggleFabVisibility()
                                true -> onShowDropdown()
                            }
                        },
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
                Icon(
                    imageVector = if (isFabVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = "Toggle FAB",
                    modifier = Modifier.align(Alignment.Center),
                    tint = Color.White
                )
            }
        }
    }
}
