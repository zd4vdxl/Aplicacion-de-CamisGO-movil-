package sv.org.arrupe.tiendacamisetas.ui.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Registro : Screen("registro")
    data object Catalogo : Screen("catalogo")
    data object Detalle : Screen("detalle/{camisetaId}") {
        fun crearRuta(camisetaId: Int) = "detalle/$camisetaId"
    }
    data object Carrito : Screen("carrito")
    data object Checkout : Screen("checkout")
    data object Confirmacion : Screen("confirmacion/{pedidoId}") {
        fun crearRuta(pedidoId: Int) = "confirmacion/$pedidoId"
    }
    data object Historial : Screen("historial")
}
