package sv.org.arrupe.tiendacamisetas.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import sv.org.arrupe.tiendacamisetas.data.model.LoginRequest
import sv.org.arrupe.tiendacamisetas.data.model.RegistroRequest
import sv.org.arrupe.tiendacamisetas.data.repository.ApiResult
import sv.org.arrupe.tiendacamisetas.data.repository.AuthRepository

sealed class AuthUiState {
    data object Inactivo : AuthUiState()
    data object Cargando : AuthUiState()
    data object Exito : AuthUiState()
    data class Error(val mensaje: String) : AuthUiState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository(application)

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Inactivo)
    val uiState: StateFlow<AuthUiState> = _uiState

    val sesionActiva: Boolean get() = repository.sesionActiva()
    val clienteId: Int get() = repository.clienteIdActual()
    val nombreCliente: String? get() = repository.nombreClienteActual()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Ingresa tu correo y contraseña.")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Cargando
            // ✅ Cambiado 'email' a 'correo' para coincidir con LoginRequest
            when (val resultado = repository.login(LoginRequest(correo = email.trim(), password = password))) {
                is ApiResult.Exito -> _uiState.value = AuthUiState.Exito
                is ApiResult.Error -> _uiState.value = AuthUiState.Error(resultado.mensaje)
            }
        }
    }

    fun registrar(
        nombre: String,
        apellido: String,
        email: String,
        telefono: String,
        direccion: String,
        password: String
    ) {
        if (nombre.isBlank() || apellido.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Completa los campos obligatorios: nombre, apellido, correo y contraseña.")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Cargando

            // ✅ Concatenamos nombre + apellido y mapeamos los campos a la API de .NET
            val nombreCompleto = "${nombre.trim()} ${apellido.trim()}".trim()
            val request = RegistroRequest(
                nombre = nombreCompleto,
                correo = email.trim(),
                telefono = telefono.trim(),
                password = password
            )

            when (val resultado = repository.registrar(request)) {
                is ApiResult.Exito -> {
                    // Tras registrarse, iniciamos sesión automáticamente.
                    login(email, password)
                }
                is ApiResult.Error -> _uiState.value = AuthUiState.Error(resultado.mensaje)
            }
        }
    }

    fun cerrarSesion() {
        repository.cerrarSesion()
        _uiState.value = AuthUiState.Inactivo
    }

    fun resetEstado() {
        _uiState.value = AuthUiState.Inactivo
    }
}