package com.capstone.dogwhere

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.capstone.dogwhere.DTO.Walk_Goal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_walk.*
import kotlinx.android.synthetic.main.activity_walk.goal_day
import kotlinx.android.synthetic.main.activity_walk.km
import kotlinx.android.synthetic.main.activity_walk.minute
import kotlinx.android.synthetic.main.activity_walk.setting1
import kotlinx.android.synthetic.main.activity_walk.setting2
import kotlinx.android.synthetic.main.activity_walk.write
import kotlinx.android.synthetic.main.activity_walk.write2
import java.text.SimpleDateFormat
import java.util.*

class WalkFragment : Fragment(){
    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore
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
        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid
        var walkGoal: Walk_Goal? =null
        db.collection("Walk_Goal").document(uid!!).get()
            .addOnSuccessListener { result ->
                walkGoal =
                    result.toObject<Walk_Goal>()
                if(walkGoal?.uid==null){
                    setting1.visibility=View.VISIBLE
                    write.visibility=View.VISIBLE
                }else{
                    rating.rating=walkGoal?.percent!!.toFloat()/20
                    goal_day.setText(walkGoal?.goal_day.toString() + "번 ")
                    if(walkGoal?.minute!=null){
                        minute.setText(walkGoal?.minute.toString() + "분씩 ")
                    }
                    if(walkGoal?.km!=null){
                        km.setText(walkGoal?.km.toString() + "km씩 ")
                    }
                    setting2.visibility=View.VISIBLE
                    write2.visibility=View.VISIBLE
                    goal(walkGoal!!)
                }

            }
        val setting1: TextView = view.findViewById(R.id.setting1)
        val setting2: TextView = view.findViewById(R.id.setting2)
        val vieww = inflater.inflate(R.layout.walk_goal_alert, null)

        val count: EditText =vieww.findViewById(R.id.counter)
        val countt: EditText =vieww.findViewById(R.id.counterr)
        val minute: EditText =vieww.findViewById(R.id.minute)
        val minutee: EditText =vieww.findViewById(R.id.minutee)
        val km: EditText =vieww.findViewById(R.id.km)

        val select_condition_group: RadioGroup =vieww.findViewById(R.id.select_condition_group)
        val time_stand_content: RelativeLayout =vieww.findViewById(R.id.time_stand_content)
        val boon: TextView =vieww.findViewById(R.id.boon)
        val both_stand_content: RelativeLayout =vieww.findViewById(R.id.both_stand_content)

