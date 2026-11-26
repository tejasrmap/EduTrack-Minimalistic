package com.gtinnox.edutrack.ui.examinations

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.School
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gtinnox.edutrack.ui.components.AcademicEditorialHeader
import com.gtinnox.edutrack.ui.components.AcademicEmptyState
import com.gtinnox.edutrack.ui.components.OrganicCard
import com.gtinnox.edutrack.ui.components.SubjectChip
import com.gtinnox.edutrack.ui.theme.Dimens
import com.gtinnox.edutrack.ui.theme.PrimaryForest
import com.gtinnox.edutrack.ui.theme.SoftDanger
import com.gtinnox.edutrack.ui.theme.SoftSage
import com.gtinnox.edutrack.ui.theme.SoftSuccess
import com.gtinnox.edutrack.ui.theme.SoftTerracotta
import com.gtinnox.edutrack.ui.theme.adaptiveHorizontalPadding
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
    val horizontalPadding = adaptiveHorizontalPadding()

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
            title = {
                Text(
                    text = "Register Examination",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Exam Title *") },
                        placeholder = { Text("e.g. Midterm Examination") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(Dimens.radiusMedium),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryForest,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Text("Course *", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
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

                    Row(horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)) {
                        OutlinedTextField(
                            value = examDate,
                            onValueChange = { examDate = it },
                            label = { Text("Date (YYYY-MM-DD)") },
                            modifier = Modifier.weight(1.2f),
                            shape = RoundedCornerShape(Dimens.radiusSmall)
                        )
                        OutlinedTextField(
                            value = examTime,
                            onValueChange = { examTime = it },
                            label = { Text("Time") },
                            modifier = Modifier.weight(0.8f),
                            shape = RoundedCornerShape(Dimens.radiusSmall)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)) {
                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            label = { Text("Hall / Room") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(Dimens.radiusSmall)
                        )
                        OutlinedTextField(
                            value = targetGrade,
                            onValueChange = { targetGrade = it },
                            label = { Text("Goal Grade") },
                            modifier = Modifier.weight(0.6f),
                            shape = RoundedCornerShape(Dimens.radiusSmall)
                        )
                    }

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Allowed materials / formulas") },
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
                            viewModel.saveExam(
                                id = null,
                                subjectId = subjectId,
                                title = title,
                                examDate = examDate,
                                examTime = examTime,
                                location = location,
                                targetGrade = targetGrade,
                                notes = notes
                            )
                            showAddDialog = false
                        }
                    },
                    shape = RoundedCornerShape(Dimens.radiusMedium),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryForest)
                ) {
                    Text("Register Exam", fontWeight = FontWeight.Bold)
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
                    title = "Examinations",
                    category = "Assessment Schedule",
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
                            Text("New Exam", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                )

                if (examsWithCountdown.isEmpty()) {
                    AcademicEmptyState(
                        icon = Icons.Default.School,
                        title = "No Registered Assessments",
                        description = "No examination dates scheduled yet. Stay organized by adding midterms and finals.",
                        actionText = "Add Examination",
                        onActionClick = { showAddDialog = true }
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(examsWithCountdown) { examItem ->
                            val exam = examItem.exam
                            val sub = subjects.find { it.id == exam.subjectId }
                            val daysLeft = examItem.daysRemaining

                            OrganicCard(
                                cornerRadius = Dimens.radiusMedium,
                                contentPadding = Dimens.spaceStandard
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Countdown Badge
                                    Surface(
                                        shape = RoundedCornerShape(Dimens.radiusSmall),
                                        color = if (daysLeft <= 3) SoftTerracotta else SoftSage,
                                        modifier = Modifier.size(54.dp)
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Text(
                                                text = if (daysLeft >= 0) "$daysLeft" else "Past",
                                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                                color = if (daysLeft <= 3) SoftDanger else PrimaryForest
                                            )
                                            Text(
                                                text = if (daysLeft >= 0) "days" else "",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (daysLeft <= 3) SoftDanger else PrimaryForest
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(Dimens.spaceMedium))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = exam.title,
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = MaterialTheme.colorScheme.onSurface,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f, fill = false)
                                            )
                                            Spacer(modifier = Modifier.width(Dimens.spaceSmall))
                                            Surface(
                                                shape = RoundedCornerShape(Dimens.radiusChip),
                                                color = SoftSage.copy(alpha = 0.6f)
                                            ) {
                                                Text(
                                                    text = "Target: ${exam.targetGrade}",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = PrimaryForest,
                                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(2.dp))

                                        Text(
                                            text = "${exam.examDate} · ${exam.examTime} · ${exam.location}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        if (sub != null) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            SubjectChip(name = sub.name, colorHex = sub.color, code = sub.code)
                                        }
                                    }

                                    IconButton(
                                        onClick = { viewModel.deleteExam(exam) },
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
