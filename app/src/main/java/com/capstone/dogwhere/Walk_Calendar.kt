package com.capstone.dogwhere

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CalendarView.OnDateChangeListener
import androidx.appcompat.app.AppCompatActivity
import com.capstone.dogwhere.DTO.Walk_Record
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_walk__calendar.*
import org.jetbrains.anko.toast


class Walk_Calendar : AppCompatActivity() {

    var memo:String=""
    var date:String=""
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_walk__calendar)

        layout_bottom2.setVisibility(View.INVISIBLE) //클릭-일
        layout_bottom.setVisibility(View.VISIBLE) //월평균

        calendar.setOnDateChangeListener(OnDateChangeListener { view, year, month, dayOfMonth ->

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
        val uid = auth.currentUser.uid
        val db = Firebase.firestore

        memo=""
        db.collection("Walk_Record").document(uid).collection(date.toString()).document(uid).get()
            .addOnSuccessListener { result ->
                if(result.get("time").toString()=="null" || result.get("time").toString()==""){
                    time_value.setText("00:00:00")

                }else{
                    time_value.setText(result.get("time").toString()) //시간 축적
                }
               if(result.get("memo").toString()=="null" || result.get("memo").toString()==""){
                    textView2.visibility = View.INVISIBLE
                    cha_Btn.visibility = View.INVISIBLE
                    del_Btn.visibility = View.INVISIBLE
                    contextEditText.visibility = View.VISIBLE
                    save_Btn.visibility = View.VISIBLE
                }else {
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
        Log.e("yy", date.toString() )
// 저장할 데이터 이름 설정. Ex) 2019-01-20

        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser.uid
        val db = Firebase.firestore
        db.collection("Walk_Record").document(uid).collection(date.toString()).document(uid).get()
            .addOnSuccessListener { result ->
                val result = result.toObject<com.capstone.dogwhere.DTO.Walk_Record>()
                if (result != null) {
                    db.collection("Walk_Record").document(uid).collection(date.toString()).document(uid).update("memo",memo).addOnSuccessListener { documentReference ->
                        //                val intent = Intent(this, BBSActivity::class.java)
                        //                startActivity(intent)
                        //                finish()
                    }
                }else{
                    db.collection("Walk_Record").document(uid).collection(date.toString()).document(uid).set(Walk_Record(uid,"", date.toString(),"", memo))
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
        val uid = auth.currentUser.uid
        val db = Firebase.firestore
        db.collection("Walk_Record").document(uid).collection(date.toString()).document(uid).update("memo","")
    }
    }

