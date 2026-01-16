package com.illouncampero.illouncampero.data.network

import com.illouncampero.illouncampero.model.Producto
import retrofit2.http.*

interface IlloApiService {
    // Para que el cliente y el admin vean la carta
    @GET("api/productos")
    suspend fun getProductos(): List<Producto>

    // Para que el admin añada un campero nuevo
    @POST("api/productos")
    suspend fun addProducto(@Body producto: Producto): Producto

    // Para que el admin borre un producto
    @DELETE("api/productos/{id}")
    suspend fun deleteProducto(@Path("id") id: String): Unit
}