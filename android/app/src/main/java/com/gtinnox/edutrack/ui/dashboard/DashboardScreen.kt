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
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import com.gtinnox.edutrack.data.local.entity.TaskEntity
import com.gtinnox.edutrack.ui.components.AcademicCard
import com.gtinnox.edutrack.ui.components.MetricCard
import com.gtinnox.edutrack.ui.components.MinimalStudyBarChart
import com.gtinnox.edutrack.ui.components.SubjectChip
import com.gtinnox.edutrack.ui.theme.AcademicAmber
import com.gtinnox.edutrack.ui.theme.AcademicIndigo
import com.gtinnox.edutrack.ui.theme.AcademicPurple
import com.gtinnox.edutrack.ui.theme.AcademicRose
import com.gtinnox.edutrack.ui.theme.AcademicSage
import com.gtinnox.edutrack.ui.viewmodel.DashboardViewModel
import com.gtinnox.edutrack.ui.viewmodel.DayTrend

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

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Scholar Greeting & Degree Banner
        item {
            AcademicCard(
                backgroundColor = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(46.dp),
                        shape = CircleShape,
                        color = AcademicIndigo.copy(alpha = 0.15f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = profile?.avatar ?: "JV",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = AcademicIndigo
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Welcome back, ${profile?.name?.split(" ")?.firstOrNull() ?: "Scholar"}",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = profile?.program ?: "B.Sc. Computer Science & Mathematics",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = onLogSession,
                        colors = ButtonDefaults.buttonColors(containerColor = AcademicIndigo),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Log Study", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }

        // Real-Time Study Metrics Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val todayHours = uiState.todayStudyMinutes / 60
                val todayMins = uiState.todayStudyMinutes % 60
                MetricCard(
                    title = "Today Focus",
                    value = "${todayHours}h ${todayMins}m",
                    subtitle = "Daily quota: 3h",
                    icon = Icons.Default.Timer,
                    iconTint = AcademicIndigo,
                    modifier = Modifier.weight(1f)
                )

                val weeklyHours = uiState.weeklyStudyMinutes / 60
                val weeklyTargetHours = uiState.weeklyTargetMinutes / 60
                MetricCard(
                    title = "Weekly Study",
                    value = "${weeklyHours}h",
                    subtitle = "Goal: ${weeklyTargetHours}h / week",
                    icon = Icons.Default.TrendingUp,
                    iconTint = AcademicSage,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Weekly Target Progress Card
        item {
            AcademicCard {
                val progress = if (uiState.weeklyTargetMinutes > 0) {
                    (uiState.weeklyStudyMinutes.toFloat() / uiState.weeklyTargetMinutes.toFloat()).coerceIn(0f, 1f)
                } else 0.5f

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Academic Quota Progress",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = "${(progress * 100).toInt()}% of weekly target completed",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "🔥 ${uiState.streakDays} Day Streak",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = AcademicAmber
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = AcademicIndigo,
                    trackColor = AcademicIndigo.copy(alpha = 0.15f)
                )
            }
        }

        // Quick Continue Studying (Subjects Row)
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Quick Focus Launch",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Launch Pomodoro",
                        style = MaterialTheme.typography.labelSmall,
                        color = AcademicIndigo
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.subjectsWithSessions.take(3).forEach { sWithSessions ->
                        AcademicCard(
                            modifier = Modifier.weight(1f),
                            onClick = { onNavigateToTimer(sWithSessions.subject.id) }
                        ) {
                            SubjectChip(
                                name = sWithSessions.subject.name.take(12),
                                code = sWithSessions.subject.code,
                                colorHex = sWithSessions.subject.color
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Start",
                                    tint = AcademicIndigo,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "25m Focus",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                    color = AcademicIndigo
                                )
                            }
                        }
                    }
                }
            }
        }

        // Today's Academic Schedule
        item {
            AcademicCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Today's Agenda",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "${uiState.todaySchedule.size} Blocks",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (uiState.todaySchedule.isEmpty()) {
                    Text(
                        text = "No scheduled lectures or study blocks today.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    uiState.todaySchedule.forEach { event ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = AcademicIndigo.copy(alpha = 0.1f),
                                modifier = Modifier.padding(end = 10.dp)
                            ) {
                                Text(
                                    text = "${event.startTime} - ${event.endTime}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = AcademicIndigo,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = event.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (event.description.isNotBlank()) {
                                    Text(
                                        text = event.description,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Pending Tasks & Deliverables
        item {
            AcademicCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Looming Deliverables",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "View All (${uiState.pendingTasks.size})",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = AcademicIndigo,
                        modifier = Modifier.clickable { onNavigateToAssignments() }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (uiState.pendingTasks.isEmpty()) {
                    Text(
                        text = "All assignments completed! No pending tasks.",
                        style = MaterialTheme.typography.bodySmall,
                        color = AcademicSage,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    uiState.pendingTasks.forEach { task ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (task.status == "completed") Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = "Toggle",
                                tint = if (task.status == "completed") AcademicSage else AcademicAmber,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable { viewModel.toggleTask(task) }
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = task.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Due: ${task.dueDate} · ${task.category}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Upcoming Examinations
        item {
            AcademicCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Upcoming Examinations",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Exams Hub",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = AcademicIndigo,
                        modifier = Modifier.clickable { onNavigateToExams() }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (uiState.upcomingExams.isEmpty()) {
                    Text(
                        text = "No upcoming examinations registered.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    uiState.upcomingExams.forEach { exam ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = AcademicPurple.copy(alpha = 0.12f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.School,
                                        contentDescription = null,
                                        tint = AcademicPurple,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = exam.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Date: ${exam.examDate} @ ${exam.examTime} · Target: ${exam.targetGrade}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
