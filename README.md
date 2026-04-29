# Illo Un Campero — App Móvil

Aplicación Android nativa del proyecto TFG. Permite a los clientes consultar la carta, gestionar el carrito, pagar con tarjeta y hacer seguimiento de sus pedidos en tiempo real.

![Kotlin](https://img.shields.io/badge/Kotlin-Android-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Compose](https://img.shields.io/badge/Jetpack_Compose-UI-4285F4?style=for-the-badge&logo=jetpack-compose&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-Auth_+_FCM-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)
![Stripe](https://img.shields.io/badge/Stripe-Pagos-635BFF?style=for-the-badge&logo=stripe&logoColor=white)

---

## Características

- Carta de productos organizada por categorías y subcategorías
- Carrito de compra con resumen y total
- Pago con tarjeta mediante Stripe PaymentSheet nativa
- Login y registro con Firebase Auth
- Seguimiento de pedidos en tiempo real
- Historial de pedidos propios
- Pantalla de cocina para rol COCINA
- Panel de administración para rol ADMIN (CRUD de productos)
- Notificaciones push con FCM
- Arquitectura MVVM con repositorios

---

## Stack

| Capa | Tecnología |
|---|---|
| Lenguaje | Kotlin |
| UI | Jetpack Compose |
| Arquitectura | MVVM (ViewModel + StateFlow + Repository) |
| Autenticación | Firebase Auth |
| Notificaciones | Firebase Cloud Messaging (FCM) |
| HTTP | Retrofit 2 |
| Pagos | Stripe Android SDK (PaymentSheet) |
| Navegación | Navigation Compose |

---

## Estructura del proyecto

```
App/app/src/main/java/com/illouncampero/illouncampero/
├── data/
│   ├── network/
│   │   ├── IlloApiService.kt       # Definición de endpoints Retrofit
│   │   └── RetrofitClient.kt       # Configuración de Retrofit
│   └── repository/
│       ├── PedidoRepository.kt
│       ├── ProductoRepository.kt
│       └── UsuarioRepository.kt
├── model/
│   ├── CuponResponse.kt
│   ├── IntentPagoResponse.kt
│   ├── Pedido.kt
│   ├── Producto.kt
│   └── Usuario.kt
├── service/
│   └── Fcmservice.kt               # Notificaciones push
├── ui/
│   ├── screens/
│   │   ├── PantallaAdmin.kt
│   │   ├── PantallaCocina.kt
│   │   ├── PantallaConfiguracion.kt
│   │   ├── PantallaDetallePedido.kt
│   │   ├── PantallaLogin.kt
│   │   ├── PantallaMisPedidos.kt
│   │   ├── PantallaPrincipal.kt
│   │   ├── PantallaRegistro.kt
│   │   └── PantallaSplash.kt
│   └── theme/
│       └── Colores.kt              # Paleta de colores del proyecto
├── util/
│   └── FirebaseExt.kt              # bearerToken() para obtener token Firebase
├── viewmodel/
│   ├── AdminViewModel.kt
│   ├── AuthViewModel.kt
│   ├── CarritoViewModel.kt
│   ├── PagoViewModel.kt
│   ├── PedidoViewModel.kt
│   ├── ProductoViewModel.kt
│   └── UsuarioViewModel.kt
└── MainActivity.kt                 # NavHost + init Stripe
```

---

## Instalación

### Requisitos
- Android Studio Hedgehog o superior
- JDK 17+
- minSdk 35 / targetSdk 36

### Configuración

1. Descarga el archivo `google-services.json` de la consola de Firebase y colócalo en `App/app/`

2. En `MainActivity.kt`, sustituye la clave pública de Stripe si usas tu propio proyecto:

```kotlin
PaymentConfiguration.init(applicationContext, "pk_test_...")
```

3. En `data/network/RetrofitClient.kt`, ajusta la `BASE_URL` si el backend no corre en local:

```kotlin
private const val BASE_URL = "http://10.0.2.2:8080/"
```

4. Sincroniza Gradle y ejecuta en emulador o dispositivo físico.

---

## Roles

| Rol | Pantallas disponibles |
|---|---|
| CLIENTE | Splash, Login, Registro, Principal (carta), Carrito, Mis Pedidos, Detalle Pedido, Configuración |
| COCINA | Todo lo anterior + Pantalla Cocina |
| ADMIN | Todo lo anterior + Panel Admin (CRUD de productos) |
