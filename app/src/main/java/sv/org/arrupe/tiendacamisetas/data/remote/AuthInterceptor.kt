package sv.org.arrupe.tiendacamisetas.data.remote

import okhttp3.Interceptor
import okhttp3.Response
import sv.org.arrupe.tiendacamisetas.data.session.SessionManager

class AuthInterceptor(private val sessionManager: SessionManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        try {
            // Leemos el token desde SessionManager
            val token = sessionManager.token

            // Solo adjuntamos el header Authorization si el token existe y no está vacío
            if (!token.isNullOrBlank()) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Importante: También forzamos a cerrar conexiones pendientes de desarrollo
        requestBuilder.addHeader("Connection", "close")

        return chain.proceed(requestBuilder.build())
    }
}