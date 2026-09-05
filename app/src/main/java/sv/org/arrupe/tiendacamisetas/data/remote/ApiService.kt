package sv.org.arrupe.tiendacamisetas.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import sv.org.arrupe.tiendacamisetas.data.model.*


interface ApiService {

    // ---------- Autenticación ----------
    @POST("api/Auth/registro")
    suspend fun registro(@Body request: RegistroRequest): Response<Cliente>

    @POST("api/Auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    // ---------- Catálogo de camisetas ----------
    @GET("api/Camisetas")
    suspend fun getCamisetas(): Response<List<Camiseta>>

    @GET("api/Camisetas/{id}")
    suspend fun getCamisetaDetalle(@Path("id") id: Int): Response<Camiseta>

    // ---------- Inventario / bodega (tallas y stock) ----------
    @GET("api/Bodega")
    suspend fun getBodega(): Response<List<BodegaItem>>

    // ---------- Servicios de personalización ----------
    @GET("api/Servicios")
    suspend fun getServicios(): Response<List<Servicio>>

    // ---------- Carrito de compras (requiere JWT) ----------
    @GET("api/Carritos/cliente/{clienteId}")
    suspend fun getCarrito(@Path("clienteId") clienteId: Int): Response<CarritoResponse>

    @POST("api/Carritos/item")
    suspend fun agregarItemCarrito(@Body request: AgregarItemRequest): Response<CarritoItem>

    @PUT("api/Carritos/item/{detalleCarritoId}")
    suspend fun actualizarItemCarrito(
        @Path("detalleCarritoId") detalleCarritoId: Int,
        @Body request: ActualizarItemRequest
    ): Response<CarritoItem>

    @DELETE("api/Carritos/item/{detalleCarritoId}")
    suspend fun eliminarItemCarrito(@Path("detalleCarritoId") detalleCarritoId: Int): Response<Unit>

    @DELETE("api/Carritos/vaciar/{clienteId}")
    suspend fun vaciarCarrito(@Path("clienteId") clienteId: Int): Response<Unit>

    // ---------- Ventas / pedidos (requiere JWT) ----------
    @GET("api/Ventas/cliente/{clienteId}")
    suspend fun getVentasCliente(@Path("clienteId") clienteId: Int): Response<List<Pedido>>

    @GET("api/Ventas/{id}")
    suspend fun getVentaDetalle(@Path("id") id: Int): Response<PedidoDetalle>

    @POST("api/Ventas/confirmar-compra")
    suspend fun confirmarCompra(@Body request: ConfirmarCompraRequest): Response<ConfirmarCompraResponse>

}

