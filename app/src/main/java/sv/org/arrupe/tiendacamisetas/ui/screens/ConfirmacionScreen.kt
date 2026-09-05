package sv.org.arrupe.tiendacamisetas.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import sv.org.arrupe.tiendacamisetas.ui.theme.VerdeExito

@Composable
fun ConfirmacionScreen(
    pedidoId: Int,
    onSeguirComprando: () -> Unit,
    onVerPedidos: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(96.dp),
            tint = VerdeExito
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            "¡Compra confirmada!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Tu pedido #$pedidoId fue procesado y pagado con éxito.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = onVerPedidos,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Ver mis pedidos")
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(
            onClick = onSeguirComprando,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Seguir comprando")
        }
    }
}
