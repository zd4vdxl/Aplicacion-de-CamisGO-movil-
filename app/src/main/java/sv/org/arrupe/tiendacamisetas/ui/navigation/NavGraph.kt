package sv.org.arrupe.tiendacamisetas.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import sv.org.arrupe.tiendacamisetas.ui.screens.CarritoScreen
import sv.org.arrupe.tiendacamisetas.ui.screens.CatalogoScreen
import sv.org.arrupe.tiendacamisetas.ui.screens.CheckoutScreen
import sv.org.arrupe.tiendacamisetas.ui.screens.ConfirmacionScreen
import sv.org.arrupe.tiendacamisetas.ui.screens.DetalleCamisetaScreen
import sv.org.arrupe.tiendacamisetas.ui.screens.HistorialPedidosScreen
import sv.org.arrupe.tiendacamisetas.ui.screens.LoginScreen
import sv.org.arrupe.tiendacamisetas.ui.screens.RegistroScreen

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Catalogo.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // --- LOGIN ---
        composable(route = Screen.Login.route) {
            LoginScreen(
                onLoginExitoso = {
                    navController.popBackStack()
                },
                onIrARegistro = {
                    navController.navigate(Screen.Registro.route)
                }
            )
        }

        // --- REGISTRO ---
        composable(route = Screen.Registro.route) {
            RegistroScreen(
                onRegistroExitoso = {
                    navController.popBackStack()
                },
                onVolver = {
                    navController.popBackStack()
                }
            )
        }

        // --- CATÁLOGO ---
        composable(route = Screen.Catalogo.route) {
            CatalogoScreen(
                onCamisetaSeleccionada = { camisetaId ->
                    navController.navigate(Screen.Detalle.crearRuta(camisetaId))
                },
                onIrACarrito = {
                    navController.navigate(Screen.Carrito.route)
                },
                onIrAHistorial = {
                    navController.navigate(Screen.Historial.route)
                },
                onCerrarSesion = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Catalogo.route) { inclusive = true }
                    }
                }
            )
        }

        // --- DETALLE DE CAMISETA ---
        composable(
            route = Screen.Detalle.route,
            arguments = listOf(
                navArgument("camisetaId") {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) { backStackEntry ->
            val camisetaId = backStackEntry.arguments?.getInt("camisetaId") ?: 0

            DetalleCamisetaScreen(
                camisetaId = camisetaId,
                onVolver = {
                    navController.popBackStack()
                },
                onIrACarrito = {
                    navController.navigate(Screen.Carrito.route)
                },
                onIrALogin = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }

        // --- CARRITO DE COMPRAS ---
        composable(route = Screen.Carrito.route) {
            CarritoScreen(
                onVolver = {
                    navController.popBackStack()
                },
                onIrAPagar = {
                    navController.navigate(Screen.Checkout.route)
                }
            )
        }

        // --- CHECKOUT ---
        composable(route = Screen.Checkout.route) {
            CheckoutScreen(
                onVolver = {
                    navController.popBackStack()
                },
                onCompraConfirmada = { pedidoId: Int ->
                    navController.navigate(Screen.Confirmacion.crearRuta(pedidoId)) {
                        popUpTo(Screen.Catalogo.route) { inclusive = false }
                    }
                }
            )
        }

        // --- CONFIRMACIÓN ---
        composable(
            route = Screen.Confirmacion.route,
            arguments = listOf(
                navArgument("pedidoId") {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) { backStackEntry ->
            val pedidoId = backStackEntry.arguments?.getInt("pedidoId") ?: 0

            ConfirmacionScreen(
                pedidoId = pedidoId,
                onSeguirComprando = {
                    navController.navigate(Screen.Catalogo.route) {
                        popUpTo(Screen.Catalogo.route) { inclusive = true }
                    }
                },
                onVerPedidos = {
                    navController.navigate(Screen.Historial.route) {
                        popUpTo(Screen.Catalogo.route) { inclusive = false }
                    }
                }
            )
        }

        // --- HISTORIAL DE PEDIDOS ---
        composable(route = Screen.Historial.route) {
            HistorialPedidosScreen(
                onVolver = {
                    navController.popBackStack()
                }
            )
        }
    }
}