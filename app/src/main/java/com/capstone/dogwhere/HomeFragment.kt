package com.capstone.dogwhere

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.capstone.dogwhere.Chat.ChatListActivity
import com.capstone.dogwhere.DTO.BBS_CommentItem
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

class HomeFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var auth: FirebaseAuth
    private lateinit var rdb: FirebaseDatabase
    private lateinit var gpsActivity: GpsActivity
    val db = Firebase.firestore
    val adapter = GroupAdapter<GroupieViewHolder>()

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


        db.collection("users").document(uid!!).collection("userprofiles").document(uid).get()
            .addOnSuccessListener { result ->
                val result = result.toObject<UserProfile>()
                Log.e("joo", result.toString())
                Glide.with(context!!).load(result?.profilePhoto).circleCrop().into(user_photo_img)
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



        return view
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

    private fun fm() {

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
    }
}
