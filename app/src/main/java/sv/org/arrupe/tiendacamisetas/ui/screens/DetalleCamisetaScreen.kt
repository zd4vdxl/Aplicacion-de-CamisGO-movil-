package sv.org.arrupe.tiendacamisetas.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import sv.org.arrupe.tiendacamisetas.data.model.BodegaItem
import sv.org.arrupe.tiendacamisetas.data.model.Servicio
import sv.org.arrupe.tiendacamisetas.data.repository.AuthRepository
import sv.org.arrupe.tiendacamisetas.ui.components.CargandoPantallaCompleta
import sv.org.arrupe.tiendacamisetas.ui.components.ErrorConReintento
import sv.org.arrupe.tiendacamisetas.ui.components.formatearPrecio
import sv.org.arrupe.tiendacamisetas.ui.viewmodel.CarritoViewModel
import sv.org.arrupe.tiendacamisetas.ui.viewmodel.CatalogoViewModel
import sv.org.arrupe.tiendacamisetas.ui.viewmodel.DetalleUiState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DetalleCamisetaScreen(
    camisetaId: Int,
    onVolver: () -> Unit,
    onIrACarrito: () -> Unit,
    onIrALogin: () -> Unit,
    catalogoViewModel: CatalogoViewModel = viewModel(),
    carritoViewModel: CarritoViewModel = viewModel()
) {
    val context = LocalContext.current
    val authRepository = remember { AuthRepository(context) }

    // Se fuerza la ejecución incondicional del detalle al abrir la pantalla
    LaunchedEffect(key1 = Unit) {
        catalogoViewModel.cargarDetalle(camisetaId)
    }

    val uiState by catalogoViewModel.detalleState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var tallaSeleccionada by remember { mutableStateOf<BodegaItem?>(null) }
    var servicioSeleccionado by remember { mutableStateOf<Servicio?>(null) }
    var cantidad by remember { mutableStateOf(1) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Detalle") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        when (val estado = uiState) {
            is DetalleUiState.Cargando -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CargandoPantallaCompleta()
                }
            }
            is DetalleUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorConReintento(estado.mensaje) {
                        catalogoViewModel.cargarDetalle(camisetaId)
                    }
                }
            }
            is DetalleUiState.Exito -> {
                val camiseta = estado.camiseta
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        if (!camiseta.imagenUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = camiseta.imagenUrl,
                                contentDescription = camiseta.equipo,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(72.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = camiseta.equipo ?: camiseta.nombre.ifEmpty { "Detalle de Camiseta" },
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${camiseta.tipoCamiseta ?: "Oficial"} · Temporada ${camiseta.temporada ?: "Actual"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            formatearPrecio(camiseta.precioBase),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                        Text("Selecciona tu talla", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))

                        if (estado.tallasDisponibles.isEmpty()) {
                            Text(
                                "No hay tallas registradas en bodega para esta camiseta.",
                                color = MaterialTheme.colorScheme.error
                            )
                        } else {
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                estado.tallasDisponibles.forEach { item ->
                                    val agotado = item.stock <= 0
                                    FilterChip(
                                        selected = tallaSeleccionada?.bodegaId == item.bodegaId,
                                        onClick = { if (!agotado) tallaSeleccionada = item },
                                        enabled = !agotado,
                                        label = {
                                            Text(if (agotado) "${item.talla} (agotado)" else "${item.talla} · ${item.stock} disp.")
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        Text("Personalización (opcional)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))

                        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = servicioSeleccionado == null,
                                onClick = { servicioSeleccionado = null },
                                label = { Text("Sin personalización") }
                            )
                            estado.servicios.forEach { servicio ->
                                FilterChip(
                                    selected = servicioSeleccionado?.servicioId == servicio.servicioId,
                                    onClick = { servicioSeleccionado = servicio },
                                    label = {
                                        Text("${servicio.nombreServicio} (+${formatearPrecio(servicio.precioAdicional)})")
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        Text("Cantidad", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedIconButton(onClick = { if (cantidad > 1) cantidad-- }) {
                                Icon(Icons.Default.Remove, contentDescription = "Disminuir")
                            }
                            Text(
                                text = "$cantidad",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                            OutlinedIconButton(onClick = {
                                val maxStock = tallaSeleccionada?.stock ?: Int.MAX_VALUE
                                if (cantidad < maxStock) cantidad++
                            }) {
                                Icon(Icons.Default.Add, contentDescription = "Aumentar")
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        val precioTotalEstimado = (camiseta.precioBase + (servicioSeleccionado?.precioAdicional ?: 0.0)) * cantidad

                        Button(
                            onClick = {
                                val talla = tallaSeleccionada
                                if (talla == null) {
                                    scope.launch { snackbarHostState.showSnackbar("Selecciona una talla antes de continuar.") }
                                } else if (!authRepository.sesionActiva()) {
                                    onIrALogin()
                                } else {
                                    carritoViewModel.agregarAlCarrito(
                                        bodegaId = talla.bodegaId,
                                        servicioId = servicioSeleccionado?.servicioId,
                                        cantidad = cantidad
                                    )
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Agregado al carrito")
                                        onIrACarrito()
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Agregar al carrito · ${formatearPrecio(precioTotalEstimado)}", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        }
    }
}