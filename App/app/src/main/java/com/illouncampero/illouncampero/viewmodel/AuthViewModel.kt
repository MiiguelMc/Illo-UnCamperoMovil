package com.illouncampero.illouncampero.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.illouncampero.illouncampero.data.repository.UsuarioRepository
import com.illouncampero.illouncampero.model.Usuario


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
    fun subirProducto(nombre: String, precio: Double, descripcion: String) {
        val producto = hashMapOf(
            "nombre" to nombre,
            "precio" to precio,
            "descripcion" to descripcion,
            "disponible" to true
        )
        db.collection("productos").add(producto)
            .addOnSuccessListener { /* Toast de éxito */ }
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
                        if (rol == "admin") {
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
}


