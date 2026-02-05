package com.illouncampero.illouncampero.model

data class Pedido(
    val id: String? = null,
    val idUsuario: String = "",
    val nombreCliente: String? = "", // Cambiamos de null a vacío para evitar errores
    val direccion: String? = "",
    val telefono: String? = "",
    val estado: String = "PENDIENTE",
    val fecha: Long = System.currentTimeMillis(),
    val total: Double = 0.0,
    val notasGenerales: String? = null,
    val productos: List<DetallePedido> = emptyList()
)

data class DetallePedido(
    val productoId: String,
    val nombre: String, // En tu DB sale como "nombre", asegúrate que coincida
    val cantidad: Int,
    val precioUnidad: Double, // En tu DB sale "precioUnidad"
    val notas: String? = null
)