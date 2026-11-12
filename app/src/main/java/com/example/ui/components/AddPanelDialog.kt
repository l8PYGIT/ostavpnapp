package com.example.ui.components

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.CyberDarkBackground
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceBorder
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AddPanelDialog(
    onDismissRequest: () -> Unit,
    onAddSubscription: (name: String, subUrl: String, panelApi: String, username: String) -> Unit,
    onImportRawConfig: (raw: String) -> Unit
) {
    val context = LocalContext.current
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // Sub tab state
    var panelName by remember { mutableStateOf("") }
    var subscriptionUrl by remember { mutableStateOf("") }
    var panelHost by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }

    // Raw tab state
    var rawConfigText by remember { mutableStateOf("") }

    val tabs = listOf("لینک سابسکریپشن", "کانفیگ مستقیم", "ورود به پنل")

    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(CyberDarkBackground)
                .padding(20.dp)
        ) {
            Text(
                text = "اتصال به پنل جدید",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "لینک سابسکریپشن یا مشخصات پنل خود را وارد کنید",
                color = TextSecondary,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = CyberSurface,
                contentColor = NeonCyan,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = NeonCyan
                    )
                },
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 11.sp,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTabIndex) {
                0 -> {
                    // Subscription Link Form
                    OutlinedTextField(
                        value = panelName,
                        onValueChange = { panelName = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("نام پنل (اختیاری)", fontSize = 12.sp) },
                        placeholder = { Text("مثلاً پنل مرزبان آلمان", color = TextMuted, fontSize = 12.sp) },
                        colors = fieldColors(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = subscriptionUrl,
                        onValueChange = { subscriptionUrl = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("لینک سابسکریپشن پنل", fontSize = 12.sp) },
                        placeholder = { Text("https://panel.example.com/sub/...", color = TextMuted, fontSize = 12.sp) },
                        colors = fieldColors(),
                        shape = RoundedCornerShape(14.dp),
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ContentPaste,
                                contentDescription = "جای‌گذاری از کلیپ‌بورد",
                                tint = NeonCyan,
                                modifier = Modifier
                                    .clickable {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val text = clipboard.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
                                        if (text.isNotBlank()) subscriptionUrl = text
                                    }
                                    .padding(8.dp)
                            )
                        }
                    )
                }

                1 -> {
                    // Raw Configs (vless://, vmess://, etc.)
                    OutlinedTextField(
                        value = rawConfigText,
                        onValueChange = { rawConfigText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        label = { Text("متن کانفیگ‌ها (vless, vmess, trojan, ss)", fontSize = 12.sp) },
                        placeholder = { Text("vless://...\nیا رشته base64", color = TextMuted, fontSize = 12.sp) },
                        colors = fieldColors(),
                        shape = RoundedCornerShape(14.dp),
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ContentPaste,
                                contentDescription = "جای‌گذاری",
                                tint = NeonCyan,
                                modifier = Modifier
                                    .clickable {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val text = clipboard.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
                                        if (text.isNotBlank()) rawConfigText = text
                                    }
                                    .padding(8.dp)
                            )
                        }
                    )
                }

                2 -> {
                    // Direct Panel API Login (Marzban / X-UI)
                    OutlinedTextField(
                        value = panelHost,
                        onValueChange = { panelHost = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("آدرس سرور پنل (Host:Port)", fontSize = 12.sp) },
                        placeholder = { Text("https://panel.example.com:8443", color = TextMuted, fontSize = 12.sp) },
                        colors = fieldColors(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("نام کاربری اشتراک", fontSize = 12.sp) },
                        placeholder = { Text("username", color = TextMuted, fontSize = 12.sp) },
                        colors = fieldColors(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onDismissRequest,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("انصراف")
                }

                Spacer(modifier = Modifier.width(10.dp))

                Button(
                    onClick = {
                        if (selectedTabIndex == 1) {
                            if (rawConfigText.isNotBlank()) {
                                onImportRawConfig(rawConfigText)
                                onDismissRequest()
                            }
                        } else if (selectedTabIndex == 0) {
                            if (subscriptionUrl.isNotBlank()) {
                                onAddSubscription(panelName, subscriptionUrl, "", "")
                                onDismissRequest()
                            }
                        } else {
                            if (panelHost.isNotBlank()) {
                                val generatedSub = "$panelHost/sub/$username"
                                onAddSubscription(panelName.ifBlank { "پنل مرزبان" }, generatedSub, panelHost, username)
                                onDismissRequest()
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonCyan,
                        contentColor = CyberDarkBackground
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("افزودن و دریافت سرورها", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = CyberSurface,
    unfocusedContainerColor = CyberSurface,
    focusedBorderColor = NeonCyan,
    unfocusedBorderColor = CyberSurfaceBorder,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    focusedLabelColor = NeonCyan,
    unfocusedLabelColor = TextSecondary
)
