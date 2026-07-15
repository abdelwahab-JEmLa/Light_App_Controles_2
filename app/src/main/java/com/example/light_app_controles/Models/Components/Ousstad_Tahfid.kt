package EntreApps.Shared.Models.Components

import EntreApps.Shared.Models.Compts
import EntreApps.Shared.Models.M00CentralParametresOfAllApps

enum class Ousstad_Tahfid(
    val ayam_tadriss: String = "dimanch/jeudi",
    val nom_arab: String = "",
    val key: String = "",
    val num: String = "",
    val login_nom: String = "",
    val login_mp: String = "",
    val its_non_admine_ousstade: Boolean = false,
) {
    Abdelwahab_Osstad(
        "dimanch/jeudi",
        "عبدالوهاب حمنيش",
        Compts.AbdelwahabTravailleChezGros_KeyId.keyId,
        "+213 553885037",
        //<--
        "ab",
        ""
    ),
    Non_Defini_Actuellemen(
        "dimanch/jeudi",
        "غير محدد حاليا",
        "Non_Defini_Actuellemen"
    ),
    Kissm_Intikali(
        "dimanch/jeudi",
        "قسم انتقالي",
        "Kissm_Intikali"
    ),
    kissme_talaba_li_dirassatihim_mena_idata(
        "dimanch/jeudi",
        "قسم طلبة للدراسة من الادارة",
        M00CentralParametresOfAllApps.get_Default().kissme_talaba_li_dirassatihim_mena_idata_Compt_KeyId,
        "",
        "",
        "",
    ),
    kissme_talaba_tam_tahwilahom_mena_idata(
        "dimanch/jeudi",
        "تم تحويلهم من الادارة",
        "kissme_talaba_tam_tahwilahom_mena_idata",
        "",
        "",
        "",
    ),
    Amine_Madrassa(
        "dimanch/mardi/jeudi",
        "أمين",
        M00CentralParametresOfAllApps.get_Default().amine_madrasa_Compt_KeyId,
        "+213 553885037",
        "امين",
        "0000",
        true
    )
    ;
}

