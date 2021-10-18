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
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.capstone.dogwhere.Chat.ChatListActivity
import com.capstone.dogwhere.DTO.Matching
import com.capstone.dogwhere.DTO.UserProfile
import com.capstone.dogwhere.DTO.Walk_Recommend_Item
import com.capstone.dogwhere.DTO.home_hot_bbs_Item
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_home.*
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
    val adapter = GroupAdapter<GroupieViewHolder>()
    val bbs_adapter = GroupAdapter<GroupieViewHolder>()
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
                    } catch (e: Exception) {
                        Log.e("joo", "Network Exception!! : " + e)
                    }
                }
                isGPSEnabled -> {
                    try {
                        location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)!!
                        Longitude = location.longitude
                        Latitude = location.latitude
                    } catch (e: Exception) {
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

            val time = System.currentTimeMillis()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd kk:mm:ss")
            val curTime = dateFormat.format(Date(time))

            Log.d("yb","yb current Time -> ${curTime}")

            val week = Calendar.getInstance()
            week.add(Calendar.DATE, -7)
            val beforeweek = SimpleDateFormat("yyyy-MM-dd kk:mm:ss").format(week.time)
            Log.d("yb","yb 일주일 전 -> ${beforeweek}")

            //orderby 안됨
            db.collection("information_bbs").whereGreaterThan("time",beforeweek).orderBy("time").orderBy("visitCnt",Query.Direction.DESCENDING)
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
                        Log.d("yb", "yb-> hot_bbs->" + document.get("title").toString())
                        bbs_adapter.add(hot_bbs_item)
                    }
                    recyclerview_hot_bbs?.adapter = bbs_adapter
                }
            bbs_adapter.setOnItemClickListener { item, view ->
                db.collection("information_bbs").document((item as home_hot_bbs_Item).oid)
                    .update("visitCnt", FieldValue.increment(1))
                    .addOnSuccessListener { Log.d("yb", "Success Plus Visit Count") }
                    .addOnFailureListener { e -> Log.w("yb", "Error Visit Count", e) }
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

            Log.d("거리", "위도 : ${Longitude}, 경도 : ${Latitude}")
            area = getCurrentAddress(Latitude, Longitude)
            Log.d("yy", area)

        }

        db.collection("Matching").get()
            .addOnSuccessListener {
                Log.d("yy", area)
                for (result in it) {
                    val matching = result.toObject<Matching>()
                    if (matching.place.contains(area)) {
                        Log.d("yy", matching.place)
                        adapter.add(
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
                    recyclerview_recommendation_walk.adapter = adapter
                    text_work1.setText(area)
                }
            }

        return view
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
            R.id.matching_list -> chattingstart()
            R.id.choker_buy -> matchingList()
            R.id.my_matching -> mymatchingList()
            R.id.menu_list -> gpsstart()
            R.id.notice -> Toast.makeText(activity, "rwerqwer", Toast.LENGTH_SHORT).show()
            R.id.setting -> Toast.makeText(activity, "rwerqwer", Toast.LENGTH_SHORT).show()
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

    private fun chattingstart() {
        activity?.let {
            Log.e("joo", "채팅방으로 이동")
            val intent = Intent(activity, ChatListActivity::class.java)
            startActivity(intent)
            it.overridePendingTransition(R.anim.slide_up_enter, R.anim.slide_up_eixt)
        }
    }

    private fun matchingList() {
        activity?.let {
            Log.d("yeongbin", "매칭 리스트 ㄱㄱ")
            val intent = Intent(activity, MatchingListActivity::class.java)
            startActivity(intent)
            it.overridePendingTransition(R.anim.slide_up_enter, R.anim.slide_up_eixt)
        }
    }

    private fun gpsstart() {
        Log.e("joo", "gps 이동")
        val intent = Intent(activity, GpsActivity::class.java)
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

        btn_profile.setOnClickListener {
            startActivity(Intent(context, UserProfileActivity::class.java))
        }
        full.setOnClickListener {
            Intent(context, MatchingListActivity::class.java).apply {
                putExtra("address", "전체")
            }.run { startActivity(this) }
        }
        adapter.setOnItemClickListener { item, view ->
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
