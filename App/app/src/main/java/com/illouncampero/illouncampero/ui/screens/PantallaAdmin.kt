package com.illouncampero.illouncampero.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.ShoppingCart
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
import com.illouncampero.illouncampero.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaAdmin(navController: NavController, viewModel: AuthViewModel) {
    // Estados para el formulario de nuevo producto
    var nombreProducto by remember { mutableStateOf("") }
    var precioProducto by remember { mutableStateOf("") }
    var descripcionProducto by remember { mutableStateOf("") }

    val context = LocalContext.current

    // Colores del diseño Admin
    val azulAdmin = Color(0xFF1C1C1C) // Negro/Gris muy oscuro
    val naranjaIllo = Color(0xFFF39200)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel Admin - Illo", color = Color.White, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = azulAdmin),
                actions = {
                    IconButton(onClick = {
                        viewModel.cerrarSesion {
                            navController.navigate("login") {
                                popUpTo("admin_panel") { inclusive = true }
                            }
                        }
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // --- SECCIÓN: AÑADIR PRODUCTO ---
            Text(text = "Añadir nuevo producto", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = nombreProducto,
                onValueChange = { nombreProducto = it },
                label = { Text("Nombre del Campero") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = precioProducto,
                onValueChange = { precioProducto = it },
                label = { Text("Precio (€)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = descripcionProducto,
                onValueChange = { descripcionProducto = it },
                label = { Text("Descripción (Ingredientes)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (nombreProducto.isNotEmpty() && precioProducto.isNotEmpty()) {
                        // AQUÍ ES DONDE LLAMARÁS AL REPOSITORIO DE TU COLEGA (Retrofit)
                        Toast.makeText(context, "Enviando a Spring Boot...", Toast.LENGTH_SHORT).show()

                        // Limpiar campos tras subir
                        nombreProducto = ""
                        precioProducto = ""
                        descripcionProducto = ""
                    } else {
                        Toast.makeText(context, "Rellena nombre y precio", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = naranjaIllo)
            ) {
                Text("SUBIR A LA CARTA", color = Color.White, fontWeight = FontWeight.Bold)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp), thickness = 2.dp, color = Color.LightGray)

            // --- SECCIÓN: GESTIÓN DE PRODUCTOS EXISTENTES ---
            Text(text = "Productos en la carta", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))

            // Lista provisional (Esto se cargará de la BD de tu colega)
            LazyColumn {
                // Ejemplo de un ítem en la lista
                item {
                    FilaProductoAdmin("Campero Pollo", "5.50€")
                    FilaProductoAdmin("Campero Mixto", "4.50€")
                    FilaProductoAdmin("Papas Locas", "6.00€")
                }
            }
        }
    }
}

@Composable
fun FilaProductoAdmin(nombre: String, precio: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = precio, color = Color.Gray)
            }
            Row {
                IconButton(onClick = { /* Lógica para editar */ }) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "Stock", tint = Color.DarkGray)
                }
                IconButton(onClick = { /* Lógica para borrar en la BD */ }) {
                    Icon(Icons.Default.Delete, contentDescription = "Borrar", tint = Color.Red)
                }
            }
        }
    }
}