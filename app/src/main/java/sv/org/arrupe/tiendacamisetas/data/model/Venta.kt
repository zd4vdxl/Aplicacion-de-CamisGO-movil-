package sv.org.arrupe.tiendacamisetas.data.model

/** Representa un registro resumido de la tabla Pedidos. */
data class Pedido(
    val pedidoId: Int,
    val fechaPedido: String? = null,
    val total: Double = 0.0,
    val estado: String? = null
)

/** Línea individual dentro del detalle de un pedido (DetallesPedido). */
data class DetalleVentaLinea(
    val detalleId: Int? = null,
    val equipo: String? = null,
    val talla: String? = null,
    val cantidad: Int = 0,
    val precioUnitario: Double = 0.0,
    val subtotal: Double = 0.0
)

/** Respuesta de GET /api/Ventas/{id}: pedido + líneas + info de pago. */
data class PedidoDetalle(
    val pedidoId: Int,
    val fechaPedido: String? = null,
    val total: Double = 0.0,
    val estado: String? = null,
    val lineas: List<DetalleVentaLinea> = emptyList(),
    val estadoPago: String? = null,
    val metodoPago: String? = null
)

/** Cuerpo enviado a POST /api/Ventas/confirmar-compra (ejecuta sp_ConfirmarCompra). */
data class ConfirmarCompraRequest(
    val clienteId: Int,
    val ultimos4Digitos: String,
    val tokenTransaccion: String
)

/** Respuesta de POST /api/Ventas/confirmar-compra. */
data class ConfirmarCompraResponse(
    val pedidoId: Int,
    val mensaje: String
)
