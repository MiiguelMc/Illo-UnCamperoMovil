package com.illouncampero.illouncampero.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.illouncampero.illouncampero.model.Usuario

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Estado para el nombre del usuario en la Home
    var nombreUsuario by mutableStateOf("Cargando...")

    // Función para el LOGIN
    fun login(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) onResult(true, null)
            else onResult(false, task.exception?.message)
        }
    }

    // Función para el REGISTRO
    fun registrarUsuario(usuario: Usuario, pass: String, onResult: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(usuario.email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val uid = auth.currentUser?.uid ?: ""
                db.collection("usuarios").document(uid).set(usuario.copy(uid = uid))
                    .addOnSuccessListener { onResult(true, null) }
                    .addOnFailureListener { onResult(false, it.message) }
            } else {
                onResult(false, task.exception?.message)
            }
        }
    }

    // Función para obtener el nombre del usuario
    fun obtenerNombreUsuario() {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("usuarios").document(uid).get().addOnSuccessListener { doc ->
                nombreUsuario = doc.getString("nombre") ?: "Usuario"
            }
        }
    }
}