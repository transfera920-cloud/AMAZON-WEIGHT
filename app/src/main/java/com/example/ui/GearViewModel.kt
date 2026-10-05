package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiWeightAnalyzer
import com.example.data.db.AppDatabase
import com.example.data.db.GearItemEntity
import com.example.data.db.GearPackEntity
import com.example.data.model.GearCategory
import com.example.data.model.PresetTemplate
import com.example.data.repository.GearRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class WeightRatioStatus(val label: String, val colorHex: Long) {
    REASONABLE("🟢 合理 (<=20%)", 0xFF2E7D32),
    SLIGHTLY_HEAVY("🟡 偏重 (20%-25%)", 0xFFF57F17),
    RECOMMEND_REDUCE("🔴 建議減重 (>25%)", 0xFFC62828);

    companion object {
        fun evaluate(totalPackKg: Float, bodyWeightKg: Float): WeightRatioStatus {
            if (bodyWeightKg <= 0f) return REASONABLE
            val ratio = (totalPackKg / bodyWeightKg) * 100f
            return when {
                ratio <= 20.0f -> REASONABLE
                ratio <= 25.0f -> SLIGHTLY_HEAVY
                else -> RECOMMEND_REDUCE
            }
        }
    }
}

data class WeightSummary(
    val totalWeightGrams: Int = 0,
    val baseWeightGrams: Int = 0,
    val consumableWeightGrams: Int = 0,
    val bodyWeightKg: Float = 70.0f,
    val ratioPercent: Float = 0.0f,
    val status: WeightRatioStatus = WeightRatioStatus.REASONABLE
) {
    val totalWeightKg: Float get() = totalWeightGrams / 1000.0f
    val baseWeightKg: Float get() = baseWeightGrams / 1000.0f
    val consumableWeightKg: Float get() = consumableWeightGrams / 1000.0f
}

data class CategoryWeight(
    val category: GearCategory,
    val totalGrams: Int,
    val percentage: Float
)

data class ComparisonResult(
    val packAName: String,
    val packBName: String,
    val packATotalGrams: Int,
    val packBTotalGrams: Int,
    val packABaseGrams: Int,
    val packBBaseGrams: Int,
    val diffTotalGrams: Int,
    val diffBaseGrams: Int
)

