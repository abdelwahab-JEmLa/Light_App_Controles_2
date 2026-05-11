package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FloatingMenu_Plus_RoomCsvBigDatas.Feature.Options

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInbox
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FloatingMenu_Plus_RoomCsvBigDatas.Feature.Options.M8Bon_Operations_FragMap_DropdownMenu.Actions.Button_State
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FloatingMenu_Plus_RoomCsvBigDatas.Feature.Options.M8Bon_Operations_FragMap_DropdownMenu.Actions.M8Bon_Operations_FragMap_DropdownMenu
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.a.Screens.a.BonVents.Screen.ViewModel.A_ViewModel
import kotlin.math.roundToInt

@Composable
fun Floating_Separated_Button(
    on_vent_key: String = "",
    buttonState: Button_State = Button_State.Companion.get_Default().copy(
        text_Label = "",
        icons = Pair(Icons.Default.FilterList, Icons.Default.AllInbox),
        colors = Pair(Color.Companion.Red, Color.Companion.Blue)
    ),
    onClick_Lence_Capture: (() -> Unit)? = null,
    viewModel: A_ViewModel,
) {
    val updatedButtonState = buttonState.copy(its_Active = true)

    val haptic = LocalHapticFeedback.current
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeightDp = configuration.screenHeightDp.dp

    var offsetX by remember { mutableFloatStateOf(screenWidth.value - 200f) }
    var offsetY by remember { mutableFloatStateOf(screenHeightDp.value - 300f) }
    var showDropdown by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.Companion.fillMaxSize(),
        contentAlignment = Alignment.Companion.Center
    ) {
        Box(
            modifier = Modifier.Companion
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
                verticalAlignment = Alignment.Companion.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FloatingActionButton(
                    modifier = Modifier.Companion.size(48.dp),
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.Companion.LongPress)
                        showDropdown = true
                    },
                    containerColor = updatedButtonState.colors.second
                ) {
                    Icon(
                        imageVector = updatedButtonState.icons.second,
                        contentDescription = null,
                        tint = Color.Companion.White,
                        modifier = Modifier.Companion.size(24.dp)
                    )
                }

                M8Bon_Operations_FragMap_DropdownMenu(
                    expanded = showDropdown,
                    onDismiss = { showDropdown = false },
                    on_vent_key = on_vent_key,
                    onClick_Lence_Capture = onClick_Lence_Capture,
                    vm = viewModel,
                )
            }
        }
    }
}
