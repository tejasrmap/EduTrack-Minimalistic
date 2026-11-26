package com.gtinnox.edutrack.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = AcademicIndigo,
    onPrimary = Color.White,
    primaryContainer = AcademicIndigoLight,
    onPrimaryContainer = AcademicIndigoDark,
    secondary = AcademicSage,
    onSecondary = Color.White,
    secondaryContainer = AcademicSageLight,
    onSecondaryContainer = AcademicSage,
    tertiary = AcademicAmber,
    onTertiary = Color.White,
    background = PaperIvory,
    onBackground = TextPrimary,
    surface = SurfaceCard,
    onSurface = TextPrimary,
    surfaceVariant = MutedElevation,
    onSurfaceVariant = TextSecondary,
    outline = HairlineBorder,
    error = AcademicRose,
    onError = Color.White,
    errorContainer = AcademicRoseLight,
    onErrorContainer = AcademicRose
)

private val DarkColorScheme = darkColorScheme(
    primary = AcademicIndigo,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF2E3A7E),
    onPrimaryContainer = Color(0xFFD4DCFF),
    secondary = AcademicSage,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF0D5E46),
    onSecondaryContainer = Color(0xFFA3EBD4),
    tertiary = AcademicAmber,
    onTertiary = Color.White,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceCard,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder,
    error = AcademicRose,
    onError = Color.White
)

@Composable
fun EduTrackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = EduTrackTypography,
        shapes = EduTrackShapes,
        content = content
    )
}
