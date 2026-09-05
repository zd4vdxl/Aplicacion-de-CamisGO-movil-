package sv.org.arrupe.tiendacamisetas.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import sv.org.arrupe.tiendacamisetas.ui.viewmodel.CheckoutUiState
import sv.org.arrupe.tiendacamisetas.ui.viewmodel.VentaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    onVolver: () -> Unit,
    onCompraConfirmada: (Int) -> Unit,
    ventaViewModel: VentaViewModel = viewModel()
) {
    var ultimos4 by remember { mutableStateOf("") }
    val uiState by ventaViewModel.checkoutState.collectAsState()

    LaunchedEffect(uiState) {
        val estado = uiState
        if (estado is CheckoutUiState.Confirmado) {
            onCompraConfirmada(estado.pedidoId)
            ventaViewModel.resetCheckout()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pago") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 24.dp),
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth().height(180.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Icon(Icons.Default.CreditCard, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "•••• •••• •••• ${ultimos4.ifBlank { "____" }}",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Simulación de pago", color = Color.White.copy(alpha = 0.85f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                "Ingresa los últimos 4 dígitos de tu tarjeta",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = ultimos4,
                onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) ultimos4 = it },
                label = { Text("Últimos 4 dígitos") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 10.dp)
            ) {
                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "Esta operación se procesa de forma segura en el servidor (sp_ConfirmarCompra).",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (uiState is CheckoutUiState.Error) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    (uiState as CheckoutUiState.Error).mensaje,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = { ventaViewModel.confirmarCompra(ultimos4) },
                enabled = uiState !is CheckoutUiState.Procesando,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (uiState is CheckoutUiState.Procesando) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Confirmar compra", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}
