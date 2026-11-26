package com.gtinnox.edutrack.ui.notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gtinnox.edutrack.data.repository.EduTrackRepository
import com.gtinnox.edutrack.ui.components.AcademicEditorialHeader
import com.gtinnox.edutrack.ui.components.AcademicEmptyState
import com.gtinnox.edutrack.ui.components.OrganicCard
import com.gtinnox.edutrack.ui.theme.DeepForest
import com.gtinnox.edutrack.ui.theme.Dimens
import com.gtinnox.edutrack.ui.theme.PrimaryForest
import com.gtinnox.edutrack.ui.theme.SoftSage
import com.gtinnox.edutrack.ui.theme.SoftSuccess
import com.gtinnox.edutrack.ui.theme.SoftTerracotta
import com.gtinnox.edutrack.ui.theme.adaptiveHorizontalPadding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun NotificationsScreen(
    repository: EduTrackRepository,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val notifications by repository.allNotifications.collectAsState(initial = emptyList())
    val horizontalPadding = adaptiveHorizontalPadding()

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = Dimens.maxContentWidth)
                .padding(horizontal = horizontalPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)
        ) {
            item {
                AcademicEditorialHeader(
                    title = "Notification Center",
                    category = "Academic Dispatch",
                    subtitle = "${notifications.count { !it.isRead }} Unread Academic Alerts",
                    onBackClick = onNavigateBack,
                    trailingAction = if (notifications.isNotEmpty()) {
                        {
                            TextButton(onClick = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    repository.markAllNotificationsRead()
                                }
                            }) {
                                Text("Mark All Read", color = PrimaryForest, fontWeight = FontWeight.Bold)
                            }
                        }
                    } else null
                )
            }

            if (notifications.isEmpty()) {
                item {
                    AcademicEmptyState(
                        icon = Icons.Default.Notifications,
                        title = "No Academic Alerts",
                        description = "You're completely up to date with examinations, scheduled lectures, and assignment deadlines."
                    )
                }
            } else {
                items(notifications) { notif ->
                    val (iconVector, iconBg, iconColor) = when (notif.type) {
                        "exam_countdown" -> Triple(Icons.Default.School, SoftTerracotta, DeepForest)
                        "task_due" -> Triple(Icons.AutoMirrored.Filled.Assignment, SoftSage, PrimaryForest)
                        "streak" -> Triple(Icons.AutoMirrored.Filled.TrendingUp, SoftSage, SoftSuccess)
                        else -> Triple(Icons.Default.Timer, SoftSage, PrimaryForest)
                    }

                    OrganicCard(
                        cornerRadius = Dimens.radiusMedium,
                        contentPadding = Dimens.spaceStandard,
                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                repository.markNotificationRead(notif.id)
                            }
                        }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = iconBg,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = iconVector,
                                        contentDescription = null,
                                        tint = iconColor,
                                        modifier = Modifier.size(Dimens.iconMedium)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(Dimens.spaceMedium))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = notif.title,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = if (notif.isRead) FontWeight.Medium else FontWeight.Bold
                                        ),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f, fill = false)
                                    )

                                    if (!notif.isRead) {
                                        Spacer(modifier = Modifier.width(Dimens.spaceSmall))
                                        Surface(
                                            shape = CircleShape,
                                            color = PrimaryForest,
                                            modifier = Modifier.size(8.dp)
                                        ) {}
                                    }
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = notif.message,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = notif.timestamp,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}
