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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.illouncampero.illouncampero.R
import com.illouncampero.illouncampero.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRegistro(navController: NavController, authViewModel: AuthViewModel) {
    val NaranjaIllo = Color(0xFFF39200)
    val MarronCocina = Color(0xFF2D1406)
    val CremaFondo = Color(0xFFF5EBDC)

    var nombre by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmarContrasena by remember { mutableStateOf("") }
    var mostrarDialogo by remember { mutableStateOf(false) }
    var mensajeDialogo by remember { mutableStateOf("") }
    var registroExitoso by remember { mutableStateOf(false) }

    val inputColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = NaranjaIllo, focusedLabelColor = NaranjaIllo,
        cursorColor = NaranjaIllo, focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White, focusedLeadingIconColor = NaranjaIllo
    )

    // Strings que se usan dentro de lambdas (deben leerse en contexto Composable)
    val errorCampos = stringResource(R.string.registro_error_campos)
    val errorContrasenas = stringResource(R.string.registro_error_contrasenas)
    val mensajeExito = stringResource(R.string.registro_exito)

    Box(modifier = Modifier.fillMaxSize().background(CremaFondo)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 30.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(40.dp))
            Text(stringResource(R.string.registro_titulo), fontSize = 28.sp, fontWeight = FontWeight.Black, color = MarronCocina)
            Text(stringResource(R.string.registro_subtitulo), fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp))
            Spacer(Modifier.height(30.dp))

            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text(stringResource(R.string.registro_nombre)) }, leadingIcon = { Icon(Icons.Default.Person, null) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = inputColors)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = apellidos, onValueChange = { apellidos = it }, label = { Text(stringResource(R.string.registro_apellidos)) }, leadingIcon = { Icon(Icons.Default.PersonOutline, null) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = inputColors)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = telefono, onValueChange = { telefono = it }, label = { Text(stringResource(R.string.registro_telefono)) }, leadingIcon = { Icon(Icons.Default.Phone, null) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = inputColors, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text(stringResource(R.string.registro_email)) }, leadingIcon = { Icon(Icons.Default.Email, null) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = inputColors, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = contrasena, onValueChange = { contrasena = it }, label = { Text(stringResource(R.string.registro_contrasena)) }, leadingIcon = { Icon(Icons.Default.Lock, null) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = inputColors, visualTransformation = PasswordVisualTransformation())
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = confirmarContrasena, onValueChange = { confirmarContrasena = it }, label = { Text(stringResource(R.string.registro_confirmar)) }, leadingIcon = { Icon(Icons.Default.CheckCircle, null) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = inputColors, visualTransformation = PasswordVisualTransformation())
            Spacer(Modifier.height(35.dp))

            Button(
                onClick = {
                    when {
                        nombre.isEmpty() || email.isEmpty() || contrasena.isEmpty() -> {
                            mensajeDialogo = errorCampos; mostrarDialogo = true
                        }
                        contrasena != confirmarContrasena -> {
                            mensajeDialogo = errorContrasenas; mostrarDialogo = true
                        }
                        else -> {
                            val nuevoUsuario = Usuario(nombre = nombre, apellidos = apellidos, telefono = telefono, email = email)
                            authViewModel.registrarUsuario(nuevoUsuario, contrasena) { exito, error ->
                                registroExitoso = exito
                                mensajeDialogo = error ?: mensajeExito
                                mostrarDialogo = true
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NaranjaIllo),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(stringResource(R.string.registro_boton), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
            }

            TextButton(onClick = { navController.popBackStack() }, modifier = Modifier.padding(vertical = 16.dp)) {
                Text(stringResource(R.string.registro_ya_tengo_cuenta), color = MarronCocina, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(30.dp))
        }
    }

    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White,
            title = {
                Text(
                    text = if (registroExitoso) stringResource(R.string.registro_dialogo_exito) else stringResource(R.string.registro_dialogo_atencion),
                    color = if (registroExitoso) Color(0xFF008445) else Color.Red,
                    fontWeight = FontWeight.Bold
                )
            },
            text = { Text(mensajeDialogo, color = MarronCocina) },
            confirmButton = {
                Button(
                    onClick = { mostrarDialogo = false; if (registroExitoso) navController.navigate("login") },
                    colors = ButtonDefaults.buttonColors(containerColor = NaranjaIllo)
                ) { Text(stringResource(R.string.registro_dialogo_ok)) }
            }
        )
    }
}