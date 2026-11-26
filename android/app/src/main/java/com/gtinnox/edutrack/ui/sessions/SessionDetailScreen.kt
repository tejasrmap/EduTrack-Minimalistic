package com.gtinnox.edutrack.ui.sessions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gtinnox.edutrack.ui.components.AcademicCard
import com.gtinnox.edutrack.ui.components.SubjectChip
import com.gtinnox.edutrack.ui.theme.AcademicIndigo
import com.gtinnox.edutrack.ui.theme.AcademicRose
import com.gtinnox.edutrack.ui.theme.AcademicSage
import com.gtinnox.edutrack.ui.viewmodel.SessionViewModel

@Composable
fun SessionDetailScreen(
    sessionId: String,
    viewModel: SessionViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val sessionWithSubject by viewModel.getSessionWithSubject(sessionId).collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    val data = sessionWithSubject

    if (data == null) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Session details loading...", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onNavigateBack) {
                Text("Go Back")
            }
        }
        return
    }

    val session = data.session
    val subject = data.subject

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Session Record?") },
            text = { Text("Are you sure you want to delete this ${session.durationMinutes}-minute study record?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteSession(session)
                        onNavigateBack()
                    }
                ) {
                    Text("Delete", color = AcademicRose)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Study Session Details",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { onNavigateToEdit(session.id) }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit")
            }
            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AcademicRose)
            }
        }

        AcademicCard {
            if (subject != null) {
                SubjectChip(
                    name = subject.name,
                    code = subject.code,
                    colorHex = subject.color
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Text(
                text = "${session.durationMinutes} Minutes",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = AcademicIndigo
            )

            Text(
                text = "Modality: ${session.sessionType.replace("_", " ").uppercase()}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Focus Score", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${session.focusScore}%", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = AcademicSage)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Entry Type", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(if (session.isManual) "Manual Log" else "Pomodoro Timer", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium))
                }
            }

            if (session.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Reflections & Notes:",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = session.notes,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Recorded at: ${session.startTime}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
