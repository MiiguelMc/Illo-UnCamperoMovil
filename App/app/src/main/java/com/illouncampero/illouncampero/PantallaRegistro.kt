package com.illouncampero.illouncampero

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun PantallaRegistro(navController: NavController) {
    // Estados para los campos
    var nombre by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }

    // Estados para el Pop-up (Dialog)
    var mostrarDialogo by remember { mutableStateOf(false) }
    var mensajeDialogo by remember { mutableStateOf("") }
    var registroExitoso by remember { mutableStateOf(false) }

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    // Usamos verticalScroll por si el teclado tapa los campos en móviles pequeños
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(25.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Crear Cuenta en Illo",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Campos de texto
        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(10.dp))

        OutlinedTextField(value = apellidos, onValueChange = { apellidos = it }, label = { Text("Apellidos") }, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = telefono,
            onValueChange = { telefono = it },
            label = { Text("Teléfono") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )
        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = confirmarContrasena,
            onValueChange = { confirmarContrasena = it },
            label = { Text("Confirmar Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Botón Registrarse
        Button(
            onClick = {
                // 1. Validaciones básicas
                if (nombre.isEmpty() || email.isEmpty() || contrasena.isEmpty()) {
                    mensajeDialogo = "Por favor, rellena los campos obligatorios."
                    mostrarDialogo = true
                } else if (contrasena != confirmarContrasena) {
                    mensajeDialogo = "Las contraseñas no coinciden."
                    mostrarDialogo = true
                } else if (contrasena.length < 6) {
                    mensajeDialogo = "La contraseña debe tener al menos 6 caracteres."
                    mostrarDialogo = true
                } else {
                    // 2. Crear usuario en Firebase Auth
                    auth.createUserWithEmailAndPassword(email, contrasena)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val uid = auth.currentUser?.uid
                                // 3. Guardar datos extra en Firestore
                                val datosUsuario = hashMapOf(
                                    "nombre" to nombre,
                                    "apellidos" to apellidos,
                                    "telefono" to telefono,
                                    "email" to email,
                                    "rol" to "cliente"
                                )

                                if (uid != null) {
                                    db.collection("usuarios").document(uid)
                                        .set(datosUsuario)
                                        .addOnSuccessListener {
                                            registroExitoso = true
                                            mensajeDialogo = "¡Cuenta creada con éxito! Ya puedes pedir tus camperos."
                                            mostrarDialogo = true
                                        }
                                }
                            } else {
                                // Error (ej: email ya registrado)
                                registroExitoso = false
                                mensajeDialogo = "Error: ${task.exception?.message}"
                                mostrarDialogo = true
                            }
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

    // --- EL POP-UP (AlertDialog) ---
    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text(if (registroExitoso) "¡Éxito!" else "Atención") },
            text = { Text(mensajeDialogo) },
            confirmButton = {
                Button(onClick = {
                    mostrarDialogo = false
                    if (registroExitoso) {
                        navController.navigate("login") // Si todo fue bien, al login
                    }
                }) {
                    Text("Aceptar")
                }
            }
        )
    }
}