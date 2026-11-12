package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PanelConfig
import com.example.data.model.ServerBotConfig
import com.example.data.model.ServerNode
import com.example.data.model.TelegramUser
import com.example.data.model.UserService
import com.example.data.model.VpnStats
import com.example.data.model.VpnStatus
import com.example.ui.components.ConnectPowerButton
import com.example.ui.components.LiveSpeedMeter
import com.example.ui.components.PanelQuotaCard
import com.example.ui.components.TrafficChart
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.ColorTelegram
import com.example.ui.theme.CyberDarkBackground
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceBorder
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    status: VpnStatus,
    stats: VpnStats,
    trafficHistory: List<Long>,
    selectedServer: ServerNode?,
    activePanel: PanelConfig?,
    activeService: UserService?,
    telegramUser: TelegramUser,
    botConfig: ServerBotConfig,
    isDarkMode: Boolean,
    isRefreshingPanel: Boolean,
    activeCleanEndpoint: String? = null,
    onToggleVpn: () -> Unit,
    onOpenServerPicker: () -> Unit,
    onOpenTelegramLogin: () -> Unit,
    onRefreshPanel: () -> Unit,
    onNavigateToServices: () -> Unit,
    onToggleTheme: () -> Unit,
    onConnectFastestServer: () -> Unit,
    onOpenCfScanner: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDarkBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top App Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(NeonCyan.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Panel VPN",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "ربات: @${botConfig.botUsername}",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            // Actions: Theme toggle & Telegram button
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Theme Toggle Icon Button
                IconButton(
                    onClick = onToggleTheme,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(CyberSurface)
                        .border(1.dp, CyberSurfaceBorder, CircleShape)
                        .testTag("theme_toggle_button")
                ) {
                    Icon(
                        imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = if (isDarkMode) "تغییر به تم روشن" else "تغییر به تم تاریک",
                        tint = if (isDarkMode) AmberWarning else NeonCyan,
                        modifier = Modifier.size(19.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Telegram Login / Profile button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (telegramUser.isLoggedIn) ColorTelegram.copy(alpha = 0.15f) else NeonCyan.copy(alpha = 0.15f))
                        .border(
                            1.dp,
                            if (telegramUser.isLoggedIn) ColorTelegram else NeonCyan,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable(onClick = onOpenTelegramLogin)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("telegram_login_top_button")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "ورود با تلگرام",
                            tint = if (telegramUser.isLoggedIn) ColorTelegram else NeonCyan,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (telegramUser.isLoggedIn) "@${telegramUser.username}" else "ورود",
                            color = if (telegramUser.isLoggedIn) ColorTelegram else NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Telegram Bot Service Banner
        if (telegramUser.isLoggedIn) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CyberSurface)
                    .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(16.dp))
                    .clickable(onClick = onNavigateToServices)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(ColorTelegram.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            tint = ColorTelegram,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = activeService?.serviceName ?: "سرویس فعال من در ربات",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "شناسه تلگرام: ${telegramUser.telegramId} • موجودی: ${telegramUser.walletBalanceToman} تومان",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "تغییر سرویس",
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        } else {
            // Not Logged in banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(ColorTelegram.copy(alpha = 0.1f))
                    .border(1.dp, ColorTelegram.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .clickable(onClick = onOpenTelegramLogin)
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "کانفیگ‌ها را از ربات تلگرام دریافت کنید",
                        color = ColorTelegram,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "با کد دریافتی از @${botConfig.botUsername} وارد شوید تا سرویس‌های شما متصل شود.",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onOpenTelegramLogin,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColorTelegram,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("ورود", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Center Power Connect Button
        ConnectPowerButton(
            status = status,
            onToggleClick = onToggleVpn
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Fastest Server Auto-Connect Banner Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(NeonCyan.copy(alpha = 0.12f))
                .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                .clickable(onClick = onConnectFastestServer)
                .padding(horizontal = 14.dp, vertical = 12.dp)
                .testTag("connect_fastest_server_button"),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(NeonCyan.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "اتصال هوشمند به سریع‌ترین سرور",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "انتخاب خودکار کمترین پینگ و تاخیر بین تمام نودها",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(NeonCyan)
                    .padding(horizontal = 12.dp, vertical = 7.dp)
            ) {
                Text(
                    text = "اتصال بهینه",
                    color = if (isDarkMode) Color(0xFF090D14) else Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Cloudflare Clean IP Quick Banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(if (activeCleanEndpoint != null) EmeraldSuccess.copy(alpha = 0.12f) else CyberSurface)
                .border(
                    1.dp,
                    if (activeCleanEndpoint != null) EmeraldSuccess.copy(alpha = 0.4f) else CyberSurfaceBorder,
                    RoundedCornerShape(16.dp)
                )
                .clickable(onClick = onOpenCfScanner)
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .testTag("cf_scanner_home_banner"),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (activeCleanEndpoint != null) EmeraldSuccess.copy(alpha = 0.2f) else NeonCyan.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudQueue,
                        contentDescription = null,
                        tint = if (activeCleanEndpoint != null) EmeraldSuccess else NeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "اسکنر اندپوینت کلودفلر (Clean IP)",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (activeCleanEndpoint != null) "اندپوینت فعال: $activeCleanEndpoint (سریع و ضد اختلال)" else "دور زدن اختلال همراه اول و ایرانسل برای CDN",
                        color = if (activeCleanEndpoint != null) EmeraldSuccess else TextSecondary,
                        fontSize = 10.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (activeCleanEndpoint != null) EmeraldSuccess else NeonCyan)
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = if (activeCleanEndpoint != null) "تنظیم شده" else "اسکن",
                    color = if (isDarkMode) Color(0xFF090D14) else Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Selected Server Card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(CyberSurface)
                .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(18.dp))
                .clickable(onClick = onOpenServerPicker)
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedServer?.flagEmoji ?: "🌐",
                    fontSize = 26.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = selectedServer?.name ?: "انتخاب سرور",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "پروتکل: ${selectedServer?.protocol?.label ?: "VLESS"}",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                        if (selectedServer?.cleanEndpoint != null) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(EmeraldSuccess.copy(alpha = 0.2f))
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Clean IP",
                                    color = EmeraldSuccess,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else if (selectedServer?.isCdn == true) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(NeonCyan.copy(alpha = 0.15f))
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "CDN",
                                    color = NeonCyan,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (selectedServer != null && selectedServer.pingMs > 0) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(EmeraldSuccess.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${selectedServer.pingMs} ms",
                            color = EmeraldSuccess,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "تغییر سرور",
                    tint = TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Live Speed Meter
        LiveSpeedMeter(
            stats = stats,
            status = status,
            activePingMs = selectedServer?.pingMs ?: -1
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Active Service / Quota Card
        PanelQuotaCard(
            panel = activePanel,
            isRefreshing = isRefreshingPanel,
            onRefreshClick = onRefreshPanel,
            onManageClick = onNavigateToServices
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Realtime Network Traffic Waveform
        if (status == VpnStatus.CONNECTED) {
            TrafficChart(trafficHistory = trafficHistory)
            Spacer(modifier = Modifier.height(14.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
