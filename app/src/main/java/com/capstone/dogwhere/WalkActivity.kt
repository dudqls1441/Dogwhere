package com.capstone.dogwhere

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_b_b_s.*

class WalkActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_walk)


        layout_home.setOnClickListener {
            startActivity(Intent(this, MainMenuActivity::class.java))
            finish()
        }
        layout_matching.setOnClickListener {
            startActivity(Intent(this, MatchingActivity::class.java))
            finish()
        }

        layout_post.setOnClickListener {
            startActivity(Intent(this, BBSActivity::class.java))
            finish()
        }
        layout_chatting.setOnClickListener {
            startActivity(Intent(this,ChattingActivity::class.java))
            finish()
        }

        layout_work.setOnClickListener {
            startActivity(Intent(this, Stop_watchActivity::class.java))
            finish()
        }
    }
}