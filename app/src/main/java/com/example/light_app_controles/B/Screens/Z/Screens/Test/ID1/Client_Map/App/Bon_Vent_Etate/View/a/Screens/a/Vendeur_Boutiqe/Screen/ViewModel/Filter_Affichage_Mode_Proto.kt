package Application4.App.Fragment.ID1.Fragment.ViewModel

import Application4.App.Fragment.ID1.Fragment.ProductListFilterLogic
enum class Filter_Affichage_Mode_Proto(val mais_sort_order : ProductListFilterLogic.Sort_Order =
                                           ProductListFilterLogic.Sort_Order.Produits_Grouped_Par_Categories) {
    Tablette_Produits_Seulement,
    Echants_Seulement,
    Tablette_Et_Echants,
    Panie_Si_Couleur_Ac_Vent_Affiche_Tout_Ces_Freres(ProductListFilterLogic.Sort_Order.Vents_Creation),
    Panie(ProductListFilterLogic.Sort_Order.Vents_Creation),
}
