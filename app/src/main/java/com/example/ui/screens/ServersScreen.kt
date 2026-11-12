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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProxyProtocol
import com.example.data.model.ServerNode
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CrimsonError
import com.example.ui.theme.CyberDarkBackground
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceBorder
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.ui.text.font.FontFamily
import com.example.ui.theme.LocalAppColors

@Composable
fun ServersScreen(
    servers: List<ServerNode>,
    selectedServer: ServerNode?,
    activeCleanEndpoint: String? = null,
    onSelectServer: (ServerNode) -> Unit,
    onAutoSelectBest: () -> Unit,
    onTestAllPings: () -> Unit,
    onTestSinglePing: (String) -> Unit,
    onAddConfigClick: () -> Unit,
    onOpenCfScanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("همه") }

    val filterOptions = listOf("همه", "سریع‌ترین", "CDN", "VLESS", "VMESS", "Trojan", "Shadowsocks")

    val filteredList = remember(servers, searchQuery, selectedFilter) {
        var list = servers
        if (searchQuery.isNotBlank()) {
            list = list.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                it.countryName.contains(searchQuery, ignoreCase = true) ||
                it.host.contains(searchQuery, ignoreCase = true)
            }
        }
        when (selectedFilter) {
            "سریع‌ترین" -> list.filter { it.pingMs > 0 }.sortedBy { it.pingMs }
            "CDN" -> list.filter { it.isCdn || it.name.contains("cdn", ignoreCase = true) }
            "VLESS" -> list.filter { it.protocol == ProxyProtocol.VLESS }
            "VMESS" -> list.filter { it.protocol == ProxyProtocol.VMESS }
            "Trojan" -> list.filter { it.protocol == ProxyProtocol.TROJAN }
            "Shadowsocks" -> list.filter { it.protocol == ProxyProtocol.SHADOWSOCKS }
            else -> list
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "سرورهای پنل",
                    color = colors.textPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${servers.size} سرور در دسترس",
                    color = colors.textSecondary,
                    fontSize = 12.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Cloudflare Clean IP Scanner Action Button
                IconButton(
                    onClick = onOpenCfScanner,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(colors.surface)
                        .border(
                            1.dp,
                            if (activeCleanEndpoint != null) EmeraldSuccess else colors.surfaceBorder,
                            CircleShape
                        )
                        .testTag("open_cf_scanner_top_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudQueue,
                        contentDescription = "اسکنر کلودفلر",
                        tint = if (activeCleanEndpoint != null) EmeraldSuccess else colors.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Auto best
                IconButton(
                    onClick = onAutoSelectBest,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(colors.surface)
                        .border(1.dp, colors.surfaceBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "انتخاب بهترین سرور",
                        tint = colors.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Ping all
                IconButton(
                    onClick = onTestAllPings,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(colors.surface)
                        .border(1.dp, colors.surfaceBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.NetworkCheck,
                        contentDescription = "تست پینگ همه",
                        tint = EmeraldSuccess,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Add node
                IconButton(
                    onClick = onAddConfigClick,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(colors.primary)
                        .testTag("add_custom_server_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "افزودن کانفیگ",
                        tint = colors.background,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Cloudflare Clean IP Scanner Banner
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(colors.surfaceVariant)
                .border(
                    1.dp,
                    if (activeCleanEndpoint != null) EmeraldSuccess.copy(alpha = 0.5f) else colors.primary.copy(alpha = 0.35f),
                    RoundedCornerShape(14.dp)
                )
                .clickable { onOpenCfScanner() }
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
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (activeCleanEndpoint != null) EmeraldSuccess.copy(alpha = 0.2f) else colors.primary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudQueue,
                        contentDescription = null,
                        tint = if (activeCleanEndpoint != null) EmeraldSuccess else colors.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "اسکنر اندپوینت‌های کلودفلر (Clean IP)",
                        color = colors.textPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (activeCleanEndpoint != null) "اندپوینت فعال: $activeCleanEndpoint (ضد اختلال)" else "رفع کندی همراه اول و ایرانسل روی کانفیگ‌های CDN",
                        color = if (activeCleanEndpoint != null) EmeraldSuccess else colors.textSecondary,
                        fontSize = 11.sp
                    )
                }
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.primary)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "اسکن",
                    color = colors.background,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("جستجوی سرور یا کشور...", color = TextMuted, fontSize = 13.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = CyberSurface,
                unfocusedContainerColor = CyberSurface,
                focusedBorderColor = NeonCyan,
                unfocusedBorderColor = CyberSurfaceBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Filter Pills
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(filterOptions) { filter ->
                val isSelected = filter == selectedFilter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) NeonCyan else CyberSurface)
                        .border(
                            1.dp,
                            if (isSelected) NeonCyan else CyberSurfaceBorder,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { selectedFilter = filter }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = filter,
                        color = if (isSelected) CyberDarkBackground else TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Server List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredList, key = { it.id }) { server ->
                val isSelected = server.id == selectedServer?.id
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) CyberSurfaceVariant else CyberSurface)
                        .border(
                            1.dp,
                            if (isSelected) NeonCyan else CyberSurfaceBorder,
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { onSelectServer(server) }
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = server.flagEmoji,
                            fontSize = 24.sp
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = server.name,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(colors.surfaceBorder)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = server.protocol.label,
                                        color = colors.primary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                if (server.isCdn || server.name.contains("cdn", ignoreCase = true)) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(colors.primary.copy(alpha = 0.15f))
                                            .border(1.dp, colors.primary.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "CDN",
                                            color = colors.primary,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                if (server.cleanEndpoint != null) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(EmeraldSuccess.copy(alpha = 0.15f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "Clean IP",
                                            color = EmeraldSuccess,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${server.host}:${server.port}",
                                    color = if (server.cleanEndpoint != null) EmeraldSuccess else colors.textMuted,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    // Ping status and selector
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(colors.background)
                                .border(1.dp, colors.surfaceBorder, RoundedCornerShape(10.dp))
                                .clickable { onTestSinglePing(server.id) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            if (server.isTestingPing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    color = colors.primary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                val pingColor = when {
                                    server.pingMs in 1..100 -> EmeraldSuccess
                                    server.pingMs in 101..200 -> colors.primary
                                    server.pingMs > 200 -> AmberWarning
                                    server.pingMs == -2 -> CrimsonError
                                    else -> colors.textMuted
                                }
                                val pingText = when {
                                    server.pingMs > 0 -> "${server.pingMs} ms"
                                    server.pingMs == -2 -> "تایم‌اوت"
                                    else -> "تست"
                                }
                                Text(
                                    text = pingText,
                                    color = pingColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (isSelected) {
                            Spacer(modifier = Modifier.width(10.dp))
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(colors.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = colors.background,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
