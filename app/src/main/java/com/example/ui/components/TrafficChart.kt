package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary

@Composable
fun TrafficChart(
    trafficHistory: List<Long>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CyberSurface)
            .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(NeonCyan)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "نمودار زنده ترافیک شبکه",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "۲۵ ثانیه گذشته",
                color = TextMuted,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        val chartColor = NeonCyan

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
        ) {
            if (trafficHistory.isEmpty()) return@Canvas

            val maxSpeed = (trafficHistory.maxOrNull() ?: 1L).coerceAtLeast(100_000L).toFloat()
            val stepX = size.width / (trafficHistory.size - 1).coerceAtLeast(1)

            val path = Path()
            val fillPath = Path()

            fillPath.moveTo(0f, size.height)

            trafficHistory.forEachIndexed { index, value ->
                val x = index * stepX
                val normalizedY = 1f - (value.toFloat() / maxSpeed).coerceIn(0f, 1f)
                val y = normalizedY * (size.height - 10.dp.toPx()) + 5.dp.toPx()

                if (index == 0) {
                    path.moveTo(x, y)
                    fillPath.lineTo(x, y)
                } else {
                    val prevX = (index - 1) * stepX
                    val prevNormY = 1f - (trafficHistory[index - 1].toFloat() / maxSpeed).coerceIn(0f, 1f)
                    val prevY = prevNormY * (size.height - 10.dp.toPx()) + 5.dp.toPx()

                    val controlX = (prevX + x) / 2
                    path.cubicTo(controlX, prevY, controlX, y, x, y)
                    fillPath.cubicTo(controlX, prevY, controlX, y, x, y)
                }
            }

            fillPath.lineTo(size.width, size.height)
            fillPath.close()

            // Draw filled gradient area under curve
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    listOf(
                        chartColor.copy(alpha = 0.30f),
                        chartColor.copy(alpha = 0.0f)
                    )
                )
            )

            // Draw glowing stroke line
            drawPath(
                path = path,
                brush = Brush.horizontalGradient(
                    listOf(chartColor.copy(alpha = 0.6f), chartColor)
                ),
                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}
