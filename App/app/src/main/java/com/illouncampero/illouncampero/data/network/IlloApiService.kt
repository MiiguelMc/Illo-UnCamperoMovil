package com.illouncampero.illouncampero.data.network

import Usuario
import com.illouncampero.illouncampero.model.CuponResponse
import com.illouncampero.illouncampero.model.Pedido
import com.illouncampero.illouncampero.model.Producto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface IlloApiService {

    @GET("api/productos")
    suspend fun getProductos(): List<Producto>

    @POST("api/productos")
    suspend fun addProducto(
        @Header("Authorization") token: String,
        @Body producto: Producto
    ): Response<Unit>

    @DELETE("api/productos/{id}")
    suspend fun deleteProducto(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Response<Unit>

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

    @PUT("api/usuarios/perfil")
    suspend fun updatePerfil(
        @Header("Authorization") token: String,
        @Body usuario: Usuario
    ): Response<Unit>

    @POST("api/pedidos/realizar-pedido")
    suspend fun realizarPedido(
        @Header("Authorization") token: String,
        @Body pedido: Pedido
    ): Response<Map<String, String>>

    @POST("api/pagos/crear-intent")
    suspend fun crearIntentPago(
        @Header("Authorization") token: String,
        @Body body: Map<String, String>
    ): Response<Map<String, String>>

    @GET("api/pedidos/mis-pedidos")
    suspend fun getMisPedidos(
        @Header("Authorization") token: String
    ): List<Pedido>

    @PATCH("api/pedidos/{id}/estado")
    suspend fun actualizarEstadoPedido(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Query("nuevoEstado") nuevoEstado: String
    ): Response<String>

    @GET("api/pedidos/activos")
    suspend fun getTodosLosPedidos(
        @Header("Authorization") token: String
    ): List<Pedido>

    // ← MOVIDO aquí, fuera del companion object y con la ruta correcta bajo /api/
    @PATCH("api/usuarios/{uid}/fcm-token")
    suspend fun actualizarFcmToken(
        @Header("Authorization") token: String,
        @Path("uid") uid: String,
        @Body body: Map<String, String>
    ): Response<Void>

    // En data/network/IlloApiService.kt
    @PUT("api/productos/{id}")
    suspend fun actualizarProducto(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Body producto: Producto
    ): Response<Unit>

    @POST("api/cupones/validar")
    suspend fun validarCupon(
        @Header("Authorization") token: String, // ← Añadimos esto
        @Body body: Map<String, String>
    ): Response<CuponResponse> // ← Usamos el modelo nuevo

    companion object  // companion object vacío — está bien así
}