package sv.org.arrupe.tiendacamisetas.data.model

/** Representa la tabla ServiciosPersonalizacion (estampados, parches, etc.). */
data class Servicio(
    val servicioId: Int,
    val nombreServicio: String,
    val calidad: String, // "Premium" o "Calidad Precio"
    val precioAdicional: Double
)
