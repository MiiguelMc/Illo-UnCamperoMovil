package com.illouncampero.illouncampero.model

data class Producto(
    private val id: String = "",
    private var nombre : String ="",
    private var descripcion : String = "",
    private var precio : Double,
    private var imagenURL : String = "",
    private var categoria : String = "", // "camperos", "bebidas", "entrantes"
    private var disponible : Boolean = true,
)