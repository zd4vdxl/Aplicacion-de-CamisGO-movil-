package sv.org.arrupe.tiendacamisetas.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import sv.org.arrupe.tiendacamisetas.data.model.Camiseta
import sv.org.arrupe.tiendacamisetas.data.repository.ApiResult
import sv.org.arrupe.tiendacamisetas.data.repository.CamisetaRepository

class CamisetaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CamisetaRepository(application)

    private val _camisetasList = MutableStateFlow<List<Camiseta>>(emptyList())
    val camisetasList: StateFlow<List<Camiseta>> = _camisetasList.asStateFlow()

    private val _selectedCamiseta = MutableStateFlow<Camiseta?>(null)
    val selectedCamiseta: StateFlow<Camiseta?> = _selectedCamiseta.asStateFlow()

    init {
        cargarCamisetas()
    }

    fun cargarCamisetas() {
        viewModelScope.launch {
            when (val resultado = repository.getCamisetas()) {
                is ApiResult.Exito -> {
                    _camisetasList.value = resultado.datos
                }
                is ApiResult.Error -> {
                    // Si ocurre un error, puedes manejarlo o dejar la lista vacía
                    _camisetasList.value = emptyList()
                }
            }
        }
    }

    fun selectCamiseta(camiseta: Camiseta) {
        _selectedCamiseta.value = camiseta
    }

    fun clearSelection() {
        _selectedCamiseta.value = null
    }
}