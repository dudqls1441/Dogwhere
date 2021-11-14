package com.capstone.dogwhere

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.*
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.capstone.dogwhere.DTO.*
import com.capstone.dogwhere.FCM.MyReceiver
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.*
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.synthetic.main.activity_matching_registration.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MatchingRegistrationActivity : AppCompatActivity(), OnMapReadyCallback,
    OnCameraMoveListener, OnMapClickListener,
    OnInfoWindowClickListener,
    OnMarkerDragListener {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    lateinit var party_address: String
    private var dogname = ArrayList<String>()
    lateinit var condition_size: String
    lateinit var condition_owner_gender: String
    lateinit var condition_neutralization: String
    lateinit var choice_lat: String
    lateinit var choice_lon: String
    var map: GoogleMap? = null
    var mLM: LocationManager? = null
    var mProvider = LocationManager.NETWORK_PROVIDER
    var mylocation: LatLng? = null
    private val FLAG_Select_Dog_Code = 1000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matching_registration)
        mylocation = getMyLocation()
        condition_size = "all"
        condition_neutralization = "all"
        condition_owner_gender = "all"
        radio_size()
        radio_neutralization()
        radio_gender()

        btn_registration.setOnClickListener {
            register()
        }
        Setting_DoneTime()

        //지도
        mLM = getSystemService(LOCATION_SERVICE) as LocationManager
        val fragment = supportFragmentManager
            .findFragmentById(R.id.registration_mapfragment) as SupportMapFragment?
        fragment!!.getMapAsync(this)

        btn_back.setOnClickListener {
            this.finish()
        }
//        val yearList = (21..25).toList()
        val monthList = (1..12).toList()
        val dayList = (1..31).toList()
        val hoursList = (1..24).toList()

        var mList = listOf(
            "00",
            "01",
            "02",
            "03",
            "04",
            "05",
            "06",
            "07",
            "08",
            "09",
            "10",
            "11",
            "12",
            "13",
            "14",
            "15",
            "16",
            "17",
            "18",
            "19",
            "20",
            "21",
            "22",
            "23",
            "24",
            "25",
            "26",
            "27",
            "28",
            "29",
            "30",
            "31",
            "32",
            "33",
            "34",
            "35",
            "36",
            "37",
            "38",
            "39",
            "40",
            "41",
            "42",
            "43",
            "44",
            "45",
            "46",
            "47",
            "48",
            "49",
            "50",
            "51",
            "52",
            "53",
            "54",
            "55",
            "56",
            "57",
            "58",
            "59"
        )

        var monthStrConvertList = monthList.map { it.toString() }
        var dayStrConvertList = dayList.map { it.toString() }
        var hoursStrCovertList = hoursList.map { it.toString() }
        var minuteStrCovertList = mList.map { it }

        npMonth.run {
            minValue = 1
            maxValue = monthStrConvertList.size

            wrapSelectorWheel = false
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        }
        npDay.run {
            minValue = 1
            wrapSelectorWheel = false
            maxValue = dayStrConvertList.size
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        }
        npHours.run {
            minValue = 0
            maxValue = hoursStrCovertList.size - 1
            wrapSelectorWheel = false
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        }
        npMinute.run {
            minValue = 0
            maxValue = minuteStrCovertList.size - 1
            wrapSelectorWheel = false
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        }


        participation_dog_layout.setOnClickListener {
            Intent(this, MatchingRegistration_Choice_Dog_Activity::class.java).apply {
                putExtra("dogchoice_state", "matching_registration")
            }.run { startActivityForResult(this, 110) }
        }

        // 지도 클릭 시 위치 고르는 화면으로 전환 후 좌표 가지고 돌아오기

    }

    private fun Setting_DoneTime() {
        var DoneTime = findViewById<TextView>(R.id.DoneTime_minute).text.toString().toInt()

        btn_time_up.setOnClickListener {
            if (DoneTime < 180) {
                DoneTime = DoneTime + 10
                val time = DoneTime.toString()
                DoneTime_minute.setText(time)
            }
            val time = DoneTime.toString()
            DoneTime_minute.setText(time)
        }
        btn_time_down.setOnClickListener {
            if (DoneTime > 30) {
                DoneTime = DoneTime - 10
                val time = DoneTime.toString()
                DoneTime_minute.setText(time)
            }
            val time = DoneTime.toString()
            DoneTime_minute.setText(time)


        }
    }

    override fun onStart() {
        super.onStart()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val location = mLM!!.getLastKnownLocation(mProvider)
        if (location != null) {
            mListener.onLocationChanged(location)
        }
        mLM!!.requestSingleUpdate(mProvider, mListener, null)

    }

    override fun onStop() {
        super.onStop()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mLM!!.removeUpdates(mListener)
    }

    override fun onResume() {
        super.onResume()

    }

    private fun moveMap(lat: Double, lng: Double) {
        if (map != null) {
            val latLng = LatLng(lat, lng)
            val position = CameraPosition.Builder()
                .target(latLng)
                .bearing(30f)
                .tilt(45f)
                .zoom(17f)
                .build()
            val update = CameraUpdateFactory.newLatLngZoom(latLng, 17f)
            //            CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);
            map!!.moveCamera(update)
            //        map.animateCamera(update);
        }
    }

    var mListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            moveMap(location.latitude, location.longitude)
        }

        override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {}
        override fun onProviderEnabled(s: String) {}
        override fun onProviderDisabled(s: String) {}
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map!!.mapType = GoogleMap.MAP_TYPE_NORMAL
        //        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
