package EntreApps.Shared.Models.Relative_Vents.Models

import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.M00CentralParametresOfAllApps.Companion.central_MainDataBases_RefProduction
import androidx.room.Entity
import androidx.room.PrimaryKey
import EntreApps.Shared.Models.Relative_Vents.Models.M8BonVent.Companion.sum_totale_et_benifice
import java.io.File

@Entity
data class M14VentPeriode(
    @PrimaryKey
    var keyID: String = generePushKey(),
    var creationTimestamp: Long = System.currentTimeMillis(),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),

    val abdelmounen_Doit_Etre_Ici: Boolean = false,


    //---------------------------------Forging Keys.Parent VentPeriod----------------------------------------------------------------------------------------------------------------------------------
    var parent_M9AppCompt_KeyID: String = "",
    var parent_M9AppCompt_DebugInfos: String = "",
    //----------------------------------------------------------------------------------------------------------------------------------------------------------------

    val son_verification_entre_vent_et_achat_est_fait: Boolean = true,
    // Section StatuesMutable
    val credit_Vents_Totale: Double = 0.0,
    val cash_Vents_Totale: Double = 0.0,

    val credit_achats_Totale: Double = 0.0,
    val cash_achats_Totale: Double = 0.0,

    val credit_produitsAuDepot: Double = 0.0,
    val valeur_Produits_depuit_Ancien_Vent_Period: Double = 0.0,
    val acheter_produitsAuDepot: Double = 0.0,

    val pre_fraits_voiture_essance_marche_et_paprasse: Double = 0.0,

    val saved_balance: Double = 0.0,

    var etateActuellementEst: EtateActuellementEst =
        EtateActuellementEst.SoquetteNonDefinie,

    ) {
    fun toFirebaseMap(): Map<String, Any?> = mapOf(
        "keyID" to keyID,
        "creationTimestamp" to creationTimestamp,
        "dernierTimeTampsSynchronisationAvecFireBase" to dernierTimeTampsSynchronisationAvecFireBase,
        "abdelmounen_Doit_Etre_Ici" to abdelmounen_Doit_Etre_Ici,
        "parent_M9AppCompt_KeyID" to parent_M9AppCompt_KeyID,
        "parent_M9AppCompt_DebugInfos" to parent_M9AppCompt_DebugInfos,
        "son_verification_entre_vent_et_achat_est_fait" to son_verification_entre_vent_et_achat_est_fait,
        "credit_Vents_Totale" to credit_Vents_Totale,
        "cash_Vents_Totale" to cash_Vents_Totale,
        "credit_achats_Totale" to credit_achats_Totale,
        "cash_achats_Totale" to cash_achats_Totale,
        "credit_produitsAuDepot" to credit_produitsAuDepot,
        "valeur_Produits_depuit_Ancien_Vent_Period" to valeur_Produits_depuit_Ancien_Vent_Period,
        "acheter_produitsAuDepot" to acheter_produitsAuDepot,
        "pre_fraits_voiture_essance_marche_et_paprasse" to pre_fraits_voiture_essance_marche_et_paprasse,
        "saved_balance" to saved_balance,
        "etateActuellementEst" to etateActuellementEst.name,
    )

    fun get_DebugInfos(): String {
        return buildString {
            append(keyID.takeLast(3))
        }
    }

    enum class EtateActuellementEst {
        SoquetteNonDefinie,
        CONFIRME,
    }

    companion object {
        const val nam_Model_Str = "M14VentPeriode"
        val ref = central_MainDataBases_RefProduction.child(nam_Model_Str)

        val ref_Test = ref
        val csv_test = File(
            M00CentralParametresOfAllApps.central_Local_Csv,
            "TestDatas/$nam_Model_Str.csv"
        )

        fun remove_ref() {
            ref.removeValue()
        }

        fun generePushKey() = M00CentralParametresOfAllApps.genereUnPushKeyFireBase(ref)

        fun get_Default() = M14VentPeriode()

        /**
         * Aggregates sales and profit data from all bons in this period.
         *
         * @param bonsList List of M8BonVent that belong to this period
         * @param ventsList List of all M10OperationVentCouleur
         * @param tariffsList List of all M13TarificationInfos for cost calculation
         * @return Aggregated Sums_Data for the entire period
         */
        fun M14VentPeriode.sum_vent_et_benifice(
            bonsList: List<M8BonVent>,
            ventsList: List<M10OperationVentCouleur>,
            tariffsList: List<M13TarificationInfos>,
        ): Sums_Data {
            var totalVentes = 0.0
            var totalBenifices = 0.0
            var onCommandBons = 0
            var creditsBons = 0.0
            var creditSum = 0.0
            var cashSum = 0.0

            // Filter bons that belong to this period
            val periodBons = bonsList.filter { it.parent_M14VentPeriod_KeyId == this.keyID }

            periodBons.forEach { bon ->
                val bonSums = bon.sum_totale_et_benifice(ventsList, tariffsList)
                totalVentes += bonSums.totale_vents
                totalBenifices += bonSums.benifices_vents

                when (bon.etateActuellementEst) {
                    M8BonVent.EtateActuellementEst.Credit,
                    M8BonVent.EtateActuellementEst.Credit -> {
                        creditsBons += 1.0
                        creditSum += if (bon.credit_fait > 0.0) bon.credit_fait else bonSums.totale_vents
                    }

                    M8BonVent.EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT,
                    M8BonVent.EtateActuellementEst.A_COMMANDE_CONFIRME -> {
                        onCommandBons++
                        cashSum += bonSums.totale_vents
                    }

                    else -> {
                        cashSum += bonSums.totale_vents
                    }
                }
            }

            return Sums_Data(
                totale_vents = totalVentes,
                totale_benifices = totalBenifices,
                on_command_bons = onCommandBons,
                credits_bons = creditsBons,
                credit_sum = creditSum,
                totale_cash = cashSum,
            )
        }
    }
}

/**
 * Aggregated data for a sales period (M14VentPeriode).
 * Contains totals across all bons in the period.
 */
data class Sums_Data(
    val totale_vents: Double,
    val totale_benifices: Double,
    val on_command_bons: Int,
    val credits_bons: Double,
    val credit_sum: Double,
    val totale_cash: Double,
)
