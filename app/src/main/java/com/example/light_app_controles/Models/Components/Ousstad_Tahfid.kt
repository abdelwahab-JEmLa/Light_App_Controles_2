package EntreApps.Shared.Models.Components

import EntreApps.Shared.Models.Compts
import EntreApps.Shared.Models.M00CentralParametresOfAllApps

enum class Ousstad_Tahfid(
    val ayam_tadriss: String = "dimanch/jeudi",
    val nom_arab: String = "",
    val key: String = ""
) {
    Abdelwahab_Osstad(
        "dimanch/jeudi",
        "عبدالوهاب حمنيش",
        Compts.AbdelwahabTravailleChezGros_KeyId.keyId

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
    ),               //<--
    //TODO(1): cree kissme_talaba_li_dirassatihim_mena_idata       //<--
    //TODO(1): fait que ca soit tou comme  Amine_Madrassa  
    Amine_Madrassa(
        "dimanch/jeudi",
        "أمين",
        M00CentralParametresOfAllApps.get_Default().amine_madrasa_Compt_KeyId
    )
    ;
}

