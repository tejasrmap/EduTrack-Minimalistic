package com.gtinnox.edutrack.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gtinnox.edutrack.ui.theme.DeepForest
import com.gtinnox.edutrack.ui.theme.Dimens
import com.gtinnox.edutrack.ui.theme.MutedSage
import com.gtinnox.edutrack.ui.theme.PrimaryForest
import com.gtinnox.edutrack.ui.theme.SoftBorder
import com.gtinnox.edutrack.ui.theme.SoftDanger
import com.gtinnox.edutrack.ui.theme.SoftSage
import com.gtinnox.edutrack.ui.theme.SoftSuccess
import com.gtinnox.edutrack.ui.theme.SoftTerracotta
import com.gtinnox.edutrack.ui.theme.SoftWarning
import com.gtinnox.edutrack.ui.theme.WhiteSurface

// ==========================================
// 1. Organic Soft Card (24dp primary / 18dp secondary)
// ==========================================
@Composable
fun OrganicCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = MaterialTheme.colorScheme.outline,
    shapeRadius: Dp = Dimens.radiusLarge,
    cornerRadius: Dp = shapeRadius,
    contentPadding: Dp = Dimens.spaceLarge,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val radius = if (cornerRadius != shapeRadius) cornerRadius else shapeRadius
    val shape = RoundedCornerShape(radius)
    val border = BorderStroke(1.dp, borderColor)

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier.fillMaxWidth(),
            shape = shape,
            colors = CardDefaults.cardColors(containerColor = backgroundColor),
            border = border,
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(contentPadding),
                content = content
            )
        }
    } else {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = shape,
            colors = CardDefaults.cardColors(containerColor = backgroundColor),
            border = border,
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(contentPadding),
                content = content
            )
        }
    }
}

// Backward-compatible alias
@Composable
fun PremiumCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = MaterialTheme.colorScheme.outline,
    shapeRadius: Dp = Dimens.radiusLarge,
    contentPadding: Dp = Dimens.spaceLarge,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    OrganicCard(
        modifier = modifier,
        backgroundColor = backgroundColor,
        borderColor = borderColor,
        shapeRadius = shapeRadius,
        contentPadding = contentPadding,
        onClick = onClick,
        content = content
    )
}

@Composable
fun AcademicCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = MaterialTheme.colorScheme.outline,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    OrganicCard(
        modifier = modifier,
        backgroundColor = backgroundColor,
        borderColor = borderColor,
        onClick = onClick,
        content = content
    )
}

// ==========================================
// 2. Soft Sage Focus Summary (Calm, Single Surface)
// Proportional Height, Aligned Streak & Progress
// ==========================================
@Composable
fun SoftSageFocusSummary(
    todayMinutes: Int,
    targetMinutes: Int = 180,
    streakDays: Int = 0,
    onStartFocus: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = if (targetMinutes > 0) {
        (todayMinutes.toFloat() / targetMinutes.toFloat()).coerceIn(0f, 1f)
    } else 0f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "softSageFocus"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.radiusXLarge),
        color = SoftSage,
        border = BorderStroke(1.dp, SoftBorder)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = Dimens.spaceLarge, vertical = Dimens.spaceLarge)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daily Focus",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = DeepForest
                )

                if (streakDays > 0) {
                    Surface(
                        shape = RoundedCornerShape(Dimens.radiusChip),
                        color = WhiteSurface.copy(alpha = 0.7f)
                    ) {
                        Text(
                            text = "🌿 $streakDays day streak",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                            color = PrimaryForest,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimens.spaceMedium))

            val hours = todayMinutes / 60
            val mins = todayMinutes % 60
            val durationText = if (hours > 0) "${hours}h ${mins}m" else "${mins}m"

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = durationText,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepForest
                    )
                )

                Text(
                    text = "${(animatedProgress * 100).toInt()}% of ${(targetMinutes / 60)}h goal",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = PrimaryForest.copy(alpha = 0.85f),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(Dimens.spaceMedium))

            // Gentle organic progress bar spanning 100% of internal width
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = PrimaryForest,
                trackColor = WhiteSurface.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(Dimens.spaceStandard))

            // Calm Forest Green Action Button with 48dp touch height
            Button(
                onClick = onStartFocus,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimens.buttonHeight),
                shape = RoundedCornerShape(Dimens.radiusMedium),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryForest,
                    contentColor = Color.White
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(Dimens.iconMedium)
                    )
                    Spacer(modifier = Modifier.width(Dimens.spaceSmall))
                    Text(
                        text = "Start Focus",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }
        }
    }
}

