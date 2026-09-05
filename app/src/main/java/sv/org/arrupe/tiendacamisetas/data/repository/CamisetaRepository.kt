package sv.org.arrupe.tiendacamisetas.data.repository

import android.content.Context
import sv.org.arrupe.tiendacamisetas.data.remote.RetrofitServiceFactory


class CamisetaRepository(context: Context) {
    private val api = RetrofitServiceFactory.makeRetrofitService(context)

    suspend fun getCamisetas() = ejecutarLlamada { api.getCamisetas() }

    suspend fun getCamisetaDetalle(id: Int) = ejecutarLlamada { api.getCamisetaDetalle(id) }

    suspend fun getBodega() = ejecutarLlamada { api.getBodega() }

    suspend fun getServicios() = ejecutarLlamada { api.getServicios() }



}


