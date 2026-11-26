package com.gtinnox.edutrack.ui.notes

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.gtinnox.edutrack.ui.components.AcademicEditorialHeader
import com.gtinnox.edutrack.ui.components.AcademicEmptyState
import com.gtinnox.edutrack.ui.components.OrganicCard
import com.gtinnox.edutrack.ui.components.SubjectChip
import com.gtinnox.edutrack.ui.theme.Dimens
import com.gtinnox.edutrack.ui.theme.PrimaryForest
import com.gtinnox.edutrack.ui.theme.SoftDanger
import com.gtinnox.edutrack.ui.theme.SoftSage
import com.gtinnox.edutrack.ui.theme.SoftTerracotta
import com.gtinnox.edutrack.ui.theme.adaptiveHorizontalPadding
import com.gtinnox.edutrack.ui.viewmodel.NoteViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun NotesScreen(
    viewModel: NoteViewModel,
    modifier: Modifier = Modifier
) {
    val notes by viewModel.filteredNotes.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var selectedSubjectId by remember { mutableStateOf<String?>(null) }
    val displayNotes = if (selectedSubjectId == null) notes else notes.filter { it.subjectId == selectedSubjectId }

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
        var content by remember { mutableStateOf("") }
        var subjectId by remember { mutableStateOf(subjects.firstOrNull()?.id ?: "") }
        var tags by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(
                    text = "New Knowledge Entry",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Note Title *") },
                        placeholder = { Text("e.g. Master Theorem & Complexity") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(Dimens.radiusMedium),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryForest,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    Text("Course *", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(subjects) { s ->
                            FilterChip(
                                selected = subjectId == s.id,
                                onClick = { subjectId = s.id },
                                label = { Text(s.code.ifBlank { s.name }) },
                                shape = RoundedCornerShape(Dimens.radiusChip),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SoftSage,
                                    selectedLabelColor = PrimaryForest
                                )
                            )
                        }
                    }

                    OutlinedTextField(
                        value = tags,
                        onValueChange = { tags = it },
                        label = { Text("Tags (comma-separated)") },
                        placeholder = { Text("theorems, algorithms, midterms") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(Dimens.radiusSmall)
                    )

                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Content & Key Formulas *") },
                        placeholder = { Text("Write formulas, definitions, and proof outlines...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(Dimens.radiusSmall),
                        minLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            viewModel.saveNote(
                                id = null,
                                title = title,
                                content = content,
                                subjectId = subjectId,
                                tags = tags
                            )
                            showAddDialog = false
                        }
                    },
                    shape = RoundedCornerShape(Dimens.radiusMedium),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryForest)
                ) {
                    Text("Save Note", fontWeight = FontWeight.Bold)
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
                    title = "Notes & Formulas",
                    category = "Academic Knowledge",
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
                            Text("New Note", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                )

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.setSearch(it) },
                    placeholder = { Text("Search syllabus notes, theorems, tags...") },
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

                // Subject filter strip
                if (subjects.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FilterChip(
                                selected = selectedSubjectId == null,
                                onClick = { selectedSubjectId = null },
                                label = { Text("All Courses") },
                                shape = RoundedCornerShape(Dimens.radiusChip),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryForest,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                        items(subjects) { s ->
                            FilterChip(
                                selected = selectedSubjectId == s.id,
                                onClick = { selectedSubjectId = if (selectedSubjectId == s.id) null else s.id },
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

                if (displayNotes.isEmpty()) {
                    AcademicEmptyState(
                        icon = Icons.Default.MenuBook,
                        title = "No Academic Notes Found",
                        description = if (searchQuery.isNotBlank()) "No notes match '$searchQuery'." else "Keep course summaries, proofs, and definitions here.",
                        actionText = "Add Note",
                        onActionClick = { showAddDialog = true }
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(displayNotes) { note ->
                            val sub = subjects.find { it.id == note.subjectId }

                            OrganicCard(
                                cornerRadius = Dimens.radiusMedium,
                                contentPadding = Dimens.spaceStandard
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = note.title,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f, fill = false)
                                        )

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            IconButton(
                                                onClick = { viewModel.toggleFavorite(note) },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (note.isFavourite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                                    contentDescription = "Favorite",
                                                    tint = if (note.isFavourite) SoftTerracotta else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(4.dp))

                                            IconButton(
                                                onClick = { viewModel.deleteNote(note) },
                                                modifier = Modifier.size(28.dp)
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

                                    if (note.content.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = note.content,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    val tagList = note.tags.split(",").map { it.trim() }.filter { it.isNotBlank() }
                                    if (tagList.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(Dimens.spaceSmall))
                                        LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            items(tagList) { tag ->
                                                Surface(
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = SoftSage.copy(alpha = 0.6f)
                                                ) {
                                                    Text(
                                                        text = "#$tag",
                                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                                        color = PrimaryForest,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    if (sub != null) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        SubjectChip(name = sub.name, colorHex = sub.color, code = sub.code)
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
