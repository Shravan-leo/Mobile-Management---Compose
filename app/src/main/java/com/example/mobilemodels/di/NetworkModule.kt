package com.example.mobilemodels.di

import com.example.mobilemodels.data.remote.MobileModelsApi
import com.example.mobilemodels.data.repository.MobileModelsRepositoryImpl
import com.example.mobilemodels.domain.repository.MobileModelsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    // Android emulator -> FastAPI running on your development computer.
    // For a physical device use your computer's LAN IP. Use HTTPS in production.
    //private const val API_BASE_URL = "http://10.0.2.2:8000/"
   // private const val API_BASE_URL = "https://stock-issued-interpretation-forecast.trycloudflare.com/"
    private const val API_BASE_URL = "https://van-representatives-bike-speak.trycloudflare.com/"

    @Provides @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    @Provides @Singleton
    fun provideRetrofit(
        client: OkHttpClient
    ): Retrofit {
        val gson = com.google.gson.GsonBuilder()
            .setLenient()
            .create()
        return Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides @Singleton
    fun provideApi(retrofit: Retrofit): MobileModelsApi =
        retrofit.create(MobileModelsApi::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton
    abstract fun bindRepository(
        implementation: MobileModelsRepositoryImpl
    ): MobileModelsRepository
}
