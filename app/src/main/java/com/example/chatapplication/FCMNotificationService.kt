package com.example.chatapplication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.example.chatapplication.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener

class FCMNotificationService : FirebaseMessagingService() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate() {
        super.onCreate()
        mAuth = FirebaseAuth.getInstance()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Check if message contains a notification payload.
        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
//            Log.d(TAG, "Message data Body: ${remoteMessage.data["userName"]}")

            val currentUser = mAuth.currentUser

            Log.d(TAG, "Message data Body: $currentUser")
//                val userId = currentUser?.uid
//                val dbRef = FirebaseDatabase.getInstance().getReference("user").child()
//            val user = dbRef.child(userId)
            // Handle notification payload here.
            sendNotification(it.body)
        }
    }


    override fun onNewToken(token: String) {
        super.onNewToken(token)

        // If token is generated or refreshed, you can update it in your server/database
        // Here you can send the token to your server if needed
        Log.d(TAG, "Refreshed token: $token")
        sendTokenToServer(token)
    }

    private fun sendTokenToServer(token: String?) {
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val dbRef = FirebaseDatabase.getInstance().getReference("user").child(userId)

            // Update the FCM token in the database
            dbRef.child("fcmToken").setValue(token)
                .addOnSuccessListener {
                    Log.d("TokenUpdate", "FCM token updated successfully")
                }
                .addOnFailureListener { e ->
                    Log.e("TokenUpdate", "Error updating FCM token", e)
                }
        } else {
            Log.e("TokenUpdate", "Current user is null, cannot update FCM token")
        }
    }

    private fun sendNotification(messageBody: String?) {
        val channelId = "my_notification" // You can create your own channel ID
        val channelName = "com.example.chatapplication" // You can create your own channel name
        val notificationManager =
            getSystemService(NotificationManager::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = notificationManager.getNotificationChannel(channelId)
            if (channel == null) {
                val newChannel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(newChannel)
            }
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("FCM Message")
//            .setContentTitle(user.name)
            .setContentText(messageBody)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // You can set your own notification icon
            .setAutoCancel(true)

        notificationManager.notify(0, notificationBuilder.build())
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}
