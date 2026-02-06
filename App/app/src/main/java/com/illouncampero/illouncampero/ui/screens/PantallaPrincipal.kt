package com.illouncampero.illouncampero.ui.screens

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.illouncampero.illouncampero.R
import com.illouncampero.illouncampero.model.Producto
import com.illouncampero.illouncampero.viewmodel.*
import kotlinx.coroutines.launch

// --- COLORES ---
val MarronBK = Color(0xFF2D1406)
val CremaBK = Color(0xFFF5EBDC)
val RojoBK = Color(0xFFD62300)
val naranjaIllo = Color(0xFFF39200)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipal(
    navController: NavController,
    authViewModel: AuthViewModel,
    prodViewModel: ProductoViewModel,
    carritoViewModel: CarritoViewModel,
    usuarioViewModel: UsuarioViewModel
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val nombre = authViewModel.nombreUsuario
    var selectedTab by remember { mutableStateOf(0) }
    val cantidadEnCarrito = carritoViewModel.contadorTotal()

    LaunchedEffect(Unit) {
        authViewModel.obtenerNombreUsuario()
        prodViewModel.cargarProductos()
        usuarioViewModel.cargarPerfil()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            MenuLateral(nombre, navController, drawerState) {
                authViewModel.cerrarSesion {
                    navController.navigate("login") { popUpTo("home") { inclusive = true } }
                }
            }
        }
    ) {
        Scaffold(
            containerColor = CremaBK,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Image(painter = painterResource(id = R.drawable.logo_circular), contentDescription = null, modifier = Modifier.height(100.dp))
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, null, tint = MarronBK, modifier = Modifier.size(30.dp))
                        }
                    },
                    actions = {
                        BadgedBox(
                            modifier = Modifier.padding(end = 8.dp),
                            badge = {
                                if (cantidadEnCarrito > 0) {
                                    Badge(containerColor = RojoBK) { Text("$cantidadEnCarrito", color = Color.White) }
                                }
                            }
                        ) {
                            IconButton(onClick = { selectedTab = 2 }) {
                                Icon(Icons.Default.ShoppingCart, null, tint = MarronBK, modifier = Modifier.size(30.dp))
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
                )
            },
            bottomBar = {
                NavigationBar(containerColor = Color.White) {
                    val itemsNav = listOf("Inicio" to Icons.Default.Home, "Carta" to Icons.Default.List, "Pedir" to Icons.Default.ShoppingCart)
                    itemsNav.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            label = { Text(item.first) },
                            icon = { Icon(item.second, null) },
                            colors = NavigationBarItemDefaults.colors(selectedIconColor = naranjaIllo, selectedTextColor = naranjaIllo)
                        )
                    }
                }
            }
        ) { paddingValues ->
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                when (selectedTab) {
                    0 -> SeccionInicio { selectedTab = 1 }
                    1 -> SeccionCarta(prodViewModel, carritoViewModel)
                    2 -> SeccionCesta(carritoViewModel, usuarioViewModel)
                }
            }
        }
    }
}

@Composable
fun SeccionInicio(onVerCarta: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        BarraDireccion("Avenida Juan XXIII, Málaga")
        CardInfoNegocio()
        Button(
            onClick = onVerCarta,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp).height(55.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RojoBK),
            shape = RoundedCornerShape(30.dp)
        ) {
            Text("COMENZAR PEDIDO", fontWeight = FontWeight.Bold, color = Color.White)
        }
        BotonBuscar()
        Spacer(Modifier.height(30.dp))
    }
}



