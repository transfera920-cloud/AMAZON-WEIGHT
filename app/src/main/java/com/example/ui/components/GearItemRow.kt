package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.GearItemEntity
import com.example.data.model.GearCategory

@Composable
fun GearItemRow(
    item: GearItemEntity,
    onTogglePacked: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val category = GearCategory.fromDisplayName(item.category)
    val subtotalGrams = item.weightGrams * item.quantity

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("gear_item_${item.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isPacked) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (item.isPacked) 2.dp else 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Carry Status Checkbox (是否攜帶)
            Checkbox(
                checked = item.isPacked,
                onCheckedChange = { onTogglePacked() },
                colors = CheckboxDefaults.colors(
                    checkedColor = category.categoryColor,
                    uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.testTag("checkbox_packed_${item.id}")
            )

            Spacer(modifier = Modifier.width(4.dp))

            // Main Info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .alpha(if (item.isPacked) 1.0f else 0.55f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textDecoration = if (item.isPacked) TextDecoration.None else TextDecoration.LineThrough
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                    // Quantity Badge if > 1
                    if (item.quantity > 1) {
                        Surface(
                            shape = RoundedCornerShape(50.dp),
                            color = category.categoryColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "x${item.quantity}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = category.categoryColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Category Badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = category.categoryColor.copy(alpha = 0.12f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = category.icon,
                                contentDescription = null,
                                tint = category.categoryColor,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = category.displayName,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = category.categoryColor
                            )
                        }
                    }

                    // Notes / Remarks
                    if (item.notes.isNotBlank()) {
                        Text(
                            text = "• ${item.notes}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Subtotal Weight Display
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.alpha(if (item.isPacked) 1.0f else 0.55f)
            ) {
                Text(
                    text = "${String.format("%,d", subtotalGrams)} g",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (item.isPacked) category.categoryColor else MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (item.quantity > 1) {
                    Text(
                        text = "(${item.weightGrams}g/個)",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            // Quick Actions: Edit & Delete
            IconButton(
                onClick = onEditClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Edit",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }

            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
