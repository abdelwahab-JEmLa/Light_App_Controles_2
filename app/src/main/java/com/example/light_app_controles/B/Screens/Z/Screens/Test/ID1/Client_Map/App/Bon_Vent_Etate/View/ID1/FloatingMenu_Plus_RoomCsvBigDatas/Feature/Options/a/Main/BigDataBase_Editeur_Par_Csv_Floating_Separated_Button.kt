package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.a.Main
                                                   
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInbox
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlin.math.roundToInt
import com.example.light_app_controles.R
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.M3.Actions.M03_Operations_FragMap_DropdownMenu
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.M2Client_Operations_FragMap_DropdownMenu.Actions.M2Client_Operations_FragMap_DropdownMenu
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.M8Bon_Operations_FragMap_DropdownMenu.Actions.M8Bon_Operations_FragMap_DropdownMenu
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.M1.Actions.M01_FragMap_DropdownMenu
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.M10.Actions.M10_FragMap_DropdownMenu
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.M13.Actions.M13_FragMap_DropdownMenu
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.M14.Actions.M14_FragMap_DropdownMenu
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.a.Main.MultiOperations_FragMap_DropdownMenu
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Options.a.Main.ViewModel.FeatureID1_ViewModel
import EntreApps.Shared.Modules.Base.AppDatabase

private enum class DialState { Closed, ChildsVisible, M8Open, M03Open, M2Open, M1_OpertaionsDatasRow_Open, M10Open, M13Open, M14Open, MultiOperationsOpen }  //<--