// Backward-compatible alias
@Composable
fun GradientHeroCard(
    todayMinutes: Int,
    targetMinutes: Int,
    streakDays: Int,
    onStartFocus: () -> Unit,
    modifier: Modifier = Modifier
) {
    SoftSageFocusSummary(
        todayMinutes = todayMinutes,
        targetMinutes = targetMinutes,
        streakDays = streakDays,
        onStartFocus = onStartFocus,
        modifier = modifier
    )
}

// ==========================================
// 3. Section Title (Sitting Directly on Background)
// Vertical Center Aligned
// ==========================================
@Composable
fun SectionHeader(
    title: String,
    badgeText: String? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = Dimens.spaceSmall, bottom = Dimens.spaceXSmall),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (badgeText != null) {
                Spacer(modifier = Modifier.width(Dimens.spaceSmall))
                Text(
                    text = "($badgeText)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (actionText != null && onActionClick != null) {
            Text(
                text = actionText,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryForest
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onActionClick() }
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            )
        }
    }
}

// ==========================================
// 4. Clean Subject Row (Minimalist Library Composition)
// Aligned Icon, Text Overflow Handling, Equal Minimum Height
// ==========================================
@Composable
fun SubjectCleanRow(
    name: String,
    code: String,
    colorHex: String,
    weeklyMins: Int,
    targetHours: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val subColor = try {
        Color(android.graphics.Color.parseColor(colorHex))
    } catch (e: Exception) {
        PrimaryForest
    }

    val progress = if (targetHours > 0) {
        (weeklyMins.toFloat() / (targetHours * 60f)).coerceIn(0f, 1f)
    } else 0f

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(Dimens.radiusMedium),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Dimens.spaceStandard, vertical = Dimens.spaceMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Soft Circular Identity
            Box(
                modifier = Modifier
                    .size(Dimens.iconButtonSize)
                    .background(subColor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = code.take(3).uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = subColor
                    )
                )
            }

            Spacer(modifier = Modifier.width(Dimens.spaceMedium))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${code.ifBlank { "COURSE" }} · ${weeklyMins / 60}h ${weeklyMins % 60}m studied",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(Dimens.spaceSmall))

            // Compact Progress indicator
            Box(
                modifier = Modifier
                    .width(54.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(6.dp)
                        .background(PrimaryForest)
                )
            }
        }
    }
}

// ==========================================
// 5. Subject Chip
// ==========================================
@Composable
fun SubjectChip(
    name: String,
    colorHex: String,
    code: String? = null,
    modifier: Modifier = Modifier
) {
    val chipColor = try {
        Color(android.graphics.Color.parseColor(colorHex))
    } catch (e: Exception) {
        PrimaryForest
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(Dimens.radiusChip),
        color = chipColor.copy(alpha = 0.12f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(chipColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (!code.isNullOrBlank()) "$code · $name" else name,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Medium,
                    color = chipColor
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ==========================================
// 6. Semantic Status & Priority Chips
// ==========================================
@Composable
fun StatusChip(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bg, textColor, label) = when (status.lowercase()) {
        "completed", "done" -> Triple(SoftSage, PrimaryForest, "Completed")
        "in_progress", "active" -> Triple(MutedSage, PrimaryForest, "In Progress")
        "pending" -> Triple(SoftTerracotta, SoftWarning, "Pending")
        else -> Triple(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant, status.replaceFirstChar { it.uppercase() })
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(Dimens.radiusChip),
        color = bg
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            color = textColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun PriorityChip(
    priority: String,
    modifier: Modifier = Modifier
) {
    val (bg, textColor) = when (priority.lowercase()) {
        "urgent", "high" -> Pair(Color(0xFFF7E8E8), SoftDanger)
        "medium" -> Pair(SoftTerracotta, SoftWarning)
        else -> Pair(SoftSage, PrimaryForest)
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(Dimens.radiusChip),
        color = bg
    ) {
        Text(
            text = priority.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
            color = textColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

// ==========================================
// 7. Metric Card
// ==========================================
@Composable
fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color = PrimaryForest,
    comparison: String? = null,
    modifier: Modifier = Modifier
) {
    OrganicCard(
        modifier = modifier,
        shapeRadius = Dimens.radiusMedium,
        contentPadding = Dimens.spaceStandard
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(Dimens.iconButtonSize)
                    .background(iconTint.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(Dimens.iconMedium)
                )
            }
            Spacer(modifier = Modifier.width(Dimens.spaceMedium))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (comparison != null) {
                    Text(
                        text = comparison,
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryForest
                    )
                }
            }
        }
    }
}

// ==========================================
// 8. Empty State
// ==========================================
@Composable
fun AcademicEmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 36.dp, horizontal = Dimens.spaceLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(Dimens.spaceStandard))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (actionText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(Dimens.spaceStandard))
            Button(
                onClick = onActionClick,
                shape = RoundedCornerShape(Dimens.radiusMedium),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryForest)
            ) {
                Text(actionText, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold))
            }
        }
    }
}
