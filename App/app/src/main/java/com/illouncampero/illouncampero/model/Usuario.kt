// EN EL MÓVIL (Kotlin)
data class Usuario(
    // Usa @SerializedName si en el JSON de SpringBoot se llaman distinto
    val uid: String = "",
    val nombre: String = "",
    val apellidos: String = "",
    val email: String = "",
    val telefono: String = "",
    val direccion: String = "",
    val rol: String = "CLIENTE"
)