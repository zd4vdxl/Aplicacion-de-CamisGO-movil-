package sv.org.arrupe.tiendacamisetas.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import sv.org.arrupe.tiendacamisetas.data.model.BodegaItem
import sv.org.arrupe.tiendacamisetas.data.model.Camiseta
import sv.org.arrupe.tiendacamisetas.data.model.Servicio
import sv.org.arrupe.tiendacamisetas.data.repository.ApiResult
import sv.org.arrupe.tiendacamisetas.data.repository.CamisetaRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

sealed class CatalogoUiState {
    data object Cargando : CatalogoUiState()
    data class Exito(val camisetas: List<Camiseta>) : CatalogoUiState()
    data class Error(val mensaje: String) : CatalogoUiState()
}

sealed class DetalleUiState {
    data object Cargando : DetalleUiState()
    data class Exito(
        val camiseta: Camiseta,
        val tallasDisponibles: List<BodegaItem>,
        val servicios: List<Servicio>
    ) : DetalleUiState()
    data class Error(val mensaje: String) : DetalleUiState()
}

class CatalogoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CamisetaRepository(application)

    private val _catalogoState = MutableStateFlow<CatalogoUiState>(CatalogoUiState.Cargando)
    val catalogoState: StateFlow<CatalogoUiState> = _catalogoState

    private val _detalleState = MutableStateFlow<DetalleUiState>(DetalleUiState.Cargando)
    val detalleState: StateFlow<DetalleUiState> = _detalleState

    init {
        cargarCatalogo()
    }

    fun cargarCatalogo() {
        viewModelScope.launch {
            _catalogoState.value = CatalogoUiState.Cargando
            when (val resultado = repository.getCamisetas()) {
                is ApiResult.Exito -> _catalogoState.value = CatalogoUiState.Exito(resultado.datos)
                is ApiResult.Error -> _catalogoState.value = CatalogoUiState.Error(resultado.mensaje)
            }
        }
    }

    fun cargarDetalle(camisetaId: Int) {
        viewModelScope.launch {
            _detalleState.value = DetalleUiState.Cargando

            // 1. Cargar el detalle principal de la camiseta primero
            val camisetaResultado = repository.getCamisetaDetalle(camisetaId)

            if (camisetaResultado is ApiResult.Error) {
                _detalleState.value = DetalleUiState.Error(camisetaResultado.mensaje)
                return@launch
            }

            val camiseta = (camisetaResultado as ApiResult.Exito).datos

            // 2. Cargar Bodega y Servicios de forma tolerante a fallos
            var tallas: List<BodegaItem> = emptyList()
            var servicios: List<Servicio> = emptyList()

            try {
                coroutineScope {
                    val bodegaDeferred = async { repository.getBodega() }
                    val serviciosDeferred = async { repository.getServicios() }

                    val bodegaRes = runCatching { bodegaDeferred.await() }.getOrNull()
                    val serviciosRes = runCatching { serviciosDeferred.await() }.getOrNull()

                    if (bodegaRes is ApiResult.Exito) {
                        tallas = bodegaRes.datos.filter { it.camisetaId == camisetaId }
                    }

                    if (serviciosRes is ApiResult.Exito) {
                        servicios = serviciosRes.datos
                    }
                }
            } catch (e: Exception) {
                // Si falla la carga de extras, se ignoran sin bloquear la vista de la camiseta
                e.printStackTrace()
            }

            // 3. Emitir el estado final con los datos recuperados
            _detalleState.value = DetalleUiState.Exito(camiseta, tallas, servicios)
        }

    }
}
