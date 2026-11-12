package com.example.data.parser

import android.util.Base64
import com.example.data.model.ProxyProtocol
import com.example.data.model.ServerNode
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.UUID

object ConfigParser {

    fun parseSubscriptionUserInfo(headerValue: String?): Triple<Long, Long, Long>? {
        if (headerValue.isNullOrBlank()) return null
        // Header format: upload=1073741824; download=2147483648; total=53687091200; expire=1735689600
        var upload = 0L
        var download = 0L
        var total = 0L
        var expire = 0L

        try {
            val parts = headerValue.split(";")
            for (part in parts) {
                val kv = part.trim().split("=")
                if (kv.size == 2) {
                    val key = kv[0].trim().lowercase()
                    val value = kv[1].trim().toLongOrNull() ?: 0L
                    when (key) {
                        "upload" -> upload = value
                        "download" -> download = value
                        "total" -> total = value
                        "expire" -> expire = value * 1000 // Convert sec to ms
                    }
                }
            }
            return Triple(upload + download, total, expire)
        } catch (_: Exception) {
            return null
        }
    }

    fun parseConfigLines(rawContent: String): List<ServerNode> {
        val trimmed = rawContent.trim()
        val textToParse = if (isLikelyBase64(trimmed)) {
            decodeBase64Safe(trimmed)
        } else {
            trimmed
        }

        val lines = textToParse.split("\r\n", "\n", "\r")
        val result = mutableListOf<ServerNode>()

        for (line in lines) {
            val clean = line.trim()
            if (clean.isBlank() || clean.startsWith("#")) continue
            val node = parseSingleLink(clean)
            if (node != null) {
                result.add(node)
            }
        }
        return result
    }

    private fun isLikelyBase64(content: String): Boolean {
        if (content.startsWith("vless://") ||
            content.startsWith("vmess://") ||
            content.startsWith("trojan://") ||
            content.startsWith("ss://")) {
            return false
        }
        val stripped = content.replace("\n", "").replace("\r", "")
        return stripped.matches(Regex("^[A-Za-z0-9+/=]+$")) && (stripped.length % 4 == 0)
    }

    private fun decodeBase64Safe(base64Str: String): String {
        return try {
            val clean = base64Str.replace("\n", "").replace("\r", "").trim()
            val decoded = java.util.Base64.getDecoder().decode(clean)
            String(decoded, StandardCharsets.UTF_8)
        } catch (_: Exception) {
            try {
                val clean = base64Str.replace("\n", "").replace("\r", "").trim()
                val decoded = Base64.decode(clean, Base64.DEFAULT or Base64.URL_SAFE)
                String(decoded, StandardCharsets.UTF_8)
            } catch (_: Exception) {
                base64Str
            }
        }
    }

    fun parseSingleLink(raw: String): ServerNode? {
        val link = raw.trim()
        return when {
            link.startsWith("vless://") -> parseVless(link)
            link.startsWith("trojan://") -> parseTrojan(link)
            link.startsWith("vmess://") -> parseVmess(link)
            link.startsWith("ss://") -> parseShadowsocks(link)
            else -> null
        }
    }

    private fun parseVless(link: String): ServerNode? {
        return try {
            val withoutScheme = link.removePrefix("vless://")
            val hashIdx = withoutScheme.indexOf('#')
            val fragment = if (hashIdx != -1) {
                decodeUrlComponent(withoutScheme.substring(hashIdx + 1))
            } else ""

            val mainPart = if (hashIdx != -1) withoutScheme.substring(0, hashIdx) else withoutScheme
            val questionIdx = mainPart.indexOf('?')
            val queryMap = if (questionIdx != -1) {
                parseQuery(mainPart.substring(questionIdx + 1))
            } else emptyMap()

            val authority = if (questionIdx != -1) mainPart.substring(0, questionIdx) else mainPart
            val atIdx = authority.indexOf('@')
            val uuid = if (atIdx != -1) authority.substring(0, atIdx) else ""
            val hostPort = if (atIdx != -1) authority.substring(atIdx + 1) else authority

            val (host, port) = parseHostPort(hostPort, 443)
            val name = fragment.ifBlank { "VLESS - $host" }
            val security = queryMap["security"] ?: "tls"
            val sni = queryMap["sni"] ?: host
            val netType = queryMap["type"] ?: "tcp"
            val isCdn = netType == "ws" || netType == "grpc" || name.contains("cdn", ignoreCase = true) || host.contains("cloudflare", ignoreCase = true) || host.contains("cf", ignoreCase = true)
            val (flag, countryName, countryCode) = inferLocation(name, host)

            ServerNode(
                id = UUID.randomUUID().toString(),
                name = name,
                protocol = ProxyProtocol.VLESS,
                host = host,
                port = port,
                uuidOrPassword = uuid,
                countryCode = countryCode,
                countryName = countryName,
                flagEmoji = flag,
                security = security,
                sni = sni,
                rawConfig = link,
                isCdn = isCdn,
                originalHost = host
            )
        } catch (_: Exception) {
            null
        }
    }

