package sv.org.arrupe.tiendacamisetas.data.repository

import android.content.Context
import retrofit2.Response
import sv.org.arrupe.tiendacamisetas.data.model.LoginRequest
import sv.org.arrupe.tiendacamisetas.data.model.LoginResponse
import sv.org.arrupe.tiendacamisetas.data.model.RegistroRequest
import sv.org.arrupe.tiendacamisetas.data.remote.RetrofitServiceFactory
import sv.org.arrupe.tiendacamisetas.data.session.SessionManager

class AuthRepository(private val context: Context) {

    private val api = RetrofitServiceFactory.makeRetrofitService(context)
    private val sessionManager = SessionManager(context)

    suspend fun registrar(request: RegistroRequest) = ejecutarLlamada { api.registro(request) }

    suspend fun login(request: LoginRequest): ApiResult<LoginResponse> {
        val resultado = ejecutarLlamada { api.login(request) }
        if (resultado is ApiResult.Exito) {
            // ✅ Accedemos a clienteId y nombre a través del objeto anidado 'cliente'
            sessionManager.guardarSesion(
                token = resultado.datos.token,
                clienteId = resultado.datos.cliente?.clienteId ?: 0,
                nombre = resultado.datos.cliente?.nombre
            )
        }
        return resultado
    }

    fun cerrarSesion() = sessionManager.cerrarSesion()

    fun sesionActiva(): Boolean = sessionManager.estaAutenticado

    fun clienteIdActual(): Int = sessionManager.clienteId

    fun nombreClienteActual(): String? = sessionManager.nombreCliente

    // Función genérica para ejecutar llamadas de Retrofit de forma segura
    private inline fun <T> ejecutarLlamada(bloque: () -> Response<T>): ApiResult<T> {
        return try {
            val respuesta = bloque()
            if (respuesta.isSuccessful) {
                val cuerpo = respuesta.body()
                if (cuerpo != null) {
                    ApiResult.Exito(cuerpo)
                } else {
                    ApiResult.Error("La respuesta del servidor está vacía")
                }
            } else {
                ApiResult.Error("Error del servidor: ${respuesta.message()}")
            }
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage ?: "Error de conexión con el servidor")
        }
    }
}