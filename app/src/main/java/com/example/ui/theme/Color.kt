package com.example.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class AppColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val surfaceBorder: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val primary: Color,
    val secondary: Color,
    val isDark: Boolean
)

val DarkAppColors = AppColors(
    background = Color(0xFF090D14),
    surface = Color(0xFF101724),
    surfaceVariant = Color(0xFF182336),
    surfaceBorder = Color(0xFF22314A),
    textPrimary = Color(0xFFF1F5F9),
    textSecondary = Color(0xFF94A3B8),
    textMuted = Color(0xFF64748B),
    primary = Color(0xFF00E5FF),
    secondary = Color(0xFF00E676),
    isDark = true
)

val LightAppColors = AppColors(
    background = Color(0xFFF1F5F9),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE2E8F0),
    surfaceBorder = Color(0xFFCBD5E1),
    textPrimary = Color(0xFF0F172A),
    textSecondary = Color(0xFF475569),
    textMuted = Color(0xFF94A3B8),
    primary = Color(0xFF0284C7),
    secondary = Color(0xFF10B981),
    isDark = false
)

val LocalAppColors = staticCompositionLocalOf { DarkAppColors }

// Dynamic adaptive color tokens
val CyberDarkBackground: Color
    @Composable get() = LocalAppColors.current.background

val CyberSurface: Color
    @Composable get() = LocalAppColors.current.surface

val CyberSurfaceVariant: Color
    @Composable get() = LocalAppColors.current.surfaceVariant

val CyberSurfaceBorder: Color
    @Composable get() = LocalAppColors.current.surfaceBorder

val TextPrimary: Color
    @Composable get() = LocalAppColors.current.textPrimary

val TextSecondary: Color
    @Composable get() = LocalAppColors.current.textSecondary

val TextMuted: Color
    @Composable get() = LocalAppColors.current.textMuted

val NeonCyan: Color
    @Composable get() = LocalAppColors.current.primary

val EmeraldSuccess: Color
    @Composable get() = LocalAppColors.current.secondary

// Static helpers
val AmberWarning = Color(0xFFFFB300)
val CrimsonError = Color(0xFFFF3D71)
val ColorTelegram = Color(0xFF29B6F6)
