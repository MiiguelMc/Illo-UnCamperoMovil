package com.illouncampero.illouncampero.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.illouncampero.illouncampero.data.network.RetrofitClient
import com.illouncampero.illouncampero.model.Pedido
import kotlinx.coroutines.tasks.await

class PedidoRepository {
    private val api = RetrofitClient.instancia

    suspend fun obtenerMisPedidos(): List<Pedido> {
        val user = FirebaseAuth.getInstance().currentUser

        // 1. Pillamos el UID del usuario actual
        val uid = user?.uid ?: ""

        // 2. Pillamos el Token
        val token = user?.getIdToken(false)?.await()?.token

        // 3. Llamamos a la nueva ruta pasando ambos
        return api.getMisPedidos("Bearer $token", uid)
    }
}