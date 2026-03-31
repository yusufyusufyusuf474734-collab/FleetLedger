package com.fleet.ledger.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Renk paleti
val Navy900 = Color(0xFF060D1A)
val Navy800 = Color(0xFF0D1321)
val Navy700 = Color(0xFF111827)
val Navy600 = Color(0xFF1A2235)
val Navy500 = Color(0xFF243044)
val Navy400 = Color(0xFF2E3D55)
val Blue500  = Color(0xFF3B82F6)
val Blue400  = Color(0xFF60A5FA)
val Green500 = Color(0xFF10B981)
val Red500   = Color(0xFFEF4444)
val Amber500 = Color(0xFFF59E0B)
val Slate300 = Color(0xFFB0BAC8)

private val ColorScheme = darkColorScheme(
    primary             = Blue500,
    onPrimary           = Color.White,
    primaryContainer    = Blue500.copy(alpha = 0.12f),
    onPrimaryContainer  = Blue400,
    secondary           = Green500,
    onSecondary         = Color.White,
    secondaryContainer  = Green500.copy(alpha = 0.12f),
    onSecondaryContainer= Green500,
    tertiary            = Amber500,
    tertiaryContainer   = Amber500.copy(alpha = 0.12f),
    background          = Navy800,
    surface             = Navy600,
    surfaceVariant      = Navy500,
    surfaceTint         = Blue500,
    onBackground        = Color(0xFFF0F4FA),
    onSurface           = Color(0xFFF0F4FA),
    onSurfaceVariant    = Slate300,
    error               = Red500,
    errorContainer      = Red500.copy(alpha = 0.12f),
    onErrorContainer    = Red500,
    outline             = Navy400,
    outlineVariant      = Navy500
)

@Composable
fun AppTheme(content: @Composable () -> Unit) = MaterialTheme(
    colorScheme = ColorScheme,
    shapes = Shapes(
        extraSmall = RoundedCornerShape(4.dp),
        small      = RoundedCornerShape(8.dp),
        medium     = RoundedCornerShape(12.dp),
        large      = RoundedCornerShape(16.dp),
        extraLarge = RoundedCornerShape(24.dp)
    ),
    typography = Typography(
        titleLarge  = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 20.sp, letterSpacing = (-0.3).sp),
        titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 16.sp, letterSpacing = (-0.2).sp),
        titleSmall  = TextStyle(fontWeight = FontWeight.Medium,   fontSize = 14.sp),
        bodyLarge   = TextStyle(fontWeight = FontWeight.Normal,   fontSize = 16.sp),
        bodyMedium  = TextStyle(fontWeight = FontWeight.Normal,   fontSize = 14.sp),
        bodySmall   = TextStyle(fontWeight = FontWeight.Normal,   fontSize = 12.sp),
        labelLarge  = TextStyle(fontWeight = FontWeight.Medium,   fontSize = 13.sp),
        labelMedium = TextStyle(fontWeight = FontWeight.Medium,   fontSize = 11.sp, letterSpacing = 0.4.sp),
        labelSmall  = TextStyle(fontWeight = FontWeight.Normal,   fontSize = 10.sp, letterSpacing = 0.4.sp)
    ),
    content = content
)

// ── Ortak bileşenler ──────────────────────────────────────────────────────────

@Composable
fun ProCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp),
        content = { Column(modifier = Modifier.padding(16.dp), content = content) }
    )
}

@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        title.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        modifier = modifier.padding(horizontal = 4.dp, vertical = 6.dp)
    )
}

@Composable
fun ProDivider() = HorizontalDivider(
    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
    thickness = 0.5.dp,
    modifier = Modifier.padding(vertical = 8.dp)
)

@Composable
fun StatusBadge(text: String, color: Color) {
    Surface(shape = MaterialTheme.shapes.extraSmall, color = color.copy(alpha = 0.15f)) {
        Text(text, style = MaterialTheme.typography.labelSmall, color = color,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
    }
}

// Para formatı
fun Double.tl(): String = "₺%,.0f".format(this)
