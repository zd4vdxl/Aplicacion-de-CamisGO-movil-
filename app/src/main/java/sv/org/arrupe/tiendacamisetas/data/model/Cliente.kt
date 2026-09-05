package sv.org.arrupe.tiendacamisetas.data.model

/**
 * Representa la tabla Clientes.
 */
data class Cliente(
    val clienteId: Int? = null,
    val nombre: String,
    val apellido: String,
    val email: String,
    val telefono: String? = null,
    val direccion: String? = null,
    val fechaRegistro: String? = null
)
