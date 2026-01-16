package com.illouncampero.illouncampero.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.illouncampero.illouncampero.R
import com.illouncampero.illouncampero.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

// Colores globales para que todas las funciones los vean
val naranjaIllo = Color(0xFFF39200)
val verdeIllo = Color(0xFF008445)
val azulOscuro = Color(0xFF0A0E21)

@Composable
fun PantallaPrincipal(navController: NavController, viewModel: AuthViewModel) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val nombre = viewModel.nombreUsuario

    LaunchedEffect(Unit) {
        viewModel.obtenerNombreUsuario()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            MenuLateral(
                nombre = nombre,
                onCerrarSesion = {
                    viewModel.cerrarSesion {
                        navController.navigate("login") { popUpTo("home") { inclusive = true } }
                    }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                CabeceraPrincipal(onMenuClick = { scope.launch { drawerState.open() } })
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                BarraUsuarioYCarrito(nombre = nombre)
                BarraDireccion("Avenida Juan XXIII")
                CardInfoNegocio()
                BotonBuscar()
            }
        }
    }
}

// --- SUB-COMPOSABLES (CADA PIEZA POR SEPARADO) ---

@Composable
fun CabeceraPrincipal(onMenuClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .background(Color.White)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo Illo",
            modifier = Modifier.height(40.dp)
        )
        IconButton(onClick = onMenuClick) {
            Icon(Icons.Default.Menu, "Abrir Menú", modifier = Modifier.size(35.dp), tint = azulOscuro)
        }
    }
}

@Composable
fun MenuLateral(nombre: String, onCerrarSesion: () -> Unit) {
    ModalDrawerSheet(
        drawerContainerColor = Color.White,
        modifier = Modifier.width(300.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(naranjaIllo)
                .padding(24.dp)
        ) {
            Column {
                Box(modifier = Modifier.size(60.dp).clip(CircleShape).background(Color.White))
                Spacer(Modifier.height(12.dp))
                Text("¡Hola, $nombre!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        }
        Spacer(Modifier.height(12.dp))
        NavigationDrawerItem(label = { Text("Mi Perfil") }, selected = false, onClick = {}, icon = { Icon(Icons.Default.Person, null) })
        NavigationDrawerItem(label = { Text("Mis Pedidos") }, selected = false, onClick = {}, icon = { Icon(Icons.Default.List, null) })
        NavigationDrawerItem(label = { Text("Configuración") }, selected = false, onClick = {}, icon = { Icon(Icons.Default.Settings, null) })
        Divider(modifier = Modifier.padding(vertical = 8.dp))
        NavigationDrawerItem(
            label = { Text("Cerrar Sesión", color = Color.Red) },
            selected = false,
            onClick = onCerrarSesion,
            icon = { Icon(Icons.Default.ExitToApp, null, tint = Color.Red) }
        )
    }
}

@Composable
fun BarraUsuarioYCarrito(nombre: String) {
    Row(modifier = Modifier.fillMaxWidth().height(65.dp)) {
        Row(
            modifier = Modifier.weight(1f).fillMaxHeight().background(naranjaIllo).padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.LightGray))
            Spacer(Modifier.width(8.dp))
            Text(text = nombre, color = Color.White, fontWeight = FontWeight.Bold)
        }
        Row(
            modifier = Modifier.weight(1f).fillMaxHeight().background(verdeIllo).padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Icon(painterResource(R.drawable.logo), null, tint = Color.White, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(8.dp))
            Text("79.89€", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun BarraDireccion(direccion: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.LocationOn, null, tint = azulOscuro, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(4.dp))
        Text(direccion, fontSize = 14.sp, color = azulOscuro)
    }
}

@Composable
fun CardInfoNegocio() {
    ElevatedCard(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFF2F2F2))
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(200.dp).padding(16.dp).clip(RoundedCornerShape(15.dp)),
                contentScale = ContentScale.Fit
            )
            Surface(color = naranjaIllo, modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(12.dp), Arrangement.Center, Alignment.CenterVertically) {
                    Icon(Icons.Default.Refresh, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Abiertos hasta las 3:00 AM", color = Color.White)
                }
            }
            Spacer(Modifier.height(16.dp))
            Text("Los mejores camperos y pizza de Málaga", fontSize = 13.sp, color = Color.Gray)
            Text("Illo, Un campero", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = azulOscuro)
            Spacer(Modifier.height(16.dp))
            BotonInfoNegocio(Icons.Default.CheckCircle, "Pedido mínimo : 12€")
            BotonInfoNegocio(Icons.Default.Build, "Espera de 20-25 minutos")
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
fun BotonInfoNegocio(icono: androidx.compose.ui.graphics.vector.ImageVector, texto: String) {
    Button(
        onClick = {},
        colors = ButtonDefaults.buttonColors(containerColor = azulOscuro),
        modifier = Modifier.fillMaxWidth(0.85f).height(50.dp).padding(vertical = 4.dp),
        shape = RoundedCornerShape(25.dp)
    ) {
        Icon(icono, null, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text(texto)
    }
}

@Composable
fun BotonBuscar() {
    OutlinedButton(
        onClick = { },
        modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp, vertical = 20.dp).height(55.dp),
        shape = RoundedCornerShape(30.dp),
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Text("Buscar", color = Color.Gray, fontSize = 18.sp)
    }
}