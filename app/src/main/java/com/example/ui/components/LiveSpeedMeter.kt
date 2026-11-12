package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VpnStats
import com.example.data.model.VpnStatus
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceBorder
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.Locale

@Composable
fun LiveSpeedMeter(
    stats: VpnStats,
    status: VpnStatus,
    activePingMs: Int,
    modifier: Modifier = Modifier
) {
    val isConnected = status == VpnStatus.CONNECTED

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CyberSurface)
            .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        // Top: Download & Upload speeds
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SpeedItem(
                icon = Icons.Default.ArrowDownward,
                iconTint = NeonCyan,
                label = "دانلود",
                speed = if (isConnected) formatBytesPerSec(stats.downloadSpeedBytesPerSec) else "0.0 KB/s"
            )

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(44.dp)
                    .background(CyberSurfaceBorder)
            )

            SpeedItem(
                icon = Icons.Default.ArrowUpward,
                iconTint = EmeraldSuccess,
                label = "آپلود",
                speed = if (isConnected) formatBytesPerSec(stats.uploadSpeedBytesPerSec) else "0.0 KB/s"
            )
        }

        Spacer(modifier = Modifier.height(14.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(CyberSurfaceBorder.copy(alpha = 0.5f))
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Bottom row: Duration and Ping
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "مدت زمان اتصال:",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isConnected) formatDuration(stats.durationSeconds) else "00:00:00",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "تاخیر (پینگ):",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                val pingText = if (activePingMs > 0) "$activePingMs ms" else if (activePingMs == -2) "خطا" else "-- ms"
                val pingColor = when {
                    activePingMs in 1..100 -> EmeraldSuccess
                    activePingMs in 101..200 -> NeonCyan
                    activePingMs > 200 -> com.example.ui.theme.AmberWarning
                    else -> TextMuted
                }
                Text(
                    text = pingText,
                    color = pingColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SpeedItem(
    icon: ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    label: String,
    speed: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconTint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = label,
                color = TextSecondary,
                fontSize = 12.sp
            )
            Text(
                text = speed,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

fun formatBytesPerSec(bytes: Long): String {
    return when {
        bytes >= 1024 * 1024 -> String.format(Locale.US, "%.1f MB/s", bytes.toFloat() / (1024 * 1024))
        bytes >= 1024 -> String.format(Locale.US, "%.0f KB/s", bytes.toFloat() / 1024)
        else -> "$bytes B/s"
    }
}

fun formatDuration(totalSec: Long): String {
    val hours = totalSec / 3600
    val minutes = (totalSec % 3600) / 60
    val seconds = totalSec % 60
    return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
}
