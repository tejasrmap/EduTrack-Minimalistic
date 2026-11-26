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
import com.gtinnox.edutrack.ui.viewmodel.DayTrend
import com.gtinnox.edutrack.ui.viewmodel.SubjectStudyStat

@Composable
fun MinimalStudyBarChart(
    trends: List<DayTrend>,
    targetMinutes: Int = 180,
    modifier: Modifier = Modifier
) {
    val barColor = AcademicIndigo
    val targetLineColor = AcademicSage.copy(alpha = 0.5f)
    val maxMins = (trends.maxOfOrNull { it.studyMinutes } ?: 180).coerceAtLeast(targetMinutes).coerceAtLeast(1)

    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .padding(vertical = 8.dp)
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val barCount = trends.size
            if (barCount == 0) return@Canvas

            val barWidth = (canvasWidth / (barCount * 2)).coerceIn(12f, 32f)
            val spacing = (canvasWidth - (barWidth * barCount)) / (barCount + 1)

            // Target line
            val targetY = canvasHeight - ((targetMinutes.toFloat() / maxMins.toFloat()) * canvasHeight)
            drawLine(
                color = targetLineColor,
                start = Offset(0f, targetY),
                end = Offset(canvasWidth, targetY),
                strokeWidth = 2f,
                cap = StrokeCap.Round
            )

            trends.forEachIndexed { index, day ->
                val x = spacing + index * (barWidth + spacing)
                val heightFactor = (day.studyMinutes.toFloat() / maxMins.toFloat()).coerceIn(0.05f, 1f)
                val barHeight = canvasHeight * heightFactor
                val y = canvasHeight - barHeight

                drawRoundRect(
                    color = if (day.studyMinutes >= targetMinutes) AcademicSage else barColor,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
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
                    style = MaterialTheme.typography.labelSmall,
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
        Text("No study data logged yet", style = MaterialTheme.typography.bodyMedium)
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
            Canvas(modifier = Modifier.size(150.dp)) {
                var startAngle = -90f
                val stroke = Stroke(width = 24f, cap = StrokeCap.Round)

                stats.forEach { stat ->
                    val sweep = (stat.totalMinutes.toFloat() / totalMins.toFloat()) * 360f
                    val statColor = try {
                        Color(android.graphics.Color.parseColor(stat.color))
                    } catch (e: Exception) {
                        AcademicIndigo
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
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Total Study",
                    style = MaterialTheme.typography.labelSmall,
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
                    AcademicIndigo
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
                        style = MaterialTheme.typography.labelSmall,
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
            .height(90.dp)
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
            color = AcademicIndigo,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )

        scores.forEachIndexed { i, score ->
            val x = i * stepX
            val y = height - (score.toFloat() / maxScore * height)
            drawCircle(
                color = AcademicIndigo,
                radius = 4.dp.toPx(),
                center = Offset(x, y)
            )
            drawCircle(
                color = Color.White,
                radius = 2.dp.toPx(),
                center = Offset(x, y)
            )
        }
    }
}
