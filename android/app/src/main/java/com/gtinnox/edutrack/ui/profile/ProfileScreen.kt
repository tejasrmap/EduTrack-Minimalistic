package com.gtinnox.edutrack.ui.profile

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gtinnox.edutrack.ui.components.AcademicEditorialHeader
import com.gtinnox.edutrack.ui.components.EduTrackDialog
import com.gtinnox.edutrack.ui.components.OrganicCard
import com.gtinnox.edutrack.ui.components.SectionHeader
import com.gtinnox.edutrack.ui.theme.Dimens
import com.gtinnox.edutrack.ui.theme.PrimaryForest
import com.gtinnox.edutrack.ui.theme.SoftDanger
import com.gtinnox.edutrack.ui.theme.SoftSage
import com.gtinnox.edutrack.ui.theme.adaptiveHorizontalPadding
import com.gtinnox.edutrack.ui.viewmodel.ProfileViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val user by viewModel.currentUser.collectAsState()
    val profile by viewModel.currentProfile.collectAsState()
    val preferences by viewModel.currentPreferences.collectAsState()
    val horizontalPadding = adaptiveHorizontalPadding()
    val snackbarHostState = remember { SnackbarHostState() }

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.eventFlow.collectLatest { msg ->
            snackbarHostState.showSnackbar(msg)
        }
    }

    val initials = (user?.fullName ?: "Scholar")
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .map { it.first().uppercase() }
        .joinToString("")

    // Edit Profile Dialog
    if (showEditProfileDialog) {
        var editName by remember { mutableStateOf(user?.fullName ?: "") }
        var editUni by remember { mutableStateOf(profile?.institution ?: "") }
        var editProgram by remember { mutableStateOf(profile?.degree ?: "") }
        var editYear by remember { mutableStateOf(profile?.academicYear ?: "") }
        var editSemester by remember { mutableStateOf(profile?.semester ?: "") }
        var editStudentId by remember { mutableStateOf(profile?.studentId ?: "") }

        EduTrackDialog(
            title = "Edit Academic Profile",
            icon = Icons.Default.Edit,
            onDismissRequest = { showEditProfileDialog = false },
            confirmText = "Save Changes",
            onConfirm = {
                viewModel.updateProfile(
                    name = editName,
                    university = editUni,
                    program = editProgram,
                    academicYear = editYear,
                    semester = editSemester,
                    studentId = editStudentId
                )
                showEditProfileDialog = false
            }
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = editName,
                    onValueChange = { editName = it },
                    label = { Text("Full Name") },
                    singleLine = true,
                    shape = RoundedCornerShape(Dimens.radiusMedium),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryForest)
                )
                OutlinedTextField(
                    value = editUni,
                    onValueChange = { editUni = it },
                    label = { Text("Institution / University") },
                    singleLine = true,
                    shape = RoundedCornerShape(Dimens.radiusMedium),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryForest)
                )
                OutlinedTextField(
                    value = editProgram,
                    onValueChange = { editProgram = it },
                    label = { Text("Degree & Major") },
                    singleLine = true,
                    shape = RoundedCornerShape(Dimens.radiusMedium),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryForest)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editYear,
                        onValueChange = { editYear = it },
                        label = { Text("Year") },
                        singleLine = true,
                        shape = RoundedCornerShape(Dimens.radiusMedium),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryForest)
                    )
                    OutlinedTextField(
                        value = editSemester,
                        onValueChange = { editSemester = it },
                        label = { Text("Semester") },
                        singleLine = true,
                        shape = RoundedCornerShape(Dimens.radiusMedium),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryForest)
                    )
                }
                OutlinedTextField(
                    value = editStudentId,
                    onValueChange = { editStudentId = it },
                    label = { Text("Student ID (Optional)") },
                    singleLine = true,
                    shape = RoundedCornerShape(Dimens.radiusMedium),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryForest)
                )
            }
        }
    }

    // Change Password Dialog
    if (showChangePasswordDialog) {
        var currentPass by remember { mutableStateOf("") }
        var newPass by remember { mutableStateOf("") }
        var confirmPass by remember { mutableStateOf("") }

        EduTrackDialog(
            title = "Change Password",
            icon = Icons.Default.Lock,
            onDismissRequest = { showChangePasswordDialog = false },
            confirmText = "Update Password",
            onConfirm = {
                viewModel.changePassword(currentPass, newPass, confirmPass) {
                    showChangePasswordDialog = false
                }
            }
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = currentPass,
                    onValueChange = { currentPass = it },
                    label = { Text("Current Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(Dimens.radiusMedium),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryForest)
                )
                OutlinedTextField(
                    value = newPass,
                    onValueChange = { newPass = it },
                    label = { Text("New Password (min 6 chars)") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(Dimens.radiusMedium),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryForest)
                )
                OutlinedTextField(
                    value = confirmPass,
                    onValueChange = { confirmPass = it },
                    label = { Text("Confirm New Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(Dimens.radiusMedium),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryForest)
                )
            }
        }
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        EduTrackDialog(
            title = "Log Out of EduTrack?",
            icon = Icons.Default.Logout,
            confirmColor = SoftDanger,
            confirmText = "Log Out",
            dismissText = "Cancel",
            onDismissRequest = { showLogoutDialog = false },
            onConfirm = {
                showLogoutDialog = false
                viewModel.logout(onLogout)
            }
        ) {
            Text(
                text = "Are you sure you want to log out? Your local study telemetry, enrolled courses, and planner records remain safely stored on this device.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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
                verticalArrangement = Arrangement.spacedBy(Dimens.spaceStandard)
            ) {
                item {
                    AcademicEditorialHeader(
                        title = "Scholar Profile",
                        category = "Academic Identity",
                        subtitle = user?.email ?: "Offline Account",
                        onBackClick = onNavigateBack
                    )
                }

                // Profile Hero Card
                item {
                    OrganicCard(
                        cornerRadius = Dimens.radiusLarge,
                        contentPadding = Dimens.spaceLarge
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = CircleShape,
                                    color = SoftSage,
                                    modifier = Modifier.size(64.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = initials,
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = PrimaryForest
                                            )
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(Dimens.spaceMedium))
                                Column {
                                    Text(
                                        text = user?.fullName ?: "Scholar",
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = user?.email ?: "scholar@university.edu",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(Dimens.radiusChip),
                                        color = SoftSage.copy(alpha = 0.5f),
                                        modifier = Modifier.padding(top = 4.dp)
                                    ) {
                                        Text(
                                            text = "AUTHENTICATED SCHOLAR",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = PrimaryForest,
                                                letterSpacing = 1.sp
                                            ),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            IconButton(onClick = { showEditProfileDialog = true }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Profile",
                                    tint = PrimaryForest
                                )
                            }
                        }
                    }
                }

                // Academic Details Section
                item {
                    SectionHeader(title = "Academic Program")
                    OrganicCard(
                        cornerRadius = Dimens.radiusMedium,
                        contentPadding = Dimens.spaceMedium
                    ) {
                        ProfileAttributeRow("Institution", profile?.institution?.ifBlank { "Unassigned" } ?: "Oxford University")
                        ProfileAttributeRow("Degree & Major", profile?.degree?.ifBlank { "Computer Science" } ?: "B.Sc. Computer Science")
                        ProfileAttributeRow("Academic Year", profile?.academicYear?.ifBlank { "Year 2" } ?: "Year 2")
                        ProfileAttributeRow("Current Semester", profile?.semester?.ifBlank { "Fall 2026" } ?: "Fall 2026")
                        if (!profile?.studentId.isNullOrBlank()) {
                            ProfileAttributeRow("Student ID", profile?.studentId ?: "")
                        }
                    }
                }

                // Study Preferences Summary
                item {
                    SectionHeader(title = "Study Telemetry Targets")
                    OrganicCard(
                        cornerRadius = Dimens.radiusMedium,
                        contentPadding = Dimens.spaceMedium
                    ) {
                        ProfileAttributeRow("Daily Study Target", "${(preferences?.dailyStudyGoalMinutes ?: 180) / 60} Hours / Day")
                        ProfileAttributeRow("Weekly Target", "${preferences?.weeklyStudyGoalHours ?: 20} Hours / Week")
                        ProfileAttributeRow("Focus Block Duration", "${preferences?.focusDurationMinutes ?: 25} Minutes")
                        ProfileAttributeRow("Break Interval", "${preferences?.shortBreakMinutes ?: 5} Minutes")
                    }
                }

                // Account Actions
                item {
                    SectionHeader(title = "Account & System")
                    OrganicCard(
                        cornerRadius = Dimens.radiusMedium,
                        contentPadding = Dimens.spaceMedium
                    ) {
                        ActionMenuRow(
                            icon = Icons.Default.Lock,
                            title = "Change Password",
                            subtitle = "Update your offline salted password hash",
                            onClick = { showChangePasswordDialog = true }
                        )
                        ActionMenuRow(
                            icon = Icons.Default.HelpOutline,
                            title = "Reset Help & Tutorials",
                            subtitle = "Replay contextual guided tours across screens",
                            onClick = { viewModel.resetTutorials() }
                        )
                        ActionMenuRow(
                            icon = Icons.Default.Settings,
                            title = "Preferences & Backup",
                            subtitle = "Theme, data export, and notification preferences",
                            onClick = onNavigateToSettings
                        )
                        ActionMenuRow(
                            icon = Icons.Default.Logout,
                            title = "Sign Out",
                            subtitle = "Safely end current authenticated session",
                            textColor = SoftDanger,
                            iconColor = SoftDanger,
                            onClick = { showLogoutDialog = true }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(64.dp))
                }
            }
        }
    }
}

@Composable
private fun ProfileAttributeRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ActionMenuRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    textColor: Color = Color.Unspecified,
    iconColor: Color = PrimaryForest,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = iconColor.copy(alpha = 0.12f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(Dimens.spaceMedium))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = if (textColor != Color.Unspecified) textColor else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
