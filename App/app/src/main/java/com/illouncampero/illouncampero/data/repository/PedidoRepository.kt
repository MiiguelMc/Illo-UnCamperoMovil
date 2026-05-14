package com.illouncampero.illouncampero.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.illouncampero.illouncampero.data.network.RetrofitClient
import com.illouncampero.illouncampero.model.Pedido
import kotlinx.coroutines.tasks.await

class PedidoRepository {
    private val api = RetrofitClient.instancia

    suspend fun obtenerMisPedidos(): List<Pedido> {
        val user = FirebaseAuth.getInstance().currentUser
        val token = user?.getIdToken(false)?.await()?.token
        return api.getMisPedidos("Bearer $token")
    }
    suspend fun obtenerTodosLosPedidos(): List<Pedido> {
        val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        // Pillamos el token para que el backend sepa que somos Admin
        val token = user?.getIdToken(false)?.await()?.token
        return api.getTodosLosPedidos("Bearer $token")
    }

    suspend fun cambiarEstado(idPedido: String, nuevoEstado: String): Boolean {
        return try {
            val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
            val token = user?.getIdToken(false)?.await()?.token

            val response = api.actualizarEstadoPedido("Bearer $token", idPedido, nuevoEstado)

            // No intentamos leer el String, solo miramos si el código es 200
            response.isSuccessful
        } catch (e: Exception) {
            println("DEBUG_ILLO: Error al cambiar estado: ${e.message}")
            false
        }
    }
}