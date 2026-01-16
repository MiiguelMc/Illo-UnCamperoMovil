package com.illouncampero.illouncampero.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.illouncampero.illouncampero.model.Usuario
import com.illouncampero.illouncampero.viewmodel.AuthViewModel

@Composable
fun PantallaRegistro(navController: NavController, authViewModel: AuthViewModel) {
    // Estados para los campos (UI State)
    var nombre by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }

    // Estados para controlar el Pop-up
    var mostrarDialogo by remember { mutableStateOf(false) }
    var mensajeDialogo by remember { mutableStateOf("") }
    var registroExitoso by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(25.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Crear Cuenta en Illo", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)

        Spacer(modifier = Modifier.height(20.dp))

        // --- CAMPOS DE TEXTO ---
        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(value = apellidos, onValueChange = { apellidos = it }, label = { Text("Apellidos") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(value = contrasena, onValueChange = { contrasena = it }, label = { Text("Contraseña") }, modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation())
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(value = confirmarContrasena, onValueChange = { confirmarContrasena = it }, label = { Text("Confirmar Contraseña") }, modifier = Modifier.fillMaxWidth(), visualTransformation = PasswordVisualTransformation())

        Spacer(modifier = Modifier.height(30.dp))

        // --- BOTÓN REGISTRARSE ---
        Button(
            onClick = {
                // 1. Validaciones de la interfaz
                if (nombre.isEmpty() || email.isEmpty() || contrasena.isEmpty()) {
                    mensajeDialogo = "Por favor, rellena los campos obligatorios."
                    mostrarDialogo = true
                } else if (contrasena != confirmarContrasena) {
                    mensajeDialogo = "Las contraseñas no coinciden."
                    mostrarDialogo = true
                } else if (contrasena.length < 6) {
                    mensajeDialogo = "La contraseña debe ser de al menos 6 caracteres."
                    mostrarDialogo = true
                } else {
                    // 2. CREAMOS EL OBJETO USUARIO
                    val nuevoUsuario = Usuario(
                        nombre = nombre,
                        apellidos = apellidos,
                        telefono = telefono,
                        email = email
                    )

                    // 3. LLAMAMOS AL VIEWMODEL (Él se encarga de Firebase)
                    authViewModel.registrarUsuario(nuevoUsuario, contrasena) { exito, error ->
                        registroExitoso = exito
                        mensajeDialogo = error ?: "Has creado tu cuenta exitosamente"
                        mostrarDialogo = true
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("REGISTRARME")
        }

        TextButton(onClick = { navController.popBackStack() }) {
            Text("Ya tengo cuenta, volver al login")
        }
    }

    // --- DIALOGO DE RESULTADO ---
    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text(if (registroExitoso) "¡Éxito!" else "Atención") },
            text = { Text(mensajeDialogo) },
            confirmButton = {
                Button(onClick = {
                    mostrarDialogo = false
                    if (registroExitoso) navController.navigate("login")
                }) {
                    Text("Aceptar")
                }
            }
        )
    }
}