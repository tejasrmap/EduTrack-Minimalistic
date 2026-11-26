package com.gtinnox.edutrack.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gtinnox.edutrack.ui.components.AcademicCard
import com.gtinnox.edutrack.ui.theme.AcademicIndigo
import com.gtinnox.edutrack.ui.viewmodel.OnboardingViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
            onFinish()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "EduTrack Academic Suite",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = AcademicIndigo
            )
            Text(
                text = "Step ${state.currentStep} of 3 · Academic Onboarding",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { state.currentStep / 3f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = AcademicIndigo
            )

            Spacer(modifier = Modifier.height(24.dp))

            when (state.currentStep) {
                1 -> {
                    AcademicCard {
                        Text(
                            text = "Scholar Identity & Institution",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = state.name,
                            onValueChange = { viewModel.updateField(name = it) },
                            label = { Text("Full Name *") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = state.university,
                            onValueChange = { viewModel.updateField(university = it) },
                            label = { Text("University / Institution *") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = state.program,
                            onValueChange = { viewModel.updateField(program = it) },
                            label = { Text("Degree & Major *") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
                2 -> {
                    AcademicCard {
                        Text(
                            text = "Academic Quotas & Goals",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = state.academicYear,
                                onValueChange = { viewModel.updateField(academicYear = it) },
                                label = { Text("Academic Year") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = state.semester,
                                onValueChange = { viewModel.updateField(semester = it) },
                                label = { Text("Semester / Term") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = state.weeklyHours.toString(),
                            onValueChange = {
                                it.toIntOrNull()?.let { h -> viewModel.updateField(weeklyHours = h) }
                            },
                            label = { Text("Target Study Hours / Week") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Recommended: 25 - 35 hours weekly for rigorous STEM & Medicine majors.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                3 -> {
                    AcademicCard {
                        Text(
                            text = "Ready to Launch Your Workspace",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Your academic workspace has been configured with enrolled modules, Pomodoro timers, and examination schedules for ${state.name}.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "• 5 Pre-Enrolled Computer Science & Math modules\n• Integrated Room database with zero cloud bloat\n• Live Pomodoro timer and Pomodoro logs",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (state.currentStep > 1) {
                    OutlinedButton(
                        onClick = { viewModel.prevStep() },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Back")
                    }
                } else {
                    Spacer(modifier = Modifier.size(1.dp))
                }

                Button(
                    onClick = { viewModel.nextStep() },
                    colors = ButtonDefaults.buttonColors(containerColor = AcademicIndigo),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(if (state.currentStep == 3) "Enter Workspace" else "Continue")
                    Spacer(modifier = Modifier.size(4.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
