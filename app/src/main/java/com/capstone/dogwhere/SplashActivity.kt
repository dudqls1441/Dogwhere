package com.capstone.dogwhere

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase

class SplashActivity : AppCompatActivity() {
    private lateinit var auth :FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val SPLASH_VIEW_TIME: Long = 1000

        auth= FirebaseAuth.getInstance()
        val user = auth.currentUser

        Handler().postDelayed({

            if (user != null) {
                startActivity(Intent(this,MainMenuActivity::class.java))
            } else {
                startActivity(Intent(this,LoginActivity::class.java))
            }
            finish()
        },SPLASH_VIEW_TIME)
    }
}