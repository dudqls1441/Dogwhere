package com.capstone.dogwhere

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.capstone.dogwhere.DTO.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects

class SplashActivity : AppCompatActivity() {
    private lateinit var auth :FirebaseAuth
    private lateinit var db : FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        db = FirebaseFirestore.getInstance()
        val SPLASH_VIEW_TIME: Long = 1000

        auth= FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = auth.currentUser.uid
        Handler().postDelayed({
            db.collection("users").whereEqualTo("uid", uid).get()
                .addOnSuccessListener { result ->
                    val result = result.toObjects<User>()
                    if(result!=null){
                        val intent = Intent(this, MainMenuActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }else{
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                }
            finish()
        },SPLASH_VIEW_TIME)
    }
}