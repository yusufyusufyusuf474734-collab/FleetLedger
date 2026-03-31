package com.fleet.ledger.ui

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val scheme = darkColorScheme(
    primary          = Color(0xFF3B82F6),
    onPrimary        = Color.White,
    primaryContainer = Color(0xFF1E3A5F),
    secondary        = Color(0xFF10B981),
    background       = Color(0xFF0D1321),
    surface          = Color(0xFF1C2333),
    surfaceVariant   = Color(0xFF243044),
    onBackground     = Color(0xFFF0F4FA),
    onSurface        = Color(0xFFF0F4FA),
    onSurfaceVariant = Color(0xFFB0BAC8),
    error            = Color(0xFFEF4444),
    outline          = Color(0xFF2E3D55)
)

@Composable
fun AppTheme(content: @Composable () -> Unit) =
    MaterialTheme(colorScheme = scheme, content = content)
