package com.illouncampero.illouncampero.model

import com.google.gson.annotations.SerializedName

data class Producto(
    @SerializedName("id") val id: String = "",
    @SerializedName("nombre") val nombre: String = "",
    @SerializedName("descripcion") val descripcion: String = "",
    @SerializedName("precio") val precio: Double = 0.0,
    @SerializedName("imagenUrl") val imagenUrl: String = "",
    @SerializedName("categoria") val categoria: String = "",
    @SerializedName("subcategoria") val subcategoria: String = "",
    @SerializedName("disponible") val disponible: Boolean = true
)