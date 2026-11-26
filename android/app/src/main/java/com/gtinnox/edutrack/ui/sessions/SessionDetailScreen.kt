package com.gtinnox.edutrack.ui.sessions

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.gtinnox.edutrack.ui.components.AcademicEditorialHeader
import com.gtinnox.edutrack.ui.components.OrganicCard
import com.gtinnox.edutrack.ui.components.SubjectChip
import com.gtinnox.edutrack.ui.theme.Dimens
import com.gtinnox.edutrack.ui.theme.PrimaryForest
import com.gtinnox.edutrack.ui.theme.SoftDanger
import com.gtinnox.edutrack.ui.theme.SoftSage
import com.gtinnox.edutrack.ui.theme.SoftSuccess
import com.gtinnox.edutrack.ui.theme.adaptiveHorizontalPadding
import com.gtinnox.edutrack.ui.viewmodel.SessionViewModel

@Composable
fun SessionDetailScreen(
    sessionId: String,
    viewModel: SessionViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val sessionWithSubject by remember(sessionId) {
        viewModel.getSessionWithSubject(sessionId)
    }.collectAsState(initial = null)
    var showDeleteDialog by remember { mutableStateOf(false) }
    val horizontalPadding = adaptiveHorizontalPadding()

    val data = sessionWithSubject

    if (data == null) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(Dimens.spaceLarge),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Session details loading...", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(Dimens.spaceStandard))
            Button(
                onClick = onNavigateBack,
                shape = RoundedCornerShape(Dimens.radiusMedium)
            ) {
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
            title = {
                Text(
                    "Delete Session Record?",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = { Text("Are you sure you want to delete this ${session.durationMinutes}-minute study record?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteSession(session)
                        onNavigateBack()
                    }
                ) {
                    Text("Delete", color = SoftDanger, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(Dimens.radiusLarge)
        )
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = Dimens.maxContentWidth)
                .padding(horizontal = horizontalPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceStandard)
        ) {
            // App Bar directly on background
            AcademicEditorialHeader(
                title = "Session Details",
                category = "Session Audit",
                onBackClick = onNavigateBack,
                trailingAction = {
                    Row(horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.size(Dimens.iconButtonSize)
                        ) {
                            IconButton(onClick = { onNavigateToEdit(session.id) }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(Dimens.iconMedium)
                                )
                            }
                        }

                        Surface(
                            shape = CircleShape,
                            color = SoftDanger.copy(alpha = 0.12f),
                            modifier = Modifier.size(Dimens.iconButtonSize)
                        ) {
                            IconButton(onClick = { showDeleteDialog = true }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = SoftDanger,
                                    modifier = Modifier.size(Dimens.iconMedium)
                                )
                            }
                        }
                    }
                }
            )

            // Main Session Card
            OrganicCard(
                cornerRadius = Dimens.radiusXLarge,
                contentPadding = Dimens.spaceLarge
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (subject != null) {
                        SubjectChip(
                            name = subject.name,
                            code = subject.code,
                            colorHex = subject.color,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                    } else {
                        Text(
                            text = "General Focus Block",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.weight(1f, fill = false)
                        )
                    }

                    Spacer(modifier = Modifier.width(Dimens.spaceSmall))

                    Surface(
                        shape = RoundedCornerShape(Dimens.radiusChip),
                        color = SoftSage.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = session.sessionType.replace("_", " ").uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = PrimaryForest
                            ),
                            maxLines = 1,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Dimens.spaceLarge))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Total Focus Time",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${session.durationMinutes} Minutes",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Focus Score",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${session.focusScore}%",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = SoftSuccess
                            )
                        )
                    }
                }

                if (session.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(Dimens.spaceLarge))
                    Text(
                        text = "Session Reflection",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = session.notes,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
