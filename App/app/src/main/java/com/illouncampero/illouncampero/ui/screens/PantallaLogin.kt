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
fun PantallaLogin(navController: NavController, viewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    val context = LocalContext.current

    val NaranjaIllo = Color(0xFFF39200)
    val MarronCocina = Color(0xFF2D1406)
    val CremaFondo = Color(0xFFF5EBDC)

    Box(modifier = Modifier.fillMaxSize().background(CremaFondo)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(60.dp))
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = stringResource(R.string.login_logo_desc),
                modifier = Modifier.size(220.dp).clip(RoundedCornerShape(20.dp)),
                contentScale = ContentScale.Fit
            )
            Spacer(Modifier.height(24.dp))
            Text(stringResource(R.string.login_bienvenida), fontSize = 28.sp, fontWeight = FontWeight.Black, color = MarronCocina)
            Text(stringResource(R.string.login_subtitulo), fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center)
            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = email, onValueChange = { email = it },
                label = { Text(stringResource(R.string.login_email)) },
                leadingIcon = { Icon(Icons.Default.Email, null, tint = NaranjaIllo) },
                modifier = Modifier.fillMaxWidth(), singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NaranjaIllo, unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = NaranjaIllo, cursorColor = NaranjaIllo,
                    focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = contrasena, onValueChange = { contrasena = it },
                label = { Text(stringResource(R.string.login_contrasena)) },
                leadingIcon = { Icon(Icons.Default.Lock, null, tint = NaranjaIllo) },
                modifier = Modifier.fillMaxWidth(), singleLine = true,
                shape = RoundedCornerShape(16.dp),
                visualTransformation = PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NaranjaIllo, unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = NaranjaIllo, cursorColor = NaranjaIllo,
                    focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Text(
                text = stringResource(R.string.login_olvide_clave),
                modifier = Modifier.align(Alignment.End).padding(top = 8.dp, bottom = 24.dp).clickable { },
                color = MarronCocina, fontSize = 14.sp, fontWeight = FontWeight.Bold
            )

            val errorCampos = stringResource(R.string.login_error_campos)
            Button(
                onClick = {
                    if (email.isNotEmpty() && contrasena.isNotEmpty()) {
                        viewModel.login(email, contrasena) { success, error ->
                            if (success) viewModel.verificarRolYEntrar(navController)
                            else Toast.makeText(context, "Error: $error", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(context, errorCampos, Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NaranjaIllo),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(stringResource(R.string.login_boton), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Color.White)
            }

            Spacer(Modifier.height(16.dp))

            OutlinedButton(
                onClick = { navController.navigate("registro") },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(28.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, NaranjaIllo)
            ) {
                Text(stringResource(R.string.login_sin_cuenta), color = NaranjaIllo, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}