package com.illouncampero.illouncampero.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.illouncampero.illouncampero.model.Usuario

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Estados para la UI
    var isLoading by mutableStateOf(false)
    var loginError by mutableStateOf<String?>(null)
    var nombreUsuario by mutableStateOf("Cargando...")

    // Función de Login
    fun login(email: String, pass: String, onSuccess: () -> Unit) {
        isLoading = true
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            isLoading = false
            if (task.isSuccessful) onSuccess()
            else loginError = task.exception?.message
        }
    }

    // Función de Registro
    fun registrarUsuario(usuario: Usuario, pass: String, onResult: (Boolean, String) -> Unit) {
        auth.createUserWithEmailAndPassword(usuario.email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val uid = auth.currentUser?.uid ?: ""
                db.collection("usuarios").document(uid).set(usuario.copy(uid = uid))
                    .addOnSuccessListener { onResult(true, "¡Cuenta creada!") }
                    .addOnFailureListener { onResult(false, it.message ?: "Error en DB") }
            } else {
                onResult(false, task.exception?.message ?: "Error Auth")
            }
        }
    }

    // Función para obtener nombre en Home
    fun obtenerNombreUsuario() {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("usuarios").document(uid).get().addOnSuccessListener { doc ->
                nombreUsuario = doc.getString("nombre") ?: "Usuario"
            }
        }
    }
}