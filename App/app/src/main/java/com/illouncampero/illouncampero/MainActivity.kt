package com.illouncampero.illouncampero

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.illouncampero.illouncampero.ui.screens.PantallaAdmin
import com.illouncampero.illouncampero.ui.screens.PantallaLogin
import com.illouncampero.illouncampero.ui.screens.PantallaPrincipal
import com.illouncampero.illouncampero.ui.screens.PantallaRegistro
import com.illouncampero.illouncampero.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Creamos el ViewModel aquí para que sea el mismo en toda la app
        val authViewModel = AuthViewModel()

        setContent {
            MaterialTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "login") {
                    composable("login") { PantallaLogin(navController, authViewModel) }
                    composable("registro") { PantallaRegistro(navController, authViewModel) }
                    composable("home") { PantallaPrincipal(navController, authViewModel) }
                    // --- NUEVA RUTA PARA EL JEFE ---
                    composable("admin_panel") { PantallaAdmin(navController, authViewModel) }
                }
            }
        }
    }
}

