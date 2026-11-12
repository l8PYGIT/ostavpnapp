package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VpnStatus
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@Composable
fun ConnectPowerButton(
    status: VpnStatus,
    onToggleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "power_pulse")

    // Pulse animation for connected & connecting state
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (status == VpnStatus.CONNECTED || status == VpnStatus.CONNECTING) 1.14f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = if (status == VpnStatus.CONNECTED || status == VpnStatus.CONNECTING) 0.15f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    // Rotation animation for connecting radar
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val (glowColor, buttonBgGradient, statusText) = when (status) {
        VpnStatus.CONNECTED -> Triple(
            EmeraldSuccess,
            Brush.radialGradient(listOf(EmeraldSuccess.copy(alpha = 0.35f), CyberSurface)),
            "متصل به پنل"
        )
        VpnStatus.CONNECTING -> Triple(
            AmberWarning,
            Brush.radialGradient(listOf(AmberWarning.copy(alpha = 0.35f), CyberSurface)),
            "در حال برقراری ارتباط..."
        )
        VpnStatus.DISCONNECTING -> Triple(
            AmberWarning,
            Brush.radialGradient(listOf(AmberWarning.copy(alpha = 0.2f), CyberSurface)),
            "در حال قطع اتصال..."
        )
        VpnStatus.ERROR -> Triple(
            Color(0xFFFF3D71),
            Brush.radialGradient(listOf(Color(0xFFFF3D71).copy(alpha = 0.25f), CyberSurface)),
            "خطا در اتصال"
        )
        VpnStatus.DISCONNECTED -> Triple(
            NeonCyan,
            Brush.radialGradient(listOf(CyberSurfaceVariant, CyberSurface)),
            "برای اتصال ضربه بزنید"
        )
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            // Outer glowing pulse ring
            if (status == VpnStatus.CONNECTED || status == VpnStatus.CONNECTING) {
                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(glowColor.copy(alpha = pulseAlpha))
                )
            }

            val emeraldColor = EmeraldSuccess
            val surfaceVariantColor = CyberSurfaceVariant

            // Radar arc / Orbiting dash track
            Canvas(
                modifier = Modifier
                    .size(180.dp)
                    .graphicsLayer {
                        rotationZ = if (status == VpnStatus.CONNECTING) rotationAngle else 0f
                    }
            ) {
                if (status == VpnStatus.CONNECTING) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(Color.Transparent, glowColor)
                        ),
                        startAngle = 0f,
                        sweepAngle = 270f,
                        useCenter = false,
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )
                } else if (status == VpnStatus.CONNECTED) {
                    drawCircle(
                        color = emeraldColor.copy(alpha = 0.8f),
                        style = Stroke(width = 3.dp.toPx())
                    )
                } else {
                    drawCircle(
                        color = surfaceVariantColor,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }

            // Main Core Button
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .shadow(16.dp, CircleShape, spotColor = glowColor)
                    .clip(CircleShape)
                    .background(buttonBgGradient)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(bounded = true, color = glowColor),
                        onClick = onToggleClick
                    )
                    .testTag("vpn_power_button"),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = if (status == VpnStatus.CONNECTED) Icons.Default.Shield else Icons.Default.PowerSettingsNew,
                        contentDescription = "دکمه اتصال VPN",
                        tint = glowColor,
                        modifier = Modifier.size(54.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = statusText,
            color = if (status == VpnStatus.DISCONNECTED) TextMuted else TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
