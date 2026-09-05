package sv.org.arrupe.tiendacamisetas.data.repository

import android.content.Context
import sv.org.arrupe.tiendacamisetas.data.model.ConfirmarCompraRequest
import sv.org.arrupe.tiendacamisetas.data.remote.RetrofitServiceFactory


class VentaRepository(context: Context) {
    private val api = RetrofitServiceFactory.makeRetrofitService(context)
    suspend fun confirmarCompra(request: ConfirmarCompraRequest) = ejecutarLlamada { api.confirmarCompra(request) }

    suspend fun getVentasCliente(clienteId: Int) = ejecutarLlamada { api.getVentasCliente(clienteId) }

    suspend fun getVentaDetalle(id: Int) = ejecutarLlamada { api.getVentaDetalle(id) }
}
