//package com.capstone.dogwhere.FCM
//
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.app.NotificationManager.IMPORTANCE_HIGH
//import android.app.PendingIntent
//import android.app.PendingIntent.FLAG_ONE_SHOT
//import android.content.Context
//import android.content.Intent
//import android.graphics.Color
//import android.media.RingtoneManager
//import android.os.Build
//import android.os.Build.VERSION_CODES.O
//import android.util.Log
//import androidx.annotation.RequiresApi
//import androidx.core.app.NotificationCompat
//import androidx.work.OneTimeWorkRequest
//import androidx.work.WorkManager
//import com.capstone.dogwhere.MainMenuActivity
//import com.capstone.dogwhere.R
//import com.google.firebase.messaging.FirebaseMessagingService
//import com.google.firebase.messaging.RemoteMessage
//import java.util.*
//
//private const val CHANNEL_ID = "my_channel"
////푸시 알림을 위한 클래스.
////푸시 알림으로 보낼 수 있는 메세지는 2가지 유형 : 1.Notification 2. Data
////보통 Data를 사용 -> 실행중이나 백그라운드(앱이 실행중이지 않을 때 ) 알림이 옴
//class MyFirebaseMessagingService : FirebaseMessagingService() {
//
//
//    //메세지를 수신할 때 호출된다. (메세지를 받을 때) remoteMesage는 수신한 메세지이다.
//    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        Log.d("MessagingService", "From ->${remoteMessage.from}")
//
//        //메세지에 데이터 페이로드가 포함되어 있는지 확인
//        //페이로드 -> 전송된 데이터
//        //데이터 값이 있는지 없는지 확인할 때 쓰임
//        if (remoteMessage.data.isNotEmpty()) {
//            Log.d("MessagingService", "Message data payload -> ${remoteMessage.data}")
//            sendNofication(remoteMessage)
//            if (true) {
//                //데이터를 처리하는데 10초 이상이 걸리면 workMananger를 사용
//                schedulejob()
//            } else {
//                //10초이내에 시작하면 아래 메서드 실행
//                handleNow()
//            }
//        }
//        //메세지에 알림 페이로드가 포함되어 있는지 확인한다.
//        remoteMessage.notification?.let {
//            Log.d("MessagingService", "Message Notification Body -> ${it.body}")
//
//        }
////        val intent = Intent(this, MainMenuActivity::class.java)
////        val notificationManager =
////            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
////        val notificationID = Random().nextInt()
////
////        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////            createNotificationChannel(notificationManager)
////        }
////
////        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
////        val pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_ONE_SHOT)
////        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
////            .setContentTitle(remoteMessage.data["title"])
////            .setContentText(remoteMessage.data["message"])
////            .setSmallIcon(R.drawable.dogwhere_launcher_img_foreground)
////            .setAutoCancel(true)
////            .setContentIntent(pendingIntent)
////            .build()
////
////        notificationManager.notify(notificationID, notification)
//
//
//    }
//
//    //FirebaseInstanceIdService는 이제 사라지고 onNewToken 사용
//    //FCM 등록 토큰이 업데이트되면 호출됨.
//    //토큰이 처음 생성될 때 여기에서 토큰을 검색할 수 있다.
//    override fun onNewToken(token: String) {
//
//        //FCM 등록 토큰을 앱 서버에 추가
//        sendRegistraionToServer(token)
//
//    }
//
//    //메세지 페이로드가 있을 때 실행되는 메서드(10초 이상 걸릴 때 호출됨)
//    //workMangger를 사용하여 비동기 작업을 예약함
//    private fun schedulejob() {
//        //-> start dispatch_job
//        val work = OneTimeWorkRequest.Builder(MyWorker::class.java).build()
//        WorkManager.getInstance(this).beginWith(work).enqueue()
//        //-> end dispatch_job
//    }
//
//    //메세지 페이로드가 있을 때 실행되는 메서드(10초 이내로 걸릴 때 호출된다.)
//    //BroadcastRecivers에 할당 된 시간을 처리
//    private fun handleNow() {
//        Log.d("MessagingService", "short lived task is done")
//
//    }
//
//    //타사 서버에 토큰을 유지해주는 메서드
//    //사용자의 FCM등록 토큰을 서버 측 계정에 연결하려면 이 방법 사용
//    //응용 프로그램에서 유지 관리를 함
//    //파라미터에 들어있는 토큰은 새로운 토큰임
//    private fun sendRegistraionToServer(token: String?) {
//        Log.d("MessagingService", "MessagingService -> sendRegistrationTokenToServer($token)")
//
//    }
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun createNotificationChannel(notificationManager: NotificationManager) {
//        val channelName = "channelName"
//        val channel = NotificationChannel(CHANNEL_ID, channelName, IMPORTANCE_HIGH).apply {
//            description = "My channel description"
//            enableLights(true)
//            lightColor = Color.GRAY
//        }
//        notificationManager.createNotificationChannel(channel)
//    }
//
//    //수신된 FCM 메시지를 포함하는 간단한 알림을 만들고 표시
//    //파라미터에 있는 messageBody에는 FCM메세지 본문이 담겨져 있다.
//    private fun sendNofication(remoteMessage: RemoteMessage) {
//        //팝업 눌렀을 때 이동하는 페이지
//
//        val intent = Intent(this, MainMenuActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//
//        val pendingIntent =
//            PendingIntent.getActivity(this, O /* requestCode*/, intent, PendingIntent.FLAG_ONE_SHOT)
//        val channelId = "" //다시 설정
//        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
//        val notificationBuilder = NotificationCompat.Builder(this, channelId)
//            .setSmallIcon(R.drawable.dogwhere_launcher_img_foreground)
//            .setContentTitle(remoteMessage.data["title"].toString()) // 다시 설정 R.string.fcm_fallback_notification_channel_label)
//            .setContentText(remoteMessage.data["body"].toString())
//            .setAutoCancel(true)
//            .setSound(defaultSoundUri)
//            .setContentIntent(pendingIntent)
//
//        val notificationManager =
//            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                channelId,
//                "Channel title",
//                NotificationManager.IMPORTANCE_DEFAULT
//            )
//            notificationManager.createNotificationChannel(channel)
//        }
//        notificationManager.notify(0, notificationBuilder.build())
//
//    }
//
//    companion object {
//        private const val TAG = "MyFirebaseMsgService"
//    }
//
//
//}