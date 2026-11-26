package com.gtinnox.edutrack.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SoftAcademiaLightScheme = lightColorScheme(
    primary = PrimaryForest,
    onPrimary = Color.White,
    primaryContainer = SoftSage,
    onPrimaryContainer = DeepForest,
    secondary = SoftSuccess,
    onSecondary = Color.White,
    secondaryContainer = MutedSage,
    onSecondaryContainer = DeepForest,
    tertiary = SoftWarning,
    onTertiary = Color.White,
    tertiaryContainer = SoftTerracotta,
    onTertiaryContainer = DeepForest,
    background = WarmIvoryBackground,
    onBackground = TextPrimary,
    surface = WhiteSurface,
    onSurface = TextPrimary,
    surfaceVariant = MutedSage,
    onSurfaceVariant = TextSecondary,
    outline = SoftBorder,
    outlineVariant = SoftBorder.copy(alpha = 0.5f),
    error = SoftDanger,
    onError = Color.White,
    errorContainer = Color(0xFFF7E8E8),
    onErrorContainer = SoftDanger
)

private val SoftAcademiaDarkScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = Color(0xFF171A18),
    primaryContainer = DarkSoftAccent,
    onPrimaryContainer = DarkText,
    secondary = DarkPrimary,
    onSecondary = Color(0xFF171A18),
    secondaryContainer = DarkSoftAccent,
    onSecondaryContainer = DarkText,
    tertiary = SoftWarning,
    onTertiary = Color(0xFF171A18),
    tertiaryContainer = Color(0xFF3D2D1B),
    onTertiaryContainer = SoftTerracotta,
    background = DarkBackground,
    onBackground = DarkText,
    surface = DarkPrimarySurface,
    onSurface = DarkText,
    surfaceVariant = DarkElevatedSurface,
    onSurfaceVariant = DarkSecondaryText,
    outline = DarkBorder,
    outlineVariant = DarkBorder.copy(alpha = 0.5f),
    error = SoftDanger,
    onError = Color.White,
    errorContainer = Color(0xFF452020),
    onErrorContainer = Color(0xFFF7E8E8)
)

@Composable
fun EduTrackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) SoftAcademiaDarkScheme else SoftAcademiaLightScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = EduTrackTypography,
        shapes = EduTrackShapes,
        content = content
    )
}
