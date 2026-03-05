// ─────────────────────────────────────────────────────────────────────────────
// PantallaConfiguracion.kt
// ─────────────────────────────────────────────────────────────────────────────
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.illouncampero.illouncampero.R
import com.illouncampero.illouncampero.viewmodel.UsuarioViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaConfiguracion(navController: NavController, viewModel: UsuarioViewModel) {
    val context = LocalContext.current
    val naranjaIllo = Color(0xFFF39200)
    val moradoIllo = Color(0xFF4B2C69)

    LaunchedEffect(Unit) { viewModel.cargarPerfil() }

    val msgPerfilActualizado = stringResource(R.string.config_perfil_actualizado)
    viewModel.mensaje?.let {
        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        viewModel.mensaje = null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.config_titulo), color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, stringResource(R.string.config_atras), tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = naranjaIllo)
            )
        }
    ) { paddingValues ->
        if (viewModel.cargando && viewModel.nombre.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = naranjaIllo) }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, null, tint = moradoIllo)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.config_mis_datos), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                OutlinedTextField(value = viewModel.nombre, onValueChange = { viewModel.nombre = it }, label = { Text(stringResource(R.string.config_nombre)) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = viewModel.email, onValueChange = {}, label = { Text(stringResource(R.string.config_email)) }, modifier = Modifier.fillMaxWidth(), enabled = false, colors = OutlinedTextFieldDefaults.colors(disabledBorderColor = Color.LightGray, disabledTextColor = Color.DarkGray))
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = viewModel.telefono, onValueChange = { viewModel.telefono = it }, label = { Text(stringResource(R.string.config_telefono)) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = viewModel.direccion, onValueChange = { viewModel.direccion = it }, label = { Text(stringResource(R.string.config_direccion)) }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                Spacer(Modifier.height(30.dp))

                Button(
                    onClick = { viewModel.guardarCambios { Toast.makeText(context, msgPerfilActualizado, Toast.LENGTH_SHORT).show() } },
                    colors = ButtonDefaults.buttonColors(containerColor = naranjaIllo),
                    modifier = Modifier.fillMaxWidth().height(50.dp), enabled = !viewModel.cargando
                ) {
                    if (viewModel.cargando) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    else Text(stringResource(R.string.config_guardar), color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(40.dp))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, null, tint = naranjaIllo)
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.config_seguridad), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                OutlinedButton(onClick = { viewModel.restablecerContrasena() }, border = BorderStroke(2.dp, naranjaIllo), modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.config_cambiar_contrasena), color = Color.Black)
                }
                Spacer(Modifier.height(24.dp))
                TextButton(onClick = { navController.popBackStack() }) {
                    Text(stringResource(R.string.config_volver), color = Color.Gray, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}