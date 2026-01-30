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
fun PantallaConfiguracion(navController: NavController, viewModel: UsuarioViewModel) { // <--- Añadimos navController
    val context = LocalContext.current
    val naranjaIllo = Color(0xFFF39200)
    val moradoIllo = Color(0xFF4B2C69)

    LaunchedEffect(Unit) {
        viewModel.cargarPerfil()
    }

    viewModel.mensaje?.let {
        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        viewModel.mensaje = null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) { // <--- Botón de volver arriba
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = naranjaIllo)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Usamos el padding del Scaffold
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(10.dp))

            // --- SECCIÓN DATOS PERSONALES ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = moradoIllo)
                Spacer(Modifier.width(8.dp))
                Text("Datos Personales", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Row(Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = viewModel.nombre,
                    onValueChange = { viewModel.nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                OutlinedTextField(
                    value = viewModel.email,
                    onValueChange = { },
                    label = { Text("Email") },
                    modifier = Modifier.weight(1f),
                    enabled = false
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = viewModel.telefono,
                    onValueChange = { viewModel.telefono = it },
                    label = { Text("Teléfono") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                OutlinedTextField(
                    value = viewModel.direccion,
                    onValueChange = { viewModel.direccion = it },
                    label = { Text("Dirección") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(30.dp))

            Button(
                onClick = { viewModel.guardarCambios { } },
                colors = ButtonDefaults.buttonColors(containerColor = naranjaIllo),
                modifier = Modifier.fillMaxWidth(0.8f).height(50.dp)
            ) {
                if (viewModel.cargando) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                else Text("Guardar Cambios", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(40.dp))

            // --- SECCIÓN SEGURIDAD ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = naranjaIllo)
                Spacer(Modifier.width(8.dp))
                Text("Seguridad", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                "Protege tu cuenta actualizando tu contraseña periódicamente.",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

            OutlinedButton(
                onClick = { viewModel.restablecerContrasena() },
                border = BorderStroke(2.dp, naranjaIllo),
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text("Cambiar Contraseña", color = Color.Black)
            }

            Spacer(Modifier.height(40.dp))

            // --- BOTÓN VOLVER ADICIONAL (AL FINAL) ---
            TextButton(
                onClick = { navController.popBackStack() } // <--- Vuelve a la pantalla anterior
            ) {
                Text("VOLVER AL MENÚ", color = Color.Gray, fontWeight = FontWeight.Bold)
            }
        }
    }
}