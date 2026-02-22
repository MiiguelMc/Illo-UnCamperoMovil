package com.illouncampero.illouncampero.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
fun PantallaLogin(navController: NavController, viewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    val context = LocalContext.current

    // Paleta de colores "Illo"
    val NaranjaIllo = Color(0xFFF39200)
    val MarronCocina = Color(0xFF2D1406)
    val CremaFondo = Color(0xFFF5EBDC)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CremaFondo)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. LOGO CON ESPACIADO
            Spacer(Modifier.height(60.dp))
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo Illo Un Campero",
                modifier = Modifier
                    .size(220.dp)
                    .clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Fit
            )

            // 2. TEXTO BIENVENIDA
            Spacer(Modifier.height(24.dp))
            Text(
                text = "¡Qué pasa, mostro!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = MarronCocina
            )
            Text(
                text = "Loguéate para pedir tu campero favorito",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))

            // 3. CAMPO EMAIL (Personalizado)
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = NaranjaIllo) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NaranjaIllo,      // Color del borde al pinchar
                    unfocusedBorderColor = Color.Gray,     // Color del borde sin pinchar
                    focusedLabelColor = NaranjaIllo,       // Color del texto de la etiqueta al pinchar
                    cursorColor = NaranjaIllo,             // Color del palito de escribir
                    focusedContainerColor = Color.White,   // Fondo blanco al pinchar
                    unfocusedContainerColor = Color.White  // Fondo blanco sin pinchar
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(Modifier.height(16.dp))

            // 4. CAMPO CONTRASEÑA (Personalizado)
            OutlinedTextField(
                value = contrasena,
                onValueChange = { contrasena = it },
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = NaranjaIllo) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                visualTransformation = PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NaranjaIllo,      // Color del borde al pinchar
                    unfocusedBorderColor = Color.Gray,     // Color del borde sin pinchar
                    focusedLabelColor = NaranjaIllo,       // Color del texto de la etiqueta al pinchar
                    cursorColor = NaranjaIllo,             // Color del palito de escribir
                    focusedContainerColor = Color.White,   // Fondo blanco al pinchar
                    unfocusedContainerColor = Color.White  // Fondo blanco sin pinchar
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            // 5. ENLACE RECUPERAR (Alineado derecha)
            Text(
                text = "¿Olvidaste la clave?",
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 8.dp, bottom = 24.dp)
                    .clickable { /* Acción */ },
                color = MarronCocina,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            // 6. BOTÓN ENTRAR (Potente)
            Button(
                onClick = {
                    if (email.isNotEmpty() && contrasena.isNotEmpty()) {
                        viewModel.login(email, contrasena) { success, error ->
                            if (success) {
                                viewModel.verificarRolYEntrar(navController)
                            } else {
                                Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Rellena todos los campos, fiera", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NaranjaIllo),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    "¡INICIA SESION!",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(16.dp))

            // 7. BOTÓN REGISTRO (Elegante)
            OutlinedButton(
                onClick = { navController.navigate("registro") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, NaranjaIllo)
            ) {
                Text(
                    "NO TENGO CUENTA TODAVÍA",
                    color = NaranjaIllo,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}