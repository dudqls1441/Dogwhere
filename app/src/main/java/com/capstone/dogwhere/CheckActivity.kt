package com.capstone.dogwhere

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import com.capstone.dogwhere.FCM.MyReceiver
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
        }


//        val matchinglist = mutableListOf<String>()
//        db.collection("users").document(uid).collection("matching").get().addOnSuccessListener {
//            for (document in it) {
//                matchinglist.add(document["documentId"].toString())
//            }
//            Log.d("yb", "ybybyb matchingList -> ${matchinglist}")
//            if (!matchinglist.isEmpty()) {
//                //.whereEqualTo("ongoing",false)
//                db.collection("Matching").whereIn("documentId", matchinglist).get()
//                    .addOnSuccessListener {
//                        for (document in it) {
//                            val date = document["date"].toString()
//                            Log.d("ybybyb", "ybybyb date1 -> ${date}")
//
//                            val year = date.substring(0, 2)
//                            val month: Int = date.substring(3, 5).toInt() - 1
//                            val day = date.substring(6, 8)
//                            Log.d("ybybyb", "ybybyb1  year ${year} month ${month}  day ${day}")
//
//                            val startime = document["startime"].toString()
//                            Log.d("ybybyb", "ybybyb date1 -> ${startime}")
//
//                            val hour = startime.split("/")
//                            Log.d("ybybyb","ybybyb hour -> ${hour}")
//
//                        }
//                    }
//            }
//        }


    }

//    private fun a() {
//        db = FirebaseFirestore.getInstance()
//        auth = FirebaseAuth.getInstance()
//
//        val uid = auth.currentUser!!.uid
//
//        db.collection("Matching").document("NJ9mdYjFK2uvN6ThGEzx").get()
//            .addOnSuccessListener {
//                val date = it.get("date").toString().split("/")
//                Log.d("ybybyb", "ybybyb date -> ${date}")
//
//                val year = date[0]
//                val month: Int = (date[1]).toInt() - 1
//                val day = date[2]
//
//
//
//                Log.d("ybybyb", "ybybyb  year ${year} month ${month}  day ${day}")
//
//                val startime = it.get("startime").toString().split("/")
//                Log.d("ybybyb", "ybybyb date1 -> ${startime}")
//
//                val hour = startime[0]
//                Log.d("ybybyb", "ybybyb hour -> ${hour}")
//
//                val minute = startime[1]
//                Log.d("ybybyb", "ybybyb minute -> ${minute}")
//
//            }
//
//
//        val calendar = Calendar.getInstance()
//        calendar.set(Calendar.YEAR, 2021)
//        calendar.set(Calendar.MONTH, 10)
//        calendar.set(Calendar.DAY_OF_MONTH, 7)
//        calendar.set(Calendar.HOUR_OF_DAY, 12)
//        calendar.set(Calendar.MINUTE, 0)
//        calendar.set(Calendar.SECOND, 20)
//
//        val alarmIntent = Intent(this, MyReceiver::class.java).apply {
//            action = "com.check.up.setAlarm"
//        }
//        val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val pendingIntent = PendingIntent.getBroadcast(
//            this,
//            0,
//            alarmIntent,
//            PendingIntent.FLAG_CANCEL_CURRENT
//        )
//
//        if (Build.VERSION.SDK_INT >= 23) {
//            alarmManager.setExactAndAllowWhileIdle(
//                AlarmManager.RTC_WAKEUP,
//                calendar.timeInMillis,
//                pendingIntent
//            )
//
//        } else {
//            if (Build.VERSION.SDK_INT >= 19) {
//                alarmManager.setExact(
//                    AlarmManager.RTC_WAKEUP,
//                    calendar.timeInMillis,
//                    pendingIntent
//                )
//            } else {
//                alarmManager.set(
//                    AlarmManager.RTC_WAKEUP,
//                    calendar.timeInMillis,
//                    pendingIntent
//                )
//            }
//        }
//    }

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
                            calendar.set(Calendar.DAY_OF_MONTH, day.toInt())
                            calendar.set(Calendar.HOUR_OF_DAY, 12)
                            calendar.set(Calendar.MINUTE, 51)
                            calendar.set(Calendar.SECOND, 20)

                            val alarmIntent = Intent(this, MyReceiver::class.java).apply {
                                action = "com.check.up.setAlarm"
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
                                    calendar.timeInMillis,
                                    pendingIntent
                                )

                            } else {
                                if (Build.VERSION.SDK_INT >= 19) {
                                    alarmManager.setExact(
                                        AlarmManager.RTC_WAKEUP,
                                        calendar.timeInMillis,
                                        pendingIntent
                                    )
                                } else {
                                    alarmManager.set(
                                        AlarmManager.RTC_WAKEUP,
                                        calendar.timeInMillis,
                                        pendingIntent
                                    )
                                }
                            }
                        }
                    }
            }
        }
    }

