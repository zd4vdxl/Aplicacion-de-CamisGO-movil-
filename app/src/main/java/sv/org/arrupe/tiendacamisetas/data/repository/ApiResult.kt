package sv.org.arrupe.tiendacamisetas.data.repository

/** Envoltorio simple para representar éxito/error de una llamada a la API. */
sealed class ApiResult<out T> {
    data class Exito<T>(val datos: T) : ApiResult<T>()
    data class Error(val mensaje: String) : ApiResult<Nothing>()
}

/** Ejecuta una llamada Retrofit y la traduce a ApiResult, capturando errores de red. */
suspend fun <T> ejecutarLlamada(block: suspend () -> retrofit2.Response<T>): ApiResult<T> {
    return try {
        val response = block()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                ApiResult.Exito(body)
            } else {
                @Suppress("UNCHECKED_CAST")
                ApiResult.Exito(Unit as T)
            }
        } else {
            val mensaje = response.errorBody()?.string()?.takeIf { it.isNotBlank() }
                ?: "Error del servidor (código ${response.code()})"
            ApiResult.Error(mensaje)
        }
    } catch (e: java.net.UnknownHostException) {
        ApiResult.Error("No se pudo conectar con el servidor. Verifica tu conexión o la URL de la API.")
    } catch (e: java.net.ConnectException) {
        ApiResult.Error("No se pudo establecer conexión con la API. ¿Está corriendo el backend?")
    } catch (e: Exception) {
        ApiResult.Error(e.localizedMessage ?: "Ocurrió un error inesperado.")
    }
}
