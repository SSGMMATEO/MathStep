package com.sanchez.mathstep.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * RetrofitClient — Singleton del cliente HTTP.
 *
 * Por qué Singleton:
 *   Crear un cliente Retrofit es costoso (crea thread pools, parsers,
 *   conexiones). Un Singleton garantiza que toda la app reutiliza
 *   la misma instancia.
 *
 * HttpLoggingInterceptor: registra en Logcat cada request y response
 * completos. Solo activo en debug; en producción se elimina.
 *
 * GsonConverterFactory: aunque la respuesta de MathJS es texto plano,
 * se incluye para cuando se agregue otra API con JSON.
 */
object RetrofitClient {

    private const val BASE_URL = "https://api.mathjs.org/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val mathApi: MathApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MathApiService::class.java)
    }
}