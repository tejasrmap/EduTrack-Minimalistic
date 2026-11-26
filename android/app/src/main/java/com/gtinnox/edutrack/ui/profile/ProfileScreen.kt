package com.gtinnox.edutrack.ui.profile

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import com.gtinnox.edutrack.ui.theme.DeepForest
import com.gtinnox.edutrack.ui.theme.Dimens
import com.gtinnox.edutrack.ui.theme.PrimaryForest
import com.gtinnox.edutrack.ui.theme.SoftSage
import com.gtinnox.edutrack.ui.theme.SoftSuccess
import com.gtinnox.edutrack.ui.theme.adaptiveHorizontalPadding
import com.gtinnox.edutrack.ui.viewmodel.ProfileViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.userProfile.collectAsState()
    val personas = viewModel.personas
    val horizontalPadding = adaptiveHorizontalPadding()

    var name by remember(profile) { mutableStateOf(profile?.name ?: "Julian Vance") }
    var email by remember(profile) { mutableStateOf(profile?.email ?: "julian.vance@university.edu") }
    var university by remember(profile) { mutableStateOf(profile?.university ?: "Oxford University") }
    var program by remember(profile) { mutableStateOf(profile?.program ?: "B.Sc. Computer Science & Mathematics") }
    var year by remember(profile) { mutableStateOf(profile?.academicYear ?: "Year 2") }
    var semester by remember(profile) { mutableStateOf(profile?.semester ?: "Fall 2026") }
    var hoursText by remember(profile) { mutableStateOf(profile?.preferredStudyHours?.toString() ?: "25") }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
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
                        title = "Academic Profile",
                        category = "Scholar Dossier",
                        onBackClick = onNavigateBack
                    )
                }

                // Profile Avatar Hero Card
                item {
                    OrganicCard(
                        cornerRadius = Dimens.radiusXLarge,
                        contentPadding = Dimens.spaceLarge
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = SoftSage,
                                modifier = Modifier.size(68.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = profile?.avatar ?: "JV",
                                        style = MaterialTheme.typography.displayMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = DeepForest
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(Dimens.spaceLarge))

                            Column {
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = program,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "$university · $year",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PrimaryForest
                                )
                            }
                        }
                    }
                }

                // Preset Academic Personas
                item {
                    SectionHeader(title = "Preset Scholar Personas")
                    Spacer(modifier = Modifier.height(Dimens.spaceSmall))
                    Text(
                        text = "Quickly switch between pre-configured academic profiles with customized curricula",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                items(personas) { persona ->
                    val isSelected = profile?.name == persona.name
                    OrganicCard(
                        cornerRadius = Dimens.radiusMedium,
                        contentPadding = Dimens.spaceStandard,
                        onClick = { viewModel.applyPersona(persona) }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (isSelected) PrimaryForest else SoftSage,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = persona.avatar,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else DeepForest
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(Dimens.spaceMedium))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = persona.name,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${persona.program} · ${persona.university}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Active",
                                    tint = SoftSuccess,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }

                // Edit Profile Fields Card
                item {
                    OrganicCard(
                        cornerRadius = Dimens.radiusLarge,
                        contentPadding = Dimens.spaceLarge
                    ) {
                        SectionHeader(title = "Custom Scholar Details")
                        Spacer(modifier = Modifier.height(Dimens.spaceMedium))

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Scholar Full Name") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(Dimens.radiusMedium)
                        )

                        Spacer(modifier = Modifier.height(Dimens.spaceMedium))

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Institutional Email") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(Dimens.radiusMedium)
                        )

                        Spacer(modifier = Modifier.height(Dimens.spaceMedium))

                        OutlinedTextField(
                            value = university,
                            onValueChange = { university = it },
                            label = { Text("University / Institution") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(Dimens.radiusMedium)
                        )

                        Spacer(modifier = Modifier.height(Dimens.spaceMedium))

                        OutlinedTextField(
                            value = program,
                            onValueChange = { program = it },
                            label = { Text("Academic Program / Major") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(Dimens.radiusMedium)
                        )

                        Spacer(modifier = Modifier.height(Dimens.spaceMedium))

                        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.spaceSmall)) {
                            OutlinedTextField(
                                value = year,
                                onValueChange = { year = it },
                                label = { Text("Year") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(Dimens.radiusSmall)
                            )
                            OutlinedTextField(
                                value = semester,
                                onValueChange = { semester = it },
                                label = { Text("Semester") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(Dimens.radiusSmall)
                            )
                            OutlinedTextField(
                                value = hoursText,
                                onValueChange = { hoursText = it },
                                label = { Text("Target h/wk") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(Dimens.radiusSmall)
                            )
                        }

                        Spacer(modifier = Modifier.height(Dimens.spaceLarge))

                        Button(
                            onClick = {
                                viewModel.updateProfile(
                                    name = name,
                                    email = email,
                                    university = university,
                                    program = program,
                                    academicYear = year,
                                    semester = semester,
                                    preferredStudyHours = hoursText.toIntOrNull() ?: 25
                                )
                            },
                            modifier = Modifier.fillMaxWidth().height(Dimens.buttonHeight),
                            shape = RoundedCornerShape(Dimens.radiusMedium),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryForest)
                        ) {
                            Text("Save Academic Dossier", fontWeight = FontWeight.Bold)
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
