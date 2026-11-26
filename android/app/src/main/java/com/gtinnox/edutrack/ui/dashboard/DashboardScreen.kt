package com.gtinnox.edutrack.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gtinnox.edutrack.ui.components.PriorityChip
import com.gtinnox.edutrack.ui.components.SectionHeader
import com.gtinnox.edutrack.ui.components.SoftAcademiaWeeklyActivity
import com.gtinnox.edutrack.ui.components.SoftSageFocusSummary
import com.gtinnox.edutrack.ui.components.SubjectCleanRow
import com.gtinnox.edutrack.ui.theme.DeepForest
import com.gtinnox.edutrack.ui.theme.PrimaryForest
import com.gtinnox.edutrack.ui.theme.SoftBorder
import com.gtinnox.edutrack.ui.theme.SoftDanger
import com.gtinnox.edutrack.ui.theme.SoftSage
import com.gtinnox.edutrack.ui.theme.SoftSuccess
import com.gtinnox.edutrack.ui.theme.SoftWarning
import com.gtinnox.edutrack.ui.viewmodel.DashboardViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToTimer: (String?) -> Unit,
    onNavigateToSubject: (String) -> Unit,
    onNavigateToAssignments: () -> Unit,
    onNavigateToExams: () -> Unit,
    onLogSession: () -> Unit,
    onSearchClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    unreadNotificationsCount: Int = 0,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val profile = uiState.userProfile

    val greetingWord = when (LocalTime.now().hour) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        else -> "Good evening"
    }

    val firstName = profile?.name?.split(" ")?.firstOrNull() ?: "Scholar"
    val dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d"))

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        // A. Elegant Header (Sitting Directly on Background, NOT in a card)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = dateStr.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.size(38.dp)
                        ) {
                            IconButton(onClick = onSearchClick) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.size(38.dp)
                        ) {
                            IconButton(onClick = onNotificationsClick) {
                                BadgedBox(
                                    badge = {
                                        if (unreadNotificationsCount > 0) {
                                            Badge(
                                                containerColor = SoftDanger,
                                                contentColor = Color.White
                                            ) {
                                                Text("$unreadNotificationsCount", fontSize = 9.sp)
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = "Notifications",
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Avatar
                        Surface(
                            shape = CircleShape,
                            color = SoftSage,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = profile?.avatar ?: "SC",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = DeepForest
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Large Two-Line Greeting
                Text(
                    text = "$greetingWord,\n$firstName",
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 38.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = profile?.program ?: "Computer Science & Mathematics",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // B. Focus Summary (ONE Beautiful Warm Sage Surface)
        item {
            SoftSageFocusSummary(
                todayMinutes = uiState.todayStudyMinutes,
                targetMinutes = 180,
                streakDays = uiState.streakDays,
                onStartFocus = { onNavigateToTimer(null) }
            )
        }

        // C. Weekly Activity (Clean 7-day bar chart sitting naturally on background)
        item {
            SoftAcademiaWeeklyActivity(
                trends = uiState.weeklyTrends
            )
        }

        // D. Continue Studying (Horizontal Rows, NOT a tiny carousel - at most 3 subjects)
        item {
            Column {
                SectionHeader(
                    title = "Continue Studying",
                    actionText = "All Courses",
                    onActionClick = { onNavigateToSubject("") }
                )
                Spacer(modifier = Modifier.height(10.dp))

                if (uiState.subjectsWithSessions.isEmpty()) {
                    Text(
                        text = "No enrolled courses yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        uiState.subjectsWithSessions.take(3).forEach { sWithSessions ->
                            val s = sWithSessions.subject
                            val totalMins = sWithSessions.sessions.sumOf { it.durationMinutes }
                            SubjectCleanRow(
                                name = s.name,
                                code = s.code,
                                colorHex = s.color,
                                weeklyMins = totalMins,
                                targetHours = s.weeklyTargetHours,
                                onClick = { onNavigateToTimer(s.id) }
                            )
                        }
                    }
                }
            }
        }

        // E. Today's Schedule (Clean timeline list, at most 2-3 events)
        item {
            Column {
                SectionHeader(
                    title = "Today's Agenda",
                    badgeText = "${uiState.todaySchedule.size}"
                )
                Spacer(modifier = Modifier.height(10.dp))

                if (uiState.todaySchedule.isEmpty()) {
                    Text(
                        text = "No scheduled lectures or study blocks today.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        uiState.todaySchedule.take(3).forEach { event ->
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Time visually separated
                                    Column(modifier = Modifier.width(68.dp)) {
                                        Text(
                                            text = event.startTime,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = PrimaryForest
                                        )
                                        Text(
                                            text = event.endTime,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .height(32.dp)
                                            .width(1.dp)
                                            .background(SoftBorder)
                                    )
                                    Spacer(modifier = Modifier.width(14.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = event.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        if (event.description.isNotBlank()) {
                                            Text(
                                                text = event.description,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // F. Upcoming Deadlines (At most 2 urgent assignments)
        item {
            Column {
                SectionHeader(
                    title = "Upcoming Deadlines",
                    actionText = "View All",
                    onActionClick = onNavigateToAssignments
                )
                Spacer(modifier = Modifier.height(10.dp))

                if (uiState.pendingTasks.isEmpty()) {
                    Text(
                        text = "No looming deliverables. You are on track.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        uiState.pendingTasks.take(2).forEach { task ->
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (task.status == "completed") Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = "Toggle",
                                        tint = if (task.status == "completed") SoftSuccess else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clickable { viewModel.toggleTask(task) }
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = task.title,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Due: ${task.dueDate}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    PriorityChip(priority = task.priority)
                                }
                            }
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
