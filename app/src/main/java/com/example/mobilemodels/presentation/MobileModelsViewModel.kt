package com.example.mobilemodels.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mobilemodels.domain.model.MobileModel
import com.example.mobilemodels.domain.repository.MobileModelsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MobileModelsUiState(
    val models: List<MobileModel> = emptyList(),
    val isLoading: Boolean = false,
    val isBusy: Boolean = false,
    val selectedModelId: Int? = null,
    val message: String? = null,
    val error: String? = null,
    val downloadedFileName: String? = null,
    val downloadedBytes: ByteArray? = null
)

@HiltViewModel
class MobileModelsViewModel @Inject constructor(
    private val repository: MobileModelsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(MobileModelsUiState())
    val uiState = _uiState.asStateFlow()

    init { refresh() }

    fun clearNotice() {
        _uiState.value = _uiState.value.copy(message = null, error = null)
    }

    fun selectModel(id: Int) {
        _uiState.value = _uiState.value.copy(selectedModelId = id)
    }

    fun refresh() = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        runCatching { repository.getModels() }
            .onSuccess { models ->
                val previous = _uiState.value.selectedModelId
                _uiState.value = _uiState.value.copy(
                    models = models,
                    selectedModelId = previous?.takeIf { id -> models.any { it.id == id } }
                        ?: models.firstOrNull()?.id,
                    isLoading = false
                )
            }
            .onFailure {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = it.message ?: "Unable to load models"
                )
            }
    }

    private fun showSuccess(msg: String) {
        _uiState.value = _uiState.value.copy(isBusy = false, message = msg, error = null)
        viewModelScope.launch {
            kotlinx.coroutines.delay(4000)
            if (_uiState.value.message == msg) {
                _uiState.value = _uiState.value.copy(message = null)
            }
        }
    }

    fun addModel(idText: String, nameText: String, priceText: String) =
        viewModelScope.launch {
            val id = idText.toIntOrNull()
            val price = priceText.toIntOrNull()
            val name = nameText.trim()
            if (id == null || id <= 0 || price == null || price <= 0 || name.length !in 3..15) {
                _uiState.value = _uiState.value.copy(
                    error = "Enter a positive ID and price, and a name with 3–15 characters."
                )
                return@launch
            }
            _uiState.value = _uiState.value.copy(isBusy = true, error = null, message = null)
            runCatching { repository.addModel(MobileModel(id, name, price)) }
                .onSuccess {
                    showSuccess("Mobile model added successfully.")
                    refresh()
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        isBusy = false, error = it.message ?: "Unable to add model"
                    )
                }
        }

    fun deleteModel(idText: String) = viewModelScope.launch {
        val id = idText.toIntOrNull()
        if (id == null || id <= 0) {
            _uiState.value = _uiState.value.copy(error = "Enter a valid model ID.")
            return@launch
        }
        _uiState.value = _uiState.value.copy(isBusy = true, error = null, message = null)
        runCatching { repository.deleteModel(id) }
            .onSuccess {
                showSuccess("Delete request completed.")
                refresh()
            }
            .onFailure {
                _uiState.value = _uiState.value.copy(
                    isBusy = false, error = it.message ?: "Unable to delete model"
                )
            }
    }

    fun uploadPdf(idText: String, fileName: String, bytes: ByteArray) =
        viewModelScope.launch {
            val id = idText.toIntOrNull()
            if (id == null || id <= 0) {
                _uiState.value = _uiState.value.copy(error = "Enter a valid model ID.")
                return@launch
            }
            val signatureValid = bytes.size >= 5 &&
                bytes.copyOfRange(0, 5).decodeToString() == "%PDF-"
            if (!fileName.endsWith(".pdf", true) || !signatureValid ||
                bytes.size > 20 * 1024 * 1024) {
                _uiState.value = _uiState.value.copy(
                    error = "Choose a valid PDF no larger than 20 MB."
                )
                return@launch
            }
            _uiState.value = _uiState.value.copy(isBusy = true, error = null, message = null)
            runCatching { repository.uploadPdf(id, fileName, bytes) }
                .onSuccess {
                    showSuccess(it)
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        isBusy = false, error = it.message ?: "PDF upload failed"
                    )
                }
        }

    fun downloadModelPdf(idText: String) = viewModelScope.launch {
        val id = idText.toIntOrNull()
        if (id == null || id <= 0) {
            _uiState.value = _uiState.value.copy(error = "Enter a valid model ID.")
            return@launch
        }
        _uiState.value = _uiState.value.copy(isBusy = true, error = null, message = null)
        runCatching { repository.downloadModelPdf(id) }
            .onSuccess {
                _uiState.value = _uiState.value.copy(
                    isBusy = false,
                    downloadedFileName = "model_$id.pdf",
                    downloadedBytes = it
                )
                showSuccess("PDF ready to save.")
            }
            .onFailure {
                _uiState.value = _uiState.value.copy(
                    isBusy = false, error = it.message ?: "PDF download failed"
                )
            }
    }

    fun downloadReport() = viewModelScope.launch {
        _uiState.value = _uiState.value.copy(isBusy = true, error = null, message = null)
        runCatching { repository.downloadReportPdf() }
            .onSuccess {
                _uiState.value = _uiState.value.copy(
                    isBusy = false,
                    downloadedFileName = "mobile_models_report.pdf",
                    downloadedBytes = it
                )
                showSuccess("Report ready to save.")
            }
            .onFailure {
                _uiState.value = _uiState.value.copy(
                    isBusy = false, error = it.message ?: "Report download failed"
                )
            }
    }
}