//        map.setIndoorEnabled(true);
//        map.setBuildingsEnabled(true);
//        map.setTrafficEnabled(true);
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }


        map!!.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    mylocation!!.latitude,
                    mylocation!!.longitude
                ), 14f
            )
        )
        map!!.isMyLocationEnabled = true
        map!!.setOnCameraMoveListener(this)
        map!!.setOnMapClickListener(this)
        map!!.setOnInfoWindowClickListener(this)
        map!!.setOnMarkerDragListener(this)
        val mapui = map!!.uiSettings
        mapui.isZoomControlsEnabled = false
        mapui.isMapToolbarEnabled = false
        mapui.isZoomGesturesEnabled = false
        mapui.isTiltGesturesEnabled = false
        mapui.isRotateGesturesEnabled = false
        mapui.isScrollGesturesEnabled = false
        mapui.isScrollGesturesEnabledDuringRotateOrZoom = false
        mapui.isCompassEnabled = false
//        val position = map!!.cameraPosition
//        val target = position.target
//
//        target.latitude
//        target.longitude
//        btn_map_add.setOnClickListener {
//            val intent = Intent(this,MatchingRegistrationActivity::class.java)
//            Log.e("joo", target.toString())
//            intent.putExtra("choice_lat", target.latitude)
//            intent.putExtra("choice_lon", target.longitude)
//            setResult(Activity.RESULT_OK,intent)
//            finish()
//        }
    }


    //강아지 선택해서 돌아왔을 때 처리하기
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            Log.d("yb", "result_ok")

            when (requestCode) {
                110 -> {
                    dogname = data?.getStringArrayListExtra("select_dogname") as ArrayList<String>
                    Log.d("yy", "저장할 강아지 리스트" + dogname.toString())
                    participation_dog_layout.text = dogname.toString()
                }
                100 -> {
                    // 좌표를 가지고 와서 위치 정보 가져오기
                    choice_lat = data!!.getStringExtra("choice_lat").toString()
                    choice_lon = data!!.getStringExtra("choice_lon").toString()
                    Log.e("joo", choice_lat + choice_lon)
                    map!!.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                choice_lat.toDouble(),
                                choice_lon.toDouble()
                            ), 14f
                        )
                    )
