package com.capstone.dogwhere

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_matching.*


class MatchingFragment : Fragment(){
    companion object{
        fun newInstance():  MatchingFragment{
            return  MatchingFragment()
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
        val view = inflater.inflate(R.layout.activity_matching, container, false)

        return  view
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        btn_matching_chart.setOnClickListener {
            startActivity(Intent(context, Search_Region::class.java))
            Log.d("지도 누름", "지도 누름")
        }

        btn_matching_map.setOnClickListener {
            startActivity(Intent(context, GeoActivity::class.java))
            Log.d("지도 누름", "지도 누름")
        }
        btn_registration_matching.setOnClickListener {
            startActivity(Intent(getActivity(), MatchingRegistrationActivity::class.java))
        }
    }


}