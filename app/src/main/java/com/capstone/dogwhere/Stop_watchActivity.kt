package com.capstone.dogwhere

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.SystemClock.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.capstone.dogwhere.DTO.Walk_Record
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_stop_watch.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates


class Stop_watchActivity : AppCompatActivity() {

    val Init=0
    val Run=1
    var cur_Status=Init
    var myCount=1
    private var myBaseTime by Delegates.notNull<Long>()
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var last_str: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stop_watch)
        date.setText(current())
        btn_start.setOnClickListener{
            start(cur_Status)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun start(Status: Int){
        when (Status) {
            Init -> {
                myBaseTime = elapsedRealtime()
                time_out.setText("00:00:00")
                println(myBaseTime)
                btn_start.setImageResource(R.drawable.stop)
                km.setTextColor(ContextCompat.getColor(this, R.color.main_mint))
                date.setTextColor(ContextCompat.getColor(this, R.color.main_mint))
                time_out.setTextColor(ContextCompat.getColor(this, R.color.main_mint))
                line1.setBackgroundColor(ContextCompat.getColor(this, R.color.main_mint))
                line2.setBackgroundColor(ContextCompat.getColor(this, R.color.main_mint))
                message_point.setText("종료")
                //myTimer이라는 핸들러를 빈 메세지를 보내서 호출
                myTimer.sendEmptyMessage(0)
                cur_Status = Run //현재상태를 런상태로 변경

            }
            Run -> {
                myTimer.removeMessages(0)
                message_point.setText("시작")
                btn_start.setImageResource(R.drawable.play)
                last_str=getTimeOut().toString()
                upload()
                cur_Status = Init
                myCount = 1
            }
        }
    }
//    fun rec(Status: Int){
//        when (Status) {
//            Run -> {
//                last_str=getTimeOut().toString()
//                Log.e("123", last_str)
//                myCount++ //카운트 증가
//            }
//            Pause -> { //리셋, 저장
//                //핸들러를 멈춤
//                myTimer.removeMessages(0)
//                btn_start.setText("시작")
//                btn_rec.setText("기록")
//                last_str=getTimeOut().toString()
//                upload()
//                time_out.setText("00:00:00")
//                cur_Status = Init
//                myCount = 1
//                record.setText("")
//                btn_rec.setEnabled(false)
//            }
//        }
//    }

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
        val date=current()
        val db = Firebase.firestore
        db.collection("Walk_Record").document(uid).collection(date.toString()).document(uid).get()
                .addOnSuccessListener { result ->
                    val result = result.toObject<com.capstone.dogwhere.DTO.Walk_Record>()
                    if (result != null) {
                        db.collection("Walk_Record").document(uid).collection(date.toString()).document(uid).update("time",time).addOnSuccessListener { documentReference ->
                            //                val intent = Intent(this, BBSActivity::class.java)
                            //                startActivity(intent)
                            //                finish()
                        }
                    }else{
                        Log.e("yy","없면")
                        db.collection("Walk_Record").document(uid).collection(date.toString()).document(uid).set(Walk_Record(uid,time, date.toString(),"", ""))
                            .addOnSuccessListener { documentReference ->
                                //                val intent = Intent(this, BBSActivity::class.java)
                                //                startActivity(intent)
                                //                finish()
                            }
                    }
                }
                .addOnFailureListener { e ->
                }
    }

    private fun current(): String? {

        val time = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-M-d")
        val curTime =dateFormat.format(Date(time))
        return curTime
    }


}