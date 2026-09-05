package sv.org.arrupe.tiendacamisetas.data.model

import com.google.gson.annotations.SerializedName

/** Cuerpo enviado a POST /api/Auth/registro */
data class RegistroRequest(
    @SerializedName("Nombre")
    val nombre: String,

    @SerializedName("Correo")
    val correo: String,

    @SerializedName("Telefono")
    val telefono: String,

    @SerializedName("Password")
    val password: String
)

/** Cuerpo enviado a POST /api/Auth/login */
data class LoginRequest(
    @SerializedName("Correo")
    val correo: String,

    @SerializedName("Password")
    val password: String
)

/** Respuesta esperada de POST /api/Auth/login */
data class LoginResponse(
    @SerializedName("token")
    val token: String,

    @SerializedName("cliente")
    val cliente: ClienteData? = null
)

data class ClienteData(
    @SerializedName("clienteID")
    val clienteId: Int,

    @SerializedName("nombre")
    val nombre: String?,

    @SerializedName("correo")
    val correo: String?
)