package com.capstone.dogwhere

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.capstone.dogwheredss.BBSActivity
import kotlinx.android.synthetic.main.activity_b_b_s.*
import kotlinx.android.synthetic.main.activity_matching.*
import kotlinx.android.synthetic.main.activity_walk.*

class WalkActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_walk)


        layout_home_inwalk.setOnClickListener {
            startActivity(Intent(this, MainMenuActivity::class.java))
            finish()
        }
        layout_matching_inwalk.setOnClickListener {
            startActivity(Intent(this, MatchingActivity::class.java))
            finish()
        }

        layout_post_inwalk.setOnClickListener {
            startActivity(Intent(this, BBSActivity::class.java))
            finish()
        }
        layout_chatting_inwalk.setOnClickListener {
            startActivity(Intent(this,ChattingActivity::class.java))
            finish()
        }

        layout_work_inwalk.setOnClickListener {
            startActivity(Intent(this, Stop_watchActivity::class.java))
            finish()
        }
        calendar.setOnClickListener {
            startActivity(Intent(this, Walk_Calendar::class.java))
            finish()
        }
        btn_stopwatch.setOnClickListener {
            startActivity(Intent(this, Stop_watchActivity::class.java))
            finish()
        }
    }
}