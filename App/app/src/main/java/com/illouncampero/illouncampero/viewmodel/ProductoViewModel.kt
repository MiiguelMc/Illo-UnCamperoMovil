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
    var cargando by mutableStateOf(false)
    var mensajeError by mutableStateOf<String?>(null)

    // --- ESTADOS DEL FORMULARIO (LOGICA DE ENTRADA) ---
    var nombreInput by mutableStateOf("")
    var precioInput by mutableStateOf("")
    var descripcionInput by mutableStateOf("")
    var categoriaInput by mutableStateOf("")
    var imagenURLInput by mutableStateOf("")
    var disponibleInput by mutableStateOf(true)

    fun cargarProductos() {
        viewModelScope.launch {
            cargando = true
            try {
                val res = repository.obtenerCarta()
                listaProductos.clear()
                listaProductos.addAll(res ?: emptyList())
            } catch (e: Exception) {
                mensajeError = "Error al cargar: ${e.localizedMessage}"
            } finally {
                cargando = false
            }
        }
    }

    fun guardarProducto(onSuccess: () -> Unit) {
        val precio = precioInput.toDoubleOrNull() ?: 0.0
        if (nombreInput.isBlank() || precio <= 0) {
            mensajeError = "Nombre y precio obligatorios"
            return
        }

        viewModelScope.launch {
            cargando = true
            try {
                val nuevoProd = Producto(
                    nombre = nombreInput,
                    precio = precio,
                    descripcion = descripcionInput,
                    categoria = categoriaInput,
                    imagenURL = imagenURLInput,
                    disponible = disponibleInput
                )
                repository.subirNuevoCampero(nuevoProd)
                limpiarFormulario()
                cargarProductos()
                onSuccess()
            } catch (e: Exception) {
                mensajeError = "Error al guardar"
            } finally {
                cargando = false
            }
        }
    }

    fun eliminarProducto(id: String) {
        viewModelScope.launch {
            try {
                repository.eliminarCampero(id)
                listaProductos.removeIf { it.getId() == id }
            } catch (e: Exception) { /* Gestionar error */ }
        }
    }

    private fun limpiarFormulario() {
        nombreInput = ""
        precioInput = ""
        descripcionInput = ""
        categoriaInput = ""
        imagenURLInput = ""
        disponibleInput = true
    }
}