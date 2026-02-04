package com.illouncampero.illouncampero.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.illouncampero.illouncampero.model.Producto

// Representa una línea del carrito: El producto y cuántas unidades lleva
data class ItemCarrito(
    val producto: Producto,
    var cantidad: Int
)

class CarritoViewModel : ViewModel() {
    // Lista reactiva de items en el carrito
    var items = mutableStateListOf<ItemCarrito>()

    // Función para añadir un producto al carrito
    fun añadirProducto(producto: Producto) {
        // Miramos si el producto ya estaba en la cesta
        val itemExistente = items.find { it.producto.getId() == producto.getId() }

        if (itemExistente != null) {
            // Si ya estaba, le sumamos 1 a la cantidad
            val index = items.indexOf(itemExistente)
            items[index] = itemExistente.copy(cantidad = itemExistente.cantidad + 1)
        } else {
            // Si es nuevo, lo añadimos con cantidad 1
            items.add(ItemCarrito(producto, 1))
        }
    }

    // Función para quitar o reducir cantidad
    fun quitarProducto(producto: Producto) {
        val itemExistente = items.find { it.producto.getId() == producto.getId() }
        if (itemExistente != null) {
            if (itemExistente.cantidad > 1) {
                val index = items.indexOf(itemExistente)
                items[index] = itemExistente.copy(cantidad = itemExistente.cantidad - 1)
            } else {
                items.remove(itemExistente)
            }
        }
    }

    // Calcular el precio total de la compra
    fun calcularTotal(): Double {
        return items.sumOf { it.producto.getPrecio() * it.cantidad }
    }

    // Saber cuántos productos totales hay (para el circulito rojo del icono)
    fun contadorTotal(): Int {
        return items.sumOf { it.cantidad }
    }

    fun vaciarCarrito() {
        items.clear()
    }
}