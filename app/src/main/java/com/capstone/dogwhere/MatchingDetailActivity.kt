package com.capstone.dogwhere

import BBS_CommonBBS
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.bumptech.glide.Glide
import com.capstone.dogwhere.DTO.UserProfile
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_matching_detail.*
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.navi_header.*
import java.lang.Exception

class MatchingDetailActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var rdb: FirebaseDatabase
    private lateinit var db: FirebaseFirestore

    private val adapter by lazy { PagerAdapter(supportFragmentManager, 2) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matching_detail)

        init()
        val btn_back = findViewById<ImageButton>(R.id.btn_back)

        btn_back.setOnClickListener {
            val intent = Intent(this,MatchingListActivity::class.java)
            startActivity(intent)
            finish()
        }
        btn_participate.setOnClickListener {
            participation()
        }





        matching_tab_layout.addTab(matching_tab_layout.newTab().setText("모임설명"))
        matching_tab_layout.addTab(matching_tab_layout.newTab().setText("참여 명단"))

        matching_view_pager.adapter = MatchingPagerAdapter(supportFragmentManager, 2)
        matching_tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                matching_view_pager.currentItem = tab!!.position
            }

        })
        matching_view_pager.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(
                matching_tab_layout
            )
        )


    }

    private fun participation() {


    }

    private fun init() {
        auth = FirebaseAuth.getInstance()
        rdb = FirebaseDatabase.getInstance()
        db = FirebaseFirestore.getInstance()
        if (intent.hasExtra("uid")) {
            val matchingLeaderUid = intent.getStringExtra("uid").toString()
            val matchingtitle = intent.getStringExtra("title").toString()
            db.collection("users").document(matchingLeaderUid).collection("userprofiles")
                .document(matchingLeaderUid).get().addOnSuccessListener {
                    val result = it.toObject<UserProfile>()
                    Glide.with(this).load(result?.profilePhoto).circleCrop()
                        .into(matching_profile_img)
                    matching_title.setText(matchingtitle)
                    matching_profile_name.setText(result?.userName)
                }.addOnFailureListener {
                    Log.d("MatchingDetailActivity", "db 가져오기 실패!")
                }
        } else {

        }
    }

}


class MatchingPagerAdapter(
    fragmentmanager: FragmentManager, val tabcount: Int
) : FragmentStatePagerAdapter(fragmentmanager) {
    override fun getCount(): Int {
        return tabcount
    }

    override fun getItem(position: Int): Fragment {

        when (position) {
            0 -> return PartyexplanationFragment()
            1 -> return PartyListFragment()
            else -> return PartyexplanationFragment()
        }
    }

}