package com.capstone.dogwhere

import BBS_CommonBBS
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_matching_detail.*
import kotlinx.android.synthetic.main.activity_user_profile.*

class MatchingDetailActivity : AppCompatActivity() {
    private val adapter by lazy { PagerAdapter(supportFragmentManager, 2) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matching_detail)

        matching_tab_layout.addTab(matching_tab_layout.newTab().setText("모임설명"))
        matching_tab_layout.addTab(matching_tab_layout.newTab().setText("참여 명단"))

        matching_view_pager.adapter = MatchingPagerAdapter(supportFragmentManager,2)
        matching_tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                matching_view_pager.currentItem = tab!!.position
            }

        })
        matching_view_pager.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(
                matching_tab_layout
            )
        )


    }
}


class MatchingPagerAdapter(
    fragmentmanager: FragmentManager, val tabcount: Int
) : FragmentStatePagerAdapter(fragmentmanager) {
    override fun getCount(): Int {
        return tabcount
    }

    override fun getItem(position: Int): Fragment {

        when (position) {
            0 -> return PartyexplanationFragment()
            1 -> return PartyListFragment()
            else -> return PartyexplanationFragment()
        }
    }

}