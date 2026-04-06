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
    var metodoPagoSeleccionado by mutableStateOf("Efectivo")

    // ── Cupón ────────────────────────────────────────────────────────────────
    var cuponInput by mutableStateOf("")                  // Lo que escribe el usuario
    var cuponAplicado by mutableStateOf<String?>(null)    // Código confirmado
    var cuponDescuento by mutableStateOf(0.0)             // Porcentaje (ej: 33.0)
    var cuponDescripcion by mutableStateOf("")            // "33.0% de descuento"
    var cuponError by mutableStateOf<String?>(null)       // Mensaje de error
    var validandoCupon by mutableStateOf(false)           // Loading del botón

    private val api = RetrofitClient.instancia

    // ── Carrito ──────────────────────────────────────────────────────────────
    fun añadirProducto(producto: Producto) {
        val itemExistente = items.find { it.producto.id == producto.id }
        if (itemExistente != null) {
            val index = items.indexOf(itemExistente)
            items[index] = itemExistente.copy(cantidad = itemExistente.cantidad + 1)
        } else {
            items.add(ItemCarrito(producto, 1))
        }
    }

    fun quitarProducto(producto: Producto) {
        val itemExistente = items.find { it.producto.id == producto.id }
        if (itemExistente != null) {
            if (itemExistente.cantidad > 1) {
                val index = items.indexOf(itemExistente)
                items[index] = itemExistente.copy(cantidad = itemExistente.cantidad - 1)
            } else {
                items.remove(itemExistente)
            }
        }
    }

    // ── Cálculo de totales ───────────────────────────────────────────────────

    /** Total bruto sin descuento */
    fun calcularSubtotal() = items.sumOf { it.producto.precio * it.cantidad }

    /** Importe que se ahorra con el cupón */
    fun calcularAhorro(): Double {
        if (cuponDescuento <= 0.0) return 0.0
        return calcularSubtotal() * (cuponDescuento / 100.0)
    }

    /** Total final que se cobra (el que va al pedido) */
    fun calcularTotal(): Double = calcularSubtotal() - calcularAhorro()

    fun contadorTotal() = items.sumOf { it.cantidad }

    // ── Cupones ──────────────────────────────────────────────────────────────

    fun validarCupon() {
        val codigo = cuponInput.trim()
        if (codigo.isBlank()) {
            cuponError = "Introduce un código"
            return
        }

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            cuponError = "Debes estar logueado"
            return
        }

        viewModelScope.launch {
            validandoCupon = true
            cuponError = null
            try {
                // 1. Pillamos el Token igual que en finalizarPedido
                val token = "Bearer ${user.getIdToken(false).await().token}"

                // 2. Llamamos a la API pasando el Token
                val respuesta = api.validarCupon(token, mapOf("codigo" to codigo))

                if (respuesta.isSuccessful) {
                    val res = respuesta.body()

                    if (res != null && res.valido) {
                        // ✅ ÉXITO
                        cuponAplicado = res.codigo ?: codigo.uppercase()
                        cuponDescuento = res.descuento ?: 0.0
                        cuponDescripcion = res.descripcion ?: "Descuento aplicado"
                        cuponInput = ""
                        cuponError = null
                    } else {
                        // ❌ CUPÓN INVÁLIDO (El backend dice que no existe o expiró)
                        cuponError = "El cupón '$codigo' no existe o ha expirado"
                    }
                } else {
                    // ❌ ERROR DE SERVIDOR (401, 403, 500...)
                    cuponError = when(respuesta.code()) {
                        401 -> "Sesión expirada, vuelve a entrar"
                        403 -> "No tienes permiso para usar cupones"
                        else -> "Error en el servidor (${respuesta.code()})"
                    }
                }
            } catch (e: Exception) {
                cuponError = "Sin conexión con el servidor"
                e.printStackTrace()
            } finally {
                validandoCupon = false
            }
        }
    }

    fun quitarCupon() {
        cuponAplicado    = null
        cuponDescuento   = 0.0
        cuponDescripcion = ""
        cuponError       = null
        cuponInput       = ""
    }

    // ── Pedido ───────────────────────────────────────────────────────────────
    fun finalizarPedido(
        nombreCli: String,
        telCli: String,
        dirCli: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        viewModelScope.launch {
            enviandoPedido = true
            try {
                val token = "Bearer ${user.getIdToken(false).await().token}"
                val nuevoPedido = Pedido(
                    idUsuario      = user.uid,
                    nombreCliente  = nombreCli,
                    telefono       = telCli,
                    direccion      = dirCli,
                    notasGenerales = notasInput,
                    metodoPago     = metodoPagoSeleccionado, // ✅ Enviamos el método de pago
                    productos      = items.map {
                        DetallePedido(it.producto.id, it.producto.nombre, it.cantidad, it.producto.precio)
                    },
                    // --- AQUÍ ESTÁ LO QUE FALTABA ---
                    total          = calcularTotal(),   // Enviamos el total ya descontado
                    cupon          = cuponAplicado,     // Enviamos "LOLO" (o null si no hay)
                    descuento      = calcularAhorro()   // Enviamos el importe que se ha restado
                )
                val response = api.realizarPedido(token, nuevoPedido)
                if (response.isSuccessful) {
                    vaciarCarrito()
                    notasInput = ""
                    onSuccess()
                } else {
                    onError("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                onError("Error de red")
            } finally {
                enviandoPedido = false
            }
        }
    }

    fun vaciarCarrito() {
        items.clear()
        quitarCupon()
    }
}