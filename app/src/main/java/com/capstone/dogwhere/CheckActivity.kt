package com.capstone.dogwhere

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.capstone.dogwhere.FCM.MyReceiver
import com.capstone.dogwhere.FCM.*
import com.capstone.dogwhere.DTO.MyNotificationList_item
import com.firepush.Fire
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_check.*
import kotlinx.android.synthetic.main.activity_walk__calendar.*
import org.jetbrains.anko.spinner
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class CheckActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    var cal = Calendar.getInstance()
    private var year = 0
    private var month = 0
    private var day = 0
    private var hour = 0
    private var minute = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check)
        getToken2()

        val breeds = arrayOf(
            "요크셔테리어", "세인트버나드", "푸들", "포메라니안", "웰시코기",
            "페키니즈", "잉글리쉬 쉽독", "빠삐용", "슈나우저", "말티즈",
            "이탈리안 골든 리트리버", "저먼 셰퍼드", "달마시안", "닥스훈트", "차우차우",
            "샤페이", "치와와", "불독", "불 테리어", "복서"
            , "닥스훈트", "차우차우",
            "비숑프리제", "비글", "말라뮤트", "시바견"
        )
        val myAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, breeds)

        breedSpinner.adapter = myAdapter

        breedSpinner.prompt = "견종을 선택하세요"
        breedSpinner.setSelection(5)
        breedSpinner.gravity = Gravity.CENTER_HORIZONTAL
        breedSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                Log.d("ybyb","position ->${position}")
                Log.d("ybyb","id ->${id}")
                Log.d("ybyb","view ->${view.toString()}")
                Log.d("ybyb","이름? ->${breeds[id.toInt()]}")

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d("ybyb","선택 안 됨")
            }
        }
        btn_alarm.setOnClickListener {
//            send()
        }

        simple_timepicker.setIs24HourView(true)


        Fire.init("AAAA1P59Tgs:APA91bEuZ_Hp7rsbkRmR0zWrI_uDhd9o3RMXz4oBpOeXHGc_RCJEo_-d1J-_BL5Hl4jk0KmzjZmWzzNeCOJ4n8jsiFo53QNaknXCq4fOwvbkuSpXNF08XMYud8dY8fHPl1PDMj8-_EDU")

        Log.d("ybyb", "minute ->${minute}")

        btn_cal.setOnClickListener {
            showDatePicker()
        }

        setting()
        btn_time.setOnClickListener {
            showTimePicker()

        }


        val c: Calendar = Calendar.getInstance();
        year = c.get(Calendar.YEAR)
        month = c.get(Calendar.MONTH)
        day = c.get(Calendar.DAY_OF_MONTH)

        val time = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yy/MM/dd")
        val curDate = dateFormat.format(Date(time))

        send_fcm(curDate.toString())

        val datePickerDialog = DatePickerDialog(
            this,
            { view, year, monthOfYear, dayOfMonth ->
                val date = SimpleDateFormat("yy/MM/dd").parse("$year/$monthOfYear/$dayOfMonth")
                val splitedDate =
                    "${year.toString().substring(2)}/${(monthOfYear + 1)}/${dayOfMonth}"
                Log.d("ybyb", "date ->${splitedDate.toString()}")
            }, year, month, day
        )

        btn_date.setOnClickListener {
            datePickerDialog.show()
        }




        year = simple_datepricker.year.toString().substring(2).toInt()
        month = simple_datepricker.month
        day = simple_datepricker.dayOfMonth



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hour = simple_timepicker.getHour();
        } else {
            hour = simple_timepicker.getCurrentHour();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            minute = simple_timepicker.getMinute();
        } else {
            minute = simple_timepicker.getCurrentMinute();
        }

    }

