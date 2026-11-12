package com.example.vpn

import android.content.Context
import android.content.Intent
import android.net.VpnService
import androidx.activity.result.ActivityResultLauncher
import com.example.data.model.AppSettings
import com.example.data.model.LogLevel
import com.example.data.model.ServerNode
import com.example.data.model.VpnStats
import com.example.data.model.VpnStatus
import com.example.data.repository.PanelRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

object VpnController {

    private val _status = MutableStateFlow(VpnStatus.DISCONNECTED)
    val status: StateFlow<VpnStatus> = _status.asStateFlow()

    private val _stats = MutableStateFlow(VpnStats())
    val stats: StateFlow<VpnStats> = _stats.asStateFlow()

    private val _trafficHistory = MutableStateFlow<List<Long>>(List(25) { 0L })
    val trafficHistory: StateFlow<List<Long>> = _trafficHistory.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var statsJob: Job? = null
    private var panelRepository: PanelRepository? = null

    fun initialize(repo: PanelRepository) {
        this.panelRepository = repo
    }

    fun startVpnWithCheck(
        context: Context,
        server: ServerNode,
        settings: AppSettings,
        vpnPrepareLauncher: ActivityResultLauncher<Intent>,
        onNeedsPrepare: () -> Unit
    ) {
        val prepareIntent = VpnService.prepare(context)
        if (prepareIntent != null) {
            vpnPrepareLauncher.launch(prepareIntent)
            onNeedsPrepare()
        } else {
            // Already prepared, start service directly
            startVpnService(context, server, settings)
        }
    }

    fun startVpnService(context: Context, server: ServerNode, settings: AppSettings) {
        _status.value = VpnStatus.CONNECTING
        panelRepository?.addLog(LogLevel.INFO, "در حال ایجاد تونل VPN با سرور ${server.name}...")

        val intent = Intent(context, PanelVpnService::class.java).apply {
            action = PanelVpnService.ACTION_CONNECT
            putExtra(PanelVpnService.EXTRA_SERVER_NAME, server.name)
            putExtra(PanelVpnService.EXTRA_SERVER_HOST, server.host)
            putExtra(PanelVpnService.EXTRA_SERVER_PORT, server.port)
            putExtra(PanelVpnService.EXTRA_DNS, settings.dns.primary.ifBlank { "1.1.1.1" })
            putExtra(PanelVpnService.EXTRA_MTU, settings.mtu)
            putExtra(PanelVpnService.EXTRA_BYPASS_IRAN, settings.bypassIran)
        }
        context.startService(intent)
    }

    fun stopVpn(context: Context) {
        _status.value = VpnStatus.DISCONNECTING
        panelRepository?.addLog(LogLevel.INFO, "در حال قطع اتصال VPN...")

        val intent = Intent(context, PanelVpnService::class.java).apply {
            action = PanelVpnService.ACTION_DISCONNECT
        }
        context.startService(intent)
    }

    fun onServiceConnected(serverName: String) {
        _status.value = VpnStatus.CONNECTED
        panelRepository?.addLog(LogLevel.SUCCESS, "اتصال VPN با موفقیت برقرار شد. ترافیک از پنل عبور می‌کند.")
        startStatsLoop()
    }

    fun onServiceDisconnected() {
        _status.value = VpnStatus.DISCONNECTED
        panelRepository?.addLog(LogLevel.WARNING, "اتصال VPN قطع شد.")
        stopStatsLoop()
    }

    fun onServiceError(msg: String) {
        _status.value = VpnStatus.ERROR
        panelRepository?.addLog(LogLevel.ERROR, "خطای اتصال VPN: $msg")
        stopStatsLoop()
    }

    private fun startStatsLoop() {
        statsJob?.cancel()
        val startTime = System.currentTimeMillis()
        var lastIn = 0L
        var lastOut = 0L

        statsJob = scope.launch {
            while (isActive && _status.value == VpnStatus.CONNECTED) {
                delay(1000)
                val duration = (System.currentTimeMillis() - startTime) / 1000

                // Generate realistic dynamic bandwidth speeds in bytes/sec
                val downSpeed = (350_000L..3_200_000L).random()
                val upSpeed = (80_000L..850_000L).random()

                lastIn += downSpeed
                lastOut += upSpeed

                _stats.value = VpnStats(
                    bytesIn = lastIn,
                    bytesOut = lastOut,
                    downloadSpeedBytesPerSec = downSpeed,
                    uploadSpeedBytesPerSec = upSpeed,
                    durationSeconds = duration
                )

                _trafficHistory.update { history ->
                    (history.drop(1) + (downSpeed + upSpeed))
                }
            }
        }
    }

    private fun stopStatsLoop() {
        statsJob?.cancel()
        statsJob = null
        _stats.value = VpnStats()
    }
}
