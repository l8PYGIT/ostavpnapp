package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import com.example.data.repository.PanelRepository
import com.example.ui.MainApp
import com.example.ui.theme.MyApplicationTheme
import com.example.vpn.VpnController

class MainActivity : ComponentActivity() {

    private lateinit var repository: PanelRepository

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        // Permission result handled
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        repository = PanelRepository(applicationContext)
        VpnController.initialize(repository)

        // Request POST_NOTIFICATIONS on Android 13+ (API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Check if opened via Telegram Deep Link
        handleIncomingIntent(intent)

        setContent {
            val settings by repository.settings.collectAsState()
            MyApplicationTheme(darkTheme = settings.isDarkMode) {
                MainApp(repository = repository)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent?) {
        val uri: Uri? = intent?.data
        if (uri != null && uri.scheme == "panelvpn" && uri.host == "auth") {
            repository.handleDeepLinkAuth(uri) { success, msg ->
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
            }
        }
    }
}