//    private fun fcm() {
//
//        auth = FirebaseAuth.getInstance()
//        val uid = auth.currentUser?.uid.toString()
//
//        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
//        val tokenToDevice: String =
//            "cf3TmWH2SvSKk9RkOKDZLH:APA91bEqQD4nr73KpaxH7dZIzMhRBjMmEYPAWkoQjds1CgMoQDMw0RbsNNTvPu0RRJ3yg_KD8Em34_UZoiVSQjuwZWFcrQuHPtFHMVtgwl9c7wFS81vUq3JdbviUXMQixPh0fnuK38GN"
//        PushNotification(
//            NotificationData("나어디개", "내 매칭에 참여자가 등록되었습니다."),
//            tokenToDevice
//        )
//    }

    private fun send_fcm(curDate :String) {
        Fire.create()
            .setTitle("매세지가 도착했습니다.")
            .setBody(curDate)
            .setCallback { pushCallback, exception ->
                Log.d("ybyb","push->${pushCallback.toString()}")
                Log.d("ybyb","e->${exception.toString()}")

            }
            .toIds("f1L8jRTER7yGpy6GUGzsGB:APA91bFp51ozn-OebtIajekvjbJ-JsRjHkLc5Bwg4OCjtk8tF63MtiPFkth5R5aMiX4tjHfFXP5ms3TJdubxI8ybmFGp12YoFAVPaNS7o1t9UHj8Fm9kBJQWARjFfajA00XTGNYhvMQe")  //toTopic("FOR TOPIC") or toCondition("CONDITION HERE")
            .push()

    }

//    private fun sendNotification2(notification: PushNotification) =
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val response = RetrofitInstance.api.sendNotification(notification)
//                Log.d("ybyb", "response message->" + response.message().toString())
//                Log.d("ybyb", "response errorBody->" + response.errorBody().toString())
//                Log.d("ybyb", "response body->" + response.body().toString())
//                Log.d("ybyb", "response body->" + response.headers().toString())
//
//
//                if (response.isSuccessful) {
//                    Log.d("ybyb", "성공")
//                } else {
//                    Log.e("ybyb", "실패" + response.errorBody().toString())
//                }
//            } catch (e: Exception) {
//                Log.e("ybyb", "에러 ->" + e.toString())
//                Log.e("ybyb", "에러 메세지 ->" + e.message.toString())
//
//            }
//        }
//

    private fun setting() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d("ybyb", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            Log.d("ybyb", "토큰 ->" + token.toString())
            Toast.makeText(baseContext, token.toString(), Toast.LENGTH_SHORT).show()
        })
    }


    fun showDatePicker() {
        DatePickerDialog(this, DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
            Log.d("ybyb", "year ->${year} month ->${month + 1}  day ->${day}")
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE)).show();
    }

    fun showTimePicker() {
        TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            timePicker.spinner()
            timePicker.setIs24HourView(true)
            Log.d("ybyb", "hour ->${hour} minute ->${minute}")
        }, cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE), true).show()
    }

//    private fun send() {
//        var title = "확인_타이틀"
//        var month = 10
//        var day = 12
//        var hour = 15
//        var minute = 28
//        try {
//            //매칭 있는 날 아침시간에 "금일은 매칭이 있습니다" 알림 보내기 위함
//            val calendar = Calendar.getInstance()
//            calendar.set(Calendar.YEAR, 2021)
//            calendar.set(Calendar.MONTH, month)
//            calendar.set(Calendar.DAY_OF_MONTH, day)
//            calendar.set(Calendar.HOUR_OF_DAY, hour)
//            calendar.set(Calendar.MINUTE, minute)
//            calendar.set(Calendar.SECOND, 0)
//
//            val content = title + " 매칭 시작 1시간 전입니다."
//
//            sendNotification(title, content, calendar).run {
//                Log.d("ybyb", "sendNotification 함수 실행")
//                text_time.text = "보냄"
//            }
//
//        } catch (e: Exception) {
//            Log.d("ybyb", "error -> ${e.toString()}")
//
//        }
//    }

    private fun sendNotification(title: String, content: String, calendar: Calendar) {
        val alarmIntent = Intent(this, MyReceiver::class.java).apply {
            action = "com.check.up.setAlarm"
            putExtra("title", title)
            putExtra("content", content)
        }

        Log.d("ybyb", "calendar -> ${calendar.time}")
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
            ).let {
                Log.d("ybyb", title + "알림 보내기")
            }

        } else {
            if (Build.VERSION.SDK_INT >= 19) {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                ).let {
                    Log.d("ybyb", title + "알림 보내기")
                }
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                ).let {
                    Log.d("ybyb", title + "알림 보내기")
                }
            }
        }
    }

    private fun getToken2() {
        val mUser = FirebaseAuth.getInstance().currentUser
        mUser!!.getIdToken(true)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val idToken: String = task.result.token.toString()
                    Log.d("ybyb", "token ->${idToken}")
                } else {
                    Log.d("ybyb", "token -> 가져오기 실패 ->${task.exception}")
                }
            }
    }

