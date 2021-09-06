package com.capstone.dogwhere

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.capstone.dogwhere.DTO.Party
import com.capstone.dogwhere.DTO.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_dog_profile.*
import kotlinx.android.synthetic.main.activity_matching_registration.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.anko.numberPicker
import java.time.Year

class MatchingRegistrationActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private val FLAG_Select_Dog_Code = 1000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matching_registration)

        btn_registration.setOnClickListener {
            register()
        }

        val yearList =(1950..2022).toList()
        val monthList = (1..12).toList()
        val dayList =(1..31).toList()

        var yearStrConvertList = yearList.map { it.toString() }
        var monthStrConvertList = monthList.map { it.toString() }
        var dayStrConvertList = dayList.map { it.toString() }

        npYear.run{
            minValue = 0
            wrapSelectorWheel=false
            maxValue = yearStrConvertList.size - 1
            displayedValues
            wrapSelectorWheel = false
            displayedValues = yearStrConvertList.toTypedArray()
        }
        npMonth.run{
            minValue = 0
            maxValue = monthStrConvertList.size - 1
            wrapSelectorWheel=false
            wrapSelectorWheel = false
            displayedValues = monthStrConvertList.toTypedArray()
        }
        npDay.run{
            minValue = 0
            wrapSelectorWheel=false
            maxValue = dayStrConvertList.size - 1
            wrapSelectorWheel = false
            displayedValues = dayStrConvertList.toTypedArray()
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
        val dog = ""
        val party_date = npYear.value.toString()+"/"+npMonth.value.toString()+"/"+npDay.value.toString()
//            party_year.text.toString() + party_month.text.toString() + party_day.text.toString()
        val party_time =
            party_hours.text.toString() + party_minute.text.toString() + party_minute.text.toString()
        val explain = edit_registration_explain.text.toString()

        put33(party_address, party_address_detail, party_date, party_time, title, explain, "")


    }

    private fun put33(
        party_address: String,
        party_address_detail: String,
        party_date: String,
        party_time: String,
        title: String,
        explain: String,
        dog: String
    ) {
        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser!!.uid
        val db = FirebaseFirestore.getInstance()
        //realtimebase
        val rdb = Firebase.database
        if (!party_address.equals("") && !party_address_detail.equals("") && !party_date.equals("") && !party_time.equals(
                ""
            )
        ) {
            val party = Party(
                uid,
                dog,
                party_address,
                party_address_detail,
                title,
                party_date,
                party_time,
                explain
            )
            Log.d("33 -> ", party.toString())

            //db
            db.collection("Party").document(uid).set(party).addOnSuccessListener {
                Log.d("InsertParty", "InsertParty_성공")

                val intent = Intent(this, MatchingDetailActivity::class.java)
                intent.putExtra("title", title)
                intent.putExtra("explain", explain)
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                Log.d("InsertParty", "InsertParty_실패")
            }
            //realtimebase
//            ref.setValue(party)


        } else {
            Toast.makeText(this, "빈 칸을 확인해주세요.", Toast.LENGTH_SHORT)
                .show()
        }

    }

}