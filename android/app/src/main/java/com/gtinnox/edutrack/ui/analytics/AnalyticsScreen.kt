package com.gtinnox.edutrack.ui.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gtinnox.edutrack.ui.components.AcademicCard
import com.gtinnox.edutrack.ui.components.FocusTrendChart
import com.gtinnox.edutrack.ui.components.MetricCard
import com.gtinnox.edutrack.ui.components.MinimalStudyBarChart
import com.gtinnox.edutrack.ui.components.MinimalSubjectDonutChart
import com.gtinnox.edutrack.ui.theme.AcademicAmber
import com.gtinnox.edutrack.ui.theme.AcademicIndigo
import com.gtinnox.edutrack.ui.theme.AcademicPurple
import com.gtinnox.edutrack.ui.theme.AcademicSage
import com.gtinnox.edutrack.ui.viewmodel.AnalyticsViewModel

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Academic Analytics",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Weekly Study Trends & Cognitive Focus Metrics",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Summary Metric Cards Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Total Time",
                    value = String.format("%.1fh", uiState.totalStudyHours),
                    subtitle = "${uiState.totalSessionsCount} recorded blocks",
                    icon = Icons.Default.Timer,
                    iconTint = AcademicIndigo,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "Focus Quality",
                    value = "${uiState.averageFocusScore}%",
                    subtitle = "Cognitive score",
                    icon = Icons.Default.Psychology,
                    iconTint = AcademicSage,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Weekly Study Duration Bar Chart
        item {
            AcademicCard {
                Text(
                    text = "Weekly Study Duration Distribution",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Daily study minutes vs target threshold line (3h / day)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                MinimalStudyBarChart(
                    trends = uiState.weeklyTrends,
                    targetMinutes = 120
                )
            }
        }

        // Subject Study Time Donut Distribution
        item {
            AcademicCard {
                Text(
                    text = "Curriculum Time Breakdown",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Percentage distribution across enrolled modules",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                MinimalSubjectDonutChart(
                    stats = uiState.subjectStats
                )
            }
        }

        // Focus Quality Trend Chart
        item {
            AcademicCard {
                Text(
                    text = "Focus Score Chronology",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Self-assessed cognitive attention scores across sessions",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                FocusTrendChart(
                    scores = listOf(85, 92, 88, 95, 90, 84, 96, 92)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
