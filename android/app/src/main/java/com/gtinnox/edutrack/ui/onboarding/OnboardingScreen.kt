package com.gtinnox.edutrack.ui.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gtinnox.edutrack.ui.components.EduTrackPermissionExplanation
import com.gtinnox.edutrack.ui.components.OrganicCard
import com.gtinnox.edutrack.ui.theme.Dimens
import com.gtinnox.edutrack.ui.theme.PrimaryForest
import com.gtinnox.edutrack.ui.theme.SoftDanger
import com.gtinnox.edutrack.ui.theme.SoftSage
import com.gtinnox.edutrack.ui.theme.SoftSuccess
import com.gtinnox.edutrack.ui.theme.adaptiveHorizontalPadding
import com.gtinnox.edutrack.ui.viewmodel.OnboardingViewModel
import com.gtinnox.edutrack.ui.viewmodel.SubjectDraft

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val horizontalPadding = adaptiveHorizontalPadding()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
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
                    .widthIn(max = 520.dp)
                    .padding(horizontal = horizontalPadding),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Progress Bar & Header
                Column(modifier = Modifier.padding(top = Dimens.spaceMedium)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (state.currentStep > 1) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.size(Dimens.iconButtonSize)
                            ) {
                                IconButton(onClick = { viewModel.prevStep() }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.size(Dimens.iconMedium)
                                    )
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.size(Dimens.iconButtonSize))
                        }

                        Surface(
                            shape = RoundedCornerShape(Dimens.radiusChip),
                            color = SoftSage.copy(alpha = 0.7f)
                        ) {
                            Text(
                                text = "STEP ${state.currentStep} OF ${state.totalSteps}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = PrimaryForest
                                ),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }

                        if (state.currentStep in 2..6) {
                            TextButton(onClick = { viewModel.nextStep() }) {
                                Text("Skip", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        } else {
                            Spacer(modifier = Modifier.width(48.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(Dimens.spaceSmall))

                    LinearProgressIndicator(
                        progress = { state.currentStep.toFloat() / state.totalSteps.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        color = PrimaryForest,
                        trackColor = SoftSage
                    )
                }

                // Step Content (Animated)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = Dimens.spaceMedium)
                ) {
                    AnimatedContent(
                        targetState = state.currentStep,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "OnboardingStep"
                    ) { step ->
                        when (step) {
                            1 -> Step1PersonalProfile(viewModel, state)
                            2 -> Step2AcademicInfo(viewModel, state)
                            3 -> Step3SubjectSetup(viewModel, state)
                            4 -> Step4StudyGoals(viewModel, state)
                            5 -> Step5StudySchedule(viewModel, state)
                            6 -> Step6Notifications(viewModel, state)
                            7 -> Step7CompletionSummary(viewModel, state, onFinish)
                            else -> Step1PersonalProfile(viewModel, state)
                        }
                    }
                }

                // Bottom Action Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = Dimens.spaceLarge),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (state.currentStep < state.totalSteps) {
                        Button(
                            onClick = { viewModel.nextStep() },
                            shape = RoundedCornerShape(Dimens.radiusMedium),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryForest,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                        ) {
                            Text(
                                text = if (state.currentStep == 1) "Get Started" else "Continue",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.width(Dimens.spaceSmall))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// STEP 1: Personal Profile
// ==========================================
@Composable
private fun Step1PersonalProfile(
    viewModel: OnboardingViewModel,
    state: com.gtinnox.edutrack.ui.viewmodel.OnboardingWizardState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(Dimens.spaceStandard)
    ) {
        Text(
            text = "Welcome to EduTrack",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Let's personalize your academic workspace so your study telemetry and course milestones belong to you.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(Dimens.spaceSmall))

        // Avatar Initial Circle
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = CircleShape,
                color = SoftSage,
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = state.avatarInitials.ifBlank { "ME" },
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryForest
                        )
                    )
                }
            }
        }

        OutlinedTextField(
            value = state.fullName,
            onValueChange = { viewModel.updatePersonalProfile(it) },
            label = { Text("Your Preferred Name") },
            placeholder = { Text("e.g. Julian Vance") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(Dimens.radiusMedium),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryForest
            )
        )
    }
}

