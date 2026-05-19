package com.illouncampero.illouncampero.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "https://illo-uncamperobackend.onrender.com/"

    // 1. Creamos un interceptor para ver los fallos en el Logcat
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // 2. Configuramos el cliente OkHttp con tiempos de espera largos (por Render)
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(60, TimeUnit.SECONDS) // Espera 60s para conectar
        .readTimeout(60, TimeUnit.SECONDS)    // Espera 60s para leer datos
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val instancia: IlloApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client) // <-- IMPORTANTE: Añadir el cliente configurado
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IlloApiService::class.java)
    }
}