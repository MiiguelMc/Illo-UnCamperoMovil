// ─────────────────────────────────────────────────────────────────────────────
// PantallaCocina.kt
// ─────────────────────────────────────────────────────────────────────────────
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.illouncampero.illouncampero.R
import com.illouncampero.illouncampero.model.Pedido
import com.illouncampero.illouncampero.viewmodel.PedidoViewModel

val NaranjaIllo = Color(0xFFF39200)
val MarronCocina = Color(0xFF2D1406)
val CremaFondo = Color(0xFFF5EBDC)
val RojoUrgente = Color(0xFFD62300)
val VerdeListo = Color(0xFF008445)
val AmarilloProceso = Color(0xFFFBC02D)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCocina(navController: NavController, viewModel: PedidoViewModel) {
    LaunchedEffect(Unit) { viewModel.cargarTodosLosPedidos() }

    Scaffold(
        containerColor = CremaFondo,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.cocina_titulo), color = Color.White, fontWeight = FontWeight.Black) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, stringResource(R.string.cocina_atras), tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.cargarTodosLosPedidos() }) {
                        Icon(Icons.Default.Refresh, stringResource(R.string.cocina_refrescar), tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NaranjaIllo)
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                viewModel.cargando -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = NaranjaIllo)
                viewModel.listaPedidosCocina.isEmpty() -> Text(stringResource(R.string.cocina_sin_comandas), modifier = Modifier.align(Alignment.Center), color = Color.Gray)
                else -> {
                    val activos by remember { derivedStateOf { viewModel.listaPedidosCocina.filter { it.estado != "ENTREGADO" } } }
                    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(items = activos, key = { it.id ?: it.hashCode() }) { pedido ->
                            ComandaCard(pedido = pedido, viewModel = viewModel, onAccion = { viewModel.avanzarEstado(pedido) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ComandaCard(pedido: Pedido, viewModel: PedidoViewModel, onAccion: () -> Unit) {
    val colorEstado = when (pedido.estado) {
        "PENDIENTE" -> Color.Red; "COCINANDO" -> AmarilloProceso
        "REPARTO" -> Color(0xFF2196F3); "ENTREGADO" -> VerdeListo
        else -> Color.Gray
    }
    val ordenLabel = stringResource(R.string.cocina_orden, pedido.id?.takeLast(5) ?: "---")
    val clienteLabel = stringResource(R.string.cocina_cliente, pedido.nombreCliente ?: "")

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(6.dp), border = BorderStroke(2.dp, colorEstado)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(ordenLabel, fontWeight = FontWeight.Black, fontSize = 22.sp, color = MarronCocina)
                Surface(color = colorEstado, shape = RoundedCornerShape(4.dp)) {
                    Text(pedido.estado, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = if (pedido.estado == "COCINANDO") Color.Black else Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
            Text(clienteLabel, color = Color.Gray, fontSize = 14.sp)
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 2.dp)

            pedido.productos.forEach { item ->
                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                    Text("${item.cantidad}x ", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = RojoUrgente)
                    Column {
                        Text(item.nombre.uppercase(), fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color.Black)
                        if (!item.notas.isNullOrBlank()) {
                            Text(stringResource(R.string.cocina_nota, item.notas), color = NaranjaIllo, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }

            if (!pedido.notasGenerales.isNullOrBlank()) {
                Spacer(Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth().background(Color(0xFFFFF9C4)).padding(8.dp)) {
                    Text(stringResource(R.string.cocina_notas_envio, pedido.notasGenerales), fontSize = 14.sp)
                }
            }

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = onAccion,
                enabled = viewModel.actualizandoPedidoId.value != pedido.id,
                modifier = Modifier.fillMaxWidth().height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorEstado),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (viewModel.actualizandoPedidoId.value == pedido.id) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    val textoBoton = when (pedido.estado) {
                        "PENDIENTE" -> stringResource(R.string.cocina_empezar)
                        "COCINANDO" -> stringResource(R.string.cocina_pasar_reparto)
                        "REPARTO" -> stringResource(R.string.cocina_marcar_entregado)
                        else -> stringResource(R.string.cocina_finalizar)
                    }
                    Text(textoBoton, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = if (pedido.estado == "COCINANDO") Color.Black else Color.White)
                }
            }
        }
    }
}