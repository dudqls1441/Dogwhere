package com.capstone.dogwhere

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.NumberPicker
import android.widget.Toast
import com.capstone.dogwhere.DTO.Matching
import com.capstone.dogwhere.DTO.Matching_InUsers
import com.capstone.dogwhere.DTO.Participant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_matching_registration.*
import kotlinx.android.synthetic.main.activity_register_user_profile.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MatchingRegistrationActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    lateinit var party_address:String
    lateinit var doguid:String
    lateinit var condition_size :String
    lateinit var condition_owner_gender :String
    lateinit var condition_neutralization : String
    private val FLAG_Select_Dog_Code = 1000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matching_registration)

        condition_size = "all"
        condition_neutralization ="all"
        condition_owner_gender ="all"
        radio_size()
        radio_neutralization()
        radio_gender()

        btn_registration.setOnClickListener {
            register()
        }

        val yearList = (21..25).toList()
        val monthList = (1..12).toList()
        val dayList = (1..31).toList()
        val hoursList = (1..24).toList()

        var mList  = listOf("00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30"
            ,"31","32","33","34","35","36","37","38","39","40","41","42","43","44","45","46","47","48","49","50","51","52","53","54","55","56","57","58","59")

        var yearStrConvertList = yearList.map { it.toString() }
        var monthStrConvertList = monthList.map { it.toString() }
        var dayStrConvertList = dayList.map { it.toString() }
        var hoursStrCovertList = hoursList.map { it.toString() }
        var minuteStrCovertList = mList.map { it }

        npYear.run {
            minValue = 21
            wrapSelectorWheel = false
            maxValue = 25
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
        edittext_place.setOnClickListener {
            Intent(this,Search_Region::class.java).apply {
                putExtra("address_state", "matching_registration")
            }.run { startActivity(this) }
        }

        participation_dog_layout.setOnClickListener {
            Intent(this,MatchingRegistration_Choice_Dog_Activity::class.java).apply {
                putExtra("dogchoice_state", "matching_registration")
            }.run { startActivity(this) }
        }
//여기
        doguid= intent.getStringArrayExtra("select_doguid").toString()
        Log.d("yy","선택한 강아지 리스트"+doguid)
        party_address = intent.getStringExtra("address").toString()
        Log.d("yy",party_address)
        if (party_address!="null"){
            edittext_place.text=party_address
        }
    }

    //강아지 선택해서 돌아왔을 때 처리하기
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


    }

    private fun radio_size() {
        radiogroup_size.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.size_all -> checked_size_size_all()
                R.id.size_small -> checked_size_small()
                R.id.size_middle -> checked_size_middle()
                R.id.size_big -> checked_size_big()


            }
        }
    }

    private fun radio_neutralization() {
        radiogroup_neutralization.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.dontCare_neutralization -> checked_dontCare_neutralization()
                R.id.neutralization -> checked_neutralization()
            }
        }
    }

    private fun radio_gender() {
        radiogroup_gender.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.gender_all -> checked_gender_all()
                R.id.gender_man -> checked_gender_man()
                R.id.gender_woman -> checked_gender_woman()
            }
        }
    }

    private fun checked_size_size_all() {
        size_all.setBackgroundResource(R.drawable.backgroundgreencircle)
        size_all.setTextColor(Color.parseColor("#00C09F"))
        size_small.setBackgroundResource(R.drawable.backgroundgraycircle)
        size_small.setTextColor(Color.parseColor("#52443C3C"))
        size_middle.setBackgroundResource(R.drawable.backgroundgraycircle)
        size_middle.setTextColor(Color.parseColor("#52443C3C"))
        size_big.setBackgroundResource(R.drawable.backgroundgraycircle)
        size_big.setTextColor(Color.parseColor("#52443C3C"))

        condition_size ="all"
    }
    private fun checked_size_small() {
        size_all.setBackgroundResource(R.drawable.backgroundgraycircle)
        size_all.setTextColor(Color.parseColor("#52443C3C"))
        size_small.setBackgroundResource(R.drawable.backgroundgreencircle)
        size_small.setTextColor(Color.parseColor("#00C09F"))
        size_middle.setBackgroundResource(R.drawable.backgroundgraycircle)
        size_middle.setTextColor(Color.parseColor("#52443C3C"))
        size_big.setBackgroundResource(R.drawable.backgroundgraycircle)
        size_big.setTextColor(Color.parseColor("#52443C3C"))

        condition_size ="small"
    }
    private fun checked_size_middle() {
        size_all.setBackgroundResource(R.drawable.backgroundgraycircle)
        size_all.setTextColor(Color.parseColor("#52443C3C"))
        size_small.setBackgroundResource(R.drawable.backgroundgraycircle)
        size_small.setTextColor(Color.parseColor("#52443C3C"))
        size_middle.setBackgroundResource(R.drawable.backgroundgreencircle)
        size_middle.setTextColor(Color.parseColor("#00C09F"))
        size_big.setBackgroundResource(R.drawable.backgroundgraycircle)
        size_big.setTextColor(Color.parseColor("#52443C3C"))

        condition_size ="middle"
    }
    private fun checked_size_big() {
        size_all.setBackgroundResource(R.drawable.backgroundgraycircle)
        size_all.setTextColor(Color.parseColor("#52443C3C"))
        size_small.setBackgroundResource(R.drawable.backgroundgraycircle)
        size_small.setTextColor(Color.parseColor("#52443C3C"))
        size_middle.setBackgroundResource(R.drawable.backgroundgraycircle)
        size_middle.setTextColor(Color.parseColor("#52443C3C"))
        size_big.setBackgroundResource(R.drawable.backgroundgreencircle)
        size_big.setTextColor(Color.parseColor("#00C09F"))

        condition_size ="big"
    }

    private fun checked_dontCare_neutralization() {
        dontCare_neutralization.setBackgroundResource(R.drawable.backgroundgreencircle)
        dontCare_neutralization.setTextColor(Color.parseColor("#00C09F"))
        neutralization.setBackgroundResource(R.drawable.backgroundgraycircle)
        neutralization.setTextColor(Color.parseColor("#52443C3C"))

        condition_neutralization ="all"
    }

    private fun checked_neutralization() {
        dontCare_neutralization.setBackgroundResource(R.drawable.backgroundgraycircle)
        dontCare_neutralization.setTextColor(Color.parseColor("#52443C3C"))
        neutralization.setBackgroundResource(R.drawable.backgroundgraycircle)
        neutralization.setTextColor(Color.parseColor("#00C09F"))

        condition_neutralization ="neutralization"
    }

    private fun checked_gender_all() {
        gender_all.setBackgroundResource(R.drawable.backgroundgreencircle)
        gender_all.setTextColor(Color.parseColor("#00C09F"))
        gender_man.setBackgroundResource(R.drawable.backgroundgraycircle)
        gender_man.setTextColor(Color.parseColor("#52443C3C"))
        gender_woman.setBackgroundResource(R.drawable.backgroundgraycircle)
        gender_woman.setTextColor(Color.parseColor("#52443C3C"))


        condition_owner_gender ="all"
    }

    private fun checked_gender_man() {
        gender_all.setBackgroundResource(R.drawable.backgroundgraycircle)
        gender_all.setTextColor(Color.parseColor("#52443C3C"))
        gender_man.setBackgroundResource(R.drawable.backgroundgreencircle)
        gender_man.setTextColor(Color.parseColor("#00C09F"))
        gender_woman.setBackgroundResource(R.drawable.backgroundgraycircle)
        gender_woman.setTextColor(Color.parseColor("#52443C3C"))


        condition_owner_gender ="man"
    }

    private fun checked_gender_woman() {
        gender_all.setBackgroundResource(R.drawable.backgroundgraycircle)
        gender_all.setTextColor(Color.parseColor("#52443C3C"))
        gender_man.setBackgroundResource(R.drawable.backgroundgraycircle)
        gender_man.setTextColor(Color.parseColor("#52443C3C"))
        gender_woman.setBackgroundResource(R.drawable.backgroundgreencircle)
        gender_woman.setTextColor(Color.parseColor("#00C09F"))


        condition_owner_gender ="woman"
    }

    private fun register() {
        auth = FirebaseAuth.getInstance()

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
                documentId,
                condition_size,
                condition_neutralization,
                condition_owner_gender
            )
            Log.d("33 -> ", matching.toString())
            val time = System.currentTimeMillis()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd kk:mm:ss")
            val curTime = dateFormat.format(Date(time))
            db.collection("Matching").document(documentId).set(matching).addOnSuccessListener {
                Log.d("InsertMatching", "InsertMatching_성공")
                db.collection("Matching").document(documentId).collection("participant").document(uid)
                    .set( Participant(uid, uid, curTime.toString()))
                    .addOnSuccessListener {
                        Log.d("Participant", "MatchingDetailActivity_participant  성공")
                    }.addOnFailureListener {
                        Log.d("Participant", "Participant 실패 이유 : ${it}")
                    }
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
                Intent(this, MatchingDetailActivity::class.java).apply {
                    putExtra("title", title)
                    putExtra("explain", explain)
                    putExtra("leaderuid", uid)
                    putExtra("documentId", documentId)
                }.run { startActivity(this) }
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