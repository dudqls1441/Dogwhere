package com.capstone.dogwhere

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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

        menuBar.setOnClickListener {

        }
    }


}