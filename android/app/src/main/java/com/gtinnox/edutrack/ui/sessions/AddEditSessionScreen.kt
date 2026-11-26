package com.gtinnox.edutrack.ui.sessions

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.gtinnox.edutrack.ui.components.AcademicEditorialHeader
import com.gtinnox.edutrack.ui.components.OrganicCard
import com.gtinnox.edutrack.ui.theme.Dimens
import com.gtinnox.edutrack.ui.theme.PrimaryForest
import com.gtinnox.edutrack.ui.theme.SoftSage
import com.gtinnox.edutrack.ui.theme.SoftSuccess
import com.gtinnox.edutrack.ui.theme.adaptiveHorizontalPadding
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
    val horizontalPadding = adaptiveHorizontalPadding()

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
                    .padding(horizontal = horizontalPadding)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(Dimens.spaceStandard)
            ) {
                // Header directly on background
                AcademicEditorialHeader(
                    title = if (existing != null) "Edit Study Session" else "Log Study Session",
                    category = "Focus Record Log",
                    onBackClick = onNavigateBack
                )

                // Subject Selector Card
                OrganicCard(
                    cornerRadius = Dimens.radiusLarge,
                    contentPadding = Dimens.spaceLarge
                ) {
                    Text(
                        text = "Associated Course",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(Dimens.spaceSmall))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)) {
                        items(subjects) { sub ->
                            val isSelected = selectedSubjectId == sub.id
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedSubjectId = sub.id },
                                label = { Text(sub.name) },
                                shape = RoundedCornerShape(Dimens.radiusChip),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SoftSage,
                                    selectedLabelColor = PrimaryForest
                                )
                            )
                        }
                    }
                }

                // Duration & Type Card
                OrganicCard(
                    cornerRadius = Dimens.radiusLarge,
                    contentPadding = Dimens.spaceLarge
                ) {
                    Text(
                        text = "Study Duration & Mode",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(Dimens.spaceMedium))

                    OutlinedTextField(
                        value = durationText,
                        onValueChange = { durationText = it },
                        label = { Text("Duration (Minutes) *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(Dimens.radiusMedium),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryForest,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Spacer(modifier = Modifier.height(Dimens.spaceSmall))

                    // Quick duration pills
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(quickDurations) { d ->
                            val isCurrent = durationText == d.toString()
                            Surface(
                                shape = RoundedCornerShape(Dimens.radiusChip),
                                color = if (isCurrent) SoftSage else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable { durationText = d.toString() }
                            ) {
                                Text(
                                    text = "${d}m",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCurrent) PrimaryForest else MaterialTheme.colorScheme.onSurfaceVariant
                                    ),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(Dimens.spaceStandard))

                    Text(
                        text = "Activity Classification",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(Dimens.spaceSmall))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        sessionTypes.forEach { (typeKey, typeLabel) ->
                            val isSelected = sessionType == typeKey
                            Surface(
                                shape = RoundedCornerShape(Dimens.radiusSmall),
                                color = if (isSelected) SoftSage else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { sessionType = typeKey }
                            ) {
                                Text(
                                    text = typeLabel,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) PrimaryForest else MaterialTheme.colorScheme.onSurface
                                    ),
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                                )
                            }
                        }
                    }
                }

                // Focus Quality Slider Card
                OrganicCard(
                    cornerRadius = Dimens.radiusLarge,
                    contentPadding = Dimens.spaceLarge
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Focus Quality Score",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${focusScore.toInt()}%",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = SoftSuccess
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(Dimens.spaceSmall))

                    Slider(
                        value = focusScore,
                        onValueChange = { focusScore = it },
                        valueRange = 0f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = PrimaryForest,
                            activeTrackColor = PrimaryForest
                        )
                    )
                }

                // Reflection Notes Card
                OrganicCard(
                    cornerRadius = Dimens.radiusLarge,
                    contentPadding = Dimens.spaceLarge
                ) {
                    Text(
                        text = "Session Reflection & Notes",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(Dimens.spaceMedium))

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        placeholder = { Text("What did you accomplish? Any theorems or concepts reviewed?") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(Dimens.radiusMedium),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryForest,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        minLines = 3,
                        maxLines = 6
                    )
                }

                Button(
                    onClick = {
                        val mins = durationText.toIntOrNull() ?: 45
                        sessionViewModel.saveSession(
                            id = existing?.id,
                            subjectId = selectedSubjectId,
                            durationMinutes = mins,
                            sessionType = sessionType,
                            notes = notes,
                            focusScore = focusScore.toInt()
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(Dimens.buttonHeight),
                    shape = RoundedCornerShape(Dimens.radiusMedium),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryForest),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = if (existing != null) "Update Session" else "Save Study Block",
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(36.dp))
            }
        }
    }
}
