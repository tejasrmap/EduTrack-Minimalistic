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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Timer
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gtinnox.edutrack.ui.components.EduTrackCoachMark
import com.gtinnox.edutrack.ui.components.PriorityChip
import com.gtinnox.edutrack.ui.components.SectionHeader
import com.gtinnox.edutrack.ui.components.SoftAcademiaWeeklyActivity
import com.gtinnox.edutrack.ui.components.SoftSageFocusSummary
import com.gtinnox.edutrack.ui.components.SubjectCleanRow
import com.gtinnox.edutrack.ui.components.TutorialStep
import com.gtinnox.edutrack.ui.theme.DeepForest
import com.gtinnox.edutrack.ui.theme.Dimens
import com.gtinnox.edutrack.ui.theme.PrimaryForest
import com.gtinnox.edutrack.ui.theme.SoftBorder
import com.gtinnox.edutrack.ui.theme.SoftDanger
import com.gtinnox.edutrack.ui.theme.SoftSage
import com.gtinnox.edutrack.ui.theme.SoftSuccess
import com.gtinnox.edutrack.ui.theme.adaptiveHorizontalPadding
import com.gtinnox.edutrack.ui.theme.isCompactScreen
import com.gtinnox.edutrack.ui.viewmodel.DashboardViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToTimer: (String?) -> Unit,
    onNavigateToSubject: (String) -> Unit,
    onNavigateToSubjects: () -> Unit = {},
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

    val firstName = uiState.userName.split(" ").firstOrNull()?.ifBlank { "Scholar" } ?: "Scholar"
    val initials = uiState.userName.split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .map { it.first().uppercase() }
        .joinToString("")
        .ifBlank { "SC" }
    val dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d"))
    val horizontalPadding = adaptiveHorizontalPadding()
    val isCompact = isCompactScreen()

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = Dimens.maxContentWidth)
                .padding(horizontal = horizontalPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceSection)
        ) {
            // A. Elegant Header (Sitting Directly on Background, NOT in a card)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = Dimens.spaceStandard)
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
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Symmetrical Action Cluster
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)
                        ) {
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
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold
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

                            // Profile Avatar with matching size & centered typography
                            Surface(
                                shape = CircleShape,
                                color = SoftSage,
                                modifier = Modifier.size(Dimens.iconButtonSize)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = initials,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = DeepForest
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(Dimens.spaceMedium))

                    // Large Two-Line Greeting with responsive font size
                    Text(
                        text = "$greetingWord,\n$firstName",
                        style = if (isCompact) {
                            MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 34.sp
                            )
                        } else {
                            MaterialTheme.typography.displayMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                lineHeight = 38.sp
                            )
                        },
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = uiState.userProgram,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Contextual First-Time Dashboard Tutorial
            if (uiState.showTutorial) {
                item {
                    var currentStep by remember { mutableIntStateOf(0) }
                    val tutorialSteps = listOf(
                        TutorialStep(
                            title = "Welcome to EduTrack!",
                            description = "Your dashboard gives you a quick overview of your study progress, daily targets, and enrolled course tasks.",
                            icon = Icons.Default.Spa
                        ),
                        TutorialStep(
                            title = "Daily Focus Progress",
                            description = "Track your active study hours against your goal, keep streak momentum, and tap 'Start Focus' to launch a pomodoro session.",
                            icon = Icons.Default.Timer
                        ),
                        TutorialStep(
                            title = "Weekly Study Telemetry",
                            description = "Visualize your study minutes across Monday to Sunday to balance your academic preparation.",
                            icon = Icons.Default.BarChart
                        ),
                        TutorialStep(
                            title = "Continuing Courses & Planner",
                            description = "Select any enrolled subject to review lecture sessions, or schedule upcoming exams and revision blocks.",
                            icon = Icons.AutoMirrored.Filled.MenuBook
                        )
                    )

                    EduTrackCoachMark(
                        steps = tutorialSteps,
                        currentStepIndex = currentStep,
                        onNext = {
                            if (currentStep < tutorialSteps.size - 1) {
                                currentStep++
                            } else {
                                viewModel.markTutorialCompleted()
                            }
                        },
                        onPrevious = {
                            if (currentStep > 0) currentStep--
                        },
                        onSkip = {
                            viewModel.markTutorialCompleted()
                        }
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
                        onActionClick = onNavigateToSubjects
                    )
                    Spacer(modifier = Modifier.height(Dimens.spaceSmall))

                    if (uiState.subjectsWithSessions.isEmpty()) {
                        Text(
                            text = "No enrolled courses yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)) {
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
                    Spacer(modifier = Modifier.height(Dimens.spaceSmall))

                    if (uiState.todaySchedule.isEmpty()) {
                        Text(
                            text = "No scheduled lectures or study blocks today.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)) {
                            uiState.todaySchedule.take(3).forEach { event ->
                                Surface(
                                    shape = RoundedCornerShape(Dimens.radiusMedium),
                                    color = MaterialTheme.colorScheme.surface,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = Dimens.spaceStandard, vertical = Dimens.spaceMedium),
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
                                        Spacer(modifier = Modifier.width(Dimens.spaceMedium))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = event.title,
                                                style = MaterialTheme.typography.titleMedium,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            if (event.description.isNotBlank()) {
                                                Text(
                                                    text = event.description,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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
            }

            // F. Upcoming Deadlines (At most 2 urgent assignments)
            item {
                Column {
                    SectionHeader(
                        title = "Upcoming Deadlines",
                        actionText = "View All",
                        onActionClick = onNavigateToAssignments
                    )
                    Spacer(modifier = Modifier.height(Dimens.spaceSmall))

                    if (uiState.pendingTasks.isEmpty()) {
                        Text(
                            text = "No looming deliverables. You are on track.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)) {
                            uiState.pendingTasks.take(2).forEach { task ->
                                Surface(
                                    shape = RoundedCornerShape(Dimens.radiusMedium),
                                    color = MaterialTheme.colorScheme.surface,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = Dimens.spaceStandard, vertical = Dimens.spaceMedium),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (task.status == "completed") Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                            contentDescription = "Toggle",
                                            tint = if (task.status == "completed") SoftSuccess else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier
                                                .size(Dimens.iconMedium)
                                                .clickable { viewModel.toggleTask(task) }
                                        )
                                        Spacer(modifier = Modifier.width(Dimens.spaceMedium))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = task.title,
                                                style = MaterialTheme.typography.titleMedium,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
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

            // Generous spacing at the bottom so floating nav never occludes content
            item {
                Spacer(modifier = Modifier.height(84.dp))
            }
        }
    }
}
