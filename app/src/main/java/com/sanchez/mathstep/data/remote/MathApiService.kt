package com.sanchez.mathstep.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * MathApiService — interfaz de Retrofit para MathJS API.
 *
 * Retrofit lee las anotaciones y genera la implementación HTTP.
 * @GET: indica que es una petición GET al path indicado.
 * @Query: agrega el parámetro como query string en la URL.
 *   Ejemplo: https://api.mathjs.org/v4/?expr=2%2B3&precision=4
 *
 * suspend: la función se ejecuta en una coroutine sin bloquear
 * el hilo principal. Retrofit 2.6+ soporta suspend nativamente.
 *
 * String: la API devuelve texto plano, no JSON, así que no
 * necesitamos una data class para deserializar.
 */
interface MathApiService {

    @GET("v4/")
    suspend fun evaluate(
        @Query("expr") expression: String,
        @Query("precision") precision: Int = 4
    ): String
}