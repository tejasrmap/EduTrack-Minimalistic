package com.gtinnox.edutrack.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.gtinnox.edutrack.ui.components.EduTrackDialog
import com.gtinnox.edutrack.ui.theme.Dimens
import com.gtinnox.edutrack.ui.theme.PrimaryForest
import com.gtinnox.edutrack.ui.theme.SoftDanger

@Composable
fun ForgotPasswordDialog(
    initialEmail: String = "",
    onDismiss: () -> Unit,
    onResetRequested: (String, String) -> Unit
) {
    var email by remember { mutableStateOf(initialEmail) }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }

    EduTrackDialog(
        title = "Reset Password",
        icon = Icons.Default.LockReset,
        onDismissRequest = onDismiss,
        confirmText = "Reset Password",
        dismissText = "Cancel",
        onConfirm = {
            if (email.isBlank()) {
                errorText = "Please enter your email address."
                return@EduTrackDialog
            }
            if (newPassword.length < 6) {
                errorText = "Password must be at least 6 characters."
                return@EduTrackDialog
            }
            if (newPassword != confirmPassword) {
                errorText = "Passwords do not match."
                return@EduTrackDialog
            }
            onResetRequested(email, newPassword)
        }
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceSmall),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Enter your registered email address and choose a new password for your offline account.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (errorText != null) {
                Surface(
                    shape = RoundedCornerShape(Dimens.radiusSmall),
                    color = SoftDanger.copy(alpha = 0.12f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorText ?: "",
                        style = MaterialTheme.typography.labelSmall,
                        color = SoftDanger,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    errorText = null
                },
                label = { Text("Account Email") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(Dimens.radiusMedium),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryForest
                )
            )

            OutlinedTextField(
                value = newPassword,
                onValueChange = {
                    newPassword = it
                    errorText = null
                },
                label = { Text("New Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                shape = RoundedCornerShape(Dimens.radiusMedium),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryForest
                )
            )

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    errorText = null
                },
                label = { Text("Confirm New Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                shape = RoundedCornerShape(Dimens.radiusMedium),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryForest
                )
            )
        }
    }
}
