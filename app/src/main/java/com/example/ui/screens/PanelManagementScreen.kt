package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PanelConfig
import com.example.data.model.ServerBotConfig
import com.example.data.model.ServiceStatus
import com.example.data.model.TelegramUser
import com.example.data.model.UserService
import com.example.ui.theme.CyberDarkBackground
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceBorder
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val ColorTelegram = Color(0xFF29B6F6)

@Composable
fun PanelManagementScreen(
    userServices: List<UserService>,
    activeService: UserService?,
    telegramUser: TelegramUser,
    botConfig: ServerBotConfig,
    isRefreshing: Boolean,
    onSelectService: (UserService) -> Unit,
    onSyncWithBotServer: () -> Unit,
    onOpenTelegramLogin: () -> Unit,
    onLogoutTelegram: () -> Unit,
    onOpenAddManualConfig: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberDarkBackground)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "سرویس‌ها و ربات تلگرام",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "دریافت خودکار کانفیگ‌ها از ربات تلگرام شما",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Sync button
                IconButton(
                    onClick = onSyncWithBotServer,
                    enabled = !isRefreshing,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(CyberSurface)
                        .border(1.dp, CyberSurfaceBorder, CircleShape)
                        .testTag("sync_with_bot_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "همگام‌سازی با ربات",
                        tint = NeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Telegram Login / Switch
                IconButton(
                    onClick = onOpenTelegramLogin,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(ColorTelegram)
                        .testTag("open_telegram_auth_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "ورود تلگرام",
                        tint = CyberDarkBackground,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // User Telegram Profile Card
            item {
                TelegramProfileCard(
                    user = telegramUser,
                    botConfig = botConfig,
                    onOpenLogin = onOpenTelegramLogin,
                    onOpenBot = { openTelegramBot(context, botConfig.botUsername) },
                    onLogout = onLogoutTelegram
                )
            }

            // Section: My Services from Bot
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "سرویس‌های خریداری شده شما (${userServices.size})",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "لینک ساب از سرور",
                        color = NeonCyan,
                        fontSize = 11.sp
                    )
                }
            }

            items(userServices, key = { it.id }) { service ->
                val isSelected = service.id == activeService?.id
                UserServiceCard(
                    service = service,
                    isSelected = isSelected,
                    onSelect = { onSelectService(service) },
                    onCopySubLink = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Sub Link", service.subscriptionUrl))
                        Toast.makeText(context, "لینک سابسکریپشن کپی شد", Toast.LENGTH_SHORT).show()
                    },
                    onRenewInBot = {
                        openTelegramBot(context, botConfig.botUsername)
                    }
                )
            }

            // Quick Bot actions
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Open Bot
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(ColorTelegram.copy(alpha = 0.15f))
                            .border(1.dp, ColorTelegram.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                            .clickable { openTelegramBot(context, botConfig.botUsername) }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = ColorTelegram, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("خرید اشتراک در ربات", color = ColorTelegram, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Add Manual
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(CyberSurface)
                            .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(14.dp))
                            .clickable(onClick = onOpenAddManualConfig)
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("افزودن کانفیگ دستی", color = TextPrimary, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Guide Card for Telegram Bot & Server
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(CyberSurface)
                        .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(18.dp))
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "نحوه کارکرد با ربات و سرور اختصاصی",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "• کاربران با ارسال دستور /login یا دکمه «ورود به اپلیکیشن» در ربات تلگرام، کد ورود ۶ رقمی خود را دریافت می‌کنند.\n" +
                               "• به محض وارد کردن کد در برنامه، اپلیکیشن به آدرس سرور شما متصل شده و تمامی کانفیگ‌ها و ساب‌های کاربر را بارگذاری می‌کند.\n" +
                               "• از بخش تنظیمات سرور می‌توانید آدرس API سرور خود و یوزرنیم ربات تلگرام‌تان را سفارشی‌سازی کنید.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 17.sp
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun TelegramProfileCard(
    user: TelegramUser,
    botConfig: ServerBotConfig,
    onOpenLogin: () -> Unit,
    onOpenBot: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CyberSurface)
            .border(1.dp, if (user.isLoggedIn) ColorTelegram.copy(alpha = 0.5f) else CyberSurfaceBorder, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(ColorTelegram.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        tint = ColorTelegram,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    if (user.isLoggedIn) {
                        Text(
                            text = "${user.firstName} (@${user.username})",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "شناسه تلگرام: ${user.telegramId}",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    } else {
                        Text(
                            text = "وارد حساب تلگرام نشده‌اید",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "جهت دریافت کانفیگ‌ها لاگین کنید",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            if (user.isLoggedIn) {
                IconButton(onClick = onLogout, modifier = Modifier.size(36.dp)) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "خروج",
                        tint = com.example.ui.theme.CrimsonError,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        if (user.isLoggedIn) {
            Spacer(modifier = Modifier.height(14.dp))
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(CyberSurfaceBorder))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "موجودی کیف پول در ربات:", color = TextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${user.walletBalanceToman} تومان",
                        color = EmeraldSuccess,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "ربات: @${botConfig.botUsername}",
                    color = ColorTelegram,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable(onClick = onOpenBot)
                )
            }
        } else {
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onOpenLogin,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ColorTelegram,
                    contentColor = CyberDarkBackground
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("ورود با کد ربات تلگرام", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun UserServiceCard(
    service: UserService,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onCopySubLink: () -> Unit,
    onRenewInBot: () -> Unit
) {
    val usedGb = service.usedBytes.toFloat() / (1024 * 1024 * 1024)
    val totalGb = service.totalBytes.toFloat() / (1024 * 1024 * 1024)
    val progress = if (totalGb > 0) (usedGb / totalGb).coerceIn(0f, 1f) else 0f
    val expireFormatted = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date(service.expireDateTimestamp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) CyberSurfaceVariant else CyberSurface)
            .border(
                1.dp,
                if (isSelected) NeonCyan else CyberSurfaceBorder,
                RoundedCornerShape(20.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = service.serviceName,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "پروتکل: ${service.protocol} • ${service.serverCount} سرور فعال",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(EmeraldSuccess.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "سرویس فعال",
                        color = EmeraldSuccess,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Progress bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "حجم مصرفی",
                color = TextSecondary,
                fontSize = 12.sp
            )
            Text(
                text = String.format(Locale.US, "%.1f GB از %.1f GB", usedGb, totalGb),
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = if (progress > 0.85f) com.example.ui.theme.CrimsonError else NeonCyan,
            trackColor = CyberDarkBackground,
            strokeCap = StrokeCap.Round
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${service.remainingDays} روز باقی‌مانده تا انقضا (${expireFormatted})",
                color = com.example.ui.theme.AmberWarning,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onCopySubLink,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyberSurfaceBorder,
                    contentColor = TextPrimary
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("کپی ساب", fontSize = 11.sp)
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (!isSelected) {
                Button(
                    onClick = onSelect,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonCyan,
                        contentColor = CyberDarkBackground
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("اتصال به این سرویس", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = onRenewInBot,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColorTelegram,
                        contentColor = CyberDarkBackground
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("تمدید در تلگرام", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun openTelegramBot(context: Context, botUsername: String) {
    try {
        val tgIntent = Intent(Intent.ACTION_VIEW, Uri.parse("tg://resolve?domain=$botUsername"))
        context.startActivity(tgIntent)
    } catch (_: Exception) {
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/$botUsername"))
        context.startActivity(webIntent)
    }
}
