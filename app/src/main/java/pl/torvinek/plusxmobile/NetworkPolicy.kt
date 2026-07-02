package pl.torvinek.plusxmobile

import java.net.URI
import java.net.URL

/**
 * Separates the two trust zones used by the application.
 *
 * Portal cookies may only be attached to the exact portal host. The backend
 * bearer may only be attached to the exact backend host. Host checks use the
 * parsed URL instead of prefix matching, so lookalike domains are rejected.
 */
internal object NetworkPolicy {
    fun requireHttpsHost(address: String, expectedHost: String): URL {
        val url = URI(address).toURL()
        require(url.protocol.equals("https", ignoreCase = true)) {
            "Only HTTPS connections are allowed."
        }
        require(url.host.equals(expectedHost, ignoreCase = true)) {
            "Unexpected host: ${url.host}"
        }
        return url
    }

    fun isHttpsHost(address: String, expectedHost: String): Boolean =
        runCatching { requireHttpsHost(address, expectedHost) }.isSuccess
}
