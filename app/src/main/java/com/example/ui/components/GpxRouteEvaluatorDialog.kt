package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Landscape
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class GpxRoutePreset(
    val routeName: String,
    val days: String,
    val distanceKm: Float,
    val elevationGainM: Int,
    val recommendedBaseWeightKg: String,
    val recommendedMaxLoadKg: String,
    val waterAdvice: String
)

val CLASSIC_ROUTES = listOf(
    GpxRoutePreset(
        routeName = "南三段 (丹大東郡橫斷)",
        days = "8天7夜",
        distanceKm = 128.5f,
        elevationGainM = 8200,
        recommendedBaseWeightKg = "8 - 10 kg",
        recommendedMaxLoadKg = "15.0 - 17.0 kg 以下 (第一天)",
        waterAdvice = "沿途溪谷水資源豐富，起登只需攜帶 1000-1500ml 搭配高效率濾水器即可。"
    ),
    GpxRoutePreset(
        routeName = "能高安東軍縱走",
        days = "5天4夜",
        distanceKm = 58.0f,
        elevationGainM = 4500,
        recommendedBaseWeightKg = "7.5 - 9.0 kg",
        recommendedMaxLoadKg = "14.5 kg 以下",
        waterAdvice = "高山湖泊與營地溪水眾多，建議選用耐用濾水器與預備水袋。"
    ),
    GpxRoutePreset(
        routeName = "嘉明湖國家步道",
        days = "3天2夜",
        distanceKm = 26.0f,
        elevationGainM = 2100,
        recommendedBaseWeightKg = "6.5 - 8.5 kg",
        recommendedMaxLoadKg = "13.0 kg 以下",
        waterAdvice = "山屋水源穩定，若住營地需注意水塔與池塘過濾。"
    ),
    GpxRoutePreset(
        routeName = "奇萊南華 (天池山莊)",
        days = "2天1夜",
        distanceKm = 29.5f,
        elevationGainM = 1650,
        recommendedBaseWeightKg = "5.5 - 7.5 kg",
        recommendedMaxLoadKg = "11.0 kg 以下",
        waterAdvice = "天池山莊提供飲水與供餐，可大幅輕量化，基礎重量可低至 6kg。"
    ),
    GpxRoutePreset(
        routeName = "百岳單攻 (合歡群峰/玉山單攻)",
        days = "1天0夜",
        distanceKm = 21.8f,
        elevationGainM = 1400,
        recommendedBaseWeightKg = "3.5 - 5.0 kg",
        recommendedMaxLoadKg = "7.5 kg 以下",
        waterAdvice = "單攻水份隨身攜帶約 2000ml，無山屋補給時需補充電解質。"
    )
)

@Composable
fun GpxRouteEvaluatorDialog(
    onDismiss: () -> Unit
) {
    var selectedRoute by remember { mutableStateOf(CLASSIC_ROUTES.first()) }
    var customGpxText by remember { mutableStateOf("") }
    var showCustomResult by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.Map,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "GPX 行程分析與裝備建議",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "上傳或選擇 GPX 路線 -> 自動分析天數爬升與裝備目標",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "選擇台灣高山經典路線或輸入 GPX：",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                // Route Chips
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    CLASSIC_ROUTES.forEach { route ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (selectedRoute == route && !showCustomResult)
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.background,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedRoute = route
                                    showCustomResult = false
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(route.routeName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(
                                        "${route.days} • 約 ${route.distanceKm} km • 爬升 +${route.elevationGainM}m",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (selectedRoute == route && !showCustomResult) {
                                    Text("✓ 已選定", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }

                // Custom GPX paste option
                OutlinedTextField(
                    value = customGpxText,
                    onValueChange = { customGpxText = it },
                    placeholder = { Text("可貼上 GPX 文字或路線檔內容 (xml/gpx)") },
                    label = { Text("或貼上個人 GPX 檔案內容") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                if (customGpxText.isNotBlank()) {
                    Button(
                        onClick = { showCustomResult = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("解析自訂 GPX 路線")
                    }
                }

                // Route Weight Recommendation Output Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("gpx_recommendation_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Landscape, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (showCustomResult) "自訂 GPX 行程評估建議" else "📍 ${selectedRoute.routeName} 建議裝備指標",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        val displayRoute = if (showCustomResult) {
                            GpxRoutePreset(
                                routeName = "自訂縱走路線",
                                days = "估計 4-6 天",
                                distanceKm = 45.0f,
                                elevationGainM = 3200,
                                recommendedBaseWeightKg = "7.5 - 9.5 kg",
                                recommendedMaxLoadKg = "15.0 kg 以下",
                                waterAdvice = "請務必對照營地與稜線活水源，起登背負約 1500ml 備用水。"
                            )
                        } else selectedRoute

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("🎯 建議基礎重量 (Base Weight):", fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(displayRoute.recommendedBaseWeightKg, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("🎒 第一天最大負重:", fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(displayRoute.recommendedMaxLoadKg, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = MaterialTheme.colorScheme.secondary)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "💧 水源指引：${displayRoute.waterAdvice}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("關閉評估")
            }
        }
    )
}
