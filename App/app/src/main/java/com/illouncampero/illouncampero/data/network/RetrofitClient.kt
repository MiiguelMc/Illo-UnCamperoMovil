package com.illouncampero.illouncampero.data.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // IMPORTANTE: Si usas EMULADOR, la IP de tu PC es 10.0.2.2
    // Si usas MÓVIL REAL, tienes que poner la IP de tu PC (ej: 192.168.1.50)
    // Dile a tu colega en qué puerto corre su Spring (normalmente 8080)
    private const val BASE_URL = "http://10.0.2.2:8080/"

    val instancia: IlloApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IlloApiService::class.java)
    }
}