package sv.org.arrupe.tiendacamisetas.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import sv.org.arrupe.tiendacamisetas.ui.viewmodel.AuthUiState
import sv.org.arrupe.tiendacamisetas.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    onLoginExitoso: () -> Unit,
    onIrARegistro: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var mostrarPassword by remember { mutableStateOf(false) }

    val uiState by authViewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Exito) {
            onLoginExitoso()
            authViewModel.resetEstado()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Checkroom,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Tienda de Camisetas",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            text = "Inicia sesión para continuar",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = if (mostrarPassword) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val icono: ImageVector = if (mostrarPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility
                IconButton(onClick = { mostrarPassword = !mostrarPassword }) {
                    Icon(icono, contentDescription = null)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        if (uiState is AuthUiState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = (uiState as AuthUiState.Error).mensaje,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { authViewModel.login(email, password) },
            enabled = uiState !is AuthUiState.Cargando,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            if (uiState is AuthUiState.Cargando) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text("Iniciar sesión", style = MaterialTheme.typography.titleMedium)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onIrARegistro,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("¿No tienes cuenta? Regístrate aquí")
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
