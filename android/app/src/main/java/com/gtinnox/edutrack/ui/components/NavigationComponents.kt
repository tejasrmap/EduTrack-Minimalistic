package com.gtinnox.edutrack.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gtinnox.edutrack.ui.navigation.BottomNavItem
import com.gtinnox.edutrack.ui.theme.DeepForest
import com.gtinnox.edutrack.ui.theme.Dimens
import com.gtinnox.edutrack.ui.theme.MutedSage
import com.gtinnox.edutrack.ui.theme.PrimaryForest
import com.gtinnox.edutrack.ui.theme.SoftDanger
import com.gtinnox.edutrack.ui.theme.SoftSage
import com.gtinnox.edutrack.ui.theme.adaptiveHorizontalPadding
import com.gtinnox.edutrack.ui.theme.isCompactScreen
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// ==========================================
// Soft Academia Editorial Header (Directly on Background)
// ==========================================
@Composable
fun AcademicEditorialHeader(
    title: String,
    category: String? = null,
    subtitle: String? = null,
    onBackClick: (() -> Unit)? = null,
    onSearchClick: (() -> Unit)? = null,
    onNotificationsClick: (() -> Unit)? = null,
    unreadNotificationsCount: Int = 0,
    trailingAction: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.spaceSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBackClick != null) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.size(Dimens.iconButtonSize)
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(Dimens.iconMedium)
                    )
                }
            }
            Spacer(modifier = Modifier.width(Dimens.spaceMedium))
        }

        Column(modifier = Modifier.weight(1f)) {
            if (category != null) {
                Text(
                    text = category.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
            }
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        if (trailingAction != null) {
            Spacer(modifier = Modifier.width(Dimens.spaceSmall))
            trailingAction()
        } else {
            if (onSearchClick != null) {
                Spacer(modifier = Modifier.width(Dimens.spaceSmall))
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(Dimens.iconButtonSize)
                ) {
                    IconButton(onClick = onSearchClick) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(Dimens.iconMedium)
                        )
                    }
                }
            }

            if (onNotificationsClick != null) {
                Spacer(modifier = Modifier.width(Dimens.spaceSmall))
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(Dimens.iconButtonSize)
                ) {
                    IconButton(onClick = onNotificationsClick) {
                        BadgedBox(
                            badge = {
                                if (unreadNotificationsCount > 0) {
                                    Badge(
                                        containerColor = SoftDanger,
                                        contentColor = Color.White
                                    ) {
                                        Text(
                                            text = "$unreadNotificationsCount",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(Dimens.iconMedium)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Backward-compatible alias
@Composable
fun AcademicHeader(
    title: String,
    subtitle: String? = null,
    onSearchClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    unreadNotificationsCount: Int = 0,
    modifier: Modifier = Modifier
) {
    val dateFormatted = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d"))
    AcademicEditorialHeader(
        title = title,
        category = dateFormatted,
        subtitle = subtitle,
        onSearchClick = onSearchClick,
        onNotificationsClick = onNotificationsClick,
        unreadNotificationsCount = unreadNotificationsCount,
        modifier = modifier
    )
}

// ==========================================
// Floating Curved Organic Bottom Navigation Bar
// Symmetrical, Adaptive Width, Inset-Aware
// ==========================================
@Composable
fun EduTrackBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Subjects,
        BottomNavItem.Focus,
        BottomNavItem.Planner,
        BottomNavItem.More
    )

    val isCompact = isCompactScreen()
    val horizontalPadding = adaptiveHorizontalPadding()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = horizontalPadding, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(Dimens.radiusPill),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            shadowElevation = 4.dp,
            modifier = Modifier.widthIn(max = Dimens.maxNavWidth)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val isSelected = currentRoute == item.route
                    val isFocus = item == BottomNavItem.Focus

                    val targetBg = when {
                        isSelected -> SoftSage
                        isFocus -> MutedSage.copy(alpha = 0.5f)
                        else -> Color.Transparent
                    }
                    val bgAnimated by animateColorAsState(targetValue = targetBg, label = "navBg")

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .clip(RoundedCornerShape(Dimens.radiusMedium))
                            .clickable { onNavigate(item.route) }
                            .background(bgAnimated),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.title,
                                tint = if (isSelected) PrimaryForest else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(Dimens.iconStandard)
                            )
                            if (isSelected && !isCompact) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = DeepForest
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
