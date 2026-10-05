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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.CompareArrows
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.db.GearPackEntity
import com.example.ui.ComparisonResult

@Composable
fun GearCompareDialog(
    allPacks: List<GearPackEntity>,
    comparisonResult: ComparisonResult?,
    onCompare: (packAId: Long, packBId: Long) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedPackA by remember { mutableStateOf(allPacks.firstOrNull()) }
    var selectedPackB by remember {
        mutableStateOf(if (allPacks.size > 1) allPacks[1] else allPacks.firstOrNull())
    }

    var expandedA by remember { mutableStateOf(false) }
    var expandedB by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.CompareArrows,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "裝備方案比較 (A vs B)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "選擇兩套裝備配置以進行重量差異比較 (例如：傳統重量版 vs 輕量化版)",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Pack A Dropdown
                Text("方案 A (對照版)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedA = true }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(selectedPackA?.name ?: "選擇方案 A", fontWeight = FontWeight.Bold)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }

                    DropdownMenu(expanded = expandedA, onDismissRequest = { expandedA = false }) {
                        allPacks.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(p.name) },
                                onClick = {
                                    selectedPackA = p
                                    expandedA = false
                                }
                            )
                        }
                    }
                }

                // Pack B Dropdown
                Text("方案 B (比較版)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedB = true }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(selectedPackB?.name ?: "選擇方案 B", fontWeight = FontWeight.Bold)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }

                    DropdownMenu(expanded = expandedB, onDismissRequest = { expandedB = false }) {
                        allPacks.forEach { p ->
                            DropdownMenuItem(
                                text = { Text(p.name) },
                                onClick = {
                                    selectedPackB = p
                                    expandedB = false
                                }
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        val a = selectedPackA?.id ?: return@Button
                        val b = selectedPackB?.id ?: return@Button
                        onCompare(a, b)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("button_start_compare"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("計算兩方案差異", fontWeight = FontWeight.Bold)
                }

                // Comparison Results Display
                if (comparisonResult != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("compare_result_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "📊 比較分析結果",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("方案 A：${comparisonResult.packAName}", fontSize = 12.sp)
                                    Text(
                                        "${comparisonResult.packATotalGrams} g (${String.format("%.2f", comparisonResult.packATotalGrams / 1000f)} kg)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text("基礎重量: ${comparisonResult.packABaseGrams} g", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text("方案 B：${comparisonResult.packBName}", fontSize = 12.sp)
                                    Text(
                                        "${comparisonResult.packBTotalGrams} g (${String.format("%.2f", comparisonResult.packBTotalGrams / 1000f)} kg)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                    Text("基礎重量: ${comparisonResult.packBBaseGrams} g", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            val diffTotal = comparisonResult.diffTotalGrams
                            val diffBase = comparisonResult.diffBaseGrams

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (diffTotal <= 0) Color(0xFF2E7D32).copy(alpha = 0.15f) else Color(0xFFC62828).copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .padding(10.dp)
                            ) {
                                Column {
                                    Text(
                                        text = if (diffTotal <= 0)
                                            "🎉 方案 B 比 方案 A 減少：${Math.abs(diffTotal)} g (${String.format("%.2f", Math.abs(diffTotal) / 1000f)} kg)！"
                                        else
                                            "⚠️ 方案 B 比 方案 A 增加：${diffTotal} g (${String.format("%.2f", diffTotal / 1000f)} kg)",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 13.5.sp,
                                        color = if (diffTotal <= 0) Color(0xFF2E7D32) else Color(0xFFC62828)
                                    )

                                    Text(
                                        text = "基礎重量差異：${if (diffBase <= 0) "減少 ${Math.abs(diffBase)}g" else "增加 ${diffBase}g"}",
                                        fontSize = 11.5.sp,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }
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
                Text("關閉")
            }
        }
    )
}
