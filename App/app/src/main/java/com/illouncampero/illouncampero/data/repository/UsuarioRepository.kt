package com.illouncampero.illouncampero.data.repository

import Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.illouncampero.illouncampero.data.network.RetrofitClient
import kotlinx.coroutines.tasks.await

class UsuarioRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val api = RetrofitClient.instancia // Tu cliente Retrofit

    // Lógica de Login
    fun login(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) onResult(true, null)
            else onResult(false, task.exception?.message)
        }
    }

        // LA FUNCIÓN DEBE SER ASÍ PARA QUE EL VIEWMODEL LA ENTIENDA
        suspend fun obtenerPerfilDirecto(uid: String): Usuario? {
            return try {
                // El .await() es lo que hace que 'val perfil' en el VM sea un Usuario y no una tarea
                val snapshot = db.collection("usuarios").document(uid).get().await()
                snapshot.toObject(Usuario::class.java)
            } catch (e: Exception) {
                null
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

    private suspend fun obtenerToken(): String {
        return "Bearer ${FirebaseAuth.getInstance().currentUser?.getIdToken(false)?.await()?.token}"
    }

    suspend fun obtenerPerfil(): Usuario? {
        val token = obtenerToken()
        // Asegúrate de tener este endpoint en tu SpringBoot
        return api.getPerfil(token).body()
    }

    suspend fun actualizarPerfil(usuario: Usuario) {
        val token = obtenerToken()
        // Asegúrate de tener este endpoint en tu SpringBoot (PUT)
        api.updatePerfil(token, usuario)
    }

    fun getCurrentUid(): String? = auth.currentUser?.uid

    fun logout() = auth.signOut()
}