    private fun parseTrojan(link: String): ServerNode? {
        return try {
            val withoutScheme = link.removePrefix("trojan://")
            val hashIdx = withoutScheme.indexOf('#')
            val fragment = if (hashIdx != -1) {
                decodeUrlComponent(withoutScheme.substring(hashIdx + 1))
            } else ""

            val mainPart = if (hashIdx != -1) withoutScheme.substring(0, hashIdx) else withoutScheme
            val questionIdx = mainPart.indexOf('?')
            val queryMap = if (questionIdx != -1) {
                parseQuery(mainPart.substring(questionIdx + 1))
            } else emptyMap()

            val authority = if (questionIdx != -1) mainPart.substring(0, questionIdx) else mainPart
            val atIdx = authority.indexOf('@')
            val password = if (atIdx != -1) authority.substring(0, atIdx) else ""
            val hostPort = if (atIdx != -1) authority.substring(atIdx + 1) else authority

            val (host, port) = parseHostPort(hostPort, 443)
            val name = fragment.ifBlank { "Trojan - $host" }
            val sni = queryMap["sni"] ?: host
            val netType = queryMap["type"] ?: "tcp"
            val isCdn = netType == "ws" || netType == "grpc" || name.contains("cdn", ignoreCase = true)
            val (flag, countryName, countryCode) = inferLocation(name, host)

            ServerNode(
                id = UUID.randomUUID().toString(),
                name = name,
                protocol = ProxyProtocol.TROJAN,
                host = host,
                port = port,
                uuidOrPassword = password,
                countryCode = countryCode,
                countryName = countryName,
                flagEmoji = flag,
                security = "tls",
                sni = sni,
                rawConfig = link,
                isCdn = isCdn,
                originalHost = host
            )
        } catch (_: Exception) {
            null
        }
    }

    private fun parseVmess(link: String): ServerNode? {
        return try {
            val b64 = link.removePrefix("vmess://").trim()
            val decodedBytes = try {
                java.util.Base64.getDecoder().decode(b64)
            } catch (_: Exception) {
                Base64.decode(b64, Base64.DEFAULT)
            }
            val jsonStr = String(decodedBytes, StandardCharsets.UTF_8)

            val add = extractJsonString(jsonStr, "add") ?: "server.com"
            val port = extractJsonInt(jsonStr, "port") ?: 443
            val id = extractJsonString(jsonStr, "id") ?: ""
            val ps = extractJsonString(jsonStr, "ps") ?: "VMess - $add"
            val sni = extractJsonString(jsonStr, "sni") ?: add
            val net = extractJsonString(jsonStr, "net") ?: "tcp"
            val isCdn = net == "ws" || net == "grpc" || ps.contains("cdn", ignoreCase = true)

            val (flag, countryName, countryCode) = inferLocation(ps, add)

            ServerNode(
                id = UUID.randomUUID().toString(),
                name = ps,
                protocol = ProxyProtocol.VMESS,
                host = add,
                port = port,
                uuidOrPassword = id,
                countryCode = countryCode,
                countryName = countryName,
                flagEmoji = flag,
                security = "tls",
                sni = sni,
                rawConfig = link,
                isCdn = isCdn,
                originalHost = add
            )
        } catch (_: Exception) {
            null
        }
    }

