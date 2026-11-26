package com.gtinnox.edutrack.ui.timer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gtinnox.edutrack.ui.components.AcademicEditorialHeader
import com.gtinnox.edutrack.ui.components.OrganicCard
import com.gtinnox.edutrack.ui.theme.DeepForest
import com.gtinnox.edutrack.ui.theme.Dimens
import com.gtinnox.edutrack.ui.theme.PrimaryForest
import com.gtinnox.edutrack.ui.theme.SoftSage
import com.gtinnox.edutrack.ui.theme.SoftSuccess
import com.gtinnox.edutrack.ui.theme.SoftTerracotta
import com.gtinnox.edutrack.ui.theme.adaptiveHorizontalPadding
import com.gtinnox.edutrack.ui.theme.isCompactScreen
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
    val horizontalPadding = adaptiveHorizontalPadding()
    val isCompact = isCompactScreen()

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

    val accentColor = when (uiState.mode) {
        TimerMode.FOCUS -> PrimaryForest
        TimerMode.SHORT_BREAK -> SoftSuccess
        TimerMode.LONG_BREAK -> SoftTerracotta
    }

    val selectedSub = subjects.find { it.id == uiState.selectedSubjectId }
    val ringSize = if (isCompact) 220.dp else 260.dp
    val canvasSize = if (isCompact) 200.dp else 240.dp

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
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimens.spaceLarge)
            ) {
                // Editorial Header directly on background
                AcademicEditorialHeader(
                    title = "Focus Sanctuary",
                    category = "Academic Immersion",
                    subtitle = "Meditation on knowledge & deliberate practice"
                )

                // 1. Soft Academia Mode Selector Pills
                Surface(
                    shape = RoundedCornerShape(Dimens.radiusPill),
                    color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TimerMode.values().forEach { mode ->
                            val isSelected = uiState.mode == mode
                            val (modeBg, modeTextColor) = when (mode) {
                                TimerMode.FOCUS -> Pair(SoftSage, PrimaryForest)
                                TimerMode.SHORT_BREAK -> Pair(SoftSage.copy(alpha = 0.6f), DeepForest)
                                TimerMode.LONG_BREAK -> Pair(SoftTerracotta.copy(alpha = 0.4f), DeepForest)
                            }

                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.setMode(mode) }
                                    .padding(horizontal = 2.dp),
                                shape = RoundedCornerShape(Dimens.radiusMedium),
                                color = if (isSelected) modeBg else Color.Transparent
                            ) {
                                Text(
                                    text = mode.title,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    ),
                                    color = if (isSelected) modeTextColor else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                // 2. Large Tabular Countdown Timer with Ambient Canvas Ring
                Box(
                    modifier = Modifier
                        .size(ringSize)
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(canvasSize)) {
                        val strokeWidth = 12.dp.toPx()
                        val stroke = Stroke(width = strokeWidth, cap = StrokeCap.Round)

                        // Track Ring
                        drawCircle(
                            color = SoftSage.copy(alpha = 0.7f),
                            style = stroke
                        )

                        // Sweep Progress Arc
                        val sweep = progress * 360f
                        drawArc(
                            color = accentColor,
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
                                fontSize = if (isCompact) 42.sp else 48.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.SansSerif,
                                letterSpacing = (-1.5).sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(Dimens.spaceSmall))
                        Surface(
                            shape = RoundedCornerShape(Dimens.radiusChip),
                            color = if (uiState.isRunning) SoftSage else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = if (uiState.isRunning) "DEEP STUDY" else "RESTING",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp
                                ),
                                color = if (uiState.isRunning) PrimaryForest else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
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
                        modifier = Modifier.size(52.dp)
                    ) {
                        IconButton(onClick = { viewModel.resetTimer() }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset Timer",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(Dimens.iconStandard)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(Dimens.spaceLarge))

                    // Play / Pause Main CTA
                    Button(
                        onClick = { viewModel.toggleStartPause() },
                        modifier = Modifier
                            .height(52.dp)
                            .width(170.dp),
                        shape = RoundedCornerShape(Dimens.radiusMedium),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accentColor,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = if (uiState.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (uiState.isRunning) "Pause" else "Start",
                                modifier = Modifier.size(Dimens.iconMedium)
                            )
                            Spacer(modifier = Modifier.width(Dimens.spaceSmall))
                            Text(
                                text = if (uiState.isRunning) "Pause" else "Start Focus",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }

                // 4. Target Subject Selector directly integrated
                OrganicCard(
                    cornerRadius = Dimens.radiusLarge,
                    contentPadding = Dimens.spaceStandard
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Module Focus",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (selectedSub != null) {
                            Surface(
                                shape = RoundedCornerShape(Dimens.radiusChip),
                                color = SoftSage
                            ) {
                                Text(
                                    text = selectedSub.name,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = PrimaryForest,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(Dimens.spaceMedium))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(subjects) { subject ->
                            val isSelected = uiState.selectedSubjectId == subject.id
                            val subColor = try {
                                Color(android.graphics.Color.parseColor(subject.color))
                            } catch (e: Exception) {
                                PrimaryForest
                            }

                            Surface(
                                shape = RoundedCornerShape(Dimens.radiusSmall),
                                color = if (isSelected) SoftSage else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) PrimaryForest else Color.Transparent
                                ),
                                modifier = Modifier.clickable { viewModel.setSubject(subject.id) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(subColor, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = subject.code.ifBlank { subject.name },
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        ),
                                        color = if (isSelected) PrimaryForest else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                // 5. Session Objective Note Input
                OrganicCard(
                    cornerRadius = Dimens.radiusLarge,
                    contentPadding = Dimens.spaceStandard
                ) {
                    Text(
                        text = "Session Objective",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Specify what milestone you intend to complete in this block",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(Dimens.spaceMedium))
                    OutlinedTextField(
                        value = uiState.sessionDescription,
                        onValueChange = { viewModel.setDescription(it) },
                        placeholder = {
                            Text(
                                "e.g. Solve dynamic programming chapter problems 1 through 5...",
                                style = MaterialTheme.typography.bodySmall
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(Dimens.radiusMedium),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryForest,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        minLines = 2,
                        maxLines = 3
                    )
                }

                // 6. Focus Statistics Summary
                Surface(
                    shape = RoundedCornerShape(Dimens.radiusMedium),
                    color = SoftSage.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, SoftSage),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Dimens.spaceStandard, vertical = Dimens.spaceMedium),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Daily Academic Momentum",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = PrimaryForest
                            )
                            Text(
                                text = "${uiState.completedSessionsCount} study intervals logged today",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(Dimens.radiusChip),
                            color = PrimaryForest,
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Text(
                                text = "✓ ${uiState.completedSessionsCount} Done",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                ),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(84.dp))
            }
        }
    }
}
