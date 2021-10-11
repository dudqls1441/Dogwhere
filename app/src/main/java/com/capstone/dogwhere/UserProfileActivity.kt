package com.capstone.dogwhere

import BBS_CommonBBS
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.bumptech.glide.Glide
import com.capstone.dogwhere.DTO.UserProfile
import com.capstone.dogwhere.FCM.NotificationData
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_b_b_s.*
import kotlinx.android.synthetic.main.activity_matching_detail.*
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.capstone.dogwhere.FCM.PushNotification
import com.capstone.dogwhere.FCM.RetrofitInstance
import com.google.firebase.messaging.FirebaseMessaging

class UserProfileActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    private val adapter by lazy { PagerAdapter(supportFragmentManager, 2) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        init()
        val btn_back = findViewById<ImageButton>(R.id.btn_back)
        btn_back.setOnClickListener {
            val intent = Intent(this,MainMenuActivity::class.java)
            startActivity(intent)
            finish()
        }


        profiletab_layout.addTab(profiletab_layout.newTab().setText("반려견 정보"))
        profiletab_layout.addTab(profiletab_layout.newTab().setText("게시글"))

        profileview_pager.adapter = UserProfileActivity@ adapter




        profiletab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                profileview_pager.currentItem = tab!!.position
            }

        })
        profileview_pager.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(
                profiletab_layout
            )
        )
    }


    private fun init() {
        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser!!.uid
        db = FirebaseFirestore.getInstance()

        db.collection("users").document(uid).collection("userprofiles").document(uid).get()
            .addOnSuccessListener {
                val result = it.toObject<UserProfile>()
                if (result != null) {
                    Glide.with(this).load(result?.profilePhoto).circleCrop()
                        .into(img_userImg)
                    text_user_name.setText(result?.userName)
                    text_user_age.setText(result?.userAge)
                    text_user_sex.setText(result?.userSex)
                } else {
                    Log.d("UserProfileActivity", "UserProfileActivity -> result값 없음")
                }

            }

    }
}


class PagerAdapter(
    fragmentmanager: FragmentManager, val tabcount: Int
) : FragmentStatePagerAdapter(fragmentmanager) {
    override fun getCount(): Int {
        return tabcount
    }

    override fun getItem(position: Int): Fragment {

        when (position) {
            0 -> return UserProfileFragment()
            1 -> return BBS_CommonBBS("puppy_bbs")
            else -> return UserProfileFragment()
        }
    }

}
