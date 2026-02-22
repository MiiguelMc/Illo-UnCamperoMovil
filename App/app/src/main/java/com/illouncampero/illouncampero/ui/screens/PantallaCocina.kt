package com.illouncampero.illouncampero.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.illouncampero.illouncampero.model.Pedido
import com.illouncampero.illouncampero.viewmodel.PedidoViewModel

// Colores del tema "Illo"
val NaranjaIllo = Color(0xFFF39200)
val MarronCocina = Color(0xFF2D1406)
val CremaFondo = Color(0xFFF5EBDC)
val RojoUrgente = Color(0xFFD62300)
val VerdeListo = Color(0xFF008445)
val AmarilloProceso = Color(0xFFFBC02D)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCocina(navController: NavController, viewModel: PedidoViewModel) {

    // 1. Cargamos todos los pedidos al entrar
    LaunchedEffect(Unit) {
        viewModel.cargarTodosLosPedidos()
    }

    Scaffold(
        containerColor = CremaFondo,
        topBar = {
            TopAppBar(
                title = {
                    Text("GESTIÓN DE COCINA 👨‍🍳", color = Color.White, fontWeight = FontWeight.Black)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Atrás", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.cargarTodosLosPedidos() }) {
                        Icon(Icons.Default.Refresh, "Refrescar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NaranjaIllo)
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (viewModel.cargando) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = NaranjaIllo)
            } else if (viewModel.listaPedidosCocina.isEmpty()) {
                Text(
                    text = "No hay comandas activas",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray
                )
            }  else {
                // ESTA LÍNEA ES LA MAGIA:
                // Observa la lista original y se actualiza sola en cuanto cambias un copy()
                val activos by remember {
                    derivedStateOf {
                        viewModel.listaPedidosCocina.filter { it.estado != "ENTREGADO" }
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // USAR 'activos' (la lista reactiva) y poner una KEY
                    items(
                        items = activos,
                        key = { it.id ?: it.hashCode() } // La KEY es obligatoria para cambios visuales rápidos
                    ) { pedido ->
                        ComandaCard(
                            pedido = pedido,
                            viewModel = viewModel,
                            onAccion = { viewModel.avanzarEstado(pedido) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ComandaCard(pedido: Pedido, viewModel: PedidoViewModel, onAccion: () -> Unit) {
    val (colorEstado, textoBoton, proximoEstadoNombre) = when (pedido.estado) {
        "PENDIENTE" -> Triple(Color.Red, "EMPEZAR A COCINAR", "PENDIENTE")
        "COCINANDO" -> Triple(AmarilloProceso, "PASAR A REPARTO", "EN COCINA")
        "REPARTO" -> Triple(Color(0xFF2196F3), "MARCAR COMO ENTREGADO", "EN REPARTO")
        "ENTREGADO" -> Triple(VerdeListo, "FINALIZADO", "ENTREGADO")
        else -> Triple(Color.Gray, "---", "DESCONOCIDO")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(6.dp),
        border = BorderStroke(2.dp, colorEstado)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // CABECERA
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ORDEN #${pedido.id?.takeLast(5) ?: "---"}",
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    color = MarronCocina
                )
                Surface(
                    color = colorEstado,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = pedido.estado,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = if (pedido.estado == "COCINANDO") Color.Black else Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Text(text = "Cliente: ${pedido.nombreCliente}", color = Color.Gray, fontSize = 14.sp)

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 2.dp)

            // LISTA DE PRODUCTOS (Texto Grande para leer rápido en cocina)
            pedido.productos.forEach { item ->
                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text(
                        text = "${item.cantidad}x ",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = RojoUrgente
                    )
                    Column {
                        Text(
                            text = item.nombre.uppercase(),
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = Color.Black
                        )
                        if (!item.notas.isNullOrBlank()) {
                            Text(
                                text = "👉 NOTA: ${item.notas}",
                                color = NaranjaIllo,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // NOTAS GENERALES DEL PEDIDO
            if (!pedido.notasGenerales.isNullOrBlank()) {
                Spacer(Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFF9C4))
                        .padding(8.dp)
                ) {
                    Text(text = "NOTAS ENVÍO: ${pedido.notasGenerales}", fontSize = 14.sp)
                }
            }

            Spacer(Modifier.height(20.dp))

            // BOTÓN DE ACCIÓN
            Button(
                onClick = onAccion,
                enabled = viewModel.actualizandoPedidoId.value != pedido.id, // Deshabilitar si se está cargando
                modifier = Modifier.fillMaxWidth().height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorEstado),
                shape = RoundedCornerShape(8.dp)
            ) {

                if (viewModel.actualizandoPedidoId.value == pedido.id) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    val textoBoton = when (pedido.estado) {
                        "PENDIENTE" -> "EMPEZAR A COCINAR"
                        "COCINANDO" -> "PASAR A REPARTO"
                        "REPARTO" -> "MARCAR COMO ENTREGADO"
                        else -> "FINALIZAR"
                    }
                    Text(textoBoton, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = if (pedido.estado == "COCINANDO") Color.Black else Color.White)
                }

            }
        }
    }
}