package com.illouncampero.illouncampero.ui.screens

import Usuario
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.illouncampero.illouncampero.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRegistro(navController: NavController, authViewModel: AuthViewModel) {
    // Colores del tema "Illo"
    val NaranjaIllo = Color(0xFFF39200)
    val MarronCocina = Color(0xFF2D1406)
    val CremaFondo = Color(0xFFF5EBDC)

    // Estados de los campos
    var nombre by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }

    // Estados para el diálogo (mantenemos tu lógica pero estilizamos el contenedor)
    var mostrarDialogo by remember { mutableStateOf(false) }
    var mensajeDialogo by remember { mutableStateOf("") }
    var registroExitoso by remember { mutableStateOf(false) }

    // Estilo común para los inputs para no repetir código
    val inputColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = NaranjaIllo,
        focusedLabelColor = NaranjaIllo,
        cursorColor = NaranjaIllo,
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        focusedLeadingIconColor = NaranjaIllo
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CremaFondo)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // TÍTULO Y SUBTÍTULO
            Text(
                text = "¡Únete a la familia! 🍔",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = MarronCocina
            )
            Text(
                text = "Crea tu cuenta para empezar a pedir",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            // --- LISTA DE CAMPOS CON ICONOS ---

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = inputColors
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = apellidos,
                onValueChange = { apellidos = it },
                label = { Text("Apellidos") },
                leadingIcon = { Icon(Icons.Default.PersonOutline, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = inputColors
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = inputColors,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = inputColors,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = contrasena,
                onValueChange = { contrasena = it },
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = inputColors,
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = confirmarContrasena,
                onValueChange = { confirmarContrasena = it },
                label = { Text("Confirmar Contraseña") },
                leadingIcon = { Icon(Icons.Default.CheckCircle, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = inputColors,
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(35.dp))

            // --- BOTÓN REGISTRARSE ---
            Button(
                onClick = {
                    // Mantenemos tu lógica de llamada, pero ahora el botón luce increíble
                    if (nombre.isEmpty() || email.isEmpty() || contrasena.isEmpty()) {
                        mensajeDialogo = "Rellena los campos obligatorios, fiera."
                        mostrarDialogo = true
                    } else if (contrasena != confirmarContrasena) {
                        mensajeDialogo = "Las contraseñas no coinciden."
                        mostrarDialogo = true
                    } else {
                        val nuevoUsuario = Usuario(nombre = nombre, apellidos = apellidos, telefono = telefono, email = email)
                        authViewModel.registrarUsuario(nuevoUsuario, contrasena) { exito, error ->
                            registroExitoso = exito
                            mensajeDialogo = error ?: "¡Cuenta creada! Ya eres un Illo más."
                            mostrarDialogo = true
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NaranjaIllo),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text("VAMOS A ELLO 🚀", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
            }

            TextButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Text("Ya tengo cuenta, volver al login", color = MarronCocina, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // --- DIALOGO DE RESULTADO (Estilizado) ---
    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White,
            title = {
                Text(
                    text = if (registroExitoso) "¡Éxito!" else "Atención",
                    color = if (registroExitoso) Color(0xFF008445) else Color.Red,
                    fontWeight = FontWeight.Bold
                )
            },
            text = { Text(mensajeDialogo, color = MarronCocina) },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogo = false
                        if (registroExitoso) navController.navigate("login")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NaranjaIllo)
                ) {
                    Text("VALE")
                }
            }
        )
    }
}