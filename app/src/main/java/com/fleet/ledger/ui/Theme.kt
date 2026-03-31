package com.fleet.ledger.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val Navy900 = Color(0xFF0A0E1A)
val Navy800 = Color(0xFF0D1321)
val Navy700 = Color(0xFF111827)
val Navy600 = Color(0xFF1C2333)
val Navy500 = Color(0xFF243044)
val Blue500  = Color(0xFF3B82F6)
val Green500 = Color(0xFF10B981)
val Red500   = Color(0xFFEF4444)
val Amber500 = Color(0xFFF59E0B)

private val scheme = darkColorScheme(
    primary             = Blue500,
    onPrimary           = Color.White,
    primaryContainer    = Blue500.copy(alpha = 0.15f),
    onPrimaryContainer  = Blue500,
    secondary           = Green500,
    onSecondary         = Color.White,
    tertiary            = Amber500,
    background          = Navy800,
    surface             = Navy600,
    surfaceVariant      = Navy500,
    onBackground        = Color(0xFFF0F4FA),
    onSurface           = Color(0xFFF0F4FA),
    onSurfaceVariant    = Color(0xFFB0BAC8),
    error               = Red500,
    outline             = Navy500
)

@Composable
fun AppTheme(content: @Composable () -> Unit) = MaterialTheme(
    colorScheme = scheme,
    typography = MaterialTheme.typography.copy(
        titleLarge  = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 20.sp),
        titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
        titleSmall  = TextStyle(fontWeight = FontWeight.Medium,   fontSize = 14.sp),
        bodyMedium  = TextStyle(fontWeight = FontWeight.Normal,   fontSize = 14.sp),
        bodySmall   = TextStyle(fontWeight = FontWeight.Normal,   fontSize = 12.sp),
        labelMedium = TextStyle(fontWeight = FontWeight.Medium,   fontSize = 11.sp),
        labelSmall  = TextStyle(fontWeight = FontWeight.Normal,   fontSize = 10.sp)
    ),
    content = content
)

// Ortak bileşenler
@Composable
fun SectionCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (title.isNotBlank()) {
                Text(title, style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer8()
            }
            content()
        }
    }
}

@Composable fun Spacer8() = androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 8.dp))
@Composable fun Divider() = HorizontalDivider(
    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), thickness = 0.5.dp,
    modifier = Modifier.padding(vertical = 8.dp))

fun Double.tl(): String = "₺%,.0f".format(this)
