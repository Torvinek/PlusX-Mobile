package pl.torvinek.plusxmobile

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class PlusxFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification?.title
            ?: message.data["title"]
            ?: "PlusX Mobile"
        val body = message.notification?.body
            ?: message.data["body"]
            ?: "Masz nowe powiadomienie."
        showPushNotification(title, body)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        registerPushToken(token)
    }

    private fun registerPushToken(token: String) {
        if (token.isBlank() || AppConfig.backendToken.isBlank()) return
        Thread {
            runCatching {
                val payload = JSONObject()
                    .put("token", token)
                    .put("app_version", "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
                    .put("device_manufacturer", Build.MANUFACTURER)
                    .put("device_model", Build.MODEL)
                    .put("android_version", Build.VERSION.RELEASE)
                    .toString()
                val connection = URL("${AppConfig.backendBaseUrl}/telegram/plusx/push/register").openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.doOutput = true
                connection.connectTimeout = 15000
                connection.readTimeout = 15000
                connection.setRequestProperty("Authorization", "Bearer ${AppConfig.backendToken}")
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                connection.outputStream.use { it.write(payload.toByteArray(Charsets.UTF_8)) }
                connection.inputStream.close()
            }
        }.start()
    }

    private fun showPushNotification(title: String, body: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val manager = getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(CHANNEL_ID, "PlusX Mobile", NotificationManager.IMPORTANCE_HIGH)
            )
        }
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            4301,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = Notification.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(Notification.PRIORITY_HIGH)
            .build()
        manager.notify((System.currentTimeMillis() % Int.MAX_VALUE).toInt(), notification)
    }

    private companion object {
        const val CHANNEL_ID = "plusx_alerts_v2"
    }
}
