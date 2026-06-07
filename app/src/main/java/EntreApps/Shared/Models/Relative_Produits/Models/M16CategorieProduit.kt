package EntreApps.Shared.Models.Relative_Produits.Models

import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.M00CentralParametresOfAllApps.Companion.central_MainDataBases_RefProduction
import EntreApps.Shared.Models.M09AppCompt
import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.File

@Entity
data class M16CategorieProduit(
    @PrimaryKey
    val id: Long = System.currentTimeMillis(),
    var bsonObjectId: String = M00CentralParametresOfAllApps.getPushFireBase(M09AppCompt.ref),
    var keyID: String = generePushKey(),
    var creationTimestamp: Long = System.currentTimeMillis(),
    var dernierTimeTampsSynchronisationAvecFireBase: Long = System.currentTimeMillis(),

    val catalogueParentId: Long = 0,
    val parentCatalogueIdObject: String = "",

    var nom: String = "",

    var position: Int = 0,

    var positionDouble: Double = 0.0,

    var displayedHeader: Boolean = false,

    val itsHeldPourDeplacement: Boolean = false,

    var cSelectionePourDeplace: Boolean = false,
) {
    fun withDernierTimeTampsSynchronisationAvecFireBase(): M16CategorieProduit {
        return this.copy(
            dernierTimeTampsSynchronisationAvecFireBase = System.currentTimeMillis()
        )
    }

    fun toFirebaseMap(): Map<String, Any?> = mapOf(
        "id"                                            to id,
        "bsonObjectId"                                  to bsonObjectId,
        "keyID"                                         to keyID,
        "creationTimestamp"                             to creationTimestamp,
        "dernierTimeTampsSynchronisationAvecFireBase"   to dernierTimeTampsSynchronisationAvecFireBase,
        "catalogueParentId"                             to catalogueParentId,
        "parentCatalogueIdObject"                       to parentCatalogueIdObject,
        "nom"                                           to nom,
        "position"                                      to position,
        "positionDouble"                                to positionDouble,
        "displayedHeader"                               to displayedHeader,
        "itsHeldPourDeplacement"                        to itsHeldPourDeplacement,
        "cSelectionePourDeplace"                        to cSelectionePourDeplace,
    )

    companion object {
        const val nam_Model_Str = "M16CategorieProduit"

        val ref = central_MainDataBases_RefProduction.child(nam_Model_Str)

        val ref_Test = ref
        val csv_test = File(
            M00CentralParametresOfAllApps.central_Local_Csv,
            "TestDatas/$nam_Model_Str.csv"
        )

        fun safeRemoveRef(): Unit {
            ref.removeValue()
        }

        fun generePushKey() =
            ref.push().key ?: throw IllegalStateException("Failed to generate Firebase key")

        fun get_default(
        ): M16CategorieProduit {
            val data = M16CategorieProduit()
            return data
        }

        fun logCategory(category: M16CategorieProduit, TAG: String) {
            Log.d(
                TAG, "Category selected for displacement processed: " +
                        "ID=${category.id}, Name='${category.nom}', " +
                        "CatalogueParentId=${category.catalogueParentId}, " +
                        "Position=${category.position}, " +
                        "${category.cSelectionePourDeplace}, " +
                        "Timestamp=${category.dernierTimeTampsSynchronisationAvecFireBase}"
            )
        }

        /**
         * Keeps only categories whose [id] appears in [activeProductCategoryIds] —
         * i.e. the distinct [M01Produit.idParentCategorie] values from products
         * that survived [M01Produit.filter_passive].
         */
        fun List<M16CategorieProduit>.filter_passive(
            activeProductCategoryIds: List<Long>,
        ): List<M16CategorieProduit> = filter { it.id in activeProductCategoryIds }
    }
}
