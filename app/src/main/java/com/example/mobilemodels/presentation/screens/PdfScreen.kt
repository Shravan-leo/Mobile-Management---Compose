package com.example.mobilemodels.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.mobilemodels.presentation.MobileModelsUiState
import com.example.mobilemodels.presentation.MobileModelsViewModel

private val BrandBlue = Color(0xFF1759D1)

@Composable
fun PdfScreen(
    id: String, onId: (String) -> Unit, fileName: String?,
    onChoose: () -> Unit, onUpload: () -> Unit,
    downloadId: String, onDownloadId: (String) -> Unit,
    state: MobileModelsUiState, vm: MobileModelsViewModel,
    onSave: () -> Unit
) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("PDF Upload and Download", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        ElevatedCard(shape = RoundedCornerShape(18.dp)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(Icons.Default.UploadFile, null, tint = BrandBlue, modifier = Modifier.size(36.dp))
                Text("Upload PDF for a model", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("PDF only · Maximum 20 MB", color = Color.Gray)
                OutlinedTextField(id, onId, label = { Text("Mobile Model ID") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedButton(onClick = onChoose, modifier = Modifier.fillMaxWidth()) {
                    Text(fileName ?: "Choose PDF")
                }
                Button(onClick = onUpload, enabled = fileName != null && !state.isBusy, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.UploadFile, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Upload PDF")
                }
            }
        }
        ElevatedCard(shape = RoundedCornerShape(18.dp)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(Icons.Default.Download, null, tint = BrandBlue, modifier = Modifier.size(36.dp))
                Text("Download stored PDF", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                OutlinedTextField(downloadId, onDownloadId, label = { Text("Mobile Model ID") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Button(onClick = { vm.downloadModelPdf(downloadId) }, enabled = !state.isBusy, modifier = Modifier.fillMaxWidth()) {
                    Text("Fetch PDF")
                }
                if (state.downloadedBytes != null && state.downloadedFileName?.startsWith("model_") == true) {
                    Text("PDF ready: ${state.downloadedFileName}")
                    OutlinedButton(onClick = onSave, modifier = Modifier.fillMaxWidth()) {
                        Text("Save PDF")
                    }
                }
            }
        }
    }
}
