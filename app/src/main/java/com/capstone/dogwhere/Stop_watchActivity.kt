package com.capstone.dogwhere

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.SystemClock.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.capstone.dogwhere.DTO.Walk_Record
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_stop_watch.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates


class Stop_watchActivity : AppCompatActivity() {

    val Init=0
    val Run=1
    val Pause=2
    var cur_Status=Init
    var myCount=1
    private var myBaseTime by Delegates.notNull<Long>()
    private var myPauseTime by Delegates.notNull<Long>()
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var last_str: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stop_watch)

        btn_start.setOnClickListener{
            start(cur_Status)
        }
        btn_rec.setOnClickListener {
            rec(cur_Status)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun start(Status: Int){
        when (Status) {
            Init -> {
                myBaseTime = elapsedRealtime()
                println(myBaseTime)
                //myTimer이라는 핸들러를 빈 메세지를 보내서 호출
                myTimer.sendEmptyMessage(0)
                btn_start.setText("일시정지") //버튼의 문자"시작"을 "멈춤"으로 변경
                btn_rec.setEnabled(true) //기록버튼 활성
                cur_Status = Run //현재상태를 런상태로 변경

            }
            Run -> {
                myTimer.removeMessages(0) //핸들러 메세지 제거
                myPauseTime = elapsedRealtime()
                btn_start.setText("시작")
                btn_rec.setText("저장")
                cur_Status = Pause
            }
            Pause -> {
                val now: Long = elapsedRealtime()
                myTimer.sendEmptyMessage(0)
                myBaseTime += now - myPauseTime
                btn_start.setText("일시정지")
                btn_rec.setText("기록")
                cur_Status = Run
            }
        }
    }
    fun rec(Status: Int){
        when (Status) {
            Run -> {
                var str= record.getText().toString()
                last_str=getTimeOut().toString()
                str += String.format("%d. %s\n", myCount, getTimeOut())
                record.setText(str)
                Log.e("123", last_str)
                myCount++ //카운트 증가
            }
            Pause -> { //리셋, 저장
                //핸들러를 멈춤
                myTimer.removeMessages(0)
                btn_start.setText("시작")
                btn_rec.setText("기록")
                last_str=getTimeOut().toString()
                upload()
                time_out.setText("00:00:00")
                cur_Status = Init
                myCount = 1
                record.setText("")
                btn_rec.setEnabled(false)
            }
        }
    }

    var myTimer: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            time_out.setText(getTimeOut())

            //sendEmptyMessage 는 비어있는 메세지를 Handler 에게 전송하는겁니다.
            sendEmptyMessage(0)
        }
    }


    fun getTimeOut(): String? {
        val now = elapsedRealtime() //애플리케이션이 실행되고나서 실제로 경과된 시간(??)^^;
        val outTime: Long = now - myBaseTime
        return String.format(
            "%02d:%02d:%02d",
            outTime / 1000 / 60,
            outTime / 1000 % 60,
            outTime % 1000 / 10
        )
    }

    private fun upload() {
        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser.uid
        Log.e("123", uid)
        val time = last_str
        Log.e("123", time)
        val distance = "123"
        val date=currenttime()
        val db = Firebase.firestore
        db.collection("Walk_Record").document(uid).collection(date.toString()).add(Walk_Record(uid, time, date.toString(), distance))
            .addOnSuccessListener { documentReference ->
                Log.e("123", "성공ㅇㅇ")
//                val intent = Intent(this, BBSActivity::class.java)
//                startActivity(intent)
//                finish()
            }
            .addOnFailureListener { e ->

            }
    }

    private fun currenttime(): String? {

        val time = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-mm-dd hh:mm:ss")
        val curTime = dateFormat.format(Date(time))
        Log.d("check",curTime)

//    val current = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//        LocalDateTime.now()
//    } else {
//
//    }
//    val now = LocalDate.now()
//    var formatter = DateTimeFormatter.ISO_DATE
//    val formatted = current.format(formatter)

        return curTime
    }


}