// ==========================================
// STEP 2: University & Academic Information
// ==========================================
@Composable
private fun Step2AcademicInfo(
    viewModel: OnboardingViewModel,
    state: com.gtinnox.edutrack.ui.viewmodel.OnboardingWizardState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(Dimens.spaceStandard)
    ) {
        Text(
            text = "Academic Information",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Where are you studying? This helps structure your semester and exam schedules.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            value = state.institution,
            onValueChange = { viewModel.updateAcademicInfo(institution = it) },
            label = { Text("University / Institution") },
            placeholder = { Text("e.g. Oxford University / Tech Institute") },
            leadingIcon = { Icon(Icons.Default.School, contentDescription = null) },
            singleLine = true,
            shape = RoundedCornerShape(Dimens.radiusMedium),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryForest)
        )

        OutlinedTextField(
            value = state.degree,
            onValueChange = { viewModel.updateAcademicInfo(degree = it) },
            label = { Text("Degree / Program") },
            placeholder = { Text("e.g. B.Sc. Computer Science") },
            singleLine = true,
            shape = RoundedCornerShape(Dimens.radiusMedium),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryForest)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)
        ) {
            OutlinedTextField(
                value = state.academicYear,
                onValueChange = { viewModel.updateAcademicInfo(academicYear = it) },
                label = { Text("Academic Year") },
                placeholder = { Text("Year 2 (Sophomore)") },
                singleLine = true,
                shape = RoundedCornerShape(Dimens.radiusMedium),
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryForest)
            )

            OutlinedTextField(
                value = state.semester,
                onValueChange = { viewModel.updateAcademicInfo(semester = it) },
                label = { Text("Term / Semester") },
                placeholder = { Text("Fall 2026") },
                singleLine = true,
                shape = RoundedCornerShape(Dimens.radiusMedium),
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryForest)
            )
        }

        OutlinedTextField(
            value = state.studentId,
            onValueChange = { viewModel.updateAcademicInfo(studentId = it) },
            label = { Text("Student ID (Optional)") },
            placeholder = { Text("e.g. S-94021") },
            singleLine = true,
            shape = RoundedCornerShape(Dimens.radiusMedium),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryForest)
        )
    }
}

