package sv.org.arrupe.tiendacamisetas.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val EsquemaClaro = lightColorScheme(
    primary = AzulPrimario,
    onPrimary = SuperficieClara,
    secondary = NaranjaAcento,
    onSecondary = SuperficieClara,
    background = FondoClaro,
    surface = SuperficieClara,
    onBackground = TextoOscuro,
    onSurface = TextoOscuro,
    error = RojoError
)

private val EsquemaOscuro = darkColorScheme(
    primary = AzulPrimarioClaro,
    onPrimary = TextoOscuro,
    secondary = NaranjaAcento,
    onSecondary = TextoOscuro,
    background = FondoOscuro,
    surface = SuperficieOscura,
    onBackground = SuperficieClara,
    onSurface = SuperficieClara,
    error = RojoError
)

@Composable
fun TiendaCamisetasTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // desactivado para mantener la identidad de marca consistente
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> EsquemaOscuro
        else -> EsquemaClaro
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = TiendaTypography,
        content = content
    )
}
