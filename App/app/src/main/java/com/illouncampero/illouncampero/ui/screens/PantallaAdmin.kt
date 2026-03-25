package com.illouncampero.illouncampero.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EuroSymbol
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Fastfood
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.illouncampero.illouncampero.R
import com.illouncampero.illouncampero.model.Producto
import com.illouncampero.illouncampero.viewmodel.AdminViewModel
import com.illouncampero.illouncampero.viewmodel.AuthViewModel
import com.illouncampero.illouncampero.viewmodel.ProductoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAdmin(
    navController: NavController,
    authViewModel: AuthViewModel,
    productoViewModel: ProductoViewModel,
    adminViewModel: AdminViewModel = viewModel() // Inyectamos el nuevo VM
) {
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
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
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

                    // ... (Mantenemos tus campos de creación igual)
                    OutlinedTextField(value = productoViewModel.nombreInput, onValueChange = { productoViewModel.nombreInput = it }, label = { Text(stringResource(R.string.admin_nombre_campero)) }, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = productoViewModel.precioInput, onValueChange = { productoViewModel.precioInput = it }, label = { Text(stringResource(R.string.admin_precio)) }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    Spacer(Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(value = productoViewModel.categoriaInput, onValueChange = { productoViewModel.categoriaInput = it }, label = { Text(stringResource(R.string.admin_categoria)) }, modifier = Modifier.weight(1f))
                        Spacer(Modifier.width(8.dp))
                        OutlinedTextField(value = productoViewModel.subcategoriaInput, onValueChange = { productoViewModel.subcategoriaInput = it }, label = { Text(stringResource(R.string.admin_subcategoria)) }, modifier = Modifier.weight(1f))
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

                    Button(
                        onClick = {
                            productoViewModel.guardarProducto(
                                onSuccess = { Toast.makeText(context, "Guardado", Toast.LENGTH_SHORT).show() },
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
                }

                items(productoViewModel.listaProductos) { producto ->
                    FilaProductoAdmin(
                        producto = producto,
                        onDelete = {
                            productoViewModel.eliminarProducto(producto.id, { Toast.makeText(context, "Eliminado", Toast.LENGTH_SHORT).show() }, {})
                        },
                        onEdit = {
                            adminViewModel.prepararEdicion(producto)
                        }
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }

            // --- MODAL DE EDICIÓN ---
            if (adminViewModel.mostrarModal) {
                DialogoEditarProducto(adminViewModel, productoViewModel)
            }
        }
    }
}

@Composable
fun FilaProductoAdmin(producto: Producto, onDelete: () -> Unit, onEdit: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = if (producto.disponible) Color(0xFFF5F5F5) else Color(0xFFFFEBEE))
    ) {
        Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(producto.nombre, fontWeight = FontWeight.Bold)
                Text("${producto.precio}€", color = Color.DarkGray, fontSize = 13.sp)
                if (!producto.disponible) {
                    Text("NO DISPONIBLE", color = Color.Red, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            Row {
                IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, "Editar", tint = Color.DarkGray) }
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, "Borrar", tint = Color.Red) }
            }
        }
    }
}

@Composable
fun DialogoEditarProducto(adminViewModel: AdminViewModel, productoViewModel: ProductoViewModel) {
    val context = LocalContext.current
    val naranjaIllo = Color(0xFFF39200)
    val marronCocina = Color(0xFF2D1406)

    AlertDialog(
        onDismissRequest = { adminViewModel.mostrarModal = false },
        shape = RoundedCornerShape(28.dp), // Bordes bien redondeados y modernos
        containerColor = Color.White,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Edit, contentDescription = null, tint = naranjaIllo, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "EDITAR PRODUCTO",
                    fontWeight = FontWeight.Black,
                    fontSize = 20.sp,
                    color = marronCocina
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // CAMPO NOMBRE
                OutlinedTextField(
                    value = adminViewModel.nombreEdit,
                    onValueChange = { adminViewModel.nombreEdit = it },
                    label = { Text("Nombre del Campero") },
                    leadingIcon = { Icon(Icons.Default.Fastfood, null, tint = naranjaIllo) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = naranjaIllo,
                        focusedLabelColor = naranjaIllo,
                        cursorColor = naranjaIllo
                    )
                )

                // CAMPO PRECIO
                OutlinedTextField(
                    value = adminViewModel.precioEdit,
                    onValueChange = { adminViewModel.precioEdit = it },
                    label = { Text("Precio (€)") },
                    leadingIcon = { Icon(Icons.Default.EuroSymbol, null, tint = naranjaIllo) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = naranjaIllo,
                        focusedLabelColor = naranjaIllo
                    )
                )

                // CAMPO DESCRIPCIÓN
                OutlinedTextField(
                    value = adminViewModel.descripcionEdit,
                    onValueChange = { adminViewModel.descripcionEdit = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = naranjaIllo,
                        focusedLabelColor = naranjaIllo
                    )
                )

                // SECCIÓN DISPONIBILIDAD (Estilo Tarjeta)
                Surface(
                    color = if (adminViewModel.disponibleEdit) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                if (adminViewModel.disponibleEdit) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                contentDescription = null,
                                tint = if (adminViewModel.disponibleEdit) Color(0xFF2E7D32) else Color(0xFFC62828)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = if (adminViewModel.disponibleEdit) "Producto Disponible" else "Agotado / Pausado",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (adminViewModel.disponibleEdit) Color(0xFF2E7D32) else Color(0xFFC62828)
                            )
                        }
                        Switch(
                            checked = adminViewModel.disponibleEdit,
                            onCheckedChange = { adminViewModel.disponibleEdit = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = naranjaIllo,
                                checkedTrackColor = naranjaIllo.copy(alpha = 0.5f)
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    adminViewModel.guardarCambios(
                        productoViewModel = productoViewModel,
                        onSuccess = { Toast.makeText(context, "¡Campero actualizado!", Toast.LENGTH_SHORT).show() },
                        onError = { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
                    )
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = naranjaIllo)
            ) {
                if (adminViewModel.cargandoEdicion) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("GUARDAR CAMBIOS", fontWeight = FontWeight.Black)
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = { adminViewModel.mostrarModal = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancelar", color = Color.Gray, fontWeight = FontWeight.Bold)
            }
        }
    )
}