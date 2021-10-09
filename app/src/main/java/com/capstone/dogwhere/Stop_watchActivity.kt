package com.capstone.dogwhere

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.SystemClock.elapsedRealtime
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.capstone.dogwhere.DTO.Walk_Record
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
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
    private var plus_dist by Delegates.notNull<Double>()
    private lateinit var auth: FirebaseAuth
    private lateinit var last_str: String
    private var test=1
    private lateinit var timesec:String

    private lateinit var locationA:Location
    private lateinit var locationB:Location
    private var Latitude1:Double =0.0
    private var Longitude1 by Delegates.notNull<Double>()
    private var Latitude2 by Delegates.notNull<Double>()
    private var Longitude2 by Delegates.notNull<Double>()
    private  var sum_dist:Double =0.0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stop_watch)

        date.setText(current())
        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser!!.uid
        val db = Firebase.firestore

        db.collection("Walk_Record").document(uid).collection(uid).document(date.text.toString()).get()
            .addOnSuccessListener { result ->
                if (result.get("time").toString() == "null" || result.get("time").toString() == "") {
                    Log.e("yy", result.get("time").toString())
                    time_out.setText("00:00:00:00")
                    distance_value.setText((Math.round(sum_dist*100)/100f).toString())
                    timesec=0.toString()
                } else {
                    time_out.setText(result.get("time").toString())
                    sum_dist=result.get("distance").toString().toDouble()
                    distance_value.setText((Math.round(sum_dist*100)/100f).toString())
                    Log.e("yy1", "11111+" + result.get("time").toString())
                    timesec= result.get("timesec").toString()

                }
                plus_sec=timesec.toLongOrNull()!!
                plus_dist=sum_dist
            }
            .addOnFailureListener { e ->

                Log.e("yy", e.toString())
            }


        btn_start.setOnClickListener{
            start(cur_Status)
        }

        btn_x.setOnClickListener{
            myTimer.removeMessages(0)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun start(Status: Int){
        var lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        when (Status) {
            Play -> { //시작
                auth = FirebaseAuth.getInstance()
                val uid = auth.currentUser!!.uid
                val db = Firebase.firestore

                val isGPSEnabled: Boolean = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
                val isNetworkEnabled: Boolean = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

                if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        0
                    )
                } else {

                    when {
                        isNetworkEnabled -> {
                            locationA = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)!!
                            Longitude1 = locationA.longitude
                            Latitude1 = locationA.latitude
                        }
                        isGPSEnabled -> {
                            locationA = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)!!
                            Longitude1 = locationA.longitude
                            Latitude1 = locationA.latitude

                        }
                    }
                    Log.d("거리", "위도 : ${Longitude1}, 경도 : ${Latitude1}")
                }
                if (test == 1) { //첫 시작
                    myBaseTime = elapsedRealtime()
                    db.collection("Walk_Record").document(uid).collection(uid)
                        .document(date.text.toString()).get()
                        .addOnSuccessListener { result ->

                            if (result.get("time").toString() == "null" || result.get("time")
                                    .toString() == "") {
                                Log.e("yy", result.get("time").toString())
                                println(myBaseTime)
                                myTimer.sendEmptyMessage(0)

                            } else {
                                Log.e("yyy", "여기오는겨?")
                                time_out.setText(result.get("time").toString()) //시간 축적???????????
                                Log.e("yyy", "11111+" + result.get("time").toString())

                                Log.e("yyy", timesec.toString())
                                myTimer.sendEmptyMessage(0)
                            }
                        }
                        .addOnFailureListener { e ->
                        }

                } else { //정지했다가 이어서 시작한 경우

                    val now = elapsedRealtime()
                    myTimer.sendEmptyMessage(0)
                    Log.e("yyyy", myBaseTime.toString())
                    myBaseTime += (now - myPauseTime)
                }

                btn_start.setImageResource(R.drawable.stop)
                km.setTextColor(ContextCompat.getColor(this, R.color.main_mint))
                date.setTextColor(ContextCompat.getColor(this, R.color.main_mint))
                time_out.setTextColor(ContextCompat.getColor(this, R.color.main_mint))
                distance_value.setTextColor(ContextCompat.getColor(this, R.color.main_mint))
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
                distance_value.setTextColor(ContextCompat.getColor(this, R.color.main_gray))
                line1.setBackgroundColor(ContextCompat.getColor(this, R.color.main_gray))
                line2.setBackgroundColor(ContextCompat.getColor(this, R.color.main_gray))

                myTimer.removeMessages(0)
                myPauseTime = elapsedRealtime()
                Log.e("yy", myPauseTime.toString())
                test = 2
                message_point.setText("이어서 시작")
                last_str = getTimeOut().toString()
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
            distance_value.setText((Math.round(sum_dist*100)/100f).toString())
            var lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if((timesec.toInt()/ 1000 % 60)%5==0) {
                val isGPSEnabled: Boolean = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
                val isNetworkEnabled: Boolean =
                    lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

                if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this@Stop_watchActivity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        0
                    )
                } else {

                    when {
                        isNetworkEnabled -> {
                            locationB = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)!!
                            Longitude2 = locationB.longitude
                            Latitude2 = locationB.latitude

                        }
                        isGPSEnabled -> {
                            locationB = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)!!
                            Longitude2 = locationB.longitude
                            Latitude2 = locationB.latitude

                        }
                    }
                    Log.d("거리", "위도 : ${Longitude2}, 경도 : ${Latitude2}")
                }
                var dist=locationA.distanceTo(locationB) / 1000
                var kmeter=dist.toString()
                Log.e("거리","dist -> "+kmeter )
                if(dist>0.0){
                    sum_dist+=dist+plus_dist
                    locationA=locationB
                }
            }

        }
    }

    fun getTimeOut(): String? {
        Log.e("yyy", timesec.toString() + "!!!!")
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
        val uid = auth.currentUser!!.uid
        Log.e("123", uid)
        val time = last_str
        Log.e("123", time)
        val date=current()
        val db = Firebase.firestore
        db.collection("Walk_Record").document(uid).collection(uid).document(date.toString()).get()
            .addOnSuccessListener { result ->
                val result = result.toObject<com.capstone.dogwhere.DTO.Walk_Record>()
                if (result != null) {
                    db.collection("Walk_Record").document(uid).collection(uid).document(date.toString()).update(
                        "time",
                        time,
                        "timesec",
                        timesec,
                        "distance",
                        sum_dist.toString()
                    ).addOnSuccessListener { documentReference ->

                    }

                }else{
                    Log.e("yy", "없면")
                    db.collection("Walk_Record").document(uid).collection(uid).document(date.toString()).set(
                        Walk_Record(
                            uid,
                            time,
                            timesec,
                            date.toString(),
                            sum_dist.toString(),
                            ""
                        )
                    )
                        .addOnSuccessListener { documentReference ->

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