    private fun parseShadowsocks(link: String): ServerNode? {
        return try {
            val withoutScheme = link.removePrefix("ss://")
            val hashIdx = withoutScheme.indexOf('#')
            val fragment = if (hashIdx != -1) {
                decodeUrlComponent(withoutScheme.substring(hashIdx + 1))
            } else ""

            val mainPart = if (hashIdx != -1) withoutScheme.substring(0, hashIdx) else withoutScheme
            val atIdx = mainPart.indexOf('@')
            val userInfo = if (atIdx != -1) mainPart.substring(0, atIdx) else ""
            val hostPort = if (atIdx != -1) mainPart.substring(atIdx + 1) else mainPart

            val (host, port) = parseHostPort(hostPort, 8388)
            val name = fragment.ifBlank { "SS - $host" }
            val (flag, countryName, countryCode) = inferLocation(name, host)

            ServerNode(
                id = UUID.randomUUID().toString(),
                name = name,
                protocol = ProxyProtocol.SHADOWSOCKS,
                host = host,
                port = port,
                uuidOrPassword = userInfo,
                countryCode = countryCode,
                countryName = countryName,
                flagEmoji = flag,
                rawConfig = link
            )
        } catch (_: Exception) {
            null
        }
    }

    private fun parseHostPort(hostPort: String, defaultPort: Int): Pair<String, Int> {
        val clean = hostPort.trim().split("/").first()
        val colonIdx = clean.lastIndexOf(':')
        return if (colonIdx != -1) {
            val host = clean.substring(0, colonIdx)
            val port = clean.substring(colonIdx + 1).toIntOrNull() ?: defaultPort
            Pair(host, port)
        } else {
            Pair(clean, defaultPort)
        }
    }

    private fun parseQuery(query: String): Map<String, String> {
        val result = mutableMapOf<String, String>()
        for (param in query.split("&")) {
            val pair = param.split("=")
            if (pair.size == 2) {
                result[pair[0].trim()] = decodeUrlComponent(pair[1].trim())
            }
        }
        return result
    }

    private fun decodeUrlComponent(str: String): String {
        return try {
            URLDecoder.decode(str, "UTF-8")
        } catch (_: Exception) {
            str
        }
    }

    private fun extractJsonString(json: String, key: String): String? {
        val pattern = Regex("\"$key\"\\s*:\\s*\"([^\"]*)\"")
        return pattern.find(json)?.groupValues?.get(1)
    }

    private fun extractJsonInt(json: String, key: String): Int? {
        val pattern = Regex("\"$key\"\\s*:\\s*([0-9]+)")
        return pattern.find(json)?.groupValues?.get(1)?.toIntOrNull()
    }

    private fun inferLocation(name: String, host: String): Triple<String, String, String> {
        val combined = "$name $host".lowercase()
        return when {
            combined.contains("de") || combined.contains("germany") || combined.contains("آلمان") || combined.contains("frankfurt") ->
                Triple("🇩🇪", "آلمان", "DE")
            combined.contains("fi") || combined.contains("finland") || combined.contains("فنلاند") || combined.contains("helsinki") ->
                Triple("🇫🇮", "فنلاند", "FI")
            combined.contains("fr") || combined.contains("france") || combined.contains("فرانسه") || combined.contains("paris") ->
                Triple("🇫🇷", "فرانسه", "FR")
            combined.contains("nl") || combined.contains("netherlands") || combined.contains("هلند") || combined.contains("amsterdam") ->
                Triple("🇳🇱", "هلند", "NL")
            combined.contains("tr") || combined.contains("turkey") || combined.contains("ترکیه") || combined.contains("istanbul") ->
                Triple("🇹🇷", "ترکیه", "TR")
            combined.contains("uk") || combined.contains("gb") || combined.contains("england") || combined.contains("انگلیس") || combined.contains("london") ->
                Triple("🇬🇧", "انگلستان", "GB")
            combined.contains("us") || combined.contains("usa") || combined.contains("آمریکا") || combined.contains("america") ->
                Triple("🇺🇸", "آمریکا", "US")
            combined.contains("ir") || combined.contains("iran") || combined.contains("ایران") ->
                Triple("🇮🇷", "ایران", "IR")
            else ->
                Triple("🌐", "سرور ابری", "UN")
        }
    }
}
