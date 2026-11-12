package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.AltRoute
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppSettings
import com.example.data.model.DnsProvider
import com.example.data.model.ServerBotConfig
import com.example.ui.theme.CyberDarkBackground
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceBorder
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

private val ColorTelegram = Color(0xFF29B6F6)

@Composable
fun SettingsScreen(
    settings: AppSettings,
    botConfig: ServerBotConfig,
    onSettingsChanged: (AppSettings) -> Unit,
    onBotConfigChanged: (ServerBotConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var dnsDropdownExpanded by remember { mutableStateOf(false) }
    var mtuValue by remember(settings.mtu) { mutableFloatStateOf(settings.mtu.toFloat()) }

    var serverUrlInput by remember(botConfig) { mutableStateOf(botConfig.serverApiUrl) }
    var botUsernameInput by remember(botConfig) { mutableStateOf(botConfig.botUsername) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDarkBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(
            text = "تنظیمات پیشرفته",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "پیکربندی سرور ربات تلگرام، شبکه و تونل‌بندی",
            color = TextSecondary,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Section: Appearance & Theme
        SettingsSectionHeader(title = "قالب و ظاهر برنامه (Theme)", icon = Icons.Default.Palette)

        SettingsCard {
            SettingsToggleRow(
                title = "تم تاریک (Dark Mode)",
                subtitle = if (settings.isDarkMode) "تم مشکی سیبرنتیک فعال است" else "تم روشن (Light Mode) با کنتراست بالا فعال است",
                checked = settings.isDarkMode,
                onCheckedChange = { onSettingsChanged(settings.copy(isDarkMode = it)) }
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Section: Smart Server Connection
        SettingsSectionHeader(title = "اتصال هوشمند به سریع‌ترین سرور", icon = Icons.Default.Bolt)

        SettingsCard {
            SettingsToggleRow(
                title = "انتخاب خودکار سریع‌ترین سرور",
                subtitle = "هنگام شروع، سروری با کمترین پینگ به صورت هوشمند انتخاب شود",
                checked = settings.autoSelectFastestServer,
                onCheckedChange = { onSettingsChanged(settings.copy(autoSelectFastestServer = it)) }
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Section: Telegram Bot & Server Backend Configuration
        SettingsSectionHeader(title = "اتصال به سرور و ربات تلگرام من", icon = Icons.Default.Send, tint = ColorTelegram)

        SettingsCard {
            Text(
                text = "برای اینکه کاربران به سرور اختصاصی شما متصل شوند، آدرس API سرور و آیدی ربات خود را تنظیم کنید:",
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = serverUrlInput,
                onValueChange = { serverUrlInput = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("آدرس سرور API من", fontSize = 12.sp) },
                placeholder = { Text("https://api.myvpnbot.com", color = TextMuted, fontSize = 12.sp) },
                colors = fieldColors(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = botUsernameInput,
                onValueChange = { botUsernameInput = it.removePrefix("@") },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("آیدی ربات تلگرام (بدون @)", fontSize = 12.sp) },
                placeholder = { Text("MyVpnBot", color = TextMuted, fontSize = 12.sp) },
                colors = fieldColors(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(
                    onClick = {
                        onBotConfigChanged(
                            botConfig.copy(
                                serverApiUrl = serverUrlInput.trim(),
                                botUsername = botUsernameInput.trim()
                            )
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColorTelegram,
                        contentColor = CyberDarkBackground
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("ذخیره مشخصات سرور و ربات", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Section: Routing & Split Tunneling
        SettingsSectionHeader(title = "تونل‌بندی انتخابی (Split Tunneling)", icon = Icons.AutoMirrored.Filled.AltRoute)

        SettingsCard {
            SettingsToggleRow(
                title = "بایپس سایت‌های داخلی ایران",
                subtitle = "سایت‌های ایرانی، درگاه‌های پرداخت شاپرک و بانک‌ها از فیلترشکن رد نشوند",
                checked = settings.bypassIran,
                onCheckedChange = { onSettingsChanged(settings.copy(bypassIran = it)) }
            )

            Spacer(modifier = Modifier.height(12.dp))
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(CyberSurfaceBorder))
            Spacer(modifier = Modifier.height(12.dp))

            SettingsToggleRow(
                title = "بایپس شبکه محلی (LAN)",
                subtitle = "امکان اتصال به پرینتر و دستگاه‌های وای‌فای خانگی در حین اتصال",
                checked = settings.bypassLan,
                onCheckedChange = { onSettingsChanged(settings.copy(bypassLan = it)) }
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Section: DNS Settings
        SettingsSectionHeader(title = "تنظیمات سامانه نام دامنه (DNS)", icon = Icons.Default.Dns)

        SettingsCard {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { dnsDropdownExpanded = true }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "سرور DNS امن",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = settings.dns.label,
                        color = NeonCyan,
                        fontSize = 12.sp
                    )
                }

                Box {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberSurfaceVariant)
                            .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "تغییر DNS",
                            color = TextPrimary,
                            fontSize = 11.sp
                        )
                    }

                    DropdownMenu(
                        expanded = dnsDropdownExpanded,
                        onDismissRequest = { dnsDropdownExpanded = false },
                        modifier = Modifier.background(CyberSurface)
                    ) {
                        DnsProvider.values().forEach { provider ->
                            DropdownMenuItem(
                                text = { Text(provider.label, color = TextPrimary, fontSize = 12.sp) },
                                onClick = {
                                    onSettingsChanged(settings.copy(dns = provider))
                                    dnsDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Section: Security & Tunnel
        SettingsSectionHeader(title = "امنیت و پروتکل", icon = Icons.Default.Security)

        SettingsCard {
            SettingsToggleRow(
                title = "کلید قطع اضطراری (Kill Switch)",
                subtitle = "در صورت قطع شدن اتصال VPN، کلیه ترافیک اینترنت مسدود شود تا IP لو نرود",
                checked = settings.killSwitch,
                onCheckedChange = { onSettingsChanged(settings.copy(killSwitch = it)) }
            )

            Spacer(modifier = Modifier.height(12.dp))
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(CyberSurfaceBorder))
            Spacer(modifier = Modifier.height(12.dp))

            SettingsToggleRow(
                title = "اتصال مجدد خودکار",
                subtitle = "در صورت تغییر وضعیت اینترنت همراه به وای‌فای، تونل بلافاصله بازسازی شود",
                checked = settings.autoReconnect,
                onCheckedChange = { onSettingsChanged(settings.copy(autoReconnect = it)) }
            )

            Spacer(modifier = Modifier.height(14.dp))
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(CyberSurfaceBorder))
            Spacer(modifier = Modifier.height(14.dp))

            // MTU Slider
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "اندازه واحد بسته (MTU)",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${mtuValue.toInt()}",
                        color = NeonCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "برای اینترنت‌های ناپایدار همراه، MTU روی ۱۴۰۰ تا ۱۴۵۰ توصیه می‌شود",
                    color = TextMuted,
                    fontSize = 11.sp
                )

                Slider(
                    value = mtuValue,
                    onValueChange = { mtuValue = it },
                    onValueChangeFinished = { onSettingsChanged(settings.copy(mtu = mtuValue.toInt())) },
                    valueRange = 1280f..1500f,
                    steps = 21,
                    colors = SliderDefaults.colors(
                        thumbColor = NeonCyan,
                        activeTrackColor = NeonCyan,
                        inactiveTrackColor = CyberDarkBackground
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // App info
        SettingsSectionHeader(title = "درباره برنامه", icon = Icons.Default.Info)

        SettingsCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "نسخه کلاینت", color = TextSecondary, fontSize = 12.sp)
                Text(text = "1.0.0 (Telegram Bot Connected)", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "پروتکل‌های پشتیبانی شده", color = TextSecondary, fontSize = 12.sp)
                Text(text = "VLESS Reality, VMESS, Trojan, SS", color = EmeraldSuccess, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SettingsSectionHeader(title: String, icon: ImageVector, tint: Color = NeonCyan) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(CyberSurface)
            .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(18.dp))
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
private fun SettingsToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 16.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = CyberDarkBackground,
                checkedTrackColor = NeonCyan,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = CyberDarkBackground,
                uncheckedBorderColor = CyberSurfaceBorder
            )
        )
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = CyberDarkBackground,
    unfocusedContainerColor = CyberDarkBackground,
    focusedBorderColor = ColorTelegram,
    unfocusedBorderColor = CyberSurfaceBorder,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    focusedLabelColor = ColorTelegram,
    unfocusedLabelColor = TextSecondary
)