@Composable
fun FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button(
    on_vent_key: String = "",
    onClick_Lence_Capture: (() -> Unit)? = null,
    context: Context = LocalContext.current,
    appDatabase: AppDatabase ,
    viewModel: FeatureID1_ViewModel = viewModel(
        factory = viewModelFactory { initializer { FeatureID1_ViewModel(appDatabase = appDatabase,context) } }
    )
) {
    val haptic = LocalHapticFeedback.current
    var dialState by remember { mutableStateOf(DialState.Closed) }

    val configuration  = LocalConfiguration.current
    val density        = LocalDensity.current
    val screenWidthPx  = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }
    val fabSizePx      = with(density) { 58.dp.toPx() }
    val paddingPx      = with(density) { 16.dp.toPx() }

    var offsetX by remember { mutableFloatStateOf(screenWidthPx  - fabSizePx - paddingPx) }
    var offsetY by remember { mutableFloatStateOf(screenHeightPx - fabSizePx * 5f) }

    val logoRotation by animateFloatAsState(
        targetValue   = if (dialState != DialState.Closed) 45f else 0f,
        animationSpec = tween(250),
        label         = "logoRot",
    )

    Box(
        modifier         = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopStart,
    ) {
        Column(
            modifier = Modifier
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    val width = placeable.width
                    val height = placeable.height
                    val xOffset = offsetX - (width - fabSizePx - paddingPx).coerceAtLeast(0f)
                    val yOffset = offsetY - (height - fabSizePx - paddingPx).coerceAtLeast(0f)
                    
                    android.util.Log.i("BigDataBase_FAB", "layout - width: $width, height: $height, state: $dialState, offsetX: $offsetX, xOffset: $xOffset, offsetY: $offsetY, yOffset: $yOffset")
                    
                    layout(width, height) {
                        placeable.placeRelative(xOffset.roundToInt(), yOffset.roundToInt())
                    }
                }
                .padding(end = 16.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {

            AnimatedVisibility(
                visible = dialState != DialState.Closed,
                enter   = fadeIn(tween(200)) + slideInVertically(tween(220)) { it },
                exit    = fadeOut(tween(150)) + slideOutVertically(tween(150)) { it },
            ) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text       = "Multi Operations",
                            style      = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color      = Color.White,
                            modifier   = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF37474F).copy(alpha = 0.92f))
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                        )
                        Box {
                            FloatingActionButton(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    dialState = DialState.MultiOperationsOpen
                                },
                                modifier       = Modifier.size(46.dp),
                                containerColor = Color(0xFF37474F),
                                shape          = CircleShape,
                                elevation      = FloatingActionButtonDefaults.elevation(4.dp),
                            ) {
                                Icon(
                                    imageVector        = Icons.Default.AllInbox,
                                    contentDescription = "Multi",
                                    tint               = Color.White,
                                    modifier           = Modifier.size(22.dp),
                                )
                            }
                            MultiOperations_FragMap_DropdownMenu(
                                expanded = dialState == DialState.MultiOperationsOpen,
                                onDismiss = { dialState = DialState.Closed },
                                vm = viewModel,
                            )
                        }
                    }
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text       = "M1 Produit",
                            style      = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color      = Color.White,
                            modifier   = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF2E7D32).copy(alpha = 0.92f))
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                        )
                        Box {
                            FloatingActionButton(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    dialState = DialState.M1_OpertaionsDatasRow_Open
                                },
                                modifier       = Modifier.size(46.dp),
                                containerColor = Color(0xFF2E7D32),
                                shape          = CircleShape,
                                elevation      = FloatingActionButtonDefaults.elevation(4.dp),
                            ) {
                                Icon(
                                    imageVector        = Icons.Default.AllInbox,
                                    contentDescription = "M1",
                                    tint               = Color.White,
                                    modifier           = Modifier.size(22.dp),
                                )
                            }
                            M01_FragMap_DropdownMenu(
                                expanded = dialState == DialState.M1_OpertaionsDatasRow_Open,
                                onDismiss = { dialState = DialState.Closed },
                                on_vent_key = on_vent_key,
                                onClick_Lence_Capture = onClick_Lence_Capture,
                                vm = viewModel,
                            )
                        }
                    }
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text       = "M10 Operation",
                            style      = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color      = Color.White,
                            modifier   = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFE91E63).copy(alpha = 0.92f))
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                        )
                        Box {
                            FloatingActionButton(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    dialState = DialState.M10Open
                                },
                                modifier       = Modifier.size(46.dp),
                                containerColor = Color(0xFFE91E63),
                                shape          = CircleShape,
                                elevation      = FloatingActionButtonDefaults.elevation(4.dp),
                            ) {
                                Icon(
                                    imageVector        = Icons.Default.AllInbox,
                                    contentDescription = "M10",
                                    tint               = Color.White,
                                    modifier           = Modifier.size(22.dp),
                                )
                            }
                            M10_FragMap_DropdownMenu(
                                expanded = dialState == DialState.M10Open,
                                onDismiss = { dialState = DialState.Closed },
                                vm = viewModel,
                            )
                        }
                    }
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text       = "M13 Tarification",
                            style      = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color      = Color.White,
                            modifier   = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF9C27B0).copy(alpha = 0.92f))
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                        )
                        Box {
                            FloatingActionButton(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    dialState = DialState.M13Open
                                },
                                modifier       = Modifier.size(46.dp),
                                containerColor = Color(0xFF9C27B0),
                                shape          = CircleShape,
                                elevation      = FloatingActionButtonDefaults.elevation(4.dp),
                            ) {
                                Icon(
                                    imageVector        = Icons.Default.AllInbox,
                                    contentDescription = "M13",
                                    tint               = Color.White,
                                    modifier           = Modifier.size(22.dp),
                                )
                            }
                            M13_FragMap_DropdownMenu(
                                expanded = dialState == DialState.M13Open,
                                onDismiss = { dialState = DialState.Closed },
                                vm = viewModel,
                            )
                        }
                    }
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text       = "M14 VentPeriode",
                            style      = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color      = Color.White,
                            modifier   = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF3F51B5).copy(alpha = 0.92f))
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                        )
                        Box {
                            FloatingActionButton(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    dialState = DialState.M14Open
                                },
                                modifier       = Modifier.size(46.dp),
                                containerColor = Color(0xFF3F51B5),
                                shape          = CircleShape,
                                elevation      = FloatingActionButtonDefaults.elevation(4.dp),
                            ) {
                                Icon(
                                    imageVector        = Icons.Default.AllInbox,
                                    contentDescription = "M14",
                                    tint               = Color.White,
                                    modifier           = Modifier.size(22.dp),
                                )
                            }
                            M14_FragMap_DropdownMenu(
                                expanded = dialState == DialState.M14Open,
                                onDismiss = { dialState = DialState.Closed },
                                vm = viewModel,
                            )
                        }
                    }
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text       = "M2 Client",
                            style      = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color      = Color.White,
                            modifier   = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFE65100).copy(alpha = 0.92f))
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                        )
                        Box {
                            FloatingActionButton(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    dialState = DialState.M2Open
                                },
                                modifier       = Modifier.size(46.dp),
                                containerColor = Color(0xFFE65100),
                                shape          = CircleShape,
                                elevation      = FloatingActionButtonDefaults.elevation(4.dp),
                            ) {
                                Icon(
                                    imageVector        = Icons.Default.AllInbox,
                                    contentDescription = "M2",
                                    tint               = Color.White,
                                    modifier           = Modifier.size(22.dp),
                                )
                            }
                            M2Client_Operations_FragMap_DropdownMenu(
                                expanded = dialState == DialState.M2Open,
                                onDismiss = { dialState = DialState.Closed },
                                on_vent_key = on_vent_key,
                                onClick_Lence_Capture = onClick_Lence_Capture,
                                vm = viewModel,
                            )
                        }
                    }
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text       = "M8 BonVent",
                            style      = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color      = Color.White,
                            modifier   = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF1565C0).copy(alpha = 0.92f))
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                        )
                        Box {
                            FloatingActionButton(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    dialState = DialState.M8Open
                                },
                                modifier       = Modifier.size(46.dp),
                                containerColor = Color(0xFF1565C0),
                                shape          = CircleShape,
                                elevation      = FloatingActionButtonDefaults.elevation(4.dp),
                            ) {
                                Icon(
                                    imageVector        = Icons.Default.AllInbox,
                                    contentDescription = "M8",
                                    tint               = Color.White,
                                    modifier           = Modifier.size(22.dp),
                                )
                            }
                            M8Bon_Operations_FragMap_DropdownMenu(
                                expanded = dialState == DialState.M8Open,
                                onDismiss = { dialState = DialState.Closed },
                                on_vent_key = on_vent_key,
                                onClick_Lence_Capture = onClick_Lence_Capture,
                                vm = viewModel,
                            )
                        }
                    }

                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text       = "M03 Couleur",
                            style      = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color      = Color.White,
                            modifier   = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF6A1B9A).copy(alpha = 0.92f))
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                        )
                        Box {
                            FloatingActionButton(
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    dialState = DialState.M03Open
                                },
                                modifier       = Modifier.size(46.dp),
                                containerColor = Color(0xFF6A1B9A),
                                shape          = CircleShape,
                                elevation      = FloatingActionButtonDefaults.elevation(4.dp),
                            ) {
                                Icon(
                                    imageVector        = Icons.Default.Palette,
                                    contentDescription = "M03",
                                    tint               = Color.White,
                                    modifier           = Modifier.size(22.dp),
                                )
                            }
                            M03_Operations_FragMap_DropdownMenu(
                                expanded              = dialState == DialState.M03Open,
                                onDismiss             = { dialState = DialState.Closed },
                                on_vent_key           = on_vent_key,
                                onClick_Lence_Capture = onClick_Lence_Capture,
                                vm                    = viewModel,
                            )
                        }
                    }
                }
            }

            FloatingActionButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    val newState = if (dialState == DialState.Closed)
                        DialState.ChildsVisible
                    else
                        DialState.Closed
                    dialState = newState
                    android.util.Log.i("BigDataBase_FAB", "FAB clicked - New state: $newState")
                },
                modifier       = Modifier
                    .size(58.dp)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            offsetX = (offsetX + dragAmount.x).coerceIn(0f, screenWidthPx  - fabSizePx - paddingPx)
                            offsetY = (offsetY + dragAmount.y).coerceIn(0f, screenHeightPx - fabSizePx - paddingPx)
                        }
                    },
                containerColor = Color.Transparent,
                elevation      = FloatingActionButtonDefaults.elevation(6.dp, 6.dp),
                shape          = CircleShape,
            ) {
                if (dialState != DialState.Closed) {
                    Box(
                        modifier         = Modifier
                            .size(58.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF37474F)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector        = Icons.Default.Close,
                            contentDescription = "Fermer",
                            tint               = Color.White,
                            modifier           = Modifier
                                .size(26.dp)
                                .rotate(logoRotation),
                        )
                    }
                } else {
                    Image(
                        painter            = painterResource(id = R.drawable.logo),
                        contentDescription = "Menu",
                        contentScale       = ContentScale.Crop,
                        modifier           = Modifier
                            .size(58.dp)
                            .clip(CircleShape),
                    )
                }
            }
        }
    }
}
