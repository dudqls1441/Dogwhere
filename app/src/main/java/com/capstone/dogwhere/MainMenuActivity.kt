package com.capstone.dogwhere

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.GravityCompat
import com.bumptech.glide.Glide
import com.capstone.dogwhere.DTO.User
import com.capstone.dogwhere.DTO.UserProfile
import com.capstone.dogwhere.DTO.Walk_Record
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main_menu.*
import kotlinx.android.synthetic.main.navi_header.*

class MainMenuActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var auth: FirebaseAuth
    private lateinit var rdb: FirebaseDatabase
    private val TAG = MainMenuActivity::class.java.simpleName
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        initLayout()
        setFragment()

        auth = FirebaseAuth.getInstance()
        rdb = FirebaseDatabase.getInstance()
        val uid = auth.currentUser.uid


        val postLef = rdb.getReference().child("userprofiles")

        db.collection("users").document(uid).collection("userprofiles").document(uid).get()
            .addOnSuccessListener { result ->
                val result = result.toObject<UserProfile>()
                Log.e("joo",result.toString())
                Glide.with(this).load(result?.profilePhoto).into(user_photo_img)
                user_name_text.setText(result?.userName)
            }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.account -> Toast.makeText(this, "rwerqwer", Toast.LENGTH_SHORT).show()
            R.id.itme2 -> Toast.makeText(this, "rwerqwer", Toast.LENGTH_SHORT).show()
        }
        layout_drawer.closeDrawers()
        return false
    }

    override fun onBackPressed() {
        if (layout_drawer.isDrawerOpen(GravityCompat.START)) {
            layout_drawer.closeDrawers()
        } else {
            super.onBackPressed()
        }
    }

    private fun initLayout() {
        btn_logout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        btn_matching.setOnClickListener {
            startActivity(Intent(this, MatchingActivity::class.java))
            finish()
        }

        btn_bbs.setOnClickListener {
            startActivity(Intent(this, BBSActivity::class.java))
            finish()
        }
        btn_chatting.setOnClickListener {
            startActivity(Intent(this,ChattingActivity::class.java))
            finish()
        }

        btn_user_walk.setOnClickListener {
            startActivity(Intent(this, Stop_watchActivity::class.java))
            finish()
        }
        menuBar.setOnClickListener {
            layout_drawer.openDrawer(GravityCompat.START)
        }
        naviView.setNavigationItemSelectedListener(this)

    }
    private fun setFragment(){
        val transaction =supportFragmentManager.beginTransaction()
        val fragment = WorkIndexFragment()

        transaction.add(R.id.layout_today_workIndex,fragment)
        transaction.commit()

    }



}
