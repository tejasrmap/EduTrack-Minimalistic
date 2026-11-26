package com.gtinnox.edutrack.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// ==========================================
// EduTrack 2.0 Shape Tokens (18-24dp cards)
// ==========================================
val EduTrackShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),      // Standard Card Radius (20dp)
    extraLarge = RoundedCornerShape(24.dp)   // Dialogs, Sheets & Hero Cards (24dp)
)
