package com.illouncampero.illouncampero.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.illouncampero.illouncampero.R
import com.illouncampero.illouncampero.viewmodel.AuthViewModel
import com.illouncampero.illouncampero.viewmodel.ProductoViewModel
import kotlinx.coroutines.launch

// --- COLORES ESTILO BURGER KING ---
val MarronBK = Color(0xFF2D1406)
val CremaBK = Color(0xFFF5EBDC)
val RojoBK = Color(0xFFD62300)
val naranjaIllo = Color(0xFFF39200)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipal(navController: NavController, authViewModel: AuthViewModel, prodViewModel: ProductoViewModel) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val nombre = authViewModel.nombreUsuario

    // Estado para la barra de navegación inferior
    var selectedTab by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        authViewModel.obtenerNombreUsuario()
        prodViewModel.cargarProductos()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            MenuLateral(
                nombre = nombre,
                navController = navController,
                drawerState = drawerState,
                onCerrarSesion = {
                    authViewModel.cerrarSesion {
                        navController.navigate("login") { popUpTo("home") { inclusive = true } }
                    }
                }
            )
        }
    ) {
        Scaffold(
            containerColor = CremaBK,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Image(
                            painter = painterResource(id = R.drawable.logo_circular),
                            contentDescription = "Logo Illo",
                            modifier = Modifier.height(100.dp)
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, "Menú", tint = MarronBK, modifier = Modifier.size(30.dp))
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* Navegar al carrito */ }) {
                            Icon(Icons.Default.ShoppingCart, "Carrito", tint = MarronBK, modifier = Modifier.size(30.dp))
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
                )
            },
            bottomBar = {
                // BARRA INFERIOR FIJA: Se mantiene visible siempre
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        label = { Text("Inicio") },
                        icon = { Icon(Icons.Default.Home, null) },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = naranjaIllo, selectedTextColor = naranjaIllo)
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        label = { Text("Carta") },
                        icon = { Icon(Icons.Default.List, null) },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = naranjaIllo, selectedTextColor = naranjaIllo)
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        label = { Text("Pedir") },
                        icon = { Icon(Icons.Default.ShoppingCart, null) },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = naranjaIllo, selectedTextColor = naranjaIllo)
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                BarraDireccion("Avenida Juan XXIII, Málaga")
                CardInfoNegocio()

                // Botón "Comenzar pedido" llamativo
                Button(
                    onClick = { /* Acción */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                        .height(55.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RojoBK),
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Text("COMENZAR PEDIDO", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                }

                BotonBuscar()
                Spacer(Modifier.height(30.dp))
            }
        }
    }
}

@Composable
fun MenuLateral(navController: NavController, nombre: String, drawerState: DrawerState, onCerrarSesion: () -> Unit) {
    val scope = rememberCoroutineScope()
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
        NavigationDrawerItem(
            label = { Text("Mi Perfil") },
            selected = false,
            icon = { Icon(Icons.Default.Person, null) },
            onClick = {
                scope.launch { drawerState.close() }
                navController.navigate("configuracion")
            }
        )
        NavigationDrawerItem(
            label = { Text("Mis Pedidos") },
            selected = false,
            icon = { Icon(Icons.Default.List, null) },
            onClick = { scope.launch { drawerState.close() } }
        )
        NavigationDrawerItem(
            label = { Text("Configuración") },
            selected = false,
            icon = { Icon(Icons.Default.Settings, null) },
            onClick = {
                scope.launch { drawerState.close() }
                navController.navigate("configuracion")
            }
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        NavigationDrawerItem(
            label = { Text("Cerrar Sesión", color = Color.Red) },
            selected = false,
            onClick = onCerrarSesion,
            icon = { Icon(Icons.Default.ExitToApp, null, tint = Color.Red) }
        )
    }
}


@Composable
fun BarraDireccion(direccion: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
        Icon(Icons.Default.LocationOn, null, tint = MarronBK, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(4.dp))
        Text(direccion, fontSize = 14.sp, color = MarronBK, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun CardInfoNegocio() {
    ElevatedCard(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(180.dp).padding(16.dp).clip(RoundedCornerShape(15.dp)),
                contentScale = ContentScale.Fit
            )
            Surface(color = naranjaIllo, modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(12.dp), Arrangement.Center, Alignment.CenterVertically) {
                    Icon(Icons.Default.Refresh, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Abiertos hasta las 3:00 AM", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(16.dp))
            Text("Illo, Un campero", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = MarronBK)
            Text("Los mejores camperos y pizza de Málaga", fontSize = 13.sp, color = Color.Gray)
            Spacer(Modifier.height(16.dp))
            BotonInfoNegocio(Icons.Default.CheckCircle, "Pedido mínimo : 12€")
            BotonInfoNegocio(Icons.Default.Build, "Espera de 20-25 minutos")
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
fun BotonInfoNegocio(icono: ImageVector, texto: String) {
    Surface(
        color = MarronBK,
        shape = RoundedCornerShape(25.dp),
        modifier = Modifier.fillMaxWidth(0.85f).padding(vertical = 4.dp).height(45.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Icon(icono, null, tint = Color.White, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(texto, color = Color.White, fontSize = 14.sp)
        }
    }
}

@Composable
fun BotonBuscar() {
    OutlinedButton(
        onClick = { },
        modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp, vertical = 10.dp).height(50.dp),
        shape = RoundedCornerShape(30.dp),
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Text("Buscar", color = Color.Gray, fontSize = 16.sp)
    }
}