package com.example.ui

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AuthSessionStatus
import com.example.data.model.VpnStatus
import com.example.data.repository.PanelRepository
import com.example.ui.components.AddPanelDialog
import com.example.ui.components.CloudflareScannerBottomSheet
import com.example.ui.components.ServerSelectionBottomSheet
import com.example.ui.components.TelegramLoginDialog
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LogsScreen
import com.example.ui.screens.PanelManagementScreen
import com.example.ui.screens.ServersScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.CyberDarkBackground
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.vpn.VpnController
import kotlinx.coroutines.launch

enum class ScreenTab(val title: String) {
    HOME("خانه"),
    SERVERS("سرورها"),
    SERVICES("سرویس‌های من"),
    SETTINGS("تنظیمات"),
    LOGS("گزارشات")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(repository: PanelRepository) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var currentTab by remember { mutableStateOf(ScreenTab.HOME) }
    var showServerSheet by remember { mutableStateOf(false) }
    var showAddPanelDialog by remember { mutableStateOf(false) }
    var showTelegramLoginDialog by remember { mutableStateOf(false) }
    var showCfScannerSheet by remember { mutableStateOf(false) }

    val status by VpnController.status.collectAsState()
    val stats by VpnController.stats.collectAsState()
    val trafficHistory by VpnController.trafficHistory.collectAsState()

    val telegramUser by repository.telegramUser.collectAsState()
    val authSession by repository.currentAuthSession.collectAsState()
    val userServices by repository.userServices.collectAsState()
    val activeUserService by repository.activeUserService.collectAsState()
    val botConfig by repository.botConfig.collectAsState()
    val authLoading by repository.authLoading.collectAsState()

    val activePanel by repository.activePanel.collectAsState()
    val servers by repository.servers.collectAsState()
    val selectedServer by repository.selectedServer.collectAsState()
    val settings by repository.settings.collectAsState()
    val logs by repository.logs.collectAsState()
    val isRefreshing by repository.isRefreshing.collectAsState()

