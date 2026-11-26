package com.gtinnox.edutrack.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import com.gtinnox.edutrack.ui.components.GradientHeroCard
import com.gtinnox.edutrack.ui.components.MetricCard
import com.gtinnox.edutrack.ui.components.PremiumCard
import com.gtinnox.edutrack.ui.components.PriorityChip
import com.gtinnox.edutrack.ui.components.SectionHeader
import com.gtinnox.edutrack.ui.components.StatusChip
import com.gtinnox.edutrack.ui.components.SubjectChip
import com.gtinnox.edutrack.ui.theme.EduTrackBorderLight
import com.gtinnox.edutrack.ui.theme.EduTrackPrimary
import com.gtinnox.edutrack.ui.theme.EduTrackPrimarySoft
import com.gtinnox.edutrack.ui.theme.EduTrackSuccess
import com.gtinnox.edutrack.ui.theme.EduTrackWarning
import com.gtinnox.edutrack.ui.viewmodel.DashboardViewModel
import java.time.LocalTime

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToTimer: (String?) -> Unit,
    onNavigateToSubject: (String) -> Unit,
    onNavigateToAssignments: () -> Unit,
    onNavigateToExams: () -> Unit,
    onLogSession: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val profile = uiState.userProfile

    val greeting = when (LocalTime.now().hour) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        else -> "Good evening"
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // 1. Scholar Identity & Greeting Bar
        item {
            PremiumCard(
                contentPadding = 16.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        color = EduTrackPrimarySoft
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = profile?.avatar ?: "TG",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = EduTrackPrimary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "$greeting, ${profile?.name?.split(" ")?.firstOrNull() ?: "Scholar"}",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = profile?.program ?: "Computer Science & Engineering",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = onLogSession,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EduTrackPrimary)
                    ) {
                        Text(
                            text = "Log Study",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        // 2. Focus Hero Card (Ambient Gradient)
        item {
            GradientHeroCard(
                todayMinutes = uiState.todayStudyMinutes,
                targetMinutes = 180, // Default 3 hours daily quota
                streakDays = uiState.streakDays,
                onStartFocus = { onNavigateToTimer(null) }
            )
        }

        // 3. Compact Academic Statistics Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                val weeklyHours = uiState.weeklyStudyMinutes / 60
                val weeklyTargetHours = uiState.weeklyTargetMinutes / 60
                MetricCard(
                    title = "Weekly Study",
                    value = "${weeklyHours}h",
                    subtitle = "Goal: ${weeklyTargetHours}h / week",
                    icon = Icons.Default.TrendingUp,
                    iconTint = EduTrackSuccess,
                    comparison = if (weeklyHours > 0) "+12% vs last week" else null,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "Study Streak",
                    value = "${uiState.streakDays} Days",
                    subtitle = "Consistent focus",
                    icon = Icons.Default.LocalFireDepartment,
                    iconTint = EduTrackWarning,
                    comparison = "Active streak",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 4. Quick Focus Launch (Subject Pills)
        item {
            Column {
                SectionHeader(
                    title = "Quick Focus Launch",
                    badgeText = "${uiState.subjectsWithSessions.size} Subjects"
                )
                Spacer(modifier = Modifier.height(10.dp))

                if (uiState.subjectsWithSessions.isEmpty()) {
                    Text(
                        text = "No subjects enrolled yet. Add subjects to launch focused timers.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(uiState.subjectsWithSessions) { sWithSessions ->
                            PremiumCard(
                                modifier = Modifier.width(150.dp),
                                contentPadding = 14.dp,
                                onClick = { onNavigateToTimer(sWithSessions.subject.id) }
                            ) {
                                SubjectChip(
                                    name = sWithSessions.subject.name.take(14),
                                    code = sWithSessions.subject.code,
                                    colorHex = sWithSessions.subject.color
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "25m Focus",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = EduTrackPrimary
                                        )
                                    )
                                    Surface(
                                        shape = CircleShape,
                                        color = EduTrackPrimarySoft,
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = "Start",
                                                tint = EduTrackPrimary,
                                                modifier = Modifier.size(14.dp)
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

        // 5. Today's Academic Schedule
        item {
            Column {
                SectionHeader(
                    title = "Today's Agenda",
                    badgeText = "${uiState.todaySchedule.size} Blocks"
                )
                Spacer(modifier = Modifier.height(10.dp))

                if (uiState.todaySchedule.isEmpty()) {
                    PremiumCard(contentPadding = 18.dp) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.DateRange,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "No scheduled lectures or study blocks today.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        uiState.todaySchedule.forEach { event ->
                            PremiumCard(contentPadding = 14.dp) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = EduTrackPrimarySoft,
                                        modifier = Modifier.padding(end = 12.dp)
                                    ) {
                                        Text(
                                            text = "${event.startTime} - ${event.endTime}",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = EduTrackPrimary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = event.title,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
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

        // 6. Looming Deliverables & Tasks
        item {
            Column {
                SectionHeader(
                    title = "Looming Deliverables",
                    badgeText = "${uiState.pendingTasks.size} Pending",
                    actionText = "View All",
                    onActionClick = onNavigateToAssignments
                )
                Spacer(modifier = Modifier.height(10.dp))

                if (uiState.pendingTasks.isEmpty()) {
                    PremiumCard(contentPadding = 18.dp) {
                        Text(
                            text = "🎉 All assignments completed! Excellent academic progress.",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = EduTrackSuccess
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        uiState.pendingTasks.take(4).forEach { task ->
                            PremiumCard(
                                contentPadding = 14.dp,
                                onClick = { viewModel.toggleTask(task) }
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (task.status == "completed") Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = "Toggle",
                                        tint = if (task.status == "completed") EduTrackSuccess else EduTrackWarning,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = task.title,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Due: ${task.dueDate} · ${task.category}",
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

        // 7. Upcoming Examinations
        item {
            Column {
                SectionHeader(
                    title = "Upcoming Examinations",
                    badgeText = "${uiState.upcomingExams.size} Scheduled",
                    actionText = "Exams Hub",
                    onActionClick = onNavigateToExams
                )
                Spacer(modifier = Modifier.height(10.dp))

                if (uiState.upcomingExams.isEmpty()) {
                    PremiumCard(contentPadding = 18.dp) {
                        Text(
                            text = "No upcoming examinations registered.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        uiState.upcomingExams.take(3).forEach { exam ->
                            PremiumCard(
                                contentPadding = 14.dp,
                                onClick = onNavigateToExams
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = EduTrackPrimarySoft,
                                        modifier = Modifier.size(42.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.School,
                                                contentDescription = null,
                                                tint = EduTrackPrimary,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = exam.title,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Date: ${exam.examDate} @ ${exam.examTime}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = EduTrackSuccess.copy(alpha = 0.12f)
                                    ) {
                                        Text(
                                            text = "Target: ${exam.targetGrade}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = EduTrackSuccess
                                            ),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
