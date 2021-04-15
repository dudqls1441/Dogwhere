package com.capstone.dogwhere

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main_menu.*

class MainMenuActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val TAG = MainMenuActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        user?.let {
            // Name, email address, and profile photo Url
            val name = user.displayName
            val email = user.email
            val photoUrl = user.photoUrl

            val emailVerified = user.isEmailVerified
            val uid = user.uid

            Log.d(TAG, name + "&" + email + "&" + photoUrl + "&" + uid)
        }
        btn_logout.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        btn_matching.setOnClickListener {
            startActivity(Intent(this,MatchingActivity::class.java))
            finish()
        }

    }


}