//
//    private fun sendNotification(title: String, content: String) {
//        auth= FirebaseAuth.getInstance()
//        val uid = auth.currentUser!!.uid
//        val sendTime_now = (SystemClock.elapsedRealtime() + 1000)
//        //calendar.timeInMillis
//
//        val alarmIntent = Intent(this, MyReceiver::class.java).apply {
//            action = "com.check.up.setAlarm"
//            putExtra("title", title)
//            putExtra("content", content)
//            putExtra("senderUid",uid)
//        }
//        val alarmManager =
//            this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val pendingIntent = PendingIntent.getBroadcast(
//            this,
//            0,
//            alarmIntent,
//            PendingIntent.FLAG_CANCEL_CURRENT
//        )
//
//        if (Build.VERSION.SDK_INT >= 23) {
//            alarmManager.setExactAndAllowWhileIdle(
//                AlarmManager.ELAPSED_REALTIME,
//                sendTime_now,
//                pendingIntent
//            )
//
//        } else {
//            if (Build.VERSION.SDK_INT >= 19) {
//                alarmManager.setExact(
//                    AlarmManager.ELAPSED_REALTIME,
//                    sendTime_now,
//                    pendingIntent
//                )
//            } else {
//                alarmManager.set(
//                    AlarmManager.ELAPSED_REALTIME,
//                    sendTime_now,
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

            Log.d("ybyb", "curtime ->${curTime}")


            Log.d("ybyb", "matchingList -> ${matchinglist}")
            if (!matchinglist.isEmpty()) {
                //.whereEqualTo("ongoing",false)
                db.collection("Matching").whereIn("documentId", matchinglist)
                    .whereEqualTo("date", curTime.toString()).limit(1).get()
                    .addOnSuccessListener {
                        for (document in it) {
                            val date = document.get("date").toString()
                            Log.d("ybyb", "date -> ${date.toString()}")

                            val spliteddate = document.get("date").toString().split("/")
                            Log.d("ybyb", "ybyb date -> ${spliteddate}")

                            val year = spliteddate[0]
                            val month: Int = (spliteddate[1]).toInt() - 1
                            val day = spliteddate[2]


                            Log.d("ybyb", "year ${year} month ${month}  day ${day}")

                            val startime = document.get("startime").toString().split("/")
                            Log.d("ybyb", "date1 -> ${startime}")

                            val hour = startime[0]
                            Log.d("ybyb", "hour -> ${hour}")

                            val minute = startime[1]
                            Log.d("ybyb", "minute -> ${minute}")


                            //매칭 있는 날 아침시간에 "금일은 매칭이 있습니다" 알림 보내기 위함
                            val calendar = Calendar.getInstance()
                            calendar.set(Calendar.YEAR, 2021)
                            calendar.set(Calendar.MONTH, month)
                            calendar.set(Calendar.DAY_OF_MONTH, 9)
                            calendar.set(Calendar.HOUR_OF_DAY, 11)
                            calendar.set(Calendar.MINUTE, 55)
                            calendar.set(Calendar.SECOND, 20)

                            val sendTime_now = (SystemClock.elapsedRealtime() + 1000)
                            //calendar.timeInMillis

                            val alarmIntent = Intent(this, MyReceiver::class.java).apply {
                                action = "com.check.up.setAlarm"
                                putExtra("title", "김영빈")
                                putExtra("content", "님에게 메시지가 도착했습니다.")
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

    private fun add_notification() {
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val uid = auth.currentUser!!.uid

        val username = "김영빈"
        val topic: String = "매칭 참가"
        val content = "님이 매칭에 참가하셨습니다."

        val data = MyNotificationList_item(uid, topic, username, content, "3")


        db.collection("users").document(uid).collection("notification").add(data)
            .addOnSuccessListener {
                Log.d("ybyb", "알림 등록1 성공함")
            }
        db.collection("Notification").add(data).addOnSuccessListener {
            Log.d("ybyb", "알림 등록2 성공함")
        }


    }

    private fun myToken() {
        //쓰레드 사용할것

        val auth = FirebaseAuth.getInstance().currentUser
        Thread(Runnable {
            try {
                auth!!.getIdToken(true).addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.i("ybyb", "getInstanceId failed", task.exception)
                        return@OnCompleteListener
                    }
                    val token = task.result?.token
                    text_time.text = token
                    Log.d("ybyb", token.toString())
                })
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }).start()
    }

}
