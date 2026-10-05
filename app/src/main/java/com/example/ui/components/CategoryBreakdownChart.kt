package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GearCategory
import com.example.ui.CategoryWeight

@Composable
fun CategoryBreakdownChart(
    breakdowns: List<CategoryWeight>,
    selectedCategoryFilter: GearCategory?,
    onSelectCategoryFilter: (GearCategory?) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalWeightGrams = breakdowns.sumOf { it.totalGrams }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("category_breakdown_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.PieChart,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "分類重量分析",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (selectedCategoryFilter != null) {
                    Surface(
                        shape = RoundedCornerShape(50.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .clickable { onSelectCategoryFilter(null) }
                    ) {
                        Text(
                            text = "清除篩選 ✕",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Multi-segment Combined Progress Bar
            if (totalWeightGrams > 0) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    breakdowns.forEach { cat ->
                        if (cat.percentage > 0f) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(cat.percentage)
                                    .background(cat.category.categoryColor)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Breakdown Rows for Each Category
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                breakdowns.forEach { cat ->
                    CategoryRowItem(
                        category = cat.category,
                        grams = cat.totalGrams,
                        percentage = cat.percentage,
                        isSelected = selectedCategoryFilter == cat.category,
                        onClick = {
                            if (selectedCategoryFilter == cat.category) {
                                onSelectCategoryFilter(null)
                            } else {
                                onSelectCategoryFilter(cat.category)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryRowItem(
    category: GearCategory,
    grams: Int,
    percentage: Float,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val progressAnimated by animateFloatAsState(
        targetValue = if (percentage > 0) percentage / 100f else 0f,
        label = "progress"
    )

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) category.categoryColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = category.categoryColor,
                        modifier = Modifier.size(10.dp)
                    ) {}
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = category.icon,
                        contentDescription = null,
                        tint = category.categoryColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = category.displayName,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "${String.format("%,d", grams)} g (${String.format("%.1f", percentage)}%)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = category.categoryColor
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { progressAnimated },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = category.categoryColor,
                trackColor = category.categoryColor.copy(alpha = 0.15f)
            )
        }
    }
}
