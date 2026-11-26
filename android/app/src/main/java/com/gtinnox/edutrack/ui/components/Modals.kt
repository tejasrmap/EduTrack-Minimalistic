package com.gtinnox.edutrack.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.gtinnox.edutrack.data.local.entity.ExaminationEntity
import com.gtinnox.edutrack.data.local.entity.NoteEntity
import com.gtinnox.edutrack.data.local.entity.SubjectEntity
import com.gtinnox.edutrack.data.local.entity.TaskEntity
import com.gtinnox.edutrack.ui.theme.DeepForest
import com.gtinnox.edutrack.ui.theme.Dimens
import com.gtinnox.edutrack.ui.theme.PrimaryForest
import com.gtinnox.edutrack.ui.theme.SoftSage
import com.gtinnox.edutrack.ui.theme.SoftSuccess
import com.gtinnox.edutrack.ui.theme.SoftTerracotta
import com.gtinnox.edutrack.ui.theme.SoftWarning

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
        shape = RoundedCornerShape(topStart = Dimens.radiusPill, topEnd = Dimens.radiusPill),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = Dimens.maxDialogWidth)
                    .padding(horizontal = Dimens.spaceLarge, vertical = Dimens.spaceMedium)
            ) {
                Text(
                    text = "Quick Academic Entry",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Select an academic record to log or schedule",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(Dimens.spaceStandard))

                Column(verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)) {
                    QuickEntryRow(
                        title = "Log Study Session",
                        subtitle = "Record study minutes, subject focus, and notes",
                        icon = Icons.Default.Timer,
                        iconBg = SoftSage,
                        iconTint = PrimaryForest,
                        onClick = { onDismiss(); onAddSession() }
                    )

                    QuickEntryRow(
                        title = "Enroll New Course",
                        subtitle = "Add module, syllabus scope, and weekly quota",
                        icon = Icons.Default.School,
                        iconBg = SoftSage,
                        iconTint = DeepForest,
                        onClick = { onDismiss(); onAddSubject() }
                    )

                    QuickEntryRow(
                        title = "New Deliverable / Task",
                        subtitle = "Track homework, lab write-ups, or problem sets",
                        icon = Icons.Default.Assignment,
                        iconBg = SoftTerracotta,
                        iconTint = SoftWarning,
                        onClick = { onDismiss(); onAddTask() }
                    )

                    QuickEntryRow(
                        title = "Schedule Assessment",
                        subtitle = "Register midterms, finals, and quiz dates",
                        icon = Icons.Default.CalendarMonth,
                        iconBg = SoftTerracotta,
                        iconTint = PrimaryForest,
                        onClick = { onDismiss(); onAddExam() }
                    )

                    QuickEntryRow(
                        title = "Draft Revision Note",
                        subtitle = "Capture theorem proofs, formulas, or summaries",
                        icon = Icons.AutoMirrored.Filled.MenuBook,
                        iconBg = SoftSage,
                        iconTint = SoftSuccess,
                        onClick = { onDismiss(); onAddNote() }
                    )

                    QuickEntryRow(
                        title = "Schedule Study Block",
                        subtitle = "Allocate planned study block on timetable",
                        icon = Icons.Default.Edit,
                        iconBg = SoftSage,
                        iconTint = PrimaryForest,
                        onClick = { onDismiss(); onAddEvent() }
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

@Composable
private fun QuickEntryRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(Dimens.radiusMedium),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Dimens.spaceStandard, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(Dimens.iconButtonSize),
                shape = CircleShape,
                color = iconBg
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(Dimens.iconMedium)
                    )
                }
            }

            Spacer(modifier = Modifier.width(Dimens.spaceMedium))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
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
        subjects.filter {
            it.name.contains(query, ignoreCase = true) ||
            it.code.contains(query, ignoreCase = true)
        }
    } else emptyList()

    val matchedTasks = if (query.isNotBlank()) {
        tasks.filter { it.title.contains(query, ignoreCase = true) }
    } else emptyList()

    val matchedNotes = if (query.isNotBlank()) {
        notes.filter {
            it.title.contains(query, ignoreCase = true) ||
            it.content.contains(query, ignoreCase = true)
        }
    } else emptyList()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(Dimens.radiusXLarge),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = Dimens.maxDialogWidth)
                .padding(Dimens.spaceStandard)
        ) {
            Column(modifier = Modifier.padding(Dimens.spaceLarge)) {
                Text(
                    text = "Academic Search",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(Dimens.spaceMedium))

                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("Search subjects, deliverables, notes...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(Dimens.iconMedium)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Dimens.radiusMedium),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(Dimens.spaceMedium))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)
                ) {
                    if (query.isBlank()) {
                        item {
                            Text(
                                text = "Type course title, task name, or keywords to filter.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = Dimens.spaceMedium)
                            )
                        }
                    } else if (matchedSubjects.isEmpty() && matchedTasks.isEmpty() && matchedNotes.isEmpty()) {
                        item {
                            Text(
                                text = "No records matching '$query'.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = Dimens.spaceMedium)
                            )
                        }
                    }

                    if (matchedSubjects.isNotEmpty()) {
                        item {
                            Text("Courses", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = PrimaryForest)
                        }
                        items(matchedSubjects) { sub ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onDismiss(); onSelectSubject(sub.id) },
                                shape = RoundedCornerShape(Dimens.radiusChip),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(sub.code, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = PrimaryForest)
                                    Spacer(modifier = Modifier.width(Dimens.spaceSmall))
                                    Text(sub.name, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }
                    }

                    if (matchedTasks.isNotEmpty()) {
                        item {
                            Text("Deliverables", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = SoftWarning)
                        }
                        items(matchedTasks) { t ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onDismiss(); onSelectTask(t.id) },
                                shape = RoundedCornerShape(Dimens.radiusChip),
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(t.title, style = MaterialTheme.typography.bodyMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
