package com.gtinnox.edutrack.ui.navigation

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WavingHand
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gtinnox.edutrack.ui.components.PremiumCard
import com.gtinnox.edutrack.ui.components.SectionHeader
import com.gtinnox.edutrack.ui.theme.EduTrackBorderLight
import com.gtinnox.edutrack.ui.theme.EduTrackDanger
import com.gtinnox.edutrack.ui.theme.EduTrackPrimary
import com.gtinnox.edutrack.ui.theme.EduTrackPrimarySoft
import com.gtinnox.edutrack.ui.theme.EduTrackSuccess
import com.gtinnox.edutrack.ui.theme.EduTrackWarning

data class MenuItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val iconColor: Color,
    val route: String
)

@Composable
fun MoreMenuScreen(
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val academicItems = listOf(
        MenuItem(
            title = "Study Session History",
            subtitle = "Comprehensive logs of focus blocks and reflections",
            icon = Icons.Default.History,
            iconColor = EduTrackPrimary,
            route = Screen.History.route
        ),
        MenuItem(
            title = "Assignments & Deliverables",
            subtitle = "Homework, problem sets, lab reports, and deadlines",
            icon = Icons.Default.Assignment,
            iconColor = EduTrackWarning,
            route = Screen.Assignments.route
        ),
        MenuItem(
            title = "Examinations Portal",
            subtitle = "Midterms, finals, quiz countdowns, and targets",
            icon = Icons.Default.School,
            iconColor = EduTrackPrimary,
            route = Screen.Examinations.route
        ),
        MenuItem(
            title = "Milestone Goals & Quotas",
            subtitle = "Weekly study targets and academic milestones",
            icon = Icons.Default.Flag,
            iconColor = EduTrackSuccess,
            route = Screen.Goals.route
        ),
        MenuItem(
            title = "Revision Notes & Formulas",
            subtitle = "Markdown notes, theorem summaries, and tags",
            icon = Icons.Default.MenuBook,
            iconColor = EduTrackPrimary,
            route = Screen.Notes.route
        )
    )

    val systemItems = listOf(
        MenuItem(
            title = "Scholar Identity & Profile",
            subtitle = "Switch academic personas or customize degree details",
            icon = Icons.Default.Person,
            iconColor = EduTrackPrimary,
            route = Screen.Profile.route
        ),
        MenuItem(
            title = "System Settings & Data",
            subtitle = "Theme, Pomodoro intervals, Room export/import",
            icon = Icons.Default.Settings,
            iconColor = EduTrackSuccess,
            route = Screen.Settings.route
        ),
        MenuItem(
            title = "Notification Center",
            subtitle = "Academic alerts, countdowns, and scheduled reviews",
            icon = Icons.Default.Notifications,
            iconColor = EduTrackWarning,
            route = Screen.Notifications.route
        ),
        MenuItem(
            title = "Onboarding Walkthrough",
            subtitle = "Revisit setup wizard and academic features",
            icon = Icons.Default.WavingHand,
            iconColor = EduTrackPrimary,
            route = Screen.Onboarding.route
        )
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Academic Suite Hub",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Coursework modules, scholar tools & preferences",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Academic Modules Section
        item {
            SectionHeader(title = "Academic Modules")
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                academicItems.forEach { item ->
                    MoreMenuRow(item = item, onClick = { onNavigate(item.route) })
                }
            }
        }

        // System & Persona Section
        item {
            SectionHeader(title = "System & Settings")
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                systemItems.forEach { item ->
                    MoreMenuRow(item = item, onClick = { onNavigate(item.route) })
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun MoreMenuRow(
    item: MenuItem,
    onClick: () -> Unit
) {
    PremiumCard(
        contentPadding = 14.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(42.dp),
                shape = RoundedCornerShape(12.dp),
                color = item.iconColor.copy(alpha = 0.12f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = item.iconColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
