package com.gtinnox.edutrack.ui.theme

import androidx.compose.ui.graphics.Color

// ==========================================
// EduTrack 2.0 Design Tokens
// ==========================================

// Primary & Accents (Light Mode)
val EduTrackPrimary = Color(0xFF615CE7)           // Lavender / Indigo Accent
val EduTrackPrimarySoft = Color(0xFFE9E7FF)       // Light lavender wash
val EduTrackPrimaryDark = Color(0xFF4944C4)
val EduTrackBackgroundLight = Color(0xFFF6F5F1)   // Warm Ivory Background
val EduTrackSurfaceLight = Color(0xFFFFFFFF)      // Pure White Card Surface
val EduTrackSurfaceSecondary = Color(0xFFF0EFF5)  // Subtle Secondary Surface
val EduTrackBorderLight = Color(0xFFE7E5ED)       // Refined Hairline Border

// Semantic Status Tokens
val EduTrackSuccess = Color(0xFF16A37B)           // Modern Emerald/Sage
val EduTrackSuccessSoft = Color(0xFFDDF4EA)
val EduTrackWarning = Color(0xFFE6A23C)           // Warm Amber
val EduTrackWarningSoft = Color(0xFFFCE8D8)
val EduTrackDanger = Color(0xFFE06464)            // Coral Rose
val EduTrackDangerSoft = Color(0xFFF8DFE5)

// Typography Tokens (Light)
val EduTrackTextPrimaryLight = Color(0xFF242433)
val EduTrackTextSecondaryLight = Color(0xFF777789)
val EduTrackTextTertiaryLight = Color(0xFFA5A4B5)

// Dark Mode Tokens
val EduTrackBackgroundDark = Color(0xFF15151D)    // Deep Midnight
val EduTrackSurfaceDark = Color(0xFF1E1E29)       // Rich Charcoal Surface
val EduTrackElevatedDark = Color(0xFF272735)      // Elevated Slate Surface
val EduTrackBorderDark = Color(0xFF333345)        // Subtle Dark Border
val EduTrackPrimaryDarkTheme = Color(0xFF8B85FF)  // Brighter Lavender for dark mode
val EduTrackPrimarySoftDark = Color(0xFF34324E)   // Dark Indigo Wash
val EduTrackTextPrimaryDark = Color(0xFFF5F4FC)
val EduTrackTextSecondaryDark = Color(0xFFA5A4B5)

// Pastel Academic Subject Identities
val PastelLavender = Color(0xFFE9E7FF)
val PastelLavenderText = Color(0xFF534EC8)

val PastelMint = Color(0xFFDDF4EA)
val PastelMintText = Color(0xFF0D7858)

val PastelPeach = Color(0xFFFCE8D8)
val PastelPeachText = Color(0xFFB86622)

val PastelRose = Color(0xFFF8DFE5)
val PastelRoseText = Color(0xFFB83A55)

val PastelSky = Color(0xFFDFEFFB)
val PastelSkyText = Color(0xFF1E70A8)

val PastelButter = Color(0xFFF8F0D7)
val PastelButterText = Color(0xFF9E7C10)

// Backward Compatibility Aliases for Existing Code
val AcademicIndigo = EduTrackPrimary
val AcademicIndigoDark = EduTrackPrimaryDark
val AcademicIndigoLight = EduTrackPrimarySoft

val AcademicSage = EduTrackSuccess
val AcademicSageLight = EduTrackSuccessSoft

val AcademicAmber = EduTrackWarning
val AcademicAmberLight = EduTrackWarningSoft

val AcademicRose = EduTrackDanger
val AcademicRoseLight = EduTrackDangerSoft

val AcademicPurple = Color(0xFF8B85FF)
val AcademicSky = PastelSkyText

val PaperIvory = EduTrackBackgroundLight
val SurfaceCard = EduTrackSurfaceLight
val MutedElevation = EduTrackSurfaceSecondary
val HairlineBorder = EduTrackBorderLight
val TextPrimary = EduTrackTextPrimaryLight
val TextSecondary = EduTrackTextSecondaryLight
val TextTertiary = EduTrackTextTertiaryLight

val DarkBackground = EduTrackBackgroundDark
val DarkSurface = EduTrackSurfaceDark
val DarkSurfaceCard = EduTrackSurfaceDark
val DarkBorder = EduTrackBorderDark
val DarkTextPrimary = EduTrackTextPrimaryDark
val DarkTextSecondary = EduTrackTextSecondaryDark
