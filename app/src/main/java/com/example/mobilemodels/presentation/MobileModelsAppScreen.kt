package com.example.mobilemodels.presentation

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.mobilemodels.presentation.screens.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val BrandBlue = Color(0xFF1759D1)
private val PageBackground = Color(0xFFF5F8FD)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MobileModelsAppScreen(viewModel: MobileModelsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var modelId by remember { mutableStateOf("") }
    var modelName by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var deleteId by remember { mutableStateOf("") }
    var pdfModelId by remember { mutableStateOf("") }
    var downloadId by remember { mutableStateOf("") }
    var selectedPdfUri by remember { mutableStateOf<Uri?>(null) }
    var selectedPdfName by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.message) {
        if (state.message == "Mobile model added successfully.") {
            modelId = ""
            modelName = ""
            price = ""
        }
        if (state.message == "Delete request completed." || state.message?.contains("deleted", true) == true) {
            deleteId = ""
        }
        if (state.message?.contains("uploaded", true) == true || state.message?.contains("success", true) == true) {
            pdfModelId = ""
            selectedPdfUri = null
            selectedPdfName = null
        }
    }

    val pdfPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        selectedPdfUri = uri
        selectedPdfName = uri?.lastPathSegment?.substringAfterLast('/')
            ?: uri?.let { queryDisplayName(context, it) }
    }

    val savePdfLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        if (uri != null) {
            val bytes = state.downloadedBytes
            if (bytes != null) {
                scope.launch {
                    runCatching {
                        withContext(Dispatchers.IO) {
                            context.contentResolver.openOutputStream(uri)?.use { it.write(bytes) }
                                ?: error("Unable to open destination")
                        }
                    }.onSuccess {
                        // Keep the success notice; the file has been saved.
                    }.onFailure {
                        // UI notice is routed through the ViewModel.
                        viewModel.clearNotice()
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Mobile Models Management",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BrandBlue,
                    titleContentColor = Color.White
                )
            )
        },
        containerColor = PageBackground
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            ScrollableTabRow(selectedTabIndex = selectedTab, edgePadding = 8.dp) {
                listOf("View Models", "Add Model", "Delete Model", "PDF Upload", "PDF Report")
                    .forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title, maxLines = 1) }
                        )
                    }
            }

            if (state.isLoading || state.isBusy) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
            }
            state.error?.let { NoticeCard(it, true) }
            state.message?.let { NoticeCard(it, false) }

            when (selectedTab) {
                0 -> ViewModelsScreen(state, viewModel)
                1 -> AddModelScreen(
                    modelId, { modelId = it }, modelName, { modelName = it },
                    price, { price = it }, state.isBusy, viewModel
                )
                2 -> DeleteModelScreen(deleteId, { deleteId = it }, state.isBusy, viewModel)
                3 -> PdfScreen(
                    pdfModelId, { pdfModelId = it }, selectedPdfName,
                    onChoose = { pdfPicker.launch(arrayOf("application/pdf")) },
                    onUpload = {
                        val uri = selectedPdfUri
                        if (uri != null) {
                            scope.launch {
                                runCatching {
                                    withContext(Dispatchers.IO) {
                                        context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                                            ?: error("Cannot read selected file")
                                    }
                                }.onSuccess { bytes ->
                                    viewModel.uploadPdf(
                                        pdfModelId,
                                        selectedPdfName ?: "upload.pdf",
                                        bytes
                                    )
                                }.onFailure {
                                    // The selected file may no longer be readable.
                                }
                            }
                        }
                    },
                    downloadId, { downloadId = it }, state, viewModel,
                    onSave = { savePdfLauncher.launch(state.downloadedFileName ?: "model.pdf") }
                )
                4 -> ReportScreen(
                    state, viewModel,
                    onSave = { savePdfLauncher.launch(state.downloadedFileName ?: "mobile_models_report.pdf") }
                )
            }
        }
    }
}

@Composable
private fun NoticeCard(text: String, isError: Boolean) {
    Surface(
        color = if (isError) Color(0xFFFFE8E8) else Color(0xFFE3F7EA),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text,
            Modifier.padding(12.dp),
            color = if (isError) Color(0xFF9D2027) else Color(0xFF176B3A)
        )
    }
}

private fun queryDisplayName(context: Context, uri: Uri): String? {
    return context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val index = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
        if (cursor.moveToFirst() && index >= 0) cursor.getString(index) else null
    }
}
