package sv.org.arrupe.tiendacamisetas.data.session

import android.content.Context
import android.content.SharedPreferences

/**
 * Guarda el token JWT y los datos básicos del cliente autenticado
 * usando SharedPreferences, para que la sesión persista entre aperturas de la app.
 */
class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences("tienda_camisetas_session", Context.MODE_PRIVATE)

    var token: String?
        get() = prefs.getString(KEY_TOKEN, null)
        set(value) = prefs.edit().putString(KEY_TOKEN, value).apply()

    var clienteId: Int
        get() = prefs.getInt(KEY_CLIENTE_ID, -1)
        set(value) = prefs.edit().putInt(KEY_CLIENTE_ID, value).apply()

    var nombreCliente: String?
        get() = prefs.getString(KEY_NOMBRE, null)
        set(value) = prefs.edit().putString(KEY_NOMBRE, value).apply()

    val estaAutenticado: Boolean
        get() = !token.isNullOrBlank() && clienteId > 0

    fun guardarSesion(token: String, clienteId: Int, nombre: String?) {
        this.token = token
        this.clienteId = clienteId
        this.nombreCliente = nombre
    }

    fun cerrarSesion() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val KEY_TOKEN = "token"
        private const val KEY_CLIENTE_ID = "clienteId"
        private const val KEY_NOMBRE = "nombre"
    }
}
