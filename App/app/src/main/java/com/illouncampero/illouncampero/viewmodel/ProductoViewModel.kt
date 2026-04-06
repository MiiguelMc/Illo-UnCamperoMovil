package com.illouncampero.illouncampero.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.illouncampero.illouncampero.data.repository.ProductoRepository
import com.illouncampero.illouncampero.model.Producto
import kotlinx.coroutines.launch

class ProductoViewModel : ViewModel() {
    private val repository = ProductoRepository()

    // --- ESTADOS DE LA LISTA ---
    var listaProductos = mutableStateListOf<Producto>()
    val listaOfertas: List<Producto>
        get() = listaProductos.filter { it.categoria?.lowercase() == "oferta" }.take(4)

    val ofertaDestacada: Producto?
        get() = listaOfertas.firstOrNull()

    var cargando by mutableStateOf(false)
    var mensajeError by mutableStateOf<String?>(null)

    // --- ESTADOS DEL FORMULARIO (LOGICA DE ENTRADA) ---
    var nombreInput by mutableStateOf("")
    var precioInput by mutableStateOf("")
    var descripcionInput by mutableStateOf("")
    var categoriaInput by mutableStateOf("")

    var subcategoriaInput by mutableStateOf("")

    var imagenUrlInput by mutableStateOf("")
    var disponibleInput by mutableStateOf(true)

    init {
        cargarProductos() // Carga automática al iniciar
    }

    fun cargarProductos() {
        viewModelScope.launch {
            cargando = true
            mensajeError = null // Limpiamos errores previos
            try {
                val res = repository.obtenerCarta()
                listaProductos.clear()
                if (res != null) {
                    listaProductos.addAll(res)
                }
            } catch (e: Exception) {
                mensajeError = "Error: ${e.localizedMessage}. ¿Está el servidor encendido?"
                e.printStackTrace()
            } finally {
                cargando = false
            }
        }
    }

    fun guardarProducto(onSuccess: () -> Unit, onError: (String) -> Unit) {
        // 1. Limpiamos el precio: cambiamos comas por puntos por si el usuario escribe "5,50"
        val precioLimpio = precioInput.replace(",", ".")
        val precio = precioLimpio.toDoubleOrNull() ?: 0.0

        // 2. Validación básica antes de enviar
        if (nombreInput.isBlank() || precio <= 0) {
            onError("Nombre y precio (mayor a 0) son obligatorios")
            return
        }

        viewModelScope.launch {
            cargando = true
            try {
                val nuevoProd = Producto(
                    // Asegúrate de que el constructor de tu clase Producto
                    // coincida con estos campos
                    nombre = nombreInput,
                    precio = precio,
                    descripcion = descripcionInput,
                    categoria = categoriaInput,
                    subcategoria = subcategoriaInput,
                    imagenUrl = imagenUrlInput,
                    disponible = disponibleInput
                )

                // 3. Llamada al repositorio
                repository.subirNuevoCampero(nuevoProd)

                // 4. Si todo va bien:
                limpiarFormulario()
                cargarProductos() // Recargamos la lista para ver el nuevo
                onSuccess()
            } catch (e: Exception) {
                // 5. Capturamos el error real (aquí verás si es 403, 500, etc.)
                onError("Error al guardar: ${e.localizedMessage}")
            } finally {
                cargando = false
            }
        }
    }

    fun eliminarProducto(id: String?, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (id.isNullOrEmpty()) {
            onError("El ID del producto está vacío. Revisa si Firebase/SpringBoot lo están enviando.")
            return
        }

        viewModelScope.launch {
            try {
                // 1. Intentamos borrar en el backend (SpringBoot)
                repository.eliminarCampero(id)

                // 2. Si la llamada al repositorio no lanzó excepción, borramos de la lista local
                // Usamos removeAll para asegurar que Compose detecte el cambio en el mutableStateListOf
                val eliminado = listaProductos.removeAll { it.id == id }

                if (eliminado) {
                    onSuccess()
                } else {
                    // Si llegamos aquí, el ID se mandó al server pero no estaba en la lista de la pantalla
                    cargarProductos() // Recargamos por si acaso
                    onSuccess()
                }
            } catch (e: Exception) {
                onError("Error al eliminar: ${e.localizedMessage}")
            }
        }
    }

    private fun limpiarFormulario() {
        nombreInput = ""
        precioInput = ""
        descripcionInput = ""
        categoriaInput = ""
        imagenUrlInput = ""
        subcategoriaInput = ""
        disponibleInput = true
    }
}