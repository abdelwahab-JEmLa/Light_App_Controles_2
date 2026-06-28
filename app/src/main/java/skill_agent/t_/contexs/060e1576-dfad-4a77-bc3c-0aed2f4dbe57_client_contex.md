# Contexte d'Isolation du TODO

**Fichier Principal :** `C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src\main\java\com\example\light_app_controles\B\Screens\Z\Screens\Test\ID1\Client_Map\App\Bon_Vent_Etate\View\a\Screens\a\Vendeur_Boutiqe\Screen\View\ViewS\Views\Screen_Panie_FragID2.kt`
**Cible :** Port du composable `Screen_Panie_FragID2` et adaptation à l'architecture locale.
**Projet :** Kotlin / Jetpack Compose / Android
**Composable parent :** `Screen_Panie_FragID2`

---

## 1. Résumé exécutif (pour IA vierge)
Le composable `Screen_Panie_FragID2` a été copié du projet `ClientJetPack` vers `Light_App_Controles`. Ce composable représente l'écran du panier d'achats. 
Il contient des imports obsolètes et des injections de dépendances (Koin, contrôles WiFi) propres à l'application cliente d'origine.
Le TODO consiste à adapter cette classe pour qu'elle s'intègre proprement à l'architecture locale de la Light App, résolve ses imports de ressources locales et de modèles, et compile avec succès.

## 2. Plan d'implémentation détaillé (Step-by-step)
1. **Ajuster le package** : Changer le package déclaré de `package Application4.App.Fragment.ID2.Fragment` vers `package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.a.Screens.a.Vendeur_Boutiqe.Screen.View.ViewS.Views`.
2. **Corriger les Imports** : 
   - Remplacer les imports de modèles/facades par les modèles locaux de `EntreApps.Shared.Models`.
   - Utiliser `com.example.light_app_controles.R` pour les ressources.
3. **Remplacer les injections Koin** : 
   - Supprimer les appels à `koinInject()` et `koinViewModel()`.
   - Passer ces dépendances (comme le ViewModel ou la Facade) en paramètres du composable ou utiliser des liaisons locales.
4. **Nettoyer les contrôles WiFi** :
   - Supprimer les dépendances et appels à `WifiTransferDatas_ControllerApp` car la Light App ne gère pas le transfert de données en WiFi.
5. **Résoudre la compilation** : Exécuter la commande de compilation et corriger les éventuelles erreurs de types ou de références.

## 3. Variables et types accessibles dans le scope du TODO
| Variable | Type | Comment y accéder | Description |
|---|---|---|---|
| `appDatabase` | `AppDatabase` | Paramètre du composable | Base de données Room locale |
| `active_Central_Values` | `ActiveCentralValues` | Scope local / ViewModel | États actifs partagés |
| `fastSearchProduitPourVent` | `String` | Scope local | Chaîne de recherche saisie par l'utilisateur |

## 4. Fichiers Relatifs Impliqués

### 4.1. `a_Compact_Presentoire_App_Produits_FragID4.kt`
- **Chemin :** `C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src\main\java\com\example\light_app_controles\B\Screens\Z\Screens\Test\ID1\Client_Map\App\Bon_Vent_Etate\View\a\Screens\a\Vendeur_Boutiqe\Screen\a_Compact_Presentoire_App_Produits_FragID4.kt`
- **Rôle :** Point d'entrée de l'écran vendeur compact. Utile pour voir comment le ViewModel `A_ViewModel_NewProtoPatterns` et `Setter_LongDatas` sont instanciés localement sans Koin.

### 4.2. `A_ViewModel_NewProtoPatterns.kt`
- **Chemin :** `C:\Users\Abou Mohamed\AndroidStudioProjects\Light_App_Controles\app\src\main\java\com\example\light_app_controles\B\Screens\Z\Screens\Test\ID1\Client_Map\App\Bon_Vent_Etate\View\a\Screens\a\Vendeur_Boutiqe\Screen\ViewModel\A_ViewModel_NewProtoPatterns.kt`
- **Rôle :** ViewModel local gérant les états et les actions sur les produits, ventes et tarifs.

