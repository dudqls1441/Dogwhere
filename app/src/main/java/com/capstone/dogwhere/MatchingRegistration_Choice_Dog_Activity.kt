package com.capstone.dogwhere

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.capstone.dogwhere.DTO.DogProfile
import com.capstone.dogwhere.DTO.Dog_Profile_Item
import com.capstone.dogwhere.DTO.Matching_List_Item
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_dog_profile.*
import kotlinx.android.synthetic.main.activity_matching_registration.*

class MatchingRegistration_Choice_Dog_Activity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var rdb: FirebaseDatabase
    private val TAG = DogProfileActivity::class.java.simpleName
    private lateinit var adapter: GroupAdapter<GroupieViewHolder>
    val db = Firebase.firestore
    private lateinit var doguid:ArrayList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matching_choice_dog_registration)

        init()

        btn_registration.setOnClickListener {
//            Intent(this, MatchingRegistrationActivity::class.java).apply {
//                putExtra("select_doguid",doguid)
//            }.run {
//                startActivity(this)
//            }
            finish()
        }

//        adapter.setOnItemClickListener { item, view ->
//            Log.d("ClickMatching", (item as  Dog_Profile_Item).name)
//            doguid=
//            doguid.add((item).uid)
//        }

        //Log.e("yy", "누른 강아지 uid"+doguid.toString())
    }

    private fun init() {
        val db = FirebaseFirestore.getInstance()
        adapter = GroupAdapter<GroupieViewHolder>()
        auth = FirebaseAuth.getInstance()
        rdb = FirebaseDatabase.getInstance()
        val uid = auth.currentUser!!.uid

        Log.d(TAG, "현재 아이디 uid 값 : ${uid}")

        db.collection("users").document(uid).collection("dogprofiles").get()
            .addOnSuccessListener { result ->
                if (result != null) {
                    for (document in result) {
                        val dogs = document.toObject<DogProfile>()
                        if (dogs != null) {
                            Log.d("dogprofile있음", result.toString())
                            adapter.add(
                                Dog_Profile_Item(
                                    dogs.uid,
                                    dogs?.dogAge + "살",
                                    dogs?.dogName,
                                    dogs?.dogBreed,
                                    dogs?.dogSex,
                                    true,
                                    dogs?.photoUrl.toString()
                                )
                            )
                            Log.d("DogProfile읽기 성공", "강아지 이름 : ${dogs?.dogName}")
                        }
                        dog_profiles_recyclerview?.adapter = adapter
                    }
                } else {
                    Toast.makeText(this,"등록된 강아지가 없습니다",Toast.LENGTH_SHORT)
                }

            }

    }
}