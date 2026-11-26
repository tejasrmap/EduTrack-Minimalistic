package com.gtinnox.edutrack.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import com.gtinnox.edutrack.ui.theme.AcademicIndigo
import com.gtinnox.edutrack.ui.theme.AcademicSage
import com.gtinnox.edutrack.ui.theme.EduTrackPrimary
import com.gtinnox.edutrack.ui.theme.EduTrackSuccess
import com.gtinnox.edutrack.ui.viewmodel.DayTrend
import com.gtinnox.edutrack.ui.viewmodel.SubjectStudyStat

@Composable
fun MinimalStudyBarChart(
    trends: List<DayTrend>,
    targetMinutes: Int = 180,
    modifier: Modifier = Modifier
) {
    val barColor = EduTrackPrimary
    val targetLineColor = EduTrackSuccess.copy(alpha = 0.5f)
    val maxMins = (trends.maxOfOrNull { it.studyMinutes } ?: 180).coerceAtLeast(targetMinutes).coerceAtLeast(1)

    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .padding(vertical = 8.dp)
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val barCount = trends.size
            if (barCount == 0) return@Canvas

            val barWidth = (canvasWidth / (barCount * 2)).coerceIn(14f, 34f)
            val spacing = (canvasWidth - (barWidth * barCount)) / (barCount + 1)

            // Target dashed/solid line
            val targetY = canvasHeight - ((targetMinutes.toFloat() / maxMins.toFloat()) * canvasHeight)
            drawLine(
                color = targetLineColor,
                start = Offset(0f, targetY),
                end = Offset(canvasWidth, targetY),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )

            trends.forEachIndexed { index, day ->
                val x = spacing + index * (barWidth + spacing)
                val heightFactor = (day.studyMinutes.toFloat() / maxMins.toFloat()).coerceIn(0.04f, 1f)
                val barHeight = canvasHeight * heightFactor
                val y = canvasHeight - barHeight

                // Background track
                drawRoundRect(
                    color = barColor.copy(alpha = 0.1f),
                    topLeft = Offset(x, 0f),
                    size = Size(barWidth, canvasHeight),
                    cornerRadius = CornerRadius(8f, 8f)
                )

                // Value bar
                drawRoundRect(
                    color = if (day.studyMinutes >= targetMinutes) EduTrackSuccess else barColor,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(8f, 8f)
                )
            }
        }

        // Days labels row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            trends.forEach {
                Text(
                    text = it.dayLabel,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
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
            modifier = Modifier.size(170.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(154.dp)) {
                var startAngle = -90f
                val stroke = Stroke(width = 24.dp.toPx(), cap = StrokeCap.Round)

                // Background subtle ring
                drawCircle(
                    color = Color.LightGray.copy(alpha = 0.2f),
                    style = stroke
                )

                stats.forEach { stat ->
                    val sweep = (stat.totalMinutes.toFloat() / totalMins.toFloat()) * 360f
                    val statColor = try {
                        Color(android.graphics.Color.parseColor(stat.color))
                    } catch (e: Exception) {
                        EduTrackPrimary
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
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Total Logged",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Legend chips
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            stats.forEach { stat ->
                val chipColor = try {
                    Color(android.graphics.Color.parseColor(stat.color))
                } catch (e: Exception) {
                    EduTrackPrimary
                }
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(chipColor, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${stat.subjectCode} (${stat.totalMinutes / 60}h)",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
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
            .height(100.dp)
            .padding(vertical = 8.dp)
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
            color = EduTrackPrimary,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        scores.forEachIndexed { i, score ->
            val x = i * stepX
            val y = height - (score.toFloat() / maxScore * height)
            drawCircle(
                color = EduTrackPrimary,
                radius = 5.dp.toPx(),
                center = Offset(x, y)
            )
            drawCircle(
                color = Color.White,
                radius = 2.5.dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}

@Composable
fun AcademicCalendarHeatmap(
    days: List<Int>, // 0 = empty, 1 = low, 2 = medium, 3 = high
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
                        3 -> EduTrackSuccess
                        2 -> EduTrackSuccess.copy(alpha = 0.65f)
                        1 -> EduTrackSuccess.copy(alpha = 0.3f)
                        else -> MaterialTheme.colorScheme.surfaceVariant
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
