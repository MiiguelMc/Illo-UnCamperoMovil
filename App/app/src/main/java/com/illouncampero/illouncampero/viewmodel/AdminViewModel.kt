package com.illouncampero.illouncampero.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.illouncampero.illouncampero.data.repository.ProductoRepository // Asumo que tienes uno
import com.illouncampero.illouncampero.model.Producto
import kotlinx.coroutines.launch

class AdminViewModel : ViewModel() {
    // Repositorio (asegúrate de tener la lógica de UPDATE en tu API/Repo)
    // Si no tienes repo separado, puedes usar el mismo que ProductoViewModel
    private val repository = ProductoRepository()

    // Estado para controlar el Modal
    var mostrarModal by mutableStateOf(false)
    var productoSeleccionado by mutableStateOf<Producto?>(null)

    // Campos del formulario de edición
    var nombreEdit by mutableStateOf("")
    var precioEdit by mutableStateOf("")
    var categoriaEdit by mutableStateOf("")
    var subcategoriaEdit by mutableStateOf("")
    var descripcionEdit by mutableStateOf("")
    var imagenUrlEdit by mutableStateOf("")
    var disponibleEdit by mutableStateOf(true)

    var cargandoEdicion by mutableStateOf(false)

    fun guardarCambios(productoViewModel: ProductoViewModel, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val idOriginal = productoSeleccionado?.id ?: return

        val productoEditado = productoSeleccionado!!.copy(
            nombre = nombreEdit,
            precio = precioEdit.toDoubleOrNull() ?: 0.0,
            descripcion = descripcionEdit,
            disponible = disponibleEdit // <--- ESTO es lo que quieres cambiar
        )

        viewModelScope.launch {
            cargandoEdicion = true
            val exito = repository.actualizarProducto(productoEditado)

            if (exito) {
                // AQUÍ ESTÁ LA MAGIA: Refrescamos la lista del otro ViewModel
                productoViewModel.cargarProductos()
                mostrarModal = false
                onSuccess()
            } else {
                onError("No se pudo conectar con el servidor")
            }
            cargandoEdicion = false
        }
    }

    // Función para abrir el modal y rellenar datos
    fun prepararEdicion(producto: Producto) {
        productoSeleccionado = producto
        nombreEdit = producto.nombre
        precioEdit = producto.precio.toString()
        categoriaEdit = producto.categoria ?: ""
        subcategoriaEdit = producto.subcategoria ?: ""
        descripcionEdit = producto.descripcion ?: ""
        imagenUrlEdit = producto.imagenUrl ?: ""
        disponibleEdit = producto.disponible
        mostrarModal = true
    }

    // Aquí llamarías a tu API (Retrofit) para hacer el PUT/PATCH
    fun actualizarProducto(productoViewModel: ProductoViewModel, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val id = productoSeleccionado?.id ?: return

        val productoActualizado = Producto(
            id = id,
            nombre = nombreEdit,
            precio = precioEdit.toDoubleOrNull() ?: 0.0,
            categoria = categoriaEdit,
            subcategoria = subcategoriaEdit,
            descripcion = descripcionEdit,
            imagenUrl = imagenUrlEdit,
            disponible = disponibleEdit
        )

        viewModelScope.launch {
            cargandoEdicion = true
            try {
                // Aquí deberías llamar a un método 'update' en tu repository
                // Ejemplo: repository.actualizarProducto(productoActualizado)

                // Por ahora, simulamos el éxito y refrescamos la lista general
                // productoViewModel.cargarProductos()

                mostrarModal = false
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Error desconocido")
            } finally {
                cargandoEdicion = false
            }
        }
    }
}