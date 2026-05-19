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

    var actualizandoPedidoId = mutableStateOf<String?>(null)

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
            // 1. Buscamos el índice
            val indice = listaPedidosCocina.indexOfFirst { it.id == pedido.id }
            if (indice != -1) {
                // --- ACTUALIZACIÓN INSTANTÁNEA ---
                // Guardamos el estado viejo por si hay error
                val estadoAnterior = listaPedidosCocina[indice].estado

                // Cambiamos el estado en local YA
                if (nuevoEstado == "ENTREGADO") {
                    listaPedidosCocina.removeAt(indice)
                } else {
                    listaPedidosCocina[indice] = listaPedidosCocina[indice].copy(estado = nuevoEstado)
                }

                // 2. Ahora lanzamos la petición al servidor en segundo plano
                viewModelScope.launch {
                    try {
                        actualizandoPedidoId.value = pedido.id
                        val exito = repository.cambiarEstado(pedido.id, nuevoEstado)
                        if (!exito) {
                            // Si falla, podrías recargar o volver atrás, pero normalmente irá bien
                            cargarTodosLosPedidos()
                        }
                    } catch (e: Exception) {
                        cargarTodosLosPedidos()
                    } finally {
                        actualizandoPedidoId.value = null
                    }
                }
            }
        }
    }
}