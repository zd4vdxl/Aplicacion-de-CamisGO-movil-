package sv.org.arrupe.tiendacamisetas.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import sv.org.arrupe.tiendacamisetas.data.model.AgregarItemRequest
import sv.org.arrupe.tiendacamisetas.data.model.CarritoResponse
import sv.org.arrupe.tiendacamisetas.data.repository.ApiResult
import sv.org.arrupe.tiendacamisetas.data.repository.AuthRepository
import sv.org.arrupe.tiendacamisetas.data.repository.CarritoRepository

sealed class CarritoUiState {
    data object Cargando : CarritoUiState()
    data class Exito(val carrito: CarritoResponse) : CarritoUiState()
    data object Vacio : CarritoUiState()
    data class Error(val mensaje: String) : CarritoUiState()
}

/** Evento puntual para mostrar mensajes (snackbars) sin repetirlos al recomponer. */
sealed class CarritoEvento {
    data class Mensaje(val texto: String) : CarritoEvento()
}

class CarritoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CarritoRepository(application)
    private val authRepository = AuthRepository(application)

    private val _uiState = MutableStateFlow<CarritoUiState>(CarritoUiState.Cargando)
    val uiState: StateFlow<CarritoUiState> = _uiState

    private val _evento = MutableStateFlow<CarritoEvento?>(null)
    val evento: StateFlow<CarritoEvento?> = _evento

    private val clienteId: Int get() = authRepository.clienteIdActual()

    init {
        // Carga los datos del carrito automáticamente al inicializar el ViewModel
        cargarCarrito()
    }

    fun cargarCarrito() {
        viewModelScope.launch {
            _uiState.value = CarritoUiState.Cargando
            when (val resultado = repository.getCarrito(clienteId)) {
                is ApiResult.Exito -> {
                    if (resultado.datos.items.isEmpty()) {
                        _uiState.value = CarritoUiState.Vacio
                    } else {
                        _uiState.value = CarritoUiState.Exito(resultado.datos)
                    }
                }
                is ApiResult.Error -> _uiState.value = CarritoUiState.Error(resultado.mensaje)
            }
        }
    }

    fun agregarAlCarrito(bodegaId: Int, servicioId: Int?, cantidad: Int) {
        viewModelScope.launch {
            val request = AgregarItemRequest(
                clienteId = clienteId,
                bodegaId = bodegaId,
                servicioId = servicioId,
                cantidad = cantidad
            )
            when (val resultado = repository.agregarItem(request)) {
                is ApiResult.Exito -> {
                    _evento.value = CarritoEvento.Mensaje("Producto agregado al carrito 🛒")
                    cargarCarrito() // Actualiza la lista para reflejar los cambios en pantalla
                }
                is ApiResult.Error -> _evento.value = CarritoEvento.Mensaje(resultado.mensaje)
            }
        }
    }

    fun actualizarCantidad(detalleCarritoId: Int, nuevaCantidad: Int) {
        if (nuevaCantidad <= 0) {
            eliminarItem(detalleCarritoId)
            return
        }
        viewModelScope.launch {
            when (val resultado = repository.actualizarCantidad(detalleCarritoId, nuevaCantidad)) {
                is ApiResult.Exito -> cargarCarrito()
                is ApiResult.Error -> _evento.value = CarritoEvento.Mensaje(resultado.mensaje)
            }
        }
    }

    fun eliminarItem(detalleCarritoId: Int) {
        viewModelScope.launch {
            when (val resultado = repository.eliminarItem(detalleCarritoId)) {
                is ApiResult.Exito -> cargarCarrito()
                is ApiResult.Error -> _evento.value = CarritoEvento.Mensaje(resultado.mensaje)
            }
        }
    }

    fun vaciarCarrito() {
        viewModelScope.launch {
            repository.vaciarCarrito(clienteId)
            cargarCarrito()
        }
    }

    fun limpiarEvento() {
        _evento.value = null
    }
}