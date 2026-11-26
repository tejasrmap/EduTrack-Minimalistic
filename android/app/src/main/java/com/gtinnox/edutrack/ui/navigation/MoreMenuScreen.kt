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
import com.gtinnox.edutrack.ui.components.AcademicCard
import com.gtinnox.edutrack.ui.theme.AcademicAmber
import com.gtinnox.edutrack.ui.theme.AcademicIndigo
import com.gtinnox.edutrack.ui.theme.AcademicPurple
import com.gtinnox.edutrack.ui.theme.AcademicRose
import com.gtinnox.edutrack.ui.theme.AcademicSage
import com.gtinnox.edutrack.ui.theme.AcademicSky

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
    val items = listOf(
        MenuItem(
            title = "Study Session History",
            subtitle = "Comprehensive logs of focus blocks and reflections",
            icon = Icons.Default.History,
            iconColor = AcademicIndigo,
            route = Screen.History.route
        ),
        MenuItem(
            title = "Assignments & Deliverables",
            subtitle = "Homework, problem sets, lab reports, and deadlines",
            icon = Icons.Default.Assignment,
            iconColor = AcademicAmber,
            route = Screen.Assignments.route
        ),
        MenuItem(
            title = "Examinations Portal",
            subtitle = "Midterms, finals, quiz countdowns, and targets",
            icon = Icons.Default.School,
            iconColor = AcademicPurple,
            route = Screen.Examinations.route
        ),
        MenuItem(
            title = "Milestone Goals & Quotas",
            subtitle = "Weekly study targets and academic milestones",
            icon = Icons.Default.Flag,
            iconColor = AcademicSage,
            route = Screen.Goals.route
        ),
        MenuItem(
            title = "Revision Notes & Formulas",
            subtitle = "Markdown notes, theorem summaries, and tags",
            icon = Icons.Default.MenuBook,
            iconColor = AcademicSky,
            route = Screen.Notes.route
        ),
        MenuItem(
            title = "Scholar Identity & Personas",
            subtitle = "Switch evaluation scholar profiles or customize degree",
            icon = Icons.Default.Person,
            iconColor = AcademicIndigo,
            route = Screen.Profile.route
        ),
        MenuItem(
            title = "System Settings & Data Backup",
            subtitle = "Theme, timer lengths, JSON database export & import",
            icon = Icons.Default.Settings,
            iconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            route = Screen.Settings.route
        ),
        MenuItem(
            title = "Notifications & Deadlines",
            subtitle = "Academic reminders and study streak logs",
            icon = Icons.Default.Notifications,
            iconColor = AcademicRose,
            route = Screen.Notifications.route
        )
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Academic Workspace Suite",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Navigation Hub for All Coursework Modules",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        items(items) { item ->
            AcademicCard(
                onClick = { onNavigate(item.route) }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = item.iconColor.copy(alpha = 0.12f),
                        modifier = Modifier.size(42.dp)
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
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
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
                        contentDescription = "Navigate",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
