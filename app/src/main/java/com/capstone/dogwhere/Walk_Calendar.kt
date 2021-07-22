package com.capstone.dogwhere

import android.os.Bundle
import android.view.View
import android.widget.CalendarView.OnDateChangeListener
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_walk__calendar.*


class Walk_Calendar : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_walk__calendar)
        layout_bottom2.setVisibility(View.INVISIBLE)
        layout_bottom1.setVisibility(View.VISIBLE)

        calendar.setOnDateChangeListener(OnDateChangeListener { view, year, month, dayOfMonth ->
            layout_bottom2.setVisibility(View.VISIBLE)
            layout_bottom1.setVisibility(View.INVISIBLE)

        })
    }
}