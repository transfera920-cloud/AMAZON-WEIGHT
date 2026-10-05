package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.GearItemEntity
import com.example.data.model.GearCategory

@Composable
fun AddEditGearDialog(
    itemToEdit: GearItemEntity?,
    onDismiss: () -> Unit,
    onSave: (name: String, category: GearCategory, weightGrams: Int, quantity: Int, isPacked: Boolean, notes: String) -> Unit
) {
    var name by remember { mutableStateOf(itemToEdit?.name ?: "") }
    var category by remember { mutableStateOf(GearCategory.fromDisplayName(itemToEdit?.category ?: GearCategory.BACKPACK.displayName)) }
    var weightString by remember { mutableStateOf(itemToEdit?.weightGrams?.toString() ?: "") }
    var quantityString by remember { mutableStateOf(itemToEdit?.quantity?.toString() ?: "1") }
    var isPacked by remember { mutableStateOf(itemToEdit?.isPacked ?: true) }
    var notes by remember { mutableStateOf(itemToEdit?.notes ?: "") }

    var expandedCategoryMenu by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf(false) }
    var weightError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (itemToEdit == null) "➕ 新增裝備" else "✏️ 編輯裝備",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Gear Name Input
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = false
                    },
                    label = { Text("裝備名稱 *") },
                    placeholder = { Text("例如：Osprey Atmos 65L 背包") },
                    isError = nameError,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_gear_name"),
                    shape = RoundedCornerShape(12.dp)
                )

                // Category Dropdown
                Text(
                    text = "裝備分類",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = category.categoryColor.copy(alpha = 0.12f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedCategoryMenu = true }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = category.icon,
                                contentDescription = null,
                                tint = category.categoryColor,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = category.displayName,
                                fontWeight = FontWeight.Bold,
                                color = category.categoryColor
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = category.categoryColor
                        )
                    }

                    DropdownMenu(
                        expanded = expandedCategoryMenu,
                        onDismissRequest = { expandedCategoryMenu = false }
                    ) {
                        GearCategory.entries.forEach { cat ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = cat.icon,
                                            contentDescription = null,
                                            tint = cat.categoryColor,
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                        Column {
                                            Text(cat.displayName, fontWeight = FontWeight.Bold)
                                            Text(cat.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                },
                                onClick = {
                                    category = cat
                                    expandedCategoryMenu = false
                                }
                            )
                        }
                    }
                }

                // Weight (g) & Quantity Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = weightString,
                        onValueChange = {
                            weightString = it
                            weightError = false
                        },
                        label = { Text("單件重量 (g) *") },
                        placeholder = { Text("例如：1800") },
                        isError = weightError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        suffix = { Text("g") },
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("input_gear_weight"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = quantityString,
                        onValueChange = { quantityString = it },
                        label = { Text("數量") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(0.8f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                // Notes Input
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("備註 / 說明 (選填)") },
                    placeholder = { Text("例如：含骨架、防水套、或攜帶位置") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                // Is Packed Toggle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { isPacked = !isPacked }
                        .padding(top = 4.dp)
                ) {
                    Checkbox(
                        checked = isPacked,
                        onCheckedChange = { isPacked = it }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "勾選攜帶此裝備 (列入總重量計算)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val validName = name.trim()
                    val parsedWeight = weightString.toIntOrNull()
                    val parsedQty = quantityString.toIntOrNull() ?: 1

                    if (validName.isBlank()) {
                        nameError = true
                        return@Button
                    }
                    if (parsedWeight == null || parsedWeight < 0) {
                        weightError = true
                        return@Button
                    }

                    onSave(validName, category, parsedWeight, parsedQty, isPacked, notes.trim())
                },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("button_save_gear")
            ) {
                Text("儲存裝備", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}
