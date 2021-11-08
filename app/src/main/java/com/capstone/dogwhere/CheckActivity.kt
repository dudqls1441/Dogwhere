package com.capstone.dogwhere

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.util.Log
import com.capstone.dogwhere.FCM.MyReceiver
import com.capstone.dogwhere.DTO.MyNotificationList_item
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_check.*
import kotlinx.android.synthetic.main.activity_walk__calendar.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*


class CheckActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check)

        val dateAndtime: LocalDateTime = LocalDateTime.now()
        val onlyDate: LocalDate = LocalDate.now()

        val date = onlyDate.toString().split("-")
        Log.d("ybybyb", "today date ->y : ${date[0]} m : ${date[1]}  d : ${date[2]} ")

        Log.d("ybybyb", "ybybybCurrent date and time: $dateAndtime")
        Log.d("ybybyb", "ybybybCurrent date: $onlyDate")




        btn_alarm.setOnClickListener {
            alarm()
            add_notification()
        }
    }

    private fun alarm() {
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val uid = auth.currentUser!!.uid

        val matchinglist = mutableListOf<String>()
        db.collection("users").document(uid).collection("matching").get().addOnSuccessListener {
            for (document in it) {
                matchinglist.add(document["documentId"].toString())
            }

            val time = System.currentTimeMillis()
            val dateFormat = SimpleDateFormat("yy/MM/dd")
            val curTime = dateFormat.format(Date(time))

            Log.d("ybybyb", "ybybyb curtime ->${curTime}")


            Log.d("yb", "ybybyb matchingList -> ${matchinglist}")
            if (!matchinglist.isEmpty()) {
                //.whereEqualTo("ongoing",false)
                db.collection("Matching").whereIn("documentId", matchinglist)
                    .whereEqualTo("date", curTime.toString()).limit(1).get()
                    .addOnSuccessListener {
                        for (document in it) {
                            val date = document.get("date").toString()
                            Log.d("ybybyb", "date -> ${date.toString()}")

                            val spliteddate = document.get("date").toString().split("/")
                            Log.d("ybybyb", "ybybyb date -> ${spliteddate}")

                            val year = spliteddate[0]
                            val month: Int = (spliteddate[1]).toInt() - 1
                            val day = spliteddate[2]


                            Log.d("ybybyb", "ybybyb  year ${year} month ${month}  day ${day}")

                            val startime = document.get("startime").toString().split("/")
                            Log.d("ybybyb", "ybybyb date1 -> ${startime}")

                            val hour = startime[0]
                            Log.d("ybybyb", "ybybyb hour -> ${hour}")

                            val minute = startime[1]
                            Log.d("ybybyb", "ybybyb minute -> ${minute}")


                            //매칭 있는 날 아침시간에 "금일은 매칭이 있습니다" 알림 보내기 위함
                            val calendar = Calendar.getInstance()
                            calendar.set(Calendar.YEAR, 2021)
                            calendar.set(Calendar.MONTH, month)
                            calendar.set(Calendar.DAY_OF_MONTH, 8)
                            calendar.set(Calendar.HOUR_OF_DAY, 21)
                            calendar.set(Calendar.MINUTE, 30)
                            calendar.set(Calendar.SECOND, 20)

                            val sendTime_now = (SystemClock.elapsedRealtime()+ 1000)
                            //calendar.timeInMillis

                            val alarmIntent = Intent(this, MyReceiver::class.java).apply {
                                action = "com.check.up.setAlarm"
                                putExtra("title","김영빈")
                                putExtra("context","님에게 메시지가 도착했습니다.")
                            }
                            val alarmManager =
                                this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                            val pendingIntent = PendingIntent.getBroadcast(
                                this,
                                0,
                                alarmIntent,
                                PendingIntent.FLAG_CANCEL_CURRENT
                            )

                            if (Build.VERSION.SDK_INT >= 23) {
                                alarmManager.setExactAndAllowWhileIdle(
                                    AlarmManager.RTC_WAKEUP,
                                    sendTime_now,
                                    pendingIntent
                                )

                            } else {
                                if (Build.VERSION.SDK_INT >= 19) {
                                    alarmManager.setExact(
                                        AlarmManager.RTC_WAKEUP,
                                        sendTime_now,
                                        pendingIntent
                                    )
                                } else {
                                    alarmManager.set(
                                        AlarmManager.RTC_WAKEUP,
                                        sendTime_now,
                                        pendingIntent
                                    )
                                }
                            }
                        }
                    }
            }
        }
    }

    private fun add_notification(){
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val uid = auth.currentUser!!.uid

        val username = "김영빈"
        val topic :String = "매칭 참가"
        val content = "님이 매칭에 참가하셨습니다."

        val data = MyNotificationList_item(uid,topic,username,content,"3")


        db.collection("users").document(uid).collection("notification").add(data).addOnSuccessListener {
            Log.d("ybybyb","알림 등록1 성공함")
        }
        db.collection("Notification").add(data).addOnSuccessListener {
            Log.d("ybybyb","알림 등록2 성공함")
        }


    }

}
