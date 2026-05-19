package com.illouncampero.illouncampero.ui.screens

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.illouncampero.illouncampero.R
import com.illouncampero.illouncampero.model.Producto
import com.illouncampero.illouncampero.viewmodel.*
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.rememberPaymentSheet
import kotlinx.coroutines.launch


// ── Paleta ───────────────────────────────────────────────────────────────────
val MarronBK    = Color(0xFF2D1406)
val CremaBK     = Color(0xFFF5EBDC)
val RojoBK      = Color(0xFFD62300)
val naranjaIllo = Color(0xFFF39200)

private val gradientesPromo = listOf(
    listOf(Color(0xFF2D1406), Color(0xFF5C2A0E)),
    listOf(Color(0xFF1A3A1A), Color(0xFF2E7D32)),
    listOf(Color(0xFF1A237E), Color(0xFF303F9F)),
    listOf(Color(0xFF33691E), Color(0xFF558B2F)),
)

// ─────────────────────────────────────────────────────────────────────────────
// PANTALLA PRINCIPAL
// ─────────────────────────────────────────────────────────────────────────────
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
    var categoriaPreseleccionada by remember { mutableStateOf("campero") }
    val cantidadEnCarrito = carritoViewModel.contadorTotal()

    val tabInicio = stringResource(R.string.principal_inicio)
    val tabCarta  = stringResource(R.string.principal_carta)
    val tabPedir  = stringResource(R.string.principal_pedir)

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
                        Image(painter = painterResource(id = R.drawable.logo_circular), contentDescription = null, modifier = Modifier.height(52.dp))
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, null, tint = MarronBK, modifier = Modifier.size(26.dp))
                        }
                    },
                    actions = {
                        Box(modifier = Modifier.padding(end = 8.dp)) {
                            IconButton(onClick = { selectedTab = 2 }) {
                                Icon(Icons.Default.ShoppingCart, null, tint = MarronBK, modifier = Modifier.size(26.dp))
                            }
                            if (cantidadEnCarrito > 0) {
                                Badge(containerColor = RojoBK, modifier = Modifier.align(Alignment.TopEnd).offset(x = (-4).dp, y = 4.dp)) {
                                    Text("$cantidadEnCarrito", color = Color.White, fontSize = 10.sp)
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White),
                    modifier = Modifier.shadow(4.dp)
                )
            },
            bottomBar = {
                NavigationBar(containerColor = Color.White, tonalElevation = 0.dp, modifier = Modifier.shadow(8.dp)) {
                    listOf(tabInicio to Icons.Default.Home, tabCarta to Icons.Default.List, tabPedir to Icons.Default.ShoppingCart)
                        .forEachIndexed { index, item ->
                            NavigationBarItem(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                label = { Text(item.first, fontSize = 11.sp, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) },
                                icon = { Icon(item.second, null, modifier = Modifier.size(22.dp)) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = naranjaIllo, selectedTextColor = naranjaIllo,
                                    unselectedIconColor = Color(0xFFAAAAAA), unselectedTextColor = Color(0xFFAAAAAA),
                                    indicatorColor = Color(0xFFFFF3E0)
                                )
                            )
                        }
                }
            }
        ) { paddingValues ->
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                when (selectedTab) {
                    0 -> SeccionInicio(nombre, prodViewModel, carritoViewModel) { cat -> categoriaPreseleccionada = cat; selectedTab = 1 }
                    1 -> SeccionCarta(prodViewModel, carritoViewModel, categoriaPreseleccionada)
                    2 -> SeccionCesta(carritoViewModel, usuarioViewModel)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SECCIÓN INICIO
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun SeccionInicio(
    nombre: String,
    prodViewModel: ProductoViewModel,
    carritoViewModel: CarritoViewModel,
    onVerCarta: (String) -> Unit
) {
    val camperos  = remember(prodViewModel.listaProductos) { prodViewModel.listaProductos.filter { it.categoria?.lowercase() == "campero"   }.shuffled().take(6) }
    val entrantes = remember(prodViewModel.listaProductos) { prodViewModel.listaProductos.filter { it.categoria?.lowercase() == "entrantes" }.shuffled().take(5) }
    val bebidas   = remember(prodViewModel.listaProductos) { prodViewModel.listaProductos.filter { it.categoria?.lowercase() == "bebidas"   }.shuffled().take(5) }
    val ofertas         = prodViewModel.listaOfertas
    val ofertaDestacada: Producto? = prodViewModel.ofertaDestacada

    val holaTexto    = stringResource(R.string.principal_hola, nombre.ifBlank { "Illo" })
    val catCampero   = stringResource(R.string.principal_cat_campero)
    val catEntrantes = stringResource(R.string.principal_cat_entrantes)
    val catBebidas   = stringResource(R.string.principal_cat_bebidas)

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).background(CremaBK)) {

        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {
            Text(holaTexto, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = MarronBK)
            Text(stringResource(R.string.inicio_subtitulo), fontSize = 13.sp, color = Color(0xFF9A7A5A))
        }

        BannerOfertaDia(ofertaDestacada, carritoViewModel)
        Spacer(Modifier.height(14.dp))

        Button(
            onClick = { onVerCarta("campero") },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MarronBK),
            shape = RoundedCornerShape(14.dp),
            elevation = ButtonDefaults.buttonElevation(0.dp)
        ) {
            Text(stringResource(R.string.principal_pide_ahora), fontWeight = FontWeight.Black, color = Color.White, fontSize = 14.sp, letterSpacing = 0.5.sp)
        }

        Spacer(Modifier.height(24.dp))
        Text(stringResource(R.string.principal_que_apetece), fontWeight = FontWeight.Bold, color = MarronBK, fontSize = 15.sp, modifier = Modifier.padding(horizontal = 20.dp))
        Spacer(Modifier.height(14.dp))
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf(catCampero to Icons.Default.Fastfood, catEntrantes to Icons.Default.Icecream, catBebidas to Icons.Default.LocalDrink)
                .forEach { (label, icon) -> ItemCategoriaCirculo(label, icon) { onVerCarta(label.lowercase()) } }
        }

        Spacer(Modifier.height(28.dp))
        SeccionPromoCards(ofertas, carritoViewModel)
        Spacer(Modifier.height(28.dp))

        if (camperos.isNotEmpty()) {
            SeccionCarrusel(titulo = stringResource(R.string.principal_mas_vendidos), accionLabel = stringResource(R.string.inicio_ver_todos), onAccion = { onVerCarta("campero") }) {
                items(camperos) { CardProductoUniforme(it) { carritoViewModel.añadirProducto(it) } }
            }
            Spacer(Modifier.height(24.dp))
        }
        if (entrantes.isNotEmpty()) {
            SeccionCarrusel(titulo = stringResource(R.string.inicio_para_picar), accionLabel = stringResource(R.string.inicio_ver_entrantes), onAccion = { onVerCarta("entrantes") }) {
                items(entrantes) { CardProductoUniforme(it) { carritoViewModel.añadirProducto(it) } }
            }
            Spacer(Modifier.height(24.dp))
        }
        if (bebidas.isNotEmpty()) {
            SeccionCarrusel(titulo = stringResource(R.string.inicio_para_beber), accionLabel = stringResource(R.string.inicio_ver_bebidas), onAccion = { onVerCarta("bebidas") }) {
                items(bebidas) { CardProductoUniforme(it) { carritoViewModel.añadirProducto(it) } }
            }
            Spacer(Modifier.height(24.dp))
        }

        CardInfoNegocio()
        Spacer(Modifier.height(16.dp))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// BANNER OFERTA DEL DÍA
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun BannerOfertaDia(oferta: Producto?, carritoViewModel: CarritoViewModel) {
    val context = LocalContext.current
    val ofertaAñadida = stringResource(R.string.principal_oferta_añadida)
    if (oferta == null) return

    Box(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(Color(0xFFB71C1C), RojoBK, Color(0xFFE53935))))
            .height(175.dp)
    ) {
        Box(modifier = Modifier.size(180.dp).align(Alignment.CenterEnd).offset(x = 55.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.05f)))
        Box(modifier = Modifier.size(90.dp).align(Alignment.BottomEnd).offset(x = (-12).dp, y = 36.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.07f)))
        Column(modifier = Modifier.align(Alignment.CenterStart).padding(20.dp)) {
            Surface(shape = RoundedCornerShape(4.dp), color = Color.White.copy(alpha = 0.2f), modifier = Modifier.padding(bottom = 8.dp)) {
                Text(stringResource(R.string.principal_oferta_dia), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
            }
            Text(oferta.nombre, color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp, lineHeight = 24.sp)
            Text("${String.format("%.2f", oferta.precio)}€", color = Color(0xFFFFE082), fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, modifier = Modifier.padding(top = 4.dp, bottom = 12.dp))
            Button(
                onClick = { carritoViewModel.añadirProducto(oferta); Toast.makeText(context, ofertaAñadida, Toast.LENGTH_SHORT).show() },
                colors = ButtonDefaults.buttonColors(containerColor = naranjaIllo),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                elevation = ButtonDefaults.buttonElevation(4.dp)
            ) {
                Icon(Icons.Default.AddShoppingCart, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(stringResource(R.string.principal_oferta_añadir), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
        Icon(Icons.Default.Star, null, modifier = Modifier.size(80.dp).align(Alignment.CenterEnd).padding(end = 20.dp), tint = Color.White.copy(alpha = 0.1f))
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// CARRUSEL DE PROMOS
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun SeccionPromoCards(ofertas: List<Producto>, carritoViewModel: CarritoViewModel) {
    if (ofertas.isEmpty()) return
    Column {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(stringResource(R.string.inicio_ofertas_exclusivas), fontWeight = FontWeight.ExtraBold, color = MarronBK, fontSize = 16.sp)
                Text(stringResource(R.string.inicio_tiempo_limitado), fontSize = 12.sp, color = Color(0xFF9A7A5A))
            }
            Surface(shape = RoundedCornerShape(8.dp), color = RojoBK.copy(alpha = 0.1f)) {
                Text(stringResource(R.string.inicio_hot), color = RojoBK, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
        }
        Spacer(Modifier.height(12.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(ofertas) { CardPromo(it, carritoViewModel) }
        }
    }
}

@Composable
fun CardPromo(oferta: Producto, carritoViewModel: CarritoViewModel) {
    val context = LocalContext.current
    val gradiente = gradientesPromo[Math.abs(oferta.id.hashCode()) % gradientesPromo.size]
    Box(modifier = Modifier.width(220.dp).height(155.dp).clip(RoundedCornerShape(18.dp)).background(Brush.linearGradient(gradiente))) {
        Box(modifier = Modifier.size(100.dp).align(Alignment.TopEnd).offset(x = 30.dp, y = (-20).dp).clip(CircleShape).background(Color.White.copy(alpha = 0.07f)))
        Column(modifier = Modifier.align(Alignment.CenterStart).padding(16.dp)) {
            Text("🔥", fontSize = 24.sp)
            Spacer(Modifier.height(4.dp))
            Text(oferta.nombre, color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(oferta.descripcion, color = Color.White.copy(alpha = 0.75f), fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(shape = RoundedCornerShape(6.dp), color = Color.White.copy(alpha = 0.2f)) {
                    Text("${String.format("%.2f", oferta.precio)}€", color = Color.White, fontWeight = FontWeight.Black, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                }
                Button(
                    onClick = { carritoViewModel.añadirProducto(oferta); Toast.makeText(context, context.getString(R.string.inicio_producto_añadido, oferta.nombre), Toast.LENGTH_SHORT).show() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.25f)),
                    shape = RoundedCornerShape(8.dp), contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp), elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    Icon(Icons.Default.AddShoppingCart, null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.inicio_añadir), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SECCIÓN CARRUSEL GENÉRICO
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun SeccionCarrusel(titulo: String, accionLabel: String, onAccion: () -> Unit, contenido: androidx.compose.foundation.lazy.LazyListScope.() -> Unit) {
    Column {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text(titulo, fontWeight = FontWeight.ExtraBold, color = MarronBK, fontSize = 15.sp)
            TextButton(onClick = onAccion) { Text(accionLabel, color = naranjaIllo, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
        }
        Spacer(Modifier.height(8.dp))
        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) { contenido() }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// CARD PRODUCTO
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun CardProductoUniforme(producto: Producto, onAdd: () -> Unit) {
    Card(modifier = Modifier.width(150.dp).height(220.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(3.dp)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxWidth().height(110.dp)) {
                AsyncImage(model = producto.imagenUrl, contentDescription = null, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)), contentScale = ContentScale.Crop, placeholder = painterResource(R.drawable.logo), error = painterResource(R.drawable.logo))
                Surface(color = naranjaIllo, shape = RoundedCornerShape(bottomEnd = 10.dp, topStart = 16.dp), modifier = Modifier.align(Alignment.TopStart)) {
                    Text("${producto.precio}€", color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
            Column(modifier = Modifier.fillMaxSize().padding(10.dp), verticalArrangement = Arrangement.SpaceBetween) {
                Text(producto.nombre, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 2, overflow = TextOverflow.Ellipsis, color = MarronBK, lineHeight = 17.sp, modifier = Modifier.weight(1f))
                Button(onClick = onAdd, modifier = Modifier.fillMaxWidth().height(32.dp), colors = ButtonDefaults.buttonColors(containerColor = naranjaIllo), shape = RoundedCornerShape(8.dp), contentPadding = PaddingValues(0.dp)) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(stringResource(R.string.inicio_añadir), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// CATEGORÍA CÍRCULO
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun ItemCategoriaCirculo(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(onClick = onClick, shape = RoundedCornerShape(16.dp), color = Color.White, shadowElevation = 3.dp, modifier = Modifier.size(68.dp)) {
            Box(contentAlignment = Alignment.Center) { Icon(icon, null, tint = naranjaIllo, modifier = Modifier.size(28.dp)) }
        }
        Spacer(Modifier.height(6.dp))
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MarronBK)
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// CARD INFO NEGOCIO
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun CardInfoNegocio() {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(3.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painter = painterResource(R.drawable.logo), contentDescription = null, modifier = Modifier.fillMaxWidth().height(160.dp).padding(20.dp).clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Fit)
            Box(modifier = Modifier.fillMaxWidth().background(naranjaIllo).padding(12.dp)) {
                Text(stringResource(R.string.principal_abierto), color = Color.White, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(), fontSize = 14.sp)
            }
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(R.string.principal_nombre_negocio), fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = MarronBK)
                Text(stringResource(R.string.principal_slogan), fontSize = 13.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SECCIÓN CARTA
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SeccionCarta(prodViewModel: ProductoViewModel, carritoViewModel: CarritoViewModel, categoriaInicial: String) {
    val categoriasPrincipales = listOf("campero", "entrantes", "postres", "bebidas")
    val etiquetasCategorias = listOf(stringResource(R.string.carta_campero), stringResource(R.string.carta_entrantes), stringResource(R.string.carta_postres), stringResource(R.string.carta_bebidas))
    val indexInicial = categoriasPrincipales.indexOf(categoriaInicial).coerceAtLeast(0)
    val pagerState = rememberPagerState(initialPage = indexInicial, pageCount = { categoriasPrincipales.size })
    val scope = rememberCoroutineScope()
    val variosLabel = stringResource(R.string.varios)

    LaunchedEffect(categoriaInicial) { pagerState.scrollToPage(indexInicial) }

    Column(modifier = Modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = Color.White, contentColor = naranjaIllo, edgePadding = 16.dp,
            divider = { HorizontalDivider(color = Color(0xFFEEEEEE)) },
            indicator = { tabPositions ->
                if (pagerState.currentPage < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]).clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp)), color = naranjaIllo, height = 3.dp)
                }
            }
        ) {
            etiquetasCategorias.forEachIndexed { index, etiqueta ->
                Tab(selected = pagerState.currentPage == index, onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    text = { Text(etiqueta.uppercase(), fontWeight = if (pagerState.currentPage == index) FontWeight.ExtraBold else FontWeight.Medium, fontSize = 12.sp, color = if (pagerState.currentPage == index) naranjaIllo else Color(0xFFAAAAAA)) })
            }
        }
        if (prodViewModel.cargando) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = naranjaIllo) }
        } else {
            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { pageIndex ->
                val catActual = categoriasPrincipales[pageIndex]
                val productosFiltradosYAgrupados = remember(prodViewModel.listaProductos, catActual) {
                    prodViewModel.listaProductos.filter { (it.categoria ?: "").trim().lowercase() == catActual.lowercase() }.groupBy { (it.subcategoria ?: "Varios").trim() }
                }
                if (productosFiltradosYAgrupados.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.SearchOff, null, tint = Color(0xFFCCCCCC), modifier = Modifier.size(48.dp))
                            Spacer(Modifier.height(8.dp))
                            Text(stringResource(R.string.sin_productos_categoria, catActual.uppercase()), color = Color.Gray, fontSize = 14.sp)
                        }
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 100.dp)) {
                        productosFiltradosYAgrupados.forEach { (subCat, productos) ->
                            stickyHeader {
                                Box(modifier = Modifier.fillMaxWidth().background(CremaBK).padding(horizontal = 16.dp, vertical = 10.dp)) {
                                    Text(if (subCat.isBlank()) variosLabel else subCat.uppercase(), fontWeight = FontWeight.Black, color = MarronBK, fontSize = 12.sp, letterSpacing = 1.sp)
                                }
                            }
                            items(productos) { producto -> FilaProductoCliente(producto) { carritoViewModel.añadirProducto(producto) } }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilaProductoCliente(producto: Producto, onAdd: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 5.dp), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(modifier = Modifier.padding(10.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(model = producto.imagenUrl, contentDescription = null, modifier = Modifier.size(88.dp).clip(RoundedCornerShape(10.dp)), contentScale = ContentScale.Crop, placeholder = painterResource(id = R.drawable.logo), error = painterResource(id = R.drawable.logo))
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(producto.nombre, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MarronBK, maxLines = 2, overflow = TextOverflow.Ellipsis)
                if (producto.descripcion.isNotBlank()) { Text(producto.descripcion, fontSize = 12.sp, color = Color(0xFF9A9A9A), maxLines = 2, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 2.dp)) }
                Spacer(Modifier.height(6.dp))
                Text("${producto.precio}€", fontWeight = FontWeight.ExtraBold, color = naranjaIllo, fontSize = 16.sp)
            }
            Spacer(Modifier.width(8.dp))
            Surface(onClick = onAdd, shape = RoundedCornerShape(10.dp), color = naranjaIllo, modifier = Modifier.size(36.dp)) {
                Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(20.dp)) }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// SECCIÓN CESTA
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeccionCesta(carritoViewModel: CarritoViewModel, usuarioViewModel: UsuarioViewModel) {
    val context = LocalContext.current
    val msgRellena       = stringResource(R.string.cesta_rellena_perfil)
    val msgPedidoEnviado = stringResource(R.string.cesta_pedido_enviado)
    val msgPagoRealizado = stringResource(R.string.cesta_pago_realizado)

    val paymentSheet = rememberPaymentSheet { result ->
        when (result) {
            is PaymentSheetResult.Completed -> {
                carritoViewModel.onPagoExitoso()
                Toast.makeText(context, msgPagoRealizado, Toast.LENGTH_SHORT).show()
            }
            is PaymentSheetResult.Canceled -> carritoViewModel.onPagoCancelado()
            is PaymentSheetResult.Failed   -> {
                carritoViewModel.onPagoCancelado()
                Toast.makeText(context, "Error en el pago: ${result.error.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    LaunchedEffect(carritoViewModel.clientSecretStripe) {
        carritoViewModel.clientSecretStripe?.let { secret ->
            paymentSheet.presentWithPaymentIntent(
                secret,
                PaymentSheet.Configuration("Illo Un Campero")
            )
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 20.dp, vertical = 16.dp)) {
            Text(stringResource(R.string.cesta_titulo), fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = MarronBK)
        }
        HorizontalDivider(color = Color(0xFFEEEEEE))

        if (carritoViewModel.items.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.ShoppingCart, null, tint = Color(0xFFDDDDDD), modifier = Modifier.size(64.dp))
                    Spacer(Modifier.height(12.dp))
                    Text(stringResource(R.string.cesta_vacia), color = Color(0xFFAAAAAA), fontSize = 16.sp)
                }
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f), contentPadding = PaddingValues(bottom = 8.dp)) {

                // ── Items ─────────────────────────────────────────────────────
                items(carritoViewModel.items) { item ->
                    Card(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(1.dp)) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.producto.nombre, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MarronBK)
                                Text("${item.producto.precio}€ / ud", fontSize = 12.sp, color = Color.Gray)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(onClick = { carritoViewModel.quitarProducto(item.producto) }, modifier = Modifier.size(32.dp)) {
                                    Icon(Icons.Default.RemoveCircleOutline, null, tint = RojoBK, modifier = Modifier.size(22.dp))
                                }
                                Text("${item.cantidad}", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = MarronBK, modifier = Modifier.widthIn(min = 20.dp), textAlign = TextAlign.Center)
                                IconButton(onClick = { carritoViewModel.añadirProducto(item.producto) }, modifier = Modifier.size(32.dp)) {
                                    Icon(Icons.Default.AddCircle, null, tint = naranjaIllo, modifier = Modifier.size(22.dp))
                                }
                            }
                            Text("${String.format("%.2f", item.producto.precio * item.cantidad)}€", fontWeight = FontWeight.ExtraBold, color = naranjaIllo, fontSize = 14.sp, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }

                // ── Notas ─────────────────────────────────────────────────────
                item {
                    OutlinedTextField(
                        value = carritoViewModel.notasInput, onValueChange = { carritoViewModel.notasInput = it },
                        label = { Text(stringResource(R.string.cesta_notas)) },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = naranjaIllo, focusedLabelColor = naranjaIllo)
                    )
                }

                // ── Método de pago ────────────────────────────────────────────
                item {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(selected = carritoViewModel.metodoPagoSeleccionado == "EFECTIVO", onClick = { carritoViewModel.metodoPagoSeleccionado = "EFECTIVO" }, label = { Text(stringResource(R.string.cesta_efectivo), fontWeight = FontWeight.SemiBold) }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFFFF3E0), selectedLabelColor = naranjaIllo), modifier = Modifier.weight(1f))
                        FilterChip(selected = carritoViewModel.metodoPagoSeleccionado == "TARJETA", onClick = { carritoViewModel.metodoPagoSeleccionado = "TARJETA" }, label = { Text(stringResource(R.string.cesta_tarjeta), fontWeight = FontWeight.SemiBold) }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFFFF3E0), selectedLabelColor = naranjaIllo), modifier = Modifier.weight(1f))
                    }
                }

                // ── ✅ CUPÓN DE DESCUENTO ──────────────────────────────────────
                item {
                    CuponDescuentoBox(carritoViewModel)
                }
            }

            // ── Resumen de totales + botón ────────────────────────────────────
            Surface(tonalElevation = 0.dp, color = Color.White, shadowElevation = 12.dp, shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)) {
                Column(Modifier.padding(20.dp)) {

                    // Desglose solo visible cuando hay cupón aplicado
                    if (carritoViewModel.cuponAplicado != null) {
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                            Text(stringResource(R.string.cesta_subtotal), fontSize = 14.sp, color = Color.Gray)
                            Text("${String.format("%.2f", carritoViewModel.calcularSubtotal())}€", fontSize = 14.sp, color = Color.Gray)
                        }
                        Spacer(Modifier.height(4.dp))
                        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                            Text(stringResource(R.string.cesta_descuento, carritoViewModel.cuponDescuento.toInt()), fontSize = 14.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.SemiBold)
                            Text("-${String.format("%.2f", carritoViewModel.calcularAhorro())}€", fontSize = 14.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(Modifier.height(8.dp))
                        HorizontalDivider(color = Color(0xFFEEEEEE))
                        Spacer(Modifier.height(8.dp))
                    }

                    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                        Text(stringResource(R.string.cesta_total), fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
                        Text("${String.format("%.2f", carritoViewModel.calcularTotal())}€", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = MarronBK)
                    }
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = {
                            if (usuarioViewModel.nombre.isBlank() || usuarioViewModel.direccion.isBlank() || usuarioViewModel.telefono.isBlank()) {
                                Toast.makeText(context, msgRellena, Toast.LENGTH_LONG).show()
                            } else {
                                if (carritoViewModel.metodoPagoSeleccionado == "TARJETA") {
                                    carritoViewModel.finalizarPedidoConTarjeta(
                                        usuarioViewModel.nombre, usuarioViewModel.telefono, usuarioViewModel.direccion
                                    ) { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
                                } else {
                                    carritoViewModel.finalizarPedido(usuarioViewModel.nombre, usuarioViewModel.telefono, usuarioViewModel.direccion,
                                        { Toast.makeText(context, msgPedidoEnviado, Toast.LENGTH_SHORT).show() },
                                        { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() })
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = naranjaIllo),
                        shape = RoundedCornerShape(14.dp),
                        enabled = !carritoViewModel.enviandoPedido,
                        elevation = ButtonDefaults.buttonElevation(0.dp)
                    ) {
                        if (carritoViewModel.enviandoPedido) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        else Text(stringResource(R.string.cesta_realizar_pedido), fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, letterSpacing = 0.5.sp)
                    }
                }
            }
        }
    }

}

// ─────────────────────────────────────────────────────────────────────────────
// ✅ CAJA DE CUPÓN DE DESCUENTO
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun CuponDescuentoBox(carritoViewModel: CarritoViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            // ── Estado: cupón aplicado ✓ ──────────────────────────────────────
            if (carritoViewModel.cuponAplicado != null) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFE8F5E9)) {
                            Text("✓ ${carritoViewModel.cuponAplicado}", color = Color(0xFF2E7D32), fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp))
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(carritoViewModel.cuponDescripcion, fontSize = 12.sp, color = Color(0xFF2E7D32))
                    }
                    IconButton(onClick = { carritoViewModel.quitarCupon() }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = stringResource(R.string.cupon_quitar), tint = Color.Gray, modifier = Modifier.size(18.dp))
                    }
                }
                return@Column
            }

            // ── Estado: campo para introducir cupón ───────────────────────────
            Text(stringResource(R.string.cupon_tienes_cupon), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MarronBK, modifier = Modifier.padding(bottom = 8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = carritoViewModel.cuponInput,
                    onValueChange = { carritoViewModel.cuponInput = it.uppercase(); carritoViewModel.cuponError = null },
                    placeholder = { Text(stringResource(R.string.cupon_placeholder), fontSize = 13.sp, color = Color.LightGray) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    isError = carritoViewModel.cuponError != null,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = naranjaIllo, focusedLabelColor = naranjaIllo, errorBorderColor = RojoBK)
                )
                Button(
                    onClick = { carritoViewModel.validarCupon() },
                    enabled = !carritoViewModel.validandoCupon,
                    colors = ButtonDefaults.buttonColors(containerColor = MarronBK),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    if (carritoViewModel.validandoCupon) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    } else {
                        Text(stringResource(R.string.cupon_aplicar), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            // Mensaje de error
            if (carritoViewModel.cuponError != null) {
                Spacer(Modifier.height(4.dp))
                Text(carritoViewModel.cuponError!!, color = RojoBK, fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// MENÚ LATERAL
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun MenuLateral(nombre: String, navController: NavController, drawerState: DrawerState, onCerrar: () -> Unit) {
    val scope = rememberCoroutineScope()
    val holaMenu = stringResource(R.string.menu_hola, nombre)
    ModalDrawerSheet(modifier = Modifier.width(280.dp), drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)) {
        Box(modifier = Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(MarronBK, Color(0xFF4A2008)))).padding(24.dp, 36.dp)) {
            Column {
                Box(modifier = Modifier.size(52.dp).clip(CircleShape).background(naranjaIllo), contentAlignment = Alignment.Center) {
                    Text(nombre.firstOrNull()?.uppercaseChar()?.toString() ?: "I", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp)
                }
                Spacer(Modifier.height(12.dp))
                Text(holaMenu, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
        Spacer(Modifier.height(8.dp))
        NavigationDrawerItem(label = { Text(stringResource(R.string.menu_perfil), fontWeight = FontWeight.Medium) }, selected = false, icon = { Icon(Icons.Default.Person, null, tint = MarronBK) }, onClick = { scope.launch { drawerState.close() }; navController.navigate("configuracion") }, modifier = Modifier.padding(horizontal = 12.dp))
        NavigationDrawerItem(label = { Text(stringResource(R.string.menu_pedidos), fontWeight = FontWeight.Medium) }, selected = false, icon = { Icon(Icons.Default.List, null, tint = MarronBK) }, onClick = { scope.launch { drawerState.close() }; navController.navigate("mis_pedidos") }, modifier = Modifier.padding(horizontal = 12.dp))
        Spacer(Modifier.weight(1f))
        HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp))
        NavigationDrawerItem(label = { Text(stringResource(R.string.menu_cerrar_sesion), color = RojoBK, fontWeight = FontWeight.Medium) }, selected = false, icon = { Icon(Icons.Default.ExitToApp, null, tint = RojoBK) }, onClick = onCerrar, modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
    }
}