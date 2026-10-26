package com.example.mobilemodels.data.remote

import com.example.mobilemodels.data.remote.dto.MobileDto
import com.example.mobilemodels.data.remote.dto.UploadPdfResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

data class CreateMobileRequest(
    val id: Int,
    val name: String,
    val price: Int
)

interface MobileModelsApi {
    @GET("api/models")
    suspend fun getModels(): ResponseBody

    @POST("api/model/")
    suspend fun addModel(
        @Body request: CreateMobileRequest
    ): Response<MobileDto>

    @DELETE("api/model/{model_id}/delete")
    suspend fun deleteModel(
        @Path("model_id") id: Int
    ): Response<Unit>

    @GET("api/reports/pdf")
    suspend fun getGeneratedReport(): ResponseBody

    @Multipart
    @POST("api/models/{model_id}/pdf")
    suspend fun uploadPdf(
        @Path("model_id") id: Int,
        @Part file: MultipartBody.Part
    ): UploadPdfResponse

    @GET("api/models/{model_id}/pdf")
    suspend fun downloadModelPdf(
        @Path("model_id") id: Int
    ): ResponseBody
}
