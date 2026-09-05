package sv.org.arrupe.tiendacamisetas.data.repository

import android.content.Context
import sv.org.arrupe.tiendacamisetas.data.model.ActualizarItemRequest
import sv.org.arrupe.tiendacamisetas.data.model.AgregarItemRequest
import sv.org.arrupe.tiendacamisetas.data.remote.RetrofitServiceFactory

class CarritoRepository(context: Context) {
    private val api = RetrofitServiceFactory.makeRetrofitService(context)

    suspend fun getCarrito(clienteId: Int) = ejecutarLlamada { api.getCarrito(clienteId) }

    suspend fun agregarItem(request: AgregarItemRequest) = ejecutarLlamada { api.agregarItemCarrito(request) }

    suspend fun actualizarCantidad(detalleCarritoId: Int, cantidad: Int) =
        ejecutarLlamada { api.actualizarItemCarrito(detalleCarritoId, ActualizarItemRequest(cantidad)) }

    suspend fun eliminarItem(detalleCarritoId: Int) = ejecutarLlamada { api.eliminarItemCarrito(detalleCarritoId) }

    suspend fun vaciarCarrito(clienteId: Int) = ejecutarLlamada { api.vaciarCarrito(clienteId) }
}