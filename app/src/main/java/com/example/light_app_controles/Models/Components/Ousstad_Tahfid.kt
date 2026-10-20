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
    val qr_image_nom: String = "",
) {
    Mounir_Osstad(
        "dimanch/jeudi",
        "منير",
        Compts.Mounir_Oustad_KeyId.keyId,
        "+213  559 51 62 65",
        "منير",
        "1111",
        true,
        "منير.jpg"
    ),
    Abdelwahab_Osstad(
        "dimanch/jeudi",
        "عبدالوهاب حمنيش",
        Compts.AbdelwahabTravailleChezGros_KeyId.keyId,
        "+213 553885037",
        "ab",
        "4321",
        false,
        "ab.png"
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
    Abdelmoumen_Madrassa(
        "lundi/mardi",
        "عبدالمومن فرحي",
        M00CentralParametresOfAllApps.get_Default().abdelmomen_madrasa_Compt_KeyId,
        "+213 776 47 48 27",
        "عبدالمومن",
        "1234",
        true,
        "عبدالمومن.jpeg"
    )
    ;
}
