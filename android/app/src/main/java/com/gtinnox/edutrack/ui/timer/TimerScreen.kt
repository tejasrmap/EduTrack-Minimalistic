package com.gtinnox.edutrack.ui.timer

import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gtinnox.edutrack.ui.components.AcademicCard
import com.gtinnox.edutrack.ui.components.SubjectChip
import com.gtinnox.edutrack.ui.theme.AcademicAmber
import com.gtinnox.edutrack.ui.theme.AcademicIndigo
import com.gtinnox.edutrack.ui.theme.AcademicSage
import com.gtinnox.edutrack.ui.viewmodel.TimerMode
import com.gtinnox.edutrack.ui.viewmodel.TimerViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun TimerScreen(
    viewModel: TimerViewModel,
    initialSubjectId: String? = null,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val subjects by viewModel.subjects.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(initialSubjectId) {
        if (!initialSubjectId.isNullOrBlank()) {
            viewModel.setSubject(initialSubjectId)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    val mins = uiState.remainingSeconds / 60
    val secs = uiState.remainingSeconds % 60
    val timeFormatted = String.format("%02d:%02d", mins, secs)

    val progress = if (uiState.totalSeconds > 0) {
        (uiState.remainingSeconds.toFloat() / uiState.totalSeconds.toFloat()).coerceIn(0f, 1f)
    } else 1f

    val dialColor = when (uiState.mode) {
        TimerMode.FOCUS -> AcademicIndigo
        TimerMode.SHORT_BREAK -> AcademicSage
        TimerMode.LONG_BREAK -> AcademicAmber
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Mode Selector Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TimerMode.values().forEach { mode ->
                    val isSelected = uiState.mode == mode
                    Surface(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .clickable { viewModel.setMode(mode) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) dialColor.copy(alpha = 0.15f) else Color.Transparent,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) dialColor else MaterialTheme.colorScheme.outline
                        )
                    ) {
                        Text(
                            text = mode.title,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isSelected) dialColor else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Circular Dial Canvas
            Box(
                modifier = Modifier.size(240.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(220.dp)) {
                    val stroke = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
                    // Background track
                    drawCircle(
                        color = dialColor.copy(alpha = 0.12f),
                        style = stroke
                    )
                    // Progress arc
                    drawArc(
                        color = dialColor,
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        style = stroke
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = timeFormatted,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 44.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (uiState.isRunning) "In Session" else "Paused / Ready",
                        style = MaterialTheme.typography.labelMedium,
                        color = dialColor
                    )
                }
            }

            // Controls Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.resetTimer() },
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset")
                }

                Button(
                    onClick = { viewModel.toggleStartPause() },
                    modifier = Modifier
                        .height(54.dp)
                        .width(160.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = dialColor),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = if (uiState.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (uiState.isRunning) "Pause" else "Start Focus",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Completed Sessions Banner
            AcademicCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Completed Intervals Today",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = "${uiState.completedSessionsCount} Pomodoros",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = AcademicSage
                    )
                }
            }

            // Target Academic Subject Selector
            AcademicCard {
                Text(
                    text = "Focus Target Subject",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    subjects.forEach { s ->
                        val isSelected = uiState.selectedSubjectId == s.id
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setSubject(s.id) },
                            label = { Text("${s.code} · ${s.name}") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AcademicIndigo.copy(alpha = 0.15f),
                                selectedLabelColor = AcademicIndigo
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = uiState.sessionDescription,
                    onValueChange = { viewModel.setDescription(it) },
                    label = { Text("What are you working on?") },
                    placeholder = { Text("e.g. Graph algorithm proofs, Chapter 4 notes") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
