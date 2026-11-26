package com.gtinnox.edutrack.ui.history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gtinnox.edutrack.data.local.entity.StudySessionEntity
import com.gtinnox.edutrack.data.local.relations.SessionWithSubject
import com.gtinnox.edutrack.ui.components.AcademicEditorialHeader
import com.gtinnox.edutrack.ui.components.AcademicEmptyState
import com.gtinnox.edutrack.ui.components.OrganicCard
import com.gtinnox.edutrack.ui.theme.Dimens
import com.gtinnox.edutrack.ui.theme.PrimaryForest
import com.gtinnox.edutrack.ui.theme.SoftDanger
import com.gtinnox.edutrack.ui.theme.SoftSage
import com.gtinnox.edutrack.ui.theme.SoftSuccess
import com.gtinnox.edutrack.ui.theme.adaptiveHorizontalPadding
import com.gtinnox.edutrack.ui.viewmodel.HistoryViewModel
import kotlinx.coroutines.flow.collectLatest
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun formatSessionDateTime(isoString: String): String {
    return try {
        val instant = Instant.parse(isoString)
        val zdt = instant.atZone(ZoneId.systemDefault())
        val localDate = zdt.toLocalDate()
        val today = LocalDate.now()
        val yesterday = today.minusDays(1)
        val timeFormatter = DateTimeFormatter.ofPattern("h:mm a")
        val timeStr = zdt.format(timeFormatter)
        when (localDate) {
            today -> "Today · $timeStr"
            yesterday -> "Yesterday · $timeStr"
            else -> {
                val dateFormatter = DateTimeFormatter.ofPattern("MMM d")
                "${localDate.format(dateFormatter)} · $timeStr"
            }
        }
    } catch (e: Exception) {
        try {
            val localDate = LocalDate.parse(isoString.take(10))
            val today = LocalDate.now()
            val yesterday = today.minusDays(1)
            when (localDate) {
                today -> "Today"
                yesterday -> "Yesterday"
                else -> {
                    val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
                    localDate.format(dateFormatter)
                }
            }
        } catch (e2: Exception) {
            isoString.take(10)
        }
    }
}

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToEdit: (String) -> Unit = {},
    onNavigateBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val sessions by viewModel.filteredSessions.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    val selectedSubjectId by viewModel.selectedSubjectId.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val horizontalPadding = adaptiveHorizontalPadding()
    val snackbarHostState = remember { SnackbarHostState() }

    var sessionToDelete by remember { mutableStateOf<StudySessionEntity?>(null) }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    // Delete Confirmation Dialog
    sessionToDelete?.let { session ->
        AlertDialog(
            onDismissRequest = { sessionToDelete = null },
            title = {
                Text(
                    text = "Delete Study Session?",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to remove this ${session.durationMinutes}-minute focus block recorded on ${formatSessionDateTime(session.startTime)}? This will update your total academic study hours."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteSession(session)
                        sessionToDelete = null
                    }
                ) {
                    Text("Delete Session", color = SoftDanger, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { sessionToDelete = null }) {
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
                // Symmetrical Academic Editorial Header
                AcademicEditorialHeader(
                    title = "Study Session History",
                    category = "Academic Archive",
                    subtitle = "${sessions.size} Recorded Focus Blocks",
                    onBackClick = onNavigateBack
                )

                // Search Bar with clean proportions
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Filter sessions by reflection notes...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(Dimens.iconMedium)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Dimens.radiusMedium),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryForest,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    singleLine = true
                )

                // Subject Filter Chips with scroll and clean spacing
                if (subjects.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FilterChip(
                                selected = selectedSubjectId == null,
                                onClick = { viewModel.setSubjectFilter(null) },
                                label = { Text("All Courses") },
                                shape = RoundedCornerShape(Dimens.radiusChip),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryForest,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                        items(subjects) { s ->
                            val isSelected = selectedSubjectId == s.id
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setSubjectFilter(if (isSelected) null else s.id) },
                                label = { Text(s.code.ifBlank { s.name }) },
                                shape = RoundedCornerShape(Dimens.radiusChip),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SoftSage,
                                    selectedLabelColor = PrimaryForest
                                )
                            )
                        }
                    }
                }

                // Sessions List or Empty State
                if (sessions.isEmpty()) {
                    AcademicEmptyState(
                        icon = Icons.Default.History,
                        title = "No Focus Blocks Recorded",
                        description = if (searchQuery.isNotBlank()) "No study sessions match '$searchQuery'." else "Complete a focus interval or manually log a study block to build your academic ledger."
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(sessions) { sessionItem ->
                            SessionHistoryCard(
                                sessionItem = sessionItem,
                                onNavigateToDetail = onNavigateToDetail,
                                onNavigateToEdit = onNavigateToEdit,
                                onDeleteRequest = { sessionToDelete = it }
                            )
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

@Composable
private fun SessionHistoryCard(
    sessionItem: SessionWithSubject,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onDeleteRequest: (StudySessionEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val session = sessionItem.session
    val subject = sessionItem.subject
    var showMenu by remember { mutableStateOf(false) }

    OrganicCard(
        cornerRadius = Dimens.radiusMedium,
        contentPadding = Dimens.spaceStandard,
        onClick = { onNavigateToDetail(session.id) },
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // LEFT: Consistent 46dp rounded pastel sage surface with icon
            Surface(
                shape = RoundedCornerShape(Dimens.radiusSmall),
                color = SoftSage,
                modifier = Modifier
                    .size(46.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = PrimaryForest,
                        modifier = Modifier.size(Dimens.iconStandard)
                    )
                }
            }

            Spacer(modifier = Modifier.width(Dimens.spaceMedium))

            // CENTER: Flexible content column with weight(1f)
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // 1. Session Title (e.g. "60 min focus block")
                Text(
                    text = "${session.durationMinutes} min focus block",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                // 2. Metadata: [92% Focus] · Nov 26 · 8:15 PM
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        shape = RoundedCornerShape(Dimens.radiusChip),
                        color = if (session.focusScore >= 90) SoftSuccess.copy(alpha = 0.16f) else SoftSage
                    ) {
                        Text(
                            text = "${session.focusScore}% Focus",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = if (session.focusScore >= 90) SoftSuccess else PrimaryForest,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Text(
                        text = " · ",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = formatSessionDateTime(session.startTime),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // 3. Subject Name / Code (clean dot + label, never squeezed)
                if (subject != null) {
                    val subColor = try {
                        Color(android.graphics.Color.parseColor(subject.color))
                    } catch (e: Exception) {
                        PrimaryForest
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .background(subColor, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${subject.code} · ${subject.name}",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // 4. Reflection Preview (max 2 lines, only if not blank)
                if (session.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = session.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // RIGHT: Compact three-dot overflow menu
            Box(
                modifier = Modifier
                    .align(Alignment.Top)
                    .padding(start = 2.dp)
            ) {
                IconButton(
                    onClick = { showMenu = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Session Options",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("View Details") },
                        onClick = {
                            showMenu = false
                            onNavigateToDetail(session.id)
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Edit Session") },
                        onClick = {
                            showMenu = false
                            onNavigateToEdit(session.id)
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete Session", color = SoftDanger) },
                        onClick = {
                            showMenu = false
                            onDeleteRequest(session)
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Delete, contentDescription = null, tint = SoftDanger, modifier = Modifier.size(18.dp))
                        }
                    )
                }
            }
        }
    }
}
