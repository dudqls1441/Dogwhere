package com.capstone.dogwhere

import BBS_CommonBBS
import BBS_TransactionBBS
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_b_b_s.*
import kotlinx.android.synthetic.main.activity_home.*

class BbsFragment : Fragment(){
    companion object{
        fun newInstance():BbsFragment{
            return BbsFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_b_b_s, container, false)

        return  view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        tab_layout.addTab(tab_layout.newTab().setText("자유게시판"))
        tab_layout.addTab(tab_layout.newTab().setText("정보게시판"))
        tab_layout.addTab(tab_layout.newTab().setText("중고거래"))
        tab_layout.addTab(tab_layout.newTab().setText("분양게시판"))

        val pagerAdapter = PagerAdapter(childFragmentManager, 4)
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
}
