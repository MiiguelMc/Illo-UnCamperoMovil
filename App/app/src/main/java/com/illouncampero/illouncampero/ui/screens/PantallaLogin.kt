package com.illouncampero.illouncampero.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.illouncampero.illouncampero.R
import com.illouncampero.illouncampero.viewmodel.AuthViewModel

@Composable
fun PantallaLogin(navController: NavController, viewModel: AuthViewModel) { // <--- Pasamos el ViewModel
    var email by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    val context = LocalContext.current

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
                .size(300.dp)
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
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(Modifier.height(30.dp))

        // BOTÓN: Iniciar Sesión
        Button(
            onClick = {
                if (email.isNotEmpty() && contrasena.isNotEmpty()) {
                    viewModel.login(email, contrasena) { success, error ->
                        if (success) {
                            // SI EL LOGIN ES CORRECTO, COMPROBAMOS EL ROL
                            viewModel.verificarRolYEntrar(navController)
                        } else {
                            Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("INICIAR SESIÓN")
        }

        Spacer(Modifier.height(15.dp))

        // BOTÓN: Registrarse
        OutlinedButton(
            onClick = { navController.navigate("registro") },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("CREAR CUENTA NUEVA")
        }

        Spacer(Modifier.height(30.dp))

        // Recuperar contraseña
        Column {
            Text(
                text = "¿Has olvidado tu contraseña? ",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = "Recuperar contraseña",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.CenterHorizontally).clickable {
                }
            )
        }
    }
}