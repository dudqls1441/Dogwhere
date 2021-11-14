package com.capstone.dogwhere.DTO

import android.os.CountDownTimer
import android.util.Log
import android.view.View
import com.capstone.dogwhere.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.matching_list_item.view.*
import kotlinx.android.synthetic.main.my_ongoing_matching_item.view.*
import kotlinx.android.synthetic.main.my_ongoing_matching_item.view.main_RelativeLayout
import kotlinx.android.synthetic.main.my_ongoing_matching_item.view.text_place
import kotlinx.android.synthetic.main.my_ongoing_matching_item.view.text_time
import kotlinx.android.synthetic.main.my_ongoing_matching_item.view.matching_RemainingTime
import kotlinx.android.synthetic.main.my_ongoing_matching_item.view.text_title
import kotlinx.android.synthetic.main.my_registered_matching_item.view.*
import kotlinx.android.synthetic.main.navi_header.*
import java.text.SimpleDateFormat
import java.util.*

class Matching_Reserved_List_Item(
    val leaderUid : String,
    val documentId : String,
    val title: String,
    val time: String,
    val place: String,
    val date : String,
    val startTime : String,
    val doneTime :String
) : Item<GroupieViewHolder>() {


    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.text_title.text = title
        viewHolder.itemView.text_place.text = place
        viewHolder.itemView.text_time.text = time
        setting(viewHolder)
    }

    override fun getLayout(): Int {
        return R.layout.my_ongoing_matching_item
    }

    private fun setting(viewHolder: GroupieViewHolder){
        val time = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yy/MM/dd")
        val curDate = dateFormat.format(Date(time))

        val timeFormat = SimpleDateFormat("HH/mm")
        val curTime = timeFormat.format(Date(time))

        val now_hour = curTime.split("/")[0]
        val now_minute = curTime.split("/")[1]

        val now_time_minute = now_hour.toInt() * 60 + now_minute.toInt()

        //시작 시간
        val splitedStartTime = startTime.split("/")
        Log.d("ybyb", "startime-> ${splitedStartTime}")

        val start_time_minute = splitedStartTime[0].toInt() * 60 + splitedStartTime[1].toInt()
        Log.d("ybyb", "시작 시간 분환산 -start_time_minute :${start_time_minute}")

        //종료 시간
        val splitedDoneTime = doneTime.split("/")
        Log.d("ybyb", "splitedDoneTime-> ${splitedDoneTime}")

        val Done_time_minute =
            splitedDoneTime[0].toInt() * 60 + splitedDoneTime[1].toInt()
        Log.d("ybyb", "종료 시간 분환산 -Done_time_minute :${Done_time_minute}")


        if(curDate.equals(date)){
            viewHolder.itemView.main_RelativeLayout.setBackgroundResource(R.drawable.round_mint.toInt())
            if (now_time_minute < start_time_minute) {
                val countDownTimer = object : CountDownTimer(200000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        viewHolder.itemView.text_title.setTextColor(R.color.main_mint.toInt())
                        viewHolder.itemView.text_place.setTextColor(R.color.main_mint.toInt())
                        viewHolder.itemView.text_time.setTextColor(R.color.main_mint.toInt())
                        viewHolder.itemView.matching_RemainingTime.visibility= View.VISIBLE
                        viewHolder.itemView.matching_RemainingTime.setText(
                            getTime(
                                splitedStartTime[0].toInt(),
                                splitedStartTime[1].toInt()
                            )
                        )
                    }
                    override fun onFinish() {}
                }
                countDownTimer.start()
            }else if(now_time_minute >= start_time_minute || now_time_minute <= Done_time_minute){
                viewHolder.itemView.text_title.setTextColor(R.color.main_mint.toInt())
                viewHolder.itemView.text_place.setTextColor(R.color.main_mint.toInt())
                viewHolder.itemView.text_time.setTextColor(R.color.main_mint.toInt())
                viewHolder.itemView.matching_RemainingTime.visibility= View.VISIBLE
                viewHolder.itemView.matching_RemainingTime.setText("현재 진행중인 매칭입니다")
            }
        }


    }
    //시간 구하는 메서드
    private fun getTime(hour: Int, minute: Int): String? {
        val date = Date()
        val calendar: Calendar = GregorianCalendar()
        calendar.time = date
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val day = calendar[Calendar.DAY_OF_MONTH]
        val c_hour = calendar[Calendar.HOUR_OF_DAY]
        val c_min = calendar[Calendar.MINUTE]
        val c_sec = calendar[Calendar.SECOND]


        val baseCal: Calendar = GregorianCalendar(year, month, day, c_hour, c_min, c_sec)
        val targetCal: Calendar = GregorianCalendar(year, month, day, hour, minute, 0) //비교대상날짜
        val diffSec = (targetCal.timeInMillis - baseCal.timeInMillis) / 1000
        val diffDays = diffSec / (24 * 60 * 60)
        targetCal.add(Calendar.DAY_OF_MONTH, (-diffDays).toInt())
        val hourTime = Math.floor((diffSec / 3600).toDouble()).toInt()
        val minTime = Math.floor(((diffSec - 3600 * hourTime) / 60).toDouble()).toInt()
        val secTime =
            Math.floor((diffSec - 3600 * hourTime - 60 * minTime).toDouble()).toInt()
        val hour = String.format("%02d", hourTime)
        val min = String.format("%02d", minTime)
        val sec = String.format("%02d", secTime)
        return hour + " 시간 " + min + " 분 " + sec + "초 남았습니다."
    }
}