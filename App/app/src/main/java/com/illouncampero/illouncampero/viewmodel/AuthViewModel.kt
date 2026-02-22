package com.illouncampero.illouncampero.viewmodel

import Usuario
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.illouncampero.illouncampero.data.repository.UsuarioRepository


class AuthViewModel : ViewModel() {
    // Instanciamos el repositorio
    private val repository = UsuarioRepository()

    var nombreUsuario by mutableStateOf("Cargando...")
    var isLoading by mutableStateOf(false)
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun login(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        repository.login(email, pass, onResult)
    }

    fun registrarUsuario(usuario: Usuario, pass: String, onResult: (Boolean, String?) -> Unit) {
        repository.registrar(usuario, pass, onResult)
    }

    fun obtenerNombreUsuario() {
        val uid = repository.getCurrentUid()
        if (uid != null) {
            repository.getNombreUsuario(uid) { nombre ->
                nombreUsuario = nombre
            }
        }
    }

    fun cerrarSesion(onSuccess: () -> Unit) {
        repository.logout()
        onSuccess()
    }

    // En AuthViewModel.kt
    fun verificarRolYEntrar(navController: NavController) {
        val uid = auth.currentUser?.uid

        if (uid != null) {
            // Guardamos la referencia al documento
            val docRef = db.collection("usuarios").document(uid)

            docRef.get()
                .addOnSuccessListener { doc ->
                    if (doc != null && doc.exists()) {
                        val rol = doc.getString("rol")
                        if (rol == "ADMIN") {
                            navController.navigate("admin_panel") {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    // Es vital tener esto para saber si hay errores de red o permisos
                    println("Error al obtener el rol: ${e.message}")
                }
        }
    }

    fun revisarSesionActual(navController: NavController) {
        if (auth.currentUser != null) {
            verificarRolYEntrar(navController)
        } else {
            navController.navigate("login") {
                // Borramos la Splash para que no puedan volver atrás
                popUpTo("splash") { inclusive = true }
            }
        }
    }
}


