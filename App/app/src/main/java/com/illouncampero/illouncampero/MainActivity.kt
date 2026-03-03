package com.illouncampero.illouncampero

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.illouncampero.illouncampero.ui.screens.PantallaAdmin
import com.illouncampero.illouncampero.ui.screens.PantallaCocina
import com.illouncampero.illouncampero.ui.screens.PantallaDetallePedido
import com.illouncampero.illouncampero.ui.screens.PantallaLogin
import com.illouncampero.illouncampero.ui.screens.PantallaMisPedidos
import com.illouncampero.illouncampero.ui.screens.PantallaPrincipal
import com.illouncampero.illouncampero.ui.screens.PantallaRegistro
import com.illouncampero.illouncampero.ui.screens.PantallaSplash
import com.illouncampero.illouncampero.viewmodel.AuthViewModel
import com.illouncampero.illouncampero.viewmodel.CarritoViewModel
import com.illouncampero.illouncampero.viewmodel.PedidoViewModel
import com.illouncampero.illouncampero.viewmodel.ProductoViewModel
import com.illouncampero.illouncampero.viewmodel.UsuarioViewModel

class MainActivity : ComponentActivity() {

    // Launcher para pedir el permiso de notificaciones (Android 13+)
    private val permisosNotificacion = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            println("DEBUG_ILLO: Permiso de notificaciones concedido")
        } else {
            println("DEBUG_ILLO: Permiso de notificaciones denegado")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Pedimos el permiso solo en Android 13 (API 33) o superior
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                permisosNotificacion.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        val authViewModel = AuthViewModel()
        val productosViewModel = ProductoViewModel()
        val usuarioViewModel = UsuarioViewModel()
        val carritoViewModel = CarritoViewModel()
        val pedidoViewModel = PedidoViewModel()

        setContent {
            MaterialTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "splash") {
                    composable("splash") { PantallaSplash(navController, authViewModel) }
                    composable("login") { PantallaLogin(navController, authViewModel) }
                    composable("registro") { PantallaRegistro(navController, authViewModel) }
                    composable("home") { PantallaPrincipal(navController, authViewModel, productosViewModel, carritoViewModel, usuarioViewModel) }
                    composable("admin_panel") { PantallaAdmin(navController, authViewModel, productosViewModel) }
                    composable("configuracion") {
                        com.illouncampero.illouncampero.ui.screens.PantallaConfiguracion(navController = navController, viewModel = usuarioViewModel)
                    }
                    composable("mis_pedidos") { PantallaMisPedidos(navController, pedidoViewModel) }
                    composable("detalle_pedido") { PantallaDetallePedido(navController, pedidoViewModel) }
                    composable("pantalla_cocina") { PantallaCocina(navController, pedidoViewModel) }
                }
            }
        }
    }
}