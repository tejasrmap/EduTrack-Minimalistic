package com.gtinnox.edutrack.ui.goals

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gtinnox.edutrack.ui.components.AcademicCard
import com.gtinnox.edutrack.ui.theme.AcademicIndigo
import com.gtinnox.edutrack.ui.theme.AcademicRose
import com.gtinnox.edutrack.ui.theme.AcademicSage
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
            title = { Text("Set Academic Milestone Target") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Target Description *") },
                        placeholder = { Text("e.g. Complete 25 Study Hours This Week") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = targetValText,
                            onValueChange = { targetValText = it },
                            label = { Text("Target Quota") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = unit,
                            onValueChange = { unit = it },
                            label = { Text("Unit") },
                            placeholder = { Text("hours / sessions") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    OutlinedTextField(
                        value = targetDate,
                        onValueChange = { targetDate = it },
                        label = { Text("Deadline Date (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val tVal = targetValText.toDoubleOrNull() ?: 25.0
                        val cVal = currentValText.toDoubleOrNull() ?: 0.0
                        viewModel.saveGoal(
                            id = null,
                            title = title,
                            type = "weekly_study",
                            targetValue = tVal,
                            currentValue = cVal,
                            unit = unit,
                            targetDate = targetDate
                        )
                        showAddDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AcademicIndigo)
                ) {
                    Text("Create Target")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Milestones & Targets",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${goals.size} Active Study Quotas",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = { showAddDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = AcademicIndigo),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Target")
                    }
                }
            }

            if (goals.isEmpty()) {
                item {
                    AcademicCard {
                        Text(
                            text = "No study targets configured. Create milestone goals to push your weekly study hours.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(goals) { goal ->
                    val progress = if (goal.targetValue > 0) {
                        (goal.currentValue / goal.targetValue).toFloat().coerceIn(0f, 1f)
                    } else 0f

                    AcademicCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (goal.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = "Toggle",
                                tint = if (goal.isCompleted) AcademicSage else AcademicIndigo,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable { viewModel.toggleGoal(goal) }
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = goal.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Target Date: ${goal.targetDate}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${goal.currentValue} / ${goal.targetValue} ${goal.unit}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = AcademicIndigo
                                    )
                                    Text(
                                        text = "${(progress * 100).toInt()}%",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = AcademicSage
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = if (goal.isCompleted) AcademicSage else AcademicIndigo
                                )
                            }

                            IconButton(onClick = { viewModel.deleteGoal(goal) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AcademicRose, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
