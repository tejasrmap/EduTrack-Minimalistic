package com.gtinnox.edutrack.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = EduTrackPrimary,
    onPrimary = Color.White,
    primaryContainer = EduTrackPrimarySoft,
    onPrimaryContainer = EduTrackPrimaryDark,
    secondary = EduTrackSuccess,
    onSecondary = Color.White,
    secondaryContainer = EduTrackSuccessSoft,
    onSecondaryContainer = EduTrackSuccess,
    tertiary = EduTrackWarning,
    onTertiary = Color.White,
    tertiaryContainer = EduTrackWarningSoft,
    onTertiaryContainer = EduTrackWarning,
    background = EduTrackBackgroundLight,
    onBackground = EduTrackTextPrimaryLight,
    surface = EduTrackSurfaceLight,
    onSurface = EduTrackTextPrimaryLight,
    surfaceVariant = EduTrackSurfaceSecondary,
    onSurfaceVariant = EduTrackTextSecondaryLight,
    outline = EduTrackBorderLight,
    outlineVariant = EduTrackBorderLight.copy(alpha = 0.5f),
    error = EduTrackDanger,
    onError = Color.White,
    errorContainer = EduTrackDangerSoft,
    onErrorContainer = EduTrackDanger
)

private val DarkColorScheme = darkColorScheme(
    primary = EduTrackPrimaryDarkTheme,
    onPrimary = Color(0xFF15151D),
    primaryContainer = EduTrackPrimarySoftDark,
    onPrimaryContainer = Color(0xFFE9E7FF),
    secondary = EduTrackSuccess,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF0F4335),
    onSecondaryContainer = Color(0xFFA5F3DC),
    tertiary = EduTrackWarning,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF4E3710),
    onTertiaryContainer = Color(0xFFFDE68A),
    background = EduTrackBackgroundDark,
    onBackground = EduTrackTextPrimaryDark,
    surface = EduTrackSurfaceDark,
    onSurface = EduTrackTextPrimaryDark,
    surfaceVariant = EduTrackElevatedDark,
    onSurfaceVariant = EduTrackTextSecondaryDark,
    outline = EduTrackBorderDark,
    outlineVariant = EduTrackBorderDark.copy(alpha = 0.5f),
    error = EduTrackDanger,
    onError = Color.White,
    errorContainer = Color(0xFF4A1A22),
    onErrorContainer = EduTrackDangerSoft
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
