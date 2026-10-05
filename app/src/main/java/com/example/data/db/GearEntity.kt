package com.example.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "gear_packs")
data class GearPackEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String = "",
    val userBodyWeightKg: Float = 70.0f,
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "gear_items",
    foreignKeys = [
        ForeignKey(
            entity = GearPackEntity::class,
            parentColumns = ["id"],
            childColumns = ["packId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["packId"])]
)
data class GearItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val packId: Long,
    val name: String,
    val category: String,
    val weightGrams: Int,
    val quantity: Int = 1,
    val isPacked: Boolean = true,
    val notes: String = ""
)
