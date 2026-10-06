package com.yarik.watcher.di.module.network

import com.yarik.watcher.logic.source.remote.weather.WeatherNetworkSource
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

const val BASE_URL = "https://api.open-meteo.com/"

private val json = Json {
    ignoreUnknownKeys = true
}

@Module
class WeatherNetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory(contentType))
            .client(client)
            .build()
    }

    @Provides
    fun provideAppSettingsNetworkService(retrofit: Retrofit): WeatherNetworkSource {
        return retrofit.create(WeatherNetworkSource::class.java)
    }
}