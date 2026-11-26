package com.gtinnox.edutrack.ui.settings

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gtinnox.edutrack.ui.components.AcademicEditorialHeader
import com.gtinnox.edutrack.ui.components.OrganicCard
import com.gtinnox.edutrack.ui.components.SectionHeader
import com.gtinnox.edutrack.ui.theme.Dimens
import com.gtinnox.edutrack.ui.theme.PrimaryForest
import com.gtinnox.edutrack.ui.theme.SoftDanger
import com.gtinnox.edutrack.ui.theme.SoftSage
import com.gtinnox.edutrack.ui.theme.adaptiveHorizontalPadding
import com.gtinnox.edutrack.ui.viewmodel.SettingsViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val horizontalPadding = adaptiveHorizontalPadding()

    var showResetDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var importJsonText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Restore Academic Curriculum?", fontWeight = FontWeight.Bold) },
            text = { Text("This will reset your subjects, study sessions, and tasks back to the default Oxford University Computer Science dataset.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResetDialog = false
                        viewModel.resetToDefaults()
                    }
                ) {
                    Text("Reset Database", color = SoftDanger, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(Dimens.radiusLarge)
        )
    }

    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text("Import Database Backup JSON", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = importJsonText,
                    onValueChange = { importJsonText = it },
                    placeholder = { Text("Paste exported JSON here...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Dimens.radiusSmall),
                    minLines = 4,
                    maxLines = 8
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showImportDialog = false
                        viewModel.importData(importJsonText)
                    },
                    shape = RoundedCornerShape(Dimens.radiusMedium),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryForest)
                ) {
                    Text("Import & Restore")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = Dimens.maxContentWidth)
                    .padding(horizontal = horizontalPadding),
                verticalArrangement = Arrangement.spacedBy(Dimens.spaceSection)
            ) {
                // Header directly on background
                item {
                    AcademicEditorialHeader(
                        title = "Settings & Storage",
                        category = "Preferences & System",
                        onBackClick = onNavigateBack
                    )
                }

                // Appearance & Theme Card
                item {
                    OrganicCard(
                        cornerRadius = Dimens.radiusLarge,
                        contentPadding = Dimens.spaceLarge
                    ) {
                        SectionHeader(title = "Appearance & Theme")
                        Spacer(modifier = Modifier.height(Dimens.spaceMedium))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Dark Mode Atmosphere",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Midnight obsidian palette (#171A18)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = uiState.isDarkMode,
                                onCheckedChange = { viewModel.toggleDarkMode(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = PrimaryForest,
                                    checkedTrackColor = SoftSage
                                )
                            )
                        }
                    }
                }

                // Focus & Timer Intervals Card
                item {
                    OrganicCard(
                        cornerRadius = Dimens.radiusLarge,
                        contentPadding = Dimens.spaceLarge
                    ) {
                        SectionHeader(title = "Focus & Pomodoro Timer")
                        Spacer(modifier = Modifier.height(Dimens.spaceMedium))

                        Text(
                            text = "Default Focus Interval",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)) {
                            listOf(20, 25, 45, 50).forEach { mins ->
                                val isSel = uiState.pomodoroMinutes == mins
                                FilterChip(
                                    selected = isSel,
                                    onClick = { viewModel.setPomodoroMinutes(mins) },
                                    label = { Text("${mins}m") },
                                    shape = RoundedCornerShape(Dimens.radiusChip),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = SoftSage,
                                        selectedLabelColor = PrimaryForest
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(Dimens.spaceStandard))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Acoustic Chime & Ticks",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Audio cues when focus intervals conclude",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = uiState.soundEnabled,
                                onCheckedChange = { viewModel.setSoundEnabled(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = PrimaryForest,
                                    checkedTrackColor = SoftSage
                                )
                            )
                        }
                    }
                }

                // Data Export, Import & Database Operations
                item {
                    OrganicCard(
                        cornerRadius = Dimens.radiusLarge,
                        contentPadding = Dimens.spaceLarge
                    ) {
                        SectionHeader(title = "Data Persistence & Database")
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Native Room Database · 9 Entities with Foreign Key Cascading",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(Dimens.spaceStandard))

                        Button(
                            onClick = { viewModel.exportData() },
                            modifier = Modifier.fillMaxWidth().height(Dimens.buttonHeight),
                            shape = RoundedCornerShape(Dimens.radiusMedium),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryForest),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                        ) {
                            Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(Dimens.iconMedium))
                            Spacer(modifier = Modifier.width(Dimens.spaceSmall))
                            Text("Export Database as JSON Backup", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(Dimens.spaceMedium))

                        OutlinedButton(
                            onClick = { showImportDialog = true },
                            modifier = Modifier.fillMaxWidth().height(Dimens.buttonHeight),
                            shape = RoundedCornerShape(Dimens.radiusMedium)
                        ) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(Dimens.iconMedium))
                            Spacer(modifier = Modifier.width(Dimens.spaceSmall))
                            Text("Import JSON Backup", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(Dimens.spaceMedium))

                        OutlinedButton(
                            onClick = { showResetDialog = true },
                            modifier = Modifier.fillMaxWidth().height(Dimens.buttonHeight),
                            shape = RoundedCornerShape(Dimens.radiusMedium)
                        ) {
                            Icon(Icons.Default.Restore, contentDescription = null, tint = SoftDanger, modifier = Modifier.size(Dimens.iconMedium))
                            Spacer(modifier = Modifier.width(Dimens.spaceSmall))
                            Text("Reset Academic Curriculum to Seed Data", color = SoftDanger, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(36.dp))
                }
            }
        }
    }
}
