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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gtinnox.edutrack.ui.components.PremiumCard
import com.gtinnox.edutrack.ui.components.SubjectChip
import com.gtinnox.edutrack.ui.theme.EduTrackPrimary
import com.gtinnox.edutrack.ui.theme.EduTrackPrimarySoft
import com.gtinnox.edutrack.ui.theme.EduTrackSuccess
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

    val quickDurations = listOf(15, 25, 45, 60, 90, 120)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent,
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = if (existing != null) "Edit Study Session" else "Log Study Session",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.weight(1f)
                )
            }

            // Subject Selector Card
            PremiumCard(contentPadding = 18.dp) {
                Text(
                    text = "Associated Course",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(subjects) { sub ->
                        val isSelected = selectedSubjectId == sub.id
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedSubjectId = sub.id },
                            label = { Text(sub.name) },
                            shape = RoundedCornerShape(10.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EduTrackPrimarySoft,
                                selectedLabelColor = EduTrackPrimary
                            )
                        )
                    }
                }
            }

            // Duration & Type Card
            PremiumCard(contentPadding = 18.dp) {
                Text(
                    text = "Study Duration & Mode",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = durationText,
                    onValueChange = { durationText = it },
                    label = { Text("Duration (Minutes) *") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Quick duration pills
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(quickDurations) { d ->
                        val isCurrent = durationText == d.toString()
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isCurrent) EduTrackPrimarySoft else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.clickable { durationText = d.toString() }
                        ) {
                            Text(
                                text = "${d}m",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCurrent) EduTrackPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Activity Classification",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    sessionTypes.forEach { (typeKey, typeLabel) ->
                        val isSelected = sessionType == typeKey
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) EduTrackPrimarySoft else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { sessionType = typeKey }
                        ) {
                            Text(
                                text = typeLabel,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) EduTrackPrimary else MaterialTheme.colorScheme.onSurface
                                ),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                            )
                        }
                    }
                }
            }

            // Focus Quality Score
            PremiumCard(contentPadding = 18.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Focus Score",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${focusScore.toInt()}%",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = EduTrackSuccess
                        )
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Slider(
                    value = focusScore,
                    onValueChange = { focusScore = it },
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = EduTrackPrimary,
                        activeTrackColor = EduTrackPrimary
                    )
                )
            }

            // Reflection Notes
            PremiumCard(contentPadding = 18.dp) {
                Text(
                    text = "Session Reflection & Completed Work",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = { Text("Summary of pages read, problems solved, or derivations proved...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 3
                )
            }

            // Save Action
            Button(
                onClick = {
                    sessionViewModel.saveSession(
                        id = sessionId,
                        subjectId = selectedSubjectId,
                        durationMinutes = durationText.toIntOrNull() ?: 45,
                        sessionType = sessionType,
                        notes = notes,
                        focusScore = focusScore.toInt()
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EduTrackPrimary)
            ) {
                Text(
                    text = if (existing != null) "Update Session Record" else "Log Study Session",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
