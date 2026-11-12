package com.example.data.scanner

import com.example.data.model.CleanEndpointResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket
import kotlin.coroutines.coroutineContext

data class EndpointCandidate(
    val ipOrDomain: String,
    val isp: String,
    val defaultPort: Int = 443
)

object CloudflareScanner {

    val CANDIDATE_ENDPOINTS = listOf(
        // همراه اول (MCI)
        EndpointCandidate("104.16.132.229", "همراه اول (MCI)"),
        EndpointCandidate("104.16.148.243", "همراه اول (MCI)"),
        EndpointCandidate("104.17.241.22", "همراه اول (MCI)"),
        EndpointCandidate("172.64.34.41", "همراه اول (MCI)"),
        EndpointCandidate("172.67.74.152", "همراه اول (MCI)"),
        EndpointCandidate("162.158.84.11", "همراه اول (MCI)"),
        EndpointCandidate("104.16.85.20", "همراه اول (MCI)"),
        EndpointCandidate("104.16.18.94", "همراه اول (MCI)"),
        EndpointCandidate("172.67.180.55", "همراه اول (MCI)"),

        // ایرانسل (MTN Irancell)
        EndpointCandidate("104.22.14.120", "ایرانسل (MTN)"),
        EndpointCandidate("104.24.10.150", "ایرانسل (MTN)"),
        EndpointCandidate("104.26.4.210", "ایرانسل (MTN)"),
        EndpointCandidate("188.114.96.3", "ایرانسل (MTN)"),
        EndpointCandidate("188.114.97.3", "ایرانسل (MTN)"),
        EndpointCandidate("188.114.98.5", "ایرانسل (MTN)"),
        EndpointCandidate("198.41.128.2", "ایرانسل (MTN)"),
        EndpointCandidate("104.26.13.11", "ایرانسل (MTN)"),
        EndpointCandidate("188.114.99.10", "ایرانسل (MTN)"),

        // مخابرات / شاتل / ADSL
        EndpointCandidate("172.66.40.170", "مخابرات/ADSL"),
        EndpointCandidate("172.66.43.25", "مخابرات/ADSL"),
        EndpointCandidate("104.18.2.161", "مخابرات/ADSL"),
        EndpointCandidate("104.18.43.147", "مخابرات/ADSL"),
        EndpointCandidate("104.27.140.2", "مخابرات/ADSL"),
        EndpointCandidate("104.28.16.1", "مخابرات/ADSL"),
        EndpointCandidate("172.67.213.112", "مخابرات/ADSL"),

        // دامنه‌های تمیز Cloudflare
        EndpointCandidate("speed.cloudflare.com", "عمومی / دامنه"),
        EndpointCandidate("cloudflare.com", "عمومی / دامنه"),
        EndpointCandidate("cdnjs.com", "عمومی / دامنه"),
        EndpointCandidate("pages.dev", "عمومی / دامنه")
    )

    suspend fun scanEndpoints(
        filterIsp: String = "همه",
        port: Int = 443,
        onProgress: (scanned: Int, total: Int, currentIp: String, cleanCount: Int) -> Unit,
        onFound: (CleanEndpointResult) -> Unit
    ) = withContext(Dispatchers.IO) {
        val targets = when (filterIsp) {
            "همراه اول (MCI)" -> CANDIDATE_ENDPOINTS.filter { it.isp.contains("همراه اول") }
            "ایرانسل (MTN)" -> CANDIDATE_ENDPOINTS.filter { it.isp.contains("ایرانسل") }
            "مخابرات/ADSL" -> CANDIDATE_ENDPOINTS.filter { it.isp.contains("مخابرات") }
            "دامنه‌ها" -> CANDIDATE_ENDPOINTS.filter { it.isp.contains("دامنه") }
            else -> CANDIDATE_ENDPOINTS
        }

        val total = targets.size
        var scanned = 0
        var cleanCount = 0

        for (candidate in targets) {
            if (!coroutineContext.isActive) break

            onProgress(scanned, total, candidate.ipOrDomain, cleanCount)

            val latency = testEndpointLatency(candidate.ipOrDomain, port)
            scanned++

            if (latency > 0) {
                cleanCount++
                val result = CleanEndpointResult(
                    ip = candidate.ipOrDomain,
                    port = port,
                    pingMs = latency,
                    ispTarget = candidate.isp,
                    packetLossPercent = 0,
                    isApplied = false
                )
                onFound(result)
            }

            onProgress(scanned, total, candidate.ipOrDomain, cleanCount)
            kotlinx.coroutines.delay(100)
        }
    }

    fun testEndpointLatency(ipOrDomain: String, port: Int, timeoutMs: Int = 1500): Int {
        val attempts = 2
        var successfulPings = 0
        var totalLatency = 0L

        for (i in 0 until attempts) {
            val startTime = System.currentTimeMillis()
            try {
                Socket().use { socket ->
                    socket.tcpNoDelay = true
                    socket.soTimeout = timeoutMs
                    socket.connect(InetSocketAddress(ipOrDomain, port), timeoutMs)
                    val elapsed = System.currentTimeMillis() - startTime
                    if (elapsed > 0) {
                        totalLatency += elapsed
                        successfulPings++
                    }
                }
            } catch (_: Exception) {
                // Real socket failure (Timeout, Host Unreachable, Connection Reset/Refused by firewall)
            }
        }

        return if (successfulPings > 0) {
            (totalLatency / successfulPings).toInt().coerceAtLeast(1)
        } else {
            -1 // Unreachable / Filtered / Timed Out
        }
    }
}
