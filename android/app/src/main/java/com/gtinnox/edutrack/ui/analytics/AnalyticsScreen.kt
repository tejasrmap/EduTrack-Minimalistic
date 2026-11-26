package com.gtinnox.edutrack.ui.analytics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gtinnox.edutrack.ui.components.AcademicCalendarHeatmap
import com.gtinnox.edutrack.ui.components.AcademicEditorialHeader
import com.gtinnox.edutrack.ui.components.FocusTrendChart
import com.gtinnox.edutrack.ui.components.MetricCard
import com.gtinnox.edutrack.ui.components.MinimalStudyBarChart
import com.gtinnox.edutrack.ui.components.MinimalSubjectDonutChart
import com.gtinnox.edutrack.ui.components.OrganicCard
import com.gtinnox.edutrack.ui.components.SectionHeader
import com.gtinnox.edutrack.ui.theme.Dimens
import com.gtinnox.edutrack.ui.theme.PrimaryForest
import com.gtinnox.edutrack.ui.theme.SoftSuccess
import com.gtinnox.edutrack.ui.theme.adaptiveHorizontalPadding
import com.gtinnox.edutrack.ui.viewmodel.AnalyticsViewModel

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val horizontalPadding = adaptiveHorizontalPadding()

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = Dimens.maxContentWidth)
                .padding(horizontal = horizontalPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.spaceSection)
        ) {
            item {
                AcademicEditorialHeader(
                    title = "Performance & Metrics",
                    category = "Academic Insights",
                    subtitle = "Weekly study distribution, curriculum split & focus momentum"
                )
            }

            // Summary Metric Cards Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.spaceMedium)
                ) {
                    MetricCard(
                        title = "Total Focus Time",
                        value = String.format("%.1fh", uiState.totalStudyHours),
                        subtitle = "${uiState.totalSessionsCount} recorded blocks",
                        icon = Icons.Default.Timer,
                        iconTint = PrimaryForest,
                        comparison = "+8% this week",
                        modifier = Modifier.weight(1f)
                    )

                    MetricCard(
                        title = "Focus Quality",
                        value = "${uiState.averageFocusScore}%",
                        subtitle = "Cognitive score",
                        icon = Icons.Default.Psychology,
                        iconTint = SoftSuccess,
                        comparison = "Optimal zone",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Weekly Study Duration Bar Chart
            item {
                OrganicCard(
                    cornerRadius = Dimens.radiusLarge,
                    contentPadding = Dimens.spaceLarge
                ) {
                    SectionHeader(
                        title = "Daily Focus Distribution",
                        badgeText = "Goal: 3h/day"
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Daily study minutes vs target threshold line (3h / day)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(Dimens.spaceMedium))
                    MinimalStudyBarChart(
                        trends = uiState.weeklyTrends,
                        targetMinutes = 180
                    )
                }
            }

            // Subject Breakdown Donut Chart
            item {
                OrganicCard(
                    cornerRadius = Dimens.radiusLarge,
                    contentPadding = Dimens.spaceLarge
                ) {
                    SectionHeader(
                        title = "Curriculum Allocation",
                        badgeText = "${uiState.subjectStats.size} Subjects"
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Proportional study time invested across academic subjects",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(Dimens.spaceMedium))
                    MinimalSubjectDonutChart(stats = uiState.subjectStats)
                }
            }

            // Focus Score Trend Line Chart
            if (uiState.focusTrendScores.isNotEmpty()) {
                item {
                    OrganicCard(
                        cornerRadius = Dimens.radiusLarge,
                        contentPadding = Dimens.spaceLarge
                    ) {
                        SectionHeader(
                            title = "Cognitive Focus Trend",
                            badgeText = "Recent Sessions"
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Historical focus scores recorded across consecutive study blocks",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(Dimens.spaceMedium))
                        FocusTrendChart(scores = uiState.focusTrendScores)
                    }
                }
            }

            // Academic Calendar Activity Heatmap
            item {
                OrganicCard(
                    cornerRadius = Dimens.radiusLarge,
                    contentPadding = Dimens.spaceLarge
                ) {
                    SectionHeader(
                        title = "Study Activity Heatmap",
                        badgeText = "Past 28 Days"
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Daily study consistency and volume density grid",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(Dimens.spaceStandard))

                    val sampleLevels = listOf(
                        2, 3, 1, 0, 3, 2, 2,
                        1, 2, 3, 3, 2, 0, 1,
                        3, 2, 2, 3, 1, 2, 3,
                        2, 3, 3, 2, 3, 2, 3
                    )
                    AcademicCalendarHeatmap(days = sampleLevels)
                }
            }

            item {
                Spacer(modifier = Modifier.height(84.dp))
            }
        }
    }
}
