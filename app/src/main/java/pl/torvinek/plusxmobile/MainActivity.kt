package pl.torvinek.plusxmobile

import android.annotation.SuppressLint
import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.ActivityNotFoundException
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.TextUtils
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebView.WebViewTransport
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLDecoder
import java.net.URLEncoder
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class MainActivity : Activity() {
    private val baseUrl = hidden(
        50, 46, 46, 42, 41, 96, 117, 117, 52, 63, 45, 116, 42, 54, 47, 41, 34, 116, 46, 44
    )
    private val indexUrl = "$baseUrl/index.php"
    private val loginUrl = "$baseUrl/login.php"
    private val telegramNewsUrl = hidden(
        50, 46, 46, 42, 41, 96, 117, 117, 56, 59, 57, 49, 63, 52, 62, 116, 46, 53, 40, 44, 51,
        52, 63, 49, 116, 42, 54, 117, 46, 63, 54, 63, 61, 40, 59, 55, 117, 42, 54, 47, 41,
        34, 117, 45, 51, 59, 62, 53, 55, 53, 41, 57, 51, 101, 54, 51, 55, 51, 46, 103, 98,
        106
    )
    private val telegramEpgUrl = hidden(
        50, 46, 46, 42, 41, 96, 117, 117, 56, 59, 57, 49, 63, 52, 62, 116, 46, 53, 40, 44, 51,
        52, 63, 49, 116, 42, 54, 117, 46, 63, 54, 63, 61, 40, 59, 55, 117, 42, 54, 47, 41,
        34, 117, 63, 42, 61, 63, 44, 63, 52, 46, 101, 54, 51, 55, 51, 46, 103, 104, 106, 106
    )
    private val diagnosticsSubmitUrl = hidden(
        50, 46, 46, 42, 41, 96, 117, 117, 56, 59, 57, 49, 63, 52, 62, 116, 46, 53, 40, 44, 51,
        52, 63, 49, 116, 42, 54, 117, 46, 63, 54, 63, 61, 40, 59, 55, 117, 42, 54, 47, 41,
        34, 117, 62, 51, 59, 61, 52, 53, 41, 46, 51, 57, 41
    )
    private val githubLatestReleaseUrl = hidden(
        50, 46, 46, 42, 41, 96, 117, 117, 59, 42, 51, 116, 61, 51, 46, 50, 47, 56, 116,
        57, 53, 55, 117, 40, 63, 42, 53, 41, 117, 14, 53, 40, 44, 51, 52, 63, 49, 117,
        10, 54, 47, 41, 2, 119, 23, 53, 56, 51, 54, 63, 117, 40, 63, 54, 63, 59, 41,
        63, 41, 117, 54, 59, 46, 63, 41, 46
    )
    private val telegramBackendToken = hidden(
        17, 18, 41, 60, 30, 32, 61, 109, 27, 18, 43, 2, 53, 43, 48, 50, 32, 98, 15, 51, 45,
        18, 30, 17, 45, 14, 46, 61, 19, 111, 10, 32, 107, 56, 54, 30, 110, 43, 46, 22, 40,
        47, 41
    )

    private lateinit var root: FrameLayout
    private var cookieHeader: String = ""
    private var lastDashboardHtml: String = ""
    private val diagnosticsSnapshots = linkedMapOf<String, String>()
    private var triedIndexFirst = false
    private var darkMode = false
    private var currentWebView: WebView? = null
    private var currentScreen = "login"
    private var lastBackPress = 0L
    private var supportPopupShownThisSession = false
    private var lastSettingsItems: List<PageItem> = emptyList()
    private var pendingM3uUser: String = ""
    private var m3uDownloadChoices: Map<String, List<M3uHtmlParser.PlaylistLink>> = emptyMap()
    private var currentM3uUsers: List<String> = emptyList()
    private var currentM3uSelectedUser: String = ""
    private var currentM3uItems: List<PageItem> = emptyList()
    private var m3uPanel: LinearLayout? = null
    private var currentPacketsSelectedUser: String = ""
    private var currentPacketsUsers: List<String> = emptyList()
    private var currentPacketsItems: List<PageItem> = emptyList()
    private var currentPacketsActivePacket: String = ""
    private val resellerAccountActivity = mutableMapOf<String, Boolean>()
    private val resellerAccountDetails = mutableMapOf<String, ResellerHtmlParser.Account>()
    private var packetsPanel: LinearLayout? = null
    private var billingPanel: LinearLayout? = null
    private var currentBillingSelectedUser: String = ""
    private var currentBillingUsers: List<String> = emptyList()
    private var currentBillingEntries: List<BillingHistoryEntry> = emptyList()
    private var currentBillingMainUser: String = ""
    private val billingDetails = mutableMapOf<String, String>()
    private var loginTransitionOverlay: View? = null
    private var pendingUpdateApk: File? = null
    private var startupUpdateDialogShown = false
    private val polishMonths = mapOf(
        "stycznia" to "01",
        "lutego" to "02",
        "marca" to "03",
        "kwietnia" to "04",
        "maja" to "05",
        "czerwca" to "06",
        "lipca" to "07",
        "sierpnia" to "08",
        "wrzesnia" to "09",
        "września" to "09",
        "wrzesien" to "09",
        "wrzesień" to "09",
        "pazdziernika" to "10",
        "października" to "10",
        "pazdziernik" to "10",
        "październik" to "10",
        "listopada" to "11",
        "listopad" to "11",
        "grudnia" to "12",
        "grudzien" to "12",
        "grudzień" to "12",
        "styczen" to "01",
        "styczeń" to "01",
        "luty" to "02",
        "marzec" to "03",
        "kwiecien" to "04",
        "kwiecień" to "04",
        "maj" to "05",
        "czerwiec" to "06",
        "lipiec" to "07",
        "sierpien" to "08",
        "sierpień" to "08"
    )
    private val displayMonths = mapOf(
        "01" to "Styczen",
        "02" to "Luty",
        "03" to "Marzec",
        "04" to "Kwiecien",
        "05" to "Maj",
        "06" to "Czerwiec",
        "07" to "Lipiec",
        "08" to "Sierpien",
        "09" to "Wrzesien",
        "10" to "Pazdziernik",
        "11" to "Listopad",
        "12" to "Grudzien"
    )

    private fun hidden(vararg values: Int): String {
        val key = 0x5A
        return values.map { (it xor key).toChar() }.joinToString("")
    }

    private data class PageItem(
        val title: String,
        val subtitle: String = "",
        val value: String = "",
        val actionUrl: String = "",
        val copyText: String = "",
        val actionLabel: String = "Otworz",
        val secondActionUrl: String = "",
        val secondActionLabel: String = "",
        val badgeText: String = "",
        val badgeImageRes: Int = 0,
        val cornerText: String = ""
    )

    private data class StartupUpdateInfo(
        val tag: String,
        val apkUrl: String,
        val shaUrl: String,
        val releaseName: String,
        val releaseBody: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        darkMode = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        root = FrameLayout(this)
        setContentView(root)
        root.setBackgroundColor(bgColor())

        CookieManager.getInstance().setAcceptCookie(true)
        setupNotifications()
        showLogin()
    }

    override fun onResume() {
        super.onResume()
        pendingUpdateApk?.let { file ->
            if (file.exists() && canInstallPackages()) {
                pendingUpdateApk = null
                launchApkInstaller(file)
            }
        }
    }

    private fun setupNotifications() {
        NewsNotificationReceiver.schedule(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 501)
        } else {
            NewsNotificationReceiver.checkNow(this)
        }
        checkStartupUpdatePrompt()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 501 && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
            NewsNotificationReceiver.checkNow(this)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun showLogin() {
        root.removeAllViews()
        root.setBackgroundColor(bgColor())
        currentScreen = "login"
        currentWebView = null

        val webView = WebView(this)
        currentWebView = webView
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true
        webView.settings.setSupportZoom(false)
        webView.settings.builtInZoomControls = false
        webView.settings.displayZoomControls = false
        webView.settings.userAgentString = webView.settings.userAgentString + " PlusXMobile"

        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView, url: String, favicon: android.graphics.Bitmap?) {
                val uri = Uri.parse(sanitizePortalUrl(Uri.parse(url)))
                if (uri.host == "new.plusx.tv" && uri.path?.endsWith("/login.php") != true) {
                    showLoginTransitionOverlay()
                    scheduleDashboardPoll(view)
                }
            }

            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val cleanUrl = sanitizePortalUrl(request.url)
                if (cleanUrl != request.url.toString()) {
                    view.loadUrl(cleanUrl)
                    return true
                }
                return false
            }

            override fun onPageFinished(view: WebView, url: String) {
                injectMobileLoginCss(view)

                val cleanUrl = sanitizePortalUrl(Uri.parse(url))
                if (cleanUrl != url) {
                    view.loadUrl(cleanUrl)
                    return
                }

                val uri = Uri.parse(cleanUrl)
                if (uri.host == "new.plusx.tv" && uri.path?.endsWith("/index.php") == true) {
                    showLoginTransitionOverlay()
                    extractDashboardFromWebView(view)
                } else if (uri.host == "new.plusx.tv") {
                    detectDashboardFromWebView(view)
                } else {
                    hideLoginTransitionOverlay()
                }
            }
        }

        root.addView(webView, FrameLayout.LayoutParams(-1, -1))
        triedIndexFirst = true
        webView.loadUrl(indexUrl)
    }

    private fun detectDashboardFromWebView(webView: WebView) {
        webView.evaluateJavascript("document.body ? document.body.innerText : ''") { encodedText ->
            if (currentWebView !== webView) return@evaluateJavascript
            val text = runCatching {
                JSONTokener(encodedText).nextValue() as String
            }.getOrDefault("")
            if (
                text.contains("DASHBOARD", ignoreCase = true) ||
                text.contains("Your balance", ignoreCase = true) ||
                text.contains("TV channels on the server", ignoreCase = true)
            ) {
                showLoginTransitionOverlay()
                extractDashboardFromWebView(webView)
            } else {
                hideLoginTransitionOverlay()
            }
        }
    }

    private fun scheduleDashboardPoll(webView: WebView, attempt: Int = 0) {
        Handler(Looper.getMainLooper()).postDelayed({
            if (currentWebView !== webView || currentScreen != "login") return@postDelayed
            webView.evaluateJavascript("document.documentElement.outerHTML") { encodedHtml ->
                if (currentWebView !== webView || currentScreen != "login") return@evaluateJavascript
                val html = runCatching {
                    JSONTokener(encodedHtml).nextValue() as String
                }.getOrDefault("")
                val balance = parseBalance(html)
                if (balance != null) {
                    cookieHeader = CookieManager.getInstance().getCookie(baseUrl).orEmpty()
                    lastDashboardHtml = html
                    showDashboard(balance)
                } else if (attempt < 20) {
                    scheduleDashboardPoll(webView, attempt + 1)
                }
            }
        }, if (attempt == 0) 180 else 180)
    }

    private fun sanitizePortalUrl(uri: Uri): String {
        if (uri.host != "new.plusx.tv") return uri.toString()

        val builder = uri.buildUpon()
            .scheme("https")
            .authority("new.plusx.tv")
            .clearQuery()

        if (uri.path.orEmpty() == "/") {
            builder.path("/login.php")
        }

        return builder.build().toString()
    }

    private fun injectMobileLoginCss(webView: WebView) {
        val js = """
            (function() {
              var meta = document.querySelector('meta[name="viewport"]');
              if (!meta) {
                meta = document.createElement('meta');
                meta.name = 'viewport';
                document.head.appendChild(meta);
              }
              meta.content = 'width=device-width, initial-scale=1, maximum-scale=1';
              var style = document.getElementById('plusx-mobile-style');
              if (!style) {
                style = document.createElement('style');
                style.id = 'plusx-mobile-style';
                style.innerHTML =
                  'html,body{width:100%!important;min-width:0!important;overflow-x:hidden!important;}' +
                  '.login-page,.fp-page,.signup-page{padding:32px!important;}' +
                  '.login-box,.card,.login-page .card{width:100%!important;max-width:360px!important;margin:0 auto!important;}' +
                  '.body{padding:24px 20px!important;}' +
                  'input{font-size:16px!important;}' +
                  '.g-recaptcha{transform:scale(.94);transform-origin:0 0;margin-bottom:8px!important;}' +
                  'button,input[type=submit],.btn{min-height:44px!important;font-size:14px!important;}';
                document.head.appendChild(style);
              }
              if (!window.plusxLoginOverlayBound) {
                window.plusxLoginOverlayBound = true;
                document.addEventListener('submit', function() {
                  var overlay = document.getElementById('plusx-login-overlay');
                  if (!overlay) {
                    overlay = document.createElement('div');
                    overlay.id = 'plusx-login-overlay';
                    overlay.innerHTML = 'Logowanie...';
                    overlay.style.cssText = 'position:fixed;inset:0;z-index:2147483647;background:rgba(15,23,42,.82);color:#fff;display:flex;align-items:center;justify-content:center;font:600 22px sans-serif;';
                    document.body.appendChild(overlay);
                  }
                  overlay.style.display = 'flex';
                }, true);
              }
            })();
        """.trimIndent()
        webView.evaluateJavascript(js, null)
    }

    private fun extractDashboardFromWebView(webView: WebView, attempt: Int = 0) {
        webView.evaluateJavascript("document.documentElement.outerHTML") { encodedHtml ->
            if (currentWebView !== webView) return@evaluateJavascript
            val html = runCatching {
                JSONTokener(encodedHtml).nextValue() as String
            }.getOrDefault("")

            val balance = parseBalance(html)
            if (balance == null) {
                if (attempt < 6 && html.contains("DASHBOARD", ignoreCase = true)) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        extractDashboardFromWebView(webView, attempt + 1)
                    }, 150)
                    return@evaluateJavascript
                }
                val currentUrl = webView.url.orEmpty()
                if (triedIndexFirst && !currentUrl.endsWith("/login.php")) {
                    triedIndexFirst = false
                    webView.loadUrl(loginUrl)
                }
                hideLoginTransitionOverlay()
                return@evaluateJavascript
            }

            cookieHeader = CookieManager.getInstance().getCookie(baseUrl).orEmpty()
            lastDashboardHtml = html
            showDashboard(balance)
        }
    }

    private fun showLoginTransitionOverlay() {
        if (loginTransitionOverlay != null) return
        val overlay = LinearLayout(this)
        overlay.orientation = LinearLayout.VERTICAL
        overlay.gravity = Gravity.CENTER
        overlay.setBackgroundColor(Color.rgb(15, 23, 42))

        val title = TextView(this)
        title.text = "Logowanie..."
        title.textSize = 22f
        title.setTypeface(null, 1)
        title.setTextColor(Color.WHITE)
        title.gravity = Gravity.CENTER
        overlay.addView(title)

        val subtitle = TextView(this)
        subtitle.text = "Ladowanie panelu PlusX Mobile"
        subtitle.textSize = 14f
        subtitle.setTextColor(Color.rgb(148, 163, 184))
        subtitle.setPadding(0, dp(8), 0, 0)
        subtitle.gravity = Gravity.CENTER
        overlay.addView(subtitle)

        loginTransitionOverlay = overlay
        root.addView(overlay, FrameLayout.LayoutParams(-1, -1))
    }

    private fun hideLoginTransitionOverlay() {
        loginTransitionOverlay?.let { root.removeView(it) }
        loginTransitionOverlay = null
    }

    private fun showDashboard(balance: String = "Ladowanie...") {
        root.removeAllViews()
        root.setBackgroundColor(bgColor())
        currentScreen = "dashboard"
        currentWebView = null

        val scroll = ScrollView(this)
        scroll.setBackgroundColor(bgColor())
        val panel = LinearLayout(this)
        panel.orientation = LinearLayout.VERTICAL
        panel.setPadding(dp(18), dp(24), dp(18), dp(26))
        panel.setBackgroundColor(bgColor())
        scroll.addView(panel)

        val header = LinearLayout(this)
        header.orientation = LinearLayout.HORIZONTAL
        header.gravity = Gravity.CENTER_VERTICAL
        header.layoutParams = LinearLayout.LayoutParams(-1, dp(50))

        val title = TextView(this)
        title.text = "PlusX Mobile"
        title.textSize = 28f
        title.setTextColor(textColor())
        title.setTypeface(null, 1)
        title.setSingleLine(true)
        title.maxLines = 1
        title.ellipsize = TextUtils.TruncateAt.END
        title.gravity = Gravity.CENTER_VERTICAL
        title.includeFontPadding = false
        header.addView(title, LinearLayout.LayoutParams(0, -2, 1f))

        val logo = ImageView(this)
        logo.setImageResource(R.drawable.plusx_logo_cropped)
        logo.scaleType = ImageView.ScaleType.FIT_CENTER
        logo.adjustViewBounds = true
        logo.alpha = 0.96f
        header.addView(logo, LinearLayout.LayoutParams(dp(124), dp(44)).apply { setMargins(dp(12), 0, 0, 0) })
        panel.addView(header)

        val subtitle = TextView(this)
        subtitle.text = "Panel Klienta / Resellera"
        subtitle.textSize = 14f
        subtitle.setTextColor(mutedColor())
        subtitle.setPadding(0, 0, 0, dp(18))
        panel.addView(subtitle)

        panel.addView(balanceCard(balance))
        panel.addView(actionButton("Odswiez balans", true) { fetchDashboard(forceRefresh = true) })
        panel.addView(actionButton("Wiadomosci") { loadNewsPage() })
        panel.addView(actionButton("Programy na dziś") { loadEpgPage() })
        panel.addView(actionButton("Kup pakiet") { loadNativePage("packets.php", "Pakiety") { parsePackets(it) } })
        panel.addView(actionButton("Doladuj konto") { showPaymentChoice() })
        panel.addView(actionButton("Historia zakupow") { loadNativePage("balance_history.php?page=1&items_pp=1000&exp_filter=&sort=", "Historia zakupow") { parseBillingHistory(it) } })
        panel.addView(actionButton("Reseller Panel") { loadResellerPage() })
        panel.addView(actionButton("Linki M3U") { loadNativePage("tuner_settings.php", "Linki M3U") { parseM3u(it) } })
        panel.addView(actionButton("Ustawienia") { loadNativePage("profile.php", "Ustawienia") { parseProfile(it) } })
        panel.addView(actionButton("Wyloguj", danger = true) {
            CookieManager.getInstance().removeAllCookies {
                cookieHeader = ""
                lastDashboardHtml = ""
                showLogin()
            }
        })
        panel.addView(copyrightFooter())

        root.addView(scroll)
        animateScreen(scroll)
        rememberDashboardAndMaybeShowSupport()
    }

    private fun balanceCard(balance: String): View {
        val card = LinearLayout(this)
        card.orientation = LinearLayout.VERTICAL
        card.setPadding(dp(20), dp(18), dp(20), dp(20))
        card.background = rounded(if (darkMode) cardColor() else Color.WHITE, 22, strokeColor())
        card.elevation = if (darkMode) 0f else dp(2).toFloat()

        val label = TextView(this)
        label.text = "Saldo"
        label.textSize = 14f
        label.setTextColor(textColor())
        card.addView(label)

        val value = TextView(this)
        value.text = balance
        value.textSize = 32f
        value.setTypeface(null, 1)
        value.setTextColor(textColor())
        value.setPadding(0, dp(6), 0, 0)
        card.addView(value)

        val params = LinearLayout.LayoutParams(-1, -2)
        params.setMargins(0, 0, 0, dp(18))
        card.layoutParams = params
        return card
    }

    private fun actionButton(text: String, primary: Boolean = false, danger: Boolean = false, onClick: () -> Unit): Button {
        val button = Button(this)
        button.text = text
        button.textSize = 15f
        button.setAllCaps(false)
        button.setTextColor(if (primary || danger) Color.WHITE else textColor())
        button.background = rounded(
            when {
                danger -> Color.rgb(220, 38, 38)
                primary -> Color.rgb(14, 165, 180)
                else -> cardColor()
            },
            16,
            if (primary || danger) 0 else strokeColor()
        )
        button.setOnClickListener {
            it.animate().scaleX(0.97f).scaleY(0.97f).setDuration(70).withEndAction {
                it.animate().scaleX(1f).scaleY(1f).setDuration(90).start()
                onClick()
            }.start()
        }
        val params = LinearLayout.LayoutParams(-1, dp(52))
        params.setMargins(0, 0, 0, dp(12))
        button.layoutParams = params
        return button
    }

    private fun compactButton(text: String, primary: Boolean = false, onClick: () -> Unit): Button {
        val button = actionButton(text, primary, false, onClick)
        button.textSize = 14f
        button.isAllCaps = false
        button.layoutParams = LinearLayout.LayoutParams(0, dp(58), 1f).apply {
            setMargins(0, dp(10), dp(8), 0)
        }
        return button
    }

    private fun smallBackButton(): Button {
        val button = actionButton("< Wroc", primary = false) { backToDashboard() }
        button.textSize = 13f
        button.layoutParams = LinearLayout.LayoutParams(-2, dp(42)).apply {
            setMargins(0, 0, 0, dp(14))
        }
        return button
    }

    private fun backToDashboard() {
        showLoadingPage("PlusX Mobile", "Proszę czekać...")
        Handler(Looper.getMainLooper()).postDelayed({
            fetchDashboard()
        }, 420)
    }

    private fun themeSwitchRow(): View {
        val row = LinearLayout(this)
        row.orientation = LinearLayout.HORIZONTAL
        row.gravity = Gravity.CENTER_VERTICAL
        row.setPadding(dp(14), dp(12), dp(14), dp(12))
        row.background = rounded(cardColor(), 18, strokeColor())

        val label = TextView(this)
        label.text = if (darkMode) "Tryb ciemny" else "Tryb jasny"
        label.textSize = 15f
        label.setTypeface(null, 1)
        label.setTextColor(if (darkMode) Color.WHITE else Color.rgb(16, 24, 40))
        row.addView(label, LinearLayout.LayoutParams(0, -2, 1f))

        val button = compactButton("Zmien", primary = true) {
            darkMode = !darkMode
            root.setBackgroundColor(bgColor())
            Toast.makeText(this, if (darkMode) "Tryb ciemny wlaczony" else "Tryb jasny wlaczony", Toast.LENGTH_SHORT).show()
            showNativePage("Ustawienia", "profile.php", settingsItems())
        }
        row.addView(button, LinearLayout.LayoutParams(dp(118), dp(46)))
        row.layoutParams = LinearLayout.LayoutParams(-1, -2).apply {
            setMargins(0, 0, 0, dp(10))
        }
        return row
    }

    private fun rounded(color: Int, radius: Int, strokeColor: Int = 0): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radius).toFloat()
            if (strokeColor != 0) setStroke(dp(1), strokeColor)
        }
    }

    private fun showModernMessage(
        titleText: String,
        messageText: String,
        positiveText: String = "OK",
        negativeText: String? = null,
        contentView: View? = null,
        dismissOnPositive: Boolean = true,
        onPositive: (() -> Unit)? = null,
        onNegative: (() -> Unit)? = null
    ): Dialog {
        val dialog = Dialog(this)
        val card = LinearLayout(this)
        card.orientation = LinearLayout.VERTICAL
        card.setPadding(dp(22), dp(22), dp(22), dp(26))
        card.background = rounded(cardColor(), 24, strokeColor())

        val icon = TextView(this)
        icon.text = "!"
        icon.gravity = Gravity.CENTER
        icon.textSize = 22f
        icon.setTypeface(null, 1)
        icon.setTextColor(Color.WHITE)
        icon.background = rounded(Color.rgb(14, 165, 180), 16)
        card.addView(icon, LinearLayout.LayoutParams(dp(52), dp(52)).apply { gravity = Gravity.CENTER_HORIZONTAL })

        val title = TextView(this)
        title.text = titleText
        title.textSize = 22f
        title.setTypeface(null, 1)
        title.gravity = Gravity.CENTER
        title.setTextColor(textColor())
        card.addView(title, LinearLayout.LayoutParams(-1, -2).apply { setMargins(0, dp(16), 0, 0) })

        if (messageText.isNotBlank()) {
            val body = TextView(this)
            body.text = messageText
            body.textSize = 15f
            body.gravity = Gravity.CENTER
            body.setLineSpacing(0f, 1.08f)
            body.setTextColor(if (darkMode) Color.rgb(203, 213, 225) else Color.rgb(52, 64, 84))
            card.addView(body, LinearLayout.LayoutParams(-1, -2).apply { setMargins(0, dp(12), 0, 0) })
        }

        contentView?.let {
            card.addView(it, LinearLayout.LayoutParams(-1, -2).apply { setMargins(0, dp(14), 0, 0) })
        }

        val row = LinearLayout(this)
        row.orientation = LinearLayout.HORIZONTAL
        row.gravity = Gravity.CENTER

        negativeText?.let {
            val negative = dialogButton(it, primary = false) {
                dialog.dismiss()
                onNegative?.invoke()
            }
            row.addView(negative)
        }

        val positive = dialogButton(positiveText, primary = true) {
            if (dismissOnPositive) dialog.dismiss()
            onPositive?.invoke()
        }
        row.addView(positive)

        card.addView(row, LinearLayout.LayoutParams(-1, -2).apply { setMargins(0, dp(12), 0, 0) })

        dialog.setContentView(card)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dialogWidth = (resources.displayMetrics.widthPixels - dp(64)).coerceAtMost(dp(400))
        dialog.window?.setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.show()
        dialog.window?.setLayout(dialogWidth, ViewGroup.LayoutParams.WRAP_CONTENT)
        card.alpha = 0f
        card.scaleX = 0.96f
        card.scaleY = 0.96f
        card.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(180).start()
        return dialog
    }

    private fun dialogButton(text: String, primary: Boolean = false, onClick: () -> Unit): Button {
        val button = Button(this)
        button.text = text
        button.textSize = 14f
        button.setAllCaps(false)
        button.minHeight = 0
        button.minimumHeight = 0
        button.includeFontPadding = false
        button.setPadding(dp(10), 0, dp(10), 0)
        button.setTextColor(if (primary) Color.WHITE else textColor())
        button.background = rounded(
            if (primary) Color.rgb(14, 165, 180) else cardColor(),
            14,
            if (primary) 0 else strokeColor()
        )
        button.setOnClickListener {
            it.animate().scaleX(0.97f).scaleY(0.97f).setDuration(70).withEndAction {
                it.animate().scaleX(1f).scaleY(1f).setDuration(90).start()
                onClick()
            }.start()
        }
        button.layoutParams = LinearLayout.LayoutParams(0, dp(56), 1f).apply {
            setMargins(dp(5), dp(10), dp(5), 0)
        }
        return button
    }

    private fun showModernOptions(titleText: String, options: List<String>, onSelected: (Int) -> Unit) {
        val list = LinearLayout(this)
        list.orientation = LinearLayout.VERTICAL
        options.forEachIndexed { index, label ->
            val row = TextView(this)
            row.text = label
            row.textSize = 15f
            row.setTextColor(textColor())
            row.setPadding(dp(14), dp(12), dp(14), dp(12))
            row.background = rounded(if (darkMode) Color.rgb(15, 23, 42) else Color.rgb(245, 247, 251), 14, strokeColor())
            row.setOnClickListener {
                (list.tag as? Dialog)?.dismiss()
                onSelected(index)
            }
            list.addView(row, LinearLayout.LayoutParams(-1, -2).apply { setMargins(0, dp(8), 0, 0) })
        }
        val dialog = showModernMessage(titleText, "", positiveText = "Anuluj", contentView = list)
        list.tag = dialog
    }

    private fun styleDialogInput(input: EditText) {
        input.setTextColor(textColor())
        input.setHintTextColor(mutedColor())
        input.setPadding(dp(14), dp(10), dp(14), dp(10))
        input.background = rounded(if (darkMode) Color.rgb(15, 23, 42) else Color.rgb(245, 247, 251), 14, strokeColor())
    }

    private fun bgColor(): Int {
        return if (darkMode) Color.rgb(15, 23, 42) else Color.rgb(245, 247, 251)
    }

    private fun cardColor(): Int {
        return if (darkMode) Color.rgb(30, 41, 59) else Color.WHITE
    }

    private fun textColor(): Int {
        return if (darkMode) Color.rgb(241, 245, 249) else Color.rgb(16, 24, 40)
    }

    private fun mutedColor(): Int {
        return if (darkMode) Color.rgb(148, 163, 184) else Color.rgb(102, 112, 133)
    }

    private fun strokeColor(): Int {
        return if (darkMode) Color.rgb(51, 65, 85) else Color.rgb(229, 231, 235)
    }

    private fun fetchDashboard(forceRefresh: Boolean = false) {
        if (!forceRefresh) {
            parseBalance(lastDashboardHtml)?.let {
                showDashboard(it)
                return
            }
        }

        Thread {
            val result = runCatching {
                val html = getHtml("$baseUrl/index.php")
                lastDashboardHtml = html
                parseBalance(html)
            }.getOrNull()

            runOnUiThread {
                if (result == null) {
                    handleSessionTimeout()
                } else {
                    showDashboard(result)
                }
            }
        }.start()
    }

    private fun handleSessionTimeout() {
        Toast.makeText(this, "Zostales wylogowany (timeout)", Toast.LENGTH_LONG).show()
        Handler(Looper.getMainLooper()).postDelayed({
            CookieManager.getInstance().removeAllCookies {
                cookieHeader = ""
                lastDashboardHtml = ""
                diagnosticsSnapshots.clear()
                triedIndexFirst = false
                showLogin()
            }
        }, 900)
    }

    private fun loadNativePage(path: String, title: String, parser: (String) -> List<PageItem>) {
        if (path.startsWith("packets.php")) {
            currentPacketsSelectedUser = Uri.parse("$baseUrl/$path").getQueryParameter("selected_user")
                ?.let { URLDecoder.decode(it, "UTF-8") }
                .orEmpty()
        }
        showLoadingPage(title)
        Thread {
            val htmlResult = runCatching { getHtml("$baseUrl/$path") }
            val items = htmlResult.map(parser).getOrElse {
                listOf(PageItem("Nie udalo sie pobrac strony", it.message.orEmpty()))
            }
            runOnUiThread {
                showNativePage(title, path, items)
            }
        }.start()
    }

    private fun showPaymentChoice() {
        root.removeAllViews()
        root.setBackgroundColor(bgColor())
        currentScreen = "native"
        currentWebView = null
        val (scroll, panel) = basePanel("Doladowanie", "Platnosc zostanie zrealizowana przez zewnetrznego operatora.")
        panel.addView(smallBackButton())
        panel.addView(itemCard(PageItem("Kryptowaluty", "Oryginalny formularz Cryptomus z portalu.", actionUrl = "app://payment/cryptomus", actionLabel = "Otworz")))
        panel.addView(itemCard(PageItem("Karta / Apple Pay / Google Pay", "Oryginalny formularz operatora z portalu.", actionUrl = "app://payment/monobank", actionLabel = "Otworz")))
        panel.addView(copyrightFooter())
        root.addView(scroll)
        animateScreen(scroll)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun openPaymentWebView(method: String) {
        root.removeAllViews()
        currentScreen = "web"
        currentWebView = null

        val container = LinearLayout(this)
        container.orientation = LinearLayout.VERTICAL
        container.setBackgroundColor(bgColor())

        val back = smallBackButton()
        back.setOnClickListener { showPaymentChoice() }
        container.addView(back)

        val loading = TextView(this)
        loading.text = "Ladowanie formularza platnosci..."
        loading.textSize = 14f
        loading.setTextColor(mutedColor())
        loading.setPadding(dp(16), dp(8), dp(16), dp(8))
        container.addView(loading)

        val webView = WebView(this)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.loadsImagesAutomatically = true
        webView.settings.javaScriptCanOpenWindowsAutomatically = true
        webView.settings.setSupportMultipleWindows(true)
        webView.webChromeClient = object : WebChromeClient() {
            override fun onCreateWindow(view: WebView, isDialog: Boolean, isUserGesture: Boolean, resultMsg: android.os.Message): Boolean {
                val transport = resultMsg.obj as WebViewTransport
                transport.webView = view
                resultMsg.sendToTarget()
                return true
            }
        }
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                loading.visibility = View.GONE
                injectPaymentCss(view, method)
            }
        }
        currentWebView = webView
        container.addView(webView, LinearLayout.LayoutParams(-1, 0, 1f))
        root.addView(container)
        webView.loadUrl("$baseUrl/balance.php", mapOf("Cookie" to cookieHeader))
    }

    private fun injectPaymentCss(webView: WebView, method: String) {
        val visible = if (method == "cryptomus") "cryptomus" else "monobank"
        val hidden = if (method == "cryptomus") "monobank" else "cryptomus"
        val js = """
            (function() {
              var style = document.getElementById('plusx-payment-style');
              if (!style) {
                style = document.createElement('style');
                style.id = 'plusx-payment-style';
                style.innerHTML = 'body{margin:0!important;padding:16px!important;overflow-x:hidden!important;background:#f8fafc!important;}' +
                  '.sidebar,.navbar,nav,aside,.info-box,.dashboard,.page-loader-wrapper,.search-bar{display:none!important;}' +
                  '#$hidden{display:none!important;}' +
                  '#$visible{display:block!important;opacity:1!important;visibility:visible!important;max-width:100%!important;width:100%!important;}' +
                  '.fade{opacity:1!important}.tab-pane{display:none!important}.tab-pane.active,#$visible.active{display:block!important;}' +
                  'form, input, button, select{max-width:100%!important;font-size:16px!important;} button,.btn,input[type=submit]{min-height:44px!important;}';
                document.head.appendChild(style);
              }
              var selected = document.getElementById('$visible');
              if (selected) {
                selected.classList.remove('fade');
                selected.classList.add('active');
                selected.classList.add('in');
              }
              document.querySelectorAll('form').forEach(function(form) { form.target = '_self'; });
            })();
        """.trimIndent()
        webView.evaluateJavascript(js, null)
    }

    private fun loadResellerPage() {
        showLoadingPage("Reseller", "Pobieranie listy kont...\nDuza lista moze ladowac sie dluzej.")
        Thread {
            val items = runCatching {
                val accounts = ResellerPaginator.fetchAll(::getHtml).accounts
                resellerAccountActivity.clear()
                resellerAccountDetails.clear()
                accounts.forEach { account ->
                    resellerAccountActivity[accountStatusKey(account.login)] = account.hasActivePackage
                    resellerAccountDetails[accountStatusKey(account.login)] = account
                }
                accounts.map(::resellerAccountItem)
            }.getOrElse {
                listOf(
                    PageItem(
                        "Nie udalo sie pobrac listy kont.",
                        "Sprobuj ponownie albo otworz wersje strony.",
                        actionUrl = "app://reseller/retry",
                        actionLabel = "Sprobuj ponownie",
                        secondActionUrl = ResellerHtmlParser.RESELLER_URL,
                        secondActionLabel = "Otworz strone"
                    )
                )
            }

            runOnUiThread {
                showNativePage("Reseller", "dealer.php", items)
            }
        }.start()
    }

    private fun showLoadingPage(title: String, subtitle: String = "Proszę czekać...") {
        root.removeAllViews()
        root.setBackgroundColor(bgColor())
        currentScreen = "native"
        currentWebView = null
        val container = LinearLayout(this)
        container.orientation = LinearLayout.VERTICAL
        container.gravity = Gravity.CENTER
        container.setPadding(dp(24), dp(24), dp(24), dp(24))
        container.setBackgroundColor(bgColor())

        val card = LinearLayout(this)
        card.orientation = LinearLayout.VERTICAL
        card.gravity = Gravity.CENTER
        card.setPadding(dp(24), dp(24), dp(24), dp(24))
        card.background = rounded(cardColor(), 22, strokeColor())

        val progress = ProgressBar(this)
        progress.isIndeterminate = true
        card.addView(progress, LinearLayout.LayoutParams(dp(42), dp(42)).apply { setMargins(0, 0, 0, dp(12)) })

        val titleView = TextView(this)
        titleView.text = title
        titleView.textSize = 22f
        titleView.setTypeface(null, 1)
        titleView.setTextColor(textColor())
        titleView.gravity = Gravity.CENTER
        card.addView(titleView)

        val subtitleView = TextView(this)
        subtitleView.text = subtitle
        subtitleView.textSize = 14f
        subtitleView.setTextColor(mutedColor())
        subtitleView.gravity = Gravity.CENTER
        subtitleView.setPadding(0, dp(10), 0, 0)
        card.addView(subtitleView)

        container.addView(card, LinearLayout.LayoutParams(-1, -2))
        root.addView(container, FrameLayout.LayoutParams(-1, -1))
        card.alpha = 0f
        card.scaleX = 0.96f
        card.scaleY = 0.96f
        card.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(180).start()
    }

    private fun showNativePage(title: String, path: String, items: List<PageItem>) {
        root.removeAllViews()
        root.setBackgroundColor(bgColor())
        currentScreen = "native"
        currentWebView = null
        val (scroll, panel) = basePanel(title, "")

        if (title == "Ustawienia") {
            panel.addView(themeSwitchRow())
        }
        panel.addView(smallBackButton())

        if (title == "Linki M3U") {
            m3uPanel = panel
            currentM3uItems = items
            renderM3uItems(panel, items, "")
            root.addView(scroll)
            animateScreen(scroll)
            return
        }

        if (title == "Pakiety") {
            packetsPanel = panel
            currentPacketsItems = items
            renderPacketsItems(panel, items, "")
            root.addView(scroll)
            animateScreen(scroll)
            return
        }

        if (title == "Historia zakupow") {
            billingPanel = panel
            renderBillingHistoryItems(panel, "")
            root.addView(scroll)
            animateScreen(scroll)
            return
        }

        if (items.isEmpty()) {
            panel.addView(infoCard("Brak danych", "Parser nie znalazl nic konkretnego na tej stronie.", ""))
        } else {
            items.forEach { item ->
                panel.addView(
                    when {
                        title == "Wiadomosci" || title == "Programy na dziś" -> newsCard(item)
                        title == "Ustawienia" -> itemCard(item, clickableCard = true)
                        else -> itemCard(item)
                    }
                )
            }
        }

        panel.addView(copyrightFooter())
        root.addView(scroll)
        animateScreen(scroll)
    }

    private fun animateScreen(view: View) {
        view.alpha = 0f
        view.translationY = dp(10).toFloat()
        view.animate().alpha(1f).translationY(0f).setDuration(180).start()
    }

    private fun basePanel(titleText: String, subtitleText: String): Pair<ScrollView, LinearLayout> {
        val scroll = ScrollView(this)
        scroll.setBackgroundColor(bgColor())
        val panel = LinearLayout(this)
        panel.orientation = LinearLayout.VERTICAL
        panel.setPadding(dp(18), dp(24), dp(18), dp(26))
        panel.setBackgroundColor(bgColor())
        scroll.addView(panel)

        val title = TextView(this)
        title.text = titleText
        title.textSize = 25f
        title.setTextColor(textColor())
        title.setTypeface(null, 1)
        panel.addView(title)

        if (subtitleText.isNotBlank()) {
            val subtitle = TextView(this)
            subtitle.text = subtitleText
            subtitle.textSize = 13f
            subtitle.setTextColor(mutedColor())
            subtitle.setPadding(0, dp(4), 0, dp(18))
            panel.addView(subtitle)
        } else {
            val spacer = View(this)
            panel.addView(spacer, LinearLayout.LayoutParams(-1, dp(18)))
        }

        return Pair(scroll, panel)
    }

    private fun copyrightFooter(): View {
        val wrap = LinearLayout(this)
        wrap.orientation = LinearLayout.VERTICAL
        wrap.gravity = Gravity.CENTER

        if (currentScreen == "dashboard") {
            val support = Button(this)
            support.text = "\u2615 Postaw mi kaw\u0119"
            support.textSize = 12f
            support.setAllCaps(false)
            support.setTextColor(if (darkMode) Color.rgb(125, 211, 252) else Color.rgb(14, 116, 144))
            support.background = rounded(Color.TRANSPARENT, 18, strokeColor())
            support.setOnClickListener { openSupportPage() }
            wrap.addView(support, LinearLayout.LayoutParams(-2, dp(38)).apply {
                setMargins(0, dp(4), 0, dp(8))
            })
        }

        val footer = TextView(this)
        footer.text = "\u00a9 Torvinek 2026"
        footer.textSize = 12f
        footer.gravity = Gravity.CENTER
        footer.setTextColor(mutedColor())
        footer.setPadding(0, dp(6), 0, dp(24))
        wrap.addView(footer, LinearLayout.LayoutParams(-1, -2))
        wrap.layoutParams = LinearLayout.LayoutParams(-1, -2)
        return wrap
    }

    private fun infoCard(title: String, subtitle: String, value: String): View {
        return itemCard(PageItem(title, subtitle, value))
    }

    private fun renderPacketsItems(panel: LinearLayout, items: List<PageItem>, loadingText: String) {
        while (panel.childCount > 3) {
            panel.removeViewAt(3)
        }
        panel.addView(packetUserCard(loadingText))
        if (items.isEmpty()) {
            panel.addView(infoCard("Brak pakietow", "Parser nie znalazl pakietow na stronie.", ""))
        } else {
            items.forEach { item -> panel.addView(itemCard(item)) }
        }
        panel.addView(copyrightFooter())
    }

    private fun packetUserCard(loadingText: String): View {
        val selected = cleanSelectedUser(currentPacketsSelectedUser)
        val card = LinearLayout(this)
        card.orientation = LinearLayout.VERTICAL
        card.setPadding(dp(16), dp(16), dp(16), dp(16))
        card.background = rounded(cardColor(), 18, strokeColor())

        val title = TextView(this)
        title.text = "Wybrany uzytkownik"
        title.textSize = 17f
        title.setTypeface(null, 1)
        title.setTextColor(textColor())
        card.addView(title)

        val subtitle = TextView(this)
        subtitle.text = if (selected.isBlank()) "Konto glowne" else "Zaladowano dla: $selected"
        subtitle.textSize = 13f
        subtitle.setTextColor(mutedColor())
        subtitle.setPadding(0, dp(4), 0, dp(10))
        card.addView(subtitle)

        val dropdown = TextView(this)
        dropdown.text = if (selected.isBlank()) "Konto glowne" else selected
        dropdown.textSize = 15f
        dropdown.setTextColor(textColor())
        dropdown.setPadding(dp(14), dp(12), dp(14), dp(12))
        dropdown.background = rounded(if (darkMode) Color.rgb(31, 41, 55) else Color.rgb(248, 250, 252), 14, strokeColor())
        dropdown.setOnClickListener { showPacketsUserDropdown() }
        card.addView(dropdown, LinearLayout.LayoutParams(-1, -2).apply { setMargins(0, 0, 0, dp(10)) })

        val count = TextView(this)
        count.text = "Dostepnych uzytkownikow: ${currentPacketsUsers.size}"
        count.textSize = 14f
        count.setTextColor(textColor())
        card.addView(count)

        if (loadingText.isNotBlank()) {
            val loading = TextView(this)
            loading.text = loadingText
            loading.textSize = 13f
            loading.setTextColor(Color.rgb(14, 165, 180))
            loading.setPadding(0, dp(10), 0, 0)
            card.addView(loading)
        }

        card.layoutParams = LinearLayout.LayoutParams(-1, -2).apply { setMargins(0, 0, 0, dp(14)) }
        return card
    }

    private fun renderBillingHistoryItems(panel: LinearLayout, loadingText: String) {
        while (panel.childCount > 3) {
            panel.removeViewAt(3)
        }
        billingDetails.clear()
        panel.addView(billingUserCard(loadingText))
        val items = billingHistoryItemsForSelectedUser()
        if (items.isEmpty()) {
            panel.addView(infoCard("Brak wpisow", "Nie znaleziono historii dla wybranego uzytkownika.", ""))
        } else {
            items.forEach { panel.addView(itemCard(it)) }
        }
        panel.addView(copyrightFooter())
    }

    private fun billingUserCard(loadingText: String): View {
        val selected = cleanSelectedUser(currentBillingSelectedUser)
        val account = resellerAccountDetails[accountStatusKey(currentBillingSelectedUser)]
        val packageHint = billingPackageHints(currentBillingEntries)[accountStatusKey(currentBillingSelectedUser)].orEmpty()
        val card = LinearLayout(this)
        card.orientation = LinearLayout.VERTICAL
        card.setPadding(dp(16), dp(16), dp(16), dp(16))
        card.background = rounded(cardColor(), 18, strokeColor())

        val title = TextView(this)
        title.text = "Historia dla uzytkownika"
        title.textSize = 17f
        title.setTypeface(null, 1)
        title.setTextColor(textColor())
        card.addView(title)

        val subtitle = TextView(this)
        subtitle.text = if (selected.isBlank()) "Pokazujesz: wszyscy uzytkownicy" else "Pokazujesz: $selected"
        subtitle.textSize = 13f
        subtitle.setTextColor(mutedColor())
        subtitle.setPadding(0, dp(4), 0, dp(10))
        card.addView(subtitle)

        val dropdown = TextView(this)
        dropdown.text = if (selected.isBlank()) "Wszyscy" else selected
        dropdown.textSize = 15f
        dropdown.setTextColor(textColor())
        dropdown.setPadding(dp(14), dp(12), dp(14), dp(12))
        dropdown.background = rounded(if (darkMode) Color.rgb(31, 41, 55) else Color.rgb(248, 250, 252), 14, strokeColor())
        dropdown.setOnClickListener { showBillingUserDropdown() }
        card.addView(dropdown, LinearLayout.LayoutParams(-1, -2).apply { setMargins(0, 0, 0, dp(10)) })

        val info = TextView(this)
        info.text = if (account == null || selected.isBlank()) {
            "Wpisow w historii: ${currentBillingEntries.size}\nDostepnych uzytkownikow: ${currentBillingUsers.size}"
        } else {
            listOf(
                "Notatka: ${account.notice.ifBlank { "Bez notatki" }}",
                if (account.hasActivePackage) "Status: aktywny" else "Status: nieaktywny",
                if (account.expiry.isNotBlank()) "Wygasa: ${account.expiry} ${account.days}".trim() else "",
                if (packageHint.isNotBlank()) "Pakiet z historii: $packageHint" else "Pakiet z historii: brak pewnych danych"
            ).filter { it.isNotBlank() }.joinToString("\n")
        }
        info.textSize = 14f
        info.setTextColor(textColor())
        info.setLineSpacing(dp(2).toFloat(), 1f)
        card.addView(info)

        if (loadingText.isNotBlank()) {
            val loading = TextView(this)
            loading.text = loadingText
            loading.textSize = 13f
            loading.setTextColor(Color.rgb(14, 165, 180))
            loading.setPadding(0, dp(10), 0, 0)
            card.addView(loading)
        }

        card.layoutParams = LinearLayout.LayoutParams(-1, -2).apply { setMargins(0, 0, 0, dp(14)) }
        return card
    }

    private fun showBillingUserDropdown() {
        val users = listOf("") + currentBillingUsers.distinctBy { accountStatusKey(it) }
        if (users.size <= 1) {
            Toast.makeText(this, "Brak listy uzytkownikow", Toast.LENGTH_SHORT).show()
            return
        }
        val dialog = Dialog(this)
        val box = LinearLayout(this)
        box.orientation = LinearLayout.VERTICAL
        box.setPadding(dp(16), dp(16), dp(16), dp(16))
        box.background = rounded(cardColor(), 18, strokeColor())

        val title = TextView(this)
        title.text = "Wybierz uzytkownika"
        title.textSize = 18f
        title.setTypeface(null, 1)
        title.setTextColor(textColor())
        box.addView(title)

        val search = EditText(this)
        search.hint = "Szukaj"
        search.textSize = 15f
        search.setSingleLine(true)
        search.setTextColor(textColor())
        search.setHintTextColor(mutedColor())
        search.setPadding(dp(12), dp(10), dp(12), dp(10))
        search.background = rounded(if (darkMode) Color.rgb(31, 41, 55) else Color.WHITE, 12, strokeColor())
        box.addView(search, LinearLayout.LayoutParams(-1, -2).apply { setMargins(0, dp(12), 0, dp(12)) })

        val rows = users.map { it to (if (it.isBlank()) "Wszyscy" else cleanSelectedUser(it)) }.toMutableList()
        val allRows = rows.toList()
        val list = ListView(this)
        list.divider = null
        list.setBackgroundColor(Color.TRANSPARENT)
        val adapter = object : ArrayAdapter<Pair<String, String>>(this, android.R.layout.simple_list_item_1, rows) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val text = TextView(context)
                val row = rows[position]
                val selected = accountStatusKey(row.first) == accountStatusKey(currentBillingSelectedUser)
                text.text = row.second
                text.textSize = 15f
                text.setTextColor(if (selected) Color.rgb(14, 165, 180) else textColor())
                text.setTypeface(null, if (selected) 1 else 0)
                text.setPadding(dp(12), dp(12), dp(12), dp(12))
                text.background = rounded(if (selected) selectedDropdownColor() else cardColor(), 10, if (selected) Color.rgb(14, 165, 180) else 0)
                return text
            }
        }
        list.adapter = adapter
        list.setOnItemClickListener { _, _, position, _ ->
            currentBillingSelectedUser = rows[position].first
            dialog.dismiss()
            billingPanel?.let { renderBillingHistoryItems(it, "") }
        }
        box.addView(list, LinearLayout.LayoutParams(-1, dp(420)))

        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString().orEmpty().trim()
                rows.clear()
                rows.addAll(if (query.isBlank()) allRows else allRows.filter { it.second.contains(query, true) })
                adapter.notifyDataSetChanged()
            }
            override fun afterTextChanged(s: Editable?) = Unit
        })

        dialog.setContentView(box)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
        dialog.window?.setLayout((resources.displayMetrics.widthPixels * 0.92f).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun renderM3uItems(panel: LinearLayout, items: List<PageItem>, loadingText: String) {
        while (panel.childCount > 3) {
            panel.removeViewAt(3)
        }
        panel.addView(m3uUserCard(loadingText))
        items.drop(1).forEach { item ->
            panel.addView(itemCard(item))
        }
        panel.addView(copyrightFooter())
    }

    private fun m3uUserCard(loadingText: String): View {
        val card = LinearLayout(this)
        card.orientation = LinearLayout.VERTICAL
        card.setPadding(dp(16), dp(16), dp(16), dp(16))
        card.background = rounded(cardColor(), 18, strokeColor())

        val title = TextView(this)
        title.text = "Wybrany uzytkownik"
        title.textSize = 17f
        title.setTypeface(null, 1)
        title.setTextColor(textColor())
        card.addView(title)

        val subtitle = TextView(this)
        subtitle.text = if (currentM3uSelectedUser.isBlank()) "Nie wykryto wybranego uzytkownika" else "Zaladowano dla: $currentM3uSelectedUser"
        subtitle.textSize = 13f
        subtitle.setTextColor(mutedColor())
        subtitle.setPadding(0, dp(4), 0, dp(10))
        card.addView(subtitle)

        val dropdown = TextView(this)
        dropdown.text = if (currentM3uSelectedUser.isBlank()) "Wybierz uzytkownika" else currentM3uSelectedUser
        dropdown.textSize = 15f
        dropdown.setTextColor(textColor())
        dropdown.setPadding(dp(14), dp(12), dp(14), dp(12))
        dropdown.background = rounded(if (darkMode) Color.rgb(31, 41, 55) else Color.rgb(248, 250, 252), 14, strokeColor())
        dropdown.setOnClickListener { showM3uUserDropdown() }
        card.addView(dropdown, LinearLayout.LayoutParams(-1, -2).apply { setMargins(0, 0, 0, dp(10)) })

        val count = TextView(this)
        count.text = "Dostepnych uzytkownikow: ${currentM3uUsers.size}"
        count.textSize = 14f
        count.setTextColor(textColor())
        card.addView(count)

        if (loadingText.isNotBlank()) {
            val loading = TextView(this)
            loading.text = loadingText
            loading.textSize = 13f
            loading.setTextColor(Color.rgb(14, 165, 180))
            loading.setPadding(0, dp(10), 0, 0)
            card.addView(loading)
        }

        card.layoutParams = LinearLayout.LayoutParams(-1, -2).apply { setMargins(0, 0, 0, dp(14)) }
        return card
    }

    private fun showM3uUserDropdown() {
        val rawUsers = currentM3uUsers.distinct()
        if (rawUsers.isEmpty()) {
            Toast.makeText(this, "Brak listy uzytkownikow", Toast.LENGTH_SHORT).show()
            return
        }
        val dialog = Dialog(this)
        val box = LinearLayout(this)
        box.orientation = LinearLayout.VERTICAL
        box.setPadding(dp(16), dp(16), dp(16), dp(16))
        box.background = rounded(cardColor(), 18, strokeColor())

        val title = TextView(this)
        title.text = "Wybierz uzytkownika"
        title.textSize = 18f
        title.setTypeface(null, 1)
        title.setTextColor(textColor())
        box.addView(title)

        val search = EditText(this)
        search.hint = "Szukaj"
        search.textSize = 15f
        search.setSingleLine(true)
        search.setTextColor(textColor())
        search.setHintTextColor(mutedColor())
        search.setPadding(dp(12), dp(10), dp(12), dp(10))
        search.background = rounded(if (darkMode) Color.rgb(31, 41, 55) else Color.WHITE, 12, strokeColor())
        box.addView(search, LinearLayout.LayoutParams(-1, dp(48)).apply { setMargins(0, dp(12), 0, dp(10)) })

        val list = ListView(this)
        list.divider = null
        list.cacheColorHint = Color.TRANSPARENT
        list.setBackgroundColor(Color.TRANSPARENT)
        val allRows = rawUsers.map { it to cleanSelectedUser(it) }
        val rows = mutableListOf<Pair<String, String>>()
        rows.addAll(allRows)
        val adapter = object : ArrayAdapter<Pair<String, String>>(this, 0, rows) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val text = (convertView as? TextView) ?: TextView(context)
                val row = getItem(position)!!
                val selected = row.second == currentM3uSelectedUser || row.first == pendingM3uUser
                text.text = row.second
                text.textSize = 15f
                text.setTypeface(null, if (selected) 1 else 0)
                text.setTextColor(if (selected) Color.rgb(14, 165, 180) else textColor())
                text.setPadding(dp(12), dp(12), dp(12), dp(12))
                text.background = rounded(if (selected) selectedDropdownColor() else cardColor(), 10, if (selected) Color.rgb(14, 165, 180) else 0)
                return text
            }
        }
        list.adapter = adapter
        list.setOnItemClickListener { _, _, position, _ ->
            val raw = rows[position].first
            dialog.dismiss()
            changeM3uUser(raw)
        }
        box.addView(list, LinearLayout.LayoutParams(-1, dp(420)))

        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString().orEmpty().trim()
                rows.clear()
                rows.addAll(
                    if (query.isBlank()) allRows
                    else allRows.filter { it.first.contains(query, true) || it.second.contains(query, true) }
                )
                adapter.notifyDataSetChanged()
            }
            override fun afterTextChanged(s: Editable?) = Unit
        })

        dialog.setContentView(box)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
        dialog.window?.setLayout((resources.displayMetrics.widthPixels * 0.92f).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun showPacketsUserDropdown() {
        val rawUsers = currentPacketsUsers.distinct()
        if (rawUsers.isEmpty()) {
            Toast.makeText(this, "Brak listy uzytkownikow", Toast.LENGTH_SHORT).show()
            return
        }
        val dialog = Dialog(this)
        val box = LinearLayout(this)
        box.orientation = LinearLayout.VERTICAL
        box.setPadding(dp(16), dp(16), dp(16), dp(16))
        box.background = rounded(cardColor(), 18, strokeColor())

        val title = TextView(this)
        title.text = "Wybierz uzytkownika"
        title.textSize = 18f
        title.setTypeface(null, 1)
        title.setTextColor(textColor())
        box.addView(title)

        val search = EditText(this)
        search.hint = "Szukaj"
        search.textSize = 15f
        search.setSingleLine(true)
        search.setTextColor(textColor())
        search.setHintTextColor(mutedColor())
        search.setPadding(dp(12), dp(10), dp(12), dp(10))
        search.background = rounded(if (darkMode) Color.rgb(31, 41, 55) else Color.WHITE, 12, strokeColor())
        box.addView(search, LinearLayout.LayoutParams(-1, dp(48)).apply { setMargins(0, dp(12), 0, dp(10)) })

        val list = ListView(this)
        list.divider = null
        list.cacheColorHint = Color.TRANSPARENT
        list.setBackgroundColor(Color.TRANSPARENT)
        val allRows = rawUsers.map { it to cleanSelectedUser(it) }
        val rows = mutableListOf<Pair<String, String>>()
        rows.addAll(allRows)
        val adapter = object : ArrayAdapter<Pair<String, String>>(this, 0, rows) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val text = (convertView as? TextView) ?: TextView(context)
                val row = getItem(position)!!
                val selected = row.second == cleanSelectedUser(currentPacketsSelectedUser) || row.first == currentPacketsSelectedUser
                text.text = row.second.ifBlank { "Konto glowne" }
                text.textSize = 15f
                text.setTypeface(null, if (selected) 1 else 0)
                text.setTextColor(if (selected) Color.rgb(14, 165, 180) else textColor())
                text.setPadding(dp(12), dp(12), dp(12), dp(12))
                text.background = rounded(if (selected) selectedDropdownColor() else cardColor(), 10, if (selected) Color.rgb(14, 165, 180) else 0)
                return text
            }
        }
        list.adapter = adapter
        list.setOnItemClickListener { _, _, position, _ ->
            val raw = rows[position].first
            dialog.dismiss()
            changePacketsUser(raw)
        }
        box.addView(list, LinearLayout.LayoutParams(-1, dp(420)))

        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString().orEmpty().trim()
                rows.clear()
                rows.addAll(
                    if (query.isBlank()) allRows
                    else allRows.filter { it.first.contains(query, true) || it.second.contains(query, true) }
                )
                adapter.notifyDataSetChanged()
            }
            override fun afterTextChanged(s: Editable?) = Unit
        })

        dialog.setContentView(box)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
        dialog.window?.setLayout((resources.displayMetrics.widthPixels * 0.92f).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun selectedDropdownColor(): Int {
        return if (darkMode) Color.rgb(15, 78, 88) else Color.rgb(224, 252, 255)
    }

    private fun changePacketsUser(rawLogin: String) {
        val display = cleanSelectedUser(rawLogin)
        currentPacketsSelectedUser = rawLogin
        currentPacketsActivePacket = ""
        packetsPanel?.let { renderPacketsItems(it, currentPacketsItems, "Ladowanie pakietow dla: $display") }
        Thread {
            val encoded = encodeUrlParam(rawLogin)
            val htmlResult = runCatching { getHtml("$baseUrl/packets.php?selected_user=$encoded") }
            val items = htmlResult.map { parsePackets(it) }.getOrElse {
                listOf(PageItem("Nie udalo sie pobrac pakietow", it.message.orEmpty()))
            }
            runOnUiThread {
                currentPacketsItems = items
                packetsPanel?.let { renderPacketsItems(it, items, "") }
            }
        }.start()
    }

    private fun changeM3uUser(rawLogin: String) {
        val display = cleanSelectedUser(rawLogin)
        pendingM3uUser = rawLogin
        currentM3uSelectedUser = display
        m3uPanel?.let { renderM3uItems(it, currentM3uItems, "Ladowanie linkow dla: $display") }
        Thread {
            val encoded = URLEncoder.encode(rawLogin, "UTF-8")
            val htmlResult = runCatching { getHtml("$baseUrl/tuner_settings.php?selected_user=$encoded") }
            val items = htmlResult.map { parseM3u(it) }.getOrElse {
                listOf(PageItem("Nie udalo sie pobrac linkow", it.message.orEmpty()))
            }
            runOnUiThread {
                currentM3uItems = items
                m3uPanel?.let { renderM3uItems(it, items, "") }
            }
        }.start()
    }

    private fun loadNewsPage() {
        showLoadingPage("Wiadomosci")
        Thread {
            val panelItems = runCatching {
                val html = getHtml("$baseUrl/index.php")
                lastDashboardHtml = html
                parseNews(html)
            }.getOrElse {
                listOf(PageItem("Wiadomosci z panelu", "Nie udalo sie pobrac panelu.", it.message.orEmpty()))
            }

            val telegramItems = runCatching {
                parseTelegramNews(getJson(telegramNewsUrl, telegramBackendToken))
            }.getOrElse {
                listOf(PageItem("Wiadomosci Telegram", "Nie udalo sie pobrac backendu.", it.message.orEmpty()))
            }

            val merged = mergeNewsItems(panelItems, telegramItems)
            runOnUiThread {
                showNativePage("Wiadomosci", "index.php", merged)
            }
        }.start()
    }

    private fun loadEpgPage() {
        showLoadingPage("Programy na dziś", "Pobieranie wydarzen z Telegrama...")
        Thread {
            val items = runCatching {
                val separator = if (telegramEpgUrl.contains("?")) "&" else "?"
                val url = "$telegramEpgUrl${separator}refresh=true&_=${System.currentTimeMillis()}"
                parseEpgEvents(getJson(url, telegramBackendToken))
            }.getOrElse {
                listOf(PageItem("Nie udalo sie pobrac programu", "Backend EPG Event", it.message.orEmpty(), badgeText = "EPG"))
            }
            runOnUiThread {
                showNativePage("Programy na dziś", "telegram/plusx/epgevent", items)
            }
        }.start()
    }

    private fun loadAboutPage(forceCheck: Boolean = false) {
        showLoadingPage("O aplikacji", "Pobieranie informacji o wersji...")
        Thread {
            val items = runCatching {
                val json = getJson(githubLatestReleaseUrl, "")
                parseAboutRelease(json, forceCheck)
            }.getOrElse {
                listOf(
                    PageItem("PlusX Mobile", "Wersja zainstalowana", appVersionLabel()),
                    PageItem("Aktualizacje", "Nie udalo sie sprawdzic GitHuba.", it.message.orEmpty(), actionUrl = "app://about", actionLabel = "Sprawdź aktualizacje")
                )
            }
            runOnUiThread {
                showNativePage("O aplikacji", "about", items)
            }
        }.start()
    }

    private fun parseAboutRelease(json: String, forceCheck: Boolean): List<PageItem> {
        val root = JSONObject(json)
        val tag = root.optString("tag_name").ifBlank { root.optString("name") }
        val releaseName = root.optString("name").ifBlank { tag.ifBlank { "Brak danych" } }
        val body = root.optString("body").ifBlank { "Brak opisu zmian w release." }
        val published = formatIsoDateForDisplay(root.optString("published_at"))
        val current = appVersionLabel()
        val updateAvailable = isRemoteVersionNewer(tag, current.substringBefore(" "))
        val apkUrl = releaseApkUrl(root)
        val updateInfo = if (updateAvailable) {
            "Dostępna nowa wersja: $tag"
        } else {
            "Masz aktualną wersję."
        }

        val checkSubtitle = if (forceCheck) "Sprawdzono przed chwilą." else "Informacje z GitHuba."
        val items = mutableListOf(
            PageItem("PlusX Mobile", "Wersja zainstalowana", current),
            PageItem(
                "Aktualizacje",
                checkSubtitle,
                updateInfo,
                actionUrl = "app://about/check",
                actionLabel = "Sprawdź aktualizacje",
                secondActionUrl = if (updateAvailable && apkUrl.isNotBlank()) {
                    "app://about/download?tag=${Uri.encode(tag)}&url=${Uri.encode(apkUrl)}"
                } else {
                    ""
                },
                secondActionLabel = "Pobierz aktualizację"
            ),
            PageItem("Najnowszy release", published.ifBlank { "GitHub" }, releaseName),
            PageItem("Co zostało dodane", "Opis zmian z GitHuba.", body.cleanMarkdownForApp())
        )
        return items
    }

    private fun checkStartupUpdatePrompt() {
        if (startupUpdateDialogShown) return
        Thread {
            val info = runCatching {
                val json = getJson(githubLatestReleaseUrl, "")
                parseStartupUpdateInfo(json)
            }.getOrNull() ?: return@Thread

            runOnUiThread {
                if (!isFinishing && !isDestroyed && !startupUpdateDialogShown) {
                    startupUpdateDialogShown = true
                    showStartupUpdateDialog(info)
                }
            }
        }.start()
    }

    private fun parseStartupUpdateInfo(json: String): StartupUpdateInfo? {
        val root = JSONObject(json)
        val tag = root.optString("tag_name").ifBlank { root.optString("name") }.trim()
        if (tag.isBlank()) return null
        if (!isRemoteVersionNewer(tag, appVersionName().substringBefore(" "))) return null
        val apkUrl = releaseApkUrl(root)
        if (apkUrl.isBlank()) return null
        val shaUrl = releaseSha256Url(root, apkUrl)
        val releaseName = root.optString("name").ifBlank { tag }
        val releaseBody = root.optString("body").cleanMarkdownForApp()
        return StartupUpdateInfo(tag, apkUrl, shaUrl, releaseName, releaseBody)
    }

    private fun showStartupUpdateDialog(info: StartupUpdateInfo) {
        val dialog = Dialog(this)
        val card = LinearLayout(this)
        card.orientation = LinearLayout.VERTICAL
        card.setPadding(dp(24), dp(22), dp(24), dp(20))
        card.background = rounded(cardColor(), 24, strokeColor())

        val icon = TextView(this)
        icon.text = "\u2b07"
        icon.textSize = 28f
        icon.gravity = Gravity.CENTER
        icon.setTextColor(Color.WHITE)
        icon.background = rounded(Color.rgb(14, 165, 180), 18, 0)
        card.addView(icon, LinearLayout.LayoutParams(dp(52), dp(52)).apply { gravity = Gravity.CENTER_HORIZONTAL })

        val title = TextView(this)
        title.text = "Dost\u0119pna aktualizacja"
        title.textSize = 21f
        title.setTypeface(null, 1)
        title.gravity = Gravity.CENTER
        title.setTextColor(textColor())
        title.setPadding(0, dp(14), 0, 0)
        card.addView(title)

        val chip = TextView(this)
        chip.text = "PlusX Mobile ${info.tag}"
        chip.textSize = 13f
        chip.gravity = Gravity.CENTER
        chip.setTextColor(Color.WHITE)
        chip.setPadding(dp(10), dp(5), dp(10), dp(5))
        chip.background = rounded(Color.rgb(14, 165, 180), 12, 0)
        card.addView(chip, LinearLayout.LayoutParams(-2, -2).apply {
            gravity = Gravity.CENTER_HORIZONTAL
            setMargins(0, dp(10), 0, dp(8))
        })

        val body = TextView(this)
        body.text = "Dost\u0119pna jest nowsza wersja aplikacji. Czy chcesz j\u0105 teraz pobra\u0107 i zainstalowa\u0107?"
        body.textSize = 14f
        body.gravity = Gravity.CENTER
        body.setTextColor(textColor())
        body.setLineSpacing(dp(2).toFloat(), 1f)
        card.addView(body)

        val changes = TextView(this)
        changes.text = info.releaseBody.ifBlank { "Brak opisu zmian." }
        changes.textSize = 13f
        changes.setTextColor(textColor())
        changes.setPadding(dp(12), dp(10), dp(12), dp(10))
        changes.background = rounded(if (darkMode) Color.rgb(15, 23, 42) else Color.rgb(241, 245, 249), 12, strokeColor())
        changes.visibility = View.GONE

        val progress = ProgressBar(this)
        progress.visibility = View.GONE

        val status = TextView(this)
        status.textSize = 13f
        status.gravity = Gravity.CENTER
        status.setTextColor(mutedColor())
        status.setPadding(0, dp(10), 0, 0)
        status.visibility = View.GONE

        val row = LinearLayout(this)
        row.orientation = LinearLayout.HORIZONTAL
        row.gravity = Gravity.END

        val later = compactButton("P\u00f3\u017aniej") { dialog.dismiss() }
        val whatsNew = compactButton("Co nowego?") {
            changes.visibility = if (changes.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        }
        lateinit var install: Button
        install = compactButton("Zainstaluj", primary = true) {
            install.isEnabled = false
            later.isEnabled = false
            whatsNew.isEnabled = false
            title.text = "Pobieranie aktualizacji"
            body.text = "Pobieranie PlusX Mobile ${info.tag}..."
            progress.visibility = View.VISIBLE
            status.visibility = View.VISIBLE
            status.text = "Prosz\u0119 czeka\u0107..."
            startUpdateDownload(info, dialog, title, body, status, progress)
        }

        card.addView(changes, LinearLayout.LayoutParams(-1, -2).apply { setMargins(0, dp(14), 0, 0) })
        card.addView(progress, LinearLayout.LayoutParams(-2, -2).apply {
            gravity = Gravity.CENTER_HORIZONTAL
            setMargins(0, dp(14), 0, 0)
        })
        card.addView(status)
        row.addView(whatsNew)
        row.addView(later)
        row.addView(install)
        card.addView(row, LinearLayout.LayoutParams(-1, -2).apply { setMargins(0, dp(16), 0, 0) })

        dialog.setContentView(card)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
        dialog.window?.setLayout(minOf(resources.displayMetrics.widthPixels - dp(48), dp(440)), ViewGroup.LayoutParams.WRAP_CONTENT)
        card.alpha = 0f
        card.scaleX = 0.96f
        card.scaleY = 0.96f
        card.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(180).start()
    }

    private fun startUpdateDownload(
        info: StartupUpdateInfo,
        dialog: Dialog,
        title: TextView,
        body: TextView,
        status: TextView,
        progress: ProgressBar
    ) {
        Thread {
            val result = runCatching { downloadAndVerifyUpdateApk(info) }
            runOnUiThread {
                progress.visibility = View.GONE
                result.onSuccess { file ->
                    title.text = "Aktualizacja gotowa"
                    body.text = "Plik zosta\u0142 pobrany i zweryfikowany."
                    status.text = ""
                    status.visibility = View.GONE
                    val open = compactButton("Otw\u00f3rz instalator", primary = true) {
                        dialog.dismiss()
                        installDownloadedApk(file)
                    }
                    (title.parent as? LinearLayout)?.addView(open, LinearLayout.LayoutParams(-1, dp(52)).apply {
                        setMargins(0, dp(14), 0, 0)
                    })
                }.onFailure {
                    title.text = "Nie uda\u0142o si\u0119 pobra\u0107 aktualizacji"
                    body.text = "Sprawd\u017a po\u0142\u0105czenie i spr\u00f3buj ponownie."
                    status.text = it.message.orEmpty().take(120)
                    status.visibility = View.VISIBLE
                    val retry = compactButton("Spr\u00f3buj ponownie", primary = true) {
                        dialog.dismiss()
                        showStartupUpdateDialog(info)
                    }
                    (title.parent as? LinearLayout)?.addView(retry, LinearLayout.LayoutParams(-1, dp(52)).apply {
                        setMargins(0, dp(14), 0, 0)
                    })
                }
            }
        }.start()
    }

    private fun releaseApkUrl(root: JSONObject): String {
        val assets = root.optJSONArray("assets") ?: return ""
        for (index in 0 until assets.length()) {
            val asset = assets.optJSONObject(index) ?: continue
            val name = asset.optString("name")
            val url = asset.optString("browser_download_url")
            if (name.endsWith(".apk", ignoreCase = true) && url.startsWith("https://")) {
                return url
            }
        }
        return ""
    }

    private fun releaseSha256Url(root: JSONObject, apkUrl: String): String {
        val assets = root.optJSONArray("assets") ?: return ""
        val apkName = apkUrl.substringAfterLast("/")
        for (index in 0 until assets.length()) {
            val asset = assets.optJSONObject(index) ?: continue
            val name = asset.optString("name")
            val url = asset.optString("browser_download_url")
            if (url.startsWith("https://") && name.equals("$apkName.sha256", ignoreCase = true)) {
                return url
            }
        }
        for (index in 0 until assets.length()) {
            val asset = assets.optJSONObject(index) ?: continue
            val name = asset.optString("name")
            val url = asset.optString("browser_download_url")
            if (url.startsWith("https://") && name.endsWith(".sha256", ignoreCase = true)) {
                return url
            }
        }
        return ""
    }

    private fun startUpdateDownload(actionUrl: String) {
        val uri = Uri.parse(actionUrl)
        val apkUrl = uri.getQueryParameter("url").orEmpty()
        val tag = uri.getQueryParameter("tag").orEmpty().ifBlank { "latest" }
        if (!apkUrl.startsWith("https://")) {
            Toast.makeText(this, "Brak prawidlowego linku APK.", Toast.LENGTH_LONG).show()
            return
        }

        showLoadingPage("Aktualizacja", "Pobieranie wersji $tag...")
        Thread {
            val result = runCatching {
                val json = getJson(githubLatestReleaseUrl, "")
                val root = JSONObject(json)
                val shaUrl = releaseSha256Url(root, apkUrl)
                downloadAndVerifyUpdateApk(StartupUpdateInfo(tag, apkUrl, shaUrl, tag, ""))
            }
            runOnUiThread {
                result.onSuccess { file ->
                    Toast.makeText(this, "Pobrano aktualizacje.", Toast.LENGTH_SHORT).show()
                    installDownloadedApk(file)
                }.onFailure {
                    showNativePage(
                        "O aplikacji",
                        "about",
                        listOf(
                            PageItem("Nie udalo sie pobrac aktualizacji", tag, it.message.orEmpty(), actionUrl = "app://about", actionLabel = "Wroc")
                        )
                    )
                }
            }
        }.start()
    }

    private fun downloadAndVerifyUpdateApk(info: StartupUpdateInfo): File {
        val file = downloadUpdateApk(info.apkUrl, info.tag)
        runCatching {
            if (info.shaUrl.isNotBlank()) {
                val expected = getRawText(info.shaUrl).trim().split(Regex("\\s+")).firstOrNull().orEmpty()
                if (expected.isNotBlank() && sha256(file) != expected.lowercase(Locale.US)) {
                    file.delete()
                    throw IllegalStateException("Suma kontrolna aktualizacji jest nieprawidlowa.")
                }
            }
            verifyDownloadedApk(file)
        }.onFailure {
            file.delete()
            throw it
        }
        return file
    }

    private fun verifyDownloadedApk(file: File) {
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            PackageManager.GET_SIGNING_CERTIFICATES
        } else {
            @Suppress("DEPRECATION")
            PackageManager.GET_SIGNATURES
        }
        val info = packageManager.getPackageArchiveInfo(file.absolutePath, flags)
            ?: throw IllegalStateException("Pobranego pliku nie mozna uzyc jako APK.")

        if (info.packageName != packageName) {
            throw IllegalStateException("Plik aktualizacji ma nieprawidlowy pakiet.")
        }

        val currentCode = packageManager.getPackageInfo(packageName, 0).longVersionCode
        val downloadedCode = info.longVersionCode
        if (downloadedCode < currentCode) {
            throw IllegalStateException("Nie mozna zainstalowac starszej wersji aplikacji.")
        }
        if (downloadedCode == currentCode) {
            throw IllegalStateException("Masz juz te wersje aplikacji.")
        }

        val appInfo = info.applicationInfo
        if (appInfo != null) {
            val flagsValue = appInfo.flags
            if ((flagsValue and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0 ||
                (flagsValue and android.content.pm.ApplicationInfo.FLAG_TEST_ONLY) != 0
            ) {
                throw IllegalStateException("Plik aktualizacji nie jest wydaniem release.")
            }
        }

        val actualCert = archiveSigningCertSha256(info)
        val expectedCert = officialSigningCertSha256()
        if (actualCert.isBlank() || actualCert != expectedCert) {
            throw IllegalStateException("Plik aktualizacji ma nieprawidlowy podpis.")
        }
    }

    private fun archiveSigningCertSha256(info: android.content.pm.PackageInfo): String {
        val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            info.signingInfo?.apkContentsSigners
        } else {
            @Suppress("DEPRECATION")
            info.signatures
        } ?: return ""
        val cert = signatures.firstOrNull()?.toByteArray() ?: return ""
        return certSha256(cert)
    }

    private fun officialSigningCertSha256(): String {
        return "96637c94668794e0ce73752eaa132b94e2cc9847f94e50fc2bb638a2498e3297"
    }

    private fun sha256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { input ->
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            while (true) {
                val read = input.read(buffer)
                if (read <= 0) break
                digest.update(buffer, 0, read)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    private fun certSha256(bytes: ByteArray): String {
        return MessageDigest.getInstance("SHA-256")
            .digest(bytes)
            .joinToString("") { "%02x".format(it) }
    }

    private fun getRawText(address: String): String {
        val connection = URL(address).openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 15000
        connection.readTimeout = 30000
        connection.setRequestProperty("User-Agent", "PlusXMobile/${appVersionName()}")
        return connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
    }

    private fun downloadUpdateApk(apkUrl: String, tag: String): File {
        val safeTag = tag.replace(Regex("[^A-Za-z0-9._-]"), "_")
        val dir = File(getExternalFilesDir(null), "updates")
        if (!dir.exists()) dir.mkdirs()
        val target = File(dir, "PlusXMobile-$safeTag.apk")
        val temp = File(dir, "PlusXMobile-$safeTag.apk.part")

        val connection = (URL(apkUrl).openConnection() as HttpURLConnection).apply {
            connectTimeout = 15000
            readTimeout = 45000
            instanceFollowRedirects = true
            requestMethod = "GET"
            setRequestProperty("User-Agent", "PlusXMobile/${appVersionName()}")
        }

        connection.inputStream.use { input ->
            FileOutputStream(temp).use { output ->
                val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                while (true) {
                    val read = input.read(buffer)
                    if (read <= 0) break
                    output.write(buffer, 0, read)
                }
            }
        }
        connection.disconnect()

        if (temp.length() < 100_000L) {
            temp.delete()
            throw IllegalStateException("Pobrany plik jest za maly.")
        }
        if (target.exists()) target.delete()
        if (!temp.renameTo(target)) {
            temp.copyTo(target, overwrite = true)
            temp.delete()
        }
        return target
    }

    private fun installDownloadedApk(file: File) {
        if (!file.exists()) {
            Toast.makeText(this, "Plik aktualizacji nie istnieje.", Toast.LENGTH_LONG).show()
            return
        }
        if (!canInstallPackages()) {
            pendingUpdateApk = file
            Toast.makeText(this, "Zezwol aplikacji na instalowanie aktualizacji.", Toast.LENGTH_LONG).show()
            openUnknownSourcesSettings()
            return
        }
        launchApkInstaller(file)
    }

    private fun canInstallPackages(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.O || packageManager.canRequestPackageInstalls()
    }

    private fun openUnknownSourcesSettings() {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:$packageName"))
        } else {
            Intent(Settings.ACTION_SECURITY_SETTINGS)
        }
        startActivity(intent)
    }

    private fun launchApkInstaller(file: File) {
        val apkUri = Uri.Builder()
            .scheme("content")
            .authority("$packageName.updateprovider")
            .appendPath(file.name)
            .build()
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }

    private fun itemCard(item: PageItem, clickableCard: Boolean = false): View {
        val card = LinearLayout(this)
        card.orientation = LinearLayout.VERTICAL
        card.setPadding(dp(16), dp(16), dp(16), dp(16))
        card.background = rounded(cardColor(), 18, strokeColor())
        if (clickableCard && item.actionUrl.isNotBlank()) {
            card.isClickable = true
            card.isFocusable = true
            card.setOnClickListener {
                card.animate().scaleX(0.985f).scaleY(0.985f).setDuration(70).withEndAction {
                    card.animate().scaleX(1f).scaleY(1f).setDuration(90).start()
                    openInternalAction(item.actionUrl)
                }.start()
            }
        }

        val header = LinearLayout(this)
        header.orientation = LinearLayout.HORIZONTAL
        header.setPadding(0, 0, 0, dp(2))

        if (item.badgeImageRes != 0 || item.badgeText.isNotBlank()) {
            val logoBox = FrameLayout(this)
            logoBox.background = rounded(if (darkMode) Color.rgb(15, 23, 42) else Color.rgb(248, 250, 252), 18, strokeColor())
            if (item.badgeImageRes != 0) {
                val image = ImageView(this)
                image.setImageResource(item.badgeImageRes)
                image.scaleType = ImageView.ScaleType.FIT_CENTER
                image.adjustViewBounds = true
                image.setPadding(dp(7), dp(7), dp(7), dp(7))
                logoBox.addView(image, FrameLayout.LayoutParams(dp(58), dp(58), Gravity.CENTER))
                header.addView(logoBox, LinearLayout.LayoutParams(dp(62), dp(58)).apply { setMargins(0, 0, dp(12), 0) })
            } else {
                val badge = TextView(this)
                badge.text = item.badgeText
                badge.textSize = 14f
                badge.setTypeface(null, 1)
                badge.gravity = 17
                badge.setTextColor(textColor())
                logoBox.addView(badge, FrameLayout.LayoutParams(dp(56), dp(48)))
                header.addView(logoBox, LinearLayout.LayoutParams(dp(56), dp(48)).apply { setMargins(0, 0, dp(12), 0) })
            }
        }

        val titleWrap = LinearLayout(this)
        titleWrap.orientation = LinearLayout.VERTICAL
        val title = TextView(this)
        title.text = item.title
        title.textSize = 17f
        title.setTypeface(null, 1)
        title.setTextColor(textColor())
        titleWrap.addView(title)

        if (item.subtitle.isNotBlank()) {
            val subtitle = TextView(this)
            subtitle.text = item.subtitle
            subtitle.textSize = 13f
            subtitle.setTextColor(mutedColor())
            subtitle.setPadding(0, dp(4), 0, 0)
            titleWrap.addView(subtitle)
        }

        if (clickableCard && item.actionUrl.isNotBlank()) {
            val hint = TextView(this)
            hint.text = "Dotknij, aby otworzyc"
            hint.textSize = 12f
            hint.setTextColor(Color.rgb(14, 165, 180))
            hint.setPadding(0, dp(6), 0, 0)
            titleWrap.addView(hint)
        }

        header.addView(titleWrap, LinearLayout.LayoutParams(0, -2, 1f))
        if (clickableCard && item.actionUrl.isNotBlank()) {
            val chevron = TextView(this)
            chevron.text = ">"
            chevron.textSize = 18f
            chevron.setTypeface(null, 1)
            chevron.setTextColor(Color.rgb(14, 165, 180))
            chevron.gravity = Gravity.CENTER
            chevron.background = rounded(
                if (darkMode) Color.rgb(15, 23, 42) else Color.rgb(236, 254, 255),
                14,
                Color.rgb(14, 165, 180)
            )
            header.addView(chevron, LinearLayout.LayoutParams(dp(34), dp(34)).apply { setMargins(dp(10), dp(4), 0, 0) })
        }
        card.addView(header)

        if (item.value.isNotBlank()) {
            val value = TextView(this)
            value.text = item.value
            value.textSize = 14f
            value.setTextColor(textColor())
            value.setLineSpacing(dp(2).toFloat(), 1.0f)
            if (item.copyText.isNotBlank()) {
                value.setPadding(dp(12), dp(10), dp(12), dp(10))
                value.background = rounded(if (darkMode) Color.rgb(31, 41, 55) else Color.rgb(229, 231, 235), 12, strokeColor())
                value.setOnClickListener { copyToClipboard(item.copyText) }
            } else {
                value.setPadding(0, dp(8), 0, 0)
            }
            card.addView(value)
        }

        if (!clickableCard && (item.actionUrl.isNotBlank() || item.secondActionUrl.isNotBlank())) {
            val row = LinearLayout(this)
            row.orientation = LinearLayout.HORIZONTAL

            if (item.actionUrl.isNotBlank()) {
                row.addView(compactButton(item.actionLabel, primary = true) { openInternalAction(item.actionUrl) })
            }

            if (item.secondActionUrl.isNotBlank()) {
                row.addView(compactButton(item.secondActionLabel.ifBlank { "Otworz" }) { openInternalAction(item.secondActionUrl) })
            }

            card.addView(row)
        }

        val params = LinearLayout.LayoutParams(-1, -2)
        params.setMargins(0, 0, 0, dp(14))
        card.layoutParams = params
        card.alpha = 0f
        card.translationY = dp(10).toFloat()
        card.animate().alpha(1f).translationY(0f).setDuration(180).start()
        return card
    }

    private fun newsCard(item: PageItem): View {
        val accent = newsAccentColor(item)
        val card = LinearLayout(this)
        card.orientation = LinearLayout.VERTICAL
        card.setPadding(dp(14), dp(14), dp(14), dp(14))
        card.background = rounded(cardColor(), 14, accent)

        val meta = LinearLayout(this)
        meta.orientation = LinearLayout.HORIZONTAL
        meta.gravity = Gravity.CENTER_VERTICAL

        val label = TextView(this)
        label.text = item.badgeText.ifBlank { item.subtitle.substringBefore(" ").ifBlank { "INFO" } }.uppercase()
        label.textSize = 11f
        label.setTypeface(null, 1)
        label.setTextColor(accent)
        label.setPadding(dp(8), dp(3), dp(8), dp(3))
        label.background = rounded(newsPillBg(accent), 8, 0)
        meta.addView(label)

        val date = item.subtitle.substringAfter("·", "").trim()
        if (date.isNotBlank()) {
            val dateView = TextView(this)
            dateView.text = date
            dateView.textSize = 12f
            dateView.setTextColor(mutedColor())
            dateView.setPadding(dp(10), 0, 0, 0)
            meta.addView(dateView)
        }

        val metaSpacer = View(this)
        meta.addView(metaSpacer, LinearLayout.LayoutParams(0, 1, 1f))

        if (item.cornerText.isNotBlank()) {
            val corner = TextView(this)
            corner.text = item.cornerText
            corner.textSize = 11f
            corner.setTypeface(null, 1)
            corner.setTextColor(Color.WHITE)
            corner.setPadding(dp(8), dp(4), dp(8), dp(4))
            corner.background = rounded(Color.rgb(22, 163, 74), 9, 0)
            meta.addView(corner)
        }
        card.addView(meta)

        val title = TextView(this)
        title.text = item.title
        title.textSize = 17f
        title.setTypeface(null, 1)
        title.setTextColor(textColor())
        title.setPadding(0, dp(12), 0, dp(8))
        title.setLineSpacing(dp(2).toFloat(), 1f)
        card.addView(title)

        if (item.value.isNotBlank()) {
            val value = TextView(this)
            value.text = item.value
            value.textSize = 14f
            value.setTextColor(textColor())
            value.setLineSpacing(dp(3).toFloat(), 1f)
            value.setPadding(dp(10), dp(10), dp(10), dp(10))
            value.background = rounded(newsBodyBg(accent), 10, newsBodyStroke(accent))
            card.addView(value)
        }

        val params = LinearLayout.LayoutParams(-1, -2)
        params.setMargins(0, 0, 0, dp(14))
        card.layoutParams = params
        card.alpha = 0f
        card.translationY = dp(8).toFloat()
        card.animate().alpha(1f).translationY(0f).setDuration(160).start()
        return card
    }

    private fun newsAccentColor(item: PageItem): Int {
        val key = "${item.badgeText} ${item.subtitle} ${item.title}".uppercase()
        return when {
            key.contains("MAI") || key.contains("MAINTENANCE") || key.contains("WARNING") -> Color.rgb(245, 158, 11)
            key.contains("UPD") || key.contains("UPDATE") || key.contains("SYSTEM") || key.contains("SECURITY") -> Color.rgb(59, 130, 246)
            key.contains("EPG") -> Color.rgb(168, 85, 247)
            key.contains("TG") || key.contains("TELEGRAM") -> Color.rgb(14, 165, 180)
            else -> Color.rgb(34, 197, 94)
        }
    }

    private fun newsPillBg(accent: Int): Int {
        return if (darkMode) Color.argb(42, Color.red(accent), Color.green(accent), Color.blue(accent))
        else Color.argb(28, Color.red(accent), Color.green(accent), Color.blue(accent))
    }

    private fun newsBodyBg(accent: Int): Int {
        return if (darkMode) Color.argb(18, Color.red(accent), Color.green(accent), Color.blue(accent))
        else Color.argb(14, Color.red(accent), Color.green(accent), Color.blue(accent))
    }

    private fun newsBodyStroke(accent: Int): Int {
        return if (darkMode) Color.argb(74, Color.red(accent), Color.green(accent), Color.blue(accent))
        else Color.argb(70, Color.red(accent), Color.green(accent), Color.blue(accent))
    }

    private fun copyToClipboard(text: String) {
        val manager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        manager.setPrimaryClip(ClipData.newPlainText("PlusX", text))
        Toast.makeText(this, "Skopiowano", Toast.LENGTH_SHORT).show()
    }

    private fun showM3uDownloadChoice(key: String) {
        val links = m3uDownloadChoices[key].orEmpty()
        if (links.isEmpty()) {
            Toast.makeText(this, "Brak linkow CDN", Toast.LENGTH_SHORT).show()
            return
        }
        val labels = links.map { "CDN ${it.cdn} - ${it.region}" }
        showModernOptions("Wybierz link", labels) { index ->
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(links[index].fullUrl)))
        }
    }

    private fun openExternalOrPortal(url: String) {
        val fixed = when {
            url.startsWith("http://") || url.startsWith("https://") -> url
            url.startsWith("/") -> "$baseUrl$url"
            else -> "$baseUrl/$url"
        }

        if (fixed.startsWith(baseUrl)) {
            openPortalPage(fixed)
        } else {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(fixed)))
        }
    }

    private fun openSupportPage() {
        val uri = Uri.parse("https://tipply.pl/@torvinek")
        if (uri.scheme != "https" || uri.host != "tipply.pl") {
            Toast.makeText(this, "Nie udalo sie otworzyc strony wsparcia.", Toast.LENGTH_LONG).show()
            return
        }
        val intent = Intent(Intent.ACTION_VIEW, uri)
        try {
            startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(this, "Nie udalo sie otworzyc strony wsparcia.", Toast.LENGTH_LONG).show()
        }
    }

    private fun rememberDashboardAndMaybeShowSupport() {
        val prefs = getSharedPreferences("plusx_support", Context.MODE_PRIVATE)
        val now = System.currentTimeMillis()
        if (prefs.getLong("first_dashboard_at", 0L) == 0L) {
            prefs.edit().putLong("first_dashboard_at", now).apply()
            return
        }
        if (supportPopupShownThisSession ||
            startupUpdateDialogShown ||
            prefs.getBoolean("disabled", false) ||
            prefs.getBoolean("accepted", false)
        ) {
            return
        }
        val first = prefs.getLong("first_dashboard_at", now)
        val snoozedUntil = prefs.getLong("snoozed_until", 0L)
        if (now - first < 72L * 60L * 60L * 1000L || now < snoozedUntil) return

        supportPopupShownThisSession = true
        Handler(Looper.getMainLooper()).postDelayed({ showSupportDialog() }, 900)
    }

    private fun showSupportDialog() {
        if (currentScreen != "dashboard" || isFinishing || isDestroyed) return
        val prefs = getSharedPreferences("plusx_support", Context.MODE_PRIVATE)
        val dialog = Dialog(this)
        val card = LinearLayout(this)
        card.orientation = LinearLayout.VERTICAL
        card.setPadding(dp(24), dp(22), dp(24), dp(20))
        card.background = rounded(cardColor(), 24, strokeColor())

        val icon = TextView(this)
        icon.text = "\u2615"
        icon.textSize = 30f
        icon.gravity = Gravity.CENTER
        icon.setTextColor(Color.WHITE)
        icon.background = rounded(Color.rgb(14, 165, 180), 18, 0)
        card.addView(icon, LinearLayout.LayoutParams(dp(52), dp(52)).apply { gravity = Gravity.CENTER_HORIZONTAL })

        val title = TextView(this)
        title.text = "Podoba Ci sie PlusX Mobile?"
        title.textSize = 20f
        title.setTypeface(null, 1)
        title.gravity = Gravity.CENTER
        title.setTextColor(textColor())
        title.setPadding(0, dp(14), 0, dp(8))
        card.addView(title)

        val body = TextView(this)
        body.text = "Jesli aplikacja Ci sie przydala i chcesz wesprzec dalszy rozwoj projektu, mozesz postawic mi kawe lub piwko. Kazde wsparcie bardzo doceniam - dzieki!"
        body.textSize = 14f
        body.gravity = Gravity.CENTER
        body.setTextColor(textColor())
        body.setLineSpacing(dp(2).toFloat(), 1f)
        card.addView(body)

        val coffee = actionButton("Postaw kawe", primary = true) {
            prefs.edit().putBoolean("accepted", true).apply()
            dialog.dismiss()
            openSupportPage()
        }
        val later = actionButton("Moze pozniej") {
            prefs.edit().putLong("snoozed_until", System.currentTimeMillis() + 30L * 24L * 60L * 60L * 1000L).apply()
            dialog.dismiss()
        }
        val never = actionButton("Nie pokazuj ponownie") {
            prefs.edit().putBoolean("disabled", true).apply()
            dialog.dismiss()
        }
        card.addView(coffee, LinearLayout.LayoutParams(-1, dp(52)).apply { setMargins(0, dp(16), 0, 0) })
        card.addView(later, LinearLayout.LayoutParams(-1, dp(46)).apply { setMargins(0, dp(8), 0, 0) })
        card.addView(never, LinearLayout.LayoutParams(-1, dp(46)).apply { setMargins(0, dp(6), 0, 0) })

        dialog.setContentView(card)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
        dialog.window?.setLayout(minOf(resources.displayMetrics.widthPixels - dp(48), dp(420)), ViewGroup.LayoutParams.WRAP_CONTENT)
        card.alpha = 0f
        card.scaleX = 0.96f
        card.scaleY = 0.96f
        card.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(180).start()
    }

    private fun openInternalAction(url: String) {
        when (url) {
            "app://packets/open" -> {
                openPortalPage("$baseUrl/packets.php")
                return
            }
            "app://reseller/retry" -> {
                loadResellerPage()
                return
            }
            "app://payment/cryptomus" -> {
                openPaymentWebView("cryptomus")
                return
            }
            "app://payment/monobank" -> {
                openPaymentWebView("monobank")
                return
            }
            "app://diagnostics" -> {
                showNativePage("Diagnostyka", "diagnostics", diagnosticsItems())
                return
            }
            "app://diagnostics/basic" -> {
                showDiagnosticsForm(false)
                return
            }
            "app://diagnostics/advanced" -> {
                showAdvancedDiagnosticsChoice()
                return
            }
            "app://about" -> {
                loadAboutPage()
                return
            }
            "app://about/check" -> {
                loadAboutPage(forceCheck = true)
                return
            }
            "app://support" -> {
                openSupportPage()
                return
            }
            else -> {
                if (url.startsWith("app://about/download")) {
                    startUpdateDownload(url)
                    return
                }
                if (url.startsWith("app://billing/details/")) {
                    showBillingDetails(url.removePrefix("app://billing/details/"))
                    return
                }
                if (url.startsWith("app://confirm-buy")) {
                    showBuyConfirmation(url)
                    return
                }
                if (url.startsWith("app://portal/")) {
                    openPortalPage(url.removePrefix("app://portal/"))
                    return
                }
                if (url.startsWith("app://m3u/download/")) {
                    showM3uDownloadChoice(url.removePrefix("app://m3u/download/"))
                    return
                }
            }
        }

        when (url) {
            "app://theme/dark" -> {
                darkMode = true
                root.setBackgroundColor(bgColor())
                Toast.makeText(this, "Tryb ciemny wlaczony", Toast.LENGTH_SHORT).show()
                showNativePage("Ustawienia", "profile.php", settingsItems())
                return
            }
            "app://theme/light" -> {
                darkMode = false
                root.setBackgroundColor(bgColor())
                Toast.makeText(this, "Tryb jasny wlaczony", Toast.LENGTH_SHORT).show()
                showNativePage("Ustawienia", "profile.php", settingsItems())
                return
            }
        }

        val fixed = when {
            url.startsWith("http://") || url.startsWith("https://") -> url
            url.startsWith("/") -> "$baseUrl$url"
            else -> "$baseUrl/$url"
        }

        val uri = Uri.parse(fixed)
        val path = uri.path.orEmpty().trimStart('/')
        val query = uri.encodedQuery
        val pathWithQuery = if (query.isNullOrBlank()) path else "$path?$query"

        when {
            path.startsWith("packets.php") -> loadNativePage(pathWithQuery, "Pakiety") { parsePackets(it) }
            path.startsWith("balance_history.php") -> loadNativePage(pathWithQuery, "Historia zakupow") { parseBillingHistory(it) }
            path.startsWith("tuner_settings.php") -> {
                pendingM3uUser = Uri.parse(fixed).getQueryParameter("selected_user")
                    ?.let { URLDecoder.decode(it, "UTF-8") }
                    .orEmpty()
                loadNativePage(pathWithQuery, "Linki M3U") { parseM3u(it) }
            }
            fixed.startsWith("http://") || fixed.startsWith("https://") && !fixed.startsWith(baseUrl) ->
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(fixed)))
            else -> openPortalPage(fixed)
        }
    }

    private fun getHtml(address: String): String {
        val connection = URL(address).openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 PlusXMobile Android")
        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml")
        connection.setRequestProperty("Cookie", cookieHeader)
        connection.connectTimeout = 30000
        connection.readTimeout = 60000
        return connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            .also { rememberDiagnostic("HTML $address", it) }
    }

    private fun getJson(address: String, token: String): String {
        val connection = URL(address).openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("User-Agent", "PlusXMobile Android")
        connection.setRequestProperty("Accept", "application/json")
        if (token.isNotBlank()) {
            connection.setRequestProperty("Authorization", "Bearer $token")
        }
        connection.connectTimeout = 30000
        connection.readTimeout = 60000
        return connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            .also { rememberDiagnostic("JSON $address", it) }
    }

    private fun rememberDiagnostic(name: String, content: String) {
        synchronized(diagnosticsSnapshots) {
            diagnosticsSnapshots[name] = content
            while (diagnosticsSnapshots.size > 10) {
                val firstKey = diagnosticsSnapshots.keys.firstOrNull() ?: break
                diagnosticsSnapshots.remove(firstKey)
            }
        }
    }

    private fun parseBalance(html: String): String? {
        val markerIndex = html.indexOf("Your balance", ignoreCase = true)
        if (markerIndex < 0) return null

        val chunk = html.substring(markerIndex, minOf(html.length, markerIndex + 700))
            .replace("&euro;", "\u20AC")
            .replace("&#8364;", "\u20AC")
            .replace("&nbsp;", " ")
            .replace("&#160;", " ")
            .replace(Regex("<[^>]+>"), " ")
            .replace(Regex("\\s+"), " ")

        return Regex("(\\d+(?:[.,]\\d{1,2})?)\\s*(?:\\u20AC|EUR)", RegexOption.IGNORE_CASE)
            .find(chunk)
            ?.value
            ?: Regex("\\b\\d+(?:[.,]\\d{1,2})\\b").find(chunk.substringAfter("Your balance", chunk))?.value?.let { "$it €" }
    }

    private fun parseProfile(html: String): List<PageItem> {
        val safeHtml = html.replace(Regex("<input[^>]*type=[\"']?password[\"']?[^>]*>", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)), "")
        val username = Regex("<label>Username:</label>.*?<input[^>]+value=\"([^\"]*)\"", RegexOption.DOT_MATCHES_ALL)
            .find(safeHtml)?.groupValues?.get(1).orEmpty()
        val email = Regex("<label>Email Address:</label>.*?<input[^>]+value=\"([^\"]*)\"", RegexOption.DOT_MATCHES_ALL)
            .find(safeHtml)?.groupValues?.get(1).orEmpty()

        val items = listOf(
            PageItem("Wyglad aplikacji", "Aktualnie: ${if (darkMode) "tryb ciemny" else "tryb jasny"}"),
            PageItem("Username", value = username.ifBlank { "Brak" }),
            PageItem("Email", "Aby zmienic email skontaktuj sie z supportem PlusX.", email.ifBlank { "Brak" }),
            PageItem("Zmien haslo", "Otwiera bezpieczny formularz portalu w WebView.", actionUrl = "/profile.php", actionLabel = "Otworz"),
            PageItem("Zarzadzaj 2FA", "Kod QR i sekret zostaja tylko po stronie portalu.", actionUrl = "/profile.php", actionLabel = "Otworz"),
            PageItem("Wesprzyj projekt", "Postaw kawe i wesprzyj rozwoj PlusX Mobile.", actionUrl = "app://support", actionLabel = "Otworz"),
            PageItem("O aplikacji", "Wersja aplikacji, aktualizacje i ostatnie zmiany z GitHuba.", actionUrl = "app://about", actionLabel = "Otworz"),
            PageItem("Diagnostyka", "Dane do pomocy technicznej. Bez doładowań, ustawien i bez prywatnych adresow IP.", actionUrl = "app://diagnostics", actionLabel = "Otworz")
        )
        lastSettingsItems = items
        return items
    }

    private fun parseBalancePage(html: String): List<PageItem> {
        val amounts = Regex("<option\\s+value=\"([0-9]+)\">\\s*([0-9]+)")
            .findAll(html)
            .map { "${it.groupValues[2]} EUR" }
            .distinct()
            .joinToString(", ")

        return listOf(
            PageItem("Crypto", "Cryptomus", "Domyslna kwota: 5.00 EUR"),
            PageItem("Visa / Mastercard", "Apple Pay / Google Pay", amounts.ifBlank { "Kwoty z formularza platnosci" })
        )
    }

    private fun parseBillingHistory(html: String): List<PageItem> {
        val clean = cleanText(html)
        val dateRegex = Regex("(\\d{2}\\.\\d{2}\\.\\d{4}\\s+\\d{2}:\\d{2})")
        val matches = dateRegex.findAll(clean).toList()
        if (matches.isEmpty()) {
            currentBillingEntries = emptyList()
            currentBillingUsers = emptyList()
            return listOf(PageItem("Brak historii", "Nie znaleziono wpisow rozliczen."))
        }

        currentBillingMainUser = parseDashboardUsername(lastDashboardHtml)
        val entries = matches.mapIndexedNotNull { index, match ->
            val start = match.range.first
            val end = matches.getOrNull(index + 1)?.range?.first ?: clean.length
            val block = clean.substring(start, end).trim()
            parseBillingHistoryEntryData(block)
        }.take(1000)

        currentBillingEntries = entries
        currentBillingUsers = billingKnownUsers(entries)
        if (currentBillingSelectedUser.isNotBlank() && currentBillingUsers.none { accountStatusKey(it) == accountStatusKey(currentBillingSelectedUser) }) {
            currentBillingSelectedUser = ""
        }

        val packageHints = entries
            .mapNotNull { entry ->
                val user = accountStatusKey(entry.user)
                val direct = directBillingPackageName(kotlin.math.abs(entry.amount))
                if (user.isNotBlank() && direct.isNotBlank()) user to direct else null
            }
            .groupBy({ it.first }, { it.second })
            .mapValues { (_, packages) -> packages.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key.orEmpty() }

        return entries.map { entry ->
            billingHistoryItem(entry, packageHints[accountStatusKey(entry.user)].orEmpty())
        }.ifEmpty {
            listOf(PageItem("Brak historii", "Nie udalo sie odczytac wpisow rozliczen."))
        }
    }

    private data class BillingHistoryEntry(
        val date: String,
        val amount: Double,
        val user: String,
        val reason: String
    )

    private fun billingKnownUsers(entries: List<BillingHistoryEntry>): List<String> {
        return (
            entries.map { it.user }.filter { it.isNotBlank() && isLikelyBillingUser(it) } +
                listOf(currentBillingMainUser).filter { it.isNotBlank() } +
                resellerAccountDetails.values.map { it.login }
            )
            .distinctBy { accountStatusKey(it) }
            .sortedWith(compareBy<String> {
                when {
                    accountStatusKey(it) == accountStatusKey(currentBillingMainUser) -> 0
                    resellerAccountDetails.containsKey(accountStatusKey(it)) -> 1
                    else -> 2
                }
            }.thenBy { cleanSelectedUser(it).lowercase(Locale.ROOT) })
    }

    private fun billingHistoryItemsForSelectedUser(): List<PageItem> {
        val selectedKey = accountStatusKey(currentBillingSelectedUser)
        val entries = currentBillingEntries.filter {
            selectedKey.isBlank() || accountStatusKey(it.user) == selectedKey
        }
        if (entries.isEmpty()) return emptyList()

        val hints = billingPackageHints(currentBillingEntries)
        val items = mutableListOf<PageItem>()
        var index = 0
        while (index < entries.size) {
            val entry = entries[index]
            if (entry.amount > 0) {
                val group = mutableListOf(entry)
                var next = index + 1
                while (next < entries.size && entries[next].amount > 0 && accountStatusKey(entries[next].user) == accountStatusKey(entry.user)) {
                    group.add(entries[next])
                    next += 1
                }
                items.add(billingTopupGroupItem(group))
                index = next
            } else {
                items.add(billingHistoryItem(entry, hints[accountStatusKey(entry.user)].orEmpty()))
                index += 1
            }
        }
        return items
    }

    private fun billingPackageHints(entries: List<BillingHistoryEntry>): Map<String, String> {
        return entries
            .mapNotNull { entry ->
                val user = accountStatusKey(entry.user)
                val direct = directBillingPackageName(kotlin.math.abs(entry.amount))
                if (user.isNotBlank() && direct.isNotBlank() && entry.amount < 0) user to direct else null
            }
            .groupBy({ it.first }, { it.second })
            .mapValues { (_, packages) -> packages.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key.orEmpty() }
    }

    private fun billingTopupGroupItem(group: List<BillingHistoryEntry>): PageItem {
        val total = group.sumOf { it.amount }
        val user = group.firstOrNull()?.user.orEmpty()
        val id = "topup_${billingDetails.size}_${System.currentTimeMillis()}"
        val details = buildString {
            appendLine("Doladowanie konta")
            appendLine("Liczba wpisow: ${group.size}")
            appendLine("Suma: ${formatEuroAmount(total)}")
            if (user.isNotBlank()) appendLine("Uzytkownik: $user")
            appendLine()
            appendLine("Szczegoly:")
            group.forEach {
                appendLine("- ${billingDateForDisplay(it.date)}: ${formatEuroAmount(it.amount)}")
            }
        }.trim()
        billingDetails[id] = details
        return PageItem(
            title = if (group.size > 1) "Doladowania konta" else "Doladowanie konta",
            subtitle = billingDateForDisplay(group.first().date),
            value = listOf(
                "Suma: ${formatEuroAmount(total)}",
                if (group.size > 1) "Polaczono wpisow: ${group.size}" else "Kwota doladowania: ${formatEuroAmount(total)}",
                if (user.isNotBlank()) "Uzytkownik: $user" else ""
            ).filter { it.isNotBlank() }.joinToString("\n"),
            actionUrl = "app://billing/details/$id",
            actionLabel = "Szczegoly",
            badgeText = "+"
        )
    }

    private fun parseBillingHistoryEntryData(block: String): BillingHistoryEntry? {
        val date = Regex("(\\d{2}\\.\\d{2}\\.\\d{4}\\s+\\d{2}:\\d{2})").find(block)?.groupValues?.get(1) ?: return null
        val amountRaw = Regex("([+-]\\d+(?:[.,]\\d+)?)").find(block.substringAfter(date))?.groupValues?.get(1) ?: return null
        val amount = amountRaw.replace(",", ".").toDoubleOrNull() ?: return null
        val user = extractBillingUser(block).ifBlank { currentBillingMainUser }
        val reason = when {
            amount > 0 -> "Doladowanie konta"
            block.contains("Purchase of package", true) -> "Zakup pakietu"
            else -> "Operacja na koncie"
        }
        return BillingHistoryEntry(date, amount, user, reason)
    }

    private fun billingHistoryItem(entry: BillingHistoryEntry, userPackageHint: String): PageItem {
        val rounded = formatEuroAmount(entry.amount)
        val details = if (entry.amount > 0) {
            "Kwota doladowania: $rounded"
        } else {
            probablePackageByPrice(kotlin.math.abs(entry.amount), userPackageHint)
        }
        val id = "entry_${billingDetails.size}_${entry.date.replace(Regex("[^0-9]"), "")}"
        billingDetails[id] = listOf(
            entry.reason,
            "Data: ${billingDateForDisplay(entry.date)}",
            "Kwota: $rounded",
            details,
            if (entry.user.isNotBlank()) "Uzytkownik: ${entry.user}" else ""
        ).filter { it.isNotBlank() }.joinToString("\n")
        val value = listOf(
            "Kwota: $rounded",
            details,
            if (entry.user.isNotBlank()) "Uzytkownik: ${entry.user}" else ""
        ).filter { it.isNotBlank() }.joinToString("\n")

        return PageItem(
            title = entry.reason,
            subtitle = billingDateForDisplay(entry.date),
            value = value,
            actionUrl = "app://billing/details/$id",
            actionLabel = "Szczegoly",
            badgeText = if (entry.amount > 0) "+" else "-"
        )
    }

    private fun showBillingDetails(id: String) {
        val details = billingDetails[id].orEmpty()
        showModernMessage("Szczegoly historii", details.ifBlank { "Brak szczegolow dla tego wpisu." })
    }

    private fun extractBillingUser(block: String): String {
        val protectedEmail = Regex("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}", RegexOption.IGNORE_CASE)
            .find(block)
            ?.value
        if (!protectedEmail.isNullOrBlank()) return protectedEmail
        val knownUsers = (resellerAccountDetails.values.map { it.login } + currentBillingMainUser)
            .filter { it.isNotBlank() }
            .sortedByDescending { it.length }
        knownUsers.firstOrNull { known ->
            Regex("(^|\\s|>)${Regex.escape(known)}($|\\s|<)", RegexOption.IGNORE_CASE).containsMatchIn(block)
        }?.let { return it }
        return Regex("(?:user_[A-Za-z0-9]+|s[a-f0-9]{7,}|\\b[a-f0-9]{10,16}\\b)", RegexOption.IGNORE_CASE)
            .findAll(block)
            .map { it.value }
            .filter { isLikelyBillingUser(it) }
            .firstOrNull()
            .orEmpty()
    }

    private fun isLikelyBillingUser(value: String): Boolean {
        val clean = cleanSelectedUser(value).trim()
        if (clean.isBlank()) return false
        if (clean.contains("@")) return true
        if (clean.startsWith("user_", ignoreCase = true)) return true
        if (Regex("^s[a-f0-9]{7,}$", RegexOption.IGNORE_CASE).matches(clean)) return true
        if (Regex("^[a-f0-9]{10,16}$", RegexOption.IGNORE_CASE).matches(clean)) return true
        return false
    }

    private fun formatEuroAmount(value: Double): String {
        return String.format(Locale.US, "%+.2f EUR", value)
    }

    private fun probablePackageByPrice(amount: Double, userPackageHint: String = ""): String {
        directBillingPackageMatch(amount)?.let { return it }

        val prices = listOf(
            4.80 to "Poland Full HD - 30 dni",
            50.00 to "Poland Full HD - 1 rok",
            6.90 to "Poland Full HD (2 devices) - 30 dni",
            70.00 to "Poland Full HD (2 devices) - 1 rok",
            6.60 to "Poland Full HD (XXX) - 30 dni",
            8.40 to "Poland Full HD (XXX) (2 devices) - 30 dni"
        )
        if (userPackageHint.isNotBlank()) {
            prices.firstOrNull { it.second == userPackageHint }?.let { (basePrice, name) ->
                val discounts = listOf(10 to 0.90, 20 to 0.80, 50 to 0.50)
                val nearestDiscount = discounts
                    .map { (discount, multiplier) -> BillingPriceCandidate(name, basePrice, discount, basePrice * multiplier, kotlin.math.abs((basePrice * multiplier) - amount)) }
                    .minByOrNull { it.delta }
                if (nearestDiscount != null && nearestDiscount.delta <= 0.55) {
                    return "Prawdopodobny pakiet: $name, po historii uzytkownika, mozliwy rabat ${nearestDiscount.discount}%\nCena bazowa: ${String.format(Locale.US, "%.2f EUR", basePrice)}"
                }
            }
        }
        val discounts = listOf(0 to 1.0, 10 to 0.90, 20 to 0.80, 50 to 0.50)
        val candidates = prices.flatMap { (basePrice, name) ->
            discounts.map { (discount, multiplier) ->
                BillingPriceCandidate(
                    packageName = name,
                    basePrice = basePrice,
                    discount = discount,
                    expected = basePrice * multiplier,
                    delta = kotlin.math.abs((basePrice * multiplier) - amount)
                )
            }
        }
        val nearest = candidates.minByOrNull { it.delta }
        return if (nearest != null && nearest.delta <= 0.45) {
            val discountText = if (nearest.discount > 0) ", mozliwy rabat ${nearest.discount}%" else ""
            "Prawdopodobny pakiet: ${nearest.packageName}$discountText\nCena bazowa: ${String.format(Locale.US, "%.2f EUR", nearest.basePrice)}"
        } else {
            "Prawdopodobny pakiet: nieznany"
        }
    }

    private fun directBillingPackageMatch(amount: Double): String? {
        val name = directBillingPackageName(amount)
        val price = when (name) {
            "Poland Full HD - 30 dni" -> "4.80 EUR"
            "Poland Full HD (XXX) - 30 dni" -> "6.60 EUR"
            "Poland Full HD (2 devices) - 30 dni" -> "6.90 EUR"
            "Poland Full HD (XXX) (2 devices) - 30 dni" -> "8.40 EUR"
            "Poland Full HD - 1 rok" -> "50.00 EUR"
            "Poland Full HD (2 devices) - 1 rok" -> "70.00 EUR"
            else -> ""
        }
        return if (name.isNotBlank()) "Prawdopodobny pakiet: $name\nCena bazowa: $price" else null
    }

    private fun directBillingPackageName(amount: Double): String {
        return when (amount) {
            in 4.45..5.05 -> "Poland Full HD - 30 dni"
            in 6.50..6.64 -> "Poland Full HD (XXX) - 30 dni"
            in 6.65..7.10 -> "Poland Full HD (2 devices) - 30 dni"
            in 8.05..8.75 -> "Poland Full HD (XXX) (2 devices) - 30 dni"
            in 48.00..52.00 -> "Poland Full HD - 1 rok"
            in 67.00..73.00 -> "Poland Full HD (2 devices) - 1 rok"
            else -> null
        }.orEmpty()
    }

    private data class BillingPriceCandidate(
        val packageName: String,
        val basePrice: Double,
        val discount: Int,
        val expected: Double,
        val delta: Double
    )

    private fun billingDateForDisplay(value: String): String {
        val match = Regex("(\\d{2})\\.(\\d{2})\\.(\\d{4})\\s+(\\d{2}:\\d{2})").find(value) ?: return value
        return "${displayDate(match.groupValues[3], match.groupValues[2], match.groupValues[1])} ${match.groupValues[4]}"
    }

    private fun parsePackets(html: String): List<PageItem> {
        val users = parseSelectedUsers(html)
        if (users.isNotEmpty()) {
            currentPacketsUsers = users
        }
        if (currentPacketsSelectedUser.isBlank()) {
            currentPacketsSelectedUser = users.firstOrNull().orEmpty()
        }
        val selectedUser = currentPacketsSelectedUser.trim()
        val resellerKnownActive = resellerAccountActivity[accountStatusKey(selectedUser)]
        var activePacket = ""
        val packets = html
            .split(Regex("(?=<div\\b[^>]*class=[\"'][^\"']*packet-item[^\"']*[\"'])", RegexOption.IGNORE_CASE))
            .asSequence()
            .filter { it.contains("packet-item", true) }
            .map { block ->
                val clean = cleanText(block).replace("â‚¬", "EUR").replace("€", "EUR")
                val name = Regex("live_tv\\s+(.+?)\\s+(ACTIVE|Ends on:|Status:|Per Month)", RegexOption.IGNORE_CASE)
                    .find(clean)?.groupValues?.get(1)?.trim()
                    ?: clean.substringAfter("live_tv").substringBefore("Status:").take(60).trim()
                val ends = Regex("(?:Ends on:|Wygasa:)\\s*([0-9.]+)", RegexOption.IGNORE_CASE)
                    .find(clean)
                    ?.groupValues
                    ?.get(1)
                    .orEmpty()
                val isActive = ends.isNotBlank() && resellerKnownActive != false
                val status = if (isActive) "Aktywny" else "Nieaktywny"
                if (isActive && activePacket.isBlank() && name.isNotBlank()) {
                    activePacket = name
                }
                val isTwoDevices = name.contains("2 devices", true) || name.contains("two devices", true)
                val isXxx = name.contains("XXX", true)
                val monthlyPrice = when {
                    name.contains("Poland Full HD", true) && isXxx && isTwoDevices -> "8.40 EUR"
                    name.contains("Poland Full HD", true) && isXxx -> "6.60 EUR"
                    name.contains("Poland Full HD", true) && isTwoDevices -> "6.90 EUR"
                    name.contains("Poland Full HD", true) -> "4.80 EUR"
                    else -> ""
                }
                val yearlyPrice = when {
                    name.contains("Poland Full HD", true) && isTwoDevices -> "70 EUR"
                    name.contains("Poland Full HD", true) -> "50 EUR"
                    else -> ""
                }
                val monthUrl = packetBuyUrl(name, "1month", selectedUser)
                val yearUrl = packetBuyUrl(name, "1year", selectedUser)
                PageItem(
                    title = name.ifBlank { "Pakiet" },
                    subtitle = status,
                    value = listOf(
                        if (isActive && ends.isNotBlank()) "Wygasa: $ends" else ""
                    ).filter { it.isNotBlank() }.joinToString("\n"),
                    actionUrl = buyConfirmUrl(monthUrl, name, "miesiac", monthlyPrice),
                    actionLabel = "Kup na Miesiąc\n${monthlyPrice.ifBlank { "Cena z portalu" }}",
                    secondActionUrl = buyConfirmUrl(yearUrl, name, "rok", yearlyPrice),
                    secondActionLabel = "Kup na Rok\n${yearlyPrice.ifBlank { "Cena z portalu" }}"
                )
            }
            .filter { it.title.isNotBlank() }
            .toList()
        currentPacketsActivePacket = activePacket
        return packets
    }

    private fun accountStatusKey(value: String): String {
        return cleanSelectedUser(value)
            .ifBlank { value }
            .trim()
            .lowercase(Locale.ROOT)
    }

    private fun parseSelectedUsers(html: String): List<String> {
        val select = Regex("<select\\b[^>]*(?:name=[\"']selected_user[\"']|id=[\"']komukey[\"'])[^>]*>(.*?)</select>", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
            .find(html)
            ?.groupValues
            ?.get(1)
            .orEmpty()
        return Regex("<option\\b([^>]*)>(.*?)</option>", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
            .findAll(select)
            .map { match ->
                val attrs = match.groupValues[1]
                val value = Regex("value=[\"']([^\"']+)[\"']", RegexOption.IGNORE_CASE).find(attrs)?.groupValues?.get(1).orEmpty()
                value.ifBlank { cleanText(match.groupValues[2]) }
            }
            .filter { it.isNotBlank() }
            .distinct()
            .toList()
    }

    private fun buyConfirmUrl(targetUrl: String, packetName: String, period: String, price: String): String {
        return "app://confirm-buy?url=${encodeUrlParam(targetUrl)}&packet=${encodeUrlParam(packetName)}&period=${encodeUrlParam(period)}&price=${encodeUrlParam(price)}"
    }

    private fun packetBuyUrl(packetName: String, period: String, selectedUser: String): String {
        val channel = encodeUrlParam(packetName.ifBlank { "Poland Full HD" })
        val selected = selectedUser.takeIf { it.isNotBlank() }?.let { "&selected_user=${encodeUrlParam(it)}" }.orEmpty()
        return "$baseUrl/packets.php?buy=$period&ch=$channel$selected"
    }

    private fun encodeUrlParam(value: String): String {
        return URLEncoder.encode(value, "UTF-8").replace("+", "%20")
    }

    private fun parseM3u(html: String): List<PageItem> {
        val data = M3uHtmlParser.parse(html)
        val items = mutableListOf<PageItem>()
        val choices = mutableMapOf<String, List<M3uHtmlParser.PlaylistLink>>()
        val rawSelectedUser = pendingM3uUser.ifBlank { data.selectedUser }
        val selectedUser = cleanSelectedUser(rawSelectedUser)
        currentM3uUsers = (listOf(rawSelectedUser) + data.availableUsers).filter { it.isNotBlank() }.distinct()
        currentM3uSelectedUser = selectedUser
        items.add(
            PageItem(
                "Wybrany uzytkownik",
                if (selectedUser.isBlank()) "Nie wykryto wybranego uzytkownika" else "Zaladowano dla: $selectedUser",
                if (data.availableUsers.isEmpty()) "" else "Dostepnych uzytkownikow: ${data.availableUsers.size}"
            )
        )
        data.userKey?.let { key ->
            items.add(PageItem("User Key", "Prawdziwy klucz z ekranu M3U.", "Kliknij szare pole, zeby skopiowac.\n****************", copyText = key))
        }
        data.groups.forEach { group ->
            val title = when (group.type) {
                M3uHtmlParser.PlaylistGroupType.TIVIMATE -> "TiviMate"
                M3uHtmlParser.PlaylistGroupType.SMART_IPTV -> "Smart IPTV"
                M3uHtmlParser.PlaylistGroupType.SS_IPTV -> "SS IPTV"
            }
            val subtitle = when (group.type) {
                M3uHtmlParser.PlaylistGroupType.TIVIMATE -> "Zwykle listy M3U / HLS"
                M3uHtmlParser.PlaylistGroupType.SMART_IPTV -> "Lista dla Smart IPTV"
                M3uHtmlParser.PlaylistGroupType.SS_IPTV -> "Lista dla SS IPTV"
            }
            val logo = when (group.type) {
                M3uHtmlParser.PlaylistGroupType.TIVIMATE -> R.drawable.m3u_tivimate_logo
                M3uHtmlParser.PlaylistGroupType.SMART_IPTV -> R.drawable.m3u_smart_iptv_logo
                M3uHtmlParser.PlaylistGroupType.SS_IPTV -> R.drawable.m3u_ssiptv_logo
            }
            val key = title.lowercase().replace(" ", "_")
            choices[key] = group.links
            val body = group.links.joinToString("\n\n") {
                "CDN ${it.cdn}\n${it.displayPath}\n${it.region}"
            }
            val first = group.links.firstOrNull()
            items.add(
                PageItem(
                    title,
                    subtitle,
                    body,
                    actionUrl = "app://m3u/download/$key",
                    copyText = first?.fullUrl.orEmpty(),
                    actionLabel = "Pobierz",
                    badgeImageRes = logo
                )
            )
        }
        data.epgLinks.firstOrNull()?.let {
            items.add(PageItem("EPG", "Program TV", it.displayPath, actionUrl = it.fullUrl, copyText = it.fullUrl, actionLabel = "Otworz", badgeImageRes = R.drawable.ic_m3u_epg))
        }
        m3uDownloadChoices = choices
        if (items.size == 1) {
            items.add(PageItem("Brak linkow", "Nie udalo sie odczytac linkow M3U dla wybranego uzytkownika."))
        }
        return items
    }

    private fun cleanSelectedUser(value: String): String {
        val clean = value
            .replace("&nbsp;", " ")
            .replace("&#160;", " ")
            .replace(Regex("\\s+"), " ")
            .trim()
        if (clean.contains("protected", true)) {
            return parseDashboardEmail(lastDashboardHtml)
                .ifBlank { parseDashboardUsername(lastDashboardHtml) }
                .ifBlank { clean }
        }
        return clean
    }

    private fun parseDashboardEmail(html: String): String {
        return Regex("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}", RegexOption.IGNORE_CASE)
            .findAll(html)
            .map { it.value.trim() }
            .firstOrNull { !it.contains("protected", true) }
            .orEmpty()
    }

    private fun parseDashboardUsername(html: String): String {
        val candidates = listOf(
            Regex("Hello\\s+([^<\\n]+)", RegexOption.IGNORE_CASE).find(html)?.groupValues?.get(1),
            Regex("user-link[^>]*>(.*?)</a>", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)).find(html)?.groupValues?.get(1),
            Regex("class=[\"'][^\"']*user-name[^\"']*[\"'][^>]*>(.*?)</", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)).find(html)?.groupValues?.get(1)
        )
        return candidates
            .asSequence()
            .filterNotNull()
            .map {
                it.replace("&nbsp;", " ")
                    .replace("&#160;", " ")
                    .replace("&amp;", "&")
                    .replace(Regex("<[^>]+>"), " ")
                    .replace(Regex("\\s+"), " ")
                    .trim()
            }
            .firstOrNull { it.isNotBlank() && !it.contains("protected", true) && !it.contains("@") }
            ?: candidates
                .asSequence()
                .filterNotNull()
                .map {
                    it.replace("&nbsp;", " ")
                        .replace("&#160;", " ")
                        .replace("&amp;", "&")
                        .replace(Regex("<[^>]+>"), " ")
                        .replace(Regex("\\s+"), " ")
                        .trim()
                }
                .firstOrNull { it.contains("@") }
                ?.substringBefore("@")
            .orEmpty()
    }

    private fun resellerAccountItem(account: ResellerHtmlParser.Account): PageItem {
        val encodedLogin = URLEncoder.encode(account.login, "UTF-8")
        val state = if (account.hasActivePackage) {
            listOf(
                "Status: aktywny",
                "Wygasa: ${account.expiry} ${account.days}".trim()
            ).filter { it != "Wygasa:" }.joinToString("\n")
        } else {
            "Status: nieaktywny\nBrak aktywnego pakietu"
        }
        return PageItem(
            account.login,
            account.notice.ifBlank { "Bez notatki" },
            state,
            "/packets.php?selected_user=$encodedLogin",
            actionLabel = "Kup pakiet",
            secondActionUrl = "/tuner_settings.php?selected_user=$encodedLogin",
            secondActionLabel = "Link M3U"
        )
    }

    private fun parseNews(html: String): List<PageItem> {
        val messages = ServerMessagesParser.parse(html)
        if (messages.isEmpty()) {
            return listOf(PageItem("Nie udalo sie odczytac wiadomosci.", "Parser nie znalazl osobnych kart wiadomosci."))
        }
        return messages.map {
            val body = listOf(it.body, listOfNotNull(it.extraLabel, it.extraText).joinToString(": "))
                .filter { part -> part.isNotBlank() }
                .joinToString("\n\n")
            PageItem(
                formatNewsText(it.title).formatNewsLine(false),
                listOf(it.label, normalizePanelDate(it.date, it.title, body)).filter { part -> part.isNotBlank() }.joinToString(" · "),
                formatNewsText(body),
                badgeText = it.label.take(3)
            )
        }
    }

    private fun showBuyConfirmation(actionUrl: String) {
        val uri = Uri.parse(actionUrl)
        val target = uri.getQueryParameter("url").orEmpty()
        val packet = uri.getQueryParameter("packet").orEmpty().ifBlank { "Pakiet" }
        val period = uri.getQueryParameter("period").orEmpty().ifBlank { "okres" }
        val price = uri.getQueryParameter("price").orEmpty().ifBlank { "Cena z portalu" }
        val user = cleanSelectedUser(currentPacketsSelectedUser).ifBlank { "Konto glowne" }
        val activePacket = currentPacketsActivePacket.trim()

        if (activePacket.isNotBlank() && !activePacket.equals(packet, ignoreCase = true)) {
            showModernMessage(
                "Nie mozna wykupic pakietu",
                "Na tym koncie jest juz aktywny pakiet:\n\n" +
                    "$activePacket\n\n" +
                    "Nie mozesz wykupic innego pakietu, dopoki obecny sie nie skonczy. " +
                    "Jesli chcesz anulowac lub zmienic pakiet, skontaktuj sie z supportem PlusX."
            )
            return
        }

        showModernMessage(
            "Potwierdz zakup",
                "Czy na pewno kupic wybrany pakiet?\n\n" +
                    "Pakiet: $packet\n" +
                    "Okres: $period\n" +
                    "Cena: $price\n" +
                    "Uzytkownik: $user",
            positiveText = "Kup",
            negativeText = "Anuluj",
            onPositive = {
                if (target.isNotBlank()) {
                    openPortalPage(target)
                }
            }
        )
    }

    private fun parseTelegramNews(json: String): List<PageItem> {
        val root = JSONObject(json)
        val messages = root.optJSONArray("messages") ?: JSONArray()
        val items = mutableListOf<PageItem>()
        for (index in 0 until messages.length()) {
            val item = messages.optJSONObject(index) ?: continue
            val text = formatNewsText(item.optString("text")).trim()
            if (text.isBlank()) continue
            val lines = text.lines().map { it.trim() }.filter { it.isNotBlank() }
            val title = lines.firstOrNull { line ->
                !line.contains("NOWE WYDARZENIE", true) &&
                    !line.contains("Pocz", true) &&
                    !line.contains("Koniec", true) &&
                    !line.startsWith("ID:", true)
            }?.removeTelegramIcons()?.formatNewsLine(false)?.ifBlank { null }
                ?: lines.firstOrNull()?.removeTelegramIcons()?.formatNewsLine(false)
                ?: "Wiadomosc Telegram"
            val body = lines
                .dropWhile { it == lines.firstOrNull() }
                .joinToString("\n") { it.formatNewsLine(true) }
                .ifBlank { text.formatNewsLine(true) }
            val date = formatIsoDateForDisplay(item.optString("date"))
            items.add(
                PageItem(
                    title = title,
                    subtitle = listOf("TELEGRAM", date).filter { it.isNotBlank() }.joinToString(" · "),
                    value = body,
                    badgeText = "TG"
                )
            )
        }
        return items
    }

    private fun parseEpgEvents(json: String): List<PageItem> {
        val root = JSONObject(json)
        val date = root.optString("date")
        val messages = root.optJSONArray("messages") ?: JSONArray()
        val items = mutableListOf<Pair<Long, PageItem>>()
        val now = System.currentTimeMillis()
        for (index in 0 until messages.length()) {
            val event = messages.optJSONObject(index) ?: continue
            val text = event.optString("text")
            val lines = text.lines().map { it.trim() }.filter { it.isNotBlank() }
            val source = Regex("\\(([^)]+)\\)").find(lines.firstOrNull().orEmpty())?.groupValues?.get(1).orEmpty()
            val title = lines.firstOrNull { it.contains("📺") }
                ?.removeTelegramIcons()
                ?.formatNewsLine(false)
                ?.ifBlank { null }
                ?: "Wydarzenie"
            val channelId = lines.firstOrNull { it.contains("ID:", true) }
                ?.removeTelegramIcons()
                ?.removePrefix("ID:")
                ?.trim()
                .orEmpty()
            val startRaw = lines.firstOrNull { it.contains("Pocz", true) }
                ?.removeTelegramIcons()
                ?.substringAfter(":")
                ?.trim()
                .orEmpty()
            val endRaw = lines.firstOrNull { it.contains("Koniec", true) }
                ?.removeTelegramIcons()
                ?.substringAfter(":")
                ?.trim()
                .orEmpty()
            val startMillis = epgTimestamp(startRaw)
            val endMillis = epgTimestamp(endRaw)
            if (endMillis > 0L && endMillis < now) {
                continue
            }
            val progressText = if (startMillis > 0L && endMillis > 0L && now in startMillis..endMillis) {
                val minute = (((now - startMillis) / 60000L) + 1L).coerceAtLeast(1L)
                "Trwa teraz · ${minute} min"
            } else {
                ""
            }
            val bitrate = lines.firstOrNull { it.contains("Bitrate", true) }
                ?.removeTelegramIcons()
                ?.substringAfter(":")
                ?.trim()
                .orEmpty()
            val start = shortDateTime(startRaw)
            val end = shortDateTime(endRaw)
            val value = listOf(
                source.takeIf { it.isNotBlank() }?.let { "Źródło: $it" },
                channelId.takeIf { it.isNotBlank() }?.let { "Kanał: $it" },
                start.takeIf { it.isNotBlank() }?.let { "Początek: $it" },
                end.takeIf { it.isNotBlank() }?.let { "Koniec: $it" },
                bitrate.takeIf { it.isNotBlank() }?.let { "Bitrate: $it" }
            ).filterNotNull().joinToString("\n")
            items.add(
                startMillis to
                PageItem(
                    title = title,
                    subtitle = listOf("EPG", displayDateFromIsoDate(date)).filter { it.isNotBlank() }.joinToString(" · "),
                    value = value.ifBlank { formatNewsText(text) },
                    badgeText = "EPG",
                    cornerText = progressText
                )
            )
        }
        return items
            .sortedByDescending { it.first }
            .map { it.second }
            .distinctBy { "${it.title}|${it.value.substringAfter("Kanał:", "").substringBefore("\n").trim()}|${it.value.substringAfter("Początek:", "").substringBefore("\n").trim()}" }
    }

    private fun mergeNewsItems(panelItems: List<PageItem>, telegramItems: List<PageItem>): List<PageItem> {
        val result = mutableListOf<PageItem>()
        panelItems.forEach { item ->
            if (!item.title.startsWith("Nie udalo", true)) {
                result.add(item)
            }
        }
        telegramItems.forEach { telegramItem ->
            val duplicate = result.any { existing -> newsLooksSimilar(existing, telegramItem) }
            if (!duplicate) result.add(telegramItem)
        }
        if (result.isEmpty()) {
            return panelItems + telegramItems
        }
        return result.sortedByDescending { newsTimestamp(it) }
    }

    private fun normalizePanelDate(rawDate: String?, title: String, body: String): String {
        val existing = rawDate.orEmpty().trim()
        if (existing.isNotBlank()) {
            return displayDateFromPolishText(existing).ifBlank { existing.lowercase().replaceFirstChar { it.uppercase() } }
        }

        val text = "$title\n$body"
        Regex("(\\d{1,2})\\s+([A-Za-ząćęłńóśźżĄĆĘŁŃÓŚŹŻ]+)\\s+(\\d{4})", RegexOption.IGNORE_CASE)
            .find(text)
            ?.let { return displayDate(it.groupValues[3], polishMonths[it.groupValues[2].lowercase()].orEmpty(), it.groupValues[1]) }

        Regex("(\\d{4})-(\\d{2})-(\\d{2})(?:[ T](\\d{2}:\\d{2}:\\d{2}))?")
            .find(text)
            ?.let {
                return displayDate(it.groupValues[1], it.groupValues[2], it.groupValues[3])
            }

        return ""
    }

    private fun formatIsoDateForDisplay(raw: String): String {
        if (raw.isBlank()) return ""
        return runCatching {
            val iso = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US)
            val date = iso.parse(raw) ?: return@runCatching ""
            val out = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            out.timeZone = TimeZone.getTimeZone("Europe/Warsaw")
            displayDateFromIsoDate(out.format(date))
        }.getOrElse {
            displayDateFromIsoDate(raw.substringBefore("T").substringBefore(" "))
        }
    }

    private fun displayDateFromIsoDate(value: String): String {
        return Regex("(\\d{4})-(\\d{2})-(\\d{2})").find(value)?.let {
            displayDate(it.groupValues[1], it.groupValues[2], it.groupValues[3])
        }.orEmpty()
    }

    private fun displayDateFromPolishText(value: String): String {
        return Regex("(\\d{1,2})\\s+([A-Za-ząćęłńóśźżĄĆĘŁŃÓŚŹŻ]+)\\s+(\\d{4})", RegexOption.IGNORE_CASE)
            .find(value)
            ?.let {
                displayDate(it.groupValues[3], polishMonths[it.groupValues[2].lowercase()].orEmpty(), it.groupValues[1])
            }
            .orEmpty()
    }

    private fun displayDate(year: String, month: String, day: String): String {
        val cleanMonth = month.padStart(2, '0')
        val monthName = displayMonths[cleanMonth] ?: cleanMonth
        return "${day.toIntOrNull() ?: day} $monthName $year"
    }

    private fun shortDateTime(value: String): String {
        Regex("(\\d{4}-\\d{2}-\\d{2})\\s+(\\d{2}:\\d{2})(?::\\d{2})?")
            .find(value)
            ?.let {
                val parts = it.groupValues[1].split("-")
                return "${parts[2]}-${parts[1]}-${parts[0]} ${it.groupValues[2]}"
            }
        Regex("(\\d{4})(\\d{2})(\\d{2})\\s+(\\d{2}:\\d{2})(?::\\d{2})?")
            .find(value)
            ?.let { return "${it.groupValues[3]}-${it.groupValues[2]}-${it.groupValues[1]} ${it.groupValues[4]}" }
        return value
    }

    private fun epgTimestamp(value: String): Long {
        Regex("(\\d{4}-\\d{2}-\\d{2})\\s+(\\d{2}:\\d{2})(?::(\\d{2}))?")
            .find(value)
            ?.let {
                val seconds = it.groupValues.getOrNull(3)?.ifBlank { "00" } ?: "00"
                parseDateMillis("yyyy-MM-dd HH:mm:ss", "${it.groupValues[1]} ${it.groupValues[2]}:$seconds")?.let { millis -> return millis }
            }
        Regex("(\\d{4})(\\d{2})(\\d{2})\\s+(\\d{2}:\\d{2})(?::(\\d{2}))?")
            .find(value)
            ?.let {
                val seconds = it.groupValues.getOrNull(5)?.ifBlank { "00" } ?: "00"
                parseDateMillis("yyyy-MM-dd HH:mm:ss", "${it.groupValues[1]}-${it.groupValues[2]}-${it.groupValues[3]} ${it.groupValues[4]}:$seconds")?.let { millis -> return millis }
            }
        return 0L
    }

    private fun newsTimestamp(item: PageItem): Long {
        val datePart = item.subtitle.substringAfter("·", item.subtitle).trim()
        if (datePart.isBlank()) return 0L

        parseDateMillis("yyyy-MM-dd HH:mm:ss", datePart)?.let { return it }
        parseDateMillis("yyyy-MM-dd HH:mm", datePart)?.let { return it }

        Regex("(\\d{1,2})\\s+([A-Za-ząćęłńóśźżĄĆĘŁŃÓŚŹŻ]+)\\s+(\\d{4})", RegexOption.IGNORE_CASE)
            .find(datePart)
            ?.let {
                val day = it.groupValues[1].padStart(2, '0')
                val month = polishMonths[it.groupValues[2].lowercase()] ?: return@let
                val normalized = "${it.groupValues[3]}-$month-$day 00:00:00"
                parseDateMillis("yyyy-MM-dd HH:mm:ss", normalized)?.let { millis -> return millis }
            }
        return 0L
    }

    private fun parseDateMillis(pattern: String, value: String): Long? {
        return runCatching {
            val format = SimpleDateFormat(pattern, Locale.US)
            format.timeZone = TimeZone.getTimeZone("Europe/Warsaw")
            format.parse(value)?.time
        }.getOrNull()
    }

    private fun newsLooksSimilar(left: PageItem, right: PageItem): Boolean {
        val leftNorm = newsComparableText(left)
        val rightNorm = newsComparableText(right)
        if (leftNorm.length < 18 || rightNorm.length < 18) return false
        if (leftNorm.contains(rightNorm) || rightNorm.contains(leftNorm)) return true

        val leftWords = leftNorm.split(" ").filter { it.length >= 4 }.toSet()
        val rightWords = rightNorm.split(" ").filter { it.length >= 4 }.toSet()
        if (leftWords.isEmpty() || rightWords.isEmpty()) return false
        val common = leftWords.intersect(rightWords).size
        val smaller = minOf(leftWords.size, rightWords.size)
        return common >= 4 && common.toDouble() / smaller.toDouble() >= 0.68
    }

    private fun newsComparableText(item: PageItem): String {
        return "${item.title} ${item.value}"
            .removeTelegramIcons()
            .lowercase()
            .replace("ą", "a")
            .replace("ć", "c")
            .replace("ę", "e")
            .replace("ł", "l")
            .replace("ń", "n")
            .replace("ó", "o")
            .replace("ś", "s")
            .replace("ź", "z")
            .replace("ż", "z")
            .replace(Regex("[^a-z0-9 ]+"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private fun String.removeTelegramIcons(): String {
        return replace(Regex("[\\uD83C-\\uDBFF\\uDC00-\\uDFFF]"), "")
            .replace("⚡", "")
            .replace("️", "")
            .replace("🟢", "")
            .replace("📺", "")
            .replace("🆔", "")
            .replace("📅", "")
            .replace("🏁", "")
            .replace("📶", "")
            .replace("⏱", "")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private fun formatNewsText(raw: String): String {
        return raw
            .replace("&bull;", " • ")
            .replace("&amp;", "&")
            .replace("&nbsp;", " ")
            .replace("&#160;", " ")
            .replace("⚡️", "\n⚡ ")
            .replace("⚡", "\n⚡ ")
            .replace("✅", "\n✅")
            .replace(Regex("(?<=[a-ząćęłńóśźż])(?=[A-ZĄĆĘŁŃÓŚŹŻ])"), " ")
            .replace(Regex(",(?=\\S)"), ", ")
            .replace(Regex("\\s+([,.])"), "$1")
            .replace(Regex("(?i)\\b(Fix|Fixed|Promotion|Important|Correct|Changed|Automatic|Some channels|There will|But some|Everything|We apologize|start|finish)\\b"), "\n$1")
            .lines()
            .joinToString("\n") { line -> line.trim() }
            .replace(Regex("\\n{3,}"), "\n\n")
            .trim()
    }

    private fun String.formatNewsLine(listMode: Boolean): String {
        val text = this
            .replace("&bull;", " • ")
            .replace("&amp;", "&")
            .replace(Regex(",(?=\\S)"), ", ")
            .replace(Regex("\\s+([,.])"), "$1")
            .replace(Regex("^⚡\\s*"), "⚡ ")
            .trim()

        if (!listMode) {
            return text.replace(Regex("\\s+"), " ")
        }

        val parts = when {
            text.count { it == ',' } >= 2 -> text.split(",")
            text.contains(" • ") -> text.split(" • ")
            else -> emptyList()
        }.map { it.trim() }.filter { it.isNotBlank() }

        return if (parts.size >= 2) {
            parts.joinToString("\n") {
                "- ${it.removePrefix("⚡").trim()}"
            }
        } else {
            text.replace(Regex("\\s+"), " ")
        }
    }

    private fun cleanText(value: String): String {
        return value
            .replace("&euro;", "EUR")
            .replace("Ă˘â€šÂ¬", "EUR")
            .replace("&nbsp;", " ")
            .replace("&amp;", "&")
            .replace("&#160;", " ")
            .replace(Regex("<[^>]+>"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private fun settingsItems(): List<PageItem> {
        if (lastSettingsItems.isEmpty()) return parseProfile("")
        return lastSettingsItems.map {
            if (it.title == "Wyglad aplikacji") {
                it.copy(subtitle = "Aktualnie: ${if (darkMode) "tryb ciemny" else "tryb jasny"}")
            } else {
                it
            }
        }
    }

    private fun diagnosticsItems(): List<PageItem> {
        return listOf(
            PageItem(
                "Przeslij dane diagnostyczne",
                "Wybierz zakres. Opis problemu nie jest wymagany, ale email kontaktowy jest potrzebny.",
                actionUrl = "app://diagnostics/basic",
                actionLabel = "Podstawowe",
                secondActionUrl = "app://diagnostics/advanced",
                secondActionLabel = "Zaawansowane"
            ),
            PageItem(
                "Co zawiera diagnostyka?",
                "Podstawowe: data, godzina, wersja aplikacji, ekran, motyw, wybrani uzytkownicy i email kontaktowy.\n\nZaawansowane: to samo plus oczyszczona zawartosc wybranej zakladki. Nie da sie wyslac diagnostyki Doladuj konto ani Ustawien."
            )
        )
    }

    private fun previewDiagnostic(content: String): String {
        val clean = content.replace(Regex("\\s+"), " ").trim()
        return clean.take(900) + if (clean.length > 900) "\n\n...ucięto podglad, pelna tresc kopiuje sie do schowka." else ""
    }

    private fun showDiagnosticsForm(advanced: Boolean, section: String = "") {
        val form = LinearLayout(this)
        form.orientation = LinearLayout.VERTICAL
        form.setPadding(dp(18), dp(8), dp(18), 0)

        val email = EditText(this)
        email.hint = "Email kontaktowy"
        email.inputType = android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        email.setSingleLine(true)
        styleDialogInput(email)
        form.addView(email, LinearLayout.LayoutParams(-1, -2))

        val problem = EditText(this)
        problem.hint = "Opis problemu (opcjonalnie)"
        problem.minLines = 3
        problem.maxLines = 5
        problem.gravity = Gravity.TOP
        problem.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
        styleDialogInput(problem)
        form.addView(problem, LinearLayout.LayoutParams(-1, -2).apply { setMargins(0, dp(10), 0, 0) })

        lateinit var dialog: Dialog
        dialog = showModernMessage(
            if (advanced) "Diagnostyka zaawansowana" else "Diagnostyka podstawowa",
                if (advanced) "Zakladka: $section\nPodaj email kontaktowy. Opis mozesz zostawic pusty."
                else "Podaj email kontaktowy. Opis mozesz zostawic pusty.",
            positiveText = "Przygotuj",
            negativeText = "Anuluj",
            contentView = form,
            dismissOnPositive = false,
            onPositive = {
                val contactEmail = email.text.toString().trim()
                if (!isValidContactEmail(contactEmail)) {
                    email.error = "Email musi miec wiecej niz 3 znaki i zawierac @"
                    Toast.makeText(this, "Podaj poprawny email kontaktowy", Toast.LENGTH_LONG).show()
                    return@showModernMessage
                }
                val problemText = problem.text.toString().trim().ifBlank { "Nie podano" }
                if (advanced) {
                    sendAdvancedDiagnostics(section, contactEmail, problemText)
                } else {
                    submitDiagnostics(
                        "PlusX Mobile - diagnostyka podstawowa",
                        diagnosticsSummary(contactEmail, problemText, null),
                        contactEmail,
                        problemText,
                        "podstawowe",
                        null
                    )
                }
                dialog.dismiss()
            }
        )
    }

    private fun showAdvancedDiagnosticsChoice() {
        val labels = listOf("Dashboard", "Wiadomosci", "Programy na dzis", "Pakiety", "Reseller Panel", "Linki M3U")
        showModernOptions("Z czym jest problem?", labels) { index ->
                showDiagnosticsForm(true, labels[index])
        }
    }

    private fun sendAdvancedDiagnostics(section: String, contactEmail: String, problemText: String) {
        val snapshots = diagnosticSnapshotsFor(section)
        if (snapshots.isEmpty()) {
            Toast.makeText(this, "Brak zapisanej zawartosci dla: $section. Wejdz najpierw w te zakladke.", Toast.LENGTH_LONG).show()
            return
        }

        val report = buildString {
            appendLine(diagnosticsSummary(contactEmail, problemText, section))
            appendLine()
            appendLine("===== DANE ZAKLADKI: $section =====")
            snapshots.forEachIndexed { index, (name, content) ->
                appendLine()
                appendLine("----- ${index + 1}. $name -----")
                appendLine(sanitizeDiagnosticContent(content, contactEmail))
            }
        }
        submitDiagnostics("PlusX Mobile - diagnostyka zaawansowana ($section)", report, contactEmail, problemText, "zaawansowane", section)
    }

    private fun diagnosticsSummary(contactEmail: String, problemText: String, section: String?): String {
        val now = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale("pl", "PL")).format(java.util.Date())
        return buildString {
            appendLine("PlusX Mobile - diagnostyka")
            appendLine("Data: $now")
            appendLine("Email kontaktowy: $contactEmail")
            appendLine("Opis problemu: $problemText")
            appendLine("Zakres: ${section ?: "Podstawowy"}")
            appendLine("Ekran aplikacji: $currentScreen")
            appendLine("Motyw: ${if (darkMode) "ciemny" else "jasny"}")
            appendLine("Wersja aplikacji: ${appVersionLabel()}")
            appendLine("Producent telefonu: ${Build.MANUFACTURER.ifBlank { "brak danych" }}")
            appendLine("Model telefonu: ${Build.MODEL.ifBlank { "brak danych" }}")
            appendLine("Urzadzenie: ${Build.DEVICE.ifBlank { "brak danych" }}")
            appendLine("Android: ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})")
            appendLine("Rozdzielczosc: ${screenResolutionLabel()}")
            appendLine("Gestosc ekranu: ${screenDensityLabel()}")
            appendLine("Orientacja: ${orientationLabel()}")
            appendLine("Jezyk systemu: ${Locale.getDefault().toLanguageTag()}")
            appendLine("Wybrany uzytkownik M3U: ${currentM3uSelectedUser.ifBlank { "brak" }}")
            appendLine("Wybrany uzytkownik Pakietow: ${cleanSelectedUser(currentPacketsSelectedUser).ifBlank { "konto glowne" }}")
            appendLine("Liczba uzytkownikow M3U: ${currentM3uUsers.size}")
            appendLine("Liczba uzytkownikow Pakietow: ${currentPacketsUsers.size}")
            appendLine("Liczba zapisanych snapshotow: ${synchronized(diagnosticsSnapshots) { diagnosticsSnapshots.size }}")
        }
    }

    private fun diagnosticSnapshotsFor(section: String): List<Pair<String, String>> {
        val snapshots = synchronized(diagnosticsSnapshots) { diagnosticsSnapshots.toList() }
        val filtered = when (section) {
            "Dashboard" -> snapshots.filter { it.first.contains("index.php", ignoreCase = true) }
            "Wiadomosci" -> snapshots.filter {
                it.first.contains("index.php", ignoreCase = true) || it.first.contains("wiadomosci", ignoreCase = true)
            }
            "Programy na dzis" -> snapshots.filter { it.first.contains("epgevent", ignoreCase = true) }
            "Pakiety" -> snapshots.filter { it.first.contains("packets.php", ignoreCase = true) }
            "Reseller Panel" -> snapshots.filter { it.first.contains("dealer.php", ignoreCase = true) }
            "Linki M3U" -> snapshots.filter { it.first.contains("tuner_settings.php", ignoreCase = true) }
            else -> emptyList()
        }
        if (section == "Dashboard" && filtered.isEmpty() && lastDashboardHtml.isNotBlank()) {
            return listOf("HTML index.php" to lastDashboardHtml)
        }
        return filtered
    }

    private fun sanitizeDiagnosticContent(content: String, contactEmail: String): String {
        return content
            .replace(Regex("(?i)(ssn=)[^;\\s\"'&<]+"), "$1[usunieto]")
            .replace(Regex("(?i)(Authorization:\\s*Bearer\\s+)[A-Za-z0-9._\\-]+"), "$1[usunieto]")
            .replace(Regex("(?i)(password|passwd|token|secret|api_hash)([\"'\\s:=]+)([^\"'\\s<>&]+)"), "$1$2[usunieto]")
            .replace(Regex("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b"), "[ip-usuniete]")
            .replace(Regex("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}", RegexOption.IGNORE_CASE)) {
                if (it.value.equals(contactEmail, ignoreCase = true)) it.value else "[email-usuniety]"
            }
    }

    private fun shareDiagnostics(title: String, content: String) {
        copyToClipboard(content)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, content)
        }
        startActivity(Intent.createChooser(intent, "Przeslij diagnostyke"))
    }

    private fun submitDiagnostics(
        title: String,
        content: String,
        contactEmail: String,
        problemText: String,
        scope: String,
        section: String?
    ) {
        Toast.makeText(this, "Wysylanie diagnostyki...", Toast.LENGTH_SHORT).show()
        Thread {
            val result = runCatching {
                val payload = JSONObject()
                    .put("title", title)
                    .put("contact_email", contactEmail)
                    .put("problem", problemText)
                    .put("scope", scope)
                    .put("section", section ?: JSONObject.NULL)
                    .put("report", content)
                    .put("app_version", appVersionLabel())
                    .put("screen", currentScreen)
                    .put("theme", if (darkMode) "ciemny" else "jasny")
                    .put("device_manufacturer", Build.MANUFACTURER)
                    .put("device_model", Build.MODEL)
                    .put("device_name", Build.DEVICE)
                    .put("android_version", "${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})")
                    .put("screen_resolution", screenResolutionLabel())
                    .put("screen_density", screenDensityLabel())
                    .put("orientation", orientationLabel())
                    .put("system_locale", Locale.getDefault().toLanguageTag())
                postJson(diagnosticsSubmitUrl, telegramBackendToken, payload.toString())
            }
            runOnUiThread {
                result.onSuccess {
                    copyToClipboard(content)
                    Toast.makeText(this, "Diagnostyka wyslana. Kopia jest tez w schowku.", Toast.LENGTH_LONG).show()
                }.onFailure {
                    Toast.makeText(this, "Backend nie odpowiedzial. Otwieram awaryjne wysylanie.", Toast.LENGTH_LONG).show()
                    shareDiagnostics(title, content)
                }
            }
        }.start()
    }

    private fun postJson(address: String, token: String, body: String): String {
        val connection = URL(address).openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 PlusXMobile Android")
        connection.setRequestProperty("Authorization", "Bearer $token")
        connection.setRequestProperty("Content-Type", "application/json; charset=utf-8")
        connection.setRequestProperty("Accept", "application/json")
        connection.doOutput = true
        connection.connectTimeout = 30000
        connection.readTimeout = 60000
        connection.outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
        val code = connection.responseCode
        val stream = if (code in 200..299) connection.inputStream else connection.errorStream
        val text = stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
        if (code !in 200..299) error("HTTP $code: $text")
        return text
    }

    private fun isValidContactEmail(value: String): Boolean {
        return value.length > 3 && value.contains("@") && Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$").matches(value)
    }

    private fun isRemoteVersionNewer(remote: String, current: String): Boolean {
        val remoteParts = versionParts(remote)
        val currentParts = versionParts(current)
        val max = maxOf(remoteParts.size, currentParts.size)
        for (index in 0 until max) {
            val left = remoteParts.getOrElse(index) { 0 }
            val right = currentParts.getOrElse(index) { 0 }
            if (left > right) return true
            if (left < right) return false
        }
        return false
    }

    private fun versionParts(value: String): List<Int> {
        return value
            .trim()
            .removePrefix("v")
            .split(Regex("[^0-9]+"))
            .filter { it.isNotBlank() }
            .map { it.toIntOrNull() ?: 0 }
    }

    private fun String.cleanMarkdownForApp(): String {
        return replace("\r\n", "\n")
            .replace(Regex("^#{1,6}\\s*", RegexOption.MULTILINE), "")
            .replace(Regex("`([^`]+)`"), "$1")
            .replace(Regex("\\*\\*([^*]+)\\*\\*"), "$1")
            .replace(Regex("\\[([^]]+)]\\(([^)]+)\\)"), "$1")
            .replace(Regex("\\n{3,}"), "\n\n")
            .trim()
    }

    private fun appVersionLabel(): String {
        return runCatching {
            val info = packageManager.getPackageInfo(packageName, 0)
            "${info.versionName} (${info.longVersionCode})\nBuild: ${BuildConfig.BUILD_TYPE.uppercase(Locale.US)}"
        }.getOrElse { "brak danych" }
    }

    private fun appVersionName(): String {
        return runCatching {
            packageManager.getPackageInfo(packageName, 0).versionName.orEmpty()
        }.getOrElse { "" }
    }

    private fun screenResolutionLabel(): String {
        val metrics = resources.displayMetrics
        return "${metrics.widthPixels}x${metrics.heightPixels}px"
    }

    private fun screenDensityLabel(): String {
        val metrics = resources.displayMetrics
        return "${metrics.densityDpi} dpi / ${String.format(Locale.US, "%.2f", metrics.density)}x"
    }

    private fun orientationLabel(): String {
        return when (resources.configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> "pozioma"
            Configuration.ORIENTATION_PORTRAIT -> "pionowa"
            else -> "nieznana"
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun openPortalPage(url: String) {
        root.removeAllViews()
        currentScreen = "web"
        currentWebView = null

        val container = LinearLayout(this)
        container.orientation = LinearLayout.VERTICAL

        val back = Button(this)
        back.text = "< Wroc"
        back.setAllCaps(false)
        back.setOnClickListener {
            fetchDashboard()
        }
        container.addView(back, LinearLayout.LayoutParams(-1, dp(48)))

        val webView = WebView(this)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = true
        webView.webViewClient = WebViewClient()
        currentWebView = webView
        container.addView(webView, LinearLayout.LayoutParams(-1, 0, 1f))

        root.addView(container)
        webView.loadUrl(url, mapOf("Cookie" to cookieHeader))
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }

    override fun onBackPressed() {
        val webView = currentWebView
        if ((currentScreen == "login" || currentScreen == "web") && webView?.canGoBack() == true) {
            webView.goBack()
            return
        }

        when (currentScreen) {
            "native", "web" -> {
                fetchDashboard()
            }
            else -> {
                val now = System.currentTimeMillis()
                if (now - lastBackPress < 1600) {
                    finish()
                } else {
                    lastBackPress = now
                    Toast.makeText(this, "Nacisnij ponownie, aby wyjsc", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

