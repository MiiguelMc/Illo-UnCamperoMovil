package com.illouncampero.illouncampero.model

data class CuponResponse(
    val valido: Boolean,
    val codigo: String?,
    val descuento: Double?,
    val descripcion: String?
)