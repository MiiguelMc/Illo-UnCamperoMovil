package com.illouncampero.illouncampero.data.network
import com.illouncampero.illouncampero.model.Producto
import retrofit2.http.GET

interface IlloApiService {
    // Dile a tu colega que te dé la URL de los productos.
    // Si él ha puesto @GetMapping("/api/productos"), tú pones esto:
    @GET("api/productos")
    suspend fun obtenerProductos(): List<Producto>
}