// ==========================================
// STEP 3: Subject Setup
// ==========================================
@Composable
private fun Step3SubjectSetup(
    viewModel: OnboardingViewModel,
    state: com.gtinnox.edutrack.ui.viewmodel.OnboardingWizardState
) {
    var subjectName by remember { mutableStateOf("") }
    var courseCode by remember { mutableStateOf("") }
    var targetHours by remember { mutableIntStateOf(5) }
    var selectedColor by remember { mutableStateOf("#355B48") }

    val palette = listOf("#355B48", "#16A37B", "#8E67E8", "#E6A23C", "#38BDF8", "#E06D53")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(Dimens.spaceStandard)
    ) {
        Text(
            text = "Add Your Subjects",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Add your enrolled courses to log study sessions and track target hours.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Subject creation card
        OrganicCard(
            cornerRadius = Dimens.radiusMedium,
            contentPadding = Dimens.spaceMedium
        ) {
            Text(
                text = "New Course",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(Dimens.spaceSmall))

            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)) {
                OutlinedTextField(
                    value = subjectName,
                    onValueChange = { subjectName = it },
                    label = { Text("Subject Name") },
                    placeholder = { Text("e.g. Algorithms") },
                    singleLine = true,
                    shape = RoundedCornerShape(Dimens.radiusMedium),
                    modifier = Modifier.weight(1.4f),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryForest)
                )
                OutlinedTextField(
                    value = courseCode,
                    onValueChange = { courseCode = it },
                    label = { Text("Code") },
                    placeholder = { Text("CS 201") },
                    singleLine = true,
                    shape = RoundedCornerShape(Dimens.radiusMedium),
                    modifier = Modifier.weight(0.9f),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryForest)
                )
            }

            Spacer(modifier = Modifier.height(Dimens.spaceSmall))

            // Color palette selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Color:", style = MaterialTheme.typography.bodySmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    palette.forEach { hex ->
                        val parsed = try {
                            Color(android.graphics.Color.parseColor(hex))
                        } catch (e: Exception) {
                            PrimaryForest
                        }
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(parsed, CircleShape)
                                .clickable { selectedColor = hex }
                                .padding(2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedColor == hex) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Dimens.spaceSmall))

            Button(
                onClick = {
                    if (subjectName.isNotBlank()) {
                        viewModel.addSubject(subjectName, courseCode, selectedColor, targetHours)
                        subjectName = ""
                        courseCode = ""
                    }
                },
                shape = RoundedCornerShape(Dimens.radiusMedium),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryForest,
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add Course")
            }
        }

        // List of Added Subjects
        if (state.subjects.isNotEmpty()) {
            Text(
                text = "Enrolled Courses (${state.subjects.size})",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            state.subjects.forEach { draft ->
                val subColor = try {
                    Color(android.graphics.Color.parseColor(draft.color))
                } catch (e: Exception) {
                    PrimaryForest
                }
                Surface(
                    shape = RoundedCornerShape(Dimens.radiusMedium),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = Dimens.spaceMedium, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(subColor, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "${draft.code} · ${draft.name}",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Text(
                                    text = "${draft.weeklyTargetHours} hrs / week target",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        IconButton(onClick = { viewModel.removeSubject(draft.id) }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove",
                                tint = SoftDanger,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// STEP 4: Study Goals
// ==========================================
@Composable
private fun Step4StudyGoals(
    viewModel: OnboardingViewModel,
    state: com.gtinnox.edutrack.ui.viewmodel.OnboardingWizardState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(Dimens.spaceStandard)
    ) {
        Text(
            text = "Study Goals & Intervals",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Set your daily target and pomodoro intervals. You can adjust these anytime in Settings.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Daily Goal Selector
        OrganicCard(
            cornerRadius = Dimens.radiusMedium,
            contentPadding = Dimens.spaceMedium
        ) {
            Text("Daily Focus Target", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(Dimens.spaceSmall))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(60, 120, 180, 240).forEach { mins ->
                    FilterChip(
                        selected = state.dailyGoalMinutes == mins,
                        onClick = { viewModel.updateGoals(dailyGoalMinutes = mins) },
                        label = { Text("${mins / 60} Hours") },
                        shape = RoundedCornerShape(Dimens.radiusChip),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryForest,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Pomodoro Lengths
        OrganicCard(
            cornerRadius = Dimens.radiusMedium,
            contentPadding = Dimens.spaceMedium
        ) {
            Text("Focus Interval Duration", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(Dimens.spaceSmall))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(25, 45, 50, 60).forEach { mins ->
                    FilterChip(
                        selected = state.focusDurationMinutes == mins,
                        onClick = { viewModel.updateGoals(focusDurationMinutes = mins) },
                        label = { Text("$mins min") },
                        shape = RoundedCornerShape(Dimens.radiusChip),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryForest,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Break Lengths
        OrganicCard(
            cornerRadius = Dimens.radiusMedium,
            contentPadding = Dimens.spaceMedium
        ) {
            Text("Short Break Duration", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(Dimens.spaceSmall))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(5, 10, 15).forEach { mins ->
                    FilterChip(
                        selected = state.shortBreakMinutes == mins,
                        onClick = { viewModel.updateGoals(shortBreakMinutes = mins) },
                        label = { Text("$mins min") },
                        shape = RoundedCornerShape(Dimens.radiusChip),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryForest,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }
    }
}

// ==========================================
// STEP 5: Study Schedule
// ==========================================
@Composable
private fun Step5StudySchedule(
    viewModel: OnboardingViewModel,
    state: com.gtinnox.edutrack.ui.viewmodel.OnboardingWizardState
) {
    val allDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val times = listOf(
        "morning" to "Morning (8 AM - 12 PM)",
        "afternoon" to "Afternoon (12 PM - 5 PM)",
        "evening" to "Evening (5 PM - 9 PM)",
        "night" to "Night (9 PM - 1 AM)"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(Dimens.spaceStandard)
    ) {
        Text(
            text = "Your Study Routine",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Tell us when you are most productive to tailor schedule recommendations.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OrganicCard(
            cornerRadius = Dimens.radiusMedium,
            contentPadding = Dimens.spaceMedium
        ) {
            Text("Preferred Study Days", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(Dimens.spaceSmall))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                allDays.forEach { day ->
                    val isSelected = state.preferredDays.contains(day)
                    Surface(
                        shape = CircleShape,
                        color = if (isSelected) PrimaryForest else SoftSage,
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { viewModel.toggleStudyDay(day) }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = day.take(1),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (isSelected) Color.White else PrimaryForest
                            )
                        }
                    }
                }
            }
        }

        OrganicCard(
            cornerRadius = Dimens.radiusMedium,
            contentPadding = Dimens.spaceMedium
        ) {
            Text("Peak Study Time", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(Dimens.spaceSmall))
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                times.forEach { (key, label) ->
                    val isSelected = state.typicalStudyTime == key
                    Surface(
                        shape = RoundedCornerShape(Dimens.radiusSmall),
                        color = if (isSelected) SoftSage.copy(alpha = 0.8f) else Color.Transparent,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) PrimaryForest else MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.updateSchedulePreferences(typicalTime = key) }
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) PrimaryForest else MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.padding(horizontal = Dimens.spaceMedium, vertical = 10.dp)
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// STEP 6: Notifications & Reminders
// ==========================================
@Composable
private fun Step6Notifications(
    viewModel: OnboardingViewModel,
    state: com.gtinnox.edutrack.ui.viewmodel.OnboardingWizardState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(Dimens.spaceStandard)
    ) {
        Text(
            text = "Gentle Academic Reminders",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )

        EduTrackPermissionExplanation(
            title = "Stay on track with upcoming deadlines",
            description = "EduTrack can remind you about planned study sessions, pomodoro breaks, and upcoming exams. Reminders stay 100% offline on your device.",
            onAllow = {
                viewModel.setNotificationsEnabled(true)
                viewModel.nextStep()
            },
            onSkip = {
                viewModel.setNotificationsEnabled(false)
                viewModel.nextStep()
            }
        )
    }
}

// ==========================================
// STEP 7: Completion & Workspace Ready
// ==========================================
@Composable
private fun Step7CompletionSummary(
    viewModel: OnboardingViewModel,
    state: com.gtinnox.edutrack.ui.viewmodel.OnboardingWizardState,
    onFinish: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.spaceStandard)
    ) {
        Surface(
            shape = CircleShape,
            color = SoftSage,
            modifier = Modifier.size(72.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = PrimaryForest,
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Text(
            text = "You're all set, ${state.fullName.split(" ").firstOrNull() ?: "Scholar"}!",
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Your personalized academic workspace has been created.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Dimens.spaceSmall))

        // Summary Card
        OrganicCard(
            cornerRadius = Dimens.radiusLarge,
            contentPadding = Dimens.spaceLarge,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "WORKSPACE SUMMARY",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryForest
                )
            )
            Spacer(modifier = Modifier.height(Dimens.spaceMedium))

            SummaryItem("Academic Program", state.degree.ifBlank { "General Studies" })
            SummaryItem("Institution", state.institution.ifBlank { "Unassigned" })
            SummaryItem("Enrolled Courses", "${state.subjects.size} Courses Added")
            SummaryItem("Daily Focus Goal", "${state.dailyGoalMinutes / 60} Hours / Day")
            SummaryItem("Focus Block", "${state.focusDurationMinutes} Minutes")
        }

        Spacer(modifier = Modifier.height(Dimens.spaceMedium))

        Button(
            onClick = {
                viewModel.finishOnboarding(onFinish)
            },
            enabled = !state.isFinishing,
            shape = RoundedCornerShape(Dimens.radiusMedium),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryForest,
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            if (state.isFinishing) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Enter EduTrack",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.width(Dimens.spaceSmall))
                Icon(Icons.Default.Spa, contentDescription = null, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun SummaryItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
