package com.illouncampero.illouncampero.model

data class Usuario(
    val uid: String = "",
    val nombre: String = "",
    val apellidos: String = "",
    val telefono: String = "",
    val email: String = "",
    val direccion: String = "",
    val rol: String = "cliente"
)