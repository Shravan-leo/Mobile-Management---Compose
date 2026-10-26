package com.example.mobilemodels.data.repository

import com.example.mobilemodels.data.remote.CreateMobileRequest
import com.example.mobilemodels.data.remote.MobileModelsApi
import com.example.mobilemodels.domain.model.MobileModel
import com.example.mobilemodels.domain.repository.MobileModelsRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class MobileModelsRepositoryImpl @Inject constructor(
    private val api: MobileModelsApi
) : MobileModelsRepository {
    override suspend fun getModels(): List<MobileModel> {
        val responseBody = api.getModels()
        val rawJson = responseBody.string().trim()
        if (rawJson.isEmpty()) return emptyList()

        if (rawJson.startsWith("<!DOCTYPE", ignoreCase = true) || rawJson.contains("<html", ignoreCase = true)) {
            throw IllegalStateException("API URL returned an HTML page (Streamlit/Web page) instead of JSON. Please update API_BASE_URL in NetworkModule.kt to point to your FastAPI backend API.")
        }

        val element = try {
            com.google.gson.JsonParser.parseString(rawJson)
        } catch (e: Exception) {
            throw IllegalStateException("Invalid server response: $rawJson", e)
        }

        return when {
            element.isJsonArray -> {
                val listType = object : com.google.gson.reflect.TypeToken<List<com.example.mobilemodels.data.remote.dto.MobileDto>>() {}.type
                val dtos: List<com.example.mobilemodels.data.remote.dto.MobileDto> = com.google.gson.Gson().fromJson(element, listType)
                dtos.map(MobileDtoMapper::toDomain)
            }
            element.isJsonPrimitive -> {
                val text = element.asString
                if (text.isNotBlank() && !text.equals("ok", ignoreCase = true)) {
                    throw IllegalStateException("Server returned: $text")
                }
                emptyList()
            }
            element.isJsonObject -> {
                val obj = element.asJsonObject
                if (obj.has("error")) {
                    throw IllegalStateException("Server error: ${obj.get("error").asString}")
                }
                if (obj.has("id") && obj.has("name")) {
                    val dto = com.google.gson.Gson().fromJson(obj, com.example.mobilemodels.data.remote.dto.MobileDto::class.java)
                    return listOf(dto.toDomain())
                }
                val arrayKey = listOf("data", "models", "items", "result", "list").firstOrNull { obj.has(it) && obj.get(it).isJsonArray }
                if (arrayKey != null) {
                    val jsonArray = obj.getAsJsonArray(arrayKey)
                    val listType = object : com.google.gson.reflect.TypeToken<List<com.example.mobilemodels.data.remote.dto.MobileDto>>() {}.type
                    val dtos: List<com.example.mobilemodels.data.remote.dto.MobileDto> = com.google.gson.Gson().fromJson(jsonArray, listType)
                    return dtos.map(MobileDtoMapper::toDomain)
                }
                emptyList()
            }
            else -> emptyList()
        }
    }

    override suspend fun addModel(model: MobileModel) {
        val response = api.addModel(
            CreateMobileRequest(model.id, model.name, model.price)
        )
        check(response.isSuccessful) {
            "Add model failed (HTTP ${response.code()})"
        }
    }

    override suspend fun deleteModel(id: Int) {
        val response = api.deleteModel(id)
        check(response.isSuccessful) {
            "Delete model failed (HTTP ${response.code()})"
        }
    }

    override suspend fun uploadPdf(
        id: Int,
        fileName: String,
        bytes: ByteArray
    ): String {
        val requestBody = bytes.toRequestBody("application/pdf".toMediaType())
        val part = MultipartBody.Part.createFormData("file", fileName, requestBody)
        return api.uploadPdf(id, part).message
    }

    override suspend fun downloadModelPdf(id: Int): ByteArray =
        api.downloadModelPdf(id).bytes()

    override suspend fun downloadReportPdf(): ByteArray =
        api.getGeneratedReport().bytes()
}

private object MobileDtoMapper {
    fun toDomain(dto: com.example.mobilemodels.data.remote.dto.MobileDto) =
        dto.toDomain()
}
