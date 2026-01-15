package com.illouncampero.illouncampero

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                // 1. Creamos el controlador (el mando)
                val navController = rememberNavController()

                // 2. Definimos las rutas sin tocar tu diseño
                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        PantallaLogin(navController) // Tu pantalla de siempre
                    }
                    composable("registro") {
                        PantallaRegistro(navController) // El archivo nuevo
                    }
                    composable("home") { PantallaPrincipal(navController) }
                }
            }
        }
    }
}

@Composable
fun PantallaLogin(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance() // Instanciamos Firebase Auth

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 30.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo de Illo",
            modifier = Modifier
                .size(300.dp) // Reducido un poco para que quepan los botones
                .padding(top = 20.dp)
        )

        Spacer(Modifier.height(10.dp))

        // Campo Email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(Modifier.height(15.dp))

        // Campo Contraseña
        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(), // Para que no se vea la clave
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(Modifier.height(30.dp))

        // BOTÓN: Iniciar Sesión
        Button(
            onClick = {
                if (email.isNotEmpty() && contrasena.isNotEmpty()) {
                    auth.signInWithEmailAndPassword(email, contrasena)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // --- TODO LO QUE PASA SI EL LOGIN ES CORRECTO ---
                                Toast.makeText(context, "¡Bienvenidillo al Mundo Camperillo!", Toast.LENGTH_SHORT).show()

                                // Navegamos a la pantalla home
                                navController.navigate("home") {
                                    // Esto hace que si el usuario da al botón "atrás" del móvil,
                                    // no vuelva a la pantalla de login, sino que se salga de la app
                                    popUpTo("login") { inclusive = true }
                                }
                            } else {
                                // --- SI HAY UN ERROR (Contraseña mal, sin internet, etc.) ---
                                Toast.makeText(
                                    context,
                                    "Error: ${task.exception?.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(context, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("INICIAR SESIÓN")
        }

        Spacer(Modifier.height(15.dp))

        // BOTÓN: Registrarse (Outlined para que sea diferente)
        OutlinedButton(
            onClick = {
                // ESTA ES LA LÍNEA QUE TE LLEVA AL OTRO ARCHIVO
                navController.navigate("registro")
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("CREAR CUENTA NUEVA")
        }

        Spacer(Modifier.height(30.dp))

        // Texto enlace registro (Lo que tenías antes)
        Column {
            Text(
                text = "¿Has olvidado tu contraseña? ",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = "Recuperar contraseña",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    /* Navegación al registro */

            )
        }
    }
}