package com.example.ui.components

import android.content.Context
import android.widget.Toast
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
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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
fun TelegramLoginDialog(
    botConfig: ServerBotConfig,
    isLoading: Boolean,
    onStartDeepLinkLogin: () -> Unit,
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
                            text = "ورود مستقیم با تلگرام",
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "ربات: @${botConfig.botUsername}",
                            color = ColorTelegram,
                            fontSize = 12.sp
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
                listOf("ورود خودکار با ۱ لمس", "کد دستی", "تنظیم سرور").forEachIndexed { index, title ->
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
                    // Fast 1-Tap Deep Link Login
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(CyberSurface)
                            .border(1.dp, ColorTelegram.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "روال ورود آسان و بدون نیاز به تایپ رمز:",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        StepItem(number = "۱", text = "روی دکمه «هدایت به تلگرام» در پایین بزنید.")
                        Spacer(modifier = Modifier.height(6.dp))
                        StepItem(number = "۲", text = "در ربات تلگرام دکمه Start و سپس «تایید ورود» را لمس کنید.")
                        Spacer(modifier = Modifier.height(6.dp))
                        StepItem(number = "۳", text = "برنامه به صورت خودکار باز شده و اشتراک فعال می‌شود!")

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                onStartDeepLinkLogin()
                                onDismissRequest()
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
                                text = "هدایت به ربات تلگرام و تایید ورود",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                1 -> {
                    // Manual Code or ID
                    Text(
                        text = "اگر مایلید به صورت دستی با کد ۶ رقمی یا شناسه عددی تلگرام وارد شوید:",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = loginCode,
                        onValueChange = { loginCode = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("کد ورود ۶ رقمی دریافتی از ربات", fontSize = 12.sp) },
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
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("تایید کد و دریافت کانفیگ‌ها", fontWeight = FontWeight.Bold)
                    }
                }

                2 -> {
                    // Server API Settings
                    Text(
                        text = "آدرس سرور API من و آیدی ربات تلگرام:",
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
                        label = { Text("آیدی ربات تلگرام (بدون @)", fontSize = 12.sp) },
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
                        Text("ذخیره مشخصات سرور", fontWeight = FontWeight.Bold)
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
