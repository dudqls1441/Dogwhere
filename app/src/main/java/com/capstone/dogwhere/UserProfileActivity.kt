package com.capstone.dogwhere

import BBS_CommonBBS
import BBS_TransactionBBS
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_b_b_s.*
import kotlinx.android.synthetic.main.activity_b_b_s.tab_layout
import kotlinx.android.synthetic.main.activity_b_b_s.view_pager
import kotlinx.android.synthetic.main.activity_user_profile.*

class UserProfileActivity : AppCompatActivity() {
    private val adapter by lazy { PagerAdapter(supportFragmentManager,2) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        profiletab_layout.addTab(profiletab_layout.newTab().setText("반려견 정보"))
        profiletab_layout.addTab(profiletab_layout.newTab().setText("게시글"))

//        val pagerAdapter = PagerAdapter(supportFragmentManager,2)
        profileview_pager.adapter=UserProfileActivity@adapter


        profiletab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                profileview_pager.currentItem = tab!!.position
            }

        })
        profileview_pager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(profiletab_layout))
    }
}

class PagerAdapter(
    fragmentmanager:FragmentManager,val tabcount :Int):FragmentStatePagerAdapter(fragmentmanager){
    override fun getCount(): Int {
        return tabcount
    }

    override fun getItem(position: Int): Fragment {

        when(position){
            0 -> return UserProfileFragment()
            1 -> return BBS_CommonBBS("puppy_bbs")
            else -> return UserProfileFragment()
        }
    }

}
