package com.illouncampero.illouncampero.model

import com.google.gson.annotations.SerializedName

class Producto(
    @SerializedName("id") private val id: String = "",
    @SerializedName("nombre") private var nombre: String = "",
    @SerializedName("descripcion") private var descripcion: String = "",
    @SerializedName("precio") private var precio: Double = 0.0,
    @SerializedName("imagenUrl") private var imagenUrl: String = "",
    @SerializedName("categoria") private var categoria: String = "",
    @SerializedName("disponible") private var disponible: Boolean = true
) {
    fun getId() = id
    fun getNombre() = nombre
    fun getDescripcion() = descripcion
    fun getPrecio() = precio
    fun getImagenUrl() = imagenUrl
    fun getCategoria() = categoria
    fun isDisponible() = disponible
}