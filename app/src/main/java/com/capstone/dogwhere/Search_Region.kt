package com.capstone.dogwhere

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_search_region.*



class Search_Region : AppCompatActivity() {
    var si:String=""
    var gun:String=""
    var gu:String=""
    var address=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_region)

        var list1 = resources.getStringArray(R.array.spinner_region)
        val adpater1: ArrayAdapter<String> = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1, list1
        )
        listview_1.setAdapter(adpater1)

        listview_1.setOnItemClickListener { parent, view, position, id ->
            listview_2.setAdapter(null)
            listview_3.setAdapter(null)
            Toast.makeText(
                this,
                listview_1.getItemAtPosition(position).toString(),
                Toast.LENGTH_SHORT
            ).show()
                when(position){
                    1 -> setRegionAdapterItem2(R.array.spinner_region_seoul)
                    2 -> setRegionAdapterItem2(R.array.spinner_region_busan)
                    3 -> setRegionAdapterItem2(R.array.spinner_region_daegu)
                    4 -> setRegionAdapterItem2(R.array.spinner_region_incheon)
                    5 -> setRegionAdapterItem2(R.array.spinner_region_gwangju)
                    6 -> setRegionAdapterItem2(R.array.spinner_region_daejeon)
                    7 -> setRegionAdapterItem2(R.array.spinner_region_ulsan)
                    8 -> setRegionAdapterItem2(R.array.spinner_region_sejong)
                    9 -> setRegionAdapterItem2(R.array.spinner_region_gyeonggi)
                    10-> setRegionAdapterItem2(R.array.spinner_region_gangwon)
                    11 -> setRegionAdapterItem2(R.array.spinner_region_chung_buk)
                    12 -> setRegionAdapterItem2(R.array.spinner_region_chung_nam)
                    13 -> setRegionAdapterItem2(R.array.spinner_region_jeon_buk)
                    14 -> setRegionAdapterItem2(R.array.spinner_region_jeon_nam)
                    15 -> setRegionAdapterItem2(R.array.spinner_region_gyeong_buk)
                    16 -> setRegionAdapterItem2(R.array.spinner_region_gyeong_nam)
                    17 -> setRegionAdapterItem2(R.array.spinner_region_jeju)
                }
            si=listview_1.getItemAtPosition(position).toString()
        }
        listview_2.setOnItemClickListener { parent, view, position, id ->
            Toast.makeText(
                this,
                listview_2.getItemAtPosition(position).toString(),
                Toast.LENGTH_SHORT
            ).show()
            if(si=="서울특별시"){
                when(position){
                    0 -> setRegionAdapterItem3(R.array.spinner_region_seoul_gangnam)
                    1 -> setRegionAdapterItem3(R.array.spinner_region_seoul_gangdong)
                    2 -> setRegionAdapterItem3(R.array.spinner_region_seoul_gangbuk)
                    3 -> setRegionAdapterItem3(R.array.spinner_region_seoul_gangseo)
                    4 -> setRegionAdapterItem3(R.array.spinner_region_seoul_gwanak)
                    5 -> setRegionAdapterItem3(R.array.spinner_region_seoul_gwangjin)
                    6 -> setRegionAdapterItem3(R.array.spinner_region_seoul_guro)
                    7 -> setRegionAdapterItem3(R.array.spinner_region_seoul_geumcheon)
                    8 -> setRegionAdapterItem3(R.array.spinner_region_seoul_nowon)
                    9 -> setRegionAdapterItem3(R.array.spinner_region_seoul_dobong)
                    10 -> setRegionAdapterItem3(R.array.spinner_region_seoul_dongdaemun)
                    11 -> setRegionAdapterItem3(R.array.spinner_region_seoul_dongjag)
                    12 -> setRegionAdapterItem3(R.array.spinner_region_seoul_mapo)
                    13 -> setRegionAdapterItem3(R.array.spinner_region_seoul_seodaemun)
                    14 -> setRegionAdapterItem3(R.array.spinner_region_seoul_seocho)
                    15 -> setRegionAdapterItem3(R.array.spinner_region_seoul_seongdong)
                    16 -> setRegionAdapterItem3(R.array.spinner_region_seoul_seongbuk)
                    17 -> setRegionAdapterItem3(R.array.spinner_region_seoul_songpa)
                    18 -> setRegionAdapterItem3(R.array.spinner_region_seoul_yangcheon)
                    19 -> setRegionAdapterItem3(R.array.spinner_region_seoul_yeongdeungpo)
                    20 -> setRegionAdapterItem3(R.array.spinner_region_seoul_yongsan)
                    21 -> setRegionAdapterItem3(R.array.spinner_region_seoul_eunpyeong)
                    22 -> setRegionAdapterItem3(R.array.spinner_region_seoul_jongno)
                    23 -> setRegionAdapterItem3(R.array.spinner_region_seoul_jung)
                    24 -> setRegionAdapterItem3(R.array.spinner_region_seoul_jungnanggu)
                }
            }
            gun=listview_2.getItemAtPosition(position).toString()
        }
        listview_3.setOnItemClickListener { parent, view, position, id ->
            gu=listview_3.getItemAtPosition(position).toString()
        }

        btn_add.setOnClickListener {
            if(si!=""){
                address=si
                if(gun!="") {
                    address = address +" "+ gun
                    if(gu!="") {
                        address = address +" "+ gu
                    }
                }
                Log.e("yy", address+"?????????????")
                val address_state =intent.getStringExtra("address_state")
                if (address_state=="matching_chart"){
                    Intent(this, MatchingListActivity::class.java).apply {
                        putExtra("address", address)
                    }.run { startActivity(this) }
                }else{
                    Intent(this, MatchingRegistrationActivity::class.java).apply {
                        putExtra("address", address)
                    }.run { startActivity(this) }
                    finish()
                }
            }else {
                Toast.makeText(
                    this,
                    "지역을 선택하세요",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setRegionAdapterItem2(region:Int) {
        var list2= resources.getStringArray(region)
        val adpater2: ArrayAdapter<String> = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1, list2
        )
        listview_2.setAdapter(adpater2)
    }
    private fun setRegionAdapterItem3(region:Int) {
        var list3=resources.getStringArray(region)
        val adpater3: ArrayAdapter<String> = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1, list3
        )
        listview_3.setAdapter(adpater3)
    }
}