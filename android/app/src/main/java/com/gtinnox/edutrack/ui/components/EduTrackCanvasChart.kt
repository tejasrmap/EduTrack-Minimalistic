package com.gtinnox.edutrack.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gtinnox.edutrack.ui.theme.DeepForest
import com.gtinnox.edutrack.ui.theme.MutedSage
import com.gtinnox.edutrack.ui.theme.PrimaryForest
import com.gtinnox.edutrack.ui.theme.SoftSage
import com.gtinnox.edutrack.ui.theme.SoftSuccess
import com.gtinnox.edutrack.ui.viewmodel.DayTrend
import com.gtinnox.edutrack.ui.viewmodel.SubjectStudyStat
import java.time.LocalDate

// ==========================================
// Clean 7-Day Activity Chart (Sitting Naturally on Background)
// ==========================================
@Composable
fun SoftAcademiaWeeklyActivity(
    trends: List<DayTrend>,
    modifier: Modifier = Modifier
) {
    val totalMins = trends.sumOf { it.studyMinutes }
    val maxMins = (trends.maxOfOrNull { it.studyMinutes } ?: 180).coerceAtLeast(60)

    val currentDayIndex = LocalDate.now().dayOfWeek.value - 1 // 0=Mon, 6=Sun

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Weekly Activity",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "${totalMins / 60}h ${totalMins % 60}m total",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Canvas 7 rounded bars
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val barCount = trends.size
            if (barCount == 0) return@Canvas

            val barWidth = 24.dp.toPx()
            val totalBarsWidth = barWidth * barCount
            val spacing = (canvasWidth - totalBarsWidth) / (barCount + 1)

            trends.forEachIndexed { index, day ->
                val x = spacing + index * (barWidth + spacing)
                val heightFactor = (day.studyMinutes.toFloat() / maxMins.toFloat()).coerceIn(0.08f, 1f)
                val barHeight = canvasHeight * heightFactor
                val y = canvasHeight - barHeight

                val isToday = index == currentDayIndex.coerceIn(0, barCount - 1)
                val barColor = if (isToday) PrimaryForest else SoftSage

                // Draw rounded bar
                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx())
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Days of week row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            trends.forEachIndexed { index, day ->
                val isToday = index == currentDayIndex.coerceIn(0, trends.size - 1)
                Text(
                    text = day.dayLabel.take(1),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                        color = if (isToday) PrimaryForest else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}

// Backward-compatible alias
@Composable
fun MinimalStudyBarChart(
    trends: List<DayTrend>,
    targetMinutes: Int = 180,
    modifier: Modifier = Modifier
) {
    SoftAcademiaWeeklyActivity(trends = trends, modifier = modifier)
}

@Composable
fun MinimalSubjectDonutChart(
    stats: List<SubjectStudyStat>,
    modifier: Modifier = Modifier
) {
    if (stats.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No study data logged yet", style = MaterialTheme.typography.bodyMedium)
        }
        return
    }

    val totalMins = stats.sumOf { it.totalMinutes }.coerceAtLeast(1)

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(160.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(144.dp)) {
                var startAngle = -90f
                val stroke = Stroke(width = 18.dp.toPx(), cap = StrokeCap.Round)

                drawCircle(
                    color = SoftSage.copy(alpha = 0.4f),
                    style = stroke
                )

                stats.forEach { stat ->
                    val sweep = (stat.totalMinutes.toFloat() / totalMins.toFloat()) * 360f
                    val statColor = try {
                        Color(android.graphics.Color.parseColor(stat.color))
                    } catch (e: Exception) {
                        PrimaryForest
                    }

                    if (sweep > 0f) {
                        drawArc(
                            color = statColor,
                            startAngle = startAngle,
                            sweepAngle = (sweep - 4f).coerceAtLeast(2f),
                            useCenter = false,
                            style = stroke
                        )
                    }
                    startAngle += sweep
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${totalMins / 60}h ${totalMins % 60}m",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Total Logged",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun FocusTrendChart(
    scores: List<Int>,
    modifier: Modifier = Modifier
) {
    if (scores.isEmpty()) return

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(90.dp)
            .padding(vertical = 6.dp)
    ) {
        val width = size.width
        val height = size.height
        val maxScore = 100f
        val stepX = width / (scores.size - 1).coerceAtLeast(1)

        val path = Path()
        scores.forEachIndexed { i, score ->
            val x = i * stepX
            val y = height - (score.toFloat() / maxScore * height)
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        drawPath(
            path = path,
            color = PrimaryForest,
            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Composable
fun AcademicCalendarHeatmap(
    days: List<Int>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val weeks = days.chunked(7)
        weeks.forEach { week ->
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                week.forEach { level ->
                    val color = when (level) {
                        3 -> PrimaryForest
                        2 -> SoftSuccess
                        1 -> SoftSage
                        else -> MutedSage
                    }
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(color, RoundedCornerShape(4.dp))
                    )
                }
            }
        }
    }
}
