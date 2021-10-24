package com.capstone.dogwhere

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_matching_detail.*
import kotlinx.android.synthetic.main.activity_my_matching_list.*

class MyMatchingListActivity : AppCompatActivity() {
    private val adapter by lazy { PagerAdapter(supportFragmentManager, 2) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_matching_list)


        btn_back_in_mymatchingList.setOnClickListener {
            finish()
        }
        mymatchinglist_tablayout.addTab(mymatchinglist_tablayout.newTab().setText("등록한 매칭"))
        mymatchinglist_tablayout.addTab(mymatchinglist_tablayout.newTab().setText("예약된 매칭"))
        mymatchinglist_tablayout.addTab(mymatchinglist_tablayout.newTab().setText("완료한 매칭"))

        mymatchinglist_ViewPager.adapter = MyMatchingListPagerAdapter(supportFragmentManager, 2)
        mymatchinglist_tablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                mymatchinglist_ViewPager.currentItem = tab!!.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })
        mymatchinglist_ViewPager.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(
                mymatchinglist_tablayout
            )
        )


    }
}


class MyMatchingListPagerAdapter(
    fragmentmanager: FragmentManager, val tabcount: Int
) : FragmentStatePagerAdapter(fragmentmanager) {
    override fun getCount(): Int {
        return tabcount
    }

    override fun getItem(position: Int): Fragment {

        when (position) {
            0 -> return RegisteredMatchingFragment()
            1 -> return ReservedMatchingFragment()
            2 -> return CompletedMatchingFragment()
            else -> return ReservedMatchingFragment()
        }
    }

}