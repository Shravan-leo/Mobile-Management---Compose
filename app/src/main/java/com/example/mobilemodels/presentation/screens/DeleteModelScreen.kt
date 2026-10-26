package com.example.mobilemodels.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mobilemodels.presentation.MobileModelsViewModel

@Composable
fun DeleteModelScreen(
    id: String, onId: (String) -> Unit, busy: Boolean, vm: MobileModelsViewModel
) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Delete Mobile Model", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        OutlinedTextField(id, onId, label = { Text("Model ID to delete") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Button(
            onClick = { vm.deleteModel(id) },
            enabled = !busy,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5484D)),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Icon(Icons.Default.Delete, null)
            Spacer(Modifier.width(8.dp))
            Text("Delete Model")
        }
        Text(
            "This action removes the model from the backend. Confirm the ID before proceeding.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}
