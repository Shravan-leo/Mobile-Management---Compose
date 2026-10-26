package com.example.mobilemodels.data.remote.dto

import com.example.mobilemodels.domain.model.MobileModel
import com.google.gson.annotations.SerializedName

data class MobileDto(
    val id: Int,
    val name: String,
    val price: Int,
    @SerializedName("device_names")
    val deviceNames: List<String>? = emptyList()
) {
    fun toDomain() = MobileModel(
        id = id,
        name = name,
        price = price,
        deviceNames = deviceNames.orEmpty()
    )
}

data class UploadPdfResponse(
    val success: Boolean,
    val message: String,
    @SerializedName("model_id") val modelId: Int,
    @SerializedName("file_size_bytes") val fileSizeBytes: Long
)
