package com.illouncampero.illouncampero.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.illouncampero.illouncampero.viewmodel.UsuarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaConfiguracion(navController: NavController, viewModel: UsuarioViewModel) {
    val context = LocalContext.current
    val naranjaIllo = Color(0xFFF39200)
    val moradoIllo = Color(0xFF4B2C69)

    // 1. Cargamos los datos actuales nada más entrar a la pantalla
    LaunchedEffect(Unit) {
        viewModel.cargarPerfil()
    }

    // 2. Escuchamos mensajes de éxito o error que vengan del ViewModel
    viewModel.mensaje?.let {
        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        viewModel.mensaje = null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = naranjaIllo)
            )
        }
    ) { paddingValues ->

        // Mostramos un cargando si la app está buscando los datos y aún no tiene el nombre
        if (viewModel.cargando && viewModel.nombre.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = naranjaIllo)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(10.dp))

                // --- SECCIÓN DATOS PERSONALES ---
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = moradoIllo)
                    Spacer(Modifier.width(8.dp))
                    Text("Mis Datos Actuales", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // CAMPO NOMBRE
                OutlinedTextField(
                    value = viewModel.nombre,
                    onValueChange = { viewModel.nombre = it },
                    label = { Text("Nombre Completo") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(Modifier.height(12.dp))

                // CAMPO EMAIL (Solo lectura, no se suele cambiar por seguridad)
                OutlinedTextField(
                    value = viewModel.email,
                    onValueChange = { },
                    label = { Text("Email (No editable)") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledBorderColor = Color.LightGray,
                        disabledTextColor = Color.DarkGray
                    )
                )

                Spacer(Modifier.height(12.dp))

                // CAMPO TELÉFONO
                OutlinedTextField(
                    value = viewModel.telefono,
                    onValueChange = { viewModel.telefono = it },
                    label = { Text("Teléfono de contacto") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(Modifier.height(12.dp))

                // CAMPO DIRECCIÓN
                OutlinedTextField(
                    value = viewModel.direccion,
                    onValueChange = { viewModel.direccion = it },
                    label = { Text("Dirección de entrega") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                Spacer(Modifier.height(30.dp))

                // BOTÓN GUARDAR (Usa 'viewModel' en minúscula)
                Button(
                    onClick = {
                        viewModel.guardarCambios {
                            Toast.makeText(context, "¡Perfil actualizado!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = naranjaIllo),
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = !viewModel.cargando
                ) {
                    if (viewModel.cargando) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("GUARDAR CAMBIOS", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(40.dp))

                // --- SECCIÓN SEGURIDAD ---
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = naranjaIllo)
                    Spacer(Modifier.width(8.dp))
                    Text("Seguridad", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // BOTÓN RESTABLECER (Usa 'viewModel' en minúscula)
                OutlinedButton(
                    onClick = { viewModel.restablecerContrasena() },
                    border = BorderStroke(2.dp, naranjaIllo),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Enviar correo para cambiar contraseña", color = Color.Black)
                }

                Spacer(Modifier.height(24.dp))

                TextButton(onClick = { navController.popBackStack() }) {
                    Text("VOLVER AL MENÚ", color = Color.Gray, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}