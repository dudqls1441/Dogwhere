package com.capstone.dogwhere

import GeneralChatFragment
import MatchingChatFragment
import TransactionChatFragment
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_chatting.*

class ChattingFragment : Fragment(){
    companion object{
        fun newInstance():ChattingFragment{
            return ChattingFragment()
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
        val view = inflater.inflate(R.layout.activity_chatting, container, false)
        return  view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        matching_tab_layout.addTab(matching_tab_layout.newTab().setText("일반채팅"))
        matching_tab_layout.addTab(matching_tab_layout.newTab().setText("매칭채팅"))
        matching_tab_layout.addTab(matching_tab_layout.newTab().setText("거래채팅"))

        val pagerAdapter = PagerAdapter2(childFragmentManager, 3)
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
}