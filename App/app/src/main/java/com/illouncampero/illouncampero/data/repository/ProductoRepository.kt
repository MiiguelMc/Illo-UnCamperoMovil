package com.illouncampero.illouncampero.data.repository

import com.illouncampero.illouncampero.data.network.RetrofitClient
import com.illouncampero.illouncampero.model.Producto

class ProductoRepository {
    private val api = RetrofitClient.instancia

    suspend fun obtenerCarta() = api.getProductos()

    suspend fun subirNuevoCampero(producto: Producto) = api.addProducto(producto)

    suspend fun eliminarCampero(id: String) = api.deleteProducto(id)
}