    val cfScanResults by repository.cfScanResults.collectAsState()
    val cfScanState by repository.cfScanState.collectAsState()
    val activeCleanEndpoint by repository.activeCleanEndpoint.collectAsState()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val cfSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // VPN Permission Launcher
    val vpnPrepareLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedServer?.let { server ->
                VpnController.startVpnService(context, server, settings)
            }
        } else {
            Toast.makeText(context, "مجوز دسترسی VPN داده نشد", Toast.LENGTH_SHORT).show()
        }
    }

    // Toggle VPN action
    val onToggleVpn = {
        if (status == VpnStatus.CONNECTED || status == VpnStatus.CONNECTING) {
            VpnController.stopVpn(context)
        } else {
            val server = selectedServer ?: servers.firstOrNull()
            if (server != null) {
                VpnController.startVpnWithCheck(
                    context = context,
                    server = server,
                    settings = settings,
                    vpnPrepareLauncher = vpnPrepareLauncher,
                    onNeedsPrepare = {
                        Toast.makeText(context, "درخواست مجوز VPN سیستم...", Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                Toast.makeText(context, "لطفاً ابتدا یک سرور را انتخاب کنید", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Handle back button on secondary screens
    if (currentTab != ScreenTab.HOME) {
        BackHandler {
            currentTab = ScreenTab.HOME
        }
    }

    // RTL Persian Layout
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            containerColor = CyberDarkBackground,
            contentColor = TextPrimary,
            bottomBar = {
                NavigationBar(
                    containerColor = CyberSurface,
                    contentColor = TextPrimary,
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .background(CyberSurface)
                        .windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    NavigationBarItem(
                        selected = currentTab == ScreenTab.HOME,
                        onClick = { currentTab = ScreenTab.HOME },
                        icon = { Icon(Icons.Default.Home, contentDescription = "خانه") },
                        label = { Text("خانه", fontSize = 11.sp, fontWeight = if (currentTab == ScreenTab.HOME) FontWeight.Bold else FontWeight.Normal) },
                        colors = navItemColors()
                    )
                    NavigationBarItem(
                        selected = currentTab == ScreenTab.SERVERS,
                        onClick = { currentTab = ScreenTab.SERVERS },
                        icon = { Icon(Icons.Default.Dns, contentDescription = "سرورها") },
                        label = { Text("سرورها", fontSize = 11.sp, fontWeight = if (currentTab == ScreenTab.SERVERS) FontWeight.Bold else FontWeight.Normal) },
                        colors = navItemColors()
                    )
                    NavigationBarItem(
                        selected = currentTab == ScreenTab.SERVICES,
                        onClick = { currentTab = ScreenTab.SERVICES },
                        icon = { Icon(Icons.Default.Send, contentDescription = "سرویس‌های من") },
                        label = { Text("سرویس‌های من", fontSize = 11.sp, fontWeight = if (currentTab == ScreenTab.SERVICES) FontWeight.Bold else FontWeight.Normal) },
                        colors = navItemColors()
                    )
                    NavigationBarItem(
                        selected = currentTab == ScreenTab.SETTINGS,
                        onClick = { currentTab = ScreenTab.SETTINGS },
                        icon = { Icon(Icons.Default.Tune, contentDescription = "تنظیمات") },
                        label = { Text("تنظیمات", fontSize = 11.sp, fontWeight = if (currentTab == ScreenTab.SETTINGS) FontWeight.Bold else FontWeight.Normal) },
                        colors = navItemColors()
                    )
                    NavigationBarItem(
                        selected = currentTab == ScreenTab.LOGS,
                        onClick = { currentTab = ScreenTab.LOGS },
                        icon = { Icon(Icons.Default.Terminal, contentDescription = "گزارشات") },
                        label = { Text("گزارشات", fontSize = 11.sp, fontWeight = if (currentTab == ScreenTab.LOGS) FontWeight.Bold else FontWeight.Normal) },
                        colors = navItemColors()
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentTab) {
                    ScreenTab.HOME -> {
                        HomeScreen(
                            status = status,
                            stats = stats,
                            trafficHistory = trafficHistory,
                            selectedServer = selectedServer,
                            activePanel = activePanel,
                            activeService = activeUserService,
                            telegramUser = telegramUser,
                            botConfig = botConfig,
                            isDarkMode = settings.isDarkMode,
                            isRefreshingPanel = isRefreshing,
                            activeCleanEndpoint = activeCleanEndpoint,
                            onToggleVpn = onToggleVpn,
                            onOpenServerPicker = { showServerSheet = true },
                            onOpenTelegramLogin = { showTelegramLoginDialog = true },
                            onRefreshPanel = { repository.syncWithBotServer() },
                            onNavigateToServices = { currentTab = ScreenTab.SERVICES },
                            onToggleTheme = { repository.toggleTheme() },
                            onConnectFastestServer = {
                                repository.connectFastestServer {
                                    if (status != VpnStatus.CONNECTED && status != VpnStatus.CONNECTING) {
                                        onToggleVpn()
                                    }
                                }
                            },
                            onOpenCfScanner = { showCfScannerSheet = true }
                        )
                    }
                    ScreenTab.SERVERS -> {
                        ServersScreen(
                            servers = servers,
                            selectedServer = selectedServer,
                            activeCleanEndpoint = activeCleanEndpoint,
                            onSelectServer = { server ->
                                repository.selectServer(server)
                                currentTab = ScreenTab.HOME
                            },
                            onAutoSelectBest = { repository.autoSelectBestServer() },
                            onTestAllPings = { repository.testAllPings() },
                            onTestSinglePing = { id -> repository.testSinglePing(id) },
                            onAddConfigClick = { showAddPanelDialog = true },
                            onOpenCfScanner = { showCfScannerSheet = true }
                        )
                    }
                    ScreenTab.SERVICES -> {
                        PanelManagementScreen(
                            userServices = userServices,
                            activeService = activeUserService,
                            telegramUser = telegramUser,
                            botConfig = botConfig,
                            isRefreshing = isRefreshing,
                            onSelectService = { svc ->
                                repository.selectUserService(svc)
                                currentTab = ScreenTab.HOME
                            },
                            onSyncWithBotServer = { repository.syncWithBotServer() },
                            onOpenTelegramLogin = { showTelegramLoginDialog = true },
                            onLogoutTelegram = { repository.logoutTelegram() },
                            onOpenAddManualConfig = { showAddPanelDialog = true }
                        )
                    }
                    ScreenTab.SETTINGS -> {
                        SettingsScreen(
                            settings = settings,
                            botConfig = botConfig,
                            onSettingsChanged = { newSettings -> repository.updateSettings(newSettings) },
                            onBotConfigChanged = { newBotConfig -> repository.updateServerBotConfig(newBotConfig) }
                        )
                    }
                    ScreenTab.LOGS -> {
                        LogsScreen(
                            logs = logs,
                            onClearLogs = { repository.clearLogs() },
                            panelHost = botConfig.serverApiUrl,
                            onAddLog = { lvl, msg -> repository.addLog(lvl, msg) }
                        )
                    }
                }
            }

            // Server Selection Bottom Sheet
            if (showServerSheet) {
                ServerSelectionBottomSheet(
                    sheetState = sheetState,
                    servers = servers,
                    selectedServer = selectedServer,
                    onServerSelect = { server ->
                        repository.selectServer(server)
                        coroutineScope.launch {
                            sheetState.hide()
                            showServerSheet = false
                        }
                    },
                    onAutoSelectBest = {
                        repository.autoSelectBestServer()
                        coroutineScope.launch {
                            sheetState.hide()
                            showServerSheet = false
                        }
                    },
                    onTestAllPings = { repository.testAllPings() },
                    onTestSinglePing = { id -> repository.testSinglePing(id) },
                    onDismissRequest = { showServerSheet = false }
                )
            }

            // Auto-create login session when dialog opens & auto-close upon verification
            LaunchedEffect(showTelegramLoginDialog) {
                if (showTelegramLoginDialog && (authSession.sessionId.isBlank() || authSession.status != AuthSessionStatus.PENDING || authSession.isExpired)) {
                    repository.createAuthSession(context = context, openTelegramApp = false)
                }
            }

            LaunchedEffect(authSession.status, showTelegramLoginDialog) {
                if (showTelegramLoginDialog && authSession.status == AuthSessionStatus.VERIFIED) {
                    kotlinx.coroutines.delay(700)
                    Toast.makeText(context, "هویت تایید شد و اشتراک‌ها از API دریافت شدند!", Toast.LENGTH_SHORT).show()
                    showTelegramLoginDialog = false
                }
            }

            // Telegram Login Dialog
            if (showTelegramLoginDialog) {
                TelegramLoginDialog(
                    authSession = authSession,
                    botConfig = botConfig,
                    isLoading = authLoading,
                    onStartDeepLinkLogin = {
                        repository.startTelegramDeepLinkLogin(context)
                    },
                    onCreateSessionOnly = {
                        repository.createAuthSession(context = context, openTelegramApp = false)
                    },
                    onLoginWithCode = { code ->
                        repository.loginWithTelegramCode(code) { success, msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            if (success) showTelegramLoginDialog = false
                        }
                    },
                    onLoginWithCredentials = { id, pass ->
                        repository.loginWithTelegramCredentials(id, pass) { success, msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            if (success) showTelegramLoginDialog = false
                        }
                    },
                    onUpdateBotConfig = { newConfig ->
                        repository.updateServerBotConfig(newConfig)
                    },
                    onDismissRequest = { showTelegramLoginDialog = false }
                )
            }

            // Manual Add Panel Dialog
            if (showAddPanelDialog) {
                AddPanelDialog(
                    onDismissRequest = { showAddPanelDialog = false },
                    onAddSubscription = { name, sub, api, user ->
                        repository.addOrUpdatePanel(name, sub, api, user)
                    },
                    onImportRawConfig = { raw ->
                        repository.importRawConfig(raw)
                    }
                )
            }

            // Cloudflare Clean IP Scanner Bottom Sheet
            if (showCfScannerSheet) {
                val cdnCount = servers.count { it.isCdn || it.host.contains("tunnel-cloud") || it.name.contains("CDN", ignoreCase = true) }
                CloudflareScannerBottomSheet(
                    sheetState = cfSheetState,
                    scanState = cfScanState,
                    results = cfScanResults,
                    activeCleanEndpoint = activeCleanEndpoint,
                    cdnServerCount = cdnCount,
                    onStartScan = { isp, port -> repository.startCloudflareScan(isp, port) },
                    onStopScan = { repository.stopCloudflareScan() },
                    onTestSingle = { ip, port -> repository.testSingleEndpoint(ip, port) },
                    onApplyEndpoint = { endpoint ->
                        repository.applyCleanEndpointToServers(endpoint)
                        Toast.makeText(context, "IP تمیز ($endpoint) روی سرورهای CDN اعمال شد", Toast.LENGTH_SHORT).show()
                    },
                    onRestoreOriginal = {
                        repository.restoreOriginalEndpoints()
                        Toast.makeText(context, "سرورها به آدرس پیش‌فرض بازگردانده شدند", Toast.LENGTH_SHORT).show()
                    },
                    onAddCustomEndpoint = { custom, port ->
                        repository.addCustomEndpoint(custom, port)
                    },
                    onDismissRequest = { showCfScannerSheet = false }
                )
            }
        }
    }
}

@Composable
private fun navItemColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = NeonCyan,
    selectedTextColor = NeonCyan,
    unselectedIconColor = TextMuted,
    unselectedTextColor = TextMuted,
    indicatorColor = CyberSurfaceBorder
)
