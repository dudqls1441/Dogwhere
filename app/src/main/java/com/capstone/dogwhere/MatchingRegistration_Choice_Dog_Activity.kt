package com.capstone.dogwhere

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.capstone.dogwhere.DTO.*
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
import java.io.Serializable

class MatchingRegistration_Choice_Dog_Activity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var rdb: FirebaseDatabase
    private val TAG = DogProfileActivity::class.java.simpleName
    private lateinit var adapter: GroupAdapter<GroupieViewHolder>
    val db = Firebase.firestore
    private var dogname=ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_matching_choice_dog_registration)

        init()


        btn_registration.setOnClickListener {
            for (i in 0 until adapter.groupCount){
                var list: Dog_Choice_item=adapter.getItem(i) as Dog_Choice_item
                if(list.checked) {
                    dogname.add((list.name))
                }
            }

            Log.e("yy","체크된 dogname: "+dogname)
            if(intent.getStringExtra("dogchoice_state")=="matching_registration"){
                val intent= Intent(this, MatchingRegistrationActivity::class.java)
                intent.putExtra("select_dogname",dogname) //강아지 유아이디 리스트 넘겨주고
                setResult(Activity.RESULT_OK,intent)
                finish()
            }else{
                val intent= Intent(this, MatchingDetailActivity::class.java)
                intent.putExtra("select_dogname",dogname) //강아지 유아이디 리스트 넘겨주고
                setResult(Activity.RESULT_OK,intent)
                finish()
            }

        }
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
                                Dog_Choice_item(
                                    dogs.uid,
                                    document.id,
                                    dogs?.dogAge + "살" ,
                                    dogs?.dogName ,
                                    dogs?.dogBreed,
                                    dogs?.dogSex,
                                    dogs?.neutering,
                                    dogs?.photoUrl.toString()
                                )
                            )
                            Log.d("ybyb","dog photo -> ${dogs?.photoUrl}")
                            Log.d("ybyb","dog document -> ${document.id}")
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