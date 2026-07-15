package Application5.App.Options

import EntreApps.Shared.Models.Components.Ousstad_Tahfid
import EntreApps.Shared.Models.Compts
import EntreApps.Shared.Models.M00CentralParametresOfAllApps

// Get active Ousstad and determine parent key
fun getActiveOussstadKey(activeOusstadTahfid: Ousstad_Tahfid?): String {
    val params = M00CentralParametresOfAllApps()

    return when (activeOusstadTahfid) {
        Ousstad_Tahfid.Abdelwahab_Osstad -> Compts.AbdelwahabTravailleChezGros_KeyId.keyId
        Ousstad_Tahfid.Amine_Madrassa -> params.amine_madrasa_Compt_KeyId
        Ousstad_Tahfid.kissme_talaba_li_dirassatihim_mena_idata -> params.kissme_talaba_li_dirassatihim_mena_idata_Compt_KeyId
        Ousstad_Tahfid.kissme_talaba_tam_tahwilahom_mena_idata -> "kissme_talaba_tam_tahwilahom_mena_idata"
        Ousstad_Tahfid.Kissm_Intikali -> "Kissm_Intikali"
        Ousstad_Tahfid.Non_Defini_Actuellemen -> "Non_Defini_Actuellemen"
        null -> Compts.AbdelwahabTravailleChezGros_KeyId.keyId // Default fallback
    }
}
