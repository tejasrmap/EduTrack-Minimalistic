package com.gtinnox.edutrack.ui.sessions

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gtinnox.edutrack.ui.components.SubjectChip
import com.gtinnox.edutrack.ui.theme.AcademicIndigo
import com.gtinnox.edutrack.ui.viewmodel.SessionUiEvent
import com.gtinnox.edutrack.ui.viewmodel.SessionViewModel
import com.gtinnox.edutrack.ui.viewmodel.SubjectViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AddEditSessionScreen(
    sessionId: String?,
    initialSubjectId: String?,
    sessionViewModel: SessionViewModel,
    subjectViewModel: SubjectViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val subjects by subjectViewModel.allSubjects.collectAsState()
    val allSessions by sessionViewModel.allSessionsWithSubject.collectAsState()

    val existing = remember(sessionId, allSessions) {
        allSessions.find { it.session.id == sessionId }?.session
    }

    var selectedSubjectId by remember(existing, initialSubjectId, subjects) {
        mutableStateOf(existing?.subjectId ?: initialSubjectId ?: subjects.firstOrNull()?.id ?: "")
    }
    var durationText by remember(existing) { mutableStateOf(existing?.durationMinutes?.toString() ?: "45") }
    var sessionType by remember(existing) { mutableStateOf(existing?.sessionType ?: "deep_work") }
    var notes by remember(existing) { mutableStateOf(existing?.notes ?: "") }
    var focusScore by remember(existing) { mutableFloatStateOf(existing?.focusScore?.toFloat() ?: 85f) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        sessionViewModel.eventFlow.collectLatest { event ->
            when (event) {
                is SessionUiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                is SessionUiEvent.SessionSaved -> onNavigateBack()
                else -> Unit
            }
        }
    }

    val sessionTypes = listOf(
        "pomodoro" to "Pomodoro Block",
        "deep_work" to "Deep Work",
        "revision" to "Exam Revision",
        "reading" to "Paper / Textbook Reading",
        "assignment" to "Problem Set / Assignment"
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = if (existing == null) "Log Study Session" else "Edit Study Session",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f)
                )
            }

            // Subject Picker
            Text("Select Academic Subject *", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                subjects.forEach { sub ->
                    val isSelected = selectedSubjectId == sub.id
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedSubjectId = sub.id },
                        label = { Text("${sub.code} · ${sub.name}") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AcademicIndigo.copy(alpha = 0.15f),
                            selectedLabelColor = AcademicIndigo
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            OutlinedTextField(
                value = durationText,
                onValueChange = { durationText = it.filter { ch -> ch.isDigit() } },
                label = { Text("Duration (Minutes) *") },
                placeholder = { Text("e.g. 45 or 90") },
                leadingIcon = { Icon(Icons.Default.Timer, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            // Session Type Chips
            Text("Session Modality", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                sessionTypes.forEach { (typeKey, label) ->
                    val isSelected = sessionType == typeKey
                    FilterChip(
                        selected = isSelected,
                        onClick = { sessionType = typeKey },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AcademicIndigo.copy(alpha = 0.15f),
                            selectedLabelColor = AcademicIndigo
                        )
                    )
                }
            }

            // Focus Score Slider
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Self-Assessed Focus Score", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold))
                    Text("${focusScore.toInt()}%", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = AcademicIndigo)
                }
                Slider(
                    value = focusScore,
                    onValueChange = { focusScore = it },
                    valueRange = 0f..100f,
                    steps = 19,
                    colors = SliderDefaults.colors(
                        thumbColor = AcademicIndigo,
                        activeTrackColor = AcademicIndigo
                    )
                )
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Session Reflections & Notes") },
                placeholder = { Text("What algorithms were derived, exercises completed, or obstacles solved?") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    val duration = durationText.toIntOrNull() ?: 0
                    sessionViewModel.saveSession(
                        id = existing?.id,
                        subjectId = selectedSubjectId,
                        durationMinutes = duration,
                        sessionType = sessionType,
                        notes = notes,
                        focusScore = focusScore.toInt(),
                        isManual = true
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AcademicIndigo),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (existing == null) "Record Study Entry" else "Update Entry",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
