package com.illouncampero.illouncampero.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.illouncampero.illouncampero.R
import com.illouncampero.illouncampero.model.Producto
import com.illouncampero.illouncampero.viewmodel.AuthViewModel
import com.illouncampero.illouncampero.viewmodel.ProductoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAdmin(navController: NavController, authViewModel: AuthViewModel, productoViewModel: ProductoViewModel) {
    val context = LocalContext.current
    val azulAdmin = Color(0xFF1C1C1C)
    val naranjaIllo = Color(0xFFF39200)

    LaunchedEffect(Unit) { productoViewModel.cargarProductos() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.admin_titulo), color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = azulAdmin),
                actions = {
                    IconButton(onClick = { authViewModel.cerrarSesion { navController.navigate("login") { popUpTo(0) } } }) {
                        Icon(Icons.Default.ExitToApp, null, tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            item {
                Button(
                    onClick = { navController.navigate("pantalla_cocina") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp).height(65.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = naranjaIllo),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.List, null, tint = Color.White, modifier = Modifier.size(28.dp))
                    Spacer(Modifier.width(12.dp))
                    Text(stringResource(R.string.admin_gestion_cocina), fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color.White)
                }
                HorizontalDivider(thickness = 2.dp)
            }

            item {
                Spacer(Modifier.height(16.dp))
                Text(stringResource(R.string.admin_gestion_producto), fontWeight = FontWeight.Bold, fontSize = 22.sp)
                Spacer(Modifier.height(16.dp))

                OutlinedTextField(value = productoViewModel.nombreInput, onValueChange = { productoViewModel.nombreInput = it }, label = { Text(stringResource(R.string.admin_nombre_campero)) }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = productoViewModel.precioInput, onValueChange = { productoViewModel.precioInput = it }, label = { Text(stringResource(R.string.admin_precio)) }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                Spacer(Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = productoViewModel.categoriaInput, onValueChange = { productoViewModel.categoriaInput = it }, label = { Text(stringResource(R.string.admin_categoria)) }, modifier = Modifier.weight(1f), placeholder = { Text(stringResource(R.string.admin_categoria_placeholder)) })
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(value = productoViewModel.subcategoriaInput, onValueChange = { productoViewModel.subcategoriaInput = it }, label = { Text(stringResource(R.string.admin_subcategoria)) }, modifier = Modifier.weight(1f), placeholder = { Text(stringResource(R.string.admin_subcategoria_placeholder)) })
                }
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = productoViewModel.descripcionInput, onValueChange = { productoViewModel.descripcionInput = it }, label = { Text(stringResource(R.string.admin_descripcion)) }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = productoViewModel.imagenUrlInput, onValueChange = { productoViewModel.imagenUrlInput = it }, label = { Text(stringResource(R.string.admin_imagen_url)) }, modifier = Modifier.fillMaxWidth())

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = productoViewModel.disponibleInput, onCheckedChange = { productoViewModel.disponibleInput = it })
                    Text(stringResource(R.string.admin_disponible))
                }
                Spacer(Modifier.height(16.dp))

                val msgGuardado = stringResource(R.string.admin_producto_guardado)
                Button(
                    onClick = {
                        productoViewModel.guardarProducto(
                            onSuccess = { Toast.makeText(context, msgGuardado, Toast.LENGTH_SHORT).show() },
                            onError = { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = naranjaIllo),
                    enabled = !productoViewModel.cargando
                ) {
                    if (productoViewModel.cargando) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    else Text(stringResource(R.string.admin_subir_producto), fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(24.dp))
                HorizontalDivider(thickness = 2.dp)
                Spacer(Modifier.height(16.dp))
                Text(stringResource(R.string.admin_productos_actuales), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(Modifier.height(8.dp))
                if (productoViewModel.listaProductos.isEmpty() && !productoViewModel.cargando) {
                    Text(stringResource(R.string.admin_sin_productos), color = Color.Gray)
                }
            }

            items(productoViewModel.listaProductos) { producto ->
                val msgEliminado = stringResource(R.string.admin_eliminado, producto.nombre)
                FilaProductoAdmin(producto) {
                    productoViewModel.eliminarProducto(
                        id = producto.id,
                        onSuccess = { Toast.makeText(context, msgEliminado, Toast.LENGTH_SHORT).show() },
                        onError = { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
                    )
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun FilaProductoAdmin(producto: Producto, onDelete: () -> Unit) {
    val catLabel = stringResource(R.string.admin_cat_label, producto.categoria ?: "")
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f)) {
                Text(producto.nombre, fontWeight = FontWeight.Bold)
                Text("${producto.precio}€", color = Color.DarkGray)
                Text(catLabel, fontSize = 11.sp, color = Color.Gray)
            }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, "Borrar", tint = Color.Red) }
        }
    }
}