package com.capstone.dogwhere

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main_menu.*

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
        homeFragment = HomeFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_frame, homeFragment).commit()

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
