package com.capstone.dogwhere

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_matching.*

class MatchingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matching)

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = MatchingHomeFragment()

        fragmentTransaction.add(R.id.matchingFragment,fragment)
        fragmentTransaction.commit()

        layout_post.setOnClickListener {
            startActivity(Intent(this, BBSActivity::class.java))
            finish()
        }
        layout_home.setOnClickListener {
            startActivity(Intent(this,MainMenuActivity::class.java))
            finish()
        }
        layout_work.setOnClickListener {
            startActivity(Intent(this,Stop_watchActivity::class.java))
            finish()
        }
        layout_chatting.setOnClickListener {
            startActivity(Intent(this,ChattingActivity::class.java))
            finish()
        }
    }


}