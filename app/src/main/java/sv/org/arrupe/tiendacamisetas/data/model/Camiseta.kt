package sv.org.arrupe.tiendacamisetas.data.model

import com.google.gson.annotations.SerializedName

data class Camiseta(
    @SerializedName("camisetaID")
    val camisetaId: Int = 0,

    val equipo: String? = null,
    val temporada: String? = null,
    val tipoCamiseta: String? = null,
    val precioBase: Double = 0.0,
    val imagenUrl: String? = null,
    val nombre: String = "",
    val descripcion: String? = null
)