package pl.torvinek.plusxmobile

import android.net.Uri
import java.net.URL

internal object AppConfig {
    val portalBaseUrl: String = BuildConfig.PORTAL_BASE_URL.trimEnd('/')
    val backendBaseUrl: String = BuildConfig.BACKEND_BASE_URL.trimEnd('/')

    /**
     * Injected at build time from local.properties, an environment variable,
     * a Gradle property, or a GitHub Actions secret. It is intentionally not
     * committed to the repository.
     *
     * A long-lived client token can never be made unrecoverable inside an APK,
     * so it must grant only the minimum read/submit permissions required by
     * the mobile application and must never be an administrative credential.
     */
    val backendToken: String = BuildConfig.BACKEND_TOKEN

    private val portalUrl = URL(portalBaseUrl)
    private val backendUrl = URL(backendBaseUrl)

    val portalHost: String = portalUrl.host
    val backendHost: String = backendUrl.host
    val portalAuthority: String = if (portalUrl.port == -1) portalUrl.host else "${portalUrl.host}:${portalUrl.port}"

    fun requirePortalUrl(address: String): URL =
        NetworkPolicy.requireHttpsHost(address, portalHost)

    fun requireBackendUrl(address: String): URL =
        NetworkPolicy.requireHttpsHost(address, backendHost)

    fun isPortalUrl(address: String): Boolean =
        NetworkPolicy.isHttpsHost(address, portalHost)

    fun isPortalUri(uri: Uri): Boolean =
        uri.scheme.equals("https", ignoreCase = true) &&
            uri.host.equals(portalHost, ignoreCase = true)
}
