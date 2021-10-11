package com.capstone.dogwhere

import android.Manifest
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.*
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.capstone.dogwhere.Chat.ChatListActivity
import com.capstone.dogwhere.DTO.BBS_CommentItem
import com.capstone.dogwhere.DTO.*
import com.capstone.dogwhere.DTO.UserProfile
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_b_b_s__common_post.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.navi_header.*
import com.capstone.dogwhere.DTO.home_hot_bbs_Item
import java.io.IOException
import java.util.*
import kotlin.properties.Delegates


class HomeFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var auth: FirebaseAuth
    private lateinit var rdb: FirebaseDatabase
    private lateinit var gpsActivity: GpsActivity
    private lateinit var location:Location
    private var Latitude by Delegates.notNull<Double>()
    private var Longitude by Delegates.notNull<Double>()
    private var area= "현재 위치"
    val adapter = GroupAdapter<GroupieViewHolder>()
    val db = Firebase.firestore


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
                    location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)!!
                    Longitude = location.longitude
                    Latitude = location.latitude
                }
                isGPSEnabled -> {
                    location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)!!
                    Longitude = location.longitude
                    Latitude = location.latitude

                }
            }

        db.collection("users").document(uid!!).collection("userprofiles").document(uid).get()
            .addOnSuccessListener { result ->
                val result = result.toObject<UserProfile>()
                Log.e("joo", result.toString())
                Glide.with(requireContext()).load(result?.profilePhoto).circleCrop().into(user_photo_img)
                user_name_text.setText(result?.userName)
            }

        db.collection("information_bbs").orderBy("visitCnt",Query.Direction.DESCENDING).limit(3).get().addOnSuccessListener {
            for (document in it) {
                val hot_bbs_item = home_hot_bbs_Item(
                    document.get("title").toString(),
                    document.get("comment").toString(),
                    Integer.parseInt(document.get("heartCnt").toString()),
                    Integer.parseInt(document.get("visitCnt").toString())

                )
                adapter.add(hot_bbs_item)
            }
            recyclerview_hot_bbs?.adapter = adapter
        }


            Log.d("거리", "위도 : ${Longitude}, 경도 : ${Latitude}")
            area = getCurrentAddress(Latitude, Longitude)
            Log.d("yy",area)

        }

        db.collection("Matching").get()
            .addOnSuccessListener {
                Log.d("yy",area)
                for (result in it){
                    val matching = result.toObject<Matching>()
                    if(matching.place.contains(area)){
                        Log.d("yy",matching.place)
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
                    }else{
                    }
                    recyclerview_recommendation_walk.adapter=adapter
                    text_work1.setText(area)
                }
            }

        return view
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
                putExtra("preActivity","MatchingListActivity")
            }.run {
                startActivity(this)
            }
        }

    }

}
