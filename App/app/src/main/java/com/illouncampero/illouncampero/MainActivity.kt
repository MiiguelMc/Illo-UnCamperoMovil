package com.illouncampero.illouncampero

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.illouncampero.illouncampero.ui.screens.PantallaAdmin
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Creamos el ViewModel aquí para que sea el mismo en toda la app
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
                    composable("home") {    PantallaPrincipal(navController, authViewModel, productosViewModel, carritoViewModel,usuarioViewModel)} // <--- PASALO AQUÍ
                    composable("admin_panel") { PantallaAdmin(navController, authViewModel, productosViewModel) }
                    composable("configuracion") {
                        com.illouncampero.illouncampero.ui.screens.PantallaConfiguracion(navController = navController, viewModel = usuarioViewModel)
                    }
                    composable("mis_pedidos") { PantallaMisPedidos(navController, pedidoViewModel) }
                    composable("detalle_pedido") { PantallaDetallePedido(navController, pedidoViewModel) }
                }
            }
        }
    }
}