        setting1.setOnClickListener {
            var condition="time_stand"
            time_stand_content.visibility=View.VISIBLE
            select_condition_group.check(R.id.time_stand)
            select_condition_group.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.time_stand -> let {
                        both_stand_content.visibility = View.INVISIBLE
                        boon.text = "분씩"
                        time_stand_content.visibility = View.VISIBLE
                        condition = "time_stand"
                    }
                    R.id.dist_stand -> let {
                        both_stand_content.visibility = View.INVISIBLE
                        boon.text = "km씩"
                        time_stand_content.visibility = View.VISIBLE
                        condition = "dist_stand"
                    }
                    R.id.both_stand -> let {
                        time_stand_content.visibility = View.INVISIBLE
                        both_stand_content.visibility = View.VISIBLE
                        condition = "both_stand"
                    }
                }
            }
            val alertDialog = AlertDialog.Builder(context)
                .setPositiveButton("저장하기") { dialog, which ->
                    when(condition){
                        "time_stand" -> {
                            if (count.text.toString() != "" && minute.text.toString() != "") {
                                db.collection("Walk_Goal").document(uid).set(
                                    Walk_Goal(
                                        uid,
                                        condition,
                                        count.text.toString().toInt(),
                                        minute.text.toString().toInt(),
                                        null,
                                        0,
                                        0,
                                        null
                                    )
                                )
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            context,
                                            "목표가 등록되었습니다",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            } else {
                                Toast.makeText(context, "입력해주세요", Toast.LENGTH_SHORT).show()
                            }
                        }
                        "dist_stand" -> {
                            if (count.text.toString() != "" && minute.text.toString() != "") {
                                db.collection("Walk_Goal").document(uid).set(
                                    Walk_Goal(
                                        uid,
                                        condition,
                                        count.text.toString().toInt(),
                                        null,
                                        minute.text.toString().toInt(),
                                        0,
                                        0,
                                        null
                                    )
                                )
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            context,
                                            "목표가 등록되었습니다",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            } else {
                                Toast.makeText(context, "입력해주세요", Toast.LENGTH_SHORT).show()
                            }
                        }
                        "both_stand" -> {
                            if (countt.text.toString() != "" && minutee.text.toString() != "" && km.text.toString() != "") {
                                db.collection("Walk_Goal").document(uid).set(
                                    Walk_Goal(
                                        uid,
                                        condition,
                                        countt.text.toString().toInt(),
                                        minutee.text.toString().toInt(),
                                        km.text.toString().toInt(),
                                        0,
                                        0,
                                        null
                                    )
                                )
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            context,
                                            "목표가 등록되었습니다",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            } else {
                                Toast.makeText(context, "입력해주세요", Toast.LENGTH_SHORT).show()
                            }

                        }
                    }
                    var ft: FragmentTransaction? = fragmentManager?.beginTransaction()
                    ft?.detach(this)?.attach(this)?.commit()
                }
                .setNeutralButton("취소", null)
                .create()
            if (vieww.getParent() != null) (vieww.getParent() as ViewGroup).removeView(
                vieww
            )
            alertDialog.setView(vieww)
            alertDialog.show()
            alertDialog.window?.setLayout(800, 1000)
        }

        setting2.setOnClickListener {
            var condition = "time_stand"

            time_stand_content.visibility = View.VISIBLE
            if (condition == walkGoal?.condition) {
                minute.setText(walkGoal?.minute.toString())
                count.setText(walkGoal?.goal_day.toString())
            } else {
                minute.setText("")
                count.setText("")
            }
            select_condition_group.check(R.id.time_stand)
            select_condition_group.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.time_stand -> let {
                        condition = "time_stand"
                        both_stand_content.visibility = View.INVISIBLE
                        boon.text = "분씩"
                        time_stand_content.visibility = View.VISIBLE
                        if (condition == walkGoal?.condition) {
                            minute.setText(walkGoal?.minute.toString())
                            count.setText(walkGoal?.goal_day.toString())
                        } else {
                            minute.setText("")
                            count.setText("")
                        }
                    }
                    R.id.dist_stand -> let {
                        condition = "dist_stand"
                        boon.text = "km씩"
                        time_stand_content.visibility = View.VISIBLE
                        both_stand_content.visibility = View.INVISIBLE
                        if (condition == walkGoal?.condition) {
                            minute.setText(walkGoal?.km.toString())
                            count.setText(walkGoal?.goal_day.toString())
                        } else {
                            minute.setText("")
                            count.setText("")
                        }
                    }
                    R.id.both_stand -> let {
                        condition = "both_stand"
                        time_stand_content.visibility = View.INVISIBLE
                        both_stand_content.visibility = View.VISIBLE
                        if (condition == walkGoal?.condition) {
                            countt.setText(walkGoal?.goal_day.toString())
                            minutee.setText(walkGoal?.minute.toString())
                            km.setText(walkGoal?.km.toString())
                        } else {
                            countt.setText("")
                            minutee.setText("")
                            km.setText("")
                        }
                    }
                }
            }
            val alertDialog = AlertDialog.Builder(context)
                .setPositiveButton("수정하기") { dialog, which ->
                    when(condition){
                        "time_stand" -> {
                            if (count.text.toString() != "" && minute.text.toString() != "") {
                                db.collection("Walk_Goal").document(uid).set(
                                    Walk_Goal(
                                        uid,
                                        condition,
                                        count.text.toString().toInt(),
                                        minute.text.toString().toInt(),
                                        null,
                                        0,
                                        0,
                                        null
                                    )
                                )
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            context,
                                            "목표가 수정되었습니다",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            } else {
                                Toast.makeText(context, "입력해주세요", Toast.LENGTH_SHORT).show()
                            }
                        }
                        "dist_stand" -> {
                            if (count.text.toString() != "" && minute.text.toString() != "") {
                                db.collection("Walk_Goal").document(uid).set(
                                    Walk_Goal(
                                        uid,
                                        condition,
                                        count.text.toString().toInt(),
                                        null,
                                        minute.text.toString().toInt(),
                                        0,
                                        0,
                                        null
                                    )
                                )
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            context,
                                            "목표가 수정되었습니다",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            } else {
                                Toast.makeText(context, "입력해주세요", Toast.LENGTH_SHORT).show()
                            }
                        }
                        "both_stand" -> {
                            if (countt.text.toString() != "" && minutee.text.toString() != "" && km.text.toString() != "") {
                                db.collection("Walk_Goal").document(uid).set(
                                    Walk_Goal(
                                        uid,
                                        condition,
                                        countt.text.toString().toInt(),
                                        minutee.text.toString().toInt(),
                                        km.text.toString().toInt(),
                                        0,
                                        0,
                                        null
                                    )
                                )
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            context,
                                            "목표가 수정되었습니다",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            } else {
                                Toast.makeText(context, "입력해주세요", Toast.LENGTH_SHORT).show()
                            }

                        }
                    }
                    var ft: FragmentTransaction? = fragmentManager?.beginTransaction()
                    ft?.detach(this)?.attach(this)?.commit()
                }
                .setNeutralButton("취소", null)
                .create()

            if (vieww.getParent() != null) (vieww.getParent() as ViewGroup).removeView(
                vieww
            )
            alertDialog.setView(vieww)
            alertDialog.show()
            alertDialog.window?.setLayout(800, 1000)
        }



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
        date.text = current()
    }

    private fun goal(goal: Walk_Goal){
        auth = FirebaseAuth.getInstance()
        val uid = auth.uid.toString()
        current()?.let {
            db.collection("Walk_Record").document(uid).collection(uid)
                .document(it).get()
                .addOnSuccessListener {
                    Log.e("yy", goal.minute.toString())
                    if(it["timesec"]!=null){
                        when(goal.condition){
                            "time_stand"->{
                                if (it["timesec"].toString().toInt()/ 1000 % 60 >= 5) {
                                    goal2(goal,uid)
                                } //확인용 5초
                            }
                            "dist_stand"->{
                                if (it["distance"].toString().toInt()>= goal.km!!) {
                                    goal2(goal,uid)
                                }
                            }
                            "both_stand"->{
                                if (it["timesec"].toString().toInt()/ 1000 / 60 >= goal.minute!!
                                    && it["distance"].toString().toInt()>= goal.km!!) {
                                    goal2(goal,uid)
                                }
                            }
                        }
                    }else{
                        Log.e("yy", "아직 오늘 기록이 없음")
                    }

                }
        }

    }
    private fun goal2(goal:Walk_Goal, uid:String){
        if(goal.percent<100 && goal.condition_date!=current()){
            db.collection("Walk_Goal").document(uid).update("percent",
                goal.percent+(100/goal.goal_day),
                "success_day",goal.success_day+1,
                "condition_date",current())
                .addOnSuccessListener {
                    Log.e("yy", "확인해보셍 됨")
                }
        }

    }

    private fun current(): String? {

        val time = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-M-d")
        val curTime =dateFormat.format(Date(time))
        return curTime
    }
}