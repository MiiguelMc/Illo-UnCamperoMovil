package com.illouncampero.illouncampero.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMisPedidos(navController: NavController, viewModel: PedidoViewModel) {
    val MarronBK = Color(0xFF2D1406)
    val CremaBK = Color(0xFFF5EBDC)
    val naranjaIllo = Color(0xFFF39200)

    LaunchedEffect(Unit) { viewModel.cargarPedidos() }

    Scaffold(
        containerColor = CremaBK,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.pedidos_titulo), color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, stringResource(R.string.pedidos_atras), tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MarronBK)
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                viewModel.cargando -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = naranjaIllo)
                viewModel.listaPedidos.isEmpty() -> Text(stringResource(R.string.pedidos_sin_pedidos), modifier = Modifier.align(Alignment.Center), color = Color.Gray)
                else -> LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(viewModel.listaPedidos) { pedido ->
                        CardPedido(pedido) {
                            viewModel.pedidoSeleccionado = pedido
                            navController.navigate("detalle_pedido")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CardPedido(pedido: Pedido, onClick: () -> Unit) {
    val verdeIllo = Color(0xFF008445)
    val RojoBK = Color(0xFFD62300)
    val numeroPedido = stringResource(R.string.pedidos_numero, pedido.id?.takeLast(5) ?: "---")
    val totalLabel = stringResource(R.string.pedidos_total, String.format("%.2f", pedido.total))

    Card(modifier = Modifier.fillMaxWidth(), shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f)) {
                Text(numeroPedido, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(pedido.estado, color = if (pedido.estado == "ENTREGADO") verdeIllo else RojoBK, fontWeight = FontWeight.Medium)
                Text(totalLabel, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
            }
            Button(onClick = onClick, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF39200))) {
                Text(stringResource(R.string.pedidos_ver_detalles), color = Color.White)
            }
        }
    }
}