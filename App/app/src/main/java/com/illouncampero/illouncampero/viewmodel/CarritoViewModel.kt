package com.illouncampero.illouncampero.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.illouncampero.illouncampero.data.network.RetrofitClient
import com.illouncampero.illouncampero.model.DetallePedido
import com.illouncampero.illouncampero.model.Pedido
import com.illouncampero.illouncampero.model.Producto
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class ItemCarrito(val producto: Producto, var cantidad: Int)

class CarritoViewModel : ViewModel() {
    var items = mutableStateListOf<ItemCarrito>()
    var enviandoPedido by mutableStateOf(false)
    var notasInput by mutableStateOf("")
    var metodoPagoSeleccionado by mutableStateOf("Efectivo") // "Efectivo" o "Tarjeta"

    private val api = RetrofitClient.instancia

    fun añadirProducto(producto: Producto) {
        val itemExistente = items.find { it.producto.id == producto.id }
        if (itemExistente != null) {
            val index = items.indexOf(itemExistente)
            // Actualizamos la posición con una copia y nueva cantidad
            items[index] = itemExistente.copy(cantidad = itemExistente.cantidad + 1)
        } else {
            // Si no existe, lo añadimos nuevo
            items.add(ItemCarrito(producto, 1))
        }
    }

    fun quitarProducto(producto: Producto) {
        val itemExistente = items.find { it.producto.id == producto.id }
        if (itemExistente != null) {
            if (itemExistente.cantidad > 1) {
                val index = items.indexOf(itemExistente)
                items[index] = itemExistente.copy(cantidad = itemExistente.cantidad - 1)
            } else { items.remove(itemExistente) }
        }
    }

    fun finalizarPedido(nombreCli: String, telCli: String, dirCli: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        viewModelScope.launch {
            enviandoPedido = true
            try {
                val token = "Bearer ${user.getIdToken(false).await().token}"
                val nuevoPedido = Pedido(
                    idUsuario = user.uid,
                    nombreCliente = nombreCli,
                    telefono = telCli,
                    direccion = dirCli,
                    notasGenerales = notasInput,
                    total = calcularTotal(),
                    productos = items.map {
                        DetallePedido(it.producto.id, it.producto.nombre, it.cantidad, it.producto.precio)
                    }
                )
                val response = api.realizarPedido(token, nuevoPedido)
                if (response.isSuccessful) {
                    vaciarCarrito()
                    notasInput = ""
                    onSuccess()
                } else { onError("Error: ${response.code()}") }
            } catch (e: Exception) { onError("Error de red") }
            finally { enviandoPedido = false }
        }
    }

    fun calcularTotal() = items.sumOf { it.producto.precio * it.cantidad }
    fun contadorTotal() = items.sumOf { it.cantidad }
    fun vaciarCarrito() = items.clear()
}