package sv.org.arrupe.tiendacamisetas.data.model

/** Respuesta de GET /api/Carritos/cliente/{clienteId}. */
data class CarritoResponse(
    val carritoId: Int,
    val clienteId: Int,
    val items: List<CarritoItem> = emptyList()
)

/**
 * Un ítem dentro del carrito, combinando datos de DetallesCarrito con la
 * info de la camiseta/servicio (similar a la vista vw_CarritoDetallado).
 */
data class CarritoItem(
    val detalleCarritoId: Int,
    val bodegaId: Int,
    val servicioId: Int? = null,
    val cantidad: Int,
    val equipo: String? = null,
    val temporada: String? = null,
    val tipoCamiseta: String? = null,
    val talla: String? = null,
    val precioBase: Double = 0.0,
    val servicioPersonalizacion: String? = null,
    val calidadPersonalizacion: String? = null,
    val precioServicio: Double = 0.0,
    val subtotalItem: Double = 0.0
)

/** Cuerpo enviado a POST /api/Carritos/item */
data class AgregarItemRequest(
    val clienteId: Int,
    val bodegaId: Int,
    val servicioId: Int? = null,
    val cantidad: Int
)

/** Cuerpo enviado a PUT /api/Carritos/item/{detalleCarritoId} */
data class ActualizarItemRequest(
    val cantidad: Int
)
