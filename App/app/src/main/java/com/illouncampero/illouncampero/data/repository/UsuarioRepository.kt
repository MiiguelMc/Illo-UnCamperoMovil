package com.illouncampero.illouncampero.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.illouncampero.illouncampero.model.Usuario

class UsuarioRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Lógica de Login
    fun login(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) onResult(true, null)
            else onResult(false, task.exception?.message)
        }
    }

    // Lógica de Registro
    fun registrar(usuario: Usuario, pass: String, onResult: (Boolean, String?) -> Unit) {
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

    // Obtener datos del usuario
    fun getNombreUsuario(uid: String, onResult: (String) -> Unit) {
        db.collection("usuarios").document(uid).get().addOnSuccessListener { doc ->
            onResult(doc.getString("nombre") ?: "Usuario")
        }
    }

    // En UsuarioRepository.kt
    fun isAdmin(onResult: (Boolean) -> Unit) {
        val uid = auth.currentUser?.uid
        if (uid != null) {
            db.collection("usuarios").document(uid).get().addOnSuccessListener { doc ->
                val rol = doc.getString("rol")
                onResult(rol == "admin") // Devuelve true si el rol es admin
            }
        }
    }

    fun getCurrentUid(): String? = auth.currentUser?.uid

    fun logout() = auth.signOut()
}