package com.gtinnox.edutrack.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// ==========================================
// Soft Academia Organic Shapes
// - 28dp to 32dp large containers
// - 24dp primary cards
// - 18dp secondary cards / buttons
// ==========================================
val EduTrackShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(18.dp),     // Secondary cards / pills
    large = RoundedCornerShape(24.dp),      // Primary cards
    extraLarge = RoundedCornerShape(30.dp)  // Large containers & Bottom Navigation
)
