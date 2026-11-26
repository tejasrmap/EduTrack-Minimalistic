package com.gtinnox.edutrack.ui.subjects

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import com.gtinnox.edutrack.ui.components.PremiumCard
import com.gtinnox.edutrack.ui.theme.EduTrackPrimary
import com.gtinnox.edutrack.ui.theme.EduTrackPrimarySoft
import com.gtinnox.edutrack.ui.viewmodel.SubjectUiEvent
import com.gtinnox.edutrack.ui.viewmodel.SubjectViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AddEditSubjectScreen(
    subjectId: String?,
    viewModel: SubjectViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val subjects by viewModel.allSubjects.collectAsState()
    val existing = remember(subjectId, subjects) {
        subjects.find { it.id == subjectId }
    }

    var name by remember(existing) { mutableStateOf(existing?.name ?: "") }
    var code by remember(existing) { mutableStateOf(existing?.code ?: "") }
    var color by remember(existing) { mutableStateOf(existing?.color ?: "#615CE7") }
    var description by remember(existing) { mutableStateOf(existing?.description ?: "") }
    var semester by remember(existing) { mutableStateOf(existing?.semester ?: "Fall 2026") }
    var instructor by remember(existing) { mutableStateOf(existing?.instructor ?: "") }
    var targetHoursText by remember(existing) { mutableStateOf(existing?.weeklyTargetHours?.toString() ?: "5") }
    var attendanceRateText by remember(existing) { mutableStateOf(existing?.attendanceRate?.toString() ?: "95") }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is SubjectUiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                is SubjectUiEvent.SubjectSaved -> onNavigateBack()
                else -> Unit
            }
        }
    }

    val palette = listOf(
        "#615CE7", // Primary Lavender/Indigo
        "#16A37B", // Mint / Sage
        "#E6A23C", // Amber / Peach
        "#8B85FF", // Soft Purple
        "#38BDF8", // Sky
        "#E06464"  // Rose
    )

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
                    text = if (existing != null) "Edit Academic Subject" else "Enroll New Subject",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.weight(1f)
                )
            }

            // Subject Info Card
            PremiumCard(contentPadding = 18.dp) {
                Text(
                    text = "Core Curriculum Details",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Subject / Course Name *") },
                    placeholder = { Text("e.g. Distributed Operating Systems") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = code,
                        onValueChange = { code = it },
                        label = { Text("Course Code *") },
                        placeholder = { Text("CS 401") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = semester,
                        onValueChange = { semester = it },
                        label = { Text("Semester") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = instructor,
                    onValueChange = { instructor = it },
                    label = { Text("Professor / Instructor") },
                    placeholder = { Text("e.g. Dr. Barbara Liskov") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Syllabus & Course Scope") },
                    placeholder = { Text("Consensus algorithms, replication, raft...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 2
                )
            }

            // Visual Identity (Palette)
            PremiumCard(contentPadding = 18.dp) {
                Text(
                    text = "Subject Color Identity",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    palette.forEach { hex ->
                        val swatchColor = Color(android.graphics.Color.parseColor(hex))
                        val isSelected = color.equals(hex, ignoreCase = true)

                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(swatchColor, CircleShape)
                                .clickable { color = hex }
                                .border(
                                    width = if (isSelected) 3.dp else 0.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Target Study & Attendance
            PremiumCard(contentPadding = 18.dp) {
                Text(
                    text = "Academic Targets",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(14.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = targetHoursText,
                        onValueChange = { targetHoursText = it },
                        label = { Text("Weekly Study Target (Hours)") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = attendanceRateText,
                        onValueChange = { attendanceRateText = it },
                        label = { Text("Attendance Target (%)") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Save Action
            Button(
                onClick = {
                    viewModel.saveSubject(
                        id = subjectId,
                        name = name,
                        code = code,
                        color = color,
                        description = description,
                        semester = semester,
                        instructor = instructor,
                        weeklyTargetHours = targetHoursText.toIntOrNull() ?: 5,
                        attendanceRate = attendanceRateText.toIntOrNull() ?: 95
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EduTrackPrimary)
            ) {
                Text(
                    text = if (existing != null) "Update Curriculum Subject" else "Enroll Subject",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
