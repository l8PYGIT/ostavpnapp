package com.example

import com.example.data.model.ProxyProtocol
import com.example.data.parser.ConfigParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testParseSubscriptionUserInfo() {
        val header = "upload=1073741824; download=2147483648; total=53687091200; expire=1735689600"
        val parsed = ConfigParser.parseSubscriptionUserInfo(header)
        assertNotNull(parsed)
        assertEquals(1073741824L + 2147483648L, parsed!!.first)
        assertEquals(53687091200L, parsed.second)
        assertEquals(1735689600000L, parsed.third)
    }

    @Test
    fun testParseVlessLink() {
        val link = "vless://f648b29c-5182-4ebc-8d19-35a09ce4a001@de-01.tunnel-cloud.net:443?security=reality&sni=speedtest.net#%F0%9F%87%A9%F0%9F%87%AA%20Germany"
        val node = ConfigParser.parseSingleLink(link)
        assertNotNull(node)
        assertEquals(ProxyProtocol.VLESS, node!!.protocol)
        assertEquals("de-01.tunnel-cloud.net", node.host)
        assertEquals(443, node.port)
        assertEquals("f648b29c-5182-4ebc-8d19-35a09ce4a001", node.uuidOrPassword)
        assertEquals("reality", node.security)
    }

    @Test
    fun testParseCdnConfig() {
        val cdnLink = "vless://uuid123@cf-cdn.domain.com:443?type=ws&security=tls&sni=my-sub.site.com#CDN%20Frankfurt"
        val node = ConfigParser.parseSingleLink(cdnLink)
        assertNotNull(node)
        assertEquals(true, node!!.isCdn)
        assertEquals("cf-cdn.domain.com", node.originalHost)
    }

    @Test
    fun testCloudflareScannerCandidates() {
        val candidates = com.example.data.scanner.CloudflareScanner.CANDIDATE_ENDPOINTS
        org.junit.Assert.assertTrue("Candidates list should not be empty", candidates.isNotEmpty())
        org.junit.Assert.assertTrue("Should include MCI candidates", candidates.any { it.isp.contains("همراه اول") })
        org.junit.Assert.assertTrue("Should include Irancell candidates", candidates.any { it.isp.contains("ایرانسل") })

        val ping = com.example.data.scanner.CloudflareScanner.testEndpointLatency("104.16.132.229", 443, timeoutMs = 800)
        org.junit.Assert.assertTrue("Latency should be non-zero (either real ms or -1 if offline)", ping != 0)
    }
}
