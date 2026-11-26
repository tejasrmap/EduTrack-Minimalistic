package com.gtinnox.edutrack.ui.examinations

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gtinnox.edutrack.ui.components.AcademicCard
import com.gtinnox.edutrack.ui.components.SubjectChip
import com.gtinnox.edutrack.ui.theme.AcademicAmber
import com.gtinnox.edutrack.ui.theme.AcademicIndigo
import com.gtinnox.edutrack.ui.theme.AcademicPurple
import com.gtinnox.edutrack.ui.theme.AcademicRose
import com.gtinnox.edutrack.ui.theme.AcademicSage
import com.gtinnox.edutrack.ui.viewmodel.ExamViewModel
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate

@Composable
fun ExaminationsScreen(
    viewModel: ExamViewModel,
    modifier: Modifier = Modifier
) {
    val examsWithCountdown by viewModel.allExamsWithCountdown.collectAsState()
    val subjects by viewModel.subjects.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var subjectId by remember { mutableStateOf(subjects.firstOrNull()?.id ?: "") }
        var examDate by remember { mutableStateOf(LocalDate.now().plusDays(14).toString()) }
        var examTime by remember { mutableStateOf("09:00") }
        var location by remember { mutableStateOf("Auditorium Hall") }
        var targetGrade by remember { mutableStateOf("A") }
        var notes by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Register Examination") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Exam Name *") },
                        placeholder = { Text("e.g. Algorithms Midterm") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Text("Subject *", style = MaterialTheme.typography.labelSmall)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        subjects.take(4).forEach { s ->
                            FilterChip(
                                selected = subjectId == s.id,
                                onClick = { subjectId = s.id },
                                label = { Text(s.name) }
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = examDate,
                            onValueChange = { examDate = it },
                            label = { Text("Date (YYYY-MM-DD)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = examTime,
                            onValueChange = { examTime = it },
                            label = { Text("Time") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            label = { Text("Location") },
                            modifier = Modifier.weight(1.5f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = targetGrade,
                            onValueChange = { targetGrade = it },
                            label = { Text("Target") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Exam Focus & Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveExam(
                            id = null,
                            title = title,
                            subjectId = subjectId,
                            examDate = examDate,
                            examTime = examTime,
                            location = location,
                            targetGrade = targetGrade,
                            notes = notes
                        )
                        showAddDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AcademicIndigo)
                ) {
                    Text("Register Exam")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Examination Portal",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${examsWithCountdown.size} Upcoming Academic Assessments",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = { showAddDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = AcademicIndigo),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Exam")
                    }
                }
            }

            if (examsWithCountdown.isEmpty()) {
                item {
                    AcademicCard {
                        Text(
                            text = "No examinations registered. Add midterms, quizzes, and finals to start revision countdowns.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(examsWithCountdown) { item ->
                    val exam = item.exam
                    val subject = item.subject
                    val days = item.daysRemaining

                    val countdownColor = when {
                        days <= 3 -> AcademicRose
                        days <= 7 -> AcademicAmber
                        else -> AcademicIndigo
                    }

                    AcademicCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (subject != null) {
                                SubjectChip(name = subject.name, code = subject.code, colorHex = subject.color)
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = countdownColor.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = if (days >= 0) "$days Days Remaining" else "Completed",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = countdownColor,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = exam.title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "📍 ${exam.location} · ⏰ ${exam.examDate} at ${exam.examTime}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (exam.notes.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Focus: ${exam.notes}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Target Grade: ${exam.targetGrade}",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = AcademicSage
                            )

                            IconButton(onClick = { viewModel.deleteExam(exam) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AcademicRose, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
