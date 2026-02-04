package com.illouncampero.illouncampero.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.illouncampero.illouncampero.R
import com.illouncampero.illouncampero.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun PantallaSplash(navController: NavController, authViewModel: AuthViewModel) {
    // LaunchedEffect se ejecuta una sola vez al entrar en la pantalla
    LaunchedEffect(Unit) {
        delay(1500) // Un segundo y medio de cortesía para que luzca el logo
        authViewModel.revisarSesionActual(navController)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White), // O el naranjaIllo si prefieres
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo Illo",
            modifier = Modifier.size(250.dp)
        )
    }
}