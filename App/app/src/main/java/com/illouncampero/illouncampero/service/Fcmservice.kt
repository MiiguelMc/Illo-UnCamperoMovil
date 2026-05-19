package com.illouncampero.illouncampero.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.illouncampero.illouncampero.MainActivity
import com.illouncampero.illouncampero.R

class FCMService : FirebaseMessagingService() {

    companion object {
        const val CHANNEL_ID = "pedidos_channel"
        const val CHANNEL_NAME = "Estado de pedidos"
    }

    /**
     * Se llama cuando llega una notificación mientras la app está en primer plano,
     * o para notificaciones de tipo "data" (sin bloque notification) en cualquier estado.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val titulo = remoteMessage.notification?.title ?: "IllouCampero"
        val cuerpo  = remoteMessage.notification?.body  ?: "Actualización de tu pedido"

        mostrarNotificacion(titulo, cuerpo)
    }

    /**
     * FCM puede renovar el token en cualquier momento.
     * Cuando eso ocurre, lo enviamos de nuevo al backend para mantenerlo actualizado.
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        println("DEBUG_ILLO: Token FCM renovado: $token")
        // El UsuarioViewModel ya se encarga de enviarlo al backend al cargar el perfil,
        // pero podemos guardarlo en SharedPreferences por si acaso para no perderlo.
        getSharedPreferences("fcm_prefs", Context.MODE_PRIVATE)
            .edit()
            .putString("fcm_token", token)
            .apply()
    }

    private fun mostrarNotificacion(titulo: String, cuerpo: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crear el canal (obligatorio en Android 8+, ignorado en versiones anteriores)
        val canal = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notificaciones sobre el estado de tus pedidos"
        }
        notificationManager.createNotificationChannel(canal)

        // Al pulsar la notificación, abre la app en MainActivity
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificacion = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)   // Usa el ícono de tu app
            .setContentTitle(titulo)
            .setContentText(cuerpo)
            .setAutoCancel(true)                   // Se descarta al pulsar
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notificacion)
    }
}