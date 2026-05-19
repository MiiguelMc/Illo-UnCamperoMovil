package com.illouncampero.illouncampero.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.illouncampero.illouncampero.R
import com.illouncampero.illouncampero.viewmodel.PedidoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetallePedido(navController: NavController, viewModel: PedidoViewModel) {
    val pedido = viewModel.pedidoSeleccionado ?: return
    val MarronBK = Color(0xFF2D1406)
    val CremaBK = Color(0xFFF5EBDC)
    val naranjaIllo = Color(0xFFF39200)

    Scaffold(
        containerColor = CremaBK,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.detalle_titulo), color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, stringResource(R.string.cocina_atras), tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MarronBK)
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(20.dp).verticalScroll(rememberScrollState())) {
            Text(stringResource(R.string.detalle_resumen), fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = MarronBK)
            Spacer(Modifier.height(12.dp))

            pedido.productos.forEach { detalle ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("${detalle.cantidad}x ${detalle.nombre}", modifier = Modifier.weight(1f))
                    Text("${String.format("%.2f", detalle.precioUnidad * detalle.cantidad)}€", fontWeight = FontWeight.Bold)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 1.dp, color = Color.LightGray)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(R.string.detalle_total), fontSize = 22.sp, fontWeight = FontWeight.Black)
                Text("${String.format("%.2f", pedido.total)}€", fontSize = 22.sp, fontWeight = FontWeight.Black, color = naranjaIllo)
            }

            Spacer(Modifier.height(32.dp))
            Text(stringResource(R.string.detalle_info_entrega), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MarronBK)
            Card(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(stringResource(R.string.detalle_cliente, pedido.nombreCliente ?: ""))
                    Text(stringResource(R.string.detalle_direccion, pedido.direccion ?: ""))
                    Text(stringResource(R.string.detalle_telefono, pedido.telefono ?: ""))
                    Spacer(Modifier.height(8.dp))
                    Text(stringResource(R.string.detalle_estado, pedido.estado), color = naranjaIllo, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}