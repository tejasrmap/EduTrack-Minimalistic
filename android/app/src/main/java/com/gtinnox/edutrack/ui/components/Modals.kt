package com.gtinnox.edutrack.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.gtinnox.edutrack.data.local.entity.ExaminationEntity
import com.gtinnox.edutrack.data.local.entity.NoteEntity
import com.gtinnox.edutrack.data.local.entity.SubjectEntity
import com.gtinnox.edutrack.data.local.entity.TaskEntity
import com.gtinnox.edutrack.ui.theme.AcademicAmber
import com.gtinnox.edutrack.ui.theme.AcademicIndigo
import com.gtinnox.edutrack.ui.theme.AcademicPurple
import com.gtinnox.edutrack.ui.theme.AcademicSage
import com.gtinnox.edutrack.ui.theme.AcademicSky

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickAddBottomSheet(
    onDismiss: () -> Unit,
    onAddSession: () -> Unit,
    onAddSubject: () -> Unit,
    onAddTask: () -> Unit,
    onAddExam: () -> Unit,
    onAddNote: () -> Unit,
    onAddEvent: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Quick Academic Entry",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Select an academic record to create",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            QuickAddOption(
                title = "Log Study Session",
                subtitle = "Record study minutes, subject focus, and notes",
                icon = Icons.Default.Timer,
                iconColor = AcademicIndigo,
                onClick = { onDismiss(); onAddSession() }
            )

            QuickAddOption(
                title = "Add Subject Course",
                subtitle = "Enroll a new academic module, code & weekly target",
                icon = Icons.AutoMirrored.Filled.MenuBook,
                iconColor = AcademicSage,
                onClick = { onDismiss(); onAddSubject() }
            )

            QuickAddOption(
                title = "Create Task / Assignment",
                subtitle = "Set homework, lab report, or problem set due date",
                icon = Icons.Default.Assignment,
                iconColor = AcademicAmber,
                onClick = { onDismiss(); onAddTask() }
            )

            QuickAddOption(
                title = "Schedule Examination",
                subtitle = "Track midterm, final, or quiz with countdown",
                icon = Icons.Default.School,
                iconColor = AcademicPurple,
                onClick = { onDismiss(); onAddExam() }
            )

            QuickAddOption(
                title = "Write Revision Note",
                subtitle = "Record summary, formula derivations, and tags",
                icon = Icons.Default.Edit,
                iconColor = AcademicSky,
                onClick = { onDismiss(); onAddNote() }
            )

            QuickAddOption(
                title = "Add Schedule Event",
                subtitle = "Lecture, lab seminar, or office hours slot",
                icon = Icons.Default.CalendarMonth,
                iconColor = AcademicIndigo,
                onClick = { onDismiss(); onAddEvent() }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun QuickAddOption(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    AcademicCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = iconColor.copy(alpha = 0.12f),
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun GlobalSearchDialog(
    subjects: List<SubjectEntity>,
    tasks: List<TaskEntity>,
    exams: List<ExaminationEntity>,
    notes: List<NoteEntity>,
    onDismiss: () -> Unit,
    onSelectSubject: (String) -> Unit,
    onSelectTask: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }

    val matchedSubjects = if (query.isNotBlank()) {
        subjects.filter { it.name.contains(query, ignoreCase = true) || it.code.contains(query, ignoreCase = true) }
    } else emptyList()

    val matchedTasks = if (query.isNotBlank()) {
        tasks.filter { it.title.contains(query, ignoreCase = true) }
    } else emptyList()

    val matchedExams = if (query.isNotBlank()) {
        exams.filter { it.title.contains(query, ignoreCase = true) }
    } else emptyList()

    val matchedNotes = if (query.isNotBlank()) {
        notes.filter { it.title.contains(query, ignoreCase = true) || it.content.contains(query, ignoreCase = true) }
    } else emptyList()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Global Academic Search",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Search subjects, tasks, exams, notes...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(modifier = Modifier.height(300.dp)) {
                    if (query.isBlank()) {
                        item {
                            Text(
                                text = "Type keyword above to search academic records.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        }
                    } else if (matchedSubjects.isEmpty() && matchedTasks.isEmpty() && matchedExams.isEmpty() && matchedNotes.isEmpty()) {
                        item {
                            Text(
                                text = "No records found matching '$query'",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        }
                    }

                    if (matchedSubjects.isNotEmpty()) {
                        item {
                            Text(
                                text = "Subjects (${matchedSubjects.size})",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = AcademicIndigo
                            )
                        }
                        items(matchedSubjects) { sub ->
                            Text(
                                text = "${sub.code} · ${sub.name}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onDismiss(); onSelectSubject(sub.id) }
                                    .padding(vertical = 8.dp)
                            )
                        }
                    }

                    if (matchedTasks.isNotEmpty()) {
                        item {
                            Text(
                                text = "Assignments & Tasks (${matchedTasks.size})",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = AcademicSage,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                        items(matchedTasks) { t ->
                            Text(
                                text = "• ${t.title} (Due: ${t.dueDate})",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onDismiss(); onSelectTask(t.id) }
                                    .padding(vertical = 6.dp)
                            )
                        }
                    }

                    if (matchedExams.isNotEmpty()) {
                        item {
                            Text(
                                text = "Examinations (${matchedExams.size})",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = AcademicAmber,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                        items(matchedExams) { ex ->
                            Text(
                                text = "• ${ex.title} (Date: ${ex.examDate})",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                    }

                    if (matchedNotes.isNotEmpty()) {
                        item {
                            Text(
                                text = "Revision Notes (${matchedNotes.size})",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = AcademicPurple,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                        items(matchedNotes) { n ->
                            Text(
                                text = "• ${n.title}",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
