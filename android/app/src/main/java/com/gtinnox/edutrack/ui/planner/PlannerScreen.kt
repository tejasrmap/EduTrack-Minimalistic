package com.gtinnox.edutrack.ui.planner

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gtinnox.edutrack.ui.components.AcademicEditorialHeader
import com.gtinnox.edutrack.ui.components.AcademicEmptyState
import com.gtinnox.edutrack.ui.components.OrganicCard
import com.gtinnox.edutrack.ui.components.SubjectChip
import com.gtinnox.edutrack.ui.theme.Dimens
import com.gtinnox.edutrack.ui.theme.PrimaryForest
import com.gtinnox.edutrack.ui.theme.SoftDanger
import com.gtinnox.edutrack.ui.theme.SoftSage
import com.gtinnox.edutrack.ui.theme.SoftSuccess
import com.gtinnox.edutrack.ui.theme.adaptiveHorizontalPadding
import com.gtinnox.edutrack.ui.viewmodel.PlannerViewModel
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun PlannerScreen(
    viewModel: PlannerViewModel,
    modifier: Modifier = Modifier
) {
    val selectedDateStr by viewModel.selectedDate.collectAsState()
    val selectedDate = remember(selectedDateStr) {
        try { LocalDate.parse(selectedDateStr) } catch (e: Exception) { LocalDate.now() }
    }
    val events by viewModel.eventsForSelectedDate.collectAsState()
    val subjects by viewModel.subjects.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val horizontalPadding = adaptiveHorizontalPadding()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    // 14 days horizontal agenda strip
    val today = LocalDate.now()
    val days = (0..13).map { today.plusDays(it.toLong()) }

    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var subjectId by remember { mutableStateOf(subjects.firstOrNull()?.id ?: "") }
        var startTime by remember { mutableStateOf("09:00") }
        var endTime by remember { mutableStateOf("10:30") }
        var desc by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(
                    text = "Schedule Study Block",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Block Title *") },
                        placeholder = { Text("e.g. Distributed Systems Lecture") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(Dimens.radiusMedium),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryForest,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Text(
                        "Target Subject",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )

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
                            value = startTime,
                            onValueChange = { startTime = it },
                            label = { Text("Start") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(Dimens.radiusSmall)
                        )
                        OutlinedTextField(
                            value = endTime,
                            onValueChange = { endTime = it },
                            label = { Text("End") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(Dimens.radiusSmall)
                        )
                    }

                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Location / Chapter") },
                        placeholder = { Text("Hall 402 / Review Lab") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(Dimens.radiusMedium)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            viewModel.addEvent(
                                title = title,
                                subjectId = subjectId,
                                date = selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                                startTime = startTime,
                                endTime = endTime,
                                description = desc
                            )
                            showAddDialog = false
                        }
                    },
                    shape = RoundedCornerShape(Dimens.radiusMedium),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryForest)
                ) {
                    Text("Schedule Block", fontWeight = FontWeight.Bold)
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
                // Header Row directly on background
                AcademicEditorialHeader(
                    title = selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMM d")),
                    category = "Academic Timetable",
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
                            Text("Add Block", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                )

                // Horizontal Date Strip (14 Days)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(days) { date ->
                        val isSelected = date == selectedDate
                        val isToday = date == today

                        Surface(
                            modifier = Modifier
                                .width(62.dp)
                                .clickable { viewModel.setDate(date.toString()) },
                            shape = RoundedCornerShape(Dimens.radiusMedium),
                            color = if (isSelected) PrimaryForest else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) PrimaryForest else MaterialTheme.colorScheme.outline
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = date.format(DateTimeFormatter.ofPattern("EEE")).uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    ),
                                    color = if (isSelected) Color.White.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = date.dayOfMonth.toString(),
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                                if (isToday) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(5.dp)
                                            .background(
                                                if (isSelected) Color.White else PrimaryForest,
                                                CircleShape
                                            )
                                    )
                                }
                            }
                        }
                    }
                }

                // Schedule Timeline Blocks
                if (events.isEmpty()) {
                    AcademicEmptyState(
                        icon = Icons.Default.CalendarMonth,
                        title = "No Scheduled Blocks",
                        description = "Nothing scheduled for ${selectedDate.format(DateTimeFormatter.ofPattern("MMM d"))}. Add a lecture, seminar, or study slot.",
                        actionText = "Schedule Event",
                        onActionClick = { showAddDialog = true }
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(events) { event ->
                            val sub = subjects.find { it.id == event.subjectId }
                            val isDone = event.isCompleted

                            OrganicCard(
                                cornerRadius = Dimens.radiusMedium,
                                contentPadding = Dimens.spaceStandard
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Complete / Incomplete Checkbox
                                    Icon(
                                        imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = "Toggle Complete",
                                        tint = if (isDone) SoftSuccess else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier
                                            .size(Dimens.iconStandard)
                                            .clickable { viewModel.toggleEvent(event) }
                                    )

                                    Spacer(modifier = Modifier.width(Dimens.spaceMedium))

                                    // Time Indicator
                                    Column(modifier = Modifier.width(76.dp)) {
                                        Text(
                                            text = event.startTime,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = if (isDone) MaterialTheme.colorScheme.onSurfaceVariant else PrimaryForest
                                        )
                                        Text(
                                            text = event.endTime,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(Dimens.spaceSmall))

                                    // Content Details
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = event.title,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
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

                                        if (sub != null) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            SubjectChip(name = sub.name, colorHex = sub.color, code = sub.code)
                                        }
                                    }

                                    // Delete Action
                                    IconButton(
                                        onClick = { viewModel.deleteEvent(event) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Event",
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
