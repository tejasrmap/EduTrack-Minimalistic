package com.gtinnox.edutrack.ui.timer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.gtinnox.edutrack.ui.components.PremiumCard
import com.gtinnox.edutrack.ui.components.SectionHeader
import com.gtinnox.edutrack.ui.theme.EduTrackPrimary
import com.gtinnox.edutrack.ui.theme.EduTrackPrimarySoft
import com.gtinnox.edutrack.ui.theme.EduTrackSuccess
import com.gtinnox.edutrack.ui.theme.EduTrackWarning
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
        TimerMode.FOCUS -> EduTrackPrimary
        TimerMode.SHORT_BREAK -> EduTrackSuccess
        TimerMode.LONG_BREAK -> EduTrackWarning
    }

    val selectedSub = subjects.find { it.id == uiState.selectedSubjectId }

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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Spacer(modifier = Modifier.height(6.dp))

            // 1. Mode Selector Pills (Focus / Short Break / Long Break)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TimerMode.values().forEach { mode ->
                        val isSelected = uiState.mode == mode
                        val (modeBg, modeTextColor) = when (mode) {
                            TimerMode.FOCUS -> Pair(EduTrackPrimarySoft, EduTrackPrimary)
                            TimerMode.SHORT_BREAK -> Pair(EduTrackSuccess.copy(alpha = 0.15f), EduTrackSuccess)
                            TimerMode.LONG_BREAK -> Pair(EduTrackWarning.copy(alpha = 0.15f), EduTrackWarning)
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.setMode(mode) }
                                .padding(horizontal = 3.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) modeBg else Color.Transparent
                        ) {
                            Text(
                                text = mode.title,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                ),
                                color = if (isSelected) modeTextColor else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 10.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            // 2. Large Tabular Countdown Timer with Ambient Canvas Ring
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(240.dp)) {
                    val strokeWidth = 14.dp.toPx()
                    val stroke = Stroke(width = strokeWidth, cap = StrokeCap.Round)

                    // Track Ring
                    drawCircle(
                        color = dialColor.copy(alpha = 0.12f),
                        style = stroke
                    )

                    // Sweep Progress Arc
                    val sweep = progress * 360f
                    drawArc(
                        color = dialColor,
                        startAngle = -90f,
                        sweepAngle = sweep,
                        useCenter = false,
                        style = stroke
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = timeFormatted,
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontSize = 46.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.SansSerif,
                            letterSpacing = (-1).sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = dialColor.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = if (uiState.isRunning) "FOCUSING" else "PAUSED",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = dialColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // 3. Primary Timer Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reset Button
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.size(54.dp)
                ) {
                    IconButton(onClick = { viewModel.resetTimer() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Timer",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(20.dp))

                // Play / Pause Main CTA
                Button(
                    onClick = { viewModel.toggleStartPause() },
                    modifier = Modifier
                        .height(58.dp)
                        .width(160.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = dialColor,
                        contentColor = Color.White
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (uiState.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (uiState.isRunning) "Pause" else "Start",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (uiState.isRunning) "Pause" else "Start",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            // 4. Subject Selector for Study Focus
            PremiumCard(contentPadding = 16.dp) {
                SectionHeader(
                    title = "Target Subject",
                    badgeText = selectedSub?.name ?: "Focus Subject"
                )
                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(subjects) { subject ->
                        val isSelected = uiState.selectedSubjectId == subject.id
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) EduTrackPrimarySoft else MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) EduTrackPrimary else Color.Transparent
                            ),
                            modifier = Modifier.clickable { viewModel.setSubject(subject.id) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val dotColor = try {
                                    Color(android.graphics.Color.parseColor(subject.color))
                                } catch (e: Exception) {
                                    EduTrackPrimary
                                }
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(dotColor, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = subject.code.ifBlank { subject.name },
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = if (isSelected) EduTrackPrimary else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // 5. Session Note Input
            PremiumCard(contentPadding = 16.dp) {
                Text(
                    text = "Session Reflection / Target Note",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = uiState.sessionDescription,
                    onValueChange = { viewModel.setDescription(it) },
                    placeholder = { Text("What are you working on right now?") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 2,
                    maxLines = 3
                )
            }

            // 6. Focus Statistics Summary
            PremiumCard(contentPadding = 16.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Pomodoros Completed Today",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${uiState.completedSessionsCount} intervals recorded",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = EduTrackSuccess.copy(alpha = 0.12f),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "✓ ${uiState.completedSessionsCount} Cycles",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = EduTrackSuccess
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