@OptIn(ExperimentalCoroutinesApi::class)
class GearViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GearRepository
    private val aiAnalyzer = GeminiWeightAnalyzer()

    val allPacks: StateFlow<List<GearPackEntity>>

    private val _selectedPackId = MutableStateFlow<Long?>(null)
    val selectedPackId: StateFlow<Long?> = _selectedPackId.asStateFlow()

    private val _currentPack = MutableStateFlow<GearPackEntity?>(null)
    val currentPack: StateFlow<GearPackEntity?> = _currentPack.asStateFlow()

    val currentItems: StateFlow<List<GearItemEntity>>

    // Dialog & UI state
    private val _isAiAnalyzing = MutableStateFlow(false)
    val isAiAnalyzing: StateFlow<Boolean> = _isAiAnalyzing.asStateFlow()

    private val _aiAnalysisResult = MutableStateFlow<String?>(null)
    val aiAnalysisResult: StateFlow<String?> = _aiAnalysisResult.asStateFlow()

    private val _comparisonResult = MutableStateFlow<ComparisonResult?>(null)
    val comparisonResult: StateFlow<ComparisonResult?> = _comparisonResult.asStateFlow()

    init {
        val database = AppDatabase.getInstance(application)
        repository = GearRepository(database.gearDao())

        allPacks = repository.allPacks.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        currentItems = _selectedPackId.flatMapLatest { packId ->
            if (packId != null && packId > 0) {
                repository.getItemsForPack(packId)
            } else {
                flowOf(emptyList())
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        viewModelScope.launch {
            val defaultId = repository.ensureDefaultPackExists()
            selectPack(defaultId)
        }
    }

    fun selectPack(packId: Long) {
        _selectedPackId.value = packId
        viewModelScope.launch {
            _currentPack.value = repository.getPackById(packId)
        }
    }

    fun updateBodyWeight(newWeightKg: Float) {
        val pack = _currentPack.value ?: return
        val updated = pack.copy(userBodyWeightKg = newWeightKg)
        _currentPack.value = updated
        viewModelScope.launch {
            repository.updatePack(updated)
        }
    }

    fun createPack(name: String, description: String, bodyWeightKg: Float = 70.0f) {
        viewModelScope.launch {
            val newId = repository.createNewPack(name, description, bodyWeightKg)
            selectPack(newId)
        }
    }

    fun createFromPreset(preset: PresetTemplate, bodyWeightKg: Float = 70.0f) {
        viewModelScope.launch {
            val newId = repository.createPackFromPreset(preset, bodyWeightKg)
            selectPack(newId)
        }
    }

    fun duplicateCurrentPack(newName: String) {
        val currentId = _selectedPackId.value ?: return
        viewModelScope.launch {
            val newId = repository.duplicatePack(currentId, newName)
            if (newId != null) {
                selectPack(newId)
            }
        }
    }

    fun deleteCurrentPack() {
        val currentId = _selectedPackId.value ?: return
        viewModelScope.launch {
            repository.deletePack(currentId)
            val remaining = allPacks.value.filter { it.id != currentId }
            if (remaining.isNotEmpty()) {
                selectPack(remaining.first().id)
            } else {
                val newDefault = repository.ensureDefaultPackExists()
                selectPack(newDefault)
            }
        }
    }

    fun addItem(
        name: String,
        category: GearCategory,
        weightGrams: Int,
        quantity: Int = 1,
        isPacked: Boolean = true,
        notes: String = ""
    ) {
        val packId = _selectedPackId.value ?: return
        viewModelScope.launch {
            val newItem = GearItemEntity(
                packId = packId,
                name = name,
                category = category.displayName,
                weightGrams = weightGrams,
                quantity = quantity,
                isPacked = isPacked,
                notes = notes
            )
            repository.insertItem(newItem)
        }
    }

    fun updateItem(item: GearItemEntity) {
        viewModelScope.launch {
            repository.updateItem(item)
        }
    }

    fun deleteItem(itemId: Long) {
        viewModelScope.launch {
            repository.deleteItem(itemId)
        }
    }

    fun toggleItemPacked(itemId: Long, currentPacked: Boolean) {
        viewModelScope.launch {
            repository.togglePackedStatus(itemId, !currentPacked)
        }
    }

    // Weight Calculations
    fun calculateSummary(items: List<GearItemEntity>, bodyWeightKg: Float): WeightSummary {
        val packed = items.filter { it.isPacked }
        var totalGrams = 0
        var baseGrams = 0
        var consumableGrams = 0

        packed.forEach { item ->
            val weight = item.weightGrams * item.quantity
            totalGrams += weight
            val category = GearCategory.fromDisplayName(item.category)
            if (category.isConsumable) {
                consumableGrams += weight
            } else {
                baseGrams += weight
            }
        }

        val totalKg = totalGrams / 1000.0f
        val ratio = if (bodyWeightKg > 0f) (totalKg / bodyWeightKg) * 100f else 0f
        val status = WeightRatioStatus.evaluate(totalKg, bodyWeightKg)

        return WeightSummary(
            totalWeightGrams = totalGrams,
            baseWeightGrams = baseGrams,
            consumableWeightGrams = consumableGrams,
            bodyWeightKg = bodyWeightKg,
            ratioPercent = ratio,
            status = status
        )
    }

    fun calculateCategoryBreakdown(items: List<GearItemEntity>): List<CategoryWeight> {
        val packed = items.filter { it.isPacked }
        val totalGrams = packed.sumOf { it.weightGrams * it.quantity }
        if (totalGrams == 0) return GearCategory.entries.map { CategoryWeight(it, 0, 0f) }

        val categoryMap = packed.groupBy { GearCategory.fromDisplayName(it.category) }

        return GearCategory.entries.map { cat ->
            val catItems = categoryMap[cat] ?: emptyList()
            val catGrams = catItems.sumOf { it.weightGrams * it.quantity }
            val percentage = (catGrams.toFloat() / totalGrams.toFloat()) * 100f
            CategoryWeight(
                category = cat,
                totalGrams = catGrams,
                percentage = percentage
            )
        }
    }

    // AI Analysis Trigger
    fun runAiAnalysis() {
        val pack = _currentPack.value ?: return
        val items = currentItems.value
        _isAiAnalyzing.value = true
        _aiAnalysisResult.value = null

        viewModelScope.launch {
            val result = aiAnalyzer.analyzeGear(pack.name, pack.userBodyWeightKg, items)
            _aiAnalysisResult.value = result
            _isAiAnalyzing.value = false
        }
    }

    fun clearAiResult() {
        _aiAnalysisResult.value = null
    }

    // Gear Comparison (Plan A vs Plan B)
    fun comparePacks(packAId: Long, packBId: Long) {
        viewModelScope.launch {
            val packA = repository.getPackById(packAId) ?: return@launch
            val packB = repository.getPackById(packBId) ?: return@launch
            val itemsA = repository.getItemsForPackOnce(packAId).filter { it.isPacked }
            val itemsB = repository.getItemsForPackOnce(packBId).filter { it.isPacked }

            val totalA = itemsA.sumOf { it.weightGrams * it.quantity }
            val totalB = itemsB.sumOf { it.weightGrams * it.quantity }

            val baseA = itemsA.filter { !GearCategory.fromDisplayName(it.category).isConsumable }
                .sumOf { it.weightGrams * it.quantity }
            val baseB = itemsB.filter { !GearCategory.fromDisplayName(it.category).isConsumable }
                .sumOf { it.weightGrams * it.quantity }

            _comparisonResult.value = ComparisonResult(
                packAName = packA.name,
                packBName = packB.name,
                packATotalGrams = totalA,
                packBTotalGrams = totalB,
                packABaseGrams = baseA,
                packBBaseGrams = baseB,
                diffTotalGrams = totalB - totalA,
                diffBaseGrams = baseB - baseA
            )
        }
    }

    fun clearComparisonResult() {
        _comparisonResult.value = null
    }

    // Formatted Text Export for sharing
    fun generateExportText(): String {
        val pack = _currentPack.value ?: return ""
        val items = currentItems.value
        val summary = calculateSummary(items, pack.userBodyWeightKg)
        val categories = calculateCategoryBreakdown(items)

        val sb = StringBuilder()
        sb.append("🏔️ 【${pack.name}】亞馬遜高山裝備重量配置\n")
        if (pack.description.isNotBlank()) sb.append("📝 ${pack.description}\n")
        sb.append("-----------------------------------\n")
        sb.append("⚖️ 總重量：${summary.totalWeightGrams} g (${String.format("%.2f", summary.totalWeightKg)} kg)\n")
        sb.append("🎒 基礎重量 (Base Weight)：${summary.baseWeightGrams} g (${String.format("%.2f", summary.baseWeightKg)} kg)\n")
        sb.append("🍎 消耗品重量 (Consumables)：${summary.consumableWeightGrams} g (${String.format("%.2f", summary.consumableWeightKg)} kg)\n")
        sb.append("👤 體重：${pack.userBodyWeightKg} kg | 負重比例：${String.format("%.1f", summary.ratioPercent)}% [${summary.status.label}]\n")
        sb.append("-----------------------------------\n")
        sb.append("📊 各分類重量統計：\n")

        categories.filter { it.totalGrams > 0 }.forEach { cat ->
            sb.append("• ${cat.category.displayName}：${cat.totalGrams} g (${String.format("%.1f", cat.percentage)}%)\n")
        }

        sb.append("-----------------------------------\n")
        sb.append("📋 攜帶裝備明細：\n")

        val grouped = items.filter { it.isPacked }.groupBy { GearCategory.fromDisplayName(it.category) }
        grouped.forEach { (cat, catItems) ->
            sb.append("\n【${cat.displayName}】\n")
            catItems.forEach { item ->
                val totalG = item.weightGrams * item.quantity
                sb.append(" - ${item.name}：${item.weightGrams}g x${item.quantity} = ${totalG}g ${if (item.notes.isNotBlank()) "(${item.notes})" else ""}\n")
            }
        }

        sb.append("\n-- 由「亞馬遜登山裝備重量分析工具」產生 --")
        return sb.toString()
    }
}
