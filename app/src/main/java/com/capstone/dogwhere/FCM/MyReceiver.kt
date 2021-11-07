package com.capstone.dogwhere.FCM

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.capstone.dogwhere.MainMenuActivity
import com.capstone.dogwhere.FCM.Constants.Companion.CHANNEL_ID
import com.capstone.dogwhere.FCM.Constants.Companion.NOTIFICATION_ID
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.LocalDateTime
import com.capstone.dogwhere.DTO.Alarm_data


class MyReceiver : BroadcastReceiver() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    lateinit var notificationManager: NotificationManager

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        if(intent != null){
            val dateAndtime: LocalDateTime = LocalDateTime.now()
            val onlyDate: LocalDate = LocalDate.now()

            val uid = auth.currentUser!!.uid

            val data = Alarm_data(uid,dateAndtime.toString(),"시간 전송")

            Log.d("ybybyb","ybybyb -> ${intent}")

            db.collection("Alarm").document(uid).collection("내용").add(data).addOnSuccessListener {
                Log.d("ybybyb","ybybyb 전송 성공")
            }

            println("ybybyb알람 온 시간: $dateAndtime")
            Log.e("ybybyb알람","ybybyb"+System.currentTimeMillis().toString())


        }
        notificationManager = context.getSystemService(
            Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannel()
        deliverNotification(context)
    }


    // Notification 을 띄우기 위한 Channel 등록
    fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID, // 채널의 아이디
                "채널 이름입니다.", // 채널의 이름
                NotificationManager.IMPORTANCE_HIGH
                /*
                1. IMPORTANCE_HIGH = 알림음이 울리고 헤드업 알림으로 표시
                2. IMPORTANCE_DEFAULT = 알림음 울림
                3. IMPORTANCE_LOW = 알림음 없음
                4. IMPORTANCE_MIN = 알림음 없고 상태줄 표시 X
                 */
            )
            notificationChannel.enableLights(true) // 불빛
            notificationChannel.lightColor = Color.RED // 색상
            notificationChannel.enableVibration(true) // 진동 여부
            notificationChannel.description = "채널의 상세정보입니다." // 채널 정보
            notificationManager.createNotificationChannel(
                notificationChannel)
        }
    }

    // Notification 등록
    private fun deliverNotification(context: Context){
        val contentIntent = Intent(context, MainMenuActivity::class.java)
        val contentPendingIntent = PendingIntent.getActivity(
            context,
            NOTIFICATION_ID, // requestCode
            contentIntent, // 알림 클릭 시 이동할 인텐트
            PendingIntent.FLAG_UPDATE_CURRENT
            /*
            1. FLAG_UPDATE_CURRENT : 현재 PendingIntent를 유지하고, 대신 인텐트의 extra data는 새로 전달된 Intent로 교체
            2. FLAG_CANCEL_CURRENT : 현재 인텐트가 이미 등록되어있다면 삭제, 다시 등록
            3. FLAG_NO_CREATE : 이미 등록된 인텐트가 있다면, null
            4. FLAG_ONE_SHOT : 한번 사용되면, 그 다음에 다시 사용하지 않음
             */
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.divider_horizontal_dim_dark)
            .setContentTitle("타이틀 입니다.") // 제목
            .setContentText("내용 입니다.") // 내용
            .setContentIntent(contentPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }
}