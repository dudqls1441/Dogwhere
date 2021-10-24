package com.capstone.dogwhere

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main_menu.*
import kotlinx.android.synthetic.main.common_bbs_item.*

class MainMenuActivity : AppCompatActivity(),
    BottomNavigationView.OnNavigationItemSelectedListener {
    private lateinit var homeFragment: HomeFragment
    private lateinit var walkFragment: WalkFragment
    private lateinit var matchingFragment: MatchingFragment
    private lateinit var bbsFragment: BbsFragment
    private lateinit var chattingFragment: ChattingFragment

    companion object {
        const val TAG: String = "로그"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        bottom_nav.setOnNavigationItemSelectedListener(this)

        if(intent.getStringExtra("state").toString()!="null"){
            Log.e("yy",intent.getStringExtra("state").toString())
            bottom_nav.selectedItemId= R.id.bbsItem

        }else{
            bottom_nav.selectedItemId= R.id.homeItem
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.homeItem -> {
                homeFragment = HomeFragment.newInstance()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_frame, homeFragment).commit()
            }
            R.id.walkItem -> {
                walkFragment = WalkFragment.newInstance()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_frame, walkFragment).commit()
            }
            R.id.matchingItem -> {
                matchingFragment = MatchingFragment.newInstance()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_frame, matchingFragment).commit()
            }
            R.id.bbsItem -> {
                bbsFragment = BbsFragment.newInstance()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_frame, bbsFragment).commit()
            }
            R.id.chattingItem -> {
                chattingFragment = ChattingFragment.newInstance()
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_frame, chattingFragment).commit()
            }
        }
        return true
    }
}
