package com.gtinnox.edutrack.ui.subjects

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.gtinnox.edutrack.ui.theme.SoftSage
import com.gtinnox.edutrack.ui.theme.SoftSuccess
import com.gtinnox.edutrack.ui.theme.adaptiveHorizontalPadding
import com.gtinnox.edutrack.ui.viewmodel.SubjectViewModel

@Composable
fun SubjectsScreen(
    viewModel: SubjectViewModel,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val subjectsWithSessions by viewModel.allSubjectsWithSessions.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val horizontalPadding = adaptiveHorizontalPadding()

    val filteredList = subjectsWithSessions.filter {
        it.subject.name.contains(searchQuery, ignoreCase = true) ||
        it.subject.code.contains(searchQuery, ignoreCase = true) ||
        it.subject.instructor.contains(searchQuery, ignoreCase = true)
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
            // Standardized Academic Editorial Header
            AcademicEditorialHeader(
                title = "Enrolled Modules",
                category = "Academic Curriculum",
                trailingAction = {
                    Button(
                        onClick = onNavigateToAdd,
                        shape = RoundedCornerShape(Dimens.radiusMedium),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryForest),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(Dimens.iconSmall))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Add Subject",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            )

            // Search Bar with standardized 18dp corner radius
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by code, name, or instructor...") },
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

            if (filteredList.isEmpty()) {
                AcademicEmptyState(
                    icon = Icons.Default.Book,
                    title = "No Academic Modules Found",
                    description = if (searchQuery.isNotBlank()) "No subjects match '$searchQuery'." else "You haven't enrolled in any courses yet.",
                    actionText = "Enroll Subject",
                    onActionClick = onNavigateToAdd
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(Dimens.spaceMedium),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredList) { item ->
                        val subject = item.subject
                        val totalMinutes = item.sessions.sumOf { it.durationMinutes }
                        val targetMinutes = (subject.weeklyTargetHours * 60).coerceAtLeast(1)
                        val progress = (totalMinutes.toFloat() / targetMinutes.toFloat()).coerceIn(0f, 1f)

                        val subColor = try {
                            Color(android.graphics.Color.parseColor(subject.color))
                        } catch (e: Exception) {
                            PrimaryForest
                        }

                        OrganicCard(
                            cornerRadius = Dimens.radiusLarge,
                            contentPadding = Dimens.spaceStandard,
                            onClick = { onNavigateToDetail(subject.id) }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(Dimens.radiusSmall),
                                    color = subColor.copy(alpha = 0.14f),
                                    modifier = Modifier.size(46.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = subject.code.take(3).uppercase(),
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = subColor
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(Dimens.spaceMedium))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = subject.name,
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f, fill = false)
                                        )
                                        Spacer(modifier = Modifier.width(Dimens.spaceSmall))
                                        Surface(
                                            shape = RoundedCornerShape(Dimens.radiusChip),
                                            color = SoftSage.copy(alpha = 0.6f)
                                        ) {
                                            Text(
                                                text = "${subject.weeklyTargetHours}h/wk",
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                color = PrimaryForest,
                                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    if (subject.instructor.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Prof. ${subject.instructor}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(Dimens.spaceSmall))

                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "View",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(Dimens.iconSmall)
                                )
                            }

                            Spacer(modifier = Modifier.height(Dimens.spaceMedium))

                            // Weekly Target Progress
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${totalMinutes / 60}h ${totalMinutes % 60}m studied",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${(progress * 100).toInt()}% of goal",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (progress >= 1f) SoftSuccess else PrimaryForest
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = subColor,
                                trackColor = SoftSage.copy(alpha = 0.6f)
                            )
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
