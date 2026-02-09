package com.illouncampero.illouncampero.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.illouncampero.illouncampero.data.repository.PedidoRepository
import com.illouncampero.illouncampero.model.Pedido
import kotlinx.coroutines.launch

class PedidoViewModel : ViewModel() {
    private val repository = PedidoRepository()
    var listaPedidosCocina = mutableStateListOf<Pedido>()

    var listaPedidos = mutableStateListOf<Pedido>()
    var cargando by mutableStateOf(false)

    // Guardamos el pedido seleccionado para mostrar en la pantalla de detalle
    var pedidoSeleccionado by mutableStateOf<Pedido?>(null)

    fun cargarPedidos() {
        viewModelScope.launch {
            cargando = true
            try {
                val res = repository.obtenerMisPedidos()
                listaPedidos.clear()
                listaPedidos.addAll(res.sortedByDescending { it.fecha }) // Los más nuevos primero
            } catch (e: Exception) {
                // Manejar error
            } finally {
                cargando = false
            }
        }
    }

    fun cargarTodosLosPedidos() {
        viewModelScope.launch {
            cargando = true
            try {
                // Ahora llama a /api/pedidos/activos
                val res = repository.obtenerTodosLosPedidos()
                listaPedidosCocina.clear()
                // No hace falta filtrar aquí porque tu Spring ya filtra los "activos"
                listaPedidosCocina.addAll(res.sortedByDescending { it.fecha })
            } catch (e: Exception) {
                println("DEBUG_ILLO: Error al cargar -> ${e.message}")
            } finally {
                cargando = false
            }
        }
    }

    fun avanzarEstado(pedido: Pedido) {
        val nuevoEstado = when (pedido.estado) {
            "PENDIENTE" -> "COCINANDO"
            "COCINANDO" -> "REPARTO"
            "REPARTO" -> "ENTREGADO"
            else -> null
        }

        if (nuevoEstado != null && pedido.id != null) {
            viewModelScope.launch {
                try {
                    val exito = repository.cambiarEstado(pedido.id, nuevoEstado)
                    if (exito) {
                        // Refrescamos la lista si la llamada fue bien
                        cargarTodosLosPedidos()
                    }
                } catch (e: Exception) {
                    // Si algo falla, que no se cierre la app
                    println("DEBUG_ILLO: Crash evitado en ViewModel: ${e.message}")
                }
            }
        }
    }
}