package com.capstone.dogwhere

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main_menu.*

class MainMenuActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var auth: FirebaseAuth
    private lateinit var rdb: FirebaseDatabase
    private val TAG = MainMenuActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        initLayout()

        auth = FirebaseAuth.getInstance()
        rdb = FirebaseDatabase.getInstance()
        val user = auth.currentUser

        user?.let {
            // Name, email address, and profile photo Url
            val name = user.displayName
            val email = user.email
            val photoUrl = user.photoUrl

//            val emailVerified = user.isEmailVerified
            val uid = user.uid

            Log.d(TAG, name + "&" + email + "&" + photoUrl + "&" + uid)
        }

        val postLef = rdb.getReference().child("userprofiles")



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

        menuBar.setOnClickListener {
            layout_drawer.openDrawer(GravityCompat.START)
        }
        naviView.setNavigationItemSelectedListener(this)
    }


}