//                    moveMap(choice_lat.toDouble(), choice_lon.toDouble())
                    addMarker(choice_lat.toDouble(), choice_lon.toDouble(), "선택한 위치")
                    party_address = getCurrentAddress(choice_lat.toDouble(), choice_lon.toDouble())
                    if (party_address != "null") {
                        edittext_place.text = party_address
                    }
                }

            }

        }


    }

    private fun radio_size() {
        radiogroup_size.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.size_all -> checked_size_size_all()
                R.id.size_small -> checked_size_small()
                R.id.size_middle -> checked_size_middle()
                R.id.size_big -> checked_size_big()


            }
        }
    }

    private fun radio_neutralization() {
        radiogroup_neutralization.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.dontCare_neutralization -> checked_dontCare_neutralization()
                R.id.neutralization -> checked_neutralization()
            }
        }
    }

    private fun radio_gender() {
        radiogroup_gender.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.gender_all -> checked_gender_all()
                R.id.gender_man -> checked_gender_man()
                R.id.gender_woman -> checked_gender_woman()
            }
        }
    }

    private fun checked_size_size_all() {
        size_all.setBackgroundResource(R.drawable.backgroundgreencircle)
        size_all.setTextColor(Color.parseColor("#00C09F"))
        size_small.setBackgroundResource(R.drawable.backgroundgraycircle)
        size_small.setTextColor(Color.parseColor("#52443C3C"))
        size_middle.setBackgroundResource(R.drawable.backgroundgraycircle)
        size_middle.setTextColor(Color.parseColor("#52443C3C"))
        size_big.setBackgroundResource(R.drawable.backgroundgraycircle)
        size_big.setTextColor(Color.parseColor("#52443C3C"))

        condition_size = "all"
    }

    private fun checked_size_small() {
        size_all.setBackgroundResource(R.drawable.backgroundgraycircle)
        size_all.setTextColor(Color.parseColor("#52443C3C"))
        size_small.setBackgroundResource(R.drawable.backgroundgreencircle)
        size_small.setTextColor(Color.parseColor("#00C09F"))
        size_middle.setBackgroundResource(R.drawable.backgroundgraycircle)
        size_middle.setTextColor(Color.parseColor("#52443C3C"))
        size_big.setBackgroundResource(R.drawable.backgroundgraycircle)
        size_big.setTextColor(Color.parseColor("#52443C3C"))

        condition_size = "all,small"
    }

    private fun checked_size_middle() {
        size_all.setBackgroundResource(R.drawable.backgroundgraycircle)
        size_all.setTextColor(Color.parseColor("#52443C3C"))
        size_small.setBackgroundResource(R.drawable.backgroundgraycircle)
        size_small.setTextColor(Color.parseColor("#52443C3C"))
        size_middle.setBackgroundResource(R.drawable.backgroundgreencircle)
        size_middle.setTextColor(Color.parseColor("#00C09F"))
        size_big.setBackgroundResource(R.drawable.backgroundgraycircle)
        size_big.setTextColor(Color.parseColor("#52443C3C"))

        condition_size = "all,middle"
    }

    private fun checked_size_big() {
        size_all.setBackgroundResource(R.drawable.backgroundgraycircle)
        size_all.setTextColor(Color.parseColor("#52443C3C"))
        size_small.setBackgroundResource(R.drawable.backgroundgraycircle)
        size_small.setTextColor(Color.parseColor("#52443C3C"))
        size_middle.setBackgroundResource(R.drawable.backgroundgraycircle)
        size_middle.setTextColor(Color.parseColor("#52443C3C"))
        size_big.setBackgroundResource(R.drawable.backgroundgreencircle)
        size_big.setTextColor(Color.parseColor("#00C09F"))

        condition_size = "all,big"
    }

    private fun checked_dontCare_neutralization() {
        dontCare_neutralization.setBackgroundResource(R.drawable.backgroundgreencircle)
        dontCare_neutralization.setTextColor(Color.parseColor("#00C09F"))
        neutralization.setBackgroundResource(R.drawable.backgroundgraycircle)
        neutralization.setTextColor(Color.parseColor("#52443C3C"))

        condition_neutralization = "all"
    }

    private fun checked_neutralization() {
        dontCare_neutralization.setBackgroundResource(R.drawable.backgroundgraycircle)
        dontCare_neutralization.setTextColor(Color.parseColor("#52443C3C"))
        neutralization.setBackgroundResource(R.drawable.backgroundgraycircle)
        neutralization.setTextColor(Color.parseColor("#00C09F"))

        condition_neutralization = "all,neutralization"
    }

    private fun checked_gender_all() {
        gender_all.setBackgroundResource(R.drawable.backgroundgreencircle)
        gender_all.setTextColor(Color.parseColor("#00C09F"))
        gender_man.setBackgroundResource(R.drawable.backgroundgraycircle)
        gender_man.setTextColor(Color.parseColor("#52443C3C"))
        gender_woman.setBackgroundResource(R.drawable.backgroundgraycircle)
        gender_woman.setTextColor(Color.parseColor("#52443C3C"))


        condition_owner_gender = "all"
    }

    private fun checked_gender_man() {
        gender_all.setBackgroundResource(R.drawable.backgroundgraycircle)
        gender_all.setTextColor(Color.parseColor("#52443C3C"))
        gender_man.setBackgroundResource(R.drawable.backgroundgreencircle)
        gender_man.setTextColor(Color.parseColor("#00C09F"))
        gender_woman.setBackgroundResource(R.drawable.backgroundgraycircle)
        gender_woman.setTextColor(Color.parseColor("#52443C3C"))


        condition_owner_gender = "all,man"
    }

    private fun checked_gender_woman() {
        gender_all.setBackgroundResource(R.drawable.backgroundgraycircle)
        gender_all.setTextColor(Color.parseColor("#52443C3C"))
        gender_man.setBackgroundResource(R.drawable.backgroundgraycircle)
        gender_man.setTextColor(Color.parseColor("#52443C3C"))
        gender_woman.setBackgroundResource(R.drawable.backgroundgreencircle)
        gender_woman.setTextColor(Color.parseColor("#00C09F"))


        condition_owner_gender = "all,wm"
    }

    private fun register() {
        auth = FirebaseAuth.getInstance()

        val party_address_detail = edittext_place_detail.text.toString()
        val title = edittext_registration_title.text.toString()
        db = FirebaseFirestore.getInstance()
        val uid = auth.currentUser!!.uid
        //산책에 참여하는 dog 가져와야 한다.
        val dog = "" //얘 나중에 지워도될듯

        var party_date =
            npYear.text.toString() + "/" + npMonth.value.toString() + "/" + npDay.value.toString()
        if (npDay.value.toString().length == 1) {
            party_date =
                npYear.text.toString() + "/" + npMonth.value.toString() + "/" + "0" + npDay.value.toString()
        }

        val party_time =
            npHours.value.toString() + "/" + npMinute.value.toString()
        val explain = edit_registration_explain.text.toString()
        val documentid = db.collection("Matching").document()
        val matchingTime = DoneTime_minute.text.toString()

        val date = party_date.split("/")
        val month: Int = date[1].toInt()
        var day = date[2].toInt()
        val start_time = party_time.split("/")
        val hour: Int = start_time[0].toInt()
        val minute = start_time[1].toInt()
        Log.d("ybyb", "MatchingRegistration__hour ->${hour} minute ->${minute}")


        var Done_hour = hour + (minute + matchingTime.toInt()) / 60
        var Done_minute = (minute + matchingTime.toInt()) % 60
        var DoneTime: String = Done_hour.toString() + "/" + Done_minute.toString()

        Log.d("ybyb", "Done_hour -> ${Done_hour} DoneTime ->${DoneTime}")

        if (Done_hour > 24) {
            day = day + 1
            Done_hour = Done_hour - 24
            DoneTime = Done_hour.toString() + "/" + Done_minute.toString()
            party_date = npYear.text.toString() + "/" + (month + 1) + "/" + day
        }
        Log.d("ybyb", "day -> ${day} Done_hour ->${Done_hour} Done_munute ->${Done_minute}")

        val Conversion_date = (date[0].toInt() * 60 * 24 * 30 * 12)+ (month * 60 * 24 * 30) + (day * 60 * 24) + (hour * 60) + minute

        Log.d("ybyb","Conversion_date ->${Conversion_date}")



        put33(
            party_address,
            party_address_detail,
            party_date,
            party_time,
            title,
            explain,
            matchingTime,
            DoneTime,
            true,
            documentid.id,
            choice_lat.toDouble(),
            choice_lon.toDouble(),
            Conversion_date
        )
        for (i in dogname) {
            db.collection("users").document(uid).collection("dogprofiles")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        val dogs = document.toObject<DogProfile>()
                        if (dogs.dogName == i) {
                            db.collection("Matching").document(documentid.id)
                                .collection("participant").document(uid)
                                .collection("dogprofile").document(i)
                                .set(
                                    Dog_Profile_Item(
                                        dogs.uid,
                                        document.id,//?
                                        dogs?.dogAge,
                                        dogs?.dogName,
                                        dogs?.dogBreed,
                                        dogs?.dogSex,
                                        dogs?.neutering,
                                        dogs?.photoUrl.toString()
                                    )
                                )
                                .addOnSuccessListener {
                                    Log.d("Participant", "MatchingDetailActivity_participant  성공")
                                }
                        }
                    }
                }
        }


    }

    private fun put33(
        party_address: String,
        party_address_detail: String,
        party_date: String,
        party_time: String,
        title: String,
        explain: String,
        matchingTime: String,
        doneTime: String,
        ongoing: Boolean,
        documentId: String,
        latitude: Double,
        longitude: Double,
        Conversion_date : Int
    ) {
        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser!!.uid
        db = FirebaseFirestore.getInstance()
        if (!party_address.equals("") && !party_address_detail.equals("") && !party_date.equals("") && !party_time.equals(
                ""
            )
        ) {
            val time = System.currentTimeMillis()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd kk:mm:ss")
            val curTime = dateFormat.format(Date(time))

            val matching = Matching(
                uid,
                party_address,
                party_address_detail,
                title,
                party_date,
                party_time,
                explain,
                matchingTime,
                doneTime,
                ongoing,
                documentId,
                condition_size,
                condition_neutralization,
                condition_owner_gender,
                latitude,
                longitude,
                Conversion_date
            )

            val date = party_date.split("/")
            Log.d("ybyb", "MatchingRegistration__date ->${date}")
            val month: Int = date[1].toInt() - 1
            var day = date[2].toInt()
            Log.d("ybyb", "MatchingRegistration__month ->${month} day ->${day}")

            val start_time = party_time.split("/")
            val hour: Int = start_time[0].toInt() - 1
            val minute = start_time[1].toInt()
            Log.d("ybyb", "MatchingRegistration__hour ->${hour} minute ->${minute}")

            //매칭 있는 날 아침시간에 "금일은 매칭이 있습니다" 알림 보내기 위함
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, 2021)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)

            val content = title + " 매칭 시작 1시간 전입니다."



            db.collection("Matching").document(documentId).set(matching).addOnSuccessListener {
                Log.d("InsertMatching", "InsertMatching_성공")
                db.collection("Matching").document(documentId).collection("participant")
                    .document(uid)
                    .set(Participant(uid, uid, curTime.toString()))
                    .addOnSuccessListener {
                        sendNotification(title, content, calendar)
                        Log.d("Participant", "MatchingDetailActivity_participant  성공")
                    }.addOnFailureListener {
                        Log.d("Participant", "Participant 실패 이유 : ${it}")
                    }
                val matchingInUsers =
                    Matching_InUsers(uid, uid, matching.title, matching.documentId)
                db.collection("users").document(uid).collection("matching")
                    .document(matching.documentId).set(matchingInUsers).addOnSuccessListener {
                        Log.d("InsertMatchingUsers", "InsertMatchingUsers_성공")
                        Log.d(
                            "InsertMatchingUsers",
                            "InsertMatchingUsers:${matching.documentId},,,,, ${matchingInUsers} "
                        )
                    }.addOnFailureListener {
                        Log.d("InsertMatchingUsers", "InsertMatchingUsers_실패")
                    }
                Intent(this, MatchingDetailActivity::class.java).apply {
                    putExtra("title", title)
                    putExtra("explain", explain)
                    putExtra("leaderuid", uid)
                    putExtra("documentId", documentId)
                }.run { startActivity(this) }
                finish()

            }.addOnFailureListener {
                Log.d("InsertParty", "InsertParty_실패")
            }
            val intent = Intent(this, MatchingDetailActivity::class.java)
            intent.putExtra("state", "after matching registered")
            intent.putExtra("title", title)
            intent.putExtra("explain", explain)
            intent.putExtra("leaderuid", uid)
            intent.putExtra("documentId", documentId)
            startActivity(intent)
            this.finish()
        } else {
            Toast.makeText(this, "빈 칸을 확인해주세요.", Toast.LENGTH_SHORT)
                .show()
        }

    }

    private fun sendNotification(title: String, content: String, calendar: Calendar) {
        val alarmIntent = Intent(this, MyReceiver::class.java).apply {
            action = "com.check.up.setAlarm"
            putExtra("title", title)
            putExtra("content", content)
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

    @SuppressLint("MissingPermission")
    private fun getMyLocation(): LatLng {
        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGPSEnabled: Boolean = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled: Boolean = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        when {
            isNetworkEnabled -> {
                val location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                val getLongtitude = location?.longitude
                val getLatitude = location?.latitude
                Log.e(
                    "joo",
                    "GPSEnabled - 경도 :${getLatitude.toString()}  위도 :${getLongtitude.toString()}"
                )
                var currentLocation = LatLng(getLongtitude!!, getLatitude!!)

                return currentLocation
            }
            isGPSEnabled -> {
                val location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                val getLongtitude = location?.longitude
                val getLatitude = location?.latitude
                Log.e(
                    "joo",
                    "GPSEnabled - 경도 :${getLatitude.toString()}  위도 :${getLongtitude.toString()}"
                )

                var currentLocation = LatLng(getLongtitude!!, getLatitude!!)

                return currentLocation
            }

            else -> {
                Toast.makeText(
                    this,
                    "GPS 권한 오류",
                    Toast.LENGTH_SHORT
                ).show()
                return LatLng(37.537523, 126.96558)
            }
        }
    }

    override fun onCameraMove() {
    }

    override fun onMapClick(p0: LatLng) {
        Log.e("joo", "mapclick hello")
        Intent(this, MatchingRegistration_Area::class.java).apply {
            putExtra("dogchoice_state", "matching_registration")
        }.run { startActivityForResult(this, 100) }

    }


    override fun onInfoWindowClick(p0: Marker) {
    }

    override fun onMarkerDragStart(p0: Marker) {
    }

    override fun onMarkerDrag(p0: Marker) {
    }

    override fun onMarkerDragEnd(p0: Marker) {
    }

    var marker: Marker? = null
    private fun addMarker(lat: Double, lng: Double, title: String) {
        if (marker != null) {
            marker!!.remove()
            marker = null
        }
        val options = MarkerOptions()
        options.position(LatLng(lat, lng))
//        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_mappoint))
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
        options.anchor(0.5f, 1f)
        options.title(title)
        options.draggable(true)
        options.snippet("snippet - $title")
        marker = map!!.addMarker(options)
    }

    private fun getCurrentAddress(latitude: Double, longitude: Double): String {

        //지오코더... GPS를 주소로 변환
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address>?
        addresses = try {
            geocoder.getFromLocation(
                latitude,
                longitude,
                7
            )
        } catch (ioException: IOException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show()
            return "지오코더 서비스 사용불가"
        } catch (illegalArgumentException: IllegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show()
            return "잘못된 GPS 좌표"
        }
        if (addresses == null || addresses.size == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show()
            return "주소 미발견"
        }
        val address: Address = addresses[0]
        Log.e("joo", address.getAddressLine(0).toString())
        val area = address.getAddressLine(0).toString()
        val arealist = area.split(" ")
        return "${arealist[1]} ${arealist[2]} ${arealist[3]}"
    }
}