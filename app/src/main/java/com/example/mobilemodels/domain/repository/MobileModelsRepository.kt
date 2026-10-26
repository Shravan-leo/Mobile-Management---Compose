package com.example.mobilemodels.domain.repository

import com.example.mobilemodels.domain.model.MobileModel

interface MobileModelsRepository {
    suspend fun getModels(): List<MobileModel>
    suspend fun addModel(model: MobileModel)
    suspend fun deleteModel(id: Int)
    suspend fun uploadPdf(id: Int, fileName: String, bytes: ByteArray): String
    suspend fun downloadModelPdf(id: Int): ByteArray
    suspend fun downloadReportPdf(): ByteArray
}
