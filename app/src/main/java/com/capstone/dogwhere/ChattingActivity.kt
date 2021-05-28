package com.capstone.dogwhere

import BBS_CommonBBS
import BBS_TransactionBBS
import GeneralChatFragment
import MatchingChatFragment
import TransactionChatFragment
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.capstone.dogwheredss.BBSActivity
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_b_b_s.*
import kotlinx.android.synthetic.main.activity_chatting.*
import kotlinx.android.synthetic.main.activity_chatting.layout_chatting
import kotlinx.android.synthetic.main.activity_chatting.layout_matching
import kotlinx.android.synthetic.main.activity_chatting.layout_home
import kotlinx.android.synthetic.main.activity_chatting.layout_post
import kotlinx.android.synthetic.main.activity_chatting.layout_work
import kotlinx.android.synthetic.main.activity_main_menu.*

class ChattingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatting)

        matching_tab_layout.addTab(matching_tab_layout.newTab().setText("일반채팅"))
        matching_tab_layout.addTab(matching_tab_layout.newTab().setText("매칭채팅"))
        matching_tab_layout.addTab(matching_tab_layout.newTab().setText("거래채팅"))

        val pagerAdapter = PagerAdapter2(supportFragmentManager, 3)
        matching_pager.adapter = pagerAdapter

        matching_tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                matching_pager.currentItem = tab!!.position
            }

        })
        matching_pager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(matching_tab_layout))


        layout_matching.setOnClickListener {
            startActivity(Intent(this, MatchingActivity::class.java))
        }

        layout_post.setOnClickListener {
            startActivity(Intent(this, BBSActivity::class.java))
        }
        layout_home.setOnClickListener {
            startActivity(Intent(this,MainMenuActivity::class.java))
        }
        layout_work.setOnClickListener {
            startActivity(Intent(this,WalkActivity::class.java))
        }

    }
}

class PagerAdapter2(
    fragmentManager: FragmentManager, val tabcount: Int
) : FragmentStatePagerAdapter(fragmentManager) {
    override fun getCount(): Int {
        return tabcount

    }

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> return GeneralChatFragment()
            1 -> return MatchingChatFragment()
            2 -> return TransactionChatFragment()
            else -> return GeneralChatFragment()
        }

    }
}