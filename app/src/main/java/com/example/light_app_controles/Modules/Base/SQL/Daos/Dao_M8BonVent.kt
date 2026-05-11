package com.example.light_app_controles.Modules.Base.SQL.Daos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.light_app_controles.B.Screens.Z.Screens.Test.ID1.Client_Map.App.Bon_Vent_Etate.View.b.Models.M8BonVent
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao_M8BonVent {
    @Query("DELETE FROM M8BonVent WHERE keyID = :keyId")
    suspend fun deleteByKeyId(keyId: String)

    @Upsert
    suspend fun upsert(data: M8BonVent)

    @Insert(onConflict = OnConflictStrategy.Companion.ABORT)
    suspend fun insertAll(datas: List<M8BonVent>)

    @Delete
    suspend fun delete(data: M8BonVent)

    @Update
    suspend fun update(data: M8BonVent)

    @Insert(onConflict = OnConflictStrategy.Companion.ABORT)
    suspend fun insert(data: M8BonVent): Long

    @Query("SELECT COUNT(*) FROM M8BonVent")
    suspend fun getCount(): Int

    @Query("SELECT COUNT(*) FROM M8BonVent")
    suspend fun isTableEmpty(): Boolean = getCount() == 0

    @Query("SELECT * FROM M8BonVent")
    suspend fun getAll(): MutableList<M8BonVent>

    @Query("SELECT * FROM M8BonVent")
    fun getAllFlow(): Flow<List<M8BonVent>>

    @Query("SELECT * FROM M8BonVent WHERE keyID = :keyId LIMIT 1")
    fun getFlow_ByKeyID(keyId: String): Flow<M8BonVent?>

    @Query("DELETE FROM M8BonVent")
    suspend fun deleteAll()

    @Delete
    suspend fun deleteData(data: M8BonVent)
}
