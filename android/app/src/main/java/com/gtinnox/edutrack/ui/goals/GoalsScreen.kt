package com.gtinnox.edutrack.ui.goals

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gtinnox.edutrack.ui.components.AcademicEditorialHeader
import com.gtinnox.edutrack.ui.components.AcademicEmptyState
import com.gtinnox.edutrack.ui.components.OrganicCard
import com.gtinnox.edutrack.ui.theme.Dimens
import com.gtinnox.edutrack.ui.theme.PrimaryForest
import com.gtinnox.edutrack.ui.theme.SoftDanger
import com.gtinnox.edutrack.ui.theme.SoftSage
import com.gtinnox.edutrack.ui.theme.SoftSuccess
import com.gtinnox.edutrack.ui.theme.adaptiveHorizontalPadding
import com.gtinnox.edutrack.ui.viewmodel.GoalViewModel
import kotlinx.coroutines.flow.collectLatest
import java.time.LocalDate

@Composable
fun GoalsScreen(
    viewModel: GoalViewModel,
    modifier: Modifier = Modifier
) {
    val goals by viewModel.allGoals.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val horizontalPadding = adaptiveHorizontalPadding()

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var targetValText by remember { mutableStateOf("25") }
        var currentValText by remember { mutableStateOf("0") }
        var unit by remember { mutableStateOf("hours") }
        var targetDate by remember { mutableStateOf(LocalDate.now().plusDays(7).toString()) }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(
                    text = "Set Academic Milestone Target",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Target Description *") },
                        placeholder = { Text("e.g. Complete 25 Study Hours This Week") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(Dimens.radiusMedium),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryForest,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)) {
                        OutlinedTextField(
                            value = targetValText,
                            onValueChange = { targetValText = it },
                            label = { Text("Target Quota") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(Dimens.radiusSmall)
                        )
                        OutlinedTextField(
                            value = unit,
                            onValueChange = { unit = it },
                            label = { Text("Unit") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(Dimens.radiusSmall)
                        )
                    }

                    OutlinedTextField(
                        value = targetDate,
                        onValueChange = { targetDate = it },
                        label = { Text("Target Date (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(Dimens.radiusSmall)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            viewModel.saveGoal(
                                id = null,
                                title = title,
                                type = "weekly_hours",
                                targetValue = targetValText.toDoubleOrNull() ?: 25.0,
                                currentValue = currentValText.toDoubleOrNull() ?: 0.0,
                                unit = unit,
                                targetDate = targetDate
                            )
                            showAddDialog = false
                        }
                    },
                    shape = RoundedCornerShape(Dimens.radiusMedium),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryForest)
                ) {
                    Text("Save Target", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(Dimens.radiusLarge)
        )
    }

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
                    .padding(horizontal = horizontalPadding),
                verticalArrangement = Arrangement.spacedBy(Dimens.spaceStandard)
            ) {
                // Header directly on background
                AcademicEditorialHeader(
                    title = "Targets & Quotas",
                    category = "Academic Milestones",
                    trailingAction = {
                        Button(
                            onClick = { showAddDialog = true },
                            shape = RoundedCornerShape(Dimens.radiusMedium),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryForest),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(Dimens.iconSmall))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("New Target", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                )

                if (goals.isEmpty()) {
                    AcademicEmptyState(
                        icon = Icons.Default.Flag,
                        title = "No Benchmark Goals Established",
                        description = "Set weekly focus targets, subject study quotas, or exam revision milestones.",
                        actionText = "Establish Milestone",
                        onActionClick = { showAddDialog = true }
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(goals) { goal ->
                            val progress = if (goal.targetValue > 0) {
                                (goal.currentValue / goal.targetValue).toFloat().coerceIn(0f, 1f)
                            } else 0f
                            val isDone = goal.isCompleted || progress >= 1f

                            OrganicCard(
                                cornerRadius = Dimens.radiusMedium,
                                contentPadding = Dimens.spaceStandard
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = "Toggle Goal",
                                        tint = if (isDone) SoftSuccess else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier
                                            .size(Dimens.iconStandard)
                                            .clickable { viewModel.toggleGoal(goal) }
                                    )

                                    Spacer(modifier = Modifier.width(Dimens.spaceMedium))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = goal.title,
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold
                                                ),
                                                color = if (isDone) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f, fill = false)
                                            )
                                            Spacer(modifier = Modifier.width(Dimens.spaceSmall))
                                            Surface(
                                                shape = RoundedCornerShape(Dimens.radiusChip),
                                                color = if (isDone) SoftSage else MaterialTheme.colorScheme.surfaceVariant
                                            ) {
                                                Text(
                                                    text = if (isDone) "Achieved" else "${(progress * 100).toInt()}%",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = if (isDone) PrimaryForest else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        LinearProgressIndicator(
                                            progress = { progress },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(6.dp)
                                                .clip(RoundedCornerShape(3.dp)),
                                            color = if (isDone) SoftSuccess else PrimaryForest,
                                            trackColor = SoftSage.copy(alpha = 0.5f)
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "${goal.currentValue.toInt()} / ${goal.targetValue.toInt()} ${goal.unit}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = "Target: ${goal.targetDate}",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = { viewModel.deleteGoal(goal) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = SoftDanger.copy(alpha = 0.7f),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(84.dp))
                        }
                    }
                }
            }
        }
    }
}
