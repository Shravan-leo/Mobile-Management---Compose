package com.example.mobilemodels.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mobilemodels.presentation.MobileModelsUiState
import com.example.mobilemodels.presentation.MobileModelsViewModel

private val BAR_COLORS = listOf(
    Color(0xFFF59E0B), // Amber / Orange
    Color(0xFF10B981), // Emerald Green
    Color(0xFF1E293B), // Dark Slate
    Color(0xFFEF4444), // Coral Red
    Color(0xFF3B82F6), // Royal Blue
    Color(0xFF8B5CF6)  // Vivid Purple
)

@Composable
fun ViewModelsScreen(state: MobileModelsUiState, viewModel: MobileModelsViewModel) {
    var isLineChartSelected by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("All Mobile Models", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

        // Select View Container Box with mutually exclusive Checkboxes
        ElevatedCard(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(14.dp)) {
                Text(
                    "Select View:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { isLineChartSelected = false }
                    ) {
                        Checkbox(
                            checked = !isLineChartSelected,
                            onCheckedChange = { isLineChartSelected = !it }
                        )
                        Text("View Table Form", style = MaterialTheme.typography.bodyMedium)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { isLineChartSelected = true }
                    ) {
                        Checkbox(
                            checked = isLineChartSelected,
                            onCheckedChange = { isLineChartSelected = it }
                        )
                        Text("View Line Chart", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        // Refresh Models Button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(onClick = viewModel::refresh) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Refresh Models")
            }
        }

        if (isLineChartSelected) {
            // Line / Bar Chart View (Inventory Status)
            ElevatedCard(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Inventory Status",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        "Stock levels by category",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF64748B)
                    )
                    Spacer(Modifier.height(4.dp))

                    if (state.models.isEmpty() && !state.isLoading) {
                        Text("No mobile models found.", color = Color.Gray)
                    } else {
                        state.models.forEachIndexed { idx, model ->
                            val deviceCount = model.deviceNames?.size ?: 0
                            val maxCapacity = 10
                            val fraction = (deviceCount.toFloat() / maxCapacity.toFloat()).coerceIn(0f, 1f)
                            val barColor = BAR_COLORS[idx % BAR_COLORS.size]

                            Column(
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        model.name.uppercase(),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E293B)
                                    )
                                    Text(
                                        "$deviceCount/$maxCapacity",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF64748B)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(10.dp)
                                        .clip(RoundedCornerShape(999.dp))
                                        .background(Color(0xFFEEF2F6))
                                ) {
                                    if (fraction > 0f) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(fraction)
                                                .fillMaxHeight()
                                                .clip(RoundedCornerShape(999.dp))
                                                .background(barColor)
                                        )
                                    }
                                }
                            }
                            if (idx < state.models.size - 1) {
                                Spacer(Modifier.height(4.dp))
                            }
                        }
                    }
                }
            }
        } else {
            // Table Form View
            ElevatedCard(shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.padding(14.dp)) {
                    Text("Companies", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Tap a row to view its device models.", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Spacer(Modifier.height(10.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("ID", fontWeight = FontWeight.Bold)
                        Text("Name", fontWeight = FontWeight.Bold)
                        Text("Price", fontWeight = FontWeight.Bold)
                    }
                    HorizontalDivider()
                    state.models.forEach { model ->
                        Row(
                            Modifier.fillMaxWidth().clickable { viewModel.selectModel(model.id) }.padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(model.id.toString(), modifier = Modifier.weight(0.8f))
                            Text(
                                model.name,
                                modifier = Modifier.weight(1.3f),
                                fontWeight = if (model.id == state.selectedModelId) FontWeight.Bold else FontWeight.Normal
                            )
                            Text("₹${model.price}", modifier = Modifier.weight(1f))
                        }
                        HorizontalDivider(color = Color(0xFFE7ECF4))
                    }
                    if (state.models.isEmpty() && !state.isLoading) {
                        Text("No mobile models found.", Modifier.padding(vertical = 12.dp))
                    }
                }
            }
            val selected = state.models.firstOrNull { it.id == state.selectedModelId }
            ElevatedCard(shape = RoundedCornerShape(18.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Device Models", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Company: ${selected?.name ?: "—"}")
                    Text("Total devices: ${selected?.deviceNames?.size ?: 0}", color = Color.Gray)
                    if (selected?.deviceNames.isNullOrEmpty()) {
                        Text("No device models found for this company.")
                    } else {
                        selected!!.deviceNames.forEach { AssistChip(onClick = {}, label = { Text(it) }) }
                    }
                }
            }
        }
    }
}
