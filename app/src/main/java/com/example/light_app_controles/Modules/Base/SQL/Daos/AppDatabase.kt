package com.example.light_app_controles.Modules.Base.SQL.Daos

import EntreApps.Shared.Models.AppType
import EntreApps.Shared.Models.M00CentralParametresOfAllApps
import EntreApps.Shared.Models.M09AppCompt
import EntreApps.Shared.Models.Relative_Produits.Models.M01Produit
import EntreApps.Shared.Models.Relative_Produits.Models.M16CategorieProduit
import EntreApps.Shared.Models.Relative_Produits.Models.M3CouleurProduitInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M10OperationVentCouleur
import EntreApps.Shared.Models.Relative_Vents.Models.M13TarificationInfos
import EntreApps.Shared.Models.Relative_Vents.Models.M15Grossist
import EntreApps.Shared.Models.Relative_Vents.Models.M2Client
import EntreApps.Shared.Modules.Base.SQL.Dao_M03CouleurProduitInfos
import EntreApps.Shared.Modules.Base.SQL.Dao_M16CategorieProduit
import EntreApps.Shared.Modules.Base.SQL.Dao_M1Produit
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import Working_IN.Feature.Models.M14VentPeriode
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.b.Models.M8BonVent
import java.util.Date

@Database(
    entities = [
        M01Produit::class,
        M2Client::class,

        M8BonVent::class,

        //Sorted ID
        M3CouleurProduitInfos::class,
        M13TarificationInfos::class,
        M10OperationVentCouleur::class,
        M14VentPeriode::class,
        M15Grossist::class,
        M00CentralParametresOfAllApps::class,
        M16CategorieProduit::class,

        M09AppCompt::class,
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(
    DateConverter::class,
    AppTypeConverter::class,
    TypeChoisiConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao_M1Produit(): Dao_M1Produit
    abstract fun dao_M2Client(): Dao_M2Client

    abstract fun dao_M03CouleurProduitInfos(): Dao_M03CouleurProduitInfos
    abstract fun dao_M8BonVent(): Dao_M8BonVent
    abstract fun dao_16CategorieProduit(): Dao_M16CategorieProduit

    abstract fun dao_M9AppCompt(): Dao_M9AppCompt
    object DatabaseModule {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration() // Safe for dev — replace with Migration() in production
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// Add this converter class anywhere in the file
class TypeChoisiConverter {
    @TypeConverter
    fun fromTypeChoisi(value: M13TarificationInfos.TypeChoisi?): String? = value?.name

    @TypeConverter
    fun toTypeChoisi(value: String?): M13TarificationInfos.TypeChoisi {
        if (value.isNullOrBlank()) return M13TarificationInfos.TypeChoisi.Historique
        return try {
            M13TarificationInfos.TypeChoisi.valueOf(value)
        } catch (e: IllegalArgumentException) {
            M13TarificationInfos.TypeChoisi.Historique // stale / unknown value → safe default
        }
    }
}

class AppTypeConverter {
    @TypeConverter
    fun fromAppType(value: AppType?): String? = value?.name

    @TypeConverter
    fun toAppType(value: String?): AppType? =
        value?.let { name -> AppType.entries.firstOrNull { it.name == name } }
}

class DateConverter {
    @TypeConverter
    fun toDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }

    @TypeConverter
    fun fromDate(date: Date?): Long? {
        return date?.time
    }
}
