package com.sanchez.mathstep.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/**
 * NetworkUtils — verifica si hay internet ANTES de llamar a la API.
 * Sin esto, sin conexión la app se quedaba esperando el timeout de
 * Retrofit en vez de usar el cálculo local de inmediato.
 */
object NetworkUtils {
    fun isOnline(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return false
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}