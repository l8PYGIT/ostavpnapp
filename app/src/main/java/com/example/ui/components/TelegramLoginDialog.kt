package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AuthSession
import com.example.data.model.AuthSessionStatus
import com.example.data.model.ServerBotConfig
import com.example.ui.theme.CyberDarkBackground
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceBorder
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CrimsonError
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

private val ColorTelegram = Color(0xFF29B6F6)

@Composable
fun TelegramLoginDialog(
    authSession: AuthSession,
    botConfig: ServerBotConfig,
    isLoading: Boolean,
    onStartDeepLinkLogin: () -> Unit,
    onCreateSessionOnly: () -> Unit,
    onLoginWithCode: (String) -> Unit,
    onLoginWithCredentials: (String, String) -> Unit,
    onUpdateBotConfig: (ServerBotConfig) -> Unit,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }

    // Manual code
    var loginCode by remember { mutableStateOf("") }
    var telegramId by remember { mutableStateOf("") }
    var userPassword by remember { mutableStateOf("") }

    // Server Config
    var serverApiUrl by remember(botConfig) { mutableStateOf(botConfig.serverApiUrl) }
    var botUsername by remember(botConfig) { mutableStateOf(botConfig.botUsername) }

    // Live countdown timer ticker
    var currentTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(authSession.sessionId) {
        while (true) {
            delay(1000)
            currentTime = System.currentTimeMillis()
        }
    }

    val remainingSeconds = remember(authSession, currentTime) {
        if (authSession.sessionId.isNotBlank()) {
            ((authSession.createdAt + (authSession.expiresInSeconds * 1000L) - currentTime) / 1000L)
                .toInt()
                .coerceAtLeast(0)
        } else 0
    }

    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(CyberDarkBackground)
                .border(1.dp, CyberSurfaceBorder, RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(ColorTelegram.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "تلگرام",
                            tint = ColorTelegram,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "احراز هویت سشن OstaApp",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "سرور API + ربات احراز هویت",
                            color = NeonCyan,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = CyberSurface,
                contentColor = NeonCyan,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = ColorTelegram
                    )
                },
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                listOf("سشن و ربات تلگرام", "کد دستی", "تنظیم سرور API").forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 11.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                0 -> {
                    // Session Login with Bot as IdP
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(CyberSurface)
                            .border(1.dp, ColorTelegram.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "معماری نشست مستقیم (API Session):",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = NeonCyan,
                                    strokeWidth = 2.dp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "اپلیکیشن مستقیماً با API سرور متصل شده و ربات فقط نقش تاییدکننده هویت (IdP) را دارد.",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Active Session Card
                        if (authSession.sessionId.isNotBlank()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CyberSurfaceVariant)
                                    .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        when (authSession.status) {
                                                            AuthSessionStatus.VERIFIED -> EmeraldSuccess
                                                            AuthSessionStatus.PENDING -> AmberWarning
                                                            AuthSessionStatus.EXPIRED, AuthSessionStatus.FAILED -> CrimsonError
                                                            else -> NeonCyan
                                                        }
                                                    )
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = authSession.status.label,
                                                color = when (authSession.status) {
                                                    AuthSessionStatus.VERIFIED -> EmeraldSuccess
                                                    AuthSessionStatus.PENDING -> AmberWarning
                                                    AuthSessionStatus.EXPIRED, AuthSessionStatus.FAILED -> CrimsonError
                                                    else -> NeonCyan
                                                },
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }

                                        // Countdown Timer
                                        if (authSession.status == AuthSessionStatus.PENDING && remainingSeconds > 0) {
                                            val minutes = remainingSeconds / 60
                                            val seconds = remainingSeconds % 60
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Timer,
                                                    contentDescription = null,
                                                    tint = AmberWarning,
                                                    modifier = Modifier.size(13.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = String.format("%02d:%02d", minutes, seconds),
                                                    color = AmberWarning,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Verification Code Display
                                    if (authSession.verificationCode.isNotBlank()) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = "کد احراز هویت ۶ رقمی سشن:",
                                                    color = TextMuted,
                                                    fontSize = 10.sp
                                                )
                                                Text(
                                                    text = authSession.verificationCode,
                                                    color = NeonCyan,
                                                    fontSize = 20.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    fontFamily = FontFamily.Monospace,
                                                    letterSpacing = 3.sp
                                                )
                                            }

                                            IconButton(
                                                onClick = {
                                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                    clipboard.setPrimaryClip(ClipData.newPlainText("Auth Code", authSession.verificationCode))
                                                    Toast.makeText(context, "کد سشن کپی شد: ${authSession.verificationCode}", Toast.LENGTH_SHORT).show()
                                                },
                                                modifier = Modifier.size(36.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.ContentCopy,
                                                    contentDescription = "کپی کد",
                                                    tint = TextSecondary,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // Steps
                        StepItem(number = "۱", text = "سشن لاگین در سرور API ایجاد می‌شود.")
                        Spacer(modifier = Modifier.height(5.dp))
                        StepItem(number = "۲", text = "با کلیک دکمه زیر، ربات تلگرام هویت شما را تایید می‌کند.")
                        Spacer(modifier = Modifier.height(5.dp))
                        StepItem(number = "۳", text = "برنامه بلافاصله تمام اشتراک‌های شما را از API دریافت می‌کند.")

                        Spacer(modifier = Modifier.height(14.dp))

                        // Primary Action Button
                        Button(
                            onClick = {
                                onStartDeepLinkLogin()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ColorTelegram,
                                contentColor = CyberDarkBackground
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("fast_telegram_deeplink_button")
                        ) {
                            Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (authSession.sessionId.isNotBlank()) "هدایت به ربات تلگرام و تایید نشست" else "ایجاد سشن و هدایت به تلگرام",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (authSession.sessionId.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = { onCreateSessionOnly() },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonCyan),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("تولید مجدد سشن جدید", fontSize = 12.sp)
                            }
                        }
                    }
                }

                1 -> {
                    // Manual Code Entry
                    Text(
                        text = "اگر کد ۶ رقمی را از ربات تلگرام یا پنل وب دریافت کرده‌اید:",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = loginCode,
                        onValueChange = { loginCode = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("کد احراز هویت ۶ رقمی", fontSize = 12.sp) },
                        placeholder = { Text("مثلاً 849201", color = TextMuted, fontSize = 12.sp) },
                        colors = fieldColors(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Default.Key, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            if (loginCode.isNotBlank()) {
                                onLoginWithCode(loginCode)
                            } else {
                                Toast.makeText(context, "لطفاً کد ورود را وارد کنید", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = CyberDarkBackground),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                    ) {
                        Text("تایید کد در API و بارگذاری اشتراک‌ها", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "یا ورود مستقیم با شناسه عددی تلگرام:",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = telegramId,
                        onValueChange = { telegramId = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("شناسه عددی تلگرام (Telegram ID)", fontSize = 12.sp) },
                        placeholder = { Text("مثلاً 98273612", color = TextMuted, fontSize = 12.sp) },
                        colors = fieldColors(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            if (telegramId.isNotBlank()) {
                                onLoginWithCredentials(telegramId, userPassword)
                            } else {
                                Toast.makeText(context, "شناسه تلگرام را وارد کنید", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ColorTelegram),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("استعلام اشتراک‌های این شناسه از API", fontSize = 12.sp)
                    }
                }

                2 -> {
                    // Server API Settings
                    Text(
                        text = "تنظیم آدرس سرور API مرکزی و آیدی ربات تاییدکننده هویت:",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = serverApiUrl,
                        onValueChange = { serverApiUrl = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("آدرس سرور API من", fontSize = 12.sp) },
                        placeholder = { Text("https://api.myvpnbot.com", color = TextMuted, fontSize = 12.sp) },
                        colors = fieldColors(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = botUsername,
                        onValueChange = { botUsername = it.removePrefix("@") },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("آیدی ربات تاییدکننده تلگرام (بدون @)", fontSize = 12.sp) },
                        placeholder = { Text("MyVpnBot", color = TextMuted, fontSize = 12.sp) },
                        colors = fieldColors(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            onUpdateBotConfig(
                                botConfig.copy(
                                    serverApiUrl = serverApiUrl.trim(),
                                    botUsername = botUsername.trim()
                                )
                            )
                            Toast.makeText(context, "مشخصات سرور ذخیره شد", Toast.LENGTH_SHORT).show()
                            onDismissRequest()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ColorTelegram, contentColor = CyberDarkBackground),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("ذخیره مشخصات سرور API", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Dismiss Button
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                OutlinedButton(
                    onClick = onDismissRequest,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("بستن")
                }
            }
        }
    }
}

@Composable
private fun StepItem(number: String, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(ColorTelegram.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = number, color = ColorTelegram, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, color = TextSecondary, fontSize = 11.sp)
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = CyberSurface,
    unfocusedContainerColor = CyberSurface,
    focusedBorderColor = ColorTelegram,
    unfocusedBorderColor = CyberSurfaceBorder,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    focusedLabelColor = ColorTelegram,
    unfocusedLabelColor = TextSecondary
)
