package com.capstone.dogwhere

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_walk.*

class WalkFragment : Fragment(){
    companion object{
        fun newInstance(): WalkFragment{
            return WalkFragment()
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
        val view = inflater.inflate(R.layout.activity_walk, container, false)
        return  view
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        calendar.setOnClickListener {
            startActivity(Intent(activity, Walk_Calendar::class.java))
        }
        btn_stopwatch.setOnClickListener {
            startActivity(Intent(activity, Stop_watchActivity::class.java))

        }
    }
}