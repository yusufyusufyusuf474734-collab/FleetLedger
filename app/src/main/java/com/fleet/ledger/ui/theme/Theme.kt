package com.fleet.ledger.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Primary600,
    onPrimary = Color.White,
    primaryContainer = Primary100,
    onPrimaryContainer = Primary900,
    secondary = Secondary500,
    onSecondary = Color.White,
    secondaryContainer = Secondary50,
    onSecondaryContainer = Secondary700,
    tertiary = Warning,
    onTertiary = Color.White,
    error = Error,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = BackgroundLight,
    onBackground = Neutral900,
    surface = SurfaceLight,
    onSurface = Neutral900,
    surfaceVariant = Neutral100,
    onSurfaceVariant = Neutral700,
    outline = Neutral400,
    outlineVariant = Neutral200,
    scrim = Color.Black.copy(alpha = 0.32f)
)

private val DarkColorScheme = darkColorScheme(
    primary = Primary400,
    onPrimary = Primary900,
    primaryContainer = Primary700,
    onPrimaryContainer = Primary100,
    secondary = Secondary500,
    onSecondary = Secondary700,
    secondaryContainer = Secondary700,
    onSecondaryContainer = Secondary50,
    tertiary = Warning,
    onTertiary = Color.Black,
    error = Error,
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = BackgroundDark,
    onBackground = Neutral100,
    surface = SurfaceDark,
    onSurface = Neutral100,
    surfaceVariant = Neutral800,
    onSurfaceVariant = Neutral300,
    outline = Neutral600,
    outlineVariant = Neutral700,
    scrim = Color.Black.copy(alpha = 0.32f)
)

@Composable
fun FleetLedgerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
