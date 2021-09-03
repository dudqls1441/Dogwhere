package com.capstone.dogwhere

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.SystemClock
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.capstone.dogwhere.DTO.UserProfile
import com.capstone.dogwhere.DTO.Walk_Record
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.prolificinteractive.materialcalendarview.*
import kotlinx.android.synthetic.main.activity_stop_watch.*
import kotlinx.android.synthetic.main.activity_walk__calendar.*
import org.jetbrains.anko.toast
import java.util.*


class Walk_Calendar : AppCompatActivity() {

    var memo: String = ""
    var date: String = ""
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_walk__calendar)

        layout_bottom2.setVisibility(View.INVISIBLE) //클릭-일
        layout_bottom.setVisibility(View.VISIBLE) //월평균
        val instance = Calendar.getInstance()
        month1.text = (instance.get(Calendar.MONTH) + 1).toString() //해당 월 나오게 다시하기
        btn_back.setOnClickListener { finish() }

        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser!!.uid
        val db = Firebase.firestore
        val calList_stamp = ArrayList<CalendarDay>()
        var month_timesec=0 //월별 통계 - 시간
        var month_dist=0.0 //월별 통계 - 거리

        db.collection("Walk_Record").document(uid).collection(uid)
            .get()
            .addOnSuccessListener { results ->
                calendar.addDecorators(
                    //    OtherDayDecorator(this),
                    SundayDecorator(),
                    SaturdayDecorator(),
                    OneDayDecorator(this)
                )
                for (result in results) {
                    val It = result.toObject<Walk_Record>()
                    if (!(It?.timesec == "" || It?.timesec == null)) {
                        val arr = result.get("date").toString().split("-")
                        if(arr[1].toInt().toString()==month1.text && arr[0].toInt().toString()==instance.get(Calendar.YEAR).toString()){
                            month_timesec+=result.get("timesec").toString().toInt()
                            month_dist+=result.get("distance").toString().toDouble()
                        }
                        if (It?.timesec!!.toInt() >= 2000 && It?.timesec!!.toInt() < 4000) {
                            calList_stamp.clear()
                            calList_stamp.add(
                                CalendarDay.from(
                                    arr[0].toInt(),
                                    arr[1].toInt() - 1,
                                    arr[2].toInt()
                                )
                            )
                            Log.e("yy", calList_stamp.toString())
                            for (calDay in calList_stamp) {
                                calendar.addDecorators(Stamp(this, calDay, "1"))
                            }
                        } else if (It?.timesec!!.toInt() >= 4000 && It?.timesec!!.toInt() < 6000) {
                            val arr = result.get("date").toString().split("-")
                            calList_stamp.clear()
                            calList_stamp.add(
                                CalendarDay.from(
                                    arr[0].toInt(),
                                    arr[1].toInt() - 1,
                                    arr[2].toInt()
                                )
                            )
                            Log.e("yy", calList_stamp.toString())
                            for (calDay in calList_stamp) {
                                calendar.addDecorators(Stamp(this, calDay, "2"))
                            }
                        } else if (It?.timesec!!.toInt() >= 6000) {
                            val arr = result.get("date").toString().split("-")
                            calList_stamp.clear()
                            calList_stamp.add(
                                CalendarDay.from(
                                    arr[0].toInt(),
                                    arr[1].toInt() - 1,
                                    arr[2].toInt()
                                )
                            )
                            Log.e("yy", calList_stamp.toString())
                            for (calDay in calList_stamp) {
                                calendar.addDecorators(Stamp(this, calDay, "3"))
                            }
                        }
                    }

                }
                distance_month.setText((Math.round(month_dist*100)/100f).toString())
                time_month.setText(getTime(month_timesec))
            }

        calendar.setOnMonthChangedListener(OnMonthChangedListener { widget, date -> //월 바꿀때
            Log.e("yy",date.toString())
            layout_bottom2.setVisibility(View.INVISIBLE)
            layout_bottom.setVisibility(View.VISIBLE)
            month1.setText((date.month.toInt()+1).toString())
            month_timesec=0
            month_dist=0.0
            db.collection("Walk_Record").document(uid).collection(uid)
                .get()
                .addOnSuccessListener { results ->
                    calendar.addDecorators(
                        //    OtherDayDecorator(this),
                        SundayDecorator(),
                        SaturdayDecorator(),
                        OneDayDecorator(this)
                    )
                    for (result in results) {
                        val It = result.toObject<Walk_Record>()
                        if (!(It?.timesec == "")) {
                            val arr = result.get("date").toString().split("-")
                            if (arr[1].toInt().toString() == month1.text && arr[0].toInt().toString()==date.year.toString()) {
                                month_timesec += result.get("timesec").toString().toInt()
                                month_dist+=result.get("distance").toString().toDouble()
                            }
                        }
                    }
                    time_month.setText(getTime(month_timesec))
                    distance_month.setText((Math.round(month_dist*100)/100f).toString())
                }
        })

        calendar.setOnDateChangedListener(OnDateSelectedListener { widget, date, selected ->  //일 바꿀때
            val year = date.year
            val month = date.month
            val dayOfMonth = date.day
            layout_bottom2.setVisibility(View.VISIBLE)
            layout_bottom.setVisibility(View.INVISIBLE)

            diaryTextView.text = String.format("%d / %d / %d", year, month + 1, dayOfMonth)
            checkedDay(year, month, dayOfMonth)


            save_Btn.setOnClickListener {
                memo = contextEditText.text.toString()
                contextEditText.setText("")
                saveDiary(year, month, dayOfMonth, memo)
                toast(memo + "데이터를 저장했습니다.")
                textView2.text = memo
                textView2.visibility = View.VISIBLE
                cha_Btn.visibility = View.VISIBLE
                del_Btn.visibility = View.VISIBLE
                contextEditText.visibility = View.INVISIBLE
                save_Btn.visibility = View.INVISIBLE
            }
            cha_Btn.setOnClickListener { // 수정 버튼을 누를 시
                contextEditText.visibility = View.VISIBLE
                textView2.visibility = View.INVISIBLE
                contextEditText.setText(memo) // editText에 textView에 저장된
                // 내용을 출력
                save_Btn.visibility = View.VISIBLE
                cha_Btn.visibility = View.INVISIBLE
                del_Btn.visibility = View.INVISIBLE
                textView2.text = "${contextEditText.getText()}"
            }

            del_Btn.setOnClickListener {
                textView2.visibility = View.INVISIBLE
                contextEditText.visibility = View.VISIBLE
                save_Btn.visibility = View.VISIBLE
                cha_Btn.visibility = View.INVISIBLE
                del_Btn.visibility = View.INVISIBLE
                removeDiary(year, month, dayOfMonth)
                toast("데이터를 삭제했습니다.")
            }

        })
    }

    fun checkedDay(cYear: Int, cMonth: Int, cDay: Int) {
        textView2.visibility = View.INVISIBLE
        cha_Btn.visibility = View.INVISIBLE
        del_Btn.visibility = View.INVISIBLE
        contextEditText.visibility = View.VISIBLE
        save_Btn.visibility = View.VISIBLE


        date = "" + cYear + "-" + (cMonth + 1) + "" + "-" + cDay
        Log.e("yy", date.toString())
// 저장할 데이터 이름 설정. Ex) 2019-01-20
        textView2.setText("")
        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser!!.uid
        val db = Firebase.firestore

        memo = ""
        db.collection("Walk_Record").document(uid).collection(uid).document(date.toString())
            .get()
            .addOnSuccessListener { result ->
                if (result.get("time").toString() == "null" || result.get("time")
                        .toString() == ""
                ) {
                    time_value.setText("00:00:00:00")
                    dist_value.setText("0.0")

                } else {
                    time_value.setText(result.get("time").toString()) //시간 축적
                    dist_value.setText((Math.round(result.get("distance").toString().toDouble()*100)/100f).toString())
                }
                if (result.get("memo").toString() == "null" || result.get("memo")
                        .toString() == ""
                ) {
                    textView2.visibility = View.INVISIBLE
                    cha_Btn.visibility = View.INVISIBLE
                    del_Btn.visibility = View.INVISIBLE
                    contextEditText.visibility = View.VISIBLE
                    save_Btn.visibility = View.VISIBLE
                } else {
                    textView2.visibility = View.VISIBLE
                    cha_Btn.visibility = View.VISIBLE
                    del_Btn.visibility = View.VISIBLE
                    contextEditText.visibility = View.INVISIBLE
                    save_Btn.visibility = View.INVISIBLE
                    memo = result.get("memo").toString()
                    Log.e("yy", memo + "__________")
                    textView2.text = memo// textView에 str 출력
                }
            }
            .addOnFailureListener { e ->

            }

    }

    @SuppressLint("WrongConstant")
    fun saveDiary(cYear: Int, cMonth: Int, cDay: Int, memo: String) {
        date = "" + cYear + "-" + (cMonth + 1) + "" + "-" + cDay
        Log.e("yy", date.toString())
// 저장할 데이터 이름 설정. Ex) 2019-01-20

        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser!!.uid
        val db = Firebase.firestore
        db.collection("Walk_Record").document(uid).collection(uid).document(date.toString())
            .get()
            .addOnSuccessListener { result ->
                val result = result.toObject<com.capstone.dogwhere.DTO.Walk_Record>()
                if (result != null) {
                    db.collection("Walk_Record").document(uid).collection(uid)
                        .document(date.toString())
                        .update("memo", memo).addOnSuccessListener { documentReference ->
                            //                val intent = Intent(this, BBSActivity::class.java)
                            //                startActivity(intent)
                            //                finish()
                        }
                } else {
                    db.collection("Walk_Record").document(uid).collection(uid)
                        .document(date.toString())
                        .set(Walk_Record(uid, "", "", date.toString(), "", memo))
                        .addOnSuccessListener { documentReference ->
                            Log.e("123", "성공ㅇㅇ")
                        }
                }
            }
            .addOnFailureListener { e ->
            }
    }

    @SuppressLint("WrongConstant")
    fun removeDiary(cYear: Int, cMonth: Int, cDay: Int) {
        date = "" + cYear + "-" + (cMonth + 1) + "" + "-" + cDay
        Log.e("yy", "삭제ㅇㅇ")
        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser!!.uid
        val db = Firebase.firestore
        db.collection("Walk_Record").document(uid).collection(uid).document(date.toString())
            .update(
                "memo",
                ""
            )
    }
    fun getTime(month_timesec:Int):String{
        return String.format("%02d:%02d:%02d",
            month_timesec / 1000 / 60 / 60,
            month_timesec / 1000 / 60,
            month_timesec / 1000 % 60, //초
        )
    }

    class OtherDayDecorator(context: Activity) : DayViewDecorator {
        private val calendar = Calendar.getInstance()
        private val drawable: Drawable
        override fun shouldDecorate(day: CalendarDay): Boolean {
            day.copyTo(calendar)
            val weekDay = calendar[Calendar.ALL_STYLES]
            return weekDay == calendar[Calendar.ALL_STYLES]
        }

        override fun decorate(view: DayViewFacade) {
            Log.e("yy", "악")
            view.addSpan(ForegroundColorSpan(Color.GRAY))
            view.setBackgroundDrawable(drawable)
        }

        init {
            drawable = context.resources.getDrawable(R.drawable.calendar_rec_gray)
        }
    }

    class SundayDecorator : DayViewDecorator {
        private val calendar = Calendar.getInstance()
        override fun shouldDecorate(day: CalendarDay): Boolean {
            day.copyTo(calendar)
            val weekDay = calendar[Calendar.DAY_OF_WEEK]
            return weekDay == Calendar.SUNDAY
        }

        override fun decorate(view: DayViewFacade) {
            view.addSpan(ForegroundColorSpan(Color.RED))
        }
    }

    class SaturdayDecorator : DayViewDecorator {
        private val calendar = Calendar.getInstance()
        override fun shouldDecorate(day: CalendarDay): Boolean {
            day.copyTo(calendar)
            val weekDay = calendar[Calendar.DAY_OF_WEEK]
            return weekDay == Calendar.SATURDAY
        }

        override fun decorate(view: DayViewFacade) {
            view.addSpan(ForegroundColorSpan(Color.BLUE))
        }
    }

    class OneDayDecorator(context: Activity) : DayViewDecorator {
        private var date: CalendarDay?
        private val drawable: Drawable
        override fun shouldDecorate(day: CalendarDay): Boolean {
            return date != null && day == date
        }

        override fun decorate(view: DayViewFacade) {
            view.setSelectionDrawable(drawable)
            view.addSpan(ForegroundColorSpan(Color.GRAY))
        }

        init {
            date = CalendarDay.today()
            drawable = context.resources.getDrawable(R.drawable.calendar_rec_mint)
        }
    }

    class Stamp(context: Activity, stamp: CalendarDay, number: String) : DayViewDecorator {
        private val drawable1: Drawable
        private val drawable2: Drawable
        private val drawable3: Drawable
        private var number = number
        private var myDay = stamp
        override fun shouldDecorate(day: CalendarDay): Boolean {

            return day == myDay
        }

        override fun decorate(view: DayViewFacade) {
            if (number == "1") {
                view.setBackgroundDrawable(drawable1)
            } else if (number == "2") {
                view.setBackgroundDrawable(drawable2)
            } else if (number == "3") {
                view.setBackgroundDrawable(drawable3)
            }
            Log.e("yy", number)


        }

        init {
            drawable1 = context.resources.getDrawable(R.drawable.ic__step1)
            drawable2 = context.resources.getDrawable(R.drawable.ic__step2)
            drawable3 = context.resources.getDrawable(R.drawable.ic__step3)
        }
    }

}
