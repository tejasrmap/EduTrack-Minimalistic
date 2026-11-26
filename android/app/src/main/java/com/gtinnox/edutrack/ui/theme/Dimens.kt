package com.gtinnox.edutrack.ui.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ==========================================
// EduTrack 2.1 — Standardized Design Tokens & Dimens
// ==========================================
object Dimens {
    // Spacing
    val spaceXSmall: Dp = 4.dp
    val spaceSmall: Dp = 8.dp
    val spaceMedium: Dp = 12.dp
    val spaceStandard: Dp = 16.dp
    val spaceLarge: Dp = 20.dp
    val spaceSection: Dp = 24.dp
    val spaceSectionLarge: Dp = 32.dp

    // Corner Radii
    val radiusChip: Dp = 12.dp
    val radiusSmall: Dp = 14.dp
    val radiusMedium: Dp = 18.dp       // Secondary cards, text fields, buttons
    val radiusLarge: Dp = 24.dp        // Primary cards
    val radiusXLarge: Dp = 28.dp       // Major dialogs, bottom sheets
    val radiusPill: Dp = 32.dp         // Floating navigation, circular pills

    // Icon Sizes
    val iconSmall: Dp = 16.dp
    val iconMedium: Dp = 20.dp
    val iconStandard: Dp = 22.dp
    val iconLarge: Dp = 28.dp

    // Heights & Touch Targets
    val buttonHeight: Dp = 48.dp
    val iconButtonSize: Dp = 40.dp
    val actionTouchSize: Dp = 48.dp
    val bottomNavHeight: Dp = 64.dp

    // Adaptive Width Limits
    val maxContentWidth: Dp = 680.dp
    val maxNavWidth: Dp = 460.dp
    val maxDialogWidth: Dp = 520.dp
}

/**
 * Returns consistent horizontal screen padding adapted to the current screen width.
 * - Compact (<360dp): 16dp
 * - Normal (360dp–420dp): 20dp
 * - Large (>420dp): 24dp
 */
@Composable
fun adaptiveHorizontalPadding(): Dp {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp
    return when {
        screenWidth < 360 -> 16.dp
        screenWidth < 420 -> 20.dp
        else -> 24.dp
    }
}

/**
 * Returns true if the device is a compact/narrow phone (<360dp width).
 */
@Composable
fun isCompactScreen(): Boolean {
    val configuration = LocalConfiguration.current
    return configuration.screenWidthDp < 360
}

/**
 * Returns true if the device is a tablet or wide-screen device (>=600dp width).
 */
@Composable
fun isTabletScreen(): Boolean {
    val configuration = LocalConfiguration.current
    return configuration.screenWidthDp >= 600
}

/**
 * Adaptive screen wrapper that ensures content is centered with a maximum width
 * on tablets/wide screens while applying consistent horizontal margins.
 */
@Composable
fun AdaptiveScreenContainer(
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = adaptiveHorizontalPadding(),
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = Dimens.maxContentWidth)
                .padding(horizontal = horizontalPadding)
        ) {
            content()
        }
    }
}
