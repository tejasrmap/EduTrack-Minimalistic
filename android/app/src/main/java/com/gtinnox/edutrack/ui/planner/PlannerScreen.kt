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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.gtinnox.edutrack.data.local.entity.ScheduleEventEntity
import com.gtinnox.edutrack.data.local.entity.SubjectEntity
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
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

fun formatBlockDuration(startTime: String, endTime: String): String {
    return try {
        val start = LocalTime.parse(startTime)
        val end = LocalTime.parse(endTime)
        val minutes = ChronoUnit.MINUTES.between(start, end)
        if (minutes <= 0) return ""
        val hrs = minutes / 60
        val mins = minutes % 60
        if (hrs > 0 && mins > 0) "${hrs}h ${mins}m"
        else if (hrs > 0) "${hrs}h"
        else "${mins}m"
    } catch (e: Exception) {
        ""
    }
}

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
    var editingEvent by remember { mutableStateOf<ScheduleEventEntity?>(null) }
    var viewingEvent by remember { mutableStateOf<ScheduleEventEntity?>(null) }
    var deletingEvent by remember { mutableStateOf<ScheduleEventEntity?>(null) }

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

    // --- CREATE DIALOG ---
    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var subjectId by remember { mutableStateOf(subjects.firstOrNull()?.id ?: "") }
        var dateStr by remember { mutableStateOf(selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE)) }
        var startTime by remember { mutableStateOf("09:00") }
        var endTime by remember { mutableStateOf("10:30") }
        var desc by remember { mutableStateOf("") }
        var validationError by remember { mutableStateOf<String?>(null) }

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
                    if (validationError != null) {
                        Text(
                            text = validationError ?: "",
                            color = SoftDanger,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    OutlinedTextField(
                        value = title,
                        onValueChange = {
                            title = it
                            validationError = null
                        },
                        label = { Text("Block Title *") },
                        placeholder = { Text("e.g. Distributed Systems Seminar") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(Dimens.radiusMedium),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryForest,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Text(
                        "Target Subject *",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )

                    if (subjects.isEmpty()) {
                        Text(
                            "No subjects enrolled yet. Add a course first.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
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
                    }

                    OutlinedTextField(
                        value = dateStr,
                        onValueChange = { dateStr = it },
                        label = { Text("Date (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(Dimens.radiusMedium)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)) {
                        OutlinedTextField(
                            value = startTime,
                            onValueChange = {
                                startTime = it
                                validationError = null
                            },
                            label = { Text("Start (HH:mm)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(Dimens.radiusSmall)
                        )
                        OutlinedTextField(
                            value = endTime,
                            onValueChange = {
                                endTime = it
                                validationError = null
                            },
                            label = { Text("End (HH:mm)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(Dimens.radiusSmall)
                        )
                    }

                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Location / Notes (Optional)") },
                        placeholder = { Text("e.g. Hall 402 / Practice problem set") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(Dimens.radiusMedium)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isBlank()) {
                            validationError = "Title is required"
                            return@Button
                        }
                        if (subjectId.isBlank()) {
                            validationError = "Please select a subject"
                            return@Button
                        }
                        if (startTime.isNotBlank() && endTime.isNotBlank() && startTime >= endTime) {
                            validationError = "End time must be after start time"
                            return@Button
                        }
                        viewModel.addEvent(
                            title = title,
                            subjectId = subjectId,
                            date = dateStr,
                            startTime = startTime,
                            endTime = endTime,
                            description = desc
                        )
                        showAddDialog = false
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

    // --- UPDATE / EDIT DIALOG ---
    editingEvent?.let { event ->
        var title by remember(event.id) { mutableStateOf(event.title) }
        var subjectId by remember(event.id) { mutableStateOf(event.subjectId) }
        var dateStr by remember(event.id) { mutableStateOf(event.date) }
        var startTime by remember(event.id) { mutableStateOf(event.startTime) }
        var endTime by remember(event.id) { mutableStateOf(event.endTime) }
        var desc by remember(event.id) { mutableStateOf(event.description) }
        var validationError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { editingEvent = null },
            title = {
                Text(
                    text = "Edit Scheduled Block",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)) {
                    if (validationError != null) {
                        Text(
                            text = validationError ?: "",
                            color = SoftDanger,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    OutlinedTextField(
                        value = title,
                        onValueChange = {
                            title = it
                            validationError = null
                        },
                        label = { Text("Block Title *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(Dimens.radiusMedium),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryForest,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Text(
                        "Target Subject *",
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

                    OutlinedTextField(
                        value = dateStr,
                        onValueChange = { dateStr = it },
                        label = { Text("Date (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(Dimens.radiusMedium)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)) {
                        OutlinedTextField(
                            value = startTime,
                            onValueChange = {
                                startTime = it
                                validationError = null
                            },
                            label = { Text("Start (HH:mm)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(Dimens.radiusSmall)
                        )
                        OutlinedTextField(
                            value = endTime,
                            onValueChange = {
                                endTime = it
                                validationError = null
                            },
                            label = { Text("End (HH:mm)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(Dimens.radiusSmall)
                        )
                    }

                    OutlinedTextField(
                        value = desc,
                        onValueChange = { desc = it },
                        label = { Text("Location / Notes") },
                        placeholder = { Text("e.g. Hall 402 / Review session") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(Dimens.radiusMedium)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isBlank()) {
                            validationError = "Title is required"
                            return@Button
                        }
                        if (subjectId.isBlank()) {
                            validationError = "Please select a subject"
                            return@Button
                        }
                        if (startTime.isNotBlank() && endTime.isNotBlank() && startTime >= endTime) {
                            validationError = "End time must be after start time"
                            return@Button
                        }
                        viewModel.updateEvent(
                            id = event.id,
                            title = title,
                            subjectId = subjectId,
                            date = dateStr,
                            startTime = startTime,
                            endTime = endTime,
                            description = desc,
                            isCompleted = event.isCompleted,
                            createdAt = event.createdAt
                        )
                        editingEvent = null
                    },
                    shape = RoundedCornerShape(Dimens.radiusMedium),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryForest)
                ) {
                    Text("Save Changes", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { editingEvent = null }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(Dimens.radiusLarge)
        )
    }

    // --- READ / FULL DETAILS DIALOG ---
    viewingEvent?.let { event ->
        val sub = subjects.find { it.id == event.subjectId }
        val durationStr = formatBlockDuration(event.startTime, event.endTime)

        AlertDialog(
            onDismissRequest = { viewingEvent = null },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Schedule Details",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Surface(
                        shape = RoundedCornerShape(Dimens.radiusChip),
                        color = if (event.isCompleted) SoftSuccess.copy(alpha = 0.2f) else SoftSage
                    ) {
                        Text(
                            text = if (event.isCompleted) "Completed" else "Scheduled",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (event.isCompleted) SoftSuccess else PrimaryForest
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)) {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    if (sub != null) {
                        SubjectChip(name = sub.name, colorHex = sub.color, code = sub.code)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = PrimaryForest,
                            modifier = Modifier.size(Dimens.iconSmall)
                        )
                        Spacer(modifier = Modifier.width(Dimens.spaceSmall))
                        Text(
                            text = event.date,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = PrimaryForest,
                            modifier = Modifier.size(Dimens.iconSmall)
                        )
                        Spacer(modifier = Modifier.width(Dimens.spaceSmall))
                        Text(
                            text = "${event.startTime} – ${event.endTime}${if (durationStr.isNotBlank()) " ($durationStr)" else ""}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = PrimaryForest
                        )
                    }

                    if (event.description.isNotBlank()) {
                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(Dimens.iconSmall)
                            )
                            Spacer(modifier = Modifier.width(Dimens.spaceSmall))
                            Text(
                                text = event.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)) {
                    Button(
                        onClick = {
                            val target = event
                            viewingEvent = null
                            editingEvent = target
                        },
                        shape = RoundedCornerShape(Dimens.radiusMedium),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryForest)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(Dimens.iconSmall))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Edit")
                    }
                    OutlinedButton(
                        onClick = {
                            val target = event
                            viewingEvent = null
                            deletingEvent = target
                        },
                        shape = RoundedCornerShape(Dimens.radiusMedium),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SoftDanger)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(Dimens.iconSmall))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Delete")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { viewingEvent = null }) {
                    Text("Close")
                }
            },
            shape = RoundedCornerShape(Dimens.radiusLarge)
        )
    }

    // --- DELETE CONFIRMATION DIALOG ---
    deletingEvent?.let { event ->
        AlertDialog(
            onDismissRequest = { deletingEvent = null },
            title = {
                Text(
                    text = "Delete Scheduled Block?",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to remove '${event.title}' scheduled on ${event.date} (${event.startTime} - ${event.endTime})? This action cannot be undone."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteEvent(event)
                        deletingEvent = null
                    }
                ) {
                    Text("Delete Block", color = SoftDanger, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingEvent = null }) {
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
                // Header Row directly on background — Unified single primary Add Block button
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
                                contentPadding = Dimens.spaceStandard,
                                onClick = { viewingEvent = event }
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Complete / Incomplete Checkbox — Isolated click to avoid card opening
                                    IconButton(
                                        onClick = { viewModel.toggleEvent(event) },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                            contentDescription = if (isDone) "Mark Incomplete" else "Mark Complete",
                                            tint = if (isDone) SoftSuccess else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(Dimens.iconStandard)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(Dimens.spaceSmall))

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

                                    Spacer(modifier = Modifier.width(Dimens.spaceXSmall))

                                    // Edit Action
                                    IconButton(
                                        onClick = { editingEvent = event },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit Event",
                                            tint = PrimaryForest,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    // Delete Action
                                    IconButton(
                                        onClick = { deletingEvent = event },
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
