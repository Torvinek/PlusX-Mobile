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
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URI

class NewsNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            schedule(context)
            return
        }

        val pendingResult = goAsync()
        Thread {
            runCatching {
                checkLatestNews(context.applicationContext)
                checkLatestRelease(context.applicationContext)
            }
            pendingResult.finish()
        }.start()
    }

    private fun checkLatestNews(context: Context) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val json = getJson(NEWS_URL, BACKEND_TOKEN)
        val root = JSONObject(json)
        val messages = root.optJSONArray("messages") ?: return
        if (messages.length() == 0) return

        val latest = messages.optJSONObject(messages.length() - 1) ?: messages.optJSONObject(0) ?: return
        val latestId = latest.optLong("id", 0L)
        if (latestId <= 0L) return

        val previousId = prefs.getLong(KEY_LAST_NEWS_ID, 0L)
        prefs.edit().putLong(KEY_LAST_NEWS_ID, latestId).apply()

        if (previousId == 0L || latestId == previousId) return
        showNewsNotification(context)
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
        if (previousTag.isBlank() || previousTag == latestTag) return
        showUpdateNotification(context)
    }

    private fun showNewsNotification(context: Context) {
        showNotification(
            context,
            3001,
            "Nowa wiadomość PlusX",
            "Zaloguj się do aplikacji żeby zobaczyć"
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
            .setPriority(Notification.PRIORITY_DEFAULT)
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
        private const val CHANNEL_ID = "plusx_news"
        private const val ALARM_ACTION = "pl.torvinek.plusxmobile.CHECK_NEWS"
        private const val INTERVAL_MS = 15L * 60L * 1000L
        private val NEWS_URL = "${AppConfig.backendBaseUrl}/telegram/plusx/wiadomosci?limit=1"
        private val BACKEND_TOKEN = AppConfig.backendToken
        private const val GITHUB_RELEASE_URL = "https://api.github.com/repos/Torvinek/PlusX-Mobile/releases/latest"

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
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + 60_000L,
                INTERVAL_MS,
                pendingIntent
            )
        }

        fun ensureChannel(context: Context) {
            val manager = context.getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Wiadomości PlusX",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Powiadomienia o nowych wiadomościach PlusX"
            }
            manager.createNotificationChannel(channel)
        }
    }
}
