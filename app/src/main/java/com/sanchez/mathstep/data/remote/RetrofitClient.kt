package com.sanchez.mathstep.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

/**
 * RetrofitClient — Singleton del cliente HTTP.
 *
 * IMPORTANTE (bug corregido): MathJS devuelve texto plano (ej. "4"), no
 * JSON. Antes se usaba GsonConverterFactory, que espera JSON válido
 * (como "\"4\""); con texto plano eso fallaba en tiempo de ejecución.
 * ScalarsConverterFactory maneja respuestas String/texto plano directamente.
 *
 * También se agregaron timeouts: sin ellos, una red lenta o caída dejaba
 * la app esperando indefinidamente en vez de fallar rápido y usar el
 * evaluador local.
 */
object RetrofitClient {

    private const val BASE_URL = "https://api.mathjs.org/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(8, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .build()

    val mathApi: MathApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(MathApiService::class.java)
    }
}