package Application5.App.View.DropDownItems.View.ButID8

import Application5.App.A_ViewModel_SeparatedAppsCodingPattern
import Application5.App.Repository.Data.Repo19Etudiant
import Application5.App.Repository.Data.Repo20ObsarvationEtudion
import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun DropDownItem_ButID8(
    nomFun: String = "Toggler_Affich_Mois_Tahfid",
    aCentralFacade: A_ViewModel_SeparatedAppsCodingPattern,
    repo19Etudiant: Repo19Etudiant = aCentralFacade.repo19Etudiant,
    repo20Observation: Repo20ObsarvationEtudion = aCentralFacade.repo20ObsarvationEtudion,
    context: Context = LocalContext.current
) {
    val activeStudentsCount = remember(repo19Etudiant.datasValue) {
        repo19Etudiant.datasValue.count { !it.exclue_de_l_affiche_au_classe }
    }

    // FIXED: Toggle dialog visibility when clicked
    IconButton(
        onClick = {
            aCentralFacade.update_activeDatas(
                displaye_dialog_mois_moinAcPlus_6_du_current =
                    !aCentralFacade.activeCentralValues.displaye_dialog_mois_moinAcPlus_6_du_current
            )
        }
    ) {
        Icon(
            imageVector = Icons.Default.CalendarMonth,
            contentDescription = "Afficher mois tahfid"
        )
    }
}
