package com.illouncampero.illouncampero.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.illouncampero.illouncampero.data.network.RetrofitClient
import com.illouncampero.illouncampero.model.Producto
import kotlinx.coroutines.tasks.await // Necesario para el .await()

class ProductoRepository {
    private val api = RetrofitClient.instancia

    // Función privada para no repetir código: obtiene el token actual del usuario
    private suspend fun obtenerToken(): String {
        val user = FirebaseAuth.getInstance().currentUser
        val result = user?.getIdToken(false)?.await()
        return "Bearer ${result?.token}"
    }

    suspend fun obtenerCarta() = api.getProductos()

    suspend fun subirNuevoCampero(producto: Producto) {
        val token = obtenerToken()
        val respuesta = api.addProducto(token, producto)

        // SI NO HACES ESTO, EL VM NUNCA SABRÁ QUE FALLÓ
        if (!respuesta.isSuccessful) {
            throw Exception("Error del servidor: ${respuesta.code()} - ${respuesta.errorBody()?.string()}")
        }
    }

    suspend fun eliminarCampero(id: String) {
        val token = obtenerToken()
        val respuesta = api.deleteProducto(token, id)

        if (!respuesta.isSuccessful) {
            throw Exception("No se pudo eliminar: ${respuesta.code()}")
        }
    }

    // En data/repository/ProductoRepository.kt
    suspend fun actualizarProducto(producto: Producto): Boolean {
        return try {
            val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
            val token = user?.getIdToken(false)?.await()?.token
            val response = api.actualizarProducto("Bearer $token", producto.id, producto)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }
}