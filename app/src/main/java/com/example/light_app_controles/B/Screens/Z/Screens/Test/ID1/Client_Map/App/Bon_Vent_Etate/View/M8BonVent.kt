package com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View

//noinspection SuspiciousImport,SuspiciousImport
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.Relative_Vents.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Firebase
import com.google.firebase.database.IgnoreExtraProperties
import com.google.firebase.database.database
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Objects
import kotlin.collections.filter
import androidx.compose.ui.graphics.Color

@Entity
data class M8BonVent(
    @PrimaryKey
    var keyID: String = generePushKey(),
    var creationTimestamps: Long = System.currentTimeMillis(),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),
    var confirmeCommande_TimeTamp: Long = 0,
    var pourcentage_AffichageDuCatalogue_Conficerie: Double = 0.0,
    var pourcentage_AffichageDuCatalogue_Cosmitiques: Double = 0.0,
    var pourcentage_AffichageDuCatalogue_tebnage: Double = 0.0,

    val nombre_produits_don_dernier_pdf_stoked: Int = 0,
    val last_sort_pdf_locale_totale_a_paye: Double = 0.0,
    val path_pdf_bon_file: String = "",

    var parent_M9AppCompt_KeyID: String = "null",
    var parent_M9AppCompt_DebugInfos: String = "null",
    var parent_M14VentPeriod_KeyId: String = "null",
    var parent_M14VentPeriod_DebugInfos: String = "null",
    var parent_M2Client_KeyID: String = "null",
    var parent_M2Client_DebugInfos: String = "null",
    var parent_M2Client_OldLongID: Long = 0L,
    var parent_M17Message_KeyID: String = "null",
    var parent_M17Message_DebugInfos: String = "null",
    var its_Confirmation_de_TransactionKeyId: String = "",
    var heurDebutInString: String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
    var heurFinInString: String = "Non Defini",
    var its_working_for_wholesaler: Boolean = false,
    var etateActuellementEst: EtateActuellementEst = EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT,
    var vocaleKeyID: String = "",
    var sonVocaleEstEcoute: Boolean = false,
    var sonEcoutementEstFaitAutimestamps: Long = 0,
    var totale_saved: Double = 0.0,
    var cUn_Versement_duBonVentKey: String = "",
    var vala_supp: Int = 0,
    var a_etai_imprime_au_moi_ne_foit: Boolean = false,
    var versement_fait: Double = 0.0,
    var ancien_credit: Double = 0.0,




    var cUn_Credit_duBonVentKey: String = "",
    var new_credit_apre_tout_fait: Double = 0.0,
    var affiche_le_verssement_au_prochen_print: Boolean = false,
    var demande_Versemet_si_Type: Double = 0.0,
    var demande_Versemet_si_Type_est_regle: Boolean = false,
    var credit_fait: Double = 0.0,
    var sum_De_Totale_Vents: Double = 0.0,
    var sum_De_Credit_Fait: Double = 0.0,
    var versement: Double = 0.0,
    var position_Don_Lis_Cible_Clients_au_VentPeriod: Int = 0,
    var cLeDataOuvertDuParentList: Boolean? = null,
    var cActive: Boolean = false,
    val parentID8C2TypeTransactionKeyByParent: String = "",
    var vid: Long = 0L,
    var moulahada: String = "",    //06_27
    var new_situation: Double = 0.0, //06_21
) {
    /**
     * Calculates the primary monetary value for this bon depending on its state:
     * - New_Situation_Credit → Σ credit_fait  −  Σ versement_fait  (for same client + period)
     * - Versemment           → versement_fait
     * - Credit / Cette_Transaction_Type_Est_Credit → credit_fait
     * - Demande_Versemet     → demande_Versemet_si_Type
     * - everything else      → 0.0
     */
    fun fun_calculative_du_main_val(allBons: List<M8BonVent>): Double {
        val samePeriodClientBons = allBons.filter {
            it.parent_M2Client_KeyID == this.parent_M2Client_KeyID &&
                    it.parent_M14VentPeriod_KeyId == this.parent_M14VentPeriod_KeyId
        }
        return when (etateActuellementEst) {
            EtateActuellementEst.New_Situation_Credit -> {
                val sumCredits = samePeriodClientBons
                    .filter {
                        it.etateActuellementEst == EtateActuellementEst.Credit ||
                                it.etateActuellementEst == EtateActuellementEst.Cette_Transaction_Type_Est_Credit
                    }
                    .sumOf { it.credit_fait }
                val sumVersements = samePeriodClientBons
                    .filter { it.etateActuellementEst == EtateActuellementEst.Versemment }
                    .sumOf { it.versement_fait }
                sumCredits - sumVersements
            }
            EtateActuellementEst.Versemment -> versement_fait
            EtateActuellementEst.Credit,
            EtateActuellementEst.Cette_Transaction_Type_Est_Credit -> credit_fait
            EtateActuellementEst.Demande_Versemet -> demande_Versemet_si_Type
            else -> 0.0
        }
    }


    fun get_DebugInfos(): String {
        return buildString {
            append("Bon")
            append("[")
            append("p.cli->")
            append(parent_M2Client_DebugInfos)
            append(") ")
            append("[")
            append(keyID.takeLast(4))
            append("])")
        }
    }



    @IgnoreExtraProperties
    enum class EtateActuellementEst(val color: Color, val nomArabe: String) {
        CreeMaisNonDefinie(Color(0xFFFFFFFF), "غير محدد"),
        ON_MODE_COMMEND_ACTUELLEMENT(
            Color(0xFF99CC00),
            " تنفيذ المطلوب في تحسين الوضع معه"
        ),
        Rapport_Entre_On_Etate_De_Bloquage(
            Color(0xFFFF4444),
            ":تقرير الدخول معه في حالة انسداد في التجارة بسبب"
        ),
        Bloque_Probleme(Color(0xFFCC0000), "حدث مشكل معه"),
        Ordre_Gerant(Color(0xFFCC0000), "توجيه المسير"),
        A_COMMANDE_CONFIRME(Color(0xFF9933CC), "تم تاكيد الطلبية"),
        COMMANDE_LIVRAI(Color(0xFF0099CC), "تم أيصال منتجاته"),

        ACHETEUR_NON_DISPO(Color(0xFFCC0000), "الشاري غائب"),
        AVEC_MARCHANDISE(Color(0xFFCC0000), "عندو سلعة"),
        FERME(Color(0xFF444444), "مغلق"),
        Cible(Color(0xFFFF6700), "معين من المسير"),
        CIBLE_PRIORITE_2(Color(0xFFFF6700), "CIBLE_PRIORITE_2"),
        CIBLE_PRIORITE_3(Color(0xFF99CC00), "CIBLE_PRIORITE_3"),
        CIBLE_POUR_2(Color(0xFF0099CC), "CIBLE_POUR_2"),
        PourVoirPanie(Color(0xFFFF4444), "للنظر"),
        RAPPORT_AU_ENREGESTREMENT_VOCALE(Color(0xFF000000), "التقرير قي التسجيل الصوتي "),
        ON_MODE_VOIRE_PANIE_ARTICLES(Color(0xFF0099CC), "في معاينة السلة"),
        A_EVITE(Color(0xFF99CC00), "اقترح ان يتجنب لمدة اسبوعين"),
        PASSE(Color(0xFFCC0000), "اقترح ان يؤجل الى مدة قادمة"),
        CommantaireSpeciale(Color(0xFFCC0000), "ملاحظة خاصة بالطلبية"),
        Passed_Sans_Livre(Color(0xFF444444), "Passed_Sans_Livre"),
        //Credits
        Credit(Color(0xFFCC0000), " "),
        Cette_Transaction_Type_Est_Credit(Color(0xFFCC0000), "تم اقراضه  "),
        Versemment(Color(0xFF4CAF50), ""),
        Demande_Versemet(Color(0xFFFF9800), "المبلغ المرجو تحظيره"),
        New_Situation_Credit(Color(0xFF673AB7), "الحالة الجديدة للدين"),
        ;
        companion object {
            const val keyModel = "ID8C2"
        }
    }

    fun isSameEntity(other: M8BonVent) =
        keyID == other.keyID && parent_M9AppCompt_KeyID == other.parent_M9AppCompt_KeyID && parent_M14VentPeriod_KeyId == other.parent_M14VentPeriod_KeyId

    override fun equals(other: Any?) =
        this === other || (other is M8BonVent && isSameEntity(other))

    override fun hashCode() = Objects.hash(
        keyID, parent_M9AppCompt_DebugInfos, parent_M14VentPeriod_KeyId
    )

    companion object {
        const val keyModel = "ID8"

        fun remove_ref() {
            ref.removeValue()
        }

        val ref = Firebase.database.getReference(
            "/00_DataPrototype-04-02/_1_developingRef/C_InfosSqlDataBases"
        ).child("Datas08BonVent")

        fun generePushKey() = M00CentralParametresOfAllApps.genereUnPushKeyFireBase(ref)

        fun get_default2(): M8BonVent {
            return M8BonVent()
        }

        fun get_default(
            parent_M9AppCompt_KeyID: String,
            parent_M9AppCompt_DebugInfos: String,
            parent_M14VentPeriod_DebugInfos: String,
            parent_M14VentPeriod_KeyId: String,
            parent_M2Client_KeyID: String,
            parent_M2Client_DebugInfos: String,
            etateActuellementEst: EtateActuellementEst? = null,
        ): M8BonVent {
            return M8BonVent(
                parent_M9AppCompt_DebugInfos = parent_M9AppCompt_DebugInfos,
                parent_M9AppCompt_KeyID = parent_M9AppCompt_KeyID,
                parent_M14VentPeriod_DebugInfos = parent_M14VentPeriod_DebugInfos,
                parent_M14VentPeriod_KeyId = parent_M14VentPeriod_KeyId,
                parent_M2Client_KeyID = parent_M2Client_KeyID,
                parent_M2Client_DebugInfos = parent_M2Client_DebugInfos,
                etateActuellementEst = etateActuellementEst
                    ?: EtateActuellementEst.ON_MODE_COMMEND_ACTUELLEMENT
            )
        }

        fun find_By_MainValuesKeys_Depuit_List(
            data_List: List<M8BonVent>,
            parent_M14VentPeriod_KeyId: String,
            parent_M2Client_KeyID: String,
            relative_Etate: EtateActuellementEst? = null,
        ) = data_List
            .find { data ->
                val match_MainValuesKeys =
                    data.parent_M14VentPeriod_KeyId == parent_M14VentPeriod_KeyId
                            && data.parent_M2Client_KeyID == parent_M2Client_KeyID
                            && data.etateActuellementEst == relative_Etate
                match_MainValuesKeys
            }


        fun M8BonVent.sum_totale_et_benifice(
            vents: List<M10OperationVentCouleur>,
            tariffs: List<M13TarificationInfos>,
        ): Sums_Bons {
            var totale = 0.0
            var benifices = 0.0

            vents
                .filter { it.parent_M8BonVent_KeyId == this.keyID }
                .forEach { op ->
                    val prixAchat: Double? = tariffs
                        .filter {
                            it.parent_M1Produit_KeyId == op.parent_M1Produit_KeyId &&
                                    it.typeChoisi == M13TarificationInfos.TypeChoisi.Tariff_ItsWorkInGrossist_SuperGros
                        }
                        .maxByOrNull { it.dernierTimeTampsSynchronisationAvecFireBase }
                        ?.prixCurrency

                    totale += op.prix_de_Vent_entre_directement_NewProto * op.quantity
                    // Skip benefit calculation if purchase price is null or zero
                    if (prixAchat != null && prixAchat != 0.0) {
                        benifices += (op.prix_de_Vent_entre_directement_NewProto - prixAchat) * op.quantity
                    }
                }

            return Sums_Bons(totale_vents = totale, benifices_vents = benifices)
        }

        fun M8BonVent.benifice(
            vents: List<M10OperationVentCouleur>,
            tariffs: List<M13TarificationInfos>,
        ): Double = sum_totale_et_benifice(vents, tariffs).benifices_vents

        fun M8BonVent.sum_totale_vents(
            vents: List<M10OperationVentCouleur>,
            tariffs: List<M13TarificationInfos>,
        ): Double = sum_totale_et_benifice(vents, tariffs).totale_vents
    }
}

data class Sums_Bons(
    val totale_vents: Double,
    val benifices_vents: Double,
)
