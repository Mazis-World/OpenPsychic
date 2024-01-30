package io.getmadd.openpsychic

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.getmadd.openpsychic.activity.MainActivity
import java.util.Random

class MessagingService : FirebaseMessagingService() {

    override fun onCreate() {
        super.onCreate()
        initFirebase()
    }

    private fun initFirebase() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                val msg = getString(R.string.msg_token_fmt, token)
                Log.d(TAG, msg)
                // Consider using a different mechanism to communicate the token to the user
            } else {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
            }
        })
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        // If needed, send the FCM registration token to your app server.
        // sendRegistrationToServer(token)
    }
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")

        // Check if the message contains a notification payload
        remoteMessage.notification?.let {
            // Handle notification here
                showNotification(it.title, it.body)


            // Check if the app is in the terminated state and launched from the notification
            if (remoteMessage.data["click_action"] == "OPEN_ACTIVITY_XYZ") {
                // Perform custom handling, such as navigating to a specific activity
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
        }
    }

    private fun showNotification(title: String?, body: String?) {
        val channelId = "default_channel"
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Default Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.op_logo) // Make sure this is a valid drawable resource
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // Use a unique notification ID for each notification
        val notificationId = System.currentTimeMillis().toInt() + Random().nextInt()
        notificationManager.notify(notificationId, builder.build())
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}
