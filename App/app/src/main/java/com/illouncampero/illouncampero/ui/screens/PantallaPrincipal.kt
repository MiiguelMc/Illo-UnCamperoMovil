package com.illouncampero.illouncampero.ui.screens

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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.illouncampero.illouncampero.R
import com.illouncampero.illouncampero.viewmodel.AuthViewModel

@Composable
fun PantallaPrincipal(navController: NavController, viewModel: AuthViewModel) {
    // Al cargar la pantalla, le pedimos al ViewModel que busque el nombre
    LaunchedEffect(Unit) {
        viewModel.obtenerNombreUsuario()
    }

    val nombre = viewModel.nombreUsuario
    // --- EL RESTO DEL DISEÑO ---
    val naranjaIllo = Color(0xFFF39200)
    val verdeIllo = Color(0xFF008445)
    val azulOscuro = Color(0xFF0A0E21)

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {

        // ... Header con logo y menú ... (lo que ya tenías)

        // --- BARRA USUARIO Y CARRITO ---
        Row(modifier = Modifier.fillMaxWidth().height(60.dp)) {
            // Sección Usuario (Naranja)
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(naranjaIllo)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Foto de perfil
                Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.LightGray))
                Spacer(Modifier.width(8.dp))

                // AQUÍ ESTÁ EL CAMBIO: Ya no pone María, pone la variable
                Text(
                    text = nombre,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            // Sección Carrito (Verde)
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(verdeIllo)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Icon(painter = painterResource(id = R.drawable.logo), contentDescription = null, tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text("79.89€", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        // --- DIRECCIÓN ---
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = azulOscuro)
            Text("Avenida Juan XXIII", fontSize = 14.sp, color = azulOscuro)
        }

        // --- CARD CENTRAL (Imagen y Datos) ---
        ElevatedCard(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFF2F2F2))
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Imagen del campero/chica
                Image(
                    painter = painterResource(id = R.drawable.logo), // Pon una de tus imágenes
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    contentScale = ContentScale.Crop
                )

                // Botón Naranja "Abiertos hasta..."
                Surface(
                    color = naranjaIllo,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Text(
                        "Abiertos hasta las 3:00 AM",
                        color = Color.White,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(Modifier.height(16.dp))
                Text("Los mejores camperos y pizza de Málaga", fontSize = 12.sp)
                Text("Illo, Un campero", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)

                Spacer(Modifier.height(16.dp))

                // Pedido Mínimo
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = azulOscuro),
                    modifier = Modifier.fillMaxWidth(0.8f).padding(vertical = 4.dp)
                ) {
                    Text("Pedido mínimo : 12€")
                }

                // Tiempo espera
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = azulOscuro),
                    modifier = Modifier.fillMaxWidth(0.8f).padding(vertical = 4.dp)
                ) {
                    Text("Espera de 20-25 minutos")
                }

                Spacer(Modifier.height(20.dp))
            }
        }

        // --- BOTÓN BUSCAR ---
        OutlinedButton(
            onClick = { /* Ir a búsqueda */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp, vertical = 20.dp)
                .height(55.dp),
            shape = RoundedCornerShape(30.dp)
        ) {
            Text("Buscar", color = Color.Gray)
        }
    }
}