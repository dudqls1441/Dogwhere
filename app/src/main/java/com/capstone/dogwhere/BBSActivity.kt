package com.capstone.dogwhere

import BBS_ImformationBBS
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TableLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_b_b_s.*

class BBSActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_b_b_s)

        tab_layout.addTab(tab_layout.newTab().setText("자유게시판"))
        tab_layout.addTab(tab_layout.newTab().setText("중고고래"))
        tab_layout.addTab(tab_layout.newTab().setText("정보게시판"))
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

        btn_matching_map.setOnClickListener {
            startActivity(Intent(this,GeoActivity::class.java))
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
            0 -> return BBS_freeBBS()
            1 -> return BBS_TransactionBBS()
            2 -> return BBS_ImformationBBS()
            3 -> return BBS_PuppySaleBBS()
            else -> return BBS_freeBBS()
        }

    }
}