//        db.collection("Matching").document("NJ9mdYjFK2uvN6ThGEzx").get()
//            .addOnSuccessListener {
//                val date = it.get("date").toString().split("/")
//                Log.d("ybybyb", "ybybyb date -> ${date}")
//
//                val year = date[0]
//                val month: Int = (date[1]).toInt() - 1
//                val day = date[2]
//
//
//                Log.d("ybybyb", "ybybyb  year ${year} month ${month}  day ${day}")
//
//                val startime = it.get("startime").toString().split("/")
//                Log.d("ybybyb", "ybybyb date1 -> ${startime}")
//
//                val hour = startime[0]
//                Log.d("ybybyb", "ybybyb hour -> ${hour}")
//
//                val minute = startime[1]
//                Log.d("ybybyb", "ybybyb minute -> ${minute}")
//
//                val calendar = Calendar.getInstance()
//                calendar.set(Calendar.YEAR, 2021)
//                calendar.set(Calendar.MONTH, 10)
//                calendar.set(Calendar.DAY_OF_MONTH, 7)
//                calendar.set(Calendar.HOUR_OF_DAY, 10)
//                calendar.set(Calendar.MINUTE, 59)
//                calendar.set(Calendar.SECOND, 20)
//
//            }


//        val alarmIntent = Intent(this, MyReceiver::class.java).apply {
//            action = "com.check.up.setAlarm"
//        }
//        val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val pendingIntent = PendingIntent.getBroadcast(
//            this,
//            0,
//            alarmIntent,
//            PendingIntent.FLAG_CANCEL_CURRENT
//        )
//
//        if (Build.VERSION.SDK_INT >= 23) {
//            alarmManager.setExactAndAllowWhileIdle(
//                AlarmManager.RTC_WAKEUP,
//                calendar.timeInMillis,
//                pendingIntent
//            )
//
//        } else {
//            if (Build.VERSION.SDK_INT >= 19) {
//                alarmManager.setExact(
//                    AlarmManager.RTC_WAKEUP,
//                    calendar.timeInMillis,
//                    pendingIntent
//                )
//            } else {
//                alarmManager.set(
//                    AlarmManager.RTC_WAKEUP,
//                    calendar.timeInMillis,
//                    pendingIntent
//                )
//            }
//        }
//    }


//    private fun getTime(hour:Int,minute:Int): String? {
//        val date = Date()
//        val calendar: Calendar = GregorianCalendar()
//        calendar.time = date
//        val year = calendar[Calendar.YEAR]
//        val month = calendar[Calendar.MONTH]
//        val day = calendar[Calendar.DAY_OF_MONTH]
//        val c_hour = calendar[Calendar.HOUR_OF_DAY]
//        val c_min = calendar[Calendar.MINUTE]
//        val c_sec = calendar[Calendar.SECOND]
//
//
//        val baseCal: Calendar = GregorianCalendar(year, month, day, c_hour, c_min, c_sec)
//        val targetCal: Calendar = GregorianCalendar(year, month, day, hour, minute, 0) //비교대상날짜
//        val diffSec = (targetCal.timeInMillis - baseCal.timeInMillis) / 1000
//        val diffDays = diffSec / (24 * 60 * 60)
//        targetCal.add(Calendar.DAY_OF_MONTH, (-diffDays).toInt())
//        val hourTime = Math.floor((diffSec / 3600).toDouble()).toInt()
//        val minTime = Math.floor(((diffSec - 3600 * hourTime) / 60).toDouble()).toInt()
//        val secTime =
//            Math.floor((diffSec - 3600 * hourTime - 60 * minTime).toDouble()).toInt()
//        val hour = String.format("%02d", hourTime)
//        val min = String.format("%02d", minTime)
//        val sec = String.format("%02d", secTime)
//        return "오늘 매칭까지 " + hour + " 시간 " + min + " 분 " + sec + "초 남았습니다."
//    }
}
