package com.gtinnox.edutrack.ui.assignments

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import com.gtinnox.edutrack.ui.theme.AcademicRose
import com.gtinnox.edutrack.ui.theme.AcademicSage
import com.gtinnox.edutrack.ui.viewmodel.AssignmentViewModel
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate

@Composable
fun AssignmentsScreen(
    viewModel: AssignmentViewModel,
    modifier: Modifier = Modifier
) {
    val tasks by viewModel.filteredTasks.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    val filterStatus by viewModel.filterStatus.collectAsState()
    val filterSubjectId by viewModel.filterSubjectId.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var subjectId by remember { mutableStateOf(subjects.firstOrNull()?.id ?: "") }
        var category by remember { mutableStateOf("assignment") }
        var priority by remember { mutableStateOf("medium") }
        var dueDate by remember { mutableStateOf(LocalDate.now().plusDays(3).toString()) }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("New Academic Assignment") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Deliverable Title *") },
                        placeholder = { Text("e.g. Dynamic Programming Problem Set") },
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
                            value = dueDate,
                            onValueChange = { dueDate = it },
                            label = { Text("Due Date (YYYY-MM-DD)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Requirements & Rubric") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveTask(
                            id = null,
                            title = title,
                            description = description,
                            subjectId = subjectId,
                            category = category,
                            priority = priority,
                            dueDate = dueDate
                        )
                        showAddDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AcademicIndigo)
                ) {
                    Text("Add Assignment")
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
                            text = "Deliverables Hub",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${tasks.size} Academic Tasks Tracked",
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
                        Text("Add Task")
                    }
                }
            }

            // Status Filter Chips
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("all" to "All Deliverables", "pending" to "Pending", "completed" to "Completed").forEach { (key, label) ->
                        val isSelected = filterStatus == key
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setFilterStatus(key) },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AcademicIndigo.copy(alpha = 0.15f),
                                selectedLabelColor = AcademicIndigo
                            )
                        )
                    }
                }
            }

            if (tasks.isEmpty()) {
                item {
                    AcademicCard {
                        Text(
                            text = "No deliverables in this category.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(tasks) { task ->
                    val subject = subjects.find { it.id == task.subjectId }
                    val isCompleted = task.status == "completed"

                    val priorityColor = when (task.priority) {
                        "high" -> AcademicRose
                        "medium" -> AcademicAmber
                        else -> AcademicSage
                    }

                    AcademicCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = "Toggle",
                                tint = if (isCompleted) AcademicSage else AcademicAmber,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable { viewModel.toggleTask(task) }
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (subject != null) {
                                        SubjectChip(name = subject.name.take(12), code = subject.code, colorHex = subject.color)
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = priorityColor.copy(alpha = 0.12f)
                                    ) {
                                        Text(
                                            text = task.priority.uppercase(),
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = priorityColor,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = task.title,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        textDecoration = if (isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                                    ),
                                    color = if (isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                                )

                                if (task.description.isNotBlank()) {
                                    Text(
                                        text = task.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 2
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "📅 Due: ${task.dueDate} · Category: ${task.category}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            IconButton(onClick = { viewModel.deleteTask(task) }) {
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