@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SeccionCarta(prodViewModel: ProductoViewModel, carritoViewModel: CarritoViewModel) {
    val categoriasPrincipales = listOf("campero", "entrantes", "postres", "bebidas")
    val pagerState = rememberPagerState(pageCount = { categoriasPrincipales.size })
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = Color.White,
            contentColor = naranjaIllo,
            edgePadding = 16.dp,
            divider = {},
            indicator = { tabPositions ->
                if (pagerState.currentPage < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        color = naranjaIllo
                    )
                }
            }
        ) {
            categoriasPrincipales.forEachIndexed { index, cat ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    text = { Text(cat.uppercase(), fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
            }
        }

        if (prodViewModel.cargando) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = naranjaIllo)
            }
        } else {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { pageIndex ->
                val catActual = categoriasPrincipales[pageIndex]

                // --- AQUÍ ESTABA EL ERROR: AÑADIMOS ( ?: "" ) ANTES DEL TRIM ---
                val productosFiltradosYAgrupados = remember(prodViewModel.listaProductos, catActual) {
                    prodViewModel.listaProductos
                        .filter {
                            val categoriaEnDB = it.getCategoria() ?: "" // Si es null, usa texto vacío
                            categoriaEnDB.trim().lowercase() == catActual.lowercase()
                        }
                        .groupBy {
                            val subcatEnDB = it.getSubcategoria() ?: "Varios" // Si es null, usa "Varios"
                            subcatEnDB.trim()
                        }
                }

                if (productosFiltradosYAgrupados.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay productos en ${catActual.uppercase()}", color = Color.Gray)
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 100.dp)) {
                        productosFiltradosYAgrupados.forEach { (subCat, productos) ->
                            stickyHeader {
                                Box(modifier = Modifier.fillMaxWidth().background(CremaBK).padding(16.dp, 8.dp)) {
                                    Text(
                                        text = if (subCat.isBlank()) "VARIOS" else subCat.uppercase(),
                                        fontWeight = FontWeight.Black,
                                        color = MarronBK,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                            items(productos) { producto ->
                                FilaProductoCliente(producto) {
                                    carritoViewModel.añadirProducto(producto)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun FilaProductoCliente(producto: Producto, onAdd: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp, 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = producto.getImagenUrl() ?: "",
                contentDescription = null,
                modifier = Modifier.size(100.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.logo),
                error = painterResource(id = R.drawable.logo)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(producto.getNombre() ?: "", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MarronBK)
                Text(producto.getDescripcion() ?: "", fontSize = 12.sp, color = Color.Gray, maxLines = 2)
                Text("${producto.getPrecio()}€", fontWeight = FontWeight.ExtraBold, color = naranjaIllo, fontSize = 18.sp)
            }
            Surface(onClick = onAdd, shape = CircleShape, color = RojoBK, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.padding(8.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeccionCesta(carritoViewModel: CarritoViewModel, usuarioViewModel: UsuarioViewModel) {
    val context = LocalContext.current
    var mostrarPagoFake by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Text("Tu Pedido", modifier = Modifier.padding(16.dp), fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = MarronBK)

        if (carritoViewModel.items.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Cesta vacía", color = Color.Gray) }
        } else {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(carritoViewModel.items) { item ->
                    ListItem(
                        headlineContent = { Text(item.producto.getNombre(), fontWeight = FontWeight.Bold) },
                        supportingContent = { Text("${item.producto.getPrecio()}€ x ${item.cantidad}") },
                        trailingContent = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { carritoViewModel.quitarProducto(item.producto) }) { Icon(Icons.Default.RemoveCircleOutline, null, tint = RojoBK) }
                                Text("${item.cantidad}", fontWeight = FontWeight.Bold)
                                IconButton(onClick = { carritoViewModel.añadirProducto(item.producto) }) { Icon(Icons.Default.AddCircle, null, tint = naranjaIllo) }
                            }
                        }
                    )
                }
                item {
                    OutlinedTextField(
                        value = carritoViewModel.notasInput,
                        onValueChange = { carritoViewModel.notasInput = it },
                        label = { Text("Notas para el restaurante") },
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                item {
                    Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                        FilterChip(selected = carritoViewModel.metodoPagoSeleccionado == "Efectivo", onClick = { carritoViewModel.metodoPagoSeleccionado = "Efectivo" }, label = { Text("Efectivo") })
                        FilterChip(selected = carritoViewModel.metodoPagoSeleccionado == "Tarjeta", onClick = { carritoViewModel.metodoPagoSeleccionado = "Tarjeta" }, label = { Text("Tarjeta") })
                    }
                }
            }

            Surface(tonalElevation = 8.dp, color = Color.White, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)) {
                Column(Modifier.padding(20.dp)) {
                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
                        Text("Total:", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("${String.format("%.2f", carritoViewModel.calcularTotal())}€", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = RojoBK)
                    }
                    Button(
                        onClick = {
                            // 1. VALIDACIÓN: Si faltan datos básicos, mandamos al usuario a rellenarlos
                            if (usuarioViewModel.nombre.isBlank() || usuarioViewModel.direccion.isBlank() || usuarioViewModel.telefono.isBlank()) {
                                Toast.makeText(context, "Por favor, rellena tus datos en 'Mi Perfil' para poder pedir", Toast.LENGTH_LONG).show()
                                // Opcional: navegar automáticamente
                                // navController.navigate("configuracion")
                            } else {
                                // 2. Si todo está ok, procedemos según el método de pago
                                if (carritoViewModel.metodoPagoSeleccionado == "Tarjeta") mostrarPagoFake = true
                                else {
                                    carritoViewModel.finalizarPedido(
                                        usuarioViewModel.nombre,
                                        usuarioViewModel.telefono,
                                        usuarioViewModel.direccion,
                                        { Toast.makeText(context, "¡Pedido enviado!", Toast.LENGTH_SHORT).show() },
                                        { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
                                    )
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp).height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = naranjaIllo),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !carritoViewModel.enviandoPedido
                    ) {
                        if (carritoViewModel.enviandoPedido) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        else Text("REALIZAR PEDIDO", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (mostrarPagoFake) {
        AlertDialog(
            onDismissRequest = { mostrarPagoFake = false },
            title = { Text("Pago Seguro") },
            text = {
                Column {
                    Text("Total: ${String.format("%.2f", carritoViewModel.calcularTotal())}€")
                    OutlinedTextField(value = "", onValueChange = {}, label = { Text("Nº Tarjeta") }, modifier = Modifier.padding(top = 12.dp))
                    Row(Modifier.padding(top = 8.dp)) {
                        OutlinedTextField(value = "", onValueChange = {}, label = { Text("Exp") }, modifier = Modifier.weight(1f))
                        Spacer(Modifier.width(8.dp))
                        OutlinedTextField(value = "", onValueChange = {}, label = { Text("CVV") }, modifier = Modifier.weight(1f))
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    mostrarPagoFake = false
                    carritoViewModel.finalizarPedido(usuarioViewModel.nombre, usuarioViewModel.telefono, usuarioViewModel.direccion, {
                        Toast.makeText(context, "Pago realizado", Toast.LENGTH_SHORT).show()
                    }, { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() })
                }) { Text("Pagar Ahora") }
            }
        )
    }
}

@Composable
fun MenuLateral(nombre: String, navController: NavController, drawerState: DrawerState, onCerrar: () -> Unit) {
    val scope = rememberCoroutineScope()
    ModalDrawerSheet(modifier = Modifier.width(300.dp)) {
        Box(Modifier.fillMaxWidth().background(naranjaIllo).padding(24.dp)) {
            Text("¡Hola, $nombre!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
        NavigationDrawerItem(label = { Text("Mi Perfil") }, selected = false, icon = { Icon(Icons.Default.Person, null) }, onClick = { scope.launch { drawerState.close() }; navController.navigate("configuracion") })
        HorizontalDivider(Modifier.padding(vertical = 8.dp))
        NavigationDrawerItem(label = { Text("Cerrar Sesión", color = Color.Red) }, selected = false, icon = { Icon(Icons.Default.ExitToApp, null, tint = Color.Red) }, onClick = onCerrar)
    }
}

@Composable
fun BarraDireccion(d: String) { Row(Modifier.fillMaxWidth().padding(16.dp), Arrangement.Center) { Icon(Icons.Default.LocationOn, null, tint = MarronBK); Spacer(Modifier.width(4.dp)); Text(d, color = MarronBK) } }

@Composable
fun CardInfoNegocio() {
    ElevatedCard(Modifier.padding(16.dp).fillMaxWidth(), shape = RoundedCornerShape(20.dp), colors = CardDefaults.elevatedCardColors(containerColor = Color.White)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painter = painterResource(R.drawable.logo), contentDescription = null, modifier = Modifier.fillMaxWidth().height(180.dp).padding(16.dp).clip(RoundedCornerShape(15.dp)), contentScale = ContentScale.Fit)
            Surface(color = naranjaIllo, modifier = Modifier.fillMaxWidth()) { Text("Abiertos hasta las 3:00 AM", Modifier.padding(12.dp), color = Color.White, fontWeight = FontWeight.Bold, textAlign = androidx.compose.ui.text.style.TextAlign.Center) }
            Text("Illo, Un campero", Modifier.padding(top = 16.dp), fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = MarronBK)
            Text("Los mejores camperos de Málaga", fontSize = 13.sp, color = Color.Gray)
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun BotonBuscar() { OutlinedButton(onClick = {}, Modifier.fillMaxWidth().padding(horizontal = 40.dp).height(50.dp), shape = RoundedCornerShape(30.dp)) { Text("Buscar productos...", color = Color.Gray) } }