package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface GearDao {
    // --- Gear Packs ---
    @Query("SELECT * FROM gear_packs ORDER BY createdAt DESC")
    fun getAllPacks(): Flow<List<GearPackEntity>>

    @Query("SELECT * FROM gear_packs WHERE id = :packId LIMIT 1")
    suspend fun getPackById(packId: Long): GearPackEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPack(pack: GearPackEntity): Long

    @Update
    suspend fun updatePack(pack: GearPackEntity)

    @Query("DELETE FROM gear_packs WHERE id = :packId")
    suspend fun deletePackById(packId: Long)

    // --- Gear Items ---
    @Query("SELECT * FROM gear_items WHERE packId = :packId ORDER BY id ASC")
    fun getItemsForPack(packId: Long): Flow<List<GearItemEntity>>

    @Query("SELECT * FROM gear_items WHERE packId = :packId ORDER BY id ASC")
    suspend fun getItemsForPackOnce(packId: Long): List<GearItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: GearItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<GearItemEntity>)

    @Update
    suspend fun updateItem(item: GearItemEntity)

    @Delete
    suspend fun deleteItem(item: GearItemEntity)

    @Query("DELETE FROM gear_items WHERE id = :itemId")
    suspend fun deleteItemById(itemId: Long)

    @Query("UPDATE gear_items SET isPacked = :isPacked WHERE id = :itemId")
    suspend fun updatePackedStatus(itemId: Long, isPacked: Boolean)

    @Query("DELETE FROM gear_items WHERE packId = :packId")
    suspend fun deleteAllItemsInPack(packId: Long)
}
