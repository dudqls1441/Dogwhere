package com.capstone.dogwhere

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.NumberPicker
import android.widget.Toast
import com.capstone.dogwhere.DTO.Matching
import com.capstone.dogwhere.DTO.Matching_InUsers
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_matching_registration.*

class MatchingRegistrationActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private val FLAG_Select_Dog_Code = 1000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matching_registration)

        btn_registration.setOnClickListener {
            register()
        }

        val yearList = (1950..2022).toList()
        val monthList = (1..12).toList()
        val dayList = (1..31).toList()
        val hoursList = (1..24).toList()
        val minuteList = (0..60).toList()

        var yearStrConvertList = yearList.map { it.toString() }
        var monthStrConvertList = monthList.map { it.toString() }
        var dayStrConvertList = dayList.map { it.toString() }
        var hoursStrCovertList = hoursList.map { it.toString() }
        var minuteStrCovertList = minuteList.map { it.toString() }

        npYear.run {
            minValue = 1
            wrapSelectorWheel = false
            maxValue = yearStrConvertList.size
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
//            displayedValues = yearStrConvertList.toTypedArray()
        }
        npMonth.run {
            minValue = 1
            maxValue = monthStrConvertList.size
            wrapSelectorWheel = false
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
//            displayedValues = monthStrConvertList.toTypedArray()
        }
        npDay.run {
            minValue = 1
            wrapSelectorWheel = false
            maxValue = dayStrConvertList.size - 1
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        }
        npHours.run {
            minValue = 0
            maxValue = hoursStrCovertList.size - 1
            wrapSelectorWheel = false
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        }
        npMinute.run {
            minValue = 0
            maxValue = minuteStrCovertList.size - 1
            wrapSelectorWheel = false
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        }






        participation_dog_layout.setOnClickListener {
            val intent = Intent(this, MatchingRegistration_Choice_Dog_Activity::class.java)
            startActivityForResult(intent, FLAG_Select_Dog_Code)
        }
    }

    //강아지 선택해서 돌아왔을 때 처리하기
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


    }

    private fun register() {
        auth = FirebaseAuth.getInstance()
        val party_address = edittext_place.text.toString()
        val party_address_detail = edittext_place_detail.text.toString()
        val title = edittext_registration_title.text.toString()
        db = FirebaseFirestore.getInstance()

        //산책에 참여하는 dog 가져와야 한다.
        val dog = ""
        val party_date =
            npYear.value.toString() + "/" + npMonth.value.toString() + "/" + npDay.value.toString()
        val party_time =
            npHours.value.toString() + "시" + npMinute.value.toString() + "분"
        val explain = edit_registration_explain.text.toString()
        val documentid = db.collection("Matching").document()

        put33(
            party_address,
            party_address_detail,
            party_date,
            party_time,
            title,
            explain,
            "",
            true,
            documentid.id
        )


    }

    private fun put33(
        party_address: String,
        party_address_detail: String,
        party_date: String,
        party_time: String,
        title: String,
        explain: String,
        dog: String,
        ongoing: Boolean,
        documentId: String
    ) {
        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser!!.uid
        db = FirebaseFirestore.getInstance()
        if (!party_address.equals("") && !party_address_detail.equals("") && !party_date.equals("") && !party_time.equals(
                ""
            )
        ) {
            val matching = Matching(
                uid,
                dog,
                party_address,
                party_address_detail,
                title,
                party_date,
                party_time,
                explain,
                ongoing,
                documentId
            )
            Log.d("33 -> ", matching.toString())

            db.collection("Matching").document(documentId).set(matching).addOnSuccessListener {
                Log.d("InsertMatching", "InsertMatching_성공")

                val matchingInUsers =
                    Matching_InUsers(uid, uid, matching.title, matching.documentId)
                db.collection("users").document(uid).collection("matching")
                    .document(matching.documentId).set(matchingInUsers).addOnSuccessListener {
                    Log.d("InsertMatchingUsers", "InsertMatchingUsers_성공")
                    Log.d(
                        "InsertMatchingUsers",
                        "InsertMatchingUsers:${matching.documentId},,,,, ${matchingInUsers} "
                    )
                }.addOnFailureListener {
                    Log.d("InsertMatchingUsers", "InsertMatchingUsers_실패")
                }
                val intent = Intent(this, MatchingDetailActivity::class.java)
                intent.putExtra("title", title)
                intent.putExtra("explain", explain)
                intent.putExtra("leaderuid", uid)
                intent.putExtra("documentId", documentId)
                startActivity(intent)
                finish()


            }.addOnFailureListener {
                Log.d("InsertParty", "InsertParty_실패")
            }


        } else {
            Toast.makeText(this, "빈 칸을 확인해주세요.", Toast.LENGTH_SHORT)
                .show()
        }

    }


//    private fun put33(
//        party_address: String,
//        party_address_detail: String,
//        party_date: String,
//        party_time: String,
//        title: String,
//        explain: String,
//        dog: String,
//        ongoing : Boolean
//    ) {
//        auth = FirebaseAuth.getInstance()
//        val uid = auth.currentUser!!.uid
//        val db = FirebaseFirestore.getInstance()
//        //realtimebase
//        val rdb = Firebase.database
//        if (!party_address.equals("") && !party_address_detail.equals("") && !party_date.equals("") && !party_time.equals(
//                ""
//            )
//        ) {
//            val party = Matching(
//                uid,
//                dog,
//                party_address,
//                party_address_detail,
//                title,
//                party_date,
//                party_time,
//                explain,
//                true
//            )
//            Log.d("33 -> ", party.toString())
//
//            //db
//            db.collection("Party").document(uid).set(party).addOnSuccessListener {
//                Log.d("InsertParty", "InsertParty_성공")
//
//                val intent = Intent(this, MatchingDetailActivity::class.java)
//                intent.putExtra("title", title)
//                intent.putExtra("explain", explain)
//                startActivity(intent)
//                finish()
//            }.addOnFailureListener {
//                Log.d("InsertParty", "InsertParty_실패")
//            }
//            //realtimebase
////            ref.setValue(party)
//
//
//        } else {
//            Toast.makeText(this, "빈 칸을 확인해주세요.", Toast.LENGTH_SHORT)
//                .show()
//        }
//
//    }

}