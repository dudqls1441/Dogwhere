package com.capstone.dogwhere

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_matching.*

class MatchingHomeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {


        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_matching, container, false)


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