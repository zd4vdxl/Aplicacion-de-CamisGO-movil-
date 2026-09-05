package sv.org.arrupe.tiendacamisetas.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import sv.org.arrupe.tiendacamisetas.data.model.Camiseta
import sv.org.arrupe.tiendacamisetas.ui.viewmodel.CamisetaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CamisetasApp(viewModel: CamisetaViewModel) {
    val camisetasList by viewModel.camisetasList.collectAsState()
    val selectedCamiseta by viewModel.selectedCamiseta.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tienda Camisetas - Catálogo") }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (camisetasList.isNotEmpty()) {
                CamisetasList(
                    camisetasList = camisetasList,
                    onItemClick = { camiseta -> viewModel.selectCamiseta(camiseta) }
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            selectedCamiseta?.let { camiseta ->
                CamisetaDetailDialog(
                    camiseta = camiseta,
                    onDismiss = { viewModel.clearSelection() }
                )
            }
        }
    }
}

@Composable
fun CamisetasList(camisetasList: List<Camiseta>, onItemClick: (Camiseta) -> Unit) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(camisetasList) { camiseta ->
            CamisetaItem(camiseta = camiseta, onClick = { onItemClick(camiseta) })
        }
    }
}

@Composable
fun CamisetaItem(camiseta: Camiseta, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(model = camiseta.imagenUrl),
                contentDescription = camiseta.equipo ?: camiseta.nombre,
                modifier = Modifier.size(64.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = camiseta.equipo ?: camiseta.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$${String.format("%.2f", camiseta.precioBase)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun CamisetaDetailDialog(camiseta: Camiseta, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = camiseta.imagenUrl),
                    contentDescription = camiseta.equipo ?: camiseta.nombre,
                    modifier = Modifier
                        .size(180.dp)
                        .padding(8.dp),
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = camiseta.equipo ?: camiseta.nombre,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$${String.format("%.2f", camiseta.precioBase)}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = camiseta.descripcion ?: "Sin descripción disponible.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { onDismiss() }) {
                    Text("Cerrar")
                }
            }
        }
    }
}