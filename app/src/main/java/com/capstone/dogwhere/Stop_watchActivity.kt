package com.capstone.dogwhere

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.SystemClock.elapsedRealtime

import android.util.Log
import android.widget.Toast
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

    val Play=0
    val Stop=1
    var cur_Status=Play
    var myCount=1
    private var outTime by Delegates.notNull<Long>()
    private var myBaseTime by Delegates.notNull<Long>()
    private var myPauseTime by Delegates.notNull<Long>()
    private var plus_sec by Delegates.notNull<Long>()
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var last_str: String
    private var test=1
    private lateinit var timesec:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stop_watch)
        date.setText(current())
        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser.uid
        val db = Firebase.firestore

        db.collection("Walk_Record").document(uid).collection(date.text.toString()).document(uid).get()
            .addOnSuccessListener { result ->
                if (result.get("time").toString() == "null" || result.get("time").toString() == "") {
                    Log.e("yy",result.get("time").toString())
                    time_out.setText("00:00:00:00")
                    timesec=0.toString()

                } else {
                    time_out.setText(result.get("time").toString()) //시간 축적
                    Log.e("yy1","11111+"+result.get("time").toString())
                    timesec= result.get("timesec").toString()

                }
                plus_sec=timesec.toLongOrNull()!!
            }
            .addOnFailureListener { e ->
                Log.e("yy","여기니")
            }


        btn_start.setOnClickListener{
            start(cur_Status)
        }

        btn_x.setOnClickListener{
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun start(Status: Int){
        when (Status) {
            Play -> { //시작
                auth = FirebaseAuth.getInstance()
                val uid = auth.currentUser.uid
                val db = Firebase.firestore

                if(test==1){ //첫 시작
                    myBaseTime = elapsedRealtime()
                    db.collection("Walk_Record").document(uid).collection(date.text.toString()).document(uid).get()
                        .addOnSuccessListener { result ->
                            if (result.get("time").toString() == "null" || result.get("time").toString() == "") {
                                Log.e("yy",result.get("time").toString())
                                println(myBaseTime)
                                myTimer.sendEmptyMessage(0)

                            } else {
                                Log.e("yyy","여기오는겨?")
                                time_out.setText(result.get("time").toString()) //시간 축적
                                Log.e("yyy","11111+"+result.get("time").toString())
                                val now= elapsedRealtime()

                                Log.e("yyy",timesec.toString())
                                myTimer.sendEmptyMessage(0)

//                                myBaseTime+=(now-myBaseTime)
//                                Log.e("yyy",myBaseTime.toString())


                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("yy","여기니")
                        }


                }else{ //정지했다가 이어서 시작한 경우
                    val now= elapsedRealtime()
                    myTimer.sendEmptyMessage(0)
                    Log.e("yyyy", myBaseTime.toString())
                    myBaseTime+=(now-myPauseTime)
                }

                btn_start.setImageResource(R.drawable.stop)
                km.setTextColor(ContextCompat.getColor(this, R.color.main_mint))
                date.setTextColor(ContextCompat.getColor(this, R.color.main_mint))
                time_out.setTextColor(ContextCompat.getColor(this, R.color.main_mint))
                line1.setBackgroundColor(ContextCompat.getColor(this, R.color.main_mint))
                line2.setBackgroundColor(ContextCompat.getColor(this, R.color.main_mint))
                message_point.setText("종료")
                cur_Status = Stop //현재상태를 런상태로 변경

            }
            Stop -> { //종료
                btn_start.setImageResource(R.drawable.play)
                km.setTextColor(ContextCompat.getColor(this, R.color.main_gray))
                date.setTextColor(ContextCompat.getColor(this, R.color.main_gray))
                time_out.setTextColor(ContextCompat.getColor(this, R.color.main_gray))
                line1.setBackgroundColor(ContextCompat.getColor(this, R.color.main_gray))
                line2.setBackgroundColor(ContextCompat.getColor(this, R.color.main_gray))

                myTimer.removeMessages(0)
                myPauseTime = elapsedRealtime()
                Log.e("yy", myPauseTime.toString())
                test=2
                message_point.setText("이어서 시작")
                last_str=getTimeOut().toString()
                upload()
                cur_Status = Play
                myCount = 1
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
        Log.e("yyy",timesec.toString()+"!!!!" )
        val now = elapsedRealtime() //애플리케이션이 실행되고나서 실제로 경과된 시간(??)^^;
        outTime= plus_sec+now - myBaseTime //???????????????ㅠㅠ
        timesec=outTime.toString()
        return String.format(
            "%02d:%02d:%02d:%02d",
            outTime / 1000 / 60 / 60,
            outTime / 1000 / 60,
            outTime / 1000 % 60, //초
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
                    db.collection("Walk_Record").document(uid).collection(date.toString()).document(uid).update("time",time,"timesec",timesec).addOnSuccessListener { documentReference ->
                        //                val intent = Intent(this, BBSActivity::class.java)
                        //                startActivity(intent)
                        //                finish()
                    }
                }else{
                    Log.e("yy","없면")
                    db.collection("Walk_Record").document(uid).collection(date.toString()).document(uid).set(Walk_Record(uid,time,timesec, date.toString(),"", ""))
                        .addOnSuccessListener { documentReference ->
                            //                val intent = Intent(this, BBSActivity::class.java)
                            //                startActivity(intent)
                            //                finish()
                        }
                }
                Toast.makeText(
                    this,
                    "저장되었습니다! 캘린더에서 확인하세요^^",
                    Toast.LENGTH_SHORT
                ).show()
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