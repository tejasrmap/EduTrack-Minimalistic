package com.gtinnox.edutrack.ui.subjects

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.gtinnox.edutrack.ui.components.AcademicCard
import com.gtinnox.edutrack.ui.components.SubjectChip
import com.gtinnox.edutrack.ui.theme.AcademicIndigo
import com.gtinnox.edutrack.ui.theme.AcademicRose
import com.gtinnox.edutrack.ui.theme.AcademicSage
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

    val data = subjectWithSessions

    if (data == null) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Subject not found or loading...", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onNavigateBack) {
                Text("Go Back")
            }
        }
        return
    }

    val subject = data.subject
    val sessions = data.sessions
    val tasks = data.tasks
    val totalMinutes = sessions.sumOf { it.durationMinutes }
    val targetMinutes = (subject.weeklyTargetHours * 60).coerceAtLeast(1)
    val progress = (totalMinutes.toFloat() / targetMinutes.toFloat()).coerceIn(0f, 1f)

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Subject?") },
            text = { Text("Are you sure you want to delete '${subject.name}'? All associated study sessions and assignments will also be deleted.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteSubject(subject)
                        onNavigateBack()
                    }
                ) {
                    Text("Delete", color = AcademicRose)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App bar row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = "${subject.code} Overview",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { onNavigateToEdit(subject.id) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AcademicRose)
                }
            }
        }

        // Overview Header Card
        item {
            AcademicCard {
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
                    Text(
                        text = subject.semester,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = subject.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Instructor", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(subject.instructor, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Attendance", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${subject.attendanceRate}%", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold), color = AcademicSage)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Target progress
                Text(
                    text = "Weekly Study: ${totalMinutes / 60}h ${totalMinutes % 60}m / ${subject.weeklyTargetHours}h target",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = try { Color(android.graphics.Color.parseColor(subject.color)) } catch (e: Exception) { AcademicIndigo }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onNavigateToTimer(subject.id) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = AcademicIndigo),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Start Focus Session For This Subject")
                }
            }
        }

        // Study Sessions History
        item {
            Text(
                text = "Recorded Study Sessions (${sessions.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        if (sessions.isEmpty()) {
            item {
                AcademicCard {
                    Text(
                        text = "No study sessions recorded yet for this subject. Launch a Pomodoro or log your study time!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(sessions) { s ->
                AcademicCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${s.durationMinutes} Minutes · ${s.sessionType.replace("_", " ").uppercase()}",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = AcademicIndigo
                        )
                        Text(
                            text = "Score: ${s.focusScore}%",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                            color = AcademicSage
                        )
                    }
                    if (s.notes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
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
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
