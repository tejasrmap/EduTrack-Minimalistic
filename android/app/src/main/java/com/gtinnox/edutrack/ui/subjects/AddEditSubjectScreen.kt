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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gtinnox.edutrack.ui.theme.AcademicAmber
import com.gtinnox.edutrack.ui.theme.AcademicIndigo
import com.gtinnox.edutrack.ui.theme.AcademicPurple
import com.gtinnox.edutrack.ui.theme.AcademicRose
import com.gtinnox.edutrack.ui.theme.AcademicSage
import com.gtinnox.edutrack.ui.theme.AcademicSky
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
    var color by remember(existing) { mutableStateOf(existing?.color ?: "#6477D9") }
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
        "#6477D9", // Academic Indigo
        "#16A37B", // Academic Sage
        "#E6A23C", // Academic Amber
        "#8E67E8", // Academic Purple
        "#38BDF8", // Academic Sky
        "#E06464"  // Academic Rose
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
                    text = if (existing == null) "Enroll Subject Course" else "Edit Subject",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Course Name *") },
                placeholder = { Text("e.g. Distributed Computing Systems") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Code *") },
                    placeholder = { Text("CS 310") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = semester,
                    onValueChange = { semester = it },
                    label = { Text("Semester") },
                    placeholder = { Text("Fall 2026") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            OutlinedTextField(
                value = instructor,
                onValueChange = { instructor = it },
                label = { Text("Instructor / Professor") },
                placeholder = { Text("Dr. Alan Turing") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = targetHoursText,
                    onValueChange = { targetHoursText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Weekly Goal (Hours) *") },
                    placeholder = { Text("6") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = attendanceRateText,
                    onValueChange = { attendanceRateText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Attendance Rate (%)") },
                    placeholder = { Text("95") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Syllabus & Course Scope") },
                placeholder = { Text("Key topics, learning objectives, and lab components...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                shape = RoundedCornerShape(12.dp)
            )

            // Color Palette Selector
            Text(
                text = "Course Theme Accent",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                palette.forEach { hex ->
                    val c = Color(android.graphics.Color.parseColor(hex))
                    val isSelected = color.equals(hex, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(c)
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { color = hex },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val targetHours = targetHoursText.toIntOrNull() ?: 5
                    val attendance = attendanceRateText.toIntOrNull() ?: 95
                    viewModel.saveSubject(
                        id = existing?.id,
                        name = name,
                        code = code,
                        color = color,
                        description = description,
                        semester = semester,
                        instructor = instructor,
                        weeklyTargetHours = targetHours,
                        attendanceRate = attendance
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AcademicIndigo),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (existing == null) "Enroll Course Module" else "Save Changes",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