## 5. Code environnant LARGE (30+ lignes avant/après le TODO)
```kotlin
package Application4.App.Fragment.ID2.Fragment

// ... (imports) ...

@Composable
fun Screen_Panie_FragID2(
    fragmentNavigationHandler: FragmentNavigationHandler_NewProto,
    panelsGroupeButtonHandler: PanelsGroupeButtonHandler = koinInject(),
    aCentralFacade: ACentralFacade = koinInject(),
    wifiTransferDatas_ControllerApp: WifiTransferDatas_ControllerApp,
    list_M13TarificationInfos: List<M13TarificationInfos>,
) {
    // ...
}
```

## 6. Imports déjà présents dans le fichier principal
```kotlin
import Application4.App.Main.A.Navigation.Component.FragmentNavigationHandler_NewProto
import Application4.App.Modules.Wi.Module.WifiTransferDatas_ControllerApp
import EntreApps.Shared.Models.Home.ActiveCentralValues
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.M09AppCompt
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import EntreApps.Shared.Modules.Base.AppDatabase
import P0_MainScreen.Main.Main.Settings.FWinID1.AbdelwahabEBoutiquePressistantsOverAll.Windows.But_4_FloatingSearchFAB.PressistatntMainActivityButtons_Sec8FWinID1
import V.DiviseParSections.App.B.ClientUisView.App.FragID.MapClients.Fragment.Windows.A_MarkerStatusDialog.Windows.MarkerStatusDialog
import V.DiviseParSections.App.B.ClientUisView.App.FragID2.PanierFinaleDAchat.Fragment.B.View.DetailBonVent.View.Options.petitePaddine
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.Dialogs.Dialog_Fast_Affiche_Panie.Dialogs.Dialog_Fast_Affiche_Panie
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.MainFilterT1
import V.DiviseParSections.App.SectionID10.PresenterElectroBoutiqueAbdelwahab.App.FragID2.FastSeach.Fragment.ViewModel.ViewModelMainFastSearchProduitPourVent
import V.DiviseParSections.App.Shared.Repository.A.Base.ACentralFacade
import V.DiviseParSections.App.Shared.Repository.A.Base.FocusedValues.Base.Get.Download.FocusedValuesGetter
import V.DiviseParSections.App.Shared.Repository.A.Base.MainRepositoys.Base.Get.Download.RepositorysMainGetter.Companion.ifTrue
import Z_CodePartageEntreApps.Modules.PanelsGroupeButtonHandler
```

## 7. Imports supplémentaires potentiellement nécessaires pour le fix
```kotlin
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.a.Screens.a.Vendeur_Boutiqe.Screen.ViewModel.A_ViewModel_NewProtoPatterns
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.ID1.FeatureID1_BigDataBase_Editeur_Par_Csv_Floating_Separated_Button.Feature.Modules.Setter_LongDatas
```

## 8. PATTERN À COPIER
L'implémentation de la recherche réactive et de l'affichage OutlinedTextField a été corrigée dans le fichier d'origine de `ClientJetPack` comme suit :
```kotlin
OutlinedTextField(
    value = fastSearchProduitPourVent,
    onValueChange = { newText ->
        val capitalizedText = newText.split(" ").joinToString(" ") { word ->
            if (word.isNotEmpty()) {
                word.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase() else it.toString()
                }
            } else {
                word
            }
        }
        update_activeCentralValuesfastSearchProduitPourVent(capitalizedText)
    },
    modifier = Modifier
        .fillMaxWidth()
        .focusRequester(focusRequester),
    placeholder = {
        Text("Rechercher un produit... (min. 3 caractères)")
    },
    singleLine = true,
    keyboardOptions = KeyboardOptions(
        imeAction = ImeAction.Done
    ),
    keyboardActions = KeyboardActions(
        onDone = {
            searchJob?.cancel()
            update_activeCentralValuesfastSearchProduitPourVent("")
        }
    )
)
```

## 9. Contraintes et pièges à éviter
- Ne pas utiliser Koin pour instancier les ViewModels dans la Light App.
- Retirer les liaisons réseaux WiFi (`WifiTransferDatas_ControllerApp`).
- Ajuster les routes et packages vers `com.example.light_app_controles`.

## 10. Règles d'Isolation (Ignore Rules)
```text
*
!*/
!*skill_agent/**
!*Screen_Panie_FragID2.kt
!*a_Compact_Presentoire_App_Produits_FragID4.kt
!*A_ViewModel_NewProtoPatterns.kt
```
