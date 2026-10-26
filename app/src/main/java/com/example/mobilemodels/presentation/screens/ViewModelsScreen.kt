package com.example.mobilemodels.presentation.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mobilemodels.presentation.MobileModelsUiState
import com.example.mobilemodels.presentation.MobileModelsViewModel

@Composable
fun ViewModelsScreen(state: MobileModelsUiState, viewModel: MobileModelsViewModel) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("All Mobile Models", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            OutlinedButton(onClick = viewModel::refresh) {
                Icon(Icons.Default.Refresh, null)
                Spacer(Modifier.width(6.dp))
                Text("Refresh")
            }
        }
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
