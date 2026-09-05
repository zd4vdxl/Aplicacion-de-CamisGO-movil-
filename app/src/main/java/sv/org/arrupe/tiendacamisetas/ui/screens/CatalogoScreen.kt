package sv.org.arrupe.tiendacamisetas.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import sv.org.arrupe.tiendacamisetas.ui.components.CamisetaCard
import sv.org.arrupe.tiendacamisetas.ui.components.CargandoPantallaCompleta
import sv.org.arrupe.tiendacamisetas.ui.components.ErrorConReintento
import sv.org.arrupe.tiendacamisetas.ui.viewmodel.AuthViewModel
import sv.org.arrupe.tiendacamisetas.ui.viewmodel.CatalogoUiState
import sv.org.arrupe.tiendacamisetas.ui.viewmodel.CatalogoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoScreen(
    onCamisetaSeleccionada: (Int) -> Unit,
    onIrACarrito: () -> Unit,
    onIrAHistorial: () -> Unit,
    onCerrarSesion: () -> Unit,
    catalogoViewModel: CatalogoViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val uiState by catalogoViewModel.catalogoState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Camisetas disponibles") },
                actions = {
                    IconButton(onClick = onIrAHistorial) {
                        Icon(Icons.Default.Receipt, contentDescription = "Mis pedidos")
                    }
                    IconButton(onClick = onIrACarrito) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito")
                    }
                    IconButton(onClick = {
                        authViewModel.cerrarSesion()
                        onCerrarSesion()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar sesión")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (val estado = uiState) {
                is CatalogoUiState.Cargando -> CargandoPantallaCompleta()
                is CatalogoUiState.Error -> ErrorConReintento(estado.mensaje) { catalogoViewModel.cargarCatalogo() }
                is CatalogoUiState.Exito -> {
                    if (estado.camisetas.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Aún no hay camisetas disponibles.")
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            itemsIndexed(
                                items = estado.camisetas,
                                key = { index, camiseta -> "${camiseta.camisetaId}_$index" }
                            ) { _, camiseta ->
                                CamisetaCard(
                                    camiseta = camiseta,
                                    onClick = { onCamisetaSeleccionada(camiseta.camisetaId) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}