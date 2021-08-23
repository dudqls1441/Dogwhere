package com.capstone.dogwhere

import FindingIdFragment
import FindingPwFragment
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_chatting.*
import kotlinx.android.synthetic.main.activity_finding_id_password.*

class Finding_Id_Password : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finding_id_password)

        btn_back.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        val pagerAdapter = PagerAdapter2(supportFragmentManager, 2)

        finding_id_pw_pager.adapter = pagerAdapter
        finding_id_pw_tab_layout.addTab(finding_id_pw_tab_layout.newTab().setText("아이디 찾기"))
        finding_id_pw_tab_layout.addTab(finding_id_pw_tab_layout.newTab().setText("패스워드 찾기"))

        finding_id_pw_tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                finding_id_pw_pager.currentItem = tab!!.position
            }

        })
        finding_id_pw_pager.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(
                finding_id_pw_tab_layout
            )
        )
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
            0 -> return FindingIdFragment()
            1 -> return FindingPwFragment()
            else -> return FindingIdFragment()
        }

    }
}
