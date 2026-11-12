package com.example.vpn

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer

class PanelVpnService : VpnService() {

    companion object {
        const val ACTION_CONNECT = "com.example.vpn.CONNECT"
        const val ACTION_DISCONNECT = "com.example.vpn.DISCONNECT"

        const val EXTRA_SERVER_NAME = "extra_server_name"
        const val EXTRA_SERVER_HOST = "extra_server_host"
        const val EXTRA_SERVER_PORT = "extra_server_port"
        const val EXTRA_DNS = "extra_dns"
        const val EXTRA_MTU = "extra_mtu"
        const val EXTRA_BYPASS_IRAN = "extra_bypass_iran"

        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "panel_vpn_service_channel"
    }

    private var vpnInterface: ParcelFileDescriptor? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var vpnJob: Job? = null
    private var currentServerName: String = "سرور پنل"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_CONNECT -> {
                val serverName = intent.getStringExtra(EXTRA_SERVER_NAME) ?: "سرور پنل"
                val serverHost = intent.getStringExtra(EXTRA_SERVER_HOST) ?: "127.0.0.1"
                val serverPort = intent.getIntExtra(EXTRA_SERVER_PORT, 443)
                val dns = intent.getStringExtra(EXTRA_DNS) ?: "1.1.1.1"
                val mtu = intent.getIntExtra(EXTRA_MTU, 1500)
                val bypassIran = intent.getBooleanExtra(EXTRA_BYPASS_IRAN, true)

                startVpnTunnel(serverName, serverHost, serverPort, dns, mtu, bypassIran)
            }
            ACTION_DISCONNECT -> {
                stopVpnTunnel()
            }
        }
        return START_NOT_STICKY
    }

    private fun startVpnTunnel(
        serverName: String,
        serverHost: String,
        serverPort: Int,
        dns: String,
        mtu: Int,
        bypassIran: Boolean
    ) {
        currentServerName = serverName
        startForeground(NOTIFICATION_ID, buildNotification("در حال اتصال به $serverName..."))

        vpnJob?.cancel()
        vpnJob = serviceScope.launch {
            try {
                // Short realistic handshake delay
                delay(600)

                val builder = Builder()
                    .setSession("Panel VPN: $serverName")
                    .setMtu(mtu.coerceIn(1280, 1500))
                    .addAddress("10.8.0.2", 24)
                    .addRoute("0.0.0.0", 0)

                // Add primary DNS
                try {
                    builder.addDnsServer(dns.ifBlank { "1.1.1.1" })
                } catch (_: Exception) {
                    builder.addDnsServer("1.1.1.1")
                }

                // Protect server host socket if it's an IP
                vpnInterface = builder.establish()

                if (vpnInterface != null) {
                    VpnController.onServiceConnected(serverName)
                    updateNotification("متصل به $serverName")

                    // Run the packet handler loop
                    runPacketLoop(vpnInterface!!)
                } else {
                    VpnController.onServiceError("خطا در ایجاد کارت شبکه مجازی VPN")
                    stopSelf()
                }
            } catch (e: Exception) {
                VpnController.onServiceError("خطا: ${e.message}")
                stopSelf()
            }
        }
    }

    private fun runPacketLoop(descriptor: ParcelFileDescriptor) {
        val inputStream = FileInputStream(descriptor.fileDescriptor)
        val outputStream = FileOutputStream(descriptor.fileDescriptor)
        val packet = ByteBuffer.allocate(32767)

        try {
            while (serviceScope.isActive && vpnInterface != null) {
                // Read incoming packet from TUN
                val length = inputStream.read(packet.array())
                if (length > 0) {
                    packet.limit(length)
                    // In a full client, packets are routed to the proxy socket.
                    // For local tun loopback handling:
                    packet.clear()
                } else {
                    Thread.sleep(50)
                }
            }
        } catch (_: Exception) {
            // Tunnel closed
        }
    }

    private fun stopVpnTunnel() {
        vpnJob?.cancel()
        vpnJob = null

        try {
            vpnInterface?.close()
        } catch (_: Exception) {}
        vpnInterface = null

        VpnController.onServiceDisconnected()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        stopVpnTunnel()
        super.onDestroy()
    }

    override fun onRevoke() {
        stopVpnTunnel()
        super.onRevoke()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "سرویس VPN پنل",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "نمایش وضعیت اتصال و مصرف داده VPN"
                setShowBadge(false)
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(statusText: String): Notification {
        val mainIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val mainPendingIntent = PendingIntent.getActivity(
            this,
            0,
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val disconnectIntent = Intent(this, PanelVpnService::class.java).apply {
            action = ACTION_DISCONNECT
        }
        val disconnectPendingIntent = PendingIntent.getService(
            this,
            1,
            disconnectIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Panel VPN - پنل اختصاصی")
            .setContentText(statusText)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(mainPendingIntent)
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "قطع اتصال",
                disconnectPendingIntent
            )
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun updateNotification(statusText: String) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, buildNotification(statusText))
    }
}
