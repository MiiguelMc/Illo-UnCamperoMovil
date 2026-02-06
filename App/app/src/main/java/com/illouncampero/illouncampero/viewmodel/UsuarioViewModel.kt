package com.illouncampero.illouncampero.viewmodel

import Usuario
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.illouncampero.illouncampero.data.repository.UsuarioRepository
import kotlinx.coroutines.launch

class UsuarioViewModel : ViewModel() {
    private val repository = UsuarioRepository()
    private val auth = FirebaseAuth.getInstance()

    var nombre by mutableStateOf("")
    var email by mutableStateOf("")
    var telefono by mutableStateOf("")
    var direccion by mutableStateOf("")
    var cargando by mutableStateOf(false)
    var mensaje by mutableStateOf<String?>(null)

    fun cargarPerfil() {
        val user = auth.currentUser ?: return
        val uid = user.uid
        email = user.email ?: ""

        viewModelScope.launch {
            cargando = true
            // Ahora esto funcionará porque el repository devuelve un Usuario?
            val perfil = repository.obtenerPerfilDirecto(uid)

            if (perfil != null) {
                nombre = perfil.nombre
                telefono = perfil.telefono
                direccion = perfil.direccion ?: ""
                println("DEBUG_ILLO: Perfil cargado correctamente")
            }
            cargando = false
        }
    }

    fun guardarCambios(onSuccess: () -> Unit) {
        viewModelScope.launch {
            cargando = true
            try {
                val usuarioActualizado = Usuario(
                    uid = auth.currentUser?.uid ?: "",
                    nombre = nombre,
                    email = email,
                    telefono = telefono,
                    direccion = direccion
                )
                repository.actualizarPerfil(usuarioActualizado)
                mensaje = "¡Perfil actualizado!"
                onSuccess()
            } catch (e: Exception) {
                mensaje = "Error al guardar cambios"
            } finally {
                cargando = false
            }
        }
    }

    fun restablecerContrasena() {
        val correo = auth.currentUser?.email
        if (correo != null) {
            auth.sendPasswordResetEmail(correo)
                .addOnCompleteListener { task ->
                    mensaje = if (task.isSuccessful) "Correo enviado a $correo" else "Error al enviar"
                }
        }
    }
}