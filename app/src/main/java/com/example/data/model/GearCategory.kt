package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material.icons.outlined.LocalDining
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.NightShelter
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class GearCategory(
    val displayName: String,
    val isConsumable: Boolean,
    val colorHex: Long,
    val description: String
) {
    BACKPACK("背包系統", false, 0xFF2E6F56, "背包、背包套、防水袋、腰包"),
    SHELTER("住宿系統", false, 0xFF3D405B, "帳篷、睡袋、睡墊、枕頭、天幕"),
    COOKING("炊事系統", false, 0xFFE07A5F, "爐具、鍋具、餐具、濾水器、點火器"),
    CLOTHING("衣物系統", false, 0xFF81B29A, "保暖層、中層、雨衣褲、備用衣物、手套、毛帽"),
    ELECTRONICS("電子設備", false, 0xFFF2CC8F, "手機、行動電源、頭燈、GPS、相機、電池"),
    SAFETY("安全裝備", false, 0xFFE0576C, "急救包、個人藥品、哨子、求生毯、頭盔"),
    PERSONAL("個人用品", false, 0xFF6C757D, "盥洗用品、防曬用品、衛生用品、登山杖"),
    CONSUMABLES("消耗品", true, 0xFF457B9D, "食物、水、瓦斯、電池等隨日耗損品");

    val categoryColor: Color
        get() = Color(colorHex)

    val icon: ImageVector
        get() = when (this) {
            BACKPACK -> Icons.Outlined.ShoppingBag
            SHELTER -> Icons.Outlined.NightShelter
            COOKING -> Icons.Outlined.Restaurant
            CLOTHING -> Icons.Outlined.Checkroom
            ELECTRONICS -> Icons.Outlined.Devices
            SAFETY -> Icons.Outlined.MedicalServices
            PERSONAL -> Icons.Outlined.Person
            CONSUMABLES -> Icons.Outlined.LocalDining
        }

    companion object {
        fun fromDisplayName(name: String): GearCategory {
            return entries.firstOrNull { it.displayName == name } ?: PERSONAL
        }
    }
}
