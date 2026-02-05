package com.illouncampero.illouncampero.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.illouncampero.illouncampero.model.Producto
import com.illouncampero.illouncampero.viewmodel.AuthViewModel
import com.illouncampero.illouncampero.viewmodel.ProductoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAdmin(
    navController: NavController,
    authViewModel: AuthViewModel,
    productoViewModel: ProductoViewModel
) {
    val context = LocalContext.current
    val azulAdmin = Color(0xFF1C1C1C)
    val naranjaIllo = Color(0xFFF39200)

    LaunchedEffect(Unit) {
        productoViewModel.cargarProductos()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel Admin - Illo", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = azulAdmin),
                actions = {
                    IconButton(onClick = {
                        authViewModel.cerrarSesion { navController.navigate("login") { popUpTo(0) } }
                    }) {
                        Icon(Icons.Default.ExitToApp, null, tint = Color.White)
                    }
                }
            )
        }

    ) { padding ->
        // USAMOS UN SOLO LAZYCOLUMN PARA TODO (Formulario + Lista)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // --- PARTE 1: EL FORMULARIO (metido en un 'item' para que no choque el scroll) ---
            item {
                Spacer(Modifier.height(16.dp))
                Text("Gestión de Producto", fontWeight = FontWeight.Bold, fontSize = 22.sp)
                Spacer(Modifier.height(16.dp))

                // 1. NOMBRE (Ancho completo)
                OutlinedTextField(
                    value = productoViewModel.nombreInput,
                    onValueChange = { productoViewModel.nombreInput = it },
                    label = { Text("Nombre del Campero") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                // 2. PRECIO (Ancho completo para que sea más fácil de dar)
                OutlinedTextField(
                    value = productoViewModel.precioInput,
                    onValueChange = { productoViewModel.precioInput = it },
                    label = { Text("Precio (€)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(Modifier.height(8.dp))

                // 3. CATEGORÍA Y SUBCATEGORÍA (En la misma fila)
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = productoViewModel.categoriaInput,
                        onValueChange = { productoViewModel.categoriaInput = it },
                        label = { Text("Categoría") },
                        modifier = Modifier.weight(1f), // Ahora sí funciona porque está en un Row
                        placeholder = { Text("Ej: Camperos") }
                    )
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(
                        value = productoViewModel.subcategoriaInput,
                        onValueChange = { productoViewModel.subcategoriaInput = it },
                        label = { Text("Subcategoría") },
                        modifier = Modifier.weight(1f), // Ahora sí funciona
                        placeholder = { Text("Ej: Pollo") }
                    )
                }

                Spacer(Modifier.height(8.dp))

                // 4. DESCRIPCIÓN (Ancho completo)
                OutlinedTextField(
                    value = productoViewModel.descripcionInput,
                    onValueChange = { productoViewModel.descripcionInput = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                // 5. URL IMAGEN (Ancho completo)
                OutlinedTextField(
                    value = productoViewModel.imagenUrlInput,
                    onValueChange = { productoViewModel.imagenUrlInput = it },
                    label = { Text("URL de la Imagen") },
                    modifier = Modifier.fillMaxWidth()
                )

                // 6. CHECKBOX DISPONIBLE
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = productoViewModel.disponibleInput,
                        onCheckedChange = { productoViewModel.disponibleInput = it }
                    )
                    Text("Disponible en carta")
                }

                Spacer(Modifier.height(16.dp))

                // 7. BOTÓN DE SUBIDA
                Button(
                    onClick = {
                        productoViewModel.guardarProducto(
                            onSuccess = {
                                Toast.makeText(context, "¡Producto guardado!", Toast.LENGTH_SHORT).show()
                            },
                            onError = { mensaje ->
                                Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = naranjaIllo),
                    enabled = !productoViewModel.cargando
                ) {
                    if (productoViewModel.cargando) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("SUBIR PRODUCTO", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(24.dp))
                HorizontalDivider(thickness = 2.dp)
                Spacer(Modifier.height(16.dp))
                Text("Productos actuales", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(Modifier.height(8.dp))

                if (productoViewModel.listaProductos.isEmpty() && !productoViewModel.cargando) {
                    Text("No hay productos en la carta", color = Color.Gray)
                }
            }


// --- PARTE 2: LA LISTA ---
            items(productoViewModel.listaProductos) { producto ->
                FilaProductoAdmin(producto) {

                    // --- AÑADE ESTA LÍNEA AQUÍ ABAJO ---
                    println("DEBUG_ILLO: El ID del producto '${producto.getNombre()}' es: '${producto.getId()}'")
                    // ----------------------------------

                    // Al hacer clic en el icono de papelera:
                    productoViewModel.eliminarProducto(
                        id = producto.getId(),
                        onSuccess = {
                            Toast.makeText(context, "Eliminado: ${producto.getNombre()}", Toast.LENGTH_SHORT).show()
                        },
                        onError = { mensajeError ->
                            Toast.makeText(context, mensajeError, Toast.LENGTH_LONG).show()
                        }
                    )
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun FilaProductoAdmin(producto: Producto, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = producto.getNombre(), fontWeight = FontWeight.Bold)
                Text(text = "${producto.getPrecio()}€", color = Color.DarkGray)
                Text(text = "Cat: ${producto.getCategoria()}", fontSize = 11.sp, color = Color.Gray)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, "Borrar", tint = Color.Red)
            }
        }
    }
}