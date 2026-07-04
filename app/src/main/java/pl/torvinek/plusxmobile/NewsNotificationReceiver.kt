package pl.torvinek.plusxmobile

import android.Manifest
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class NewsNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val appContext = context.applicationContext
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            schedule(appContext)
            return
        }

        val pendingResult = goAsync()
        Thread {
            runCatching {
                checkLatestNews(appContext)
                checkLatestRelease(appContext)
                checkStoredResellerExpiry(appContext)
            }
            schedule(appContext)
            pendingResult.finish()
        }.start()
    }

    private fun checkLatestNews(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val json = getJson(NEWS_URL, BACKEND_TOKEN)
        val root = JSONObject(json)
        val messages = root.optJSONArray("messages") ?: return
        if (messages.length() == 0) return

        var latestId = 0L
        for (index in 0 until messages.length()) {
            val id = messages.optJSONObject(index)?.optLong("id", 0L) ?: 0L
            if (id > latestId) latestId = id
        }
        if (latestId <= 0L) return

        val previousId = prefs.getLong(KEY_LAST_NEWS_ID, 0L)
        prefs.edit().putLong(KEY_LAST_NEWS_ID, latestId).apply()

        if (previousId != 0L && latestId > previousId) {
            showNewsNotification(context)
        }
    }

    private fun checkLatestRelease(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val json = getJson(GITHUB_RELEASE_URL, null)
        val root = JSONObject(json)
        val latestTag = root.optString("tag_name").trim().ifBlank { return }
        val currentVersion = appVersionName(context)
        if (!isRemoteVersionNewer(latestTag, currentVersion)) {
            prefs.edit().putString(KEY_LAST_RELEASE_TAG, latestTag).apply()
            return
        }

        val previousTag = prefs.getString(KEY_LAST_RELEASE_TAG, "").orEmpty()
        prefs.edit().putString(KEY_LAST_RELEASE_TAG, latestTag).apply()
        if (previousTag != latestTag) {
            showUpdateNotification(context)
        }
    }

    private fun showNewsNotification(context: Context) {
        showNotification(
            context,
            3001,
            "Nowa wiadomoĹ›Ä‡ PlusX",
            "Zaloguj siÄ™ do aplikacji ĹĽeby zobaczyÄ‡"
        )
    }

    private fun showUpdateNotification(context: Context) {
        showNotification(
            context,
            3002,
            "Nowa wersja Aplikacji!",
            "Zaloguj sie do aplikacji zeby byc z nowosciami!"
        )
    }

    private fun showNotification(context: Context, id: Int, title: String, text: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        ensureChannel(context)
        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            2001,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = Notification.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(text)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(Notification.PRIORITY_HIGH)
            .build()

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.notify(id, notification)
    }

    private fun getJson(address: String, token: String?): String {
        val url = if (token.isNullOrBlank()) {
            URI(address).toURL()
        } else {
            AppConfig.requireBackendUrl(address)
        }
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("User-Agent", "PlusXMobile Android")
        connection.setRequestProperty("Accept", "application/json")
        if (!token.isNullOrBlank()) {
            connection.setRequestProperty("Authorization", "Bearer $token")
        }
        connection.connectTimeout = 30000
        connection.readTimeout = 60000
        return connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
    }

    private fun appVersionName(context: Context): String {
        return runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName.orEmpty()
        }.getOrDefault("")
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

    companion object {
        private const val PREFS = "plusx_news_notifications"
        private const val KEY_LAST_NEWS_ID = "last_news_id"
        private const val KEY_LAST_RELEASE_TAG = "last_release_tag"
        private const val KEY_RESELLER_EXPIRY_SNAPSHOT = "reseller_expiry_snapshot"
        private const val CHANNEL_ID = "plusx_alerts_v2"
        private const val ALARM_ACTION = "pl.torvinek.plusxmobile.CHECK_NEWS"
        private const val INTERVAL_MS = 15L * 60L * 1000L
        private val NEWS_URL = "${AppConfig.backendBaseUrl}/telegram/plusx/wiadomosci?limit=1"
        private val BACKEND_TOKEN = AppConfig.backendToken
        private const val GITHUB_RELEASE_URL = "https://api.github.com/repos/Torvinek/PlusX-Mobile/releases/latest"

        fun checkNow(context: Context) {
            ensureChannel(context)
            val intent = Intent(context, NewsNotificationReceiver::class.java).apply {
                action = ALARM_ACTION
            }
            context.sendBroadcast(intent)
        }

        fun checkResellerExpiry(context: Context, accounts: List<ResellerHtmlParser.Account>) {
            ensureChannel(context)
            saveResellerExpirySnapshot(context, accounts)
            val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            accounts
                .filter { it.hasActivePackage }
                .mapNotNull { account ->
                    val daysLeft = resellerDaysLeft(account.days) ?: daysLeftFromExpiry(account.expiry) ?: return@mapNotNull null
                    val level = when (daysLeft) {
                        3 -> "red"
                        7 -> "yellow"
                        else -> return@mapNotNull null
                    }
                    ExpiryAlert(account, daysLeft, level)
                }
                .sortedBy { it.daysLeft }
                .forEach { alert ->
                    val key = "expiry_${alert.level}_${alert.account.login}_${alert.account.expiry}"
                    if (prefs.getBoolean(key, false)) return@forEach
                    if (showResellerExpiryNotification(context, alert.account.login, alert.daysLeft, alert.level)) {
                        prefs.edit().putBoolean(key, true).apply()
                    }
                }
        }

        private fun checkStoredResellerExpiry(context: Context) {
            val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            val raw = prefs.getString(KEY_RESELLER_EXPIRY_SNAPSHOT, "").orEmpty()
            if (raw.isBlank()) return
            val accounts = runCatching {
                val items = JSONArray(raw)
                (0 until items.length()).mapNotNull { index ->
                    val item = items.optJSONObject(index) ?: return@mapNotNull null
                    val login = item.optString("login").trim()
                    val expiry = item.optString("expiry").trim()
                    if (login.isBlank() || expiry.isBlank()) return@mapNotNull null
                    login to expiry
                }
            }.getOrDefault(emptyList())

            accounts.forEach { (login, expiry) ->
                val daysLeft = daysLeftFromExpiry(expiry) ?: return@forEach
                val level = when (daysLeft) {
                    3 -> "red"
                    7 -> "yellow"
                    else -> return@forEach
                }
                val key = "expiry_${level}_${login}_${expiry}"
                if (prefs.getBoolean(key, false)) return@forEach
                if (showResellerExpiryNotification(context, login, daysLeft, level)) {
                    prefs.edit().putBoolean(key, true).apply()
                }
            }
        }

        private fun saveResellerExpirySnapshot(context: Context, accounts: List<ResellerHtmlParser.Account>) {
            val items = JSONArray()
            accounts
                .filter { it.hasActivePackage && it.login.isNotBlank() && it.expiry.isNotBlank() }
                .forEach { account ->
                    items.put(
                        JSONObject()
                            .put("login", account.login)
                            .put("expiry", account.expiry)
                    )
                }
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_RESELLER_EXPIRY_SNAPSHOT, items.toString())
                .apply()
        }

        fun schedule(context: Context) {
            ensureChannel(context)
            val intent = Intent(context, NewsNotificationReceiver::class.java).apply {
                action = ALARM_ACTION
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                2002,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val alarmManager = context.getSystemService(AlarmManager::class.java)
            alarmManager.cancel(pendingIntent)
            val triggerAt = System.currentTimeMillis() + INTERVAL_MS
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
            }
        }

        fun ensureChannel(context: Context) {
            val manager = context.getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Wiadomosci PlusX",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Powiadomienia o nowych wiadomosciach PlusX"
            }
            manager.createNotificationChannel(channel)
        }

        private data class ExpiryAlert(
            val account: ResellerHtmlParser.Account,
            val daysLeft: Int,
            val level: String
        )

        private fun resellerDaysLeft(days: String): Int? {
            return Regex("(\\d+)").find(days)?.groupValues?.get(1)?.toIntOrNull()
        }

        private fun daysLeftFromExpiry(expiry: String): Int? {
            val date = runCatching {
                LocalDate.parse(expiry.trim(), DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            }.getOrNull() ?: return null
            return ChronoUnit.DAYS.between(LocalDate.now(), date).toInt()
        }

        private fun showResellerExpiryNotification(context: Context, login: String, daysLeft: Int, level: String): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }

            val title = if (level == "red") {
                "Pakiet wygasa za 3 dni"
            } else {
                "Pakiet wygasa za 7 dni"
            }
            val dayWord = when (daysLeft) {
                1 -> "dzien"
                else -> "dni"
            }
            val text = "Na użytkowniku $login zostało tylko $daysLeft $dayWord pakietu."
            val openIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                2301 + daysLeft,
                openIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val notification = Notification.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .build()

            val stableId = 4000 + (login.hashCode() and 0x0FFF) + if (level == "red") 10000 else 0
            context.getSystemService(NotificationManager::class.java).notify(stableId, notification)
            return true
        }

        private fun hidden(vararg values: Int): String {
            val key = 0x5A
            return values.map { (it xor key).toChar() }.joinToString("")
        }
    }
}


