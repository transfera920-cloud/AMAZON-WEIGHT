package com.example.data.api

import com.example.BuildConfig
import com.example.data.db.GearItemEntity
import com.example.data.model.GearCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiWeightAnalyzer {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun analyzeGear(
        packName: String,
        bodyWeightKg: Float,
        items: List<GearItemEntity>
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY" || apiKey == "YOUR_GEMINI_API_KEY") {
            return@withContext generateLocalRuleBasedAnalysis(packName, bodyWeightKg, items)
        }

        val prompt = buildAnalysisPrompt(packName, bodyWeightKg, items)

        try {
            // Build Gemini REST API payload via JSONObject
            val partObj = JSONObject().put("text", prompt)
            val partsArray = JSONArray().put(partObj)
            val contentObj = JSONObject().put("parts", partsArray)
            val contentsArray = JSONArray().put(contentObj)
            val requestJson = JSONObject().put("contents", contentsArray).toString()

            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(requestJson.toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrBlank()) {
                val jsonResponse = JSONObject(responseBody)
                val candidates = jsonResponse.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val content = candidate.optJSONObject("content")
                    if (content != null) {
                        val parts = content.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            val text = parts.getJSONObject(0).optString("text")
                            if (!text.isNullOrBlank()) {
                                return@withContext text
                            }
                        }
                    }
                }
            }
            generateLocalRuleBasedAnalysis(packName, bodyWeightKg, items)
        } catch (e: Exception) {
            generateLocalRuleBasedAnalysis(packName, bodyWeightKg, items)
        }
    }

    private fun buildAnalysisPrompt(
        packName: String,
        bodyWeightKg: Float,
        items: List<GearItemEntity>
    ): String {
        val totalGrams = items.sumOf { if (it.isPacked) it.weightGrams * it.quantity else 0 }
        val baseGrams = items.sumOf {
            val cat = GearCategory.fromDisplayName(it.category)
            if (it.isPacked && !cat.isConsumable) it.weightGrams * it.quantity else 0
        }
        val consumableGrams = totalGrams - baseGrams

        val itemListString = items.filter { it.isPacked }.joinToString("\n") {
            "- ${it.name} (${it.category}): ${it.weightGrams}g x${it.quantity} = ${it.weightGrams * it.quantity}g ${if (it.notes.isNotBlank()) "[${it.notes}]" else ""}"
        }

        return """
            你是一位專精於「台灣高山縱走與百岳輕量化 (UL)」的專業裝備顧問與教練（亞馬遜國家山岳協會標準）。
            請針對以下裝備清單進行專業的「裝備減重與輕量化分析」：

            【行程/配置名稱】：$packName
            【使用者體重】：${bodyWeightKg} kg
            【出發總重量】：${totalGrams} g (${String.format("%.2f", totalGrams / 1000.0)} kg)
            【基礎重量 Base Weight (不含消耗品)】：${baseGrams} g (${String.format("%.2f", baseGrams / 1000.0)} kg)
            【消耗品重量 Consumables】：${consumableGrams} g (${String.format("%.2f", consumableGrams / 1000.0)} kg)
            【負重/體重比】：${String.format("%.1f", (totalGrams / 1000.0 / bodyWeightKg) * 100)} %

            【裝備詳細清單】：
            $itemListString

            請依據以下格式輸出繁體中文分析報告：

            ### 📊 1. 最重裝備前三名 (Top 3 Heavy Gear)
            (列出最重的 3 項裝備名稱與重量，並給出明確的改進方案與可減少的公克數)

            ### 💡 2. 可替代輕量化裝備建議 (Alternative Gear Suggestions)
            (例如：若將 2800g 的傳統帳篷更換為 1200g 的雙人輕量帳/非自立帳，可直接減重 1600g...)

            ### ⚠️ 3. 冗餘與不必要重量診斷 (Unnecessary Weight / Redundancies)
            (抓出可能重複帶的物品、過重的備用衣物、過多的瓶罐、過重的行動電源或過多行動糧)

            ### 🏔️ 4. 針對台灣高山特性的減重與安全方向 (Taiwan Alpine Safety Advice)
            (綜合考量台灣高山地形、水源補給點、低溫保暖、風雨衣與稜線氣候，給出 3 點極具價值的實戰建議)

            語言請簡潔專業、數字精準，態度鼓勵與重視高山安全。
        """.trimIndent()
    }

    private fun generateLocalRuleBasedAnalysis(
        packName: String,
        bodyWeightKg: Float,
        items: List<GearItemEntity>
    ): String {
        val packedItems = items.filter { it.isPacked }
        if (packedItems.isEmpty()) {
            return "目前清單中尚無勾選攜帶的裝備。請先新增或勾選裝備後再點擊 AI 減重分析！"
        }

        val totalGrams = packedItems.sumOf { it.weightGrams * it.quantity }
        val baseGrams = packedItems.sumOf {
            val cat = GearCategory.fromDisplayName(it.category)
            if (!cat.isConsumable) it.weightGrams * it.quantity else 0
        }
        val totalKg = totalGrams / 1000.0f
        val ratioPercent = (totalKg / bodyWeightKg) * 100f

        val sortedByWeight = packedItems.sortedByDescending { it.weightGrams * it.quantity }
        val top3 = sortedByWeight.take(3)

        val sb = StringBuilder()
        sb.append("### 📊 1. 最重裝備前三名 (Top 3 Heavy Gear)\n")
        top3.forEachIndexed { idx, item ->
            val totalItemWeight = item.weightGrams * item.quantity
            sb.append("${idx + 1}. **${item.name}**：${totalItemWeight}g (${item.category})\n")
            when {
                item.category.contains("住宿") && totalItemWeight > 1800 -> {
                    val potentialSave = totalItemWeight - 1000
                    sb.append("   - 💡 *建議*：若更換為 UL 輕量雙人帳或非自立登山帳 (約 850g-1000g)，預計可減重 **${potentialSave}g**！\n")
                }
                item.category.contains("背包") && totalItemWeight > 1500 -> {
                    val potentialSave = totalItemWeight - 950
                    sb.append("   - 💡 *建議*：若改用輕量化無鋼架/網架 45-50L 背包 (約 850g-950g)，預計可減重 **${potentialSave}g**！\n")
                }
                item.category.contains("電子") && totalItemWeight > 350 -> {
                    val potentialSave = totalItemWeight - 210
                    sb.append("   - 💡 *建議*：20000mAh 行動電源偏重，若行程水源充沛或天數在3天內，改用 10000mAh 快充款可減重 **${potentialSave}g**。\n")
                }
                item.category.contains("炊事") && totalItemWeight > 300 -> {
                    val potentialSave = totalItemWeight - 120
                    sb.append("   - 💡 *建議*：更換為極致鈦金屬深鍋與直噴鈦爐頭，預計可減少 **${potentialSave}g** 重量。\n")
                }
                else -> {
                    val saveEst = (totalItemWeight * 0.35).toInt()
                    sb.append("   - 💡 *建議*：評估升級輕量規格或優化附屬配件，可節省約 **${saveEst}g**。\n")
                }
            }
        }

        sb.append("\n### 💡 2. 可替代輕量化裝備建議 (Alternative Gear Suggestions)\n")
        var alternativesFound = false
        packedItems.forEach { item ->
            val w = item.weightGrams * item.quantity
            if (item.category.contains("住宿") && w > 1200 && !alternativesFound) {
                sb.append("• **睡袋/睡墊系統**：目前 ${item.name} (${w}g)。升級至 850FP+ 高蓬鬆鵝絨睡袋 (約 650g) 並選用蛋殼閉孔睡墊，能顯著壓縮體積並減少 300g-500g。\n")
                alternativesFound = true
            } else if (item.category.contains("衣物") && w > 600) {
                sb.append("• **衣物系統重疊**：目前 ${item.name} (${w}g)。高山穿著採用「底層排汗+保暖羽絨+風雨衣」三層洋蔥穿法即可，避免攜帶多餘棉質或非必備衣物。\n")
            }
        }
        if (!alternativesFound) {
            sb.append("• **微型物品積少成多**：多數登山者的邊角重量（打包袋、鋼質快扣、多餘營繩、大包裝保養品）加總高達 800g，建議換成 PE夾鏈袋 與 Dyneema 細繩。\n")
        }

        sb.append("\n### ⚠️ 3. 冗餘與不必要重量診斷 (Unnecessary Weight Diagnosis)\n")
        val consumableItems = packedItems.filter { GearCategory.fromDisplayName(it.category).isConsumable }
        val waterItems = consumableItems.filter { it.name.contains("水") }
        val waterWeight = waterItems.sumOf { it.weightGrams * it.quantity }

        if (waterWeight > 2000) {
            sb.append("• **背水過量警告**：清單中包含水約 ${waterWeight}g。請查閱台灣高山稜線與營地水源紀錄，若沿途有稜線水塘或溪谷，建議起登背 1000ml-1500ml 搭配高效率濾水器，即可現省 **1000g** 以上背負！\n")
        } else {
            sb.append("• **分裝與包裝減重**：檢查濕紙巾、個人藥品、行動糧是否已有過度外包裝，拆除硬盒使用小夾鏈袋分裝可減少 150g-250g。\n")
        }

        sb.append("\n### 🏔️ 4. 針對台灣高山特性的減重與安全方向 (Taiwan Alpine Safety Advice)\n")
        sb.append("1. **保暖與風雨衣不可刪**：台灣百岳即使夏季午後亦常有強陣雨與低溫，保暖羽絨與 Gore-Tex 風雨衣褲屬於「生命維持裝備」，切勿為了輕量化而濫刪！\n")
        sb.append("2. **負重體重比評估**：您目前負重比為 **${String.format("%.1f", ratioPercent)}%** (${if (ratioPercent <= 20) "🟢 屬於健康舒適範圍" else if (ratioPercent <= 25) "🟡 屬於偏重，建議進行小幅減重" else "🔴 建議積極減重至20%以下"}）。建議將基礎重量控制在 8kg 以內。\n")
        sb.append("3. **糧食精準計算**：高山熱量消耗約每日 2500-3000 kcal，選擇乾燥飯與高脂肪堅果類，每日糧食控制在 550g-650g 之間最為理想。\n")

        return sb.toString()
    }
}
