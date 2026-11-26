package com.gtinnox.edutrack.ui.assignments

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
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
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gtinnox.edutrack.ui.components.AcademicEditorialHeader
import com.gtinnox.edutrack.ui.components.AcademicEmptyState
import com.gtinnox.edutrack.ui.components.OrganicCard
import com.gtinnox.edutrack.ui.components.PriorityChip
import com.gtinnox.edutrack.ui.components.SubjectChip
import com.gtinnox.edutrack.ui.theme.Dimens
import com.gtinnox.edutrack.ui.theme.PrimaryForest
import com.gtinnox.edutrack.ui.theme.SoftDanger
import com.gtinnox.edutrack.ui.theme.SoftSage
import com.gtinnox.edutrack.ui.theme.SoftSuccess
import com.gtinnox.edutrack.ui.theme.adaptiveHorizontalPadding
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
    val horizontalPadding = adaptiveHorizontalPadding()

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
            title = {
                Text(
                    text = "New Academic Deliverable",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Deliverable Title *") },
                        placeholder = { Text("e.g. Dynamic Programming Problem Set") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(Dimens.radiusMedium),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryForest,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Text("Subject *", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(subjects) { s ->
                            FilterChip(
                                selected = subjectId == s.id,
                                onClick = { subjectId = s.id },
                                label = { Text(s.code.ifBlank { s.name }) },
                                shape = RoundedCornerShape(Dimens.radiusChip),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SoftSage,
                                    selectedLabelColor = PrimaryForest
                                )
                            )
                        }
                    }

                    OutlinedTextField(
                        value = dueDate,
                        onValueChange = { dueDate = it },
                        label = { Text("Due Date (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(Dimens.radiusSmall)
                    )

                    Text("Priority Level", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("low", "medium", "urgent").forEach { p ->
                            FilterChip(
                                selected = priority == p,
                                onClick = { priority = p },
                                label = { Text(p.replaceFirstChar { it.uppercase() }) },
                                shape = RoundedCornerShape(Dimens.radiusChip)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Notes & Requirements") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(Dimens.radiusSmall),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            viewModel.saveTask(
                                id = null,
                                subjectId = subjectId,
                                title = title,
                                description = description,
                                dueDate = dueDate,
                                priority = priority,
                                category = category
                            )
                            showAddDialog = false
                        }
                    },
                    shape = RoundedCornerShape(Dimens.radiusMedium),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryForest)
                ) {
                    Text("Save Task", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(Dimens.radiusLarge)
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent,
        modifier = modifier
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = Dimens.maxContentWidth)
                    .padding(horizontal = horizontalPadding),
                verticalArrangement = Arrangement.spacedBy(Dimens.spaceStandard)
            ) {
                // Header directly on background
                AcademicEditorialHeader(
                    title = "Assignments",
                    category = "Course Deliverables",
                    trailingAction = {
                        Button(
                            onClick = { showAddDialog = true },
                            shape = RoundedCornerShape(Dimens.radiusMedium),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryForest),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(Dimens.iconSmall))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("New Task", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                )

                // Status Filter Tabs
                val tabs = listOf("all" to "All Tasks", "pending" to "Pending", "completed" to "Completed")
                val currentTabIndex = when (filterStatus) {
                    "pending" -> 1
                    "completed" -> 2
                    else -> 0
                }

                Surface(
                    shape = RoundedCornerShape(Dimens.radiusPill),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                    ) {
                        tabs.forEachIndexed { index, (statusVal, label) ->
                            val isSelected = currentTabIndex == index
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.setFilterStatus(statusVal) },
                                shape = RoundedCornerShape(Dimens.radiusMedium),
                                color = if (isSelected) SoftSage else Color.Transparent
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    ),
                                    color = if (isSelected) PrimaryForest else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // Subject Filter Strip
                if (subjects.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FilterChip(
                                selected = filterSubjectId == null,
                                onClick = { viewModel.setFilterSubject(null) },
                                label = { Text("All Subjects") },
                                shape = RoundedCornerShape(Dimens.radiusChip),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryForest,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                        items(subjects) { s ->
                            FilterChip(
                                selected = filterSubjectId == s.id,
                                onClick = { viewModel.setFilterSubject(if (filterSubjectId == s.id) null else s.id) },
                                label = { Text(s.code.ifBlank { s.name }) },
                                shape = RoundedCornerShape(Dimens.radiusChip),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SoftSage,
                                    selectedLabelColor = PrimaryForest
                                )
                            )
                        }
                    }
                }

                if (tasks.isEmpty()) {
                    AcademicEmptyState(
                        icon = Icons.Default.Assignment,
                        title = "No Deliverables Found",
                        description = "No assignment tasks matching this filter. Stay ahead by scheduling problem sets.",
                        actionText = "Add Deliverable",
                        onActionClick = { showAddDialog = true }
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(tasks) { task ->
                            val sub = subjects.find { it.id == task.subjectId }
                            val isDone = task.status == "completed"

                            OrganicCard(
                                cornerRadius = Dimens.radiusMedium,
                                contentPadding = Dimens.spaceStandard
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = "Toggle Complete",
                                        tint = if (isDone) SoftSuccess else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier
                                            .size(Dimens.iconStandard)
                                            .clickable { viewModel.toggleTask(task) }
                                    )

                                    Spacer(modifier = Modifier.width(Dimens.spaceMedium))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = task.title,
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = if (isDone) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f, fill = false)
                                            )
                                            Spacer(modifier = Modifier.width(Dimens.spaceSmall))
                                            PriorityChip(priority = task.priority)
                                        }

                                        if (task.description.isNotBlank()) {
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = task.description,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            if (sub != null) {
                                                SubjectChip(name = sub.name, colorHex = sub.color, code = sub.code)
                                            } else {
                                                Box {}
                                            }

                                            Text(
                                                text = "Due: ${task.dueDate}",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = { viewModel.deleteTask(task) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = SoftDanger.copy(alpha = 0.7f),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(84.dp))
                        }
                    }
                }
            }
        }
    }
}
