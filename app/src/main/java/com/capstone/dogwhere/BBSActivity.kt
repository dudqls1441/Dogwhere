package com.capstone.dogwheredss

import BBS_CommonBBS
import BBS_TransactionBBS
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.capstone.dogwhere.*
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_b_b_s.*
import kotlinx.android.synthetic.main.activity_b_b_s.layout_chatting
import kotlinx.android.synthetic.main.activity_b_b_s.layout_matching
import kotlinx.android.synthetic.main.activity_b_b_s.layout_post
import kotlinx.android.synthetic.main.activity_b_b_s.layout_work
import kotlinx.android.synthetic.main.activity_b_b_s.layout_home


class BBSActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_b_b_s)

        tab_layout.addTab(tab_layout.newTab().setText("자유게시판"))
        tab_layout.addTab(tab_layout.newTab().setText("정보게시판"))
        tab_layout.addTab(tab_layout.newTab().setText("중고거래"))
        tab_layout.addTab(tab_layout.newTab().setText("분양게시판"))

        val pagerAdapter = PagerAdapter(supportFragmentManager, 4)
        view_pager.adapter = pagerAdapter

        tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                view_pager.currentItem = tab!!.position
            }

        })
        view_pager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tab_layout))

        layout_home.setOnClickListener {
            startActivity(Intent(this, MainMenuActivity::class.java))
        }
        layout_matching.setOnClickListener {
            startActivity(Intent(this, MatchingActivity::class.java))
        }

        layout_post.setOnClickListener {
            startActivity(Intent(this, BBSActivity::class.java))
        }
        layout_chatting.setOnClickListener {
            startActivity(Intent(this, ChattingActivity::class.java))
        }

        layout_work.setOnClickListener {
            startActivity(Intent(this, WalkActivity::class.java))
            finish()
        }



    }
}

class PagerAdapter(
    fragmentManager: FragmentManager, val tabcount: Int
) : FragmentStatePagerAdapter(fragmentManager) {
    override fun getCount(): Int {
        return tabcount

    }

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> return BBS_CommonBBS("free_bbs")
            1 -> return BBS_CommonBBS("information_bbs")
            2 -> return BBS_TransactionBBS("transaction_bbs")
            3 -> return BBS_TransactionBBS("puppy_bbs")
            else -> return BBS_CommonBBS("free_bbs")
        }

    }
}