package sv.org.arrupe.tiendacamisetas.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import sv.org.arrupe.tiendacamisetas.data.model.CarritoItem
import sv.org.arrupe.tiendacamisetas.ui.components.CargandoPantallaCompleta
import sv.org.arrupe.tiendacamisetas.ui.components.formatearPrecio
import sv.org.arrupe.tiendacamisetas.ui.viewmodel.CarritoEvento
import sv.org.arrupe.tiendacamisetas.ui.viewmodel.CarritoUiState
import sv.org.arrupe.tiendacamisetas.ui.viewmodel.CarritoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(
    onVolver: () -> Unit,
    onIrAPagar: () -> Unit,
    carritoViewModel: CarritoViewModel = viewModel()
) {
    LaunchedEffect(Unit) { carritoViewModel.cargarCarrito() }

    val uiState by carritoViewModel.uiState.collectAsState()
    val evento by carritoViewModel.evento.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(evento) {
        val actual = evento
        if (actual is CarritoEvento.Mensaje) {
            snackbarHostState.showSnackbar(actual.texto)
            carritoViewModel.limpiarEvento()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Mi carrito") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val estado = uiState) {
                is CarritoUiState.Cargando -> CargandoPantallaCompleta()
                is CarritoUiState.Error -> Box(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(estado.mensaje, color = MaterialTheme.colorScheme.error)
                }
                is CarritoUiState.Vacio -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Tu carrito está vacío", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Agrega camisetas desde el catálogo",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                is CarritoUiState.Exito -> {
                    val items = estado.carrito.items
                    val total = items.sumOf { it.subtotalItem }

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(items, key = { it.detalleCarritoId }) { item ->
                            CarritoItemCard(
                                item = item,
                                onAumentar = { carritoViewModel.actualizarCantidad(item.detalleCarritoId, item.cantidad + 1) },
                                onDisminuir = { carritoViewModel.actualizarCantidad(item.detalleCarritoId, item.cantidad - 1) },
                                onEliminar = { carritoViewModel.eliminarItem(item.detalleCarritoId) }
                            )
                        }
                    }

                    Surface(shadowElevation = 8.dp) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                Text(
                                    formatearPrecio(total),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = onIrAPagar,
                                modifier = Modifier.fillMaxWidth().height(52.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.ShoppingCartCheckout, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Proceder al pago", style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CarritoItemCard(
    item: CarritoItem,
    onAumentar: () -> Unit,
    onDisminuir: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.equipo ?: "Camiseta", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(
                        "${item.tipoCamiseta ?: ""} · Talla ${item.talla ?: "-"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (!item.servicioPersonalizacion.isNullOrBlank() && item.servicioPersonalizacion != "Sin Personalización") {
                        Text(
                            "Personalización: ${item.servicioPersonalizacion} (${item.calidadPersonalizacion})",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                IconButton(onClick = onEliminar) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedIconButton(onClick = onDisminuir, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Remove, contentDescription = "Disminuir", modifier = Modifier.size(16.dp))
                    }
                    Text("${item.cantidad}", modifier = Modifier.padding(horizontal = 14.dp))
                    OutlinedIconButton(onClick = onAumentar, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Add, contentDescription = "Aumentar", modifier = Modifier.size(16.dp))
                    }
                }
                Text(
                    formatearPrecio(item.subtotalItem),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
