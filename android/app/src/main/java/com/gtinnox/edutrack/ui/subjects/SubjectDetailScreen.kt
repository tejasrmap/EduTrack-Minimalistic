package com.gtinnox.edutrack.ui.subjects

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gtinnox.edutrack.ui.components.AcademicEditorialHeader
import com.gtinnox.edutrack.ui.components.OrganicCard
import com.gtinnox.edutrack.ui.components.SectionHeader
import com.gtinnox.edutrack.ui.components.SubjectChip
import com.gtinnox.edutrack.ui.theme.Dimens
import com.gtinnox.edutrack.ui.theme.PrimaryForest
import com.gtinnox.edutrack.ui.theme.SoftDanger
import com.gtinnox.edutrack.ui.theme.SoftSage
import com.gtinnox.edutrack.ui.theme.SoftSuccess
import com.gtinnox.edutrack.ui.theme.adaptiveHorizontalPadding
import com.gtinnox.edutrack.ui.viewmodel.SubjectViewModel

@Composable
fun SubjectDetailScreen(
    subjectId: String,
    viewModel: SubjectViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onNavigateToTimer: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val subjectWithSessions by viewModel.getSubjectWithSessions(subjectId).collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    val horizontalPadding = adaptiveHorizontalPadding()

    val data = subjectWithSessions

    if (data == null) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(Dimens.spaceLarge),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Curriculum module loading...", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(Dimens.spaceStandard))
            Button(
                onClick = onNavigateBack,
                shape = RoundedCornerShape(Dimens.radiusMedium),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryForest)
            ) {
                Text("Return to Modules")
            }
        }
        return
    }

    val subject = data.subject
    val sessions = data.sessions
    val totalMinutes = sessions.sumOf { it.durationMinutes }
    val targetMinutes = (subject.weeklyTargetHours * 60).coerceAtLeast(1)
    val progress = (totalMinutes.toFloat() / targetMinutes.toFloat()).coerceIn(0f, 1f)

    val subColor = try {
        Color(android.graphics.Color.parseColor(subject.color))
    } catch (e: Exception) {
        PrimaryForest
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    "Delete Course Module?",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = { Text("Are you sure you want to remove '${subject.name}'? Associated study logs and deliverables will also be cleared.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteSubject(subject)
                        onNavigateBack()
                    }
                ) {
                    Text("Delete Module", color = SoftDanger, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(Dimens.radiusLarge)
        )
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = Dimens.maxContentWidth)
                .padding(horizontal = horizontalPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceStandard)
        ) {
            // App Bar directly on background
            item {
                AcademicEditorialHeader(
                    title = subject.name,
                    category = "${subject.code} Curriculum",
                    onBackClick = onNavigateBack,
                    trailingAction = {
                        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(Dimens.iconButtonSize)
                            ) {
                                IconButton(onClick = { onNavigateToEdit(subject.id) }) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit",
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(Dimens.iconMedium)
                                    )
                                }
                            }

                            Surface(
                                shape = CircleShape,
                                color = SoftDanger.copy(alpha = 0.12f),
                                modifier = Modifier.size(Dimens.iconButtonSize)
                            ) {
                                IconButton(onClick = { showDeleteDialog = true }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = SoftDanger,
                                        modifier = Modifier.size(Dimens.iconMedium)
                                    )
                                }
                            }
                        }
                    }
                )
            }

            // Overview Header Card
            item {
                OrganicCard(
                    cornerRadius = Dimens.radiusXLarge,
                    contentPadding = Dimens.spaceLarge
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SubjectChip(
                            name = subject.name,
                            code = subject.code,
                            colorHex = subject.color
                        )
                        Surface(
                            shape = RoundedCornerShape(Dimens.radiusChip),
                            color = SoftSage.copy(alpha = 0.6f)
                        ) {
                            Text(
                                text = subject.semester,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = PrimaryForest,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    if (subject.description.isNotBlank()) {
                        Spacer(modifier = Modifier.height(Dimens.spaceMedium))
                        Text(
                            text = subject.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(Dimens.spaceStandard))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Course Instructor",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Prof. ${subject.instructor.ifBlank { "Department Staff" }}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Attendance Rate",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${subject.attendanceRate}%",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SoftSuccess
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(Dimens.spaceStandard))

                    // Target Progress
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Weekly Focus Commitment",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${totalMinutes / 60}h ${totalMinutes % 60}m / ${subject.weeklyTargetHours}h",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (progress >= 1f) SoftSuccess else PrimaryForest
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = subColor,
                        trackColor = SoftSage.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.height(Dimens.spaceLarge))

                    Button(
                        onClick = { onNavigateToTimer(subject.id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(Dimens.buttonHeight),
                        shape = RoundedCornerShape(Dimens.radiusMedium),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryForest),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(Dimens.iconMedium))
                        Spacer(modifier = Modifier.width(Dimens.spaceSmall))
                        Text(
                            text = "Start Focus Session for Course",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            // Study Sessions History Header directly on background
            item {
                SectionHeader(
                    title = "Study Session Logs",
                    badgeText = "${sessions.size} Sessions logged"
                )
            }

            if (sessions.isEmpty()) {
                item {
                    OrganicCard(
                        cornerRadius = Dimens.radiusMedium,
                        contentPadding = Dimens.spaceStandard
                    ) {
                        Text(
                            text = "No study sessions recorded yet for this course. Launch the Focus Timer to begin accumulating verified academic hours.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(sessions) { s ->
                    OrganicCard(
                        cornerRadius = Dimens.radiusMedium,
                        contentPadding = Dimens.spaceStandard
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(Dimens.radiusChip),
                                color = SoftSage.copy(alpha = 0.6f)
                            ) {
                                Text(
                                    text = "${s.durationMinutes} mins · ${s.sessionType.replace("_", " ").uppercase()}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryForest
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Text(
                                text = "Focus: ${s.focusScore}%",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SoftSuccess
                                )
                            )
                        }
                        if (s.notes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(Dimens.spaceSmall))
                            Text(
                                text = s.notes,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
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
