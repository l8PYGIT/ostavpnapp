package com.example.data.model

data class TelegramUser(
    val telegramId: String = "",
    val username: String = "",
    val firstName: String = "",
    val authToken: String = "",
    val walletBalanceToman: Long = 0L,
    val isLoggedIn: Boolean = false,
    val loginTime: Long = 0L
)

data class UserService(
    val id: String,
    val serviceName: String,
    val subscriptionUrl: String,
    val totalBytes: Long = 50L * 1024 * 1024 * 1024,
    val usedBytes: Long = 14L * 1024 * 1024 * 1024,
    val expireDateTimestamp: Long = System.currentTimeMillis() + 28L * 24 * 3600 * 1000,
    val status: ServiceStatus = ServiceStatus.ACTIVE,
    val protocol: String = "VLESS Reality / Trojan",
    val serverCount: Int = 6,
    val isDefault: Boolean = false
) {
    val remainingBytes: Long
        get() = (totalBytes - usedBytes).coerceAtLeast(0L)

    val usagePercentage: Float
        get() = if (totalBytes > 0) (usedBytes.toFloat() / totalBytes).coerceIn(0f, 1f) else 0f

    val remainingDays: Int
        get() = (((expireDateTimestamp - System.currentTimeMillis()) / (1000 * 60 * 60 * 24))).toInt().coerceAtLeast(0)
}

enum class ServiceStatus(val label: String) {
    ACTIVE("فعال"),
    EXPIRED("منقضی شده"),
    SUSPENDED("معلق")
}

data class ServerBotConfig(
    val serverApiUrl: String = "https://api.myvpnbot.com",
    val botUsername: String = "MyVpnBot",
    val botDisplayName: String = "ربات هوشمند وی‌پی‌ان تلگرام",
    val supportUsername: String = "vpn_support"
)

data class PanelConfig(
    val id: String,
    val name: String,
    val subscriptionUrl: String,
    val panelApiUrl: String = "",
    val username: String = "",
    val totalBytes: Long = 50L * 1024 * 1024 * 1024,
    val usedBytes: Long = 18L * 1024 * 1024 * 1024,
    val expireDateTimestamp: Long = System.currentTimeMillis() + 25L * 24 * 3600 * 1000,
    val isActive: Boolean = true,
    val serverCount: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    val remainingBytes: Long
        get() = (totalBytes - usedBytes).coerceAtLeast(0L)

    val usagePercentage: Float
        get() = if (totalBytes > 0) (usedBytes.toFloat() / totalBytes).coerceIn(0f, 1f) else 0f

    val remainingDays: Int
        get() = (((expireDateTimestamp - System.currentTimeMillis()) / (1000 * 60 * 60 * 24))).toInt().coerceAtLeast(0)
}

enum class ProxyProtocol(val label: String) {
    VLESS("VLESS"),
    VMESS("VMESS"),
    TROJAN("Trojan"),
    SHADOWSOCKS("Shadowsocks")
}

data class ServerNode(
    val id: String,
    val name: String,
    val protocol: ProxyProtocol,
    val host: String,
    val port: Int,
    val uuidOrPassword: String,
    val countryCode: String = "DE",
    val countryName: String = "آلمان",
    val flagEmoji: String = "🇩🇪",
    val pingMs: Int = -1,
    val isTestingPing: Boolean = false,
    val security: String = "tls",
    val sni: String = "",
    val rawConfig: String = "",
    val isCdn: Boolean = false,
    val cleanEndpoint: String? = null,
    val originalHost: String? = null
)

data class CleanEndpointResult(
    val ip: String,
    val port: Int = 443,
    val pingMs: Int,
    val ispTarget: String = "عمومی",
    val packetLossPercent: Int = 0,
    val isApplied: Boolean = false
)

data class ScanState(
    val isScanning: Boolean = false,
    val totalToScan: Int = 0,
    val scannedCount: Int = 0,
    val currentTestingIp: String = "",
    val cleanFoundCount: Int = 0,
    val selectedIsp: String = "همه",
    val selectedPort: Int = 443
) {
    val progress: Float
        get() = if (totalToScan > 0) (scannedCount.toFloat() / totalToScan).coerceIn(0f, 1f) else 0f
}

enum class VpnStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    DISCONNECTING,
    ERROR
}

data class VpnStats(
    val bytesIn: Long = 0L,
    val bytesOut: Long = 0L,
    val downloadSpeedBytesPerSec: Long = 0L,
    val uploadSpeedBytesPerSec: Long = 0L,
    val durationSeconds: Long = 0L
)

enum class DnsProvider(val label: String, val primary: String, val secondary: String) {
    CLOUDFLARE("Cloudflare (1.1.1.1)", "1.1.1.1", "1.0.0.1"),
    GOOGLE("Google (8.8.8.8)", "8.8.8.8", "8.8.4.4"),
    ADGUARD("AdGuard Anti-Ad (94.140.14.14)", "94.140.14.14", "94.140.14.15"),
    SHECAN("Shecan / ضد تحریم (178.22.122.100)", "178.22.122.100", "185.51.200.2"),
    CUSTOM("سفارشی", "", "")
}

data class AppSettings(
    val dns: DnsProvider = DnsProvider.CLOUDFLARE,
    val customDnsPrimary: String = "1.1.1.1",
    val bypassIran: Boolean = true,
    val bypassLan: Boolean = true,
    val killSwitch: Boolean = false,
    val autoReconnect: Boolean = true,
    val mtu: Int = 1500,
    val isPersianLanguage: Boolean = true,
    val isDarkMode: Boolean = true,
    val autoSelectFastestServer: Boolean = false
)

data class LogEntry(
    val id: Long = System.currentTimeMillis(),
    val timestamp: String,
    val level: LogLevel,
    val message: String
)

enum class LogLevel {
    INFO,
    SUCCESS,
    WARNING,
    ERROR
}
