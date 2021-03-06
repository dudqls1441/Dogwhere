package com.capstone.dogwhere

import BBS_CommonBBS
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.bumptech.glide.Glide
import com.capstone.dogwhere.Chat.ChatRoomActivity
import com.capstone.dogwhere.DTO.UserProfile
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.synthetic.main.activity_b_b_s.*
import kotlinx.android.synthetic.main.activity_matching_detail.*
import kotlinx.android.synthetic.main.activity_user_profile.*

class UserProfileActivity : AppCompatActivity() {
    lateinit var auth: FirebaseAuth
    // 필수 정보

    lateinit var db: FirebaseFirestore



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        val profile_name = intent.getStringExtra("name").toString()
        val profile_uid = intent.getStringExtra("uid").toString()

        val adapter = PagerAdapter(supportFragmentManager, 2, profile_uid)


        Log.e("joo", "profile_name,profile_uid -> "+profile_name +"++"+ profile_uid)
        init(profile_uid)
        val btn_back = findViewById<ImageButton>(R.id.btn_back)
        btn_back.setOnClickListener {
            val intent = Intent(this,MainMenuActivity::class.java)
            startActivity(intent)
            finish()
        }
        btn_add_dog.setOnClickListener {
            val intent = Intent(this,DogProfileActivity::class.java)
            startActivity(intent)
            finish()
        }


        profiletab_layout.addTab(profiletab_layout.newTab().setText("반려견 정보"))
        profiletab_layout.addTab(profiletab_layout.newTab().setText("게시글"))

        profileview_pager.adapter = UserProfileActivity@ adapter

        btn_profile_chatting.setOnClickListener {
            val intent = Intent(this, ChatRoomActivity::class.java)
            Log.e("joo", "yourUid:"+profile_uid+"  name:"+profile_name)
            intent.putExtra("yourUid", profile_uid)
            intent.putExtra("name", profile_name)
            startActivity(intent)
        }


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

    private fun init(profile_uid : String) {
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser!!.uid

        Log.d("yb","uid -> ${uid} , profile -> ${profile_uid}")
        if(uid.equals(profile_uid)||profile_uid =="null"){
            btn_profile_chatting.visibility= View.GONE
        }

        db.collection("users").document(profile_uid).collection("userprofiles").document(profile_uid).get()
            .addOnSuccessListener {
                Log.d("yb","yb -> profile_uid- > ${profile_uid}")
                val result = it.toObject<UserProfile>()
                if (result != null) {
                    Glide.with(this).load(result!!.profilePhoto).circleCrop()
                        .into(img_userImg)
                    Log.d("yb","yb -> profilePhoto- > ${result!!.profilePhoto}")
                    text_user_name.setText(result?.userName)
                    text_user_age.setText(result?.userAge)
                    text_user_sex.setText(result?.userSex)
                } else {
                    Log.d("UserProfileActivity", "UserProfileActivity -> result값 없음")
                }

            }

    }
    override fun onBackPressed() {
        super.onBackPressed()

        val intent = Intent(this,MainMenuActivity::class.java)
        startActivity(intent)
        finish()
    }
}


class PagerAdapter(
    fragmentmanager: FragmentManager, val tabcount: Int, val profile_uid: String
) : FragmentStatePagerAdapter(fragmentmanager) {

    override fun getCount(): Int {
        return tabcount
    }

    override fun getItem(position: Int): Fragment {

        when (position) {
            0 -> return UserProfileFragment(profile_uid)
            1 -> return BBS_CommonBBS("free_bbs")
            else -> return UserProfileFragment(profile_uid)
        }
    }

}
