package com.capstone.dogwhere.FCM

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.capstone.dogwhere.MainMenuActivity
import java.util.*
import android.R.*
import android.app.NotificationChannel
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.graphics.Color
import android.util.Log
import androidx.annotation.RequiresApi
import com.capstone.dogwhere.R
import kotlin.random.Random.*
import kotlin.random.Random.Default.nextInt
import kotlin.random.Random.Default.nextInt

private const val CHANNEL_ID = "my_channel"

class FirebaseService : FirebaseMessagingService() {

    companion object {
        var sharedPref: SharedPreferences? = null

        var token: String?
            get() {
                return sharedPref?.getString("token", "")
            }
            set(value) {
                sharedPref?.edit()?.putString("token", value)?.apply()
            }
    }

    // 사용자 디바이스 토큰 새로 생성
    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        token = newToken
    }

    //알람이 온 경우 --> 어떻게 보이게 할 것인지, 누르면 어디로 이동하게 할 것인지 정하는 메소드
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        //알림 팝업 누를 시 MainActivity로 이동
        val intent = Intent(this, MainMenuActivity::class.java)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = 45
        Log.d("ybyb","FirebaseServic로부터 옴 data->${message.data}")
        Log.d("ybyb","FirebaseServic로부터 옴 notification->${message.notification}")
        Log.d("ybyb","FirebaseServic로부터 옴 messageType->${message.messageType}")
        Log.d("ybyb","FirebaseServic로부터 옴 sentTime->${message.sentTime.toString()}")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_ONE_SHOT)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["message"])
            .setSmallIcon(R.drawable.dogwhere_launcher_img_foreground)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationID, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channelName = "channelName"
        Log.d("ybyb","FirebaseServic로부터 옴")

        val channel = NotificationChannel(CHANNEL_ID, channelName, IMPORTANCE_HIGH).apply {
            description = "My channel description"
            enableLights(true)
            lightColor = Color.GREEN
        }
        notificationManager.createNotificationChannel(channel)
    }
}