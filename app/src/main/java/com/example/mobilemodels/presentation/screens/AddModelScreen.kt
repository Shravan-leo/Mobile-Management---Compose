package com.example.mobilemodels.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mobilemodels.presentation.MobileModelsViewModel

@Composable
fun AddModelScreen(
    id: String, onId: (String) -> Unit,
    name: String, onName: (String) -> Unit,
    price: String, onPrice: (String) -> Unit,
    busy: Boolean, vm: MobileModelsViewModel
) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Add New Mobile Model", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        OutlinedTextField(id, onId, label = { Text("Model ID") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(name, onName, label = { Text("Model Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(price, onPrice, label = { Text("Price (₹)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Button(
            onClick = { vm.addModel(id, name, price) },
            enabled = !busy,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Icon(Icons.Default.Add, null)
            Spacer(Modifier.width(8.dp))
            Text(if (busy) "Please wait…" else "Add Model")
        }
    }
}
