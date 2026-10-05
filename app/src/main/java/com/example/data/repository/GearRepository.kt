package com.example.data.repository

import com.example.data.db.GearDao
import com.example.data.db.GearItemEntity
import com.example.data.db.GearPackEntity
import com.example.data.model.PresetTemplate
import com.example.data.model.PresetTemplates
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class GearRepository(private val gearDao: GearDao) {

    val allPacks: Flow<List<GearPackEntity>> = gearDao.getAllPacks()

    fun getItemsForPack(packId: Long): Flow<List<GearItemEntity>> {
        return gearDao.getItemsForPack(packId)
    }

    suspend fun getPackById(packId: Long): GearPackEntity? {
        return gearDao.getPackById(packId)
    }

    suspend fun getItemsForPackOnce(packId: Long): List<GearItemEntity> {
        return gearDao.getItemsForPackOnce(packId)
    }

    suspend fun createNewPack(name: String, description: String, bodyWeightKg: Float = 70.0f): Long {
        val pack = GearPackEntity(
            name = name,
            description = description,
            userBodyWeightKg = bodyWeightKg
        )
        return gearDao.insertPack(pack)
    }

    suspend fun updatePack(pack: GearPackEntity) {
        gearDao.updatePack(pack)
    }

    suspend fun deletePack(packId: Long) {
        gearDao.deletePackById(packId)
    }

    suspend fun insertItem(item: GearItemEntity): Long {
        return gearDao.insertItem(item)
    }

    suspend fun updateItem(item: GearItemEntity) {
        gearDao.updateItem(item)
    }

    suspend fun deleteItem(itemId: Long) {
        gearDao.deleteItemById(itemId)
    }

    suspend fun togglePackedStatus(itemId: Long, isPacked: Boolean) {
        gearDao.updatePackedStatus(itemId, isPacked)
    }

    suspend fun createPackFromPreset(template: PresetTemplate, bodyWeightKg: Float = 70.0f): Long {
        val packId = createNewPack(
            name = template.title,
            description = template.subtitle + " - " + template.description,
            bodyWeightKg = bodyWeightKg
        )

        val itemsToInsert = template.items.map { item ->
            GearItemEntity(
                packId = packId,
                name = item.name,
                category = item.category.displayName,
                weightGrams = item.weightGrams,
                quantity = item.quantity,
                isPacked = true,
                notes = item.notes
            )
        }

        gearDao.insertItems(itemsToInsert)
        return packId
    }

    suspend fun duplicatePack(sourcePackId: Long, newName: String): Long? {
        val sourcePack = gearDao.getPackById(sourcePackId) ?: return null
        val sourceItems = gearDao.getItemsForPackOnce(sourcePackId)

        val newPackId = createNewPack(
            name = newName,
            description = "複製自 ${sourcePack.name}",
            bodyWeightKg = sourcePack.userBodyWeightKg
        )

        val duplicatedItems = sourceItems.map {
            it.copy(id = 0, packId = newPackId)
        }
        gearDao.insertItems(duplicatedItems)
        return newPackId
    }

    suspend fun ensureDefaultPackExists(): Long {
        val packs = allPacks.firstOrNull()
        if (!packs.isNullOrEmpty()) {
            return packs.first().id
        }

        // Initialize default pack: 三天兩夜
        val defaultPreset = PresetTemplates.ALL_PRESETS.find { it.title.contains("三天兩夜") }
            ?: PresetTemplates.ALL_PRESETS.first()

        return createPackFromPreset(defaultPreset, 70.0f)
    }
}
