package com.capstone.dogwhere

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.capstone.dogwhere.DTO.*
import com.google.android.material.datepicker.MaterialDatePicker.Builder.datePicker
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_matching_list.*
import kotlinx.android.synthetic.main.navi_header.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class HomeFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var auth: FirebaseAuth
    private lateinit var rdb: FirebaseDatabase
    private lateinit var gpsActivity: GpsActivity
    private lateinit var location: Location
    private var Latitude = 0.0
    private var Longitude = 0.0
    private var area = "현재 위치"
    val adapter1 = GroupAdapter<GroupieViewHolder>()
    val adapter2 = GroupAdapter<GroupieViewHolder>()
    val adapter3 = GroupAdapter<GroupieViewHolder>()
    val db = Firebase.firestore
    private val GPS_ENABLE_REQUEST_CODE = 2001
    private val PERMISSIONS_REQUEST_CODE = 100
    var REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )


    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_home, container, false)
        super.onCreate(savedInstanceState)

        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting()
        } else {
            checkRunTimePermission()
        }

        init()
        gpsActivity = GpsActivity.newInstance()
        this.requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frame_weather, GpsActivity())
            .commit()
        auth = FirebaseAuth.getInstance()
        rdb = FirebaseDatabase.getInstance()
        val uid = auth.currentUser?.uid
        var lm = this.requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager
        val isGPSEnabled: Boolean = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled: Boolean = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this.requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                0
            )
        } else {

            when {
                isNetworkEnabled -> {
                    try {
                        location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)!!
                        Longitude = location.longitude
                        Latitude = location.latitude
                    } catch (e: NullPointerException) {
                        Log.e("joo", "Network Exception!! : " + e)
                    }
                }
                isGPSEnabled -> {
                    try {
                        location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)!!
                        Longitude = location.longitude
                        Latitude = location.latitude
                    } catch (e: NullPointerException) {
                        Log.e("joo", "Gps Exception!! : " + e)
                    }

                }
            }

            db.collection("users").document(uid!!).collection("userprofiles").document(uid).get()
                .addOnSuccessListener { result ->
                    val result = result.toObject<UserProfile>()
                    Log.e("joo", result.toString())
                    Glide.with(requireContext()).load(result?.profilePhoto).circleCrop()
                        .into(user_photo_img)
                    user_name_text.setText(result?.userName)
                }


            val week = Calendar.getInstance()
            week.add(Calendar.DATE, -7)
            val beforeweek = SimpleDateFormat("yyyy-MM-dd kk:mm:ss").format(week.time)
            Log.d("yb", "yb 일주일 전 -> ${beforeweek}")

            db.collection("free_bbs").orderBy("heartCnt", Query.Direction.DESCENDING)
                .limit(2).get().addOnSuccessListener {
                    for (document in it) {
                        val hot_bbs_item = home_hot_bbs_Item(
                            document.get("title").toString(),
                            document.get("content").toString(),
                            Integer.parseInt(document.get("heartCnt").toString()),
                            Integer.parseInt(document.get("visitCnt").toString()),
                            document.get("username").toString(),
                            document.get("time").toString(),
                            document.get("uid").toString(),
                            document.get("oid").toString()
                        )
                        adapter2.add(hot_bbs_item)
                    }
                    recyclerview_hot_bbs?.adapter = adapter2
                }
            adapter2.setOnItemClickListener { item, view ->
                item as home_hot_bbs_Item
                Intent(context, BBS_Common_Post::class.java).apply {
                    putExtra("tab", "information_bbs")
                    putExtra("title", (item).title)
                    putExtra("content", (item).content)
                    putExtra("name", (item).username)
                    putExtra("time", (item).time)
                    putExtra("uid", (item).uid)
                    putExtra("oid", (item).oid)
                }.run { context?.startActivity(this) }

            }

            var walkGoal:Walk_Goal?=null
            db.collection("Walk_Goal").document(uid).get()
                .addOnSuccessListener { result ->
                    walkGoal =
                        result.toObject<Walk_Goal>()
                    if (walkGoal?.uid == null) {
                        setting1.visibility = View.VISIBLE
                        write.visibility = View.VISIBLE
                    } else {
                        rating.rating=walkGoal?.percent!!.toFloat()/20
                        goal_day.setText(walkGoal?.goal_day.toString() + "번 ")
                        if (walkGoal?.minute != null) {
                            minute.setText(walkGoal?.minute.toString() + "분씩 ")
                        }
                        if (walkGoal?.km != null) {
                            km.setText(walkGoal?.km.toString() + "km씩 ")
                        }
                        setting2.visibility = View.VISIBLE
                        write2.visibility = View.VISIBLE
                        goal(walkGoal!!)
                    }
                }
            val setting1: TextView = view.findViewById(R.id.setting1)
            val setting2: TextView = view.findViewById(R.id.setting2)
            val view = inflater.inflate(R.layout.walk_goal_alert, null)

            val count:EditText=view.findViewById(R.id.counter)
            val countt:EditText=view.findViewById(R.id.counterr)
            val minute:EditText=view.findViewById(R.id.minute)
            val minutee:EditText=view.findViewById(R.id.minutee)
            val km:EditText=view.findViewById(R.id.km)

            val select_condition_group:RadioGroup=view.findViewById(R.id.select_condition_group)
            val time_stand_content:RelativeLayout=view.findViewById(R.id.time_stand_content)
            val boon: TextView =view.findViewById(R.id.boon)
            val both_stand_content:RelativeLayout=view.findViewById(R.id.both_stand_content)

            setting1.setOnClickListener {
                var condition="time_stand"
                time_stand_content.visibility=View.VISIBLE
                select_condition_group.check(R.id.time_stand)
                select_condition_group.setOnCheckedChangeListener { group, checkedId ->
                    when (checkedId) {
                        R.id.time_stand -> let {
                            both_stand_content.visibility = View.INVISIBLE
                            boon.text = "분씩"
                            time_stand_content.visibility = View.VISIBLE
                            condition = "time_stand"
                        }
                        R.id.dist_stand -> let {
                            both_stand_content.visibility = View.INVISIBLE
                            boon.text = "km씩"
                            time_stand_content.visibility = View.VISIBLE
                            condition = "dist_stand"
                        }
                        R.id.both_stand -> let {
                            time_stand_content.visibility = View.INVISIBLE
                            both_stand_content.visibility = View.VISIBLE
                            condition = "both_stand"
                        }
                    }
                }
                val alertDialog = AlertDialog.Builder(context)
                    .setPositiveButton("저장하기") { dialog, which ->
                            when(condition){
                                "time_stand" -> {
                                    if (count.text.toString() != "" && minute.text.toString() != "") {
                                        db.collection("Walk_Goal").document(uid).set(
                                            Walk_Goal(
                                                uid,
                                                condition,
                                                count.text.toString().toInt(),
                                                minute.text.toString().toInt(),
                                                null,
                                                0,
                                                0,
                                                null
                                            )
                                        )
                                            .addOnSuccessListener {
                                                Toast.makeText(
                                                    context,
                                                    "목표가 등록되었습니다",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                    } else {
                                        Toast.makeText(context, "입력해주세요", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                "dist_stand" -> {
                                    if (count.text.toString() != "" && minute.text.toString() != "") {
                                        db.collection("Walk_Goal").document(uid).set(
                                            Walk_Goal(
                                                uid,
                                                condition,
                                                count.text.toString().toInt(),
                                                null,
                                                minute.text.toString().toInt(),
                                                0,
                                                0,
                                                null
                                            )
                                        )
                                            .addOnSuccessListener {
                                                Toast.makeText(
                                                    context,
                                                    "목표가 등록되었습니다",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                    } else {
                                        Toast.makeText(context, "입력해주세요", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                "both_stand" -> {
                                    if (countt.text.toString() != "" && minutee.text.toString() != "" && km.text.toString() != "") {
                                        db.collection("Walk_Goal").document(uid).set(
                                            Walk_Goal(
                                                uid,
                                                condition,
                                                countt.text.toString().toInt(),
                                                minutee.text.toString().toInt(),
                                                km.text.toString().toInt(),
                                                0,
                                                0,
                                                null
                                            )
                                        )
                                            .addOnSuccessListener {
                                                Toast.makeText(
                                                    context,
                                                    "목표가 등록되었습니다",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                    } else {
                                        Toast.makeText(context, "입력해주세요", Toast.LENGTH_SHORT).show()
                                    }

                                }
                            }
                        var ft: FragmentTransaction? = fragmentManager?.beginTransaction()
                        ft?.detach(this)?.attach(this)?.commit()
                        }
                    .setNeutralButton("취소", null)
                    .create()
                if (view.getParent() != null) (view.getParent() as ViewGroup).removeView(
                    view
                )
                alertDialog.setView(view)
                alertDialog.show()
                alertDialog.window?.setLayout(800, 1000)
            }

            setting2.setOnClickListener {
                var condition = "time_stand"

                        time_stand_content.visibility = View.VISIBLE
                        if (condition == walkGoal?.condition) {
                            minute.setText(walkGoal?.minute.toString())
                            count.setText(walkGoal?.goal_day.toString())
                        } else {
                            minute.setText("")
                            count.setText("")
                        }
                select_condition_group.check(R.id.time_stand)
                select_condition_group.setOnCheckedChangeListener { group, checkedId ->
                    when (checkedId) {
                        R.id.time_stand -> let {
                            condition = "time_stand"
                            both_stand_content.visibility = View.INVISIBLE
                            boon.text = "분씩"
                            time_stand_content.visibility = View.VISIBLE
                            if (condition == walkGoal?.condition) {
                                minute.setText(walkGoal?.minute.toString())
                                count.setText(walkGoal?.goal_day.toString())
                            } else {
                                minute.setText("")
                                count.setText("")
                            }
                        }
                        R.id.dist_stand -> let {
                            condition = "dist_stand"
                            boon.text = "km씩"
                            time_stand_content.visibility = View.VISIBLE
                            both_stand_content.visibility = View.INVISIBLE
                            if (condition == walkGoal?.condition) {
                                minute.setText(walkGoal?.km.toString())
                                count.setText(walkGoal?.goal_day.toString())
                            } else {
                                minute.setText("")
                                count.setText("")
                            }
                        }
                        R.id.both_stand -> let {
                            condition = "both_stand"
                            time_stand_content.visibility = View.INVISIBLE
                            both_stand_content.visibility = View.VISIBLE
                            if (condition == walkGoal?.condition) {
                                countt.setText(walkGoal?.goal_day.toString())
                                minutee.setText(walkGoal?.minute.toString())
                                km.setText(walkGoal?.km.toString())
                            } else {
                                countt.setText("")
                                minutee.setText("")
                                km.setText("")
                            }
                        }
                    }
                }
                val alertDialog = AlertDialog.Builder(context)
                    .setPositiveButton("수정하기") { dialog, which ->
                        when(condition){
                            "time_stand" -> {
                                if (count.text.toString() != "" && minute.text.toString() != "") {
                                    db.collection("Walk_Goal").document(uid).set(
                                        Walk_Goal(
                                            uid,
                                            condition,
                                            count.text.toString().toInt(),
                                            minute.text.toString().toInt(),
                                            null,
                                            0,
                                            0,
                                            null
                                        )
                                    )
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                context,
                                                "목표가 수정되었습니다",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                } else {
                                    Toast.makeText(context, "입력해주세요", Toast.LENGTH_SHORT).show()
                                }
                            }
                            "dist_stand" -> {
                                if (count.text.toString() != "" && minute.text.toString() != "") {
                                    db.collection("Walk_Goal").document(uid).set(
                                        Walk_Goal(
                                            uid,
                                            condition,
                                            count.text.toString().toInt(),
                                            null,
                                            minute.text.toString().toInt(),
                                            0,
                                            0,
                                            null
                                        )
                                    )
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                context,
                                                "목표가 수정되었습니다",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                } else {
                                    Toast.makeText(context, "입력해주세요", Toast.LENGTH_SHORT).show()
                                }
                            }
                            "both_stand" -> {
                                if (countt.text.toString() != "" && minutee.text.toString() != "" && km.text.toString() != "") {
                                    db.collection("Walk_Goal").document(uid).set(
                                        Walk_Goal(
                                            uid,
                                            condition,
                                            countt.text.toString().toInt(),
                                            minutee.text.toString().toInt(),
                                            km.text.toString().toInt(),
                                            0,
                                            0,
                                            null
                                        )
                                    )
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                context,
                                                "목표가 수정되었습니다",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                } else {
                                    Toast.makeText(context, "입력해주세요", Toast.LENGTH_SHORT).show()
                                }

                            }
                        }
                        var ft: FragmentTransaction? = fragmentManager?.beginTransaction()
                        ft?.detach(this)?.attach(this)?.commit()
                    }
                    .setNeutralButton("취소", null)
                    .create()

                if (view.getParent() != null) (view.getParent() as ViewGroup).removeView(
                    view
                )
                alertDialog.setView(view)
                alertDialog.show()
                alertDialog.window?.setLayout(800, 1000)
            }

            Log.d("거리", "위도 : ${Longitude}, 경도 : ${Latitude}")
            area = getCurrentAddress(Latitude, Longitude)
            Log.d("yy", area)

        }

        db.collection("users").document(uid!!).collection("dogprofiles").get()
            .addOnSuccessListener {
                for (document in it) {
                    val result = document.toObject<DogProfile>()
                    Log.d("ybyb", result!!.dogstate.toString())
                    adapter3.add(
                        home_dogstate_item(
                            result?.dogName.toString(),
                            document.id,
                            result?.dogstate.toString()
                        )

                    )
                    Log.d(
                        "ybyb",
                        "yb home -> ${document.id}//---${result?.dogName.toString()} $$$$$$${result?.dogstate.toString()}"
                    )
                    recyclerview_dogstate.adapter=adapter3
                }
            }

        db.collection("Matching").get()
            .addOnSuccessListener {
                Log.d("yy", area)
                for (result in it) {
                    val matching = result.toObject<Matching>()
                    if (matching.place.contains(area)) {
                        Log.d("yy", matching.place)
                        adapter1.add(
                            Walk_Recommend_Item(
                                matching?.uid,
                                matching.documentId,
                                matching?.title,
                                matching?.place,
                                matching?.place_detail,
                                matching?.date,
                                matching?.startime
                            )
                        )
                    } else {
                    }
                    recyclerview_recommendation_walk.adapter = adapter1
                    text_work1.setText(area)
                }

            }

        return view
    }

    private fun init() {
        auth = FirebaseAuth.getInstance()
        val uid = auth.uid.toString()
        val matchinglist = mutableListOf<String>()
        db.collection("users").document(uid).collection("matching").get().addOnSuccessListener {
            for (document in it) {
                if (!uid.equals(document["matchingLeaderUid"].toString())) {
                    matchinglist.add(document["documentId"].toString())
                }
            }
            if (!matchinglist.isEmpty()) {
                //.whereEqualTo("ongoing",false)
                db.collection("Matching").whereIn("documentId", matchinglist)
                    .whereEqualTo("ongoing", false).get()
                    .addOnSuccessListener {
                        completedMatching_count.setText(it.count().toString())
                    }
                db.collection("Matching").whereIn("documentId", matchinglist)
                    .whereEqualTo("ongoing", true).get()
                    .addOnSuccessListener {
                        reservedMatching_count.setText(it.count().toString())
                    }
            } else {
                Log.d("yb", "yb matchingList 비어있음")
                reservedMatching_count.setText("0")
                completedMatching_count.setText("0")
            }
        }
    }
    private fun goal(goal: Walk_Goal){
        auth = FirebaseAuth.getInstance()
        val uid = auth.uid.toString()
        currenttime()?.let {
            db.collection("Walk_Record").document(uid).collection(uid)
                .document(it).get()
                .addOnSuccessListener {
                    Log.e("yy", goal.minute.toString())
                    if(it["timesec"]!=null){
                        when(goal.condition){
                            "time_stand"->{
                                if (it["timesec"].toString().toInt()/ 1000 % 60 >= 5) {
                                    goal2(goal,uid)
                                } //확인용 5초
                            }
                            "dist_stand"->{
                                if (it["distance"].toString().toInt()>= goal.km!!) {
                                    goal2(goal,uid)
                                }
                            }
                            "both_stand"->{
                                if (it["timesec"].toString().toInt()/ 1000 / 60 >= goal.minute!!
                                    && it["distance"].toString().toInt()>= goal.km!!) {
                                    goal2(goal,uid)
                                }
                            }
                        }
                    }else{
                        Log.e("yy", "아직 오늘 기록이 없음")
                    }

                }
        }

    }
    private fun goal2(goal:Walk_Goal, uid:String){
        if(goal.percent<100 && goal.condition_date!=currenttime()){
            db.collection("Walk_Goal").document(uid).update("percent",
                goal.percent+(100/goal.goal_day),
                "success_day",goal.success_day+1,
            "condition_date",currenttime())
                .addOnSuccessListener {
                            Log.e("yy", "확인해보셍 됨")
                }
            }

    }
    private fun currenttime(): String? {
        val time = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-M-d")
        val curTime = dateFormat.format(Date(time))
        Log.d("check", curTime)
        return curTime
    }






    private fun showDialogForLocationServiceSetting() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("위치 서비스 비활성화")
        builder.setMessage(
            """
                앱을 사용하기 위해서는 위치 서비스가 필요합니다.
                위치 설정을 수정하실래요?
                """.trimIndent()
        )
        builder.setCancelable(true)
        builder.setPositiveButton("설정", DialogInterface.OnClickListener { dialog, id ->
            val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE)
        })
        builder.setNegativeButton("취소",
            DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
        builder.create().show()
    }

    private fun checkLocationServicesStatus(): Boolean {
        val locationManager =
            this.requireActivity().getSystemService(LOCATION_SERVICE) as LocationManager
        return (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
    }

    private fun checkRunTimePermission() {

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            this.requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            this.requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
            hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED
        ) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음
        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this.requireActivity(),
                    REQUIRED_PERMISSIONS[0]
                )
            ) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(
                    this.requireContext(),
                    "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                    Toast.LENGTH_LONG
                )
                    .show()
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(
                    this.requireActivity(), REQUIRED_PERMISSIONS,
                    PERMISSIONS_REQUEST_CODE
                )
            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(
                    this.requireActivity(), REQUIRED_PERMISSIONS,
                    PERMISSIONS_REQUEST_CODE
                )
            }
        }
    }

    private fun getCurrentAddress(latitude: Double, longitude: Double): String {

        //지오코더... GPS를 주소로 변환
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses: List<Address>?
        addresses = try {
            geocoder.getFromLocation(
                latitude,
                longitude,
                7
            )
        } catch (ioException: IOException) {
            //네트워크 문제
            Toast.makeText(context, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show()
            return "지오코더 서비스 사용불가"
        } catch (illegalArgumentException: IllegalArgumentException) {
            Toast.makeText(context, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show()
            return "잘못된 GPS 좌표"
        }
        if (addresses == null || addresses.size == 0) {
            Toast.makeText(context, "주소 미발견", Toast.LENGTH_LONG).show()
            return "주소 미발견"
        }
        val address: Address = addresses[0]
        Log.e("joo", address.getAddressLine(0).toString())
        val area = address.getAddressLine(0).toString()
        val arealist = area.split(" ")
        return "${arealist[1]}"
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.matching_list -> matchingList()
            R.id.my_matching -> mymatchingList()
            R.id.choker_buy -> checkActivity()
               // Toast.makeText(activity, "서비스 준비중입니다.", Toast.LENGTH_SHORT).show()
            R.id.menu_list -> yeongbinFucntion()
            R.id.notice -> {
                val intent = Intent(activity, WebView::class.java)
                startActivity(intent)
            }
            R.id.setting -> testmap()
            R.id.btn_logout -> clicklogut()
        }
        layout_drawer.closeDrawers()
        return false
    }


    private fun mymatchingList() {
        activity?.let {
            Log.e("yb", "나의 매칭 리스트로 이동")
            val intent = Intent(activity, MyMatchingListActivity::class.java)
            startActivity(intent)
            it.overridePendingTransition(R.anim.slide_up_enter, R.anim.slide_up_eixt)

        }


    }

    private fun checkActivity() {
        activity?.let {
            Log.e("joo", "채팅방으로 이동")
            val intent = Intent(activity, CheckActivity::class.java)
            startActivity(intent)
            it.overridePendingTransition(R.anim.slide_up_enter, R.anim.slide_up_eixt)
        }
    }

    private fun matchingList() {
        activity?.let {
            Log.d("yeongbin", "매칭 리스트 ㄱㄱ")
            val intent = Intent(activity, Search_Region::class.java)
            startActivity(intent)
            it.overridePendingTransition(R.anim.slide_up_enter, R.anim.slide_up_eixt)
        }
    }

//    private fun gpsstart() {
//        Log.e("joo", "gps 이동")
//        val intent = Intent(activity, GpsActivity::class.java)
//        startActivity(intent)
//    }

    private fun yeongbinFucntion() {
        auth = FirebaseAuth.getInstance()
        val uid = auth.uid.toString()
        val intent = Intent(activity, UserProfileActivity::class.java)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    private fun testmap() {
        Log.e("joo", "지도 이동")
        val intent = Intent(activity, MatchingRegistration_Area::class.java)
        startActivity(intent)
    }

    private fun clicklogut() {
        Log.d("123", "로그아웃")
        auth.signOut()
        activity?.let { MySharedPreferences.clearUser(it) }
        startActivity(Intent(activity, LoginActivity::class.java))

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        menuBar.setOnClickListener {
            layout_drawer.openDrawer(GravityCompat.START)
        }
        naviView.setNavigationItemSelectedListener(this)


        btn_my_notification.setOnClickListener {
            Log.e("ybybyb", "알림 리스트 이동")
            val intent = Intent(activity, NotificationListActivity::class.java)
            startActivity(intent)

        }
        full.setOnClickListener {
            Intent(context, MatchingListActivity::class.java).apply {
                putExtra("address", "전체")
            }.run { startActivity(this) }
        }
        adapter1.setOnItemClickListener { item, view ->
            Log.d("ClickMatching", (item as Walk_Recommend_Item).title)
            Intent(context, MatchingDetailActivity::class.java).apply {
                putExtra("title", (item).title)
                putExtra("leaderuid", (item).uid)
                putExtra("documentId", (item).documentId)
                putExtra("preActivity", "MatchingListActivity")
            }.run {
                startActivity(this)
            }
        }

    }

}
