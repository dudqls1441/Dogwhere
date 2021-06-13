package com.capstone.dogwhere

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_matching_registration.*

class MatchingRegistrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matching_registration)


        participation_dog_layout.setOnClickListener {
            startActivity(Intent(this,MatchingRegistration_Choice_Dog_Activity::class.java))

        }
        btn_registration.setOnClickListener {
            startActivity(Intent(this,MatchingDetailActivity::class.java))
        }
    }
}