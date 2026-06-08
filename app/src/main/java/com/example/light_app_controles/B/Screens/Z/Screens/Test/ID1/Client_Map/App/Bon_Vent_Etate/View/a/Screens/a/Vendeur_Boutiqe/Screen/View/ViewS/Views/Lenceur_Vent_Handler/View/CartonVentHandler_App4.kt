package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.a.Screens.a.Vendeur_Boutiqe.Screen.View.ViewS.Views.Lenceur_Vent_Handler.View

import EntreApps.Shared.Models.M00CentralParametresOfAllApps.Companion.ifTrue
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID5.Ancien_PresenterApp_FragID5.Fragment.View.ViewS.FastInit_Outlined_Int_Edite_Modulable_Proto4
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
enum class ActivationTigger(val activation: Boolean = false) {
    Categories16(true),
    CatronAdd(true),
}

@Composable
fun CartonVentHandler_App4(
    activation: Boolean = ActivationTigger.CatronAdd.activation,
    currentCartons: Int,
    depotEnCartons: Int,
    isAvailable: Boolean,
    isAdmin: Boolean,
    compactMode: Boolean,
    containerColor: Color,
    horizontalPadding: Dp,
    verticalPadding: Dp,
    modifier: Modifier = Modifier,
    onVentUpdate: (newCartons: Int) -> Unit,
) {
    val cartonShape = RoundedCornerShape(
        topStart = 12.dp, topEnd = 12.dp,
        bottomStart = 0.dp, bottomEnd = 0.dp
    )
    activation.ifTrue {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .clip(cartonShape)
                .background(containerColor.copy(alpha = 0.10f))
                .padding(horizontal = horizontalPadding, vertical = verticalPadding / 2),
            contentAlignment = Alignment.CenterEnd
        ) {
            FastInit_Outlined_Int_Edite_Modulable_Proto4(
                start_count = currentCartons,
                au_depot = depotEnCartons,
                standard_count = 1,                     // 1 carton par premier clic
                startCouleur = Color(0xFFF44336),
                icon = Icons.Default.Inventory2,
                isAvailable = isAvailable,
                compact_taille = compactMode,
                show_depot_card_on_top_in_flow_row = true,
                is_admin = isAdmin,
                add_spacing_between_depot_and_sale = isAdmin,
                on_Data_Update = onVentUpdate,
            )
        }
    }
}
