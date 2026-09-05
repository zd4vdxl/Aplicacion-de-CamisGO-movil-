package sv.org.arrupe.tiendacamisetas.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import sv.org.arrupe.tiendacamisetas.data.model.ConfirmarCompraRequest
import sv.org.arrupe.tiendacamisetas.data.model.Pedido
import sv.org.arrupe.tiendacamisetas.data.repository.ApiResult
import sv.org.arrupe.tiendacamisetas.data.repository.AuthRepository
import sv.org.arrupe.tiendacamisetas.data.repository.VentaRepository
import java.util.UUID

sealed class CheckoutUiState {
    data object Inactivo : CheckoutUiState()
    data object Procesando : CheckoutUiState()
    data class Confirmado(val pedidoId: Int, val mensaje: String) : CheckoutUiState()
    data class Error(val mensaje: String) : CheckoutUiState()
}

// Declaración que faltaba para corregir los errores de Unresolved reference
sealed class HistorialUiState {
    data object Cargando : HistorialUiState()
    data object Vacio : HistorialUiState()
    data class Exito(val pedidos: List<Pedido>) : HistorialUiState()
    data class Error(val mensaje: String) : HistorialUiState()
}

class VentaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = VentaRepository(application)
    private val authRepository = AuthRepository(application)

    private val _checkoutState = MutableStateFlow<CheckoutUiState>(CheckoutUiState.Inactivo)
    val checkoutState: StateFlow<CheckoutUiState> = _checkoutState

    private val _historialState = MutableStateFlow<HistorialUiState>(HistorialUiState.Cargando)
    val historialState: StateFlow<HistorialUiState> = _historialState

    fun confirmarCompra(ultimos4Digitos: String) {
        if (ultimos4Digitos.length != 4 || !ultimos4Digitos.all { it.isDigit() }) {
            _checkoutState.value = CheckoutUiState.Error("Ingresa los últimos 4 dígitos de tu tarjeta.")
            return
        }
        viewModelScope.launch {
            _checkoutState.value = CheckoutUiState.Procesando
            val request = ConfirmarCompraRequest(
                clienteId = authRepository.clienteIdActual(),
                ultimos4Digitos = ultimos4Digitos,
                tokenTransaccion = UUID.randomUUID().toString()
            )
            when (val resultado = repository.confirmarCompra(request)) {
                is ApiResult.Exito -> _checkoutState.value =
                    CheckoutUiState.Confirmado(resultado.datos.pedidoId, resultado.datos.mensaje)
                is ApiResult.Error -> _checkoutState.value = CheckoutUiState.Error(resultado.mensaje)
            }
        }
    }

    fun resetCheckout() {
        _checkoutState.value = CheckoutUiState.Inactivo
    }

    fun cargarHistorial() {
        viewModelScope.launch {
            _historialState.value = HistorialUiState.Cargando
            when (val resultado = repository.getVentasCliente(authRepository.clienteIdActual())) {
                is ApiResult.Exito -> {
                    _historialState.value = if (resultado.datos.isEmpty()) {
                        HistorialUiState.Vacio
                    } else {
                        HistorialUiState.Exito(resultado.datos.sortedByDescending { it.pedidoId })
                    }
                }
                is ApiResult.Error -> _historialState.value = HistorialUiState.Error(resultado.mensaje)
            }
        }
    }
}