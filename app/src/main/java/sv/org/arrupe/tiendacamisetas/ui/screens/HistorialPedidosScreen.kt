package sv.org.arrupe.tiendacamisetas.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import sv.org.arrupe.tiendacamisetas.data.model.Pedido
import sv.org.arrupe.tiendacamisetas.ui.components.CargandoPantallaCompleta
import sv.org.arrupe.tiendacamisetas.ui.components.formatearPrecio
import sv.org.arrupe.tiendacamisetas.ui.theme.AmarilloStockBajo
import sv.org.arrupe.tiendacamisetas.ui.theme.VerdeExito
import sv.org.arrupe.tiendacamisetas.ui.viewmodel.HistorialUiState
import sv.org.arrupe.tiendacamisetas.ui.viewmodel.VentaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialPedidosScreen(
    onVolver: () -> Unit,
    ventaViewModel: VentaViewModel = viewModel()
) {
    LaunchedEffect(Unit) { ventaViewModel.cargarHistorial() }
    val uiState by ventaViewModel.historialState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis pedidos") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val estado = uiState) {
                is HistorialUiState.Cargando -> CargandoPantallaCompleta()
                is HistorialUiState.Error -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(estado.mensaje, color = MaterialTheme.colorScheme.error)
                }
                is HistorialUiState.Vacio -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Aún no tienes pedidos realizados.")
                }
                is HistorialUiState.Exito -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(estado.pedidos, key = { it.pedidoId }) { pedido ->
                            PedidoCard(pedido)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PedidoCard(pedido: Pedido) {
    Card(shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Pedido #${pedido.pedidoId}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                EstadoBadge(pedido.estado ?: "Desconocido")
            }
            Spacer(modifier = Modifier.height(6.dp))
            pedido.fechaPedido?.let {
                Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                formatearPrecio(pedido.total),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
private fun EstadoBadge(estado: String) {
    val color = when (estado.lowercase()) {
        "pagado", "aprobado", "completado" -> VerdeExito
        "pendiente" -> AmarilloStockBajo
        else -> MaterialTheme.colorScheme.error
    }
    Surface(color = color.copy(alpha = 0.15f), shape = RoundedCornerShape(50)) {
        Text(
            estado,
            color = color,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}
