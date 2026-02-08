package com.illouncampero.illouncampero.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.illouncampero.illouncampero.data.repository.PedidoRepository
import com.illouncampero.illouncampero.model.Pedido
import kotlinx.coroutines.launch

class PedidoViewModel : ViewModel() {
    private val repository = PedidoRepository()

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
}