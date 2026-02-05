package com.illouncampero.illouncampero.data.network

import Usuario
import com.illouncampero.illouncampero.model.Pedido
import com.illouncampero.illouncampero.model.Producto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface IlloApiService {
    // El GET es público según tu SecurityConfig, no necesita token
    @GET("api/productos")
    suspend fun getProductos(): List<Producto>

    // Para añadir, necesitamos el Token de Admin
    @POST("api/productos")
    suspend fun addProducto(
        @Header("Authorization") token: String,
        @Body producto: Producto
    ): Response<Unit>

    // Para borrar, necesitamos el Token de Admin y el ID en la URL
    @DELETE("api/productos/{id}")
    suspend fun deleteProducto(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<Unit>

    // Si en el futuro haces el editar (PUT), se dejaría así:
    @PUT("api/productos/{id}")
    suspend fun updateProducto(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body producto: Producto
    ): Response<Unit>

    @GET("api/usuarios/perfil")
    suspend fun getPerfil(
        @Header("Authorization") token: String
    ): Response<Usuario>

    // Actualizar los datos del usuario
    @PUT("api/usuarios/perfil")
    suspend fun updatePerfil(
        @Header("Authorization") token: String,
        @Body usuario: Usuario
    ): Response<Unit>
    // ... dentro de interface IlloApiService
    @POST("api/pedidos/realizar-pedido")
    suspend fun realizarPedido(
        @Header("Authorization") token: String,
        @Body pedido: Pedido
    ): Response<Unit>
}