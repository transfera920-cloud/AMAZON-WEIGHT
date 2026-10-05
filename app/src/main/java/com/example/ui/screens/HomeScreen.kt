package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.data.db.GearItemEntity
import com.example.data.model.GearCategory
import com.example.ui.GearViewModel
import com.example.ui.components.AddEditGearDialog
import com.example.ui.components.AiAnalysisDialog
import com.example.ui.components.BentoHeaderCard
import com.example.ui.components.CategoryBreakdownChart
import com.example.ui.components.GearCompareDialog
import com.example.ui.components.GearItemRow
import com.example.ui.components.GpxRouteEvaluatorDialog
import com.example.ui.components.NewPackDialog
import com.example.ui.components.PresetsDialog
import com.example.ui.components.ShareExportDialog
import com.example.ui.components.WeightMetricsCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: GearViewModel,
    modifier: Modifier = Modifier
) {
    val allPacks by viewModel.allPacks.collectAsState()
    val currentPack by viewModel.currentPack.collectAsState()
    val currentItems by viewModel.currentItems.collectAsState()

    val isAiAnalyzing by viewModel.isAiAnalyzing.collectAsState()
    val aiAnalysisResult by viewModel.aiAnalysisResult.collectAsState()
    val comparisonResult by viewModel.comparisonResult.collectAsState()

    // Dialog Visibilities
    var showAddEditDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<GearItemEntity?>(null) }
    var showAiDialog by remember { mutableStateOf(false) }
    var showPresetsDialog by remember { mutableStateOf(false) }
    var showCompareDialog by remember { mutableStateOf(false) }
    var showGpxDialog by remember { mutableStateOf(false) }
    var showShareDialog by remember { mutableStateOf(false) }
    var showNewPackDialog by remember { mutableStateOf(false) }

    // Search and Category Filter
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf<GearCategory?>(null) }

    // Weight calculations
    val summary = remember(currentItems, currentPack?.userBodyWeightKg) {
        viewModel.calculateSummary(currentItems, currentPack?.userBodyWeightKg ?: 70.0f)
    }

    val categoryBreakdowns = remember(currentItems) {
        viewModel.calculateCategoryBreakdown(currentItems)
    }

    // Filtered Items List
    val filteredItems = remember(currentItems, searchQuery, selectedCategoryFilter) {
        currentItems.filter { item ->
            val matchesSearch = searchQuery.isBlank() ||
                    item.name.contains(searchQuery, ignoreCase = true) ||
                    item.notes.contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedCategoryFilter == null ||
                    item.category == selectedCategoryFilter!!.displayName
            matchesSearch && matchesCategory
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "亞馬遜裝備重量分析",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        )
                    }
                },
                actions = {
                    if (currentPack != null) {
                        IconButton(
                            onClick = { viewModel.deleteCurrentPack() },
                            modifier = Modifier.testTag("button_delete_pack")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "刪除此清單",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    itemToEdit = null
                    showAddEditDialog = true
                },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("新增裝備", fontWeight = FontWeight.Bold) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                modifier = Modifier.testTag("fab_add_gear")
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 90.dp, start = 16.dp, end = 16.dp, top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: Bento Header Banner & Pack Selector Toolbar
            item {
                BentoHeaderCard(
                    currentPack = currentPack,
                    allPacks = allPacks,
                    onSelectPack = { packId -> viewModel.selectPack(packId) },
                    onNewPackClick = { showNewPackDialog = true },
                    onPresetsClick = { showPresetsDialog = true },
                    onCompareClick = { showCompareDialog = true },
                    onGpxClick = { showGpxDialog = true },
                    onShareClick = { showShareDialog = true },
                    onAiClick = {
                        showAiDialog = true
                        viewModel.runAiAnalysis()
                    }
                )
            }

            // Section 2: Weight Metrics Grid (Total, Base, Consumables, Body Weight Ratio)
            item {
                WeightMetricsCard(
                    summary = summary,
                    onBodyWeightChanged = { newWeight -> viewModel.updateBodyWeight(newWeight) }
                )
            }

            // Section 3: Category Weight Breakdown Chart & Filters
            item {
                CategoryBreakdownChart(
                    breakdowns = categoryBreakdowns,
                    selectedCategoryFilter = selectedCategoryFilter,
                    onSelectCategoryFilter = { cat -> selectedCategoryFilter = cat }
                )
            }

            // Section 4: Search Bar & Quick Category Chips
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "裝備清單明細 (${filteredItems.size} 項)",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "已勾選總重：${String.format("%,d", summary.totalWeightGrams)} g",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("搜尋裝備名稱或備註...") },
                        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_search_gear"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(14.dp)
                    )

                    // Horizontal Scrollable Category Filter Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FilterChip(
                                selected = selectedCategoryFilter == null,
                                onClick = { selectedCategoryFilter = null },
                                label = { Text("全部 (${currentItems.size})", fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }

                        items(GearCategory.entries) { cat ->
                            val catCount = currentItems.count { it.category == cat.displayName }
                            FilterChip(
                                selected = selectedCategoryFilter == cat,
                                onClick = {
                                    selectedCategoryFilter = if (selectedCategoryFilter == cat) null else cat
                                },
                                label = { Text("${cat.displayName} ($catCount)", fontSize = 12.sp) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = cat.icon,
                                        contentDescription = null,
                                        tint = if (selectedCategoryFilter == cat) Color.White else cat.categoryColor,
                                        modifier = Modifier.size(14.dp)
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = cat.categoryColor,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Section 5: Gear Items List Rows
            if (filteredItems.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ShoppingBag,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (searchQuery.isNotBlank() || selectedCategoryFilter != null)
                                    "找不到符合條件的裝備"
                                else
                                    "此清單中尚無裝備",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "點擊右下角「新增裝備」或載入常用高山模板！",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            } else {
                items(filteredItems, key = { it.id }) { item ->
                    GearItemRow(
                        item = item,
                        onTogglePacked = { viewModel.toggleItemPacked(item.id, item.isPacked) },
                        onEditClick = {
                            itemToEdit = item
                            showAddEditDialog = true
                        },
                        onDeleteClick = { viewModel.deleteItem(item.id) }
                    )
                }
            }
        }
    }

    // Dialogs
    if (showAddEditDialog) {
        AddEditGearDialog(
            itemToEdit = itemToEdit,
            onDismiss = { showAddEditDialog = false },
            onSave = { name, category, weight, quantity, isPacked, notes ->
                if (itemToEdit == null) {
                    viewModel.addItem(name, category, weight, quantity, isPacked, notes)
                } else {
                    viewModel.updateItem(
                        itemToEdit!!.copy(
                            name = name,
                            category = category.displayName,
                            weightGrams = weight,
                            quantity = quantity,
                            isPacked = isPacked,
                            notes = notes
                        )
                    )
                }
                showAddEditDialog = false
            }
        )
    }

    if (showAiDialog) {
        AiAnalysisDialog(
            isAnalyzing = isAiAnalyzing,
            analysisResult = aiAnalysisResult,
            onDismiss = {
                showAiDialog = false
                viewModel.clearAiResult()
            },
            onReAnalyze = { viewModel.runAiAnalysis() }
        )
    }

    if (showCompareDialog) {
        GearCompareDialog(
            allPacks = allPacks,
            comparisonResult = comparisonResult,
            onCompare = { packAId, packBId -> viewModel.comparePacks(packAId, packBId) },
            onDismiss = {
                showCompareDialog = false
                viewModel.clearComparisonResult()
            }
        )
    }

    if (showGpxDialog) {
        GpxRouteEvaluatorDialog(
            onDismiss = { showGpxDialog = false }
        )
    }

    if (showShareDialog) {
        ShareExportDialog(
            exportText = viewModel.generateExportText(),
            onDismiss = { showShareDialog = false }
        )
    }

    if (showPresetsDialog) {
        PresetsDialog(
            onSelectPreset = { preset ->
                viewModel.createFromPreset(preset)
            },
            onDismiss = { showPresetsDialog = false }
        )
    }

    if (showNewPackDialog) {
        NewPackDialog(
            onDismiss = { showNewPackDialog = false },
            onCreatePack = { name, desc ->
                viewModel.createPack(name, desc)
            }
        )
    }
}
