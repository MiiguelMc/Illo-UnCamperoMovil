package com.illouncampero.illouncampero.model

import com.google.gson.annotations.SerializedName

data class Pedido(
    @SerializedName("id") val id: String? = null,
    @SerializedName("idUsuario") val idUsuario: String = "",
    @SerializedName("nombreCliente") val nombreCliente: String? = "",
    @SerializedName("direccion") val direccion: String? = "",
    @SerializedName("telefono") val telefono: String? = "",
    @SerializedName("estado") val estado: String = "PENDIENTE",
    @SerializedName("fecha") val fecha: Long = System.currentTimeMillis(),
    @SerializedName("total") val total: Double = 0.0,
    @SerializedName("notasGenerales") val notasGenerales: String? = null,
    @SerializedName("productos") val productos: List<DetallePedido> = emptyList(),
    @SerializedName("metodoPago") val metodoPago: String = "EFECTIVO",
    @SerializedName("cupon") val cupon: String? = null,
    @SerializedName("descuento") val descuento: Double? = 0.0 // Ponlo como opcional
)

data class DetallePedido(
    @SerializedName("productoId") val productoId: String,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("cantidad") val cantidad: Int,
    @SerializedName("precioUnidad") val precioUnidad: Double,
    @SerializedName("notas") val notas: String